import { http } from "@google-cloud/functions-framework";
import { executeHandler } from "@roger/r4b-common-nodemodules";

import { callendwrapup } from "./handlers/callendwrapup.js";

/**
 * Maintains active handler mappings for Wrap-Up execution.
 * Includes a fallback tag specifically for catching periodic internal schedulers.
 */
const wrapupRegistry = {
  "callendwrapup": callendwrapup,
  "Unknown-Tag": callendwrapup
};

/**
 * Primary HTTP trigger for the Call End Wrap-Up Cloud Function.
 * Expected to be invoked periodically (e.g., via Cloud Scheduler).
 */
http("R4B", async (webhookRequest, webhookResponse) => {
  return executeHandler(webhookRequest, webhookResponse, wrapupRegistry);
}); 