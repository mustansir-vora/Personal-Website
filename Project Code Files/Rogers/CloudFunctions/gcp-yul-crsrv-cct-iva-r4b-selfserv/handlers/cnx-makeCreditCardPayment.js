import { cnxApiConfig } from "@roger/r4b-common-nodemodules";
import { isValidCVV } from "../services/cnx-ccHelpers.js";

export async function handleProcessCreditCardPayment(req, res) {
	const originalParams = req.body.sessionInfo?.parameters || {};
	const params = structuredClone(originalParams);
	const sessionId = params.conversationId || params.sessionId || "Unknown-Session";
	const tag = "makeCreditCardPayment";
	const apiConfigName = "accountselfserve";
	const apiEnvironmentPath = (params.mwInstance || "") + "/";

	const consumerSemafone = params.api?.response?.consumerSemafone;
	const cardScheme = consumerSemafone?.readSecureData?.cardScheme ?? "VISA";
	const securityCode = params.cvv ?? params.securityCode ?? "";

	if (securityCode && !isValidCVV(securityCode, cardScheme)) {
		cnxApiConfig.sendWebhookResponse(res, sessionId, tag, {
			...params,
			paymentConfirmation: "invalidCVV"
		});
		return;
	}

	await cnxApiConfig.makeApiCall({
		req,
		res,
		apiConfig: apiConfigName,
		tag,
		buildRequest: (innerParams) => {
			const { paymentAmount } = innerParams;
			const consolidatedAccountInd = innerParams.consolidatedAccountInd ?? false;

			const billingAccountIndex = innerParams.app?.active?.billingAccountIndex || 0;
			const account = innerParams.api?.identifyBillingAccounts?.billingAccounts?.[billingAccountIndex];
			const accountNumber = account?.accountNumber ?? innerParams.accountNumber ?? '';

			const innerConsumerSemafone = innerParams.api?.response?.consumerSemafone;
			const token = innerConsumerSemafone?.createToken?.token;
			const innerCardScheme = innerConsumerSemafone?.readSecureData?.cardScheme ?? "VISA";
			const expirationDate = innerParams.api?.checkCCExpirationDate?.ccExpirationDate ?? innerParams.ccExpirationDate ?? "";
			const expiryMonth = expirationDate ? expirationDate.substring(0, 2) : "";
			const expiryYear = expirationDate ? expirationDate.substring(2, 4) : "";
			const innerSecurityCode = innerParams.api?.checkCVV?.cvv ?? "";

			const creditCardInfo = {
				cardType: innerCardScheme,
				cardNumberToken: token,
				expiryMonth,
				expiryYear,
				securityCode: innerSecurityCode
			};

			const pathParams = {
				INPUT_ACCOUNTNUMBER: accountNumber,
				INPUT_BRAND: "ROGERS"
			};

			return {
				method: "POST",
				url: cnxApiConfig.getApiUrl(apiConfigName, tag, apiEnvironmentPath, pathParams),
				body: { paymentAmount, creditCardInfo, consolidatedAccountInd }
			};
		},
		onSuccess: (innerParams, payload) => ({
			...innerParams,
			api: {
				...innerParams.api,
				paymentManagement: {
					paymentConfirmation: {
						authorizationNumber: payload?.authorizationNumber,
						newBalance: payload?.newBalance,
						paymentCompleted: true,
					}
				}
			}
		}),
		onFailure: (innerParams, payload) => ({
			...innerParams,
			paymentConfirmation: payload || "systemError"
		})
	});
}
