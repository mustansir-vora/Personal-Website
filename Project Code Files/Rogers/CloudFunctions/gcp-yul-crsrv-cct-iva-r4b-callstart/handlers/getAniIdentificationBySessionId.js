import {
    apiClient,
    logger,
    parseJson,
    getRequestHeaders,
    sendWebhookResponse,
    sendErrorResponse
} from "@roger/r4b-common-nodemodules";


import { DisambigParams } from "../services/disambiguationAccountService.js";


export async function getAniIdentificationBySessionId(req, res, params, tag, sessionId) {

    try {

        const env = params.mwInstance || "qa4";
        const ani = params.ani || "NA";
        const sessionList = params.accountSessionList || [];
        const flowLanguage = params.flowLanguage || "en-US";

        logger.logWebhookRequest(sessionId, tag, {
            env,
            sessionList
        });

        const apiUrl = params[tag.substring(0, 20)][env];
        const { headers, tokenConfig } = getRequestHeaders(sessionId, ani, params, env);

        const requestBody = {
            sessionIds: sessionList
        };

        const apiResult = await apiClient.postRequest({
            sessionId,
            tag,
            url: apiUrl,
            headers,
            data: requestBody,
            tokenConfig
        });

        const { Status, ResponsePayload } = apiResult;

        const profileClassification =
            parseJson(ResponsePayload.profileSummary?.profileClassification);

        const dirtyANI =
            parseJson(ResponsePayload.dirtyANI);

        const billingAccounts =
            parseJson(ResponsePayload.numberOfBillingAccounts) === "NA"
                ? 0
                : parseJson(ResponsePayload.numberOfBillingAccounts);

        let sessionParams = {};

        if (Status === 200 && billingAccounts !== 0 && billingAccounts <= 4) {
            const accounts = ResponsePayload.billingAccounts || [];
            sessionParams = await DisambigParams(
                accounts,
                billingAccounts,
                flowLanguage
            );

            sessionParams = {
                ...sessionParams,
                profileClassification,
                dirtyANI,
                api: {
                    ...params.api,
                    identifyBillingAccounts: ResponsePayload
                }
            };

        } else if (billingAccounts === 0) {

            sessionParams = {
                returnCode: "0",
                numberOfBillingAccounts: billingAccounts,
                profileClassification,
                api: {
                    ...params.api,
                    identifyBillingAccounts: ResponsePayload
                }
            };

        }
        else if (billingAccounts > 4) {
            sessionParams = {
                returnCode: "0",
                numberOfBillingAccounts: billingAccounts,
                profileClassification,
                api: {
                    ...params.api,
                    identifyBillingAccounts: ResponsePayload
                }
            };
        }

        else {

            sessionParams = {
                returnCode: "1"
            };

        }
        return sendWebhookResponse(res, sessionId, tag, sessionParams);

    } catch (err) {
        return sendErrorResponse(res, sessionId, tag, err, 1);
    }
}
