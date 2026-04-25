import { logger, sendWebhookResponse, sendErrorResponse } from '@roger/r4b-common-nodemodules';
import { executeApiCall } from '../services/executeApiCall.js';

export async function getAuthenticationAttributes(req, res, params, tag, sessionId) {
  try {
    const env = params.mwInstance || "qa4";
    const accountNumber = params?.accountNumber;
    const brand = params?.brand;
    const ani = params?.ani || "";

    logger.logWebhookRequest(sessionId, tag, { accountNumber, brand });

    const d = await executeApiCall({
      params, tag, env, sessionId, ani,
      urlResolver: (baseUrl) => baseUrl.replace("${accountNumber}", accountNumber).replace("${brand}", brand)
    });

    const attributes = d?.contactAuthenticationAttributes || [];
    const validRoles = ['BusinessAuthorizedLevel1', 'AuthorizedLevel2', 'AuthorizedLevel3'];
    const hasPinInd = Array.isArray(attributes) && attributes.length > 0 && attributes.some(contact => validRoles.includes(contact.contactRole) && contact.hasPinInd === true);

    const sessionParams = {
      returnCode: "0",
      hasPinInd: hasPinInd,
      authAttributesPayload: d
    };

    return sendWebhookResponse(res, sessionId, tag, sessionParams);
  } catch (error) {
    return sendErrorResponse(res, sessionId, tag, error, 1, { returnCode: "1" });
  }
}
