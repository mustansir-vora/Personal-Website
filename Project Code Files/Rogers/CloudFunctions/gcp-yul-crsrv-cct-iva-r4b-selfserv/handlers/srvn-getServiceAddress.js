import { logger, sendWebhookResponse, sendErrorResponse } from "@roger/r4b-common-nodemodules";
import { executeApiCall } from "../services/srvn-executeApiCall.js";
import { DEFAULT_ENV } from "../services/srvn-config.js";

export async function getServiceAddress(req, res, params, tag, sessionId) {
    const env = params.mwInstance || DEFAULT_ENV;
    const ani = params.ani || "Unknown-ANI";
    const accountNumber = params?.accountNumber;

    try {
        // Extract unique samKeys
        const samKeysResult = [...new Set(
            (params?.subscriptionsList || [])
                .filter(s => s.samKey && s.serviceCategory?.english !== "Mobile")
                .map(s => String(s.samKey))
        )];

        logger.logWebhookRequest(sessionId, tag, { accountNumber, samKeys: samKeysResult });

        if (samKeysResult.length === 0) {
            throw new Error(`No Samkeys found in the subscriptions list for account ${accountNumber}.`);
        }

        const d = await executeApiCall({
            params, tag, env, sessionId, ani,
            urlResolver: (baseUrl) => {
                const queryParams = new URLSearchParams();
                samKeysResult.forEach(key => queryParams.append('samKey', key));
                return `${baseUrl}?${queryParams.toString()}`;
            }
        });

        const addresses = d.addressList || [];
        if (addresses.length === 0) {
            throw new Error(`No Service Address found in the address list for samKeys: ${samKeysResult}.`);
        }

        const postalCodesList = addresses.map(item => item.serviceAddress?.postalCode).filter(Boolean);
        const samKeys = [];
        const postalCodes = [];
        const seenPairs = new Set();

        addresses.forEach(item => {
            const currentSamKey = item.samKey;
            const currentPostalCode = item.serviceAddress?.postalCode;
            if (currentSamKey && currentPostalCode) {
                const pairString = `${currentSamKey}_${currentPostalCode}`;
                if (!seenPairs.has(pairString)) {
                    seenPairs.add(pairString);
                    samKeys.push(currentSamKey);
                    postalCodes.push(currentPostalCode);
                }
            }
        });

        return sendWebhookResponse(res, sessionId, tag, {
            returnCode: "0",
            isMultipleAddress: addresses.length > 1,
            isSinglePostalCode: postalCodesList.length > 0 && postalCodesList.every(code => code === postalCodesList[0]),
            samKeys,
            postalCodes,
            serviceAddressPayload: d
        });
    } catch (error) {
        return sendErrorResponse(res, sessionId, tag, error, 1, { returnCode: "1" });
    }
}
