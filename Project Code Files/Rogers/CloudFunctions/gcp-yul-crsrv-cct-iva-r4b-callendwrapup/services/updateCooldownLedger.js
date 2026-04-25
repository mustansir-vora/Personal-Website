import { cooldownCache, MAX_CACHE_SIZE } from './config.js';

/**
 * Registers a session ID into the cooldown cache.
 * Evicts the oldest entry if the cache size exceeds its limit to safeguard memory.
 */
export function updateCooldownLedger(sessionId, timestamp) {
  cooldownCache.set(sessionId, timestamp);
  
  if (cooldownCache.size > MAX_CACHE_SIZE) {
    const [oldestKey] = cooldownCache.keys();
    cooldownCache.delete(oldestKey);
  }
}
