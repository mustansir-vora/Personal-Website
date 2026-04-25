import {
    apiClient,
    logger,
    parseJson,
    getRequestHeaders,
    sendWebhookResponse,
    sendErrorResponse
} from "@roger/r4b-common-nodemodules";



export async function getSmsParameters(req, res, params, tag, sessionId) {

    let sessionParams = {};

    try {

        const env = params.mwInstance || "qa4";
        const brand = params.brand;
        const language = params.flowLanguage;
        const smsId = params.smsId;

        logger.logWebhookRequest(sessionId, tag, {
            env,
            brand,
            language,
            smsId
        });

        const apiUrl = params[tag][env]
            .replace("${brand}", brand)
            .replace("${language}", language)
            .replace("${smsId}", smsId);

        const { headers, tokenConfig } = getRequestHeaders(
            sessionId,
            params.ani || "NA",
            params,
            env
        );

        const apiResult = await apiClient.getRequest({
            sessionId,
            tag,
            url: apiUrl,
            headers,
            tokenConfig
        });

        const { Status, ResponsePayload } = apiResult;

        if (Status === 200) {

            const smsParams = ResponsePayload.smsParams || {};

            const smsAvailable = parseJson(ResponsePayload.smsAvailable);
            const smsTemplateId = parseJson(smsParams.smsTemplateId);
            const smsLeadInScriptContent = parseJson(smsParams.smsLeadInScript?.voiceScriptContent);
            const smsOptInQuestionScriptContent = parseJson(smsParams.smsOptInQuestionScript?.voiceScriptContent);
            const smsForcePromptForPhoneInd = parseJson(smsParams.smsForcePromptForPhoneInd);
            const smsAllowPromptForPhoneInd = parseJson(smsParams.smsAllowPromptForPhoneInd);
            const smsAllowPassiveMessageInd = parseJson(smsParams.smsAllowPassiveMessageInd);
            const smsPhoneNumberScript = parseJson(smsParams.smsGetPhoneNumberScript?.voiceScriptContent);
            const smsConfirmPhoneNumberScript = parseJson(smsParams.smsConfirmPhoneNumberScript?.voiceScriptContent);
            const smsPlaySuccessMsgScript = parseJson(smsParams.smsPlaySuccessMsgScript?.voiceScriptContent);
            const smsPlayFailMsgScript = parseJson(smsParams.smsPlayFailMsgScript?.voiceScriptContent);

            sessionParams = {
                smsAvailable,
                smsLeadInScriptContent,
                smsOptInQuestionScriptContent,
                smsForcePromptForPhoneInd,
                smsAllowPromptForPhoneInd,
                smsAllowPassiveMessageInd,
                smsPlaySuccessMsgScript,
                smsPlayFailMsgScript,
                smsTemplateId,
                smsPhoneNumberScript,
                smsConfirmPhoneNumberScript,
                returnCode: "0"
            };

        } else {

            sessionParams = {
                smsAvailable: false,
                returnCode: "1"
            };

        }

        return sendWebhookResponse(res, sessionId, tag, sessionParams);

    }

    catch (err) {

        return sendErrorResponse(res, sessionId, tag, err, 1, {
            smsAvailable: false
        });

    }

}