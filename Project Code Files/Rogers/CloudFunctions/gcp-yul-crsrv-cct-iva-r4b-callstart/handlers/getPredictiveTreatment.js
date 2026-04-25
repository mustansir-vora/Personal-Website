import {
    apiClient,
    logger,
    parseJson,
    getRequestHeaders,
    sendWebhookResponse,
    sendErrorResponse
} from "@roger/r4b-common-nodemodules";

//predictive treatment related handler
export async function getPredictiveTreatment(req, res, params, tag, sessionId) {

    let sessionParams = {};

    try {

        const env = params.mwInstance || "qa4";
        const ani = params.ani || "NA";

        const language = params.flowLanguage || "NA";
        const brand = params.brand || "ROGERS";
        const applicationId = params.applicationId || "NA";
        const accountNumber = params.accountNumber || "";
        const sessionIds = params.accountSessionList || [];
        const sessionIdInContext = params.sessionIdInContext || "";

        logger.logWebhookRequest(sessionId, tag, {
            env,
            language,
            brand,
            applicationId,
            sessionIds
        });

        const apiUrl = params[tag][env];

        const { headers, tokenConfig } = getRequestHeaders(sessionId, ani, params, env);


        const requestBody = {
            language,
            brand,
            applicationId,
            sessionIds,
            sessionIdInContext: accountNumber === "" ? "" : sessionIdInContext
        };


        const { Status, ResponsePayload } = await apiClient.postRequest({
            sessionId,
            tag,
            url: apiUrl,
            headers,
            data: requestBody,
            tokenConfig
        });;

        if (Status === 200 && ResponsePayload?.enabled === true) {

            const p = ResponsePayload.predictiveRule;

            sessionParams = {
                enabled: parseJson(ResponsePayload.enabled),
                reportingVar_predictiveRule: parseJson(p?.predictiveRuleId),
                predictiveMessageAvailable: parseJson(p?.predictiveMessageAvailable),
                questionOrMessage: parseJson(p?.questionOrMessage),
                voiceScriptContent: parseJson(p?.predictiveScript?.voiceScriptContent),
                predictiveIntent: parseJson(p?.intent),
                action: parseJson(p?.action),
                systemOverride: parseJson(p?.routingParams?.systemOverride),
                returnCode: "0"
            };

        } else {

            sessionParams = {
                returnCode: "1"
            };

        }
        return sendWebhookResponse(res, sessionId, tag, sessionParams);

    } catch (err) {

        return sendErrorResponse(res, sessionId, tag, err, 1, { enabled: false });

    }

}