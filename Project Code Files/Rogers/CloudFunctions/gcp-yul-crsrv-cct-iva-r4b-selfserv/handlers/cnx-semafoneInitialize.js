import { cnxApiConfig } from "@roger/r4b-common-nodemodules";
import { mergeConsumerSemafone, semafoneFailureParam } from "../services/cnx-semafoneHelpers.js";

export async function handleSemafoneInitialize(req, res) {
	const tag = "semafoneInitialize";
	const params = req.body.sessionInfo?.parameters || {};
	const apiEnvironmentPath = (params.mwInstance || "") + "/";
	const apiConfigName = "accountselfserve";

	await cnxApiConfig.makeApiCall({
		req,
		res,
		apiConfig: apiConfigName,
		tag,
		buildRequest: (params) => ({
			method: "GET",
			url: cnxApiConfig.getApiUrl(apiConfigName, tag, apiEnvironmentPath, {}, {
				semafoneCR: params.semafoneCR ?? ""
			})
		}),
		onSuccess: (params, payload) => ({
			...params,
			...mergeConsumerSemafone(params, {
				semafoneInitialized: true,
				semafoneSessionId: payload?.semafoneSessionId
			})
		}),
		onFailure: semafoneFailureParam("transferType")
	});
}
