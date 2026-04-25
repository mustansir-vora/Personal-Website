import { logger } from '@roger/r4b-common-nodemodules';

/**
 * Standardized error handling wrapper for concise and consistent error routing.
 * Logs the error securely and either throws, responds via HTTP, or returns a fallback depending on configurations.
 */
export function handleError(errorInstance, sessionId, tag, policy = {}) {
  logger.logErrorResponse({ 
    sessionId, 
    tag, 
    attemptCount: 1, 
    err: errorInstance 
  });

  const errorMessage = errorInstance instanceof Error 
    ? errorInstance.message 
    : String(errorInstance);

  if (policy.throwError === true) {
    throw errorInstance;
  }

  if (policy.res !== undefined) {
    return policy.res.status(500).json({ error: errorMessage });
  }

  if (typeof policy.buildResponse === 'function') {
    return policy.buildResponse(errorMessage);
  }

  return policy.fallbackValue;
}
