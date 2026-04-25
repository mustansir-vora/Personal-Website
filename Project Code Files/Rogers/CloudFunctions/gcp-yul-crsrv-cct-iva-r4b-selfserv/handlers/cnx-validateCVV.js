import { cnxApiConfig } from "@roger/r4b-common-nodemodules";

export async function handleValidateCVV(req, res) {
	const originalParams = req.body.sessionInfo?.parameters || {};
	const params = structuredClone(originalParams);
	const sessionId = params.conversationId || params.sessionId || "Unknown-Session";
	const tag = "CC_ENTRY_VALIDATE_CVV";

	const rawThree = params.cvvThreeDigit || params.cvvthreedigit;
	const rawFour = params.cvvFourDigit || params.cvvfourdigit;

	const cardScheme = params.api?.response?.consumerSemafone?.readSecureData?.cardScheme;
	const isAmex = cardScheme === "AMEX";

	let cvvValue = "";
	let cvvError = null;

	if (rawFour !== undefined && rawFour !== null) {
		cvvValue = String(rawFour).padStart(4, '0');
	} else if (rawThree !== undefined && rawThree !== null) {
		cvvValue = String(rawThree).padStart(3, '0');
	}

	console.log("INCOMING CVV:", { rawThree, rawFour }, "PROCESSED:", cvvValue);

	if (!cvvValue || !/^\d+$/.test(cvvValue)) {
		cvvError = "invalidFormat";
	} else {
		const len = cvvValue.length;
		if (isAmex && len !== 4) {
			cvvError = "invalidLengthAmex";
		} else if (!isAmex && len !== 3) {
			cvvError = "invalidLength";
		}
	}

	params.api = {
		...params.api,
		checkCVV: cvvError ? { valid: false, error: cvvError } : { valid: true, cvv: cvvValue }
	};

	return cnxApiConfig.sendWebhookResponse(res, sessionId, tag, { ...params });
}
