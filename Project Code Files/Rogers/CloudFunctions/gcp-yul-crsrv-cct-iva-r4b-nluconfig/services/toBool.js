/** Safe boolean parsing */
export function toBool(v) {
  if (typeof v === "boolean") return v;
  if (v === undefined || v === null) return false;
  return String(v).trim().toLowerCase() === "true";
}
