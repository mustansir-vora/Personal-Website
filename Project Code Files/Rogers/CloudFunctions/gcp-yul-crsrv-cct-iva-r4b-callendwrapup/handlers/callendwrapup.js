import { logger } from '@roger/r4b-common-nodemodules';
import { executeBigQueryJob } from '../services/executeBigQueryJob.js';
import { processInBatches } from '../services/processInBatches.js';
import { handleError } from '../services/handleError.js';

/**
 * Orchestrates the wrap-up process.
 * 1. Fetches candidate rows from BigQuery.
 * 2. Processes those rows concurrently.
 * 3. Returns a summary of the execution results.
 */
export async function callendwrapup(req, res, params, tag, sessionId) {
  const startTime = Date.now();

  try {
    logger.logWebhookDetails(sessionId, tag);
    logger.logWebhookRequest(sessionId, tag, { method: req?.method, url: req?.url, body: req?.body });

    const rows = await executeBigQueryJob(sessionId, tag);
    const executionSummary = await processInBatches(rows, sessionId, tag);

    const payload = {
      ...executionSummary,
      message: 'Wrap-up job completed successfully',
      executionTimeMs: Date.now() - startTime
    };

    logger.logWebhookResponse(sessionId, tag, payload);
    logger.logConsole(sessionId, tag, `Execution completed in ${Date.now() - startTime}ms`);

    return res.status(200).json(payload);
  } catch (error) {
    return handleError(error, sessionId, tag, { res });
  }
}
