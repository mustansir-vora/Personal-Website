import { logger } from '@roger/r4b-common-nodemodules';
import { BigQuery } from '@google-cloud/bigquery';
import { handleError } from './handleError.js';

/**
 * Executes the configured BigQuery SQL statement to retrieve target rows.
 */
export async function executeBigQueryJob(sessionId, tag) {
  const bqClient = new BigQuery();
  const querySql = process.env.BIG_QUERY_SQL;
  const requestStart = Date.now();

  if (!querySql) {
    throw new Error("Missing required environment variable: BIG_QUERY_SQL");
  }

  try {
    logger.logApiRequest({
      sessionId,
      tag,
      attemptCount: 1,
      url: 'BigQuery',
      method: 'SQL',
      params: { sql: querySql }
    });

    const [job] = await bqClient.createQueryJob({ query: querySql });
    logger.logConsole(sessionId, tag, `BigQuery job dispatched. Job ID: ${job.id}`);

    const [rows] = await job.getQueryResults();
    
    logger.logApiResponse({
      sessionId,
      tag,
      attemptCount: 1,
      status: 200,
      executionTimeMs: Date.now() - requestStart,
      response: { rowCount: rows.length }
    });

    return rows || [];
  } catch (error) {
    return handleError(error, sessionId, tag, { throwError: true });
  }
}
