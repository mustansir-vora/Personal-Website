import { apiClient, getRequestHeaders } from "@roger/r4b-common-nodemodules";

/**
 * Handles out-bound network requests for the self-serve modules.
 * This service formats the payload, assigns authentication tokens,
 * and validates the HTTP response status comprehensively.
 *
 * @param {Object} configuration - The API invocation settings.
 * @returns {Promise<Object>} The JSON payload from the completed request.
 * @throws {Error} When the target API returns an unexpected status code.
 */
export async function executeApiCall(configuration) {
  // Extract essential routing parameters
  const apiDictionary = configuration.params;
  const fulfillmentTag = configuration.tag;
  const targetEnvironment = configuration.env;
  
  // Resolve the baseline URL from configuration
  const baseEndpoint = apiDictionary[fulfillmentTag]?.[targetEnvironment];
  if (!baseEndpoint) {
      throw new Error(`SelfServe Routing Error: No API URL mapped for ${fulfillmentTag}.${targetEnvironment}`);
  }
  
  // Apply any dynamic URL path replacements if a resolver function is provided
  const targetUrl = typeof configuration.urlResolver === 'function' 
      ? configuration.urlResolver(baseEndpoint) 
      : baseEndpoint;

  // Retrieve required authentication headers from common utilities
  const headerData = getRequestHeaders(
      configuration.sessionId, 
      configuration.ani, 
      apiDictionary, 
      targetEnvironment
  );

  // Construct the standardized payload for the apiClient utility
  const networkPayload = {
      sessionId: configuration.sessionId,
      tag: fulfillmentTag,
      url: targetUrl,
      headers: headerData.headers,
      tokenConfig: headerData.tokenConfig
  };

  // Attach request body if one exists
  if (configuration.data) {
      networkPayload.data = configuration.data;
  }

  // Determine HTTP method and execute the network stream
  const isPostRequest = (configuration.method || "GET").toUpperCase() === "POST";
  const apiResponse = isPostRequest 
      ? await apiClient.postRequest(networkPayload) 
      : await apiClient.getRequest(networkPayload);

  // Verify the return status against expectations
  const expectedStatusCode = configuration.expectedStatus || 200;
  if (apiResponse.Status !== expectedStatusCode) {
      throw new Error(`SelfServe Network Error: Expected ${expectedStatusCode}, but received ${apiResponse.Status} from ${fulfillmentTag}.`);
  }
  
  return apiResponse.ResponsePayload || {};
}
