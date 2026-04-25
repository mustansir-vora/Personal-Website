import {
    apiClient,
    logger,
    parseJson,
    getRequestHeaders,
    sendWebhookResponse,
    sendErrorResponse
} from "@roger/r4b-common-nodemodules";



export async function sendSmsMessage(req, res, params, tag, sessionId) {

    let sessionParams = {};

    try {

        const env = params.mwInstance || "qa4";
        const brand = params.brand;
        const language = params.flowLanguage;

        let smsPhone = "";
        let messageRuleId = params.messageRuleId || "";

        const accountNumber = parseJson(params.accountNumber);
        const smsAttributes = params.smsAttributes || {};

        // Receiver details (UNCHANGED LOGIC)
        if (parseJson(params.reusablePhone) !== "NA") {
            smsPhone = params.reusablePhone;
        }
        else if (parseJson(params.subscriptionId) !== "NA") {
            smsPhone = params.subscriptionId;
        }
        else {
            smsPhone = parseJson(
                params.subscriptionsList?.[0]?.subscriptionSerialNumber
            );
        }

        const smsTemplateId = params.smsTemplateId;

        logger.logWebhookRequest(sessionId, tag, {
            env,
            brand,
            language,
            smsPhone,
            smsTemplateId,
            smsAttributes
        });

        const apiUrl = params[tag][env]
            .replace("${brand}", brand)
            .replace("${language}", language);

        const { headers, tokenConfig } = getRequestHeaders(
            sessionId,
            params.ani,
            params,
            env
        );

        const requestBody = {
            receiver: {
                firstName: "firstName",
                lastName: "lastName",
                phoneNumber: smsPhone,
                ...(accountNumber !== "NA" && { accountNumber })
            },
            smsTemplateId,
            ...(smsAttributes && Object.keys(smsAttributes).length > 0 && { smsAttributes })
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

        if (Status === 200 || Status === 202) {

            const smsMessageType = parseJson(ResponsePayload.smsMessageType);
            const smsMessageSent = parseJson(ResponsePayload.smsMessageSent);
            const messageId = parseJson(ResponsePayload.messageId);

            messageRuleId =
                !messageRuleId || messageRuleId === "NA"
                    ? messageId
                    : `${messageRuleId},${messageId}`;

            sessionParams = {
                smsMessageType,
                smsMessageSent,
                messageRuleId,
                smsStatus: "api_success",
                returnCode: "0"
            };

        }
        else {

            sessionParams = {
                smsStatus: "api_failure",
                returnCode: "1"
            };

        }

        return sendWebhookResponse(res, sessionId, tag, sessionParams);

    }
    catch (err) {

        return sendErrorResponse(res, sessionId, tag, err, 1, {
            smsStatus: false
        });

    }

}