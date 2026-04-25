import { cnxApiConfig } from "@roger/r4b-common-nodemodules";

export async function billingProfile(req, res) {
	const tag = req.body?.fulfillmentInfo?.tag;
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
			const sessionIdInContext = params.app?.active?.sessionInContext || "ffc98fe8-3b2e-40e8-9670-e8e5179fb44c";
			const billingAccountIndex = params.app?.active?.billingAccountIndex;
			const account = params.api?.identifyBillingAccounts?.billingAccounts?.[billingAccountIndex];
			const accountNumber = account?.accountNumber || 884330986;
			const refreshGetBillingProfileInfoFlag = params.refreshGetBillingProfileInfo;
			const accountBrand = params.brand;
			const lob = account?.lob || '';
			const maestroInd = account?.maestroInd || false;
			const fetchIPPFromBackendInd = true;
			let sessionId;

			if (!refreshGetBillingProfileInfoFlag) {
				sessionId = sessionIdInContext;
			}
			const pathParams = {};
			const urlParams = {
				"maestroInd": maestroInd,
				"lob": lob,
				"accountBrand": accountBrand,
				"fetchIPPFromBackendInd": fetchIPPFromBackendInd,
				"sessionId": sessionId,
				"accountNumber": accountNumber
			};
			return {
				method: "GET",
				url: cnxApiConfig.getApiUrl(apiConfig, tag, apiEnvironmentPath, pathParams, urlParams)
			};

		},
		onSuccess: (params, payload) => {
			let ptpInstallments = [];
			if (payload.billingProfile?.ptpDetails?.ptpInfo) {
				ptpInstallments = payload.billingProfile.ptpDetails.ptpInfo.ptpInstallments || [];
			}
			payload.billingProfile.pendingPTP = false;
			for (const installment of ptpInstallments) {
				const currentDate = new Date();
				const datePromised = new Date(installment.datePromised);
				if (installment.installmentStatus === "P" && datePromised < currentDate) {
					payload.billingProfile.ptpAmount = installment.amount;
					payload.billingProfile.ptpPaymentDate = installment.datePromised;
					payload.billingProfile.ptpPaymentMethod = installment.paymentMethod;
					payload.billingProfile.pendingPTP = true;
					payload.billingProfile.futuredatedPTP = false;
					break;
				}
				if (installment.installmentStatus === "P" && datePromised > currentDate) {
					payload.billingProfile.ptpAmount = installment.amount;
					payload.billingProfile.ptpPaymentDate = installment.datePromised;
					payload.billingProfile.ptpPaymentMethod = installment.paymentMethod;
					payload.billingProfile.pendingPTP = true;
					payload.billingProfile.futuredatedPTP = true;
					break;
				}
			}

			let playAccountBalanceDates = {};
			if (payload.billingProfile?.billingPaymentDetails?.billingCycle?.billCloseDate) {
				let datesplit = payload.billingProfile.billingPaymentDetails.billingCycle.billCloseDate.split("-");
				let playbillCloseDate = datesplit[1] + "/" + datesplit[2];
				playAccountBalanceDates.playbillCloseDate = playbillCloseDate;
			}
			if (payload.billingProfile?.lastPaymentDetails?.lastBillingDate) {
				let datesplit = payload.billingProfile.lastPaymentDetails.lastBillingDate.split("-");
				let playlastBillingDate = datesplit[1] + "/" + datesplit[2];
				playAccountBalanceDates.playlastBillingDate = playlastBillingDate;
			}
			if (payload.billingProfile?.ptpPaymentDate) {
				let datesplit = payload.billingProfile.ptpPaymentDate.split("-");
				let playptpPaymentDate = datesplit[1] + "/" + datesplit[2];
				playAccountBalanceDates.playptpPaymentDate = playptpPaymentDate;
			}
			payload.billingProfile.playAccountBalanceDates = playAccountBalanceDates;

			return {
				api: {
					...params.api,
					billingProfile: {
						...payload.billingProfile,
						returnCode: 0
					}
				}
			};
		},
		onFailure: (params, payload) => {
			return {
				api: {
					...params.api,
					billingProfile: {
						...(params.api?.billingProfile || {}),
						returnCode: 1
					}
				}
			};
		}
	});
}
