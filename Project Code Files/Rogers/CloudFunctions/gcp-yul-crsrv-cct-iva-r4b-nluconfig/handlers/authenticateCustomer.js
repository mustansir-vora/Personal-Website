import { logger, sendWebhookResponse, sendErrorResponse } from '@roger/r4b-common-nodemodules';
import { executeApiCall } from '../services/executeApiCall.js';

function evaluateAuthMatch(authenticatedContacts, authRoletoMatch, authLevel) {
  let authRoleMatch = false;
  let bestApiLevel = null;
  let authLevelAchieved = null;

  // 1. check for Authorized Levels
  const apiLevels = authenticatedContacts
    .map(contact => contact.contactRole)
    .filter(role => role.startsWith("BusinessAuthorizedLevel") || role.startsWith("AuthorizedLevel"))
    .map(role => parseInt(role.replace("BusinessAuthorizedLevel", "").replace("AuthorizedLevel", ""), 10))
    .filter(num => !isNaN(num));

  if (apiLevels.length > 0) {
    bestApiLevel = Math.max(...apiLevels);
    // Required level matches what we set from authLevel
    const requiredLevel = parseInt(authRoletoMatch.replace("BusinessAuthorizedLevel", "").replace("AuthorizedLevel", ""), 10);

    if (!isNaN(requiredLevel) && bestApiLevel <= requiredLevel) {
      authRoleMatch = true;
    }
  }
  if (authRoleMatch) {
    authLevelAchieved = authLevel;
  }
  return { authRoleMatch, authLevelAchieved };
}

export async function authenticateCustomer(req, res, params, tag, sessionId) {
  try {
    const env = params.mwInstance || "qa4";
    const accountNumber = params?.accountNumber;
    const maestroInd = params?.maestroAccountInd;
    const lob = params?.lob;
    const brand = params?.brand;
    const pin = params?.pin || "";
    const maskedPin = "*".repeat(pin.length);
    const authLevel = params?.nluConfig?.authLevel;
    let authRoletoMatch = "";
    let authRoleMatch = false;
    const ani = params?.ani || "";
    let authLevelAchieved = null;

    if (authLevel === "high") {
      authRoletoMatch = "BusinessAuthorizedLevel1";
    } else if (authLevel === "low") {
      authRoletoMatch = "AuthorizedLevel3";
    }

    logger.logWebhookRequest(sessionId, tag, { accountNumber, maestroInd, lob, brand, pin: maskedPin, authLevel, authRoletoMatch });

    const requestBody = {
      brand: brand,
      maestroInd: maestroInd,
      lob: lob,
      contactQualifiers: {
        contactRoles: [
          "BusinessAuthorizedLevel1",
          "AuthorizedLevel2",
          "AuthorizedLevel3",
        ]
      },
      authenticationItems: {
        pin: pin,
      }
    };

    const d = await executeApiCall({
      params, tag, env, sessionId, ani, method: "POST", data: requestBody,
      urlResolver: (baseUrl) => baseUrl.replace("${accountNumber}", accountNumber)
    });

    const authenticationMatchFoundInd = d?.authenticationResult?.authenticationMatchFoundInd || false;

    if (authenticationMatchFoundInd) {
      const authenticatedContacts = d?.authenticationResult?.authenticatedContacts || [];
      const evaluation = evaluateAuthMatch(authenticatedContacts, authRoletoMatch, authLevel);
      authRoleMatch = evaluation.authRoleMatch;
      authLevelAchieved = evaluation.authLevelAchieved;
    }

    const sessionParams = {
      returnCode: 0, // In user's script, this isn't quoted
      authenticationMatchFoundInd: !!authenticationMatchFoundInd,
      authRoleMatch: authRoleMatch,
      authLevelAchieved: authLevelAchieved,
      authRoleNeeded: authRoletoMatch,
      authenticatePayload: d,
    };

    return sendWebhookResponse(res, sessionId, tag, sessionParams);
  } catch (error) {
    return sendErrorResponse(res, sessionId, tag, error, 1, { returnCode: "1" });
  }
}
