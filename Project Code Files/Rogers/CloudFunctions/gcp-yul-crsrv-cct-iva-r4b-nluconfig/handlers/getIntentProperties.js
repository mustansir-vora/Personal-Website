import { logger, sendWebhookResponse, sendErrorResponse } from '@roger/r4b-common-nodemodules';
import { fetchNluDataWithRetry } from '../services/fetchNluDataWithRetry.js';
import { resolveCustomerType } from '../services/resolveCustomerType.js';
import { toBool } from '../services/toBool.js';
import { getRequired } from '../services/getRequired.js';
import { normalizeAuthLevelString } from '../services/normalizeAuthLevelString.js';

export async function getIntentProperties(req, res, params, tag, sessionId) {
  // START LOG: Webhook request
  logger.logWebhookRequest(sessionId, tag, { savedIntent: params?.savedIntent, profileClassification: params?.profileClassification });

  try {
    const savedIntent = params?.savedIntent;
    if (!savedIntent || typeof savedIntent !== "string" || !savedIntent.trim()) {
      throw new Error(`Missing or invalid 'savedIntent': ${savedIntent} in session parameters`);
    }

    const profileClassification = params?.profileClassification;
    const customerType = resolveCustomerType(profileClassification);

    const data = await fetchNluDataWithRetry(sessionId, tag, params);
    const row = data[savedIntent];

    if (!row) {
      throw new Error(`No NLU config row found for intent: ${savedIntent}`);
    }

    let destinationTypeKey, destinationKey, identifyKey, authLevelKey;
    if (customerType) {
      destinationTypeKey = `destinationType${customerType}`;
      destinationKey = `destination${customerType}`;
      identifyKey = `identify${customerType}`;
      authLevelKey = `authLevel${customerType}`;
    }

    const destinationTypeVal = getRequired(row, destinationTypeKey);
    const destinationVal = getRequired(row, destinationKey);
    const identifyVal = getRequired(row, identifyKey);
    const authLevelValRaw = getRequired(row, authLevelKey);

    const missing = [];
    if (destinationTypeVal === undefined) missing.push(destinationTypeKey);
    if (destinationVal === undefined) missing.push(destinationKey);
    if (identifyVal === undefined) missing.push(identifyKey);
    if (authLevelValRaw === undefined) missing.push(authLevelKey);

    if (missing.length > 0) {
      const scope = customerType ? `for '${customerType}'` : "for 'Default'.";
      throw new Error(`Missing required NLU columns ${scope}: ${missing.join(", ")}`);
    }

    const authLevelVal = normalizeAuthLevelString(authLevelValRaw);

    const existingNluConfig = params?.nluConfig && typeof params.nluConfig === "object" ? params.nluConfig : {};

    const nluConfig = {
      ...existingNluConfig,
      intentGroup: row?.intentGroup,
      destinationType: destinationTypeVal,
      destination: destinationVal,
      closed_door_SS: toBool(row?.closed_door_SS),
      wireless_SS: toBool(row?.wirelessSS),
      identify: identifyVal.toLowerCase(),
      specialIntent: toBool(row?.specialIntent),
      authLevel: authLevelVal,
      repromptMenu_EN: row?.repromptMenu_EN,
      repromptMenu_FR: row?.repromptMenu_FR,
    };

    const updatedParams = { ...params, nluConfig, returnCode: "0" };

    // END LOG
    logger.logWebhookResponse(sessionId, tag, { appliedConfig: nluConfig, customerType, savedIntent });

    return sendWebhookResponse(res, sessionId, tag, updatedParams);
  } catch (error) {
    return sendErrorResponse(res, sessionId, tag, error, 1, { returnCode: "1" });
  }
}
