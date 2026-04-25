import { cnxApiConfig } from "@roger/r4b-common-nodemodules";

export async function handleCreateUpdatePTP(req, res) {
	const tag = "createUpdatePTP";
	const apiConfigName = "accountselfserve";

	await cnxApiConfig.makeApiCall({
		req,
		res,
		apiConfig: apiConfigName,
		tag,
		buildRequest: (params) => {
			const { eligibilityRefId } = params.api.billingProfile?.ptpDetails?.eligibilityDetails || {};
			const { ptpSeqNumber } = params.api.billingProfile?.ptpDetails?.ptpInfo || {};
			const apiEnvironmentPath = (params.mwInstance || "") + "/";

			const {
				numberOfInstalls,
				promiseAmount,
				promiseDate,
				promiseMethod,
				firstPromiseAmount,
				firstPromiseDate,
				secondPromiseAmount,
				secondPromiseDate,
				dateEstimatedPosting,
				accountNumber,
				ptpOperation
			} = params;

			if (!accountNumber) {
				throw new Error("Missing accountNumber");
			}

			const ptpInfo = { eligibilityRefId, ptpSeqNumber };

			if (numberOfInstalls === 1) {
				ptpInfo.ptpInstallment1 = {
					amount: promiseAmount,
					datePromised: promiseDate,
					paymentMethod: promiseMethod,
					dateEstimatedPosting
				};
			} else if (numberOfInstalls === 2) {
				ptpInfo.ptpInstallment1 = {
					amount: firstPromiseAmount,
					datePromised: firstPromiseDate,
					paymentMethod: promiseMethod,
					dateEstimatedPosting
				};
				ptpInfo.ptpInstallment2 = {
					amount: secondPromiseAmount,
					datePromised: secondPromiseDate,
					paymentMethod: promiseMethod,
					dateEstimatedPosting
				};
			}

			return {
				method: "PUT",
				url: cnxApiConfig.getApiUrl(apiConfigName, tag, apiEnvironmentPath, {
					INPUT_ACCOUNTNUMBER: accountNumber
				}),
				body: { ptpOperation, ptpInfo }
			};
		},
		onSuccess: (params, payload) => ({
			...params,
			api: {
				...params.api,
				response: {
					...params.api?.response,
					selfServiceAttempted: true,
					attemptedSSName: params.numberOfInstalls === 1 ? "passive-singleInstallmentPtp" : "passive-multipleInstallmentPtp",
					ptp: {
						...params.api?.response?.ptp,
						createUpdatePTP: { success: true, response: payload }
					}
				}
			}
		}),
		onFailure: (params, payload) => ({
			...params,
			api: {
				...params.api,
				response: {
					...params.api?.response,
					selfServiceAttempted: true,
					attemptedSSName: params.numberOfInstalls === 1 ? "passive-singleInstallmentPtp" : "passive-multipleInstallmentPtp",
					ptp: {
						...params.api?.response?.ptp,
						createUpdatePTP: {
							success: false,
							error: payload || "systemError"
						}
					}
				}
			}
		})
	});
}
