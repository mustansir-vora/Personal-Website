import {
    apiClient,
    logger,
    parseJson,
    getRequestHeaders,
    sendWebhookResponse,
    sendErrorResponse,
    resolveLanguageSwitch
} from "@roger/r4b-common-nodemodules";

import { buildBroadcastSessionParams, buildFallbackSessionParams } from "../services/broadcastServices.js";

//broadcast related handler
export async function getBroadcastMessage(req, res, params, tag, sessionId) {
    let sessionParams = {};
    try {
        let reportingVar_messageID = params.reportingVar_messageID || "";
        let reportingVar_messageType = params.reportingVar_messageType || "";
        const isSwitchInvoked = params.isSwitchInvoked === true;

        /* ---------- Language Switch Handling ---------- */
        const resolved = await resolveLanguageSwitch(params, sessionId, tag);

        const resolvedParams = resolved.params;
        const resolvedSessionId = resolved.sessionId;
        const env = resolvedParams.mwInstance || "qa4";
        const broadcastId = parseJson(resolvedParams.broadCastId);

        if (broadcastId === "NA") {
            logger.logConsole(resolvedSessionId, tag, "No broadCastId found in params");

            sessionParams = buildFallbackSessionParams(
                reportingVar_messageID,
                reportingVar_messageType,
                isSwitchInvoked,
                resolvedParams
            );
            return sendWebhookResponse(res, resolvedSessionId, tag, sessionParams);
        }

        const dnis = resolvedParams.dnis || "NA";
        const language = resolvedParams.flowLanguage || "NA";
        const ani = resolvedParams.ani || "NA";
        const brand = resolvedParams.brand || "NA";
        const applicationId = resolvedParams.applicationId || "NA";
        const accountNumber = resolvedParams.accountNumber || "";
        const sessionIds = resolvedParams.accountSessionList || [];

        reportingVar_messageID =
            reportingVar_messageID
                ? `${reportingVar_messageID},${broadcastId}`
                : broadcastId;

        logger.logWebhookRequest(resolvedSessionId, tag, {
            env,
            broadcastId,
            dnis,
            language,
            brand,
            applicationId,
            sessionIds
        });

        /* ---------- API Setup ---------- */

        const apiUrl = resolvedParams[tag][env];
        const { headers, tokenConfig } = getRequestHeaders(resolvedSessionId, ani, resolvedParams, env);

        const requestBody = {
            broadcastId,
            language,
            applicationId,
            brand,
            sessionIds: accountNumber === "" ? sessionIds : [],
            sessionIdInContext: accountNumber === "" ? "" : sessionIds[0]
        };

        const apiResult = await apiClient.postRequest({
            sessionId: resolvedSessionId,
            tag,
            url: apiUrl,
            headers,
            data: requestBody,
            tokenConfig
        });

        const { Status, ResponsePayload } = apiResult;


        sessionParams = (Status === 200 && ResponsePayload.enabled === true)
            ? buildBroadcastSessionParams(
                ResponsePayload,
                reportingVar_messageID,
                reportingVar_messageType,
                resolvedParams
            )
            : buildFallbackSessionParams(
                reportingVar_messageID, reportingVar_messageType,
                isSwitchInvoked,
                resolvedParams
            );

        return sendWebhookResponse(res, resolvedSessionId, tag, sessionParams);


    } catch (err) {
        return sendErrorResponse(res, sessionId, tag, err, 1, { enabled: false });
    }

}