/**
 * Resolves verbose CRM `profileClassification` into a streamlined `customerType`.
 * 
 * Target definitions rely heavily upon resolving whether an account implies Business, 
 * Enterprise, Government, or Default structures.
 */
export function resolveCustomerType(pc) {
  const classification = String(pc || "").trim().toLowerCase();
  
  if (classification === "business_enterprise_wireless") return "Enterprise";
  if (["business_small_medium_wireless", "business_small_medium_wireline_east", "business_unknown"].includes(classification)) return "Business";
  if (classification === "government") return "Government";
  
  return "Business";
}
