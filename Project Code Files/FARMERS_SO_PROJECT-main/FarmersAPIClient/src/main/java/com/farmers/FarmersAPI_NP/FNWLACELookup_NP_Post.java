package com.farmers.FarmersAPI_NP;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONObject;

import com.farmers.APIUtil.Constants;
import com.farmers.APIUtil.Encrypt_Decrypt;
import com.farmers.APIUtil.stacktrace;
import com.farmers.bean.RequestBean;

public class FNWLACELookup_NP_Post {
	
	public JSONObject start(String url, String tid, String ani, String aor, String lookupType, String region, int conntimeout, int readtimeout, LoggerContext context) {
		Logger logger = context.getLogger(Constants.FARMERS_LOGGER_NAME);
		logger.info(tid + " : Parameters Passed from CVP : url :: " + url + ", tid :: " + tid + ", ani :: " + ani + ", aor :: " + aor + ", lookupType :: " + lookupType + ", connTimeOut :: " +conntimeout+", readTimeOut :: "+readtimeout);
		JSONObject response = new JSONObject();
		APIConnection connection = null;
		String Scope = "api.agentinfo.read";
		logger.info(tid+" : Scope = "+Scope);
		
		try {
			
			connection = new APIConnection();
			String requestBody = formRequestBody(tid, ani, aor, lookupType, logger);
			HashMap<String, String> headers = (HashMap<String, String>) getHeaders(Scope, tid, logger, region);
			
			if (null != headers && headers.containsKey("TOKEN_ERROR")) {
				response = formatErrorResponse("OAUTH TOKEN_ERROR", 1);
			} else {
				RequestBean requestBean = new RequestBean(url, requestBody, "POST", headers);
				response = connection.sendHttpRequest(requestBean, logger, tid, conntimeout, readtimeout);
				logger.info(tid+" : Final Response = "+response);
			}
			
		}
		catch (Exception e) {
			// Handle other exceptions
			logger.error(tid+" : Unexpected error occurred : " + e.getMessage());
			stacktrace.printStackTrace(logger, e);
			e.printStackTrace();
		}
		
		return response;
	}
	
	private Map<String, String> getHeaders(String Scope, String tid, Logger logger, String region) {

		Map<String, String> headers = null;
		try {
			Properties prop = new Properties();
	        prop.load(new FileInputStream(Constants.CONFIG_PATH));
	        
	        String client_id = prop.getProperty("FARMERS.FNWL.NonProd.ClientID");
	        String client_secret = prop.getProperty("FARMERS.FNWL.NonProd.ClientSecret");
	        Encrypt_Decrypt Decryptor = new Encrypt_Decrypt();
	        client_id = Decryptor.decrypt(client_id);
	        client_secret = Decryptor.decrypt(client_secret);
	        
	        headers = new HashMap<String, String>();
	        headers.put("client_id", client_id);
			headers.put("client_secret", client_secret);
			headers.put("frms_source", prop.getProperty("Farmers.Host.frms_source"));
			headers.put("frms_appid", prop.getProperty("Farmers.Host.frms_appid"));
			headers.put("frms_tid", tid);
			headers.put("frms_region", region);
			headers.put("Content-Type", "application/json");
			
			TokenInvocation_FNWL tokeninvocation = new TokenInvocation_FNWL();
			String access_token = tokeninvocation.getAccessToken(Scope, logger, tid);
			if (null != access_token) {
				headers.put("Authorization", "Bearer " + access_token);
			} else {
				headers.put("TOKEN_ERROR", "true");
			}
			
			logger.info(tid+" : Headers : "+headers);
		} catch (Exception e) {
			// Handle other exceptions
			logger.error(tid+" : Unexpected error occurred : " + e.getMessage());
			stacktrace.printStackTrace(logger, e);
			e.printStackTrace();
		}
		return headers;
	}
	
	public JSONObject formatErrorResponse(String error, int statusCode) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("responseCode", statusCode);
		jsonObject.put("responseMsg", error);
		return jsonObject;
	}
	
	public String formRequestBody(String tid, String ani, String aor, String lookupType, Logger logger) {

		JSONObject reqBody = null;
		JSONObject criteria = null;
		List<Map<String, String>> agentSearchCriteria = null;
		Map<String, String> agentCriteria = null;
		
		try {
			agentCriteria = new HashMap<String, String>();
			agentCriteria.put("businessUnit", "IVRFNWL");
			if (lookupType.equalsIgnoreCase("AOR")) {
				agentCriteria.put("AORId", aor);
			}
			else {
				agentCriteria.put("phoneNumber", ani);
			}
			agentSearchCriteria = new ArrayList<Map<String, String>>();
			agentSearchCriteria.add(agentCriteria);
			criteria = new JSONObject();
			criteria.put("userId", "VRS-101");
			criteria.put("systemName", "VRS");
			criteria.put("agentSearchCriteria", agentSearchCriteria);
			reqBody = new JSONObject();
			reqBody.put("criteria", criteria);
		}
		catch (Exception e) {
			// Handle other exceptions
			logger.error(tid+" : Unexpected error occurred : " + e.getMessage());
			stacktrace.printStackTrace(logger, e);
			e.printStackTrace();
		}
		return reqBody.toString();
	}
}
