import csv from "csv-parser";
import { storage, NLU_BUCKET_NAME, NLU_FILE_NAME, nluCache, nluCacheStream2 } from './config.js';

export async function getNluData(sessionId, tag, params) {
  const agentName = params?.agentName;
  let file;
  let source = "";
  if (agentName && agentName === "stream2") {
    file = storage.bucket(NLU_BUCKET_NAME).file("r4b-nluconfig-Stream2.csv");
  } else {
    file = storage.bucket(NLU_BUCKET_NAME).file(NLU_FILE_NAME);
  }
  try {
    const [metadata] = await file.getMetadata();
    const currentEtag = metadata.etag;

    if (agentName && agentName === "stream2") {
      if (nluCacheStream2.data && nluCacheStream2.etag && nluCacheStream2.etag === currentEtag) {
        return { source: "cache - Stream 2", data: nluCacheStream2.data };
      }
    } else {
      if (nluCache.data && nluCache.etag && nluCache.etag === currentEtag) {
        return { source: "cache", data: nluCache.data };
      }
    }

    const dataMap = {};
    await new Promise((resolve, reject) => {
      file.createReadStream()
        .on("error", reject)
        .pipe(csv())
        .on("data", (row) => {
          const keys = Object.keys(row);
          const primaryKey = row[keys[0]];
          if (primaryKey) {
            dataMap[primaryKey] = row;
          }
        })
        .on("end", () => resolve())
        .on("error", reject);
    });

    if (agentName && agentName === "stream2") {
      nluCacheStream2.etag = currentEtag;
      nluCacheStream2.data = dataMap;
      source = "Bucket - Stream 2"
    } else {
      nluCache.etag = currentEtag;
      nluCache.data = dataMap;
      source = "Bucket"
    }
    return { source: source, data: dataMap };
  } catch (err) {
    throw new Error("Failed to fetch NLU data from bucket: " + (err?.message || err));
  }
}
