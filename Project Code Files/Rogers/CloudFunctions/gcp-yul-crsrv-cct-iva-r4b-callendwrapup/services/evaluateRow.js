import { logger } from '@roger/r4b-common-nodemodules';
import { extractSegment } from './extractSegment.js';
import { parseSafeDate } from './parseSafeDate.js';
import { executeDetectIntent } from './executeDetectIntent.js';
import { updateCooldownLedger } from './updateCooldownLedger.js';
import { handleError } from './handleError.js';
import { cooldownCache, COOLDOWN_PERIOD_MS } from './config.js';

/**
 * Evaluates a single BigQuery row to determine if Detect Intent should be triggered.
 */
export async function evaluateRow(row, globalSessionId, tag) {
  const conversationName = row?.conversation_name;
  const rowSessionId = extractSegment(conversationName, process.env.SESSION_MARKER, globalSessionId, tag, "extractSessionId");

  if (!rowSessionId) {
    logger.logConsole(globalSessionId, tag, `Malformed or missing session ID for: ${conversationName}`);
    return { conversationName, triggered: false, reason: 'invalid_session_id' };
  }

  const lastActivityDate = parseSafeDate(row?.last_activity_time, globalSessionId, tag);
  const currentTime = Date.now();

  // Enforce cooldown to prevent aggressive re-triggering
  if (cooldownCache.has(rowSessionId)) {
    const lastAttemptTime = cooldownCache.get(rowSessionId);
    if (currentTime - lastAttemptTime < COOLDOWN_PERIOD_MS) {
      logger.logConsole(globalSessionId, tag, `Suppressed execution for ${rowSessionId}: Cooldown active.`);
      return { conversationName, triggered: false, reason: 'cooldown_active' };
    }
  }

  try {
    const location = extractSegment(conversationName, process.env.LOCATION_MARKER, globalSessionId, tag, "extractLocation") || 'northamerica-northeast1';

    await executeDetectIntent({
      conversationName,
      sessionId: rowSessionId,
      lastActivity: lastActivityDate,
      location,
      projectId: row?.project_id,
      agentId: row?.agent_id,
      languageCode: row?.language_code,
      tag
    });

    updateCooldownLedger(rowSessionId, currentTime);
    return { conversationName, triggered: true };
  } catch (error) {
    return handleError(error, globalSessionId, tag, {
      buildResponse: (msg) => ({ conversationName, triggered: false, error: msg })
    });
  }
}
