import { logger, sendWebhookResponse, sendErrorResponse } from "@roger/r4b-common-nodemodules";
import { executeApiCall } from "../services/srvn-executeApiCall.js";
import { getCaseTypeLevels } from "../services/srvn-getCaseTypeLevels.js";
import { DEFAULT_ENV } from "../services/srvn-config.js";

export async function createOutageNotification(req, res, params, tag, sessionId) {
    const env = params.mwInstance || DEFAULT_ENV;
    const ani = params.ani || "Unknown-ANI";
    const caseTypeLevels = getCaseTypeLevels(params?.invokedOSFrom);

    try {
        const payloadParams = {
            accountNumber: params?.accountNumber,
            outageNumber: params?.outageNumber,
            parentCaseId: params?.parentCaseId,
            priority: params?.priority,
            communicationMethod: params?.communicationMethod,
            communicationValue: params?.communicationValue || ani,
            caseTypeLevels
        };

        logger.logWebhookRequest(sessionId, tag, payloadParams);

        // Note expectedStatus: 201 overrides the default 200 validation
        const d = await executeApiCall({
            params, tag, env, sessionId, ani,
            method: "POST", data: payloadParams, expectedStatus: 201
        });

        return sendWebhookResponse(res, sessionId, tag, {
            returnCode: "0",
            notificationCaseId: d.notificationCaseId || "",
        });
    } catch (error) {
        return sendErrorResponse(res, sessionId, tag, error, 1, { returnCode: "1" });
    }
}
