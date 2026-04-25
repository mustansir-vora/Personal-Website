import { logger } from '@roger/r4b-common-nodemodules';
import { evaluateRow } from './evaluateRow.js';
import { handleError } from './handleError.js';
import { CONCURRENCY_LIMIT } from './config.js';

/**
 * Processes a list of rows subject to a strict concurrency limit.
 */
export async function processInBatches(rows, sessionId, tag) {
  const diagnostics = [];
  let triggeredCount = 0;
  let skippedByCooldown = 0;

  // Use a copy of the rows as our task queue
  const queue = [...rows];

  // Worker routine to dequeue and process rows until empty
  const consumeQueue = async () => {
    while (queue.length > 0) {
      const row = queue.shift();
      if (!row) continue;

      try {
        const result = await evaluateRow(row, sessionId, tag);
        diagnostics.push(result);

        if (result.triggered) {
          triggeredCount++;
        } else if (result.reason === 'cooldown_active') {
          skippedByCooldown++;
        }
      } catch (error) {
        diagnostics.push(
          handleError(error, sessionId, tag, {
            buildResponse: (msg) => ({
              conversationName: row?.conversation_name,
              triggered: false,
              reason: 'worker_error',
              error: msg
            })
          })
        );
      }
    }
  };

  try {
    // Spawn workers up to the concurrency limit or the number of rows
    const workerCount = Math.min(CONCURRENCY_LIMIT, rows.length);
    const workerPool = Array.from({ length: workerCount }, consumeQueue);
    await Promise.all(workerPool);
  } finally {
    logger.logConsole(sessionId, tag, `
      ====== Execution Summary ======
      Total Candidates Evaluated  : ${rows.length}
      Successfully Triggered      : ${triggeredCount}
      Skipped (Cooldown Active)   : ${skippedByCooldown}
      ===============================
    `);
  }

  return {
    totalRows: rows.length,
    triggeredCount,
    skippedByCooldown,
    details: diagnostics
  };
}
