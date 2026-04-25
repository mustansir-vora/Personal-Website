import { logger } from '@roger/r4b-common-nodemodules';
import { GoogleAuth } from 'google-auth-library';
import { handleError } from './handleError.js';

/**
 * Exchanges Google service account credentials for a short-lived OAuth 2.0 access token.
 */
export async function fetchGoogleAccessToken(sessionId, tag) {
  const fetchStart = Date.now();
  const requestedScopes = [process.env.SCOPE_1, process.env.SCOPE_2].filter(Boolean);

  try {
    logger.logConsole(sessionId, tag, 'Initiating Google Auth token request');

    const authInstance = new GoogleAuth({ scopes: requestedScopes });
    const authClient = await authInstance.getClient();
    const tokenResponse = await authClient.getAccessToken();

    // Accommodate variations across different versions of google-auth-library
    if (!tokenResponse?.token) {
      const rawToken = (typeof tokenResponse === 'string' && tokenResponse) ? tokenResponse : '';
      const sourceIndicator = rawToken ? 'raw-string' : 'exchange-failure';
      
      logger.logConsole(sessionId, tag, `Token provisioned: source=${sourceIndicator}, duration=${Date.now() - fetchStart}ms`);
      return rawToken;
    }

    logger.logConsole(sessionId, tag, `Token provisioned: source=token-object, duration=${Date.now() - fetchStart}ms`);
    return tokenResponse.token;
  } catch (error) {
    return handleError(error, sessionId, tag, { fallbackValue: '' });
  }
}
