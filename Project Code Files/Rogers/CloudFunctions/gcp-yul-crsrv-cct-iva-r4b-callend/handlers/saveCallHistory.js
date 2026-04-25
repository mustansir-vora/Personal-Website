import {
    apiClient,
    logger,
    normalizeRequestValue,
    getRequestHeaders,
    sendWebhookResponse,
    sendErrorResponse
} from "@roger/r4b-common-nodemodules";


export async function saveCallHistory(req, res, params, tag, sessionId) {

    let sessionParams = {};

    try {

        const env = params.mwInstance || "qa4";

        const sessionIds = params.accountSessionList || [];
        const sessionIdInContext = normalizeRequestValue(params.sessionIdInContext);

        const conversationId = normalizeRequestValue(params.sessionId);

        const GDFCallStartTime = normalizeRequestValue(params.callStartTime);

        const callStartTime = GDFCallStartTime
            ? Math.floor(
                new Date(GDFCallStartTime.replace(" ", "T") + "Z").getTime() / 1000
            ).toString()
            : "";

        const dnis = normalizeRequestValue(params.dnis);
        const brand = normalizeRequestValue(params.brand);
        const applicationId = normalizeRequestValue(params.applicationId);
        const ani = normalizeRequestValue(params.ani);

        const accountNumber =
            normalizeRequestValue(params.accountNumber) === ""
                ? null
                : normalizeRequestValue(params.accountNumber);

        const language = normalizeRequestValue(params.flowLanguage);
        const customerType = normalizeRequestValue(params.profileClassification);
        const systemOverride = normalizeRequestValue(params.systemOverride);
        const preRouteOverride = normalizeRequestValue(params.preRouteOverrideRuleId);

        const tn = normalizeRequestValue(params.tn);
        const tnType = normalizeRequestValue(params.tnType);

        const routingSegments = Array.isArray(params.routingSegment)
            ? params.routingSegment
                .flat()
                .filter(seg => seg && seg !== "NA")
                .join(",")
            : "";

        const profileSegments = Array.isArray(params.profileSegment)
            ? params.profileSegment
                .flat()
                .filter(seg => seg && seg !== "NA")
                .join(",")
            : "";

        const intentGrouping = normalizeRequestValue(params.nluConfig?.intentGroup);
        const intent = normalizeRequestValue(params.savedIntent);
        const completedSelfServe = normalizeRequestValue(params.completedSSName);

        const ecid = normalizeRequestValue(params.ecid);
        const contactId = normalizeRequestValue(params.contactId);

        const messageRuleId =
            Array.isArray(params.messageRuleId) && params.messageRuleId.length > 0
                ? params.messageRuleId.join(",")
                : "";

        const feature = normalizeRequestValue(params.featureRuleId);
        const product = normalizeRequestValue(params.product);

        const transferAlert = normalizeRequestValue(params.transferAlert);
        const transferDID = normalizeRequestValue(params.transferDID);
        const transferVQ = normalizeRequestValue(params.transferVQ);
        const transferType = normalizeRequestValue(params.transferType);

        const callEndReason = normalizeRequestValue(params.endReason);
        const callActionType = normalizeRequestValue(params.callActionType);

        logger.logWebhookRequest(sessionId, tag, {
            env,
            sessionIds,
            sessionIdInContext,
            conversationId,
            callStartTime,
            dnis,
            brand,
            applicationId,
            ani,
            accountNumber,
            language,
            customerType,
            intentGrouping,
            intent,
            systemOverride,
            preRouteOverride,
            ecid,
            contactId,
            completedSelfServe,
            messageRuleId,
            routingSegments,
            profileSegments,
            feature,
            product,
            callEndReason,
            callActionType,
            transferType,
            transferAlert,
            transferDID,
            transferVQ
        });

        const apiUrl = params[tag][env];

        const { headers, tokenConfig } = getRequestHeaders(
            sessionId,
            ani,
            params,
            env
        );

        const requestBody = {
            callHistoryRecord: {
                conversationId,
                callStartTime,
                sessionId: sessionIds.length > 0 ? sessionIds.join(",") : sessionIdInContext,
                dnis,
                brand,
                applicationId,
                accountNumber,
                tn,
                tnType,
                ecid,
                contactId,
                language,
                customerType,
                intentGrouping,
                intent,
                channel: "voice",
                systemOverride,
                preRouteOverride,
                completedSelfServe,
                messageRuleId,
                routingSegments,
                profileSegments,
                feature,
                product,
                callEndReason,
                callActionType,
                transferType,
                transferAlert,
                transferDID,
                transferVQ
            }
        };

        const apiResult = await apiClient.postRequest({
            sessionId,
            tag,
            url: apiUrl,
            headers,
            data: requestBody,
            tokenConfig
        });

        const { Status } = apiResult;

        if (Status === 200 || Status === 204) {

            sessionParams = {
                postCallHistoryStatus: "SUCCESS",
                returnCode: "0"
            };

        } else {

            sessionParams = {
                postCallHistoryStatus: "FAILED",
                returnCode: "1"
            };

        }

        return sendWebhookResponse(res, sessionId, tag, sessionParams);

    }

    catch (err) {

        return sendErrorResponse(res, sessionId, tag, err, 1, {
            postCallHistoryStatus: "ERROR",
        });

    }

}