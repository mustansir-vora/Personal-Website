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

public class ForemostPolicyInquiry_Post {
	
	//private  final Logger logger = LogManager.getLogger(Constants.LOGGER_NAME);
	
	public  JSONObject start(String url, String tid, String policynumber, String systemdate, String policysource, int conntimeout, int readtimeout, LoggerContext context) {

		Logger logger = context.getLogger(Constants.FARMERS_LOGGER_NAME);
		logger.info(tid+" : Parameters Passed from CVP : " + url+", "+tid+", "+policynumber+", "+systemdate+", "+policysource+", "+conntimeout+", "+readtimeout);
		JSONObject response = new JSONObject();
		APIConnection connection = null;
		String Scope = "api.policyinfo.read";

		try {
			connection = new APIConnection();

			String requestBody = formRequestBody(policynumber, systemdate, policysource);

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
			headers.put("client_id", client_id);
			headers.put("client_secret", client_secret);
			headers.put("Content-Type", "application/json");
			headers.put("frms_source", prop.getProperty("Farmers.Host.frms_source"));
			headers.put("frms_appid", prop.getProperty("Farmers.Host.frms_appid"));
			headers.put("frms_tid", tid);
			headers.put("frms_ipaddress", "0.0.0.0");
			headers.put("frms_region", prop.getProperty("Farmers.Host.frms_region"));
			
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
	

	public  String formRequestBody(String policynumber, String systemdate, String policysource) {

		JSONObject searchCriteria = null;
		JSONObject reqBody = null;
		try {
			String date = systemdate.substring(0,2)+"/"+systemdate.substring(2,4)+"/"+systemdate.substring(4);
			List<Map<String, String>> policies = new ArrayList<Map<String, String>>();
			Map<String, String> list = new HashMap<String, String>();
			
			if (policynumber == null || policynumber.isEmpty() || policynumber.equalsIgnoreCase("")) {
				list.put("policyNumber", "default");
			}
			else {
				list.put("policyNumber", policynumber);
			}
			
			list.put("systemDate", systemdate);
			list.put("policySource", policysource);
			policies.add(list);
			
			searchCriteria = new JSONObject();
			searchCriteria.put("policies", policies);
			
			reqBody = new JSONObject();
			reqBody.put("searchCriteria", searchCriteria);

		} catch (Exception e) {

		}
		return reqBody.toString();

	}
	
	
	public  JSONObject formatErrorResponse(String error, int statusCode) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("responseCode", statusCode);
		jsonObject.put("responseMsg", error);
		return jsonObject;
	}
	
	
	    public static void main(String[] args) { 
		  
		  LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);;
	      File file = new File(new StringBuffer("C:\\Servion\\FARMERS_SO_CVP\\Config\\").append("log4j2.xml").toString());
	      context.setConfigLocation(file.toURI());
		  String url = "https://api-ss.farmersinsurance.com/plcyms/v3/policies?operation=searchByPolicySource"; 
		  JSONObject resp = null; String tid = "Ciscotest"; 
		  ForemostPolicyInquiry_Post test = new ForemostPolicyInquiry_Post();
		  resp = test.start(url, tid, "0080881219", "04/17/2024", "Foremost", 10000, 10000, context); 
	  } 

}
