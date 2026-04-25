import { cnxApiConfig } from "@roger/r4b-common-nodemodules";

export async function handleValidateExpiration(req, res) {
	const originalParams = req.body.sessionInfo?.parameters || {};
	const params = structuredClone(originalParams);
	const sessionId = params.conversationId || params.sessionId || "Unknown-Session";
	const tag = "validateCCExpirationDate";

	const rawValue = params.ccExpirationDate || params.ccexpirationdate;
	const expirationDate = rawValue ? String(rawValue).padStart(4, '0') : "";

	let expirationError = null;
	if (!expirationDate || !/^\d{4}$/.test(expirationDate)) {
		expirationError = "invalidFormat";
	} else {
		const month = parseInt(expirationDate.substring(0, 2), 10);
		const year = parseInt(expirationDate.substring(2, 4), 10);

		const now = new Date();
		const currentTwoDigitYear = now.getFullYear() % 100;
		const thisMonth = now.getMonth() + 1;

		if (month < 1 || month > 12) {
			expirationError = "invalidMonth";
		} else if (year < currentTwoDigitYear || year > (currentTwoDigitYear + 10)) {
			expirationError = "invalidYearRange";
		} else if (year === currentTwoDigitYear && month < thisMonth) {
			expirationError = "expired";
		}
	}

	params.api = {
		...params.api,
		checkCCExpirationDate: expirationError
			? { valid: false, error: expirationError }
			: { valid: true, ccExpirationDate: expirationDate }
	};

	return cnxApiConfig.sendWebhookResponse(res, sessionId, tag, { ...params });
}
