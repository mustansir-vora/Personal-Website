import { cnxApiConfig } from "@roger/r4b-common-nodemodules";

export async function getPaymentFromHistory(req, res) {
	try {
		// 1. Log the incoming request to help troubleshooting
		const originalParams = req.body.sessionInfo?.parameters || {};
		const params = structuredClone(originalParams);
		const sessionId = params.conversationId || "Unknown-Session";

		const body = req.body;
		const sessionParams = body.sessionInfo?.parameters || {};
		const tag = body.fulfillmentInfo?.tag || "";

		let pastPaymentObjectparams = {};

		// 2. Logic for extracting the payment object
		const pastPayments = sessionParams.api?.paymentHistory?.pastPayments;

		// "Zero-Safe" check: This handles the index 0 correctly
		const indexValue = sessionParams.paymentIndex;
		const index = (indexValue !== undefined && indexValue !== null) ? parseInt(indexValue) : null;

		if (Array.isArray(pastPayments) && index !== null) {
			if (index >= 0 && index < pastPayments.length) {
				const selectedPayment = pastPayments[index];
				selectedPayment.paymentDate = selectedPayment.paymentDate.split('T')[0];
				selectedPayment.depositDate = selectedPayment.depositDate.split('T')[0];

				pastPaymentObjectparams["pastPaymentObject"] = selectedPayment;
			}
		}

		const newParams = {
			api: {
				...params.api,
				pastPaymentObject: {
					...pastPaymentObjectparams.pastPaymentObject
				}
			}
		};

		cnxApiConfig.sendWebhookResponse(res, sessionId, tag, newParams);

	} catch (err) {
		const originalParams = req.body.sessionInfo?.parameters || {};
		const sessionId = originalParams.conversationId || "Unknown-Session";
		const tag = req.body.fulfillmentInfo?.tag || "";
		cnxApiConfig.sendErrorResponse(res, sessionId, tag, err);
	}
}
