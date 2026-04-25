import { Storage } from "@google-cloud/storage";
import * as logger from "../utils/logger.js";
import * as apiClient from "./apiClient.js";

const storage = new Storage();
const BUCKET_NAME = process.env.GCS_BUCKET_NAME;

// Generic cache store
const configCache = {
	callStart: { data: null, etag: null },
	callEnd: { data: null, etag: null },
	accountselfserve: { data: null, etag: null }
};

export async function loadConfig({ sessionId, cacheKey}) {
  try {
    const file = storage.bucket(BUCKET_NAME).file(cacheKey + ".json");

    const metaStart = Date.now();
    const [metadata] = await file.getMetadata();
    const metaTime = Date.now() - metaStart;

    logger.logConsole(
      sessionId,
      cacheKey,
      `Metadata fetched in ${metaTime} ms (etag=${metadata.etag})`
    );

    const cache = configCache[cacheKey];

    // Serve from cache
    if (cache.data && cache.etag === metadata.etag) {
      logger.logConsole(
        sessionId,
        cacheKey,
        `Config served from cache (etag=${metadata.etag}) - ` + JSON.stringify(cache.data)
      );

      return buildSuccess(cache.data);
    }

    // Download new config
    const downloadStart = Date.now();
    const [contents] = await file.download();
    const downloadTime = Date.now() - downloadStart;

    const parsed = JSON.parse(contents.toString());

    // Update cache
    configCache[cacheKey] = {
      data: parsed,
      etag: metadata.etag
    };

    logger.logConsole(
      sessionId,
      cacheKey,
      `Config downloaded in ${downloadTime} ms (etag=${metadata.etag}) - ` + JSON.stringify(parsed) 
    );

    return buildSuccess(parsed);

  } catch (err) {
    logger.logErrorResponse({ sessionId, cacheKey, attemptCount: 1, err });

    console.log(JSON.stringify({
      event: "ErrorResponse",
      severity: "ERROR",
      sessionId,
      cacheKey,
      details: JSON.stringify({
        message: `Failed to load config file at : ${BUCKET_NAME}/${cacheKey}`
      })
    }));

    return buildFailure();
  }
}

function buildSuccess(payload) {
  return {
    Status: 200,
    ReturnCode: "0",
    ResponsePayload: payload
  };
}

function buildFailure() {
  return {
    Status: 500,
    ReturnCode: "1",
    ResponsePayload: {}
  };
}

function getApiUrl(apiConfig, tag, mw, pathParams = {}, urlParams = {}) {
    const config = configCache[apiConfig].data[tag];
    const env = configCache[apiConfig].data.env;

    if (!config) {
        throw new Error(`API config not found for tag: ${tag}`);
    }

    let path = config.path;

    for (const [key, value] of Object.entries(pathParams)) {
        path = path.replace(key, value);
    }

    if (Object.keys(urlParams).length > 0 ) {
        path += "?";
        for (const [key, value] of Object.entries(urlParams)) {
            if (value != null) {
                path += key + "=" + value + "&";
            }
        }
        path = path.slice(0,-1);
    }

    const url = env.hostName + mw + path;

    return url;
}

function buildTokenConfig(apiConfig, tag, retries) {
    const config = configCache[apiConfig].data;

    return {
        timeOutMs: config.env.timeOutMS || "15000",
        apiAttempts: retries,
        tokenUrl: config.env.tokenUrl,
        scope: config.env.scope,
        tokenRefreshTimeMin: config.env.tokenRefreshTimeMin || "55"
    };
}



function buildHeaders(sessionId, ani) {
    return {
        cdr: sessionId,
        transactionId: `${sessionId}-${ani}`,
        transactionDateTime: new Date().toISOString()
    };
}

async function sendWebhookResponse(res, sessionId, tag, sessionParams) {
    const webhookResponse = {
        sessionInfo: { parameters: sessionParams }
    };

    logger.logWebhookResponse(sessionId, tag, webhookResponse);
    res.status(200).json(webhookResponse);
}

async function sendErrorResponse(res, sessionId, tag, err) {
    logger.logErrorResponse({ sessionId, tag, attempt: 1, err });

    const webhookResponse = {
        sessionInfo: {
            parameters: { returnCode: "1" }
        }
    };

    logger.logWebhookResponse(sessionId, tag, webhookResponse);
    res.status(200).json(webhookResponse);
}

async function makeApiCall({
    req,
    res,
    apiConfig,
    tag,
    buildRequest,      // builds url, method, body
    onSuccess,         // updates sessionParams on success
    onFailure          // updates sessionParams on failure
}) {
    const originalParams = req.body.sessionInfo?.parameters || {};
    const params = structuredClone(originalParams);
    const sessionId = params.conversationId || "Unknown-Session";

    try {
        const { method, url, body } = buildRequest(params);
        const retries = configCache[apiConfig].data[tag].retries;
        const useStub = configCache[apiConfig].data[tag].useStub;

        if (useStub) {
            //logger.logConsole(sessionId, tag, "Using stub response");
            // return mocked payload here if needed
        }

        logger.logWebhookRequest(sessionId, tag, body || {});
        logger.logConsole(sessionId, tag, url);

        const headers = buildHeaders(sessionId, params.ani);
        const tokenConfig = buildTokenConfig(apiConfig, tag, retries);

        let apiResult;
        if (method === "POST") {
            apiResult = await apiClient.postRequest({ sessionId, tag, url, headers, data: body, tokenConfig });
        } else if (method === "DELETE") {
            apiResult = await apiClient.deleteRequest({ sessionId, tag, url, headers, tokenConfig });
        } else if (method === "PUT") {
            apiResult = await apiClient.putRequest({ sessionId, tag, url, headers, data: body, tokenConfig });
        } else {
            apiResult = await apiClient.getRequest({ sessionId, tag, url, headers, tokenConfig });
        }
        
        let newParams;

        if (apiResult.Status === 200 || apiResult.Status < 300) {
            newParams = onSuccess(params, apiResult.ResponsePayload);
        } else {
            newParams = onFailure(params, apiResult.ResponsePayload);
        }

        sendWebhookResponse(res, sessionId, tag, newParams);

    } catch (err) {
        sendErrorResponse(res, sessionId, tag, err);
    }
}

async function invalidTag(res, sessionId, tag) {
    logger.logConsole(sessionId, tag, "Invalid tag for this function");

    const webhookResponse = {
        sessionInfo: {
            parameters: {
                returnCode: "1",
                errorDetails: "Invalid tag for this function"
            }
        }
    };

    logger.logWebhookResponse(sessionId, tag, webhookResponse);
    res.status(200).json(webhookResponse);
}

export { getApiUrl, sendWebhookResponse, sendErrorResponse, makeApiCall, invalidTag };