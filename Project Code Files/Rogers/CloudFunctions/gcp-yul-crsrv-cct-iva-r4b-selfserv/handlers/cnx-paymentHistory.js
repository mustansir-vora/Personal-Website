import { cnxApiConfig } from "@roger/r4b-common-nodemodules";

export async function paymentHistory(req, res) {
	const tag = "paymentHistory";
	const originalParams = req.body.sessionInfo?.parameters || {};
	const params = structuredClone(originalParams);
	const apiEnvironmentPath = (params.mwInstance || "") + "/";
	const apiConfig = "accountselfserve";

	await cnxApiConfig.makeApiCall({
		req,
		res,
		apiConfig,
		tag,
		buildRequest: (params) => {
			const billingAccountIndex = params.app?.active?.billingAccountIndex;
			const account = params.api?.identifyBillingAccounts?.billingAccounts?.[billingAccountIndex];
			const accountNumber = account?.accountNumber || '507413555';

			const pathParams = {
				INPUT_ACCOUNTNUMBER: accountNumber
			};

			return {
				method: "GET",
				url: cnxApiConfig.getApiUrl(apiConfig, tag, apiEnvironmentPath, pathParams)
			};
		},
		onSuccess: (params, payload) => {
			return {
				api: {
					...params.api,
					paymentHistory: {
						...payload,
						returnCode: 0
					}
				}
			};
		},
		onFailure: (params, payload) => {
			return {
				api: {
					...params.api,
					paymentHistory: {
						...(params.api?.paymentHistory || {}),
						returnCode: 1
					}
				}
			};
		}
	});
}
