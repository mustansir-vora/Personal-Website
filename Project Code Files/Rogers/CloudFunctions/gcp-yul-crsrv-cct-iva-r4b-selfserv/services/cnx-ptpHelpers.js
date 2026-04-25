export function getNestedValue(obj, path) {
	return path.split(".").reduce((current, key) => current?.[key], obj) ?? null;
}

export function formatDateISO(date) {
	try {
		const year = date.getFullYear();
		const month = String(date.getMonth() + 1).padStart(2, "0");
		const day = String(date.getDate()).padStart(2, "0");
		return `${year}-${month}-${day}`;
	} catch (err) {
		return null;
	}
}

export function addDaysToDate(baseDate, days) {
	const result = new Date(baseDate);
	try {
		result.setDate(result.getDate() + days);
		return formatDateISO(result);
	} catch (err) {
		return null;
	}
}

export function dateFromConfigKey(baseDate, config, key, { sign = 1, offset = 0 } = {}) {
	const val = config?.[key];
	if (val == null) return null;
	const days = Number(val) * sign + offset;
	return addDaysToDate(baseDate, days) || null;
}

export function computePTPDatesFromAppConfig(appConfig) {
	const currentDate = new Date();
	const cfg = appConfig || {};

	const pbSingleInstallmentDate = dateFromConfigKey(currentDate, cfg, "pbSingleInstallmentDays");
	const ccSingleInstallmentDate = dateFromConfigKey(currentDate, cfg, "ccSingleInstallmentDays");
	const pbInstallmentDate1 = dateFromConfigKey(currentDate, cfg, "pbInstallment1Days");
	const pbInstallmentDate2 = dateFromConfigKey(currentDate, cfg, "pbInstallment2Days");
	const ccInstallmentDate1 = dateFromConfigKey(currentDate, cfg, "ccInstallment1Days");
	const ccInstallmentDate2 = dateFromConfigKey(currentDate, cfg, "ccInstallment2Days");
	const pbCollectedDate = dateFromConfigKey(currentDate, cfg, "pbCollectedDays", { sign: -1 });
	const ccCollectedDate = dateFromConfigKey(currentDate, cfg, "ccCollectedDays", { sign: -1 });
	const pbDateEstimatedPosting = dateFromConfigKey(currentDate, cfg, "pbOffsetDays", { offset: 1 });
	const ccDateEstimatedPosting = dateFromConfigKey(currentDate, cfg, "ccOffsetDays", { offset: 2 });

	return {
		pbSingleInstallmentDate,
		ccSingleInstallmentDate,
		pbInstallmentDate1,
		pbInstallmentDate2,
		ccInstallmentDate1,
		ccInstallmentDate2,
		pbCollectedDate,
		ccCollectedDate,
		pbDateEstimatedPosting,
		ccDateEstimatedPosting,
		pbSingleInstallmentDays: cfg.pbSingleInstallmentDays,
		ccSingleInstallmentDays: cfg.ccSingleInstallmentDays,
		pbInstallment1Days: cfg.pbInstallment1Days,
		ccInstallment1Days: cfg.ccInstallment1Days,
		pbInstallment2Days: cfg.pbInstallment2Days,
		ccInstallment2Days: cfg.ccInstallment2Days
	};
}

export function buildPTPResponse(billingProfile, appConfig) {
	const { ptpDetails, ippDetails, balanceDetails } = billingProfile || {};
	const { ptpInfo, eligibilityDetails, ptpRecommendations } = ptpDetails || {};
	const { ptpInstallments = [] } = ptpInfo || {};

	const ptpResponseStructure = {
		ippStatus: ippDetails?.ippStatus || null,
		ptpEnrolledInd: ippDetails?.ptpEnrolledInd ?? null,
		ptpCreateEligibilityInd: eligibilityDetails?.ptpCreateEligibilityInd ?? null,
		ptpUpdateEligibilityInd: eligibilityDetails?.ptpUpdateEligibilityInd ?? null,
		ptpCategoryNFlow: eligibilityDetails?.ptpCategoryNFlow ?? null,
		totalInstallments: ptpInfo?.totalInstallments ?? null,
		currentBalance: balanceDetails?.currentBalance ?? null,
		pastDueBalance: balanceDetails?.pastDueBalance ?? null,
		paymentMethod: ptpInstallments[0]?.paymentMethod ?? null,
		datePromised: ptpInstallments[0]?.datePromised ?? null,
		amount: ptpInstallments[0]?.amount ?? null,
		datePromised1: ptpInstallments[0]?.datePromised ?? null,
		amount1: ptpInstallments[0]?.amount ?? null,
		datePromised2: ptpInstallments[1]?.datePromised ?? null,
		amount2: ptpInstallments[1]?.amount ?? null,
		installmentStatus1: ptpInstallments[0]?.installmentStatus ?? null,
		ptpCategory: ptpInfo?.ptpCategory ?? null,
		accountStatus: billingProfile?.accountStatus ?? null,
		recomInstallAmount1: ptpRecommendations?.[0]?.installmentAmounts?.[0] ?? null,
		recomInstallAmount2: ptpRecommendations?.[0]?.installmentAmounts?.[1] ?? null,
		systemOfRecord: billingProfile?.systemOfRecords ?? null,
	};

	const dateFields = computePTPDatesFromAppConfig(appConfig);
	return { ...ptpResponseStructure, ...dateFields };
}
