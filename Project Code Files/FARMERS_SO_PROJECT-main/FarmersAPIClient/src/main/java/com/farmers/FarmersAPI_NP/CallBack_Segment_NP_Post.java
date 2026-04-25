package com.farmers.FarmersAPI_NP;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONObject;

import com.farmers.APIUtil.Constants;
import com.farmers.APIUtil.Encrypt_Decrypt;
import com.farmers.APIUtil.stacktrace;
import com.farmers.bean.RequestBean;

public class CallBack_Segment_NP_Post {

	public JSONObject start(String url, String tid, int conntimeout, int readtimeout, String eventType ,String phoneNumber,String state,String timezone,String callBackTime,String callback,String callbackConsent,String firstName,String lastName, LoggerContext context,String region) {

		Logger logger = context.getLogger(Constants.FARMERS_LOGGER_NAME);
		logger.info(tid+" : Parameters Passed from CVP : "+url+", "+tid+", "+eventType+", "+phoneNumber+", "+state+", "+timezone+", "+callBackTime+", "+callback+", "+callbackConsent+", "+firstName+", "+lastName+", "+conntimeout+", "+readtimeout+", "+region);
		JSONObject response = new JSONObject();
		APIConnection connection = null;
		String Scope = "api.ivrcallinfo.create";
		logger.info(tid+" : Scope = "+Scope);
		try {
			connection = new APIConnection();

			String requestBody = formRequestBody(eventType,phoneNumber,state,timezone,callBackTime,callback,callbackConsent,firstName,lastName);

			HashMap<String, String> headers = (HashMap<String, String>) getHeaders(Scope, tid, logger,region);
			if (null != headers && headers.containsKey("TOKEN_ERROR")) {
				response = formatErrorResponse("OAUTH TOKEN_ERROR", 1);
			} else {
				RequestBean requestBean = new RequestBean(url, requestBody, "POST", headers);
				response = connection.sendHttpRequest(requestBean, logger, tid, conntimeout, readtimeout);
				logger.info(tid+" : Final Response = "+response);
			}
		} catch (Exception e) {
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

			String client_id = prop.getProperty("Farmers.Host.NonProd.ClientID");
			String client_secret = prop.getProperty("Farmers.Host.NonProd.ClientSecret");
			Encrypt_Decrypt Decryptor = new Encrypt_Decrypt();
			client_id = Decryptor.decrypt(client_id);
			client_secret = Decryptor.decrypt(client_secret);

			headers = new HashMap<String, String>();
			headers.put("client_id", client_id);
			headers.put("client_secret", client_secret);
			headers.put("frms_source", prop.getProperty("Farmers.Host.frms_source"));
			headers.put("frms_appid", prop.getProperty("Farmers.Host.frms_appid"));
			headers.put("frms_tid", tid);
			headers.put("frms_region",region);
			headers.put("Content-Type", "application/json");



			 TokenInvocationNonProd tokeninvocation = new TokenInvocationNonProd();
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

	public  String formRequestBody(String eventType ,String phoneNumber,String state,String timezone,String callBackTime,String callback,String callbackConsent,String firstName,String lastName) {

		JSONObject event = null;
		JSONObject reqBody = null;

		event = new JSONObject();


		event.put("eventType", eventType);
		event.put("phoneNumber", phoneNumber);
		event.put("state", state);
		event.put("timezone", timezone);
		event.put("callbackTime", callBackTime);
		event.put("callbackConsent", callbackConsent);
		event.put("callback", callback);
		event.put("firstName", firstName);
		event.put("lastName", lastName);


		reqBody = new JSONObject();
		reqBody.put("event", event);

		return reqBody.toString();

	}
}