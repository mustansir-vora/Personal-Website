export function isInProgress(status) {
  if (!status) return false;
  return status.replace(/\s+/g, '').toLowerCase() === 'inprogress';
}
