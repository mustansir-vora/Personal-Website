import { http } from "@google-cloud/functions-framework";
import { executeHandler, cnxApiConfig, logger } from "@roger/r4b-common-nodemodules";

import { getServiceAddress } from "./handlers/srvn-getServiceAddress.js";
import { getOutages } from "./handlers/srvn-getOutages.js";
import { createOutageNotification } from "./handlers/srvn-createOutageNotification.js";
import { getFAQws } from "./handlers/srvn-getFAQws.js";
import { createInteraction } from "./handlers/srvn-createInteraction.js";

// Integrated connex handlers
import { handleSemafoneInitialize } from "./handlers/cnx-semafoneInitialize.js";
import { handleInitSecureData } from "./handlers/cnx-initSecureData.js";
import { handleReadSecureData } from "./handlers/cnx-readSecureData.js";
import { handleCreateToken } from "./handlers/cnx-createToken.js";
import { handleLogout } from "./handlers/cnx-logout.js";
import { handleValidateExpiration } from "./handlers/cnx-validateCCExpirationDate.js";
import { handleValidateCardNumber } from "./handlers/cnx-validateCardNumber.js";
import { handleValidateCVV } from "./handlers/cnx-validateCVV.js";
import { handleProcessCreditCardPayment } from "./handlers/cnx-makeCreditCardPayment.js";
import { handleCreateUpdatePTP } from "./handlers/cnx-createUpdatePTP.js";
import { handleGetPTPBillingProfile } from "./handlers/cnx-getPTPBillingProfile.js";
import { getPaymentFromHistory } from "./handlers/cnx-getPaymentObject.js";
import { paymentHistory } from "./handlers/cnx-paymentHistory.js";
import { billingProfile } from "./handlers/cnx-billingProfile.js";

/**
 * Registry mapping Dialogflow fulfillment tags to their respective handler functions.
 * Keeping this distinct per module ensures clean modularity.
 */
const selfServHandlers = {
  getServiceAddress,
  getOutages,
  createOutageNotification,
  getFAQws,
  createInteraction,

  // Integrated connex routing
  semafoneInitialize: handleSemafoneInitialize,
  readSecureData: handleReadSecureData,
  createToken: handleCreateToken,
  logout: handleLogout,
  initSecureData: handleInitSecureData,
  validateCCExpirationDate: handleValidateExpiration,
  validateCardNumber: handleValidateCardNumber,
  validateCVV: handleValidateCVV,
  makeCreditCardPayment: handleProcessCreditCardPayment,
  createUpdatePTP: handleCreateUpdatePTP,
  getPTPBillingProfile: handleGetPTPBillingProfile,
  getPaymentObject: getPaymentFromHistory,
  paymentHistory,
  billingProfile
};

const cnxHandlers = {
  semafoneInitialize: handleSemafoneInitialize,
  readSecureData: handleReadSecureData,
  createToken: handleCreateToken,
  logout: handleLogout,
  initSecureData: handleInitSecureData,
  validateCCExpirationDate: handleValidateExpiration,
  validateCardNumber: handleValidateCardNumber,
  validateCVV: handleValidateCVV,
  makeCreditCardPayment: handleProcessCreditCardPayment,
  createUpdatePTP: handleCreateUpdatePTP,
  getPTPBillingProfile: handleGetPTPBillingProfile,
  getPaymentObject: getPaymentFromHistory,
  paymentHistory,
  billingProfile
}

/**
 * Main HTTP trigger for the Self-Serve Cloud Function.
 * Accepts the Webhook request and delegates sequence routing.
 */
http("R4B", async (request, response) => {
  const tag = request.body?.fulfillmentInfo?.tag;

  if (cnxHandlers[tag]) {
    const sessionId = request.body?.sessionInfo?.parameters?.conversationId || "Unknown-Session";
    const res = response;
    const apiConfig = "accountselfserve";
    const tagHandler = cnxHandlers[tag];

    const apiConfigResponse = await cnxApiConfig.loadConfig({ sessionId, cacheKey: apiConfig });

    if (apiConfigResponse.Status === 200) {
      logger.logWebhookDetails(sessionId, tag);

      if (!tagHandler) {
        return cnxApiConfig.invalidTag(res, sessionId, tag);
      }
    }
    else {
      return cnxApiConfig.sendErrorResponse(res, sessionId, tag, "api config not loaded for : " + apiConfig);
    }
  }

  return executeHandler(request, response, selfServHandlers);
}); 