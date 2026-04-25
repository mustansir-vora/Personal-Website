import { http } from "@google-cloud/functions-framework";
import { executeHandler } from "@roger/r4b-common-nodemodules";

import { getIntentProperties } from "./handlers/getIntentProperties.js";
import { getAuthenticationAttributes } from "./handlers/getAuthenticationAttributes.js";
import { authenticateCustomer } from "./handlers/authenticateCustomer.js";

/**
 * Registry mapping Dialogflow fulfillment tags to their respective handler functions.
 */
const nluHandlers = {
  getIntentProperties,
  getAuthenticationAttributes,
  authenticateCustomer
};

/**
 * Main HTTP trigger entry point for the Dialogflow CX Webhook.
 * 
 * Routes incoming webhooks from Dialogflow to specific internal functions based on the 
 * fulfillment tag provided.
 */
http("R4B", async (request, response) => {
  return executeHandler(request, response, nluHandlers);
}); 