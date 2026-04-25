import {
    apiClient,
    logger,
    parseJson,
    getTerminationMatrix,
    normalizeRequestValue,
    getRequestHeaders,
    sendWebhookResponse,
    sendErrorResponse
} from "@roger/r4b-common-nodemodules";

//
export async function getTerminationAction(req, res, params, tag, sessionId) {

    const fallbackInput = {
        flagIndRuleId: "NA",
        customerType: parseJson(params.profileClassification),
        language: parseJson(params.flowLanguage),
        applicationId: parseJson(params.applicationId),
        delinquencyRuleId: parseJson(params.delinquencyRuleId),
        feature: parseJson(params.featureRuleId),
        intent: parseJson(params.savedIntent),
        intentGroup: parseJson(params.nluConfig?.intentGroup)
    };

    try {

        const env = params.mwInstance || "qa4";
        const apiUrl = params[tag][env];

        logger.logWebhookRequest(sessionId, tag, {
            env,
            sessionIds: params.accountSessionList || [],
            sessionIdInContext: normalizeRequestValue(params.sessionIdInContext),
            fallbackInput,
            preRouteOverride: params.preRouteOverride,
            preRouteOverrideRuleId: params.preRouteOverrideRuleId
        });

        const { headers, tokenConfig } = getRequestHeaders(
            sessionId,
            params.ani,
            params,
            env
        );

        const requestBody = buildTerminationRequestBody(params);

        const [apiSettle, matrixSettle] = await Promise.allSettled([
            apiClient.postRequest({
                sessionId,
                tag,
                url: apiUrl,
                headers,
                data: requestBody,
                tokenConfig
            }),
            getTerminationMatrix({ sessionId, tag, input: fallbackInput })
        ]);

        const apiResult =
            apiSettle.status === "fulfilled" ? apiSettle.value : null;

        const matrixResult =
            matrixSettle.status === "fulfilled" ? matrixSettle.value : null;

        let sessionParams;

        if (
            apiResult &&
            apiResult.Status === 200 &&
            parseJson(apiResult.ResponsePayload?.terminationActionFound) === true
        ) {

            sessionParams = extractTerminationAction(
                apiResult.ResponsePayload,
                params.flowLanguage
            );

        }
        else if (matrixResult && matrixResult.Status === 200) {

            sessionParams = buildMatrixFallback(matrixResult);

            logger.logConsole(
                sessionId,
                tag,
                "Primary API failed, fallback matrix used"
            );
        }
        else {

            sessionParams = {
                terminationActionFound: false,
                isTerminationFromCSV: true,
                messagesAlreadyDelivered: "BusServ_QA",
                returnCode: "1"
            };

        }

        return sendWebhookResponse(res, sessionId, tag, sessionParams);

    }
    catch (err) {

        try {

            const matrixFallback = await getTerminationMatrix({
                sessionId,
                tag,
                input: fallbackInput
            });

            const sessionParams = buildMatrixFallback(matrixFallback);

            logger.logConsole(
                sessionId,
                tag,
                "Fallback matrix used from catch block"
            );

            return sendWebhookResponse(res, sessionId, tag, sessionParams);

        }
        catch (matrixErr) {

            return sendErrorResponse(res, sessionId, tag, matrixErr, 2, {
                terminationActionFound: false,
                isTerminationFromCSV: true,
                messagesAlreadyDelivered: "BusServ_QA"
            });

        }

    }

}


function buildTerminationRequestBody(params) {

    const sessionIds = params.accountSessionList || [];
    const sessionIdInContext = normalizeRequestValue(params.sessionIdInContext);
    const preRouteOverride = params.preRouteOverrideRuleId === "NA" ? params.preRouteOverride : params.preRouteOverrideRuleId;

    return {
        sessions: {
            sessionIds,
            sessionIdInContext
        },
        itmParams: {
            brand: params.brand,
            applicationId: normalizeRequestValue(params.applicationId),
            language: normalizeRequestValue(params.flowLanguage),
            customerType: normalizeRequestValue(params.profileClassification),
            intentCore: normalizeRequestValue(params.nluConfig?.intentGroup),
            intentNLU: normalizeRequestValue(params.savedIntent),
            preRouteOverride: normalizeRequestValue(preRouteOverride),
            systemOverride: normalizeRequestValue(params.systemOverride),
            listloadCategoryA: normalizeRequestValue(params.listloadCategoryA),
            listloadCategoryB: normalizeRequestValue(params.listloadCategoryB),
            listloadCategoryC: normalizeRequestValue(params.listloadCategoryC),
            listloadCategoryD: normalizeRequestValue(params.listloadCategoryD),
            listloadCategoryE: normalizeRequestValue(params.listloadCategoryE),
            delinquencyRuleId: normalizeRequestValue(params.delinquencyRuleId),
            provider: "",
            channel: "voice",
            billingSystem: normalizeRequestValue(params.billingSystem),
            product: "",
            feature: normalizeRequestValue(params.featureRuleId),
            flagIndRuleId: "",
            footPrint: "",
            transferType: normalizeRequestValue(params.transferType)
        }
    };

}

function extractTerminationAction(payload, language) {

    const primary = payload.primaryTerminationAction || {};
    const secondary = payload.secondaryTerminationAction || {};

    const primaryType = parseJson(primary.actionType);
    const primaryParams = primary.actionParams || {};
    const itmActionId = primaryParams.itmActionId || "NA";
    const primaryTransferScriptId = parseJson(primaryParams.transferScript?.voiceScriptId);

    const secondaryType = parseJson(secondary.actionType);
    const secondaryParams = secondary.actionParams || {};
    const secondaryTransferScriptId = parseJson(secondaryParams.transferScript?.voiceScriptId);

    const primaryScript = getScriptContent(primaryType, primaryParams);
    const secondaryScript = getScriptContent(secondaryType, secondaryParams);

    const { transferDID, transferVQ, transferAlert } =
        getTransferParams(primaryParams, secondaryType, secondaryParams);


    return {
        terminationActionFound: parseJson(payload.terminationActionFound),
        primaryActionType: primaryType,
        primaryScriptContent: primaryScript,
        secondaryActionType: secondaryType,
        secondaryScriptContent: secondaryScript,
        smsId: parseJson(primaryParams.smsId),
        itmActionId,
        primaryTransferScriptId,
        secondaryTransferScriptId,
        transferDID: parseJson(transferDID),
        transferVQ: parseJson(transferVQ),
        reportingVar_VQ: parseJson(transferVQ),
        reportingVar_DID: parseJson(transferDID),
        transferAlert,
        reasonCode1: parseJson(payload.terminationReasonCodes?.reasonCode1),
        reasonCode2: parseJson(payload.terminationReasonCodes?.reasonCode2),
        reasonCode3: parseJson(payload.terminationReasonCodes?.reasonCode3),
        ivrResult: parseJson(payload.terminationReasonCodes?.result),
        callnotes: parseJson(payload.terminationReasonCodes?.note),
        agentAlertEN: language === "en-US" ? transferAlert : "NA",
        agentAlertFR: language === "fr-CA" ? transferAlert : "NA",
        returnCode: "0",
        status: 200
    };

}

function buildMatrixFallback(matrixResult) {

    return {
        terminationActionFound: true,
        primaryActionType: matrixResult.ResponsePayload.exitAction,
        transferDID: matrixResult.ResponsePayload.transferDID,
        transferVQ: matrixResult.ResponsePayload.transferVQ,
        primaryScriptContent: matrixResult.ResponsePayload.terminationMessage,
        returnCode: matrixResult.ReturnCode,
        status: matrixResult.Status,
        isTerminationFromCSV: true,
        messagesAlreadyDelivered: "BusServ_QA"
    };

}

function getTransferParams(primaryParams, secondaryType, secondaryParams) {

    let transferDID = parseJson(primaryParams.transferDID);
    let transferVQ = parseJson(primaryParams.transferVQ);
    let transferAlert = parseJson(primaryParams.transferAlert);


    if (secondaryType === "transfer") {
        transferDID = parseJson(secondaryParams.transferDID);
        transferVQ = parseJson(secondaryParams.transferVQ);
        transferAlert = parseJson(secondaryParams.transferAlert);
    }

    return {
        transferDID,
        transferVQ,
        transferAlert
    };

}

function getScriptContent(actionType, actionParams) {

    if (actionType === "transfer") {
        return parseJson(actionParams.transferScript?.voiceScriptContent);
    }
    if (actionType === "termination") {
        return parseJson(actionParams.terminationScript?.voiceScriptContent);
    }
    return "NA";
}