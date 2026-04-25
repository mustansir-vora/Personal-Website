import {
    apiClient,
    logger,
    getIvaConfigs,
    parseJson,
    getCallStartMatrix,
    getRequestHeaders,
    sendWebhookResponse
} from "@roger/r4b-common-nodemodules";

//call start related handler
export async function getCallStartParams(req, res, params, tag, sessionId) {

    let sessionParams = {};
    let ivaConfig = {};
    let callStartMatrix = {};
    let matrixPromise;

    try {

        const dnis = params.dnis || "NA";
        const ani = params.ani || "NA";
        const env = params.mwInstance || "qa4";

        logger.logWebhookRequest(sessionId, tag, { dnis, ani, env });

        const configPromise = getIvaConfigs({ sessionId, tag });
        matrixPromise = getCallStartMatrix({ sessionId, tag });

        const configResult = await configPromise;
        ivaConfig = configResult.ResponsePayload;



        const apiUrl = ivaConfig[tag][env]
            .replace("${dnis}", dnis)
            .replace("${ani}", ani);

        const { headers, tokenConfig } = getRequestHeaders(sessionId, ani, ivaConfig, env);

        const apiResult = await apiClient.getRequest({
            sessionId,
            tag,
            url: apiUrl,
            headers,
            tokenConfig
        });

        const { Status, ResponsePayload } = apiResult;

        if (Status !== 200) {

            const matrixResult = await matrixPromise;

            if (matrixResult?.Status === 200) {
                logger.logConsole(
                    sessionId,
                    tag,
                    "getCallStartMatrix successful in fallback."
                );
                callStartMatrix = matrixResult.ResponsePayload;
            }

            sessionParams = {
                returnCode: "0",
                ...callStartMatrix
            };

        } else {

            const d = ResponsePayload || {};

            sessionParams = {
                brand: parseJson(d.brand),
                dnisLanguage: parseJson(d.dnisLanguage),
                aniLookup: parseJson(d.aniLookup),
                validAni: parseJson(d.aniDetails?.validANI),
                searchHomeContact: parseJson(d.icmSearchIndicators?.searchHomeContact),
                searchMobileContact: parseJson(d.icmSearchIndicators?.searchMobileContact),
                searchBusinessContact: parseJson(d.icmSearchIndicators?.searchBusinessContact),
                npaLanguage: parseJson(d.aniDetails?.npaLanguage),
                greetingScriptEn: parseJson(d.greetingScript?.scriptContent?.en),
                greetingScriptFr: parseJson(d.greetingScript?.scriptContent?.fr),
                disclaimerScriptEn: parseJson(d.disclaimerScript?.scriptContent?.en),
                disclaimerScriptFr: parseJson(d.disclaimerScript?.scriptContent?.fr),
                systemOverride: parseJson(d.routingParams?.systemOverride),
                offerLanguageMenu: parseJson(d.offerLanguageMenu),
                applicationId: parseJson(d.applicationId),
                reportingVar_applicationID: parseJson(d.applicationId),
                aniConfirm: parseJson(d.aniConfirm),
                identifyAccount: parseJson(d.identifyAccount),
                idType: parseJson(d.idType),
                predictiveInd: parseJson(d.predictiveInd),
                involuntaryRedirectInd: parseJson(d.involuntaryRedirectInd),
                voluntaryRedirect: parseJson(d.voluntaryRedirect),
                callHistory: parseJson(d.callHistory),
                savedIntent: parseJson(d.intent),
                closedDoorTFN: parseJson(d.closedDoorTFN),
                returnCode: "0",
                api: {
                    callStartParams: d
                }
            };

        }

        const finalParams = {
            ...ivaConfig,
            ...sessionParams
        };
        return sendWebhookResponse(res, sessionId, tag, finalParams);

    } catch (err) {

        logger.logErrorResponse({ sessionId, tag, attempt: 1, err });

        try {

            if (matrixPromise) {

                const matrixResult = await matrixPromise;
                if (matrixResult?.Status === 200) {
                    callStartMatrix = matrixResult.ResponsePayload;
                }
            }

            logger.logConsole(
                sessionId,
                tag,
                "Error in getCallStartParams, returning with default values."
            );

        } catch (fallbackErr) {

            logger.logErrorResponse({ sessionId, tag, attempt: 2, err: fallbackErr });

            logger.logConsole(
                sessionId,
                tag,
                "Error in fallback CallStartMatrix, returning with default return code only."
            );

        }

    }

    const webhookResponse = {
        returnCode: "1",
        ...callStartMatrix,
        ...ivaConfig,
        api: {
            callStartParams: callStartMatrix
        }
    };
    return sendWebhookResponse(res, sessionId, tag, webhookResponse);
}