import { cnxApiConfig } from "@roger/r4b-common-nodemodules";
import { isValidCardNumber } from "../services/cnx-ccHelpers.js";

export async function handleValidateCardNumber(req, res) {
	const originalParams = req.body.sessionInfo?.parameters || {};
	const params = structuredClone(originalParams);
	const sessionId = params.conversationId || params.sessionId || "Unknown-Session";
	const tag = "CC_ENTRY_VALIDATE_CARD_NUMBER";

	const cardNumber =
		params.creditCardNumber ?? params.cardNumber ?? params.cardToken;

	let newParams;

	if (!cardNumber) {
		newParams = { ...params, cardNumber: "invalidFormat" };
	} else if (!isValidCardNumber(cardNumber)) {
		newParams = { ...params, cardNumber: "invalid" };
	} else {
		newParams = {
			...params,
			api: {
				...params.api,
				ccValidation: {
					...params.api?.ccValidation,
					cardNumber: { valid: true }
				}
			}
		};
	}

	cnxApiConfig.sendWebhookResponse(res, sessionId, tag, newParams);
}
