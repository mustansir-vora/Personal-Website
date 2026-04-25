import { logger } from '@roger/r4b-common-nodemodules';
import axios from 'axios';
import { buildEndpointUrl } from './buildEndpointUrl.js';
import { fetchGoogleAccessToken } from './fetchGoogleAccessToken.js';
import { handleError } from './handleError.js';

/**
 * Constructs the payload and triggers the Dialogflow CX Detect Intent API.
 */
export async function executeDetectIntent(config) {
  const { conversationName, sessionId, lastActivity, location, projectId, agentId, languageCode, tag } = config;
  
  const endpointUrl = buildEndpointUrl(conversationName, sessionId, location, projectId, agentId, tag);

  const requestPayload = {
    queryInput: {
      event: { event: process.env.EVENT_NAME || 'wrapUp' },
      languageCode: languageCode,
    },
    queryParams: {
      parameters: { reportingVar_callEndTime: lastActivity }
    }
  };

  const token = await fetchGoogleAccessToken(sessionId, tag);
  const requestHeaders = {
    'Content-Type': 'application/json',
    Authorization: `Bearer ${token}`,
  };

  const requestStart = Date.now();

  try {
    logger.logApiRequest({
      sessionId,
      tag,
      attemptCount: 1,
      url: endpointUrl,
      method: 'POST',
      headers: requestHeaders,
      data: requestPayload
    });

    const response = await axios.post(endpointUrl, requestPayload, {
      headers: requestHeaders,
      timeout: 15_000
    });

    logger.logApiResponse({
      sessionId,
      tag,
      attemptCount: 1,
      status: response.status,
      executionTimeMs: Date.now() - requestStart,
      response: response.data
    });

    return response.data;
  } catch (error) {
    return handleError(error, sessionId, tag, { throwError: true });
  }
}
