import { logger } from '@roger/r4b-common-nodemodules';
import { getNluData } from './getNluData.js';

export async function fetchNluDataWithRetry(sessionId, tag, params, maxAttempts = 2) {
  let attempt = 1;
  let nluFetchError = null;

  while (attempt <= maxAttempts) {
    try {
      const nluResult = await getNluData(sessionId, tag, params);
      return nluResult.data;
    } catch (err) {
      nluFetchError = err;
      logger.logErrorResponse({ sessionId, tag, attempt, err: nluFetchError });
      attempt++;
    }
  }

  throw new Error(`Error fetching NLU data: ${nluFetchError?.message}`);
}
