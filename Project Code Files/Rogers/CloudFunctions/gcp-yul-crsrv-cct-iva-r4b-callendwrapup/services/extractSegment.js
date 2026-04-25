import { logger } from '@roger/r4b-common-nodemodules';

/**
 * Extracts a specific segment of a conversation path string immediately following a known marker.
 * Example path: projects/{...}/locations/{...}/agents/{...}/sessions/{segment}
 */
export function extractSegment(fullPath, marker, sessionId, tag, operationName) {
  const extractStart = Date.now();
  
  if (typeof fullPath !== 'string' || !marker) {
    logger.logConsole(sessionId, tag, `[${operationName}] Failed: Invalid input data. duration=${Date.now() - extractStart}ms`);
    return null;
  }

  const markerPosition = fullPath.indexOf(marker);
  if (markerPosition === -1) {
    logger.logConsole(sessionId, tag, `[${operationName}] Failed: Marker not found in path. duration=${Date.now() - extractStart}ms`);
    return null;
  }

  const remainder = fullPath.substring(markerPosition + marker.length);
  const matchedSegment = remainder.split('/')[0] || null;

  logger.logConsole(sessionId, tag, `[${operationName}] Concluded: success=${Boolean(matchedSegment)}, duration=${Date.now() - extractStart}ms`);
  
  return matchedSegment;
}
