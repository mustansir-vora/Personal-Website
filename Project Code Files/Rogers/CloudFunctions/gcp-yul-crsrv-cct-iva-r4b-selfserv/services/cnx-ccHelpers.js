export function isValidCardNumber(cardNumber) {
	const digits = String(cardNumber).replace(/\D/g, "");
	if (digits.length < 13 || digits.length > 19) return false;

	let sum = 0;
	for (let i = digits.length - 1; i >= 0; i--) {
		let digit = parseInt(digits[i], 10);
		if ((digits.length - 1 - i) % 2 === 1) {
			digit *= 2;
			if (digit > 9) digit -= 9;
		}
		sum += digit;
	}
	return sum % 10 === 0;
}

export function isValidCVV(cvv, cardScheme) {
	if (cvv == null || typeof cvv !== "string") return false;
	const digits = String(cvv).replace(/\D/g, "");
	const scheme = (cardScheme || "").toUpperCase();
	const requiredLength = scheme === "AMEX" ? 4 : 3;
	return digits.length === requiredLength && /^\d+$/.test(digits);
}
