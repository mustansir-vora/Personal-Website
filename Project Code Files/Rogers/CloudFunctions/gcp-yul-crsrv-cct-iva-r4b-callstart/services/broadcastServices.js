import { parseJson } from "@roger/r4b-common-nodemodules";

//broadcast related util functions


export function buildFallbackSessionParams(
    reportingVar_messageID, reportingVar_messageType,
    isSwitchInvoked,
    resolvedParams = {}
) {
    const base = {
        reportingVar_messageID,
        reportingVar_messageType,
        isSwitchInvoked: false,
        enabled: false,
        returnCode: "1"
    };
    return isSwitchInvoked
        ? { ...resolvedParams, ...base }
        : base;
}

export function buildBroadcastSessionParams(
    ResponsePayload,
    reportingVar_messageID,
    reportingVar_messageType,
    resolvedParams = {}
) {
    const b = ResponsePayload.broadcast;
    const type = b?.questionOrMessage;

    reportingVar_messageType = reportingVar_messageType
        ? `${reportingVar_messageType},${parseJson(type)}`
        : parseJson(type);

    const sessionParams = {
        ...resolvedParams,
        isSwitchInvoked: false,
        enabled: parseJson(ResponsePayload.enabled),
        questionOrMessage: parseJson(type),
        primaryMessage: parseJson(b?.broadcastScript?.voiceScriptContent),
        nextAction: parseJson(b?.nextAction),
        broadCastIntent: parseJson(b?.intent),
        smsEnabled: parseJson(b?.smsEnabled),
        smsId: parseJson(b?.smsId),
        returnCode: "0",
        reportingVar_messageID,
        reportingVar_messageType
    };

    if (type === "question") {
        sessionParams.secondaryMessageRequired =
            parseJson(b?.secondaryMessageRequired);
        sessionParams.secondaryMessage =
            parseJson(b?.secondaryMessageScript?.voiceScriptContent);
    }

    return sessionParams;
}