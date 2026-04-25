package com.farmers.FarmersAPI;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONObject;

import com.farmers.APIUtil.Constants;
import com.farmers.APIUtil.Encrypt_Decrypt;
import com.farmers.APIUtil.stacktrace;
import com.farmers.bean.RequestBean;

public class AgentInfo_Post {
	
	//private Logger logger = LogManager.getLogger(Constants.LOGGER_NAME);

	public  JSONObject start(String url, String tid, String businessunit, String phonenumber,String userid, String systemname, int conntimeout, int readtimeout, LoggerContext context) {

		Logger logger = context.getLogger(Constants.FARMERS_LOGGER_NAME);
		logger.info(tid+" : Parameters Passed from CVP : "+url+", "+tid+", "+businessunit+", "+phonenumber+", "+userid+", "+systemname+", "+conntimeout+", "+readtimeout);
		JSONObject response = new JSONObject();
		APIConnection connection = null;
		String Scope = "api.agentinfo.read";

		try {
			connection = new APIConnection();

			String requestBody = formRequestBody(businessunit, phonenumber, userid, systemname);

			HashMap<String, String> headers = (HashMap<String, String>) getHeaders(Scope, tid, logger);
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

	private  Map<String, String> getHeaders(String Scope, String tid, Logger logger) {

		Map<String, String> headers = null;
		try {
			Properties prop = new Properties();
	        prop.load(new FileInputStream(Constants.CONFIG_PATH));
	        
	        String client_id = prop.getProperty("Farmers.Host.ClientID");
	        String client_secret = prop.getProperty("Farmers.Host.ClientSecret");
	        Encrypt_Decrypt Decryptor = new Encrypt_Decrypt();
	        client_id = Decryptor.decrypt(client_id);
	        client_secret = Decryptor.decrypt(client_secret);
			
			headers = new HashMap<String, String>();
			headers.put("frms_source", prop.getProperty("Farmers.Host.frms_source"));
			headers.put("frms_appid", prop.getProperty("Farmers.Host.frms_appid"));
			headers.put("frms_tid", tid);
			headers.put("frms_ipaddress", "0.0.0.0");
			headers.put("Content-Type", "application/json");
			headers.put("client_id", client_id);
			headers.put("client_secret", client_secret);
			headers.put("frms_region", prop.getProperty("Farmers.Host.frms_region"));
			
			// access_token = new String();
			TokenInvocation tokeninvocation = new TokenInvocation();
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

	public  JSONObject formatErrorResponse(String error, int statusCode) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("responseCode", statusCode);
		jsonObject.put("responseMsg", error);
		return jsonObject;
	}

	public  String formRequestBody(String businessunit, String phonenumber,String userid, String systemname) {

		JSONObject Criteria = null;
		JSONObject reqBody = null;
		try {
			List<Map<String, String>> agentSearchCriteria = new ArrayList<Map<String, String>>();
			Map<String, String> list = new HashMap<String, String>();
			list.put("businessUnit", businessunit);
			if (phonenumber == null || phonenumber.isEmpty() || phonenumber.equalsIgnoreCase("")) {
				list.put("phoneNumber", "default");
			}
			else {
				list.put("phoneNumber", phonenumber);
			}
			
			agentSearchCriteria.add(list);

			Criteria = new JSONObject();
			Criteria.put("systemName", systemname);
			Criteria.put("agentSearchCriteria", agentSearchCriteria);
			Criteria.put("userId", userid);
			
			reqBody = new JSONObject();
			reqBody.put("criteria", Criteria);

		} catch (Exception e) {

		}
		return reqBody.toString();

	}

	
	  public static void main(String[] args) {
	  
	  LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);;
	  File file = new File(new StringBuffer("C:\\Servion\\FARMERS_SO_CVP\\Config\\").append("log4j2.xml").toString());
	  context.setConfigLocation(file.toURI());
	  String url = "https://api-ss.farmersinsurance.com/agentms/v2/agents?operation=retrieveByPhonenumber";
	  JSONObject resp = null; String tid = "DNMIS"; 
	  AgentInfo_Post test = new AgentInfo_Post();
	  resp = test.start(url, tid, "IVRPLA", "5074747005","VRS-101", "VRS", 10000, 10000 ,context); 
	  }
	 
}
