import { logger } from '@roger/r4b-common-nodemodules';
import { handleError } from './handleError.js';

/**
 * Safely parses assorted timestamp formats into a valid JavaScript Date object.
 * Accommodates raw strings as well as BigQuery's object representation { value: string }.
 */
export function parseSafeDate(input, sessionId, tag) {
  const parseStart = Date.now();
  
  try {
    const stringValue = (typeof input === 'object' && input !== null && 'value' in input) 
      ? input.value 
      : input;
      
    const parsedDate = new Date(stringValue);
    const isValid = Number.isFinite(parsedDate.getTime());

    logger.logConsole(sessionId, tag, `Timestamp parsed: valid=${isValid}, inputType=${typeof stringValue}, parsingTime=${Date.now() - parseStart}ms`);
    
    return isValid ? parsedDate : null;
  } catch (error) {
    return handleError(error, sessionId, tag, { fallbackValue: null });
  }
}
