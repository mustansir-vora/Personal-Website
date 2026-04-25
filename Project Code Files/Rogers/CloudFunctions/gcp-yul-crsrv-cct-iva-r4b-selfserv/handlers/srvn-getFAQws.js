import { logger, sendWebhookResponse, sendErrorResponse } from "@roger/r4b-common-nodemodules";
import { executeApiCall } from "../services/srvn-executeApiCall.js";
import { DEFAULT_ENV } from "../services/srvn-config.js";

export async function getFAQws(req, res, params, tag, sessionId) {
    const env = params.mwInstance || DEFAULT_ENV;
    const ani = params.ani || "Unknown-ANI";
    const language = params?.flowLanguage || "en-US";
    const faqIntentId = params?.savedIntent;

    try {
        logger.logWebhookRequest(sessionId, tag, { language, faqIntentId });

        const d = await executeApiCall({
            params, tag, env, sessionId, ani,
            urlResolver: (baseUrl) => baseUrl.replace("${language}", language).replace("${faqIntentId}", faqIntentId)
        });

        return sendWebhookResponse(res, sessionId, tag, {
            returnCode: "0",
            faqAvailable: d?.faqAvailable,
            faqIntentId: d?.faqIntentId,
            faqVoiceScriptId: d?.faqParams?.script?.voiceScriptId,
            faqVoiceScriptContent: d?.faqParams?.script?.voiceScriptContent,
            introvoiceScriptId: d?.faqParams?.introScript?.voiceScriptId,
            introvoiceScriptContent: d?.faqParams?.introScript?.voiceScriptContent,
            confirmvoiceScriptId: d?.faqParams?.confirmScript?.voiceScriptId,
            confirmvoiceScriptContent: d?.faqParams?.confirmScript?.voiceScriptContent,
            smsId: d?.faqParams?.smsId,
            faqNextAction: d?.faqParams?.nextAction?.toLowerCase(),
            interactionLabel: d?.faqParams?.interactionLabel,
            interactionNotes: d?.faqParams?.interactionNotes,
            notes: d?.faqParams?.notes,
            faqResponse: d,
        });
    } catch (error) {
        return sendErrorResponse(res, sessionId, tag, error, 1, { returnCode: "1" });
    }
}
