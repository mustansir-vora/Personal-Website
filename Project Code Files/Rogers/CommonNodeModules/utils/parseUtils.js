export function parseJson(value) {
    if (value === undefined || value === "NULL" || value === "" || value === null) return "NA";
    return value;
}

export function normalizeRequestValue(value) {
    if (value === undefined || value === null || value === "" || value === "NA") {
        return "";
    }
    return value;
}

export function parseGenesysParams(value) {
    if (!value || typeof value === "object") return value || {};
    if (typeof value !== "string") return {};

    try {
        let parsed = JSON.parse(value);
        return typeof parsed === "string" ? JSON.parse(parsed) : parsed;
    } catch {
        return {};
    }
}