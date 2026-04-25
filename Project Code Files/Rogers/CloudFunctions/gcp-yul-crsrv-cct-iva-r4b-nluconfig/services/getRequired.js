/** Ensure a required column exists (present and not empty) or return undefined */
export function getRequired(row, key) {
  if (!Object.hasOwn(row, key)) return undefined;
  const v = row[key];
  if (v === undefined || v === null) return undefined;
  const s = String(v).trim();
  return s === "" ? undefined : s;
}
