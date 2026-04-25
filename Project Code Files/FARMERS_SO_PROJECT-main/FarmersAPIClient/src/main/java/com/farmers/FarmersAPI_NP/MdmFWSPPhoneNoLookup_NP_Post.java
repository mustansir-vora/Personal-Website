package com.farmers.FarmersAPI_NP;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
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

public class MdmFWSPPhoneNoLookup_NP_Post {
	
	//private final Logger logger = LogManager.getLogger(Constants.LOGGER_NAME);

	public JSONObject start(String url, String tid, String telephonenumber, String excludedSource, int conntimeout, int readtimeout, LoggerContext context, String region) {

		Logger logger = context.getLogger(Constants.FARMERS_LOGGER_NAME);
		logger.info(tid+" : Parameters Passed from CVP : "+url+", "+tid+", "+telephonenumber+", "+excludedSource+", "+conntimeout+", "+readtimeout);
		JSONObject response = new JSONObject();
		APIConnection connection = null;
		String Scope = "api.policyinfo.read";

		try {
			connection = new APIConnection();

			String requestBody = formRequestBody(telephonenumber,excludedSource);

			HashMap<String, String> headers = (HashMap<String, String>) getHeaders(Scope, tid, logger, region);
			if (null != headers && headers.containsKey("TOKEN_ERROR")) {
				response = formatErrorResponse("OAUTH TOKEN_ERROR", 1);
			} else {
				RequestBean requestBean = new RequestBean(url, requestBody, "POST", headers);
				response = connection.sendHttpRequest(requestBean, logger, tid, conntimeout, readtimeout);
				logger.info(tid+" : Final Response = "+response);
			}
			//Thread.sleep(10000);
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
			headers.put("client_secret", client_secret);
			headers.put("client_id", client_id);
			headers.put("frms_appid", prop.getProperty("Farmers.Host.frms_appid"));
			headers.put("frms_source", prop.getProperty("Farmers.Host.frms_source"));
			headers.put("frms_tid", tid);
			headers.put("frms_region", region);
			headers.put("criteria", "phonenumber");
			headers.put("frms_choiceinfo", "Y");
			headers.put("Content-Type", "application/json");
			//headers.put("frms_brand", "FWS");
			

			
			// access_token = new String();
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

	public String formRequestBody(String telephonenumber,String excludedSource) {

		JSONObject Criteria = null;
		JSONObject reqBody = null;
		try {

			Criteria = new JSONObject();
			String mobileNumber = String.format("%s-%s-%s", 
					telephonenumber.substring(0, 3), 
					telephonenumber.substring(3, 6), 
					telephonenumber.substring(6, 10));
			
			if ((telephonenumber != null && !telephonenumber.equalsIgnoreCase("")) && !(excludedSource.equalsIgnoreCase("NULL") && "".equalsIgnoreCase(excludedSource))) {
				
				Criteria.put("phoneNumber", mobileNumber);
				Criteria.put("excludedSource", excludedSource);
			}
			
			else {
				Criteria.put("phoneNumber", "default");
				Criteria.put("excludedSource", "Life");
			}
			
			
			reqBody = new JSONObject();
			reqBody.put("contacts", Criteria);

		} catch (Exception e) {

		}
		return reqBody.toString();

	}

	
	  public static void main(String[] args) {
	  
		  LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);;
	      File file = new File(new StringBuffer("C:\\Servion\\FARMERS_SO_CVP\\Config\\").append("log4j2.xml").toString());
	      context.setConfigLocation(file.toURI());
		  
	  String url = "https://api-np-ss.farmersinsurance.com/PLE/plcyms/v3/policies?operation=searchByCriteria";
	  String tid = "Ciscotest";
	  String telephoneNumber = "832-422-3340";
	  String excludedSource="Life";
	  JSONObject resp = null;
	  
	  MdmFWSPPhoneNoLookup_NP_Post test = new MdmFWSPPhoneNoLookup_NP_Post();
	  //resp = test.start(url, tid, telephoneNumber, excludedSource, 18000, 18000, context);
	  
	  try {
	  
		  Encrypt_Decrypt ED= new Encrypt_Decrypt();
		  
		  System.out.println(ED.encrypt("11D6e1F7bbaA40c4A0F2c467aF11522C"));
		  //Non Prod encrypted clinet_id : o+el930eoFkgimKeD0NTJS93yIQY+ZX+tuXS6K2pD5DFR9WbK/Giuw==
		  //Non Prod encrypted client_secret : 8LRwiACKfrLjp9FjnAWbG+w1fgUP/GO2yDVWMq0bIVXFR9WbK/Giuw==

	  }
	  
	  catch(Exception e) {
		  
	  }
	  }
	 

}
