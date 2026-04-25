/** Normalize authLevel to "none" | "high" | "low"; default to "none" when invalid */
export function normalizeAuthLevelString(v) {
  const val = (v ?? "").toString().trim().toUpperCase();
  if (val === "NONE" || val === "HIGH" || val === "LOW") return val.toLowerCase();
  return "none";
}
