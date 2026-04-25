import * as logger from "../utils/logger.js";
import { sendWebhookResponse } from "../utils/httpUtils.js";


export async function executeHandler(req, res, handlerRegistry) {

    const params = req.body?.sessionInfo?.parameters || {};
    const tag = req.body?.fulfillmentInfo?.tag || "Unknown-Tag";
    const sessionId = params.sessionId || "Unknown-Session";

    logger.logWebhookDetails(sessionId, tag);



    const handler = handlerRegistry[tag];

    if (handler) {
        return handler(req, res, params, tag, sessionId);
    }

    logger.logConsole(sessionId, tag, "Invalid tag for this function");

    return sendWebhookResponse(res, sessionId, tag, {
        errorDetails: "Invalid tag for this function"
    });

}