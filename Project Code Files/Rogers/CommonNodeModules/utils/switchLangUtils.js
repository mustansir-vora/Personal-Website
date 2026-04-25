import * as logger from "./logger.js";
import { getIvaConfigs } from "../config/ivaConfig.js";
import { parseGenesysParams } from "./parseUtils.js";

export async function resolveLanguageSwitch(params, sessionId, tag) {

    if (!params.isSwitchInvoked) {
        return { params, sessionId };
    }
    // previous param
    const previousParams = parseGenesysParams(params.profileInfo)

    const oldSessionId =
        previousParams.sessionId || "SwitchLanguage";

    logger.logConsole(oldSessionId, tag, {
        message: "Language switch detected",
        rawProfileInfo: previousParams,
        type: typeof previousParams
    });


    const ivaResultConfig = await getIvaConfigs({
        sessionId: oldSessionId,
        tag
    });

    logger.logConsole(oldSessionId, tag, { ...ivaResultConfig.ResponsePayload })

    const updatedParams = {
        ...previousParams,
        ...ivaResultConfig.ResponsePayload,
        broadCastId: ivaResultConfig.ResponsePayload.broadCastId1
    };

    logger.logConsole(
        oldSessionId,
        tag,
        {
            message: "Language switch detected. Loaded IVA configs with session params.",
            updatedParams
        }
    );

    return {
        params: updatedParams,
        sessionId: oldSessionId
    };
}