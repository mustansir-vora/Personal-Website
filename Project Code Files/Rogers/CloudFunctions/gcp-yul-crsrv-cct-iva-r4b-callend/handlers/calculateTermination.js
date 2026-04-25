import {
    apiClient,
    logger,
    parseJson,
    getRequestHeaders,
    sendWebhookResponse,
    sendErrorResponse
} from "@roger/r4b-common-nodemodules";


export async function calculateTermination(req, res, params, tag, sessionId) {

    let sessionParams = {};

    try {

        const env = params.mwInstance || "qa4";
        const brand = params.brand;
        const sessionIds = params.accountSessionList || [];
        const sessionIdInContext = params.sessionIdInContext || "";

        logger.logWebhookRequest(sessionId, tag, {
            env, brand, sessionIds, sessionIdInContext
        });

        const apiUrl = params[tag][env];

        const { headers, tokenConfig } =
            getRequestHeaders(sessionId, params.ani, params, env);

        const requestBody = {
            brand,
            sessionIds,
            sessionIdInContext
        };

        const apiResult = await apiClient.postRequest({
            sessionId,
            tag,
            url: apiUrl,
            headers,
            data: requestBody,
            tokenConfig
        });

        const { Status, ResponsePayload } = apiResult;

        if (Status === 200) {

            const flags = ResponsePayload.predictiveRoutingFlags || [];

            const preRouteOverrideRuleId = parseJson(
                flags.find(f => f.routingFlagCategory === "PREROUTEOVERRIDE")
                    ?.routingFlagsList?.[0]?.predictiveRuleId
            );

            const delinquencyRuleId = parseJson(
                flags.find(f => f.routingFlagCategory === "DELINQUENCY")
                    ?.routingFlagsList?.[0]?.predictiveRuleId
            );

            const featureRuleId = parseJson(
                flags.find(f => f.routingFlagCategory === "FEATURE")
                    ?.routingFlagsList?.[0]?.predictiveRuleId
            );

            sessionParams = {
                predictiveRoutingFlagsFound: parseJson(ResponsePayload.predictiveRoutingFlagsFound),
                preRouteOverrideRuleId,
                delinquencyRuleId,
                featureRuleId,
                reportingVar_feature: featureRuleId,
                returnCode: "0"
            };

        } else {

            sessionParams = {
                returnCode: "1",
                predictiveRoutingFlagsFound: parseJson(ResponsePayload.predictiveRoutingFlagsFound)
            };

        }

        return sendWebhookResponse(res, sessionId, tag, sessionParams);

    } catch (err) {
        return sendErrorResponse(res, sessionId, tag, err, 1);
    }

}