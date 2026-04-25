package com.farmers.FarmersAPI_NP;

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

public class RiskAttritionAPILookup_NP_Post {
	
	public JSONObject start(String url, String tid, String policynumber, String policysource, String timestamp, int conntimeout, int readtimeout, LoggerContext context, String region) {

		Logger logger = context.getLogger(Constants.FARMERS_LOGGER_NAME);
		logger.info(tid+" : Parameters Passed from CVP : " + url+", "+tid+", "+policynumber+", "+policysource+", "+timestamp+", "+conntimeout+", "+readtimeout);
		JSONObject response = new JSONObject();
		APIConnection connection = null;
		String Scope = "api.idcard.create";

		try {
			connection = new APIConnection();

			String requestBody = formRequestBody(policynumber, policysource, timestamp);

			HashMap<String, String> headers = (HashMap<String, String>) getHeaders(Scope, tid,policysource, logger, region);
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

	private Map<String, String> getHeaders(String Scope, String tid, String policysource, Logger logger, String region) {

		Map<String, String> headers = null;
		try {
			Properties prop = new Properties();
	        prop.load(new FileInputStream(Constants.CONFIG_PATH));
	        
	        String client_id = prop.getProperty("Farmers.Mdm.Host.ClientID");
	        String client_secret = prop.getProperty("Farmers.Mdm.Host.ClientSecret");
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
			headers.put("frms_region", region);
			
			
			//TokenInvocationNonProd tokeninvocation = new TokenInvocationNonProd();
			TokenInvocationMDM tokeninvocation = new TokenInvocationMDM();
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
	

	public String formRequestBody(String policynumber, String policysource, String timestamp) {
		
		JSONObject reqBody = new JSONObject();
				JSONObject report = new JSONObject();
				report.put("policyNumber", policynumber);
				report.put("policySource", policysource);
				report.put("transactiontimestamp", timestamp);
				
				reqBody.put("report", report);
		
		return reqBody.toString();
	}
	
	
	public JSONObject formatErrorResponse(String error, int statusCode) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("responseCode", statusCode);
		jsonObject.put("responseMsg", error);
		return jsonObject;
	}
	  public static void main(String[] args) {
		  
	  LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);;
	  File file = new File(new StringBuffer("C:\\Servion\\FARMERS_SO_CVP\\Config\\").append("log4j2.xml").toString());
	  context.setConfigLocation(file.toURI());
		  
	  String url = "https://api-ss.farmersinsurance.com/cm/mlopssysms/v1/riskAttrition";
	  JSONObject resp = null; 
	  String tid = "test123"; 
	  String policynumber = "8421037960";
	  String policysource = "ARS";
	  String timestamp="";
	  RiskAttritionAPILookup_NP_Post test = new RiskAttritionAPILookup_NP_Post();
	  resp = test.start(url, tid, policynumber, policysource, timestamp, 10000, 10000, context, "QA");
	  } 
	 

}
