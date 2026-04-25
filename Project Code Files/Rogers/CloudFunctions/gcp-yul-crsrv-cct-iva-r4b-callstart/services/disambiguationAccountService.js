import { parseJson } from "@roger/r4b-common-nodemodules";

import { DisambigPrompts } from "./disambiguationPromptService.js";

//disambiguation related util functions
export async function DisambigParams(accounts, billingAccounts, flowLanguage) {

    const sortedAccounts = sortAccounts(accounts);
    const accountData = buildAccountData(sortedAccounts);
    const promptData = DisambigPrompts(
        sortedAccounts,
        accountData,
        billingAccounts,
        flowLanguage
    );
    return buildSessionParams(
        accountData,
        promptData,
        billingAccounts
    );

}


function sortAccounts(accounts) {
    return accounts.sort((a, b) =>
        Number(b.businessInd === true) - Number(a.businessInd === true)
    );
}


function buildAccountData(sortedAccounts) {

    const accountNumberList = sortedAccounts.map(a => parseJson(a.accountNumber));
    const accountSessionList = sortedAccounts.map(a => parseJson(a.sessionId));
    const businessIndList = sortedAccounts.map(a => parseJson(a.businessInd));
    const maestroAccountIndList = sortedAccounts.map(a => parseJson(a.maestroAccountInd));
    const collectionSuspendedIndList = sortedAccounts.map(a => parseJson(a.collectionSuspendedInd));
    const accountClassificationList = sortedAccounts.map(a => parseJson(a.accountClassification));
    const lobList = sortedAccounts.map(a => parseJson(a.lob));
    const accountStatusList = sortedAccounts.map(a => parseJson(a.accountStatus));
    const cbpTypeList = sortedAccounts.map(a => parseJson(a.cbpType));
    const cbpSubTypeList = sortedAccounts.map(a => parseJson(a.cbpSubType));
    const billingSystemList = sortedAccounts.map(a => parseJson(a.billingSystem));
    const numberOfSubscriptionsList = sortedAccounts.map(a => parseJson(a.numberOfSubscriptions));

    const numberOfWirelessList = sortedAccounts.map(a => parseJson(a.subscriptionSummary?.numberOfWireless));
    const numberOfTVList = sortedAccounts.map(a => parseJson(a.subscriptionSummary?.numberOfTV));
    const numberOfInternetList = sortedAccounts.map(a => parseJson(a.subscriptionSummary?.numberOfInternet));
    const numberOfPhoneList = sortedAccounts.map(a => parseJson(a.subscriptionSummary?.numberOfPhone));
    const numberOfHomeSecurityList = sortedAccounts.map(a => parseJson(a.subscriptionSummary?.numberOfHomeSecurity));

    const productLabelsMixEnList = sortedAccounts.map(a =>
        parseJson(a.subscriptionSummary?.productLabelMix?.english)
    );

    const productLabelsMixFrList = sortedAccounts.map(a =>
        parseJson(a.subscriptionSummary?.productLabelMix?.french)
    );

    const serviceCategoryMixEnList = sortedAccounts.map(a =>
        parseJson(a.subscriptionSummary?.serviceCategoryMix?.english)
    );

    const serviceCategoryMixFrList = sortedAccounts.map(a =>
        parseJson(a.subscriptionSummary?.serviceCategoryMix?.french)
    );

    const routingSegmentList = sortedAccounts.map(a =>
        (a.routingSegments ?? []).map(r => parseJson(r.segment))
    );

    const profileSegmentList = sortedAccounts.map(a =>
        (a.profileSegments ?? []).map(p => parseJson(p.segment))
    );

    const routingCategoryAList = sortedAccounts.map(a => parseJson(a.businessSegments?.find(r => r.category === "A")?.segment));
    const routingCategoryBList = sortedAccounts.map(a => parseJson(a.businessSegments?.find(r => r.category === "B")?.segment));
    const routingCategoryCList = sortedAccounts.map(a => parseJson(a.businessSegments?.find(r => r.category === "C")?.segment));
    const routingCategoryDList = sortedAccounts.map(a => parseJson(a.businessSegments?.find(r => r.category === "D")?.segment));
    const routingCategoryEList = sortedAccounts.map(a => parseJson(a.businessSegments?.find(r => r.category === "E")?.segment));

    const masterSubscriptionsList = sortedAccounts.map(account => {
        const subs = account.subscriptions || [];
        subs.sort((a, b) =>
            (b?.serviceCategory === "Wireless") -
            (a?.serviceCategory === "Wireless")
        );
        return subs;
    });

    return {
        accountNumberList,
        accountSessionList,
        businessIndList,
        maestroAccountIndList,
        collectionSuspendedIndList,
        accountClassificationList,
        lobList,
        accountStatusList,
        cbpTypeList,
        cbpSubTypeList,
        billingSystemList,
        numberOfSubscriptionsList,
        numberOfWirelessList,
        numberOfTVList,
        numberOfInternetList,
        numberOfPhoneList,
        numberOfHomeSecurityList,
        productLabelsMixEnList,
        productLabelsMixFrList,
        serviceCategoryMixEnList,
        serviceCategoryMixFrList,
        routingSegmentList,
        profileSegmentList,
        routingCategoryAList,
        routingCategoryBList,
        routingCategoryCList,
        routingCategoryDList,
        routingCategoryEList,
        masterSubscriptionsList
    };

}


function buildSessionParams(accountData, promptData, billingAccounts) {
    return {
        numberOfBillingAccounts: billingAccounts,
        lastFourAccountNumberList: accountData.accountNumberList.map(a =>
            a !== "NA" ? a.slice(-4) : "NA"
        ),
        ...accountData,
        ...promptData,
        reportingVar_accountNumbers: accountData.accountNumberList.join(","),
        returnCode: "0"
    };
}