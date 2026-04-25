import { logger, sendWebhookResponse, sendErrorResponse } from "@roger/r4b-common-nodemodules";
import { executeApiCall } from "../services/srvn-executeApiCall.js";

const OS_PRODUCT_MAP = {
  "TechInternetSupport": "Internet",
  "TechTvSupport": "Cable TV",
  "TechBusinessPhone": "Home Phone"
};

const CATEGORY_PRODUCT_MAP = {
  "TV": "Cable TV",
  "Business Phone": "Home Phone",
  "Mobile": "Wireless",
  "Advantage Voice": "Wireless",
  "Internet": "Internet"
};

function determineProductLine(invokedOSFrom, serviceCategoryMix) {
  if (OS_PRODUCT_MAP[invokedOSFrom]) return OS_PRODUCT_MAP[invokedOSFrom];
  if (serviceCategoryMix && serviceCategoryMix.length > 0) {
    return serviceCategoryMix.length === 1 
      ? (CATEGORY_PRODUCT_MAP[serviceCategoryMix[0]] || "Wireless") 
      : "MULTI";
  }
  return "Wireless";
}

export async function createInteraction(req, res, params, tag, sessionId) {
  try {
    const env = params.mwInstance || "qa4";
    const ani = params?.ani || "Unknown-ANI";
    const accountNumber = params?.accountNumber;
    const brand = params?.brand || "ROGERS";
    const contacId = null;
    const ecid = null;
    const connectionId = params?.conversationId;
    const directionCode = "Inbound";
    const notes = params?.interactionNotes;
    const mediumDescription = "Phone";
    const skillSet = "0888Def";
    const authenticated = params?.authenticated;

    let authenticationFlag = 0;
    if (authenticated) {
      authenticationFlag = 1;
    }

    const authenticationCounter = params?.authenticationCounter;
    const subscriptionNumber = null;
    const productLine = determineProductLine(params?.invokedOSFrom || "", params?.serviceCategoryMixEnList);

    const transactionLabel = params?.interactionLabel;
    const result = "Completed Request";

    const loggingPayload = {
      accountNumber, brand, contacId, ecid, connectionId,
      directionCode, notes, mediumDescription, skillSet,
      authenticationFlag, authenticationCounter, subscriptionNumber,
      productLine, transactionLabel, result
    };

    logger.logWebhookRequest(sessionId, tag, loggingPayload);

    const requestBody = {
      brand,
      contacId,
      ecid,
      connectionId,
      directionCode,
      notes,
      mediumDescription,
      skillSet,
      authenticationFlag,
      authenticationCounter,
      topic: {
        subscriptionNumber,
        productLine,
        transactionLabel,
        result
      }
    };

    const d = await executeApiCall({
      params, tag, env, sessionId, ani,
      method: "POST", data: requestBody,
      urlResolver: (baseUrl) => baseUrl.replace("${accountNumber}", accountNumber),
      expectedStatus: 201
    });

    return sendWebhookResponse(res, sessionId, tag, {
      returnCode: "0",
      interactionId: d?.interactionId
    });
  } catch (error) {
    return sendErrorResponse(res, sessionId, tag, error, 1, { returnCode: "1" });
  }
}
