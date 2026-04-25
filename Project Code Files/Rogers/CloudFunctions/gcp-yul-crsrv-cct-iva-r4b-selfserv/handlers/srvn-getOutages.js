import { logger, sendWebhookResponse, sendErrorResponse } from "@roger/r4b-common-nodemodules";
import { executeApiCall } from "../services/srvn-executeApiCall.js";
import { isInProgress } from "../services/srvn-isInProgress.js";

const getServices = (osFrom, system) => {
  if (osFrom === "TechInternetSupport") return ["HS"];
  if (osFrom === "TechTvSupport") {
    return system?.toLowerCase() === "maestro" ? ["IPTV"] : ["DTV"];
  }
  if (osFrom === "TechHomeSecurity") return ["SHM"];
  if (osFrom === "TechBusinessPhone" || osFrom === "TechHomePhone") return ["TN"];
  return [];
};

const getFirstActiveOutage = (outages) => {
  for (const account of (outages || [])) {
    const activeDetail = (account.outageDetails || []).find(detail => isInProgress(detail.status));
    if (activeDetail) {
      return {
        accountNumber: account.accountNumber || "",
        samKey: account.samKey || "",
        postalCode: account.postalCode || "",
        outageNumber: activeDetail.outageNumber || "",
        ticketNumber: activeDetail.ticketNumber || "",
        parentCaseId: activeDetail.parentCaseId || "",
        category: activeDetail.category || "",
        outageCode: activeDetail.outageCode || "",
        affectedServices: activeDetail.affectedServices || [],
        voiceScriptContent: activeDetail.outageMessage?.voiceScriptContent || ""
      };
    }
  }
  return null;
};

export async function getOutages(req, res, params, tag, sessionId) {
  try {
    const env = params.mwInstance || "qa4";
    const ani = params?.ani || "Unknown-ANI";
    const languageCode = params?.flowLanguage || "en-US";
    const accountBrand = params?.brand || "ROGERS";
    const subscriptionsList = params?.subscriptionsList || [];

    const subscriptionAccountDetails = params?.subscriptionAccountDetails;
    const linkedAccountNumber = subscriptionAccountDetails?.linkedAccountNumber;
    const linkedAccountSystem = subscriptionAccountDetails?.linkedAccountSystem;
    const samKey = params?.samKey;

    const samKeysList = samKey ? [samKey] : [];
    const invokedOSFrom = params?.invokedOSFrom || "";
    const services = getServices(invokedOSFrom, linkedAccountSystem);

    let accountsArray = [];
    if (linkedAccountNumber && linkedAccountSystem) {
      accountsArray = [
        {
          accountNumber: linkedAccountNumber,
          accountBrand,
          maestroInd: linkedAccountSystem.toLowerCase() === "maestro",
          samKeys: samKeysList,
          services
        }
      ];
    } else {
      accountsArray = subscriptionsList.map(subscription => ({
        accountNumber: subscription.linkedAccountNumber,
        accountBrand,
        maestroInd: subscription.linkedAccountSystem?.toLowerCase() === "maestro",
        samKeys: [],
        services: []
      }));
    }

    logger.logWebhookRequest(sessionId, tag, { language: languageCode, accountsArray });

    const d = await executeApiCall({
      params, tag, env, sessionId, ani, method: "POST",
      data: { language: languageCode, sessionId, accounts: accountsArray }
    });

    const firstActiveOutage = getFirstActiveOutage(d?.outages);

    const finalOutagesArray = firstActiveOutage ? [firstActiveOutage] : [];

    return sendWebhookResponse(res, sessionId, tag, {
      returnCode: 0,
      outageCount: finalOutagesArray.length,
      outages: finalOutagesArray,
      outagesPayload: d
    });
  } catch (error) {
    return sendErrorResponse(res, sessionId, tag, error, 1, { returnCode: "1" });
  }
}
