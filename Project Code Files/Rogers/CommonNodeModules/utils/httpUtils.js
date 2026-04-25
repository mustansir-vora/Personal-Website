import * as logger from "./logger.js";

export function buildWebhookResponse(parameters) {

    return {
        sessionInfo: {
            parameters
        }
    };

}


export function getRequestHeaders(sessionId, ani, params, env) {

    const headers = {
        cdr: sessionId,
        transactionId: `${sessionId}-${ani || "NA"}`,
        transactionDateTime: new Date().toISOString()
    };

    const tokenConfig = {
        timeOutMs: params.timeOutMs,
        apiAttempts: params.apiAttempts,
        tokenUrl: params.getToken?.[env],
        scope: params.scope?.[env],
        tokenRefreshTimeMin: params.tokenRefreshTimeMin
    };

    return { headers, tokenConfig };
}



/* ------------------------------
   Send Success Response
--------------------------------*/
export function sendWebhookResponse(res, sessionId, tag, sessionParams) {

    const webhookResponse = buildWebhookResponse(sessionParams);
    logger.logWebhookResponse(sessionId, tag, webhookResponse);
    return res.status(200).json(webhookResponse);

}


/* ------------------------------
   Send Error Response
--------------------------------*/
export function sendErrorResponse(res, sessionId, tag, err, attempt, defaultParams = {}) {

    logger.logErrorResponse({
        sessionId,
        tag,
        attempt,
        err
    });

    const webhookResponse = buildWebhookResponse({
        returnCode: "1",
        ...defaultParams
    });
    logger.logWebhookResponse(sessionId, tag, webhookResponse);
    res.setHeader("Content-Type", "application/json");
    return res.status(200).send(webhookResponse);
}
