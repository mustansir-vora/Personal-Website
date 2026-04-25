import { Storage } from "@google-cloud/storage";

export const storage = new Storage();
export const NLU_BUCKET_NAME = process.env.GCS_BUCKET_NAME;
export const NLU_FILE_NAME = process.env.GCS_NLU_FILE_NAME;

export const nluCache = { etag: null, data: null };
export const nluCacheStream2 = { etag: null, data: null };