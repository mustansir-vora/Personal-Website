import {
    apiClient,
    logger,
    parseJson,
    getRequestHeaders,
    sendWebhookResponse,
    sendErrorResponse
} from "@roger/r4b-common-nodemodules";


// billing profile related handler
export async function getBillingProfile(req, res, params, tag, sessionId) {

    try {

        const env = params.mwInstance || "qa4";
        const ani = params.ani || "NA";

        const accountNumber = params.accountNumber || "NA";
        const maestroInd = params.maestroInd || false;
        const lob = params.lob || "NA";
        const accountBrand = params.brand || "ROGERS";
        const sessionIdInContext = params.sessionIdInContext || "NA";

        logger.logWebhookRequest(sessionId, tag, {
            env,
            accountNumber,
            maestroInd,
            lob,
            accountBrand
        });

        const apiUrl = params[tag][env]
            .replace("${accountNumber}", accountNumber)
            .replace("${maestroInd}", maestroInd)
            .replace("${lob}", lob)
            .replace("${accountBrand}", accountBrand)
            .replace("${sessionId}", sessionIdInContext);

        const { headers, tokenConfig } = getRequestHeaders(sessionId, ani, params, env);


        const apiResult = await apiClient.getRequest({
            sessionId,
            tag,
            url: apiUrl,
            headers,
            tokenConfig
        });

        const { Status, ResponsePayload } = apiResult;

        let sessionParams = {};

        if (Status === 200) {

            const b = ResponsePayload.billingProfile;

            sessionParams = {
                billingAccountNumber: parseJson(b?.billingAccountNumber),
                actualBalance: parseJson(b?.balanceDetails?.actualBalance),
                returnCode: "0"
            };

        } else {

            sessionParams = {
                returnCode: "1"
            };

        }

        return sendWebhookResponse(res, sessionId, tag, sessionParams);

    } catch (err) {

        return sendErrorResponse(res, sessionId, tag, err, 1);

    }
}
