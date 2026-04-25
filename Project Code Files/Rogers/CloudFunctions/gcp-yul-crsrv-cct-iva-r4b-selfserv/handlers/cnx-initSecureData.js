import { cnxApiConfig } from "@roger/r4b-common-nodemodules";
import { mergeConsumerSemafone, semafoneFailureParam, semafoneSessionQueryParams } from "../services/cnx-semafoneHelpers.js";

export async function handleInitSecureData(req, res) {
	const tag = "initSecureData";
	const params = req.body.sessionInfo?.parameters || {};
	const apiEnvironmentPath = (params.mwInstance || "") + "/";
	const apiConfigName = "accountselfserve";

	await cnxApiConfig.makeApiCall({
		req,
		res,
		apiConfig: apiConfigName,
		tag,
		buildRequest: (params) => {
			const semafoneQueryParams = semafoneSessionQueryParams(params);
			return {
				method: "POST",
				url: cnxApiConfig.getApiUrl(apiConfigName, tag, apiEnvironmentPath, {}, semafoneQueryParams),
				body: null
			};
		},
		onSuccess: (params) => ({
			...params,
			...mergeConsumerSemafone(params, { initSecureData: { success: true } })
		}),
		onFailure: semafoneFailureParam("initSecureData")
	});
}
