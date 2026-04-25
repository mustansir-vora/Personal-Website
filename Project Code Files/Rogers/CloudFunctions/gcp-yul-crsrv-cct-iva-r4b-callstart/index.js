import * as functions from "@google-cloud/functions-framework";
import { executeHandler } from "@roger/r4b-common-nodemodules";

import { getCallStartParams } from "./handlers/getCallStartParams.js";
import { getAniIdentification } from "./handlers/getAniIdentification.js";
import { getAniIdentificationBySessionId } from "./handlers/getAniIdentificationBySessionId.js";
import { getBroadcastMessage } from "./handlers/getBroadcastMessage.js";
import { getBillingProfile } from "./handlers/getBillingProfile.js";
import { getPredictiveTreatment } from "./handlers/getPredictiveTreatment.js";




const handlerRegistry = {
  getCallStartParams,
  getAniIdentification,
  getAniIdentificationBySessionId,
  getBroadcastMessage,
  getBillingProfile,
  getPredictiveTreatment
};


functions.http("R4B", async (req, res) => {
  //call End Functions
  return executeHandler(req, res, handlerRegistry);
}); 