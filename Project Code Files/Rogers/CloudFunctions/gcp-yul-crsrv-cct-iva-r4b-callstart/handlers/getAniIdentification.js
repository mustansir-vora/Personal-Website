import {
    apiClient,
    logger,
    parseJson,
    getRequestHeaders,
    sendWebhookResponse,
    sendErrorResponse
} from "@roger/r4b-common-nodemodules";


import { DisambigParams } from "../services/disambiguationAccountService.js";


export async function getAniIdentification(req, res, params, tag, sessionId) {

    let sessionParams = {};
    let tn = params.ani;
    let tnType = "ANI";

    try {

        const env = params.mwInstance || "qa4";
        const ani = params.ani || "NA";
        const flowLanguage = params.flowLanguage || "en-US";

        const accountNumber = params.userInputAccount || params.accountNumber || "NA";
        const idType = params.callerIdType || "NA";
        const idNumber = params.callerIdType === "phone" ? ani : accountNumber;

        const searchHomeContact = params.searchHomeContact || false;
        const searchMobileContact = params.searchMobileContact || false;
        const searchBusinessContact = params.searchBusinessContact || false;
        const searchBrand = params.brand || "NA";
        const predictiveInd = params.predictiveInd || false;

        logger.logWebhookRequest(sessionId, tag, {
            ani,
            env,
            accountNumber,
            idType,
            idNumber,
            searchHomeContact,
            searchMobileContact,
            searchBusinessContact,
            searchBrand,
            predictiveInd
        });

        const apiUrl = params[tag][env];

        const { headers, tokenConfig } = getRequestHeaders(sessionId, ani, params, env);
        // request body
        const requestBody = {
            identifiers: {
                idType,
                idNumber,
                filters: {
                    searchHomeContact,
                    searchMobileContact,
                    searchBusinessContact,
                    searchBrand
                },
                predictiveInd
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

        const { Status, ResponsePayload } = apiResult;

        if (Status === 200) {

            const accounts = ResponsePayload.billingAccounts || [];

            const billingAccounts =
                parseJson(ResponsePayload.numberOfBillingAccounts) === "NA"
                    ? 0
                    : parseJson(ResponsePayload.numberOfBillingAccounts);

            const uniqueBillingLanguage =
                parseJson(ResponsePayload.uniqueBillingLanguage);

            const profileClassification =
                parseJson(ResponsePayload.profileSummary?.profileClassification);

            const billingLanguage =
                parseJson(accounts[0]?.billingLanguage);

            const dirtyANI =
                parseJson(ResponsePayload.dirtyANI);

            const parsedItn = parseJson(params.itn);

            if (parsedItn !== "NA") {
                tn = params.itn;
                tnType = "ITN";
            }

            if (billingAccounts !== 0 && billingAccounts <= 4) {

                sessionParams = await DisambigParams(accounts, billingAccounts, flowLanguage);

                sessionParams = {
                    ...sessionParams,
                    uniqueBillingLanguage,
                    billingLanguage,
                    dirtyANI,
                    tn,
                    tnType,
                    profileClassification,
                    reportingVar_customerType: profileClassification,
                    api: {
                        ...params.api,
                        identifyBillingAccounts: ResponsePayload
                    }
                };

            } else {

                sessionParams = {
                    uniqueBillingLanguage,
                    billingLanguage,
                    dirtyANI,
                    returnCode: "0",
                    numberOfBillingAccounts: billingAccounts,
                    accountSessionList: ResponsePayload.billingAccounts.map(a => parseJson(a.sessionId)),
                    profileClassification,
                    tn,
                    tnType,
                    api: {
                        ...params.api,
                        identifyBillingAccounts: ResponsePayload
                    }
                };

            }
        } else {

            sessionParams = {
                returnCode: "1",
                tn,
                tnType
            };

        }

        return sendWebhookResponse(res, sessionId, tag, sessionParams);

    } catch (err) {
        return sendErrorResponse(res, sessionId, tag, err, 1, { tn, tnType });
    }

}