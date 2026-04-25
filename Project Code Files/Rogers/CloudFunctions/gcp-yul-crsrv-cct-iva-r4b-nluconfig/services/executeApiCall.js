import { apiClient, getRequestHeaders } from '@roger/r4b-common-nodemodules';

/**
 * Standardized network handler enforcing headers, session integrity, timeout metrics, 
 * routing credentials, and validation rules over remote API streams.
 */
export async function executeApiCall(invokeOptions) {
  const { params, tag, env, sessionId, ani, urlResolver = null, method = "GET", data = null, expectedStatus = 200 } = invokeOptions;
  
  const configuredEndpoint = params[tag]?.[env];
  if (!configuredEndpoint) {
      throw new Error(`Missing API URL config for ${tag}.${env}`);
  }
  
  const resolvedUrl = typeof urlResolver === "function" ? urlResolver(configuredEndpoint) : configuredEndpoint;

  const { headers, tokenConfig } = getRequestHeaders(sessionId, ani, params, env);

  const transportConfig = {
    sessionId, 
    tag, 
    url: resolvedUrl,
    headers,
    tokenConfig
  };

  if (data) {
      transportConfig.data = data;
  }

  // Execute standard outbound HTTP stream towards remote provider
  let apiResponseData;
  if (method === "POST") {
    apiResponseData = await apiClient.postRequest(transportConfig);
  } else {
    apiResponseData = await apiClient.getRequest(transportConfig);
  }

  if (apiResponseData.Status !== expectedStatus) {
    throw new Error(`API response was not ${expectedStatus}. API Status: ${apiResponseData.Status}.`);
  }
  
  return apiResponseData.ResponsePayload || {};
}
