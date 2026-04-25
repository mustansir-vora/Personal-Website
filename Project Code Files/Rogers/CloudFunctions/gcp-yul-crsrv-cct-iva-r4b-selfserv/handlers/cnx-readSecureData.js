import { cnxApiConfig } from "@roger/r4b-common-nodemodules";
import { mergeConsumerSemafone, semafoneFailureParam, semafoneSessionQueryParams } from "../services/cnx-semafoneHelpers.js";

export async function handleReadSecureData(req, res) {
	const tag = "readSecureData";
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
				method: "GET",
				url: cnxApiConfig.getApiUrl(apiConfigName, tag, apiEnvironmentPath, {}, semafoneQueryParams)
			};
		},
		onSuccess: (params, payload) => ({
			...params,
			...mergeConsumerSemafone(params, {
				readSecureData: {
					state: payload?.state,
					validationState: payload?.validationState,
					cardScheme: payload?.cardScheme,
					length: payload?.length,
					validResponse: true,
				}
			})
		}),
		onFailure: semafoneFailureParam("secureData")
	});
}
