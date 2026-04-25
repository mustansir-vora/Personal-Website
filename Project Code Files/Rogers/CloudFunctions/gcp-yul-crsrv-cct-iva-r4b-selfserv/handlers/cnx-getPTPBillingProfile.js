import { cnxApiConfig } from "@roger/r4b-common-nodemodules";
import { buildPTPResponse } from "../services/cnx-ptpHelpers.js";

export async function handleGetPTPBillingProfile(req, res) {
	const originalParams = req.body.sessionInfo?.parameters || {};
	const params = structuredClone(originalParams);
	const sessionId = params.conversationId || params.sessionId || "Unknown-Session";
	const tag = "getPTPBillingProfile";

	const { billingProfile } = params.api || {};
	const appConfig = params || {};

	try {
		const ptpResponse = buildPTPResponse(billingProfile, appConfig);

		const existingApi = params.api || {};
		const existingResponse = existingApi.response || {};
		const existingPtp = existingResponse.ptp || {};

		const newApi = {
			...existingApi,
			response: {
				...existingResponse,
				ptp: {
					...existingPtp,
					...ptpResponse
				}
			}
		};

		cnxApiConfig.sendWebhookResponse(res, sessionId, tag, { ...params, api: newApi });

	} catch (err) {
		cnxApiConfig.sendErrorResponse(res, sessionId, tag, err, {
			...params,
			api: {
				...params.api,
				response: {
					...params.api?.response,
					ptp: {
						...params.api?.response?.ptp,
						error: err.message || "Unknown error"
					}
				}
			}
		});
	}
}
