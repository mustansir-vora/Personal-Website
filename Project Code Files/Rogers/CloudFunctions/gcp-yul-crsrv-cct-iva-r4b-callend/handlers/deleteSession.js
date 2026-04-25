import {
    apiClient,
    logger,
    getRequestHeaders,
    sendWebhookResponse,
    sendErrorResponse
} from "@roger/r4b-common-nodemodules";

export async function deleteSession(req, res, params, tag, sessionId) {
    let sessionParams = {};
    let reportingVar_sessionDuration = "NA";

    try {

        const env = params.mwInstance || "qa4";
        const sessionIdsToDelete = params.accountSessionList || [];

        const startTime = params.reportingVar_callStartTime;
        const endTime = params.reportingVar_callEndTime;

        if (startTime && endTime) {

            const start = new Date(startTime.replace(" ", "T"));
            const end = new Date(endTime.replace(" ", "T"));

            reportingVar_sessionDuration = end - start;
        }

        logger.logWebhookRequest(sessionId, tag, {
            env,
            sessionIdsToDelete,
            startTime,
            endTime,
            reportingVar_sessionDuration
        });

        const apiUrlBase = params[tag][env];

        if (!Array.isArray(sessionIdsToDelete) || sessionIdsToDelete.length === 0) {

            sessionParams = {
                deleteStatus: "No_Sessions_Provided",
                returnCode: "1"
            };

            return sendWebhookResponse(res, sessionId, tag, sessionParams);
        }

        const queryParams = sessionIdsToDelete
            .map(id => `sessionIdsToDelete=${encodeURIComponent(id)}`)
            .join("&");

        const apiUrl = `${apiUrlBase}${queryParams}`;

        const { headers, tokenConfig } = getRequestHeaders(
            sessionId,
            params.ani,
            params,
            env
        );
        const apiResult = await apiClient.deleteRequest({
            sessionId,
            tag,
            url: apiUrl,
            headers,
            tokenConfig
        });

        const { Status } = apiResult;

        sessionParams = {
            deleteStatus: Status === 202 || Status === 200 ? "Success" : "Failed",
            deleteApiStatus: Status,
            returnCode: Status === 202 || Status === 200 ? "0" : "1",
            reportingVar_sessionDuration
        };

        return sendWebhookResponse(res, sessionId, tag, sessionParams);

    }

    catch (err) {

        return sendErrorResponse(res, sessionId, tag, err, 1, {
            deleteStatus: "Error",
            reportingVar_sessionDuration
        });

    }

}