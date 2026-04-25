import { cnxApiConfig } from "@roger/r4b-common-nodemodules";
import { mergeConsumerSemafone, semafoneFailureParam } from "../services/cnx-semafoneHelpers.js";

export async function handleCreateToken(req, res) {
	const tag = "createToken";
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
				semafoneCR: params.semafoneCR ?? "",
				cardScheme:
					params.api?.response?.consumerSemafone?.readSecureData?.cardScheme ?? ""
			})
		}),
		onSuccess: (params, payload) => ({
			...params,
			...mergeConsumerSemafone(params, {
				createToken: { token: payload?.token }
			})
		}),
		onFailure: semafoneFailureParam("createToken")
	});
}
