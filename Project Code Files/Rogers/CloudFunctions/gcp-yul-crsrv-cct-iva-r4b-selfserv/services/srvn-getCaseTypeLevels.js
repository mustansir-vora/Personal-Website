export function getCaseTypeLevels(invokedOSFrom) {
  const baseLevels = {
      caseLevel1: "Tech - Cable",
      caseLevel2: "Automated Activity",
      caseLevel3: "Known Outage IVR",
      caseLevel5: "Ticket"
  };

  let caseLevel4 = "Internet";
  if (invokedOSFrom === "TechTvSupport") caseLevel4 = "TV";
  else if (invokedOSFrom === "TechBusinessPhone" || invokedOSFrom === "TechHomePhone") caseLevel4 = "Home Phone";

  return { ...baseLevels, caseLevel4 };
}
