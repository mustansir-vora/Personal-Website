import { logger } from '@roger/r4b-common-nodemodules';

/**
 * Formats a fully qualified Dialogflow REST API URL correctly mapping environment variables and extracted keys.
 */
export function buildEndpointUrl(conversationName, sessionId, location, projectId, agentId, tag) {
  const buildStart = Date.now();
  
  const targetHost = `${location}${process.env.HOST || ''}`;
  let baseEndpoint = process.env.BASE_URL || '';

  baseEndpoint = baseEndpoint
    .replace("${host}", targetHost)
    .replace("${projectName}", projectId || '')
    .replace("${agentID}", agentId || '')
    .replace("${location}", location || '');

  if (typeof conversationName === 'string' && !conversationName.includes('environments')) {
    baseEndpoint = baseEndpoint.replace("environments/-/", "");
  }

  logger.logConsole(sessionId, tag, `Endpoint constructed: location=${location}, targetHost=${targetHost}, duration=${Date.now() - buildStart}ms`);
  
  return `${baseEndpoint}/${encodeURIComponent(sessionId)}:detectIntent`;
}
