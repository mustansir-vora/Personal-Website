import * as functions from "@google-cloud/functions-framework";


import { calculateTermination } from "./handlers/calculateTermination.js";
import { getTerminationAction } from "./handlers/getTerminationAction.js";
import { getSmsParameters } from "./handlers/getSmsParameters.js";
import { sendSmsMessage } from "./handlers/sendSmsMessage.js";
import { saveCallHistory } from "./handlers/saveCallHistory.js";
import { deleteSession } from "./handlers/deleteSession.js";

import { executeHandler } from "@roger/r4b-common-nodemodules";

const handlerRegistry = {
    calculateTermination,
    getTerminationAction,
    getSmsParameters,
    sendSmsMessage,
    saveCallHistory,
    deleteSession
};

functions.http("R4B", async (req, res) => {
    //call Start Function
    return executeHandler(req, res, handlerRegistry);
}); 