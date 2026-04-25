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

public class PolicyInquiry_RetrieveInsurancePoliciesbyParty_NP_Post {
	
	//private Logger logger = LogManager.getLogger(Constants.LOGGER_NAME);
	
	public JSONObject start(String url, String tid, String telephonenumber, int conntimeout, int readtimeout, LoggerContext context, String region) {

		Logger logger = context.getLogger(Constants.FARMERS_LOGGER_NAME);
		logger.info(tid+" : Parameters Passed from CVP : " + url+", "+telephonenumber+", "+conntimeout+", "+readtimeout);
		JSONObject response = new JSONObject();
		APIConnection connection = null;
		String Scope = "api.policyinfo.read";

		try {
			connection = new APIConnection();

			String requestBody = formRequestBody(telephonenumber);

			HashMap<String, String> headers = (HashMap<String, String>) getHeaders(Scope, tid, logger, region);
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
			headers.put("frms_target", "BW");
			headers.put("frms_region",region);
			
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
	

	public String formRequestBody(String telephonenumber) {

		JSONObject searchCriteria = null;
		JSONObject reqBody = null;
		try {
			
			searchCriteria = new JSONObject();
			
			if (telephonenumber == null || telephonenumber.isEmpty() || telephonenumber.equalsIgnoreCase("")) {
				searchCriteria.put("telephoneNumber", "default");
			}
			else {
				searchCriteria.put("telephoneNumber", telephonenumber);
			}
			
			reqBody = new JSONObject();
			reqBody.put("searchCriteria", searchCriteria);

		} catch (Exception e) {

		}
		return reqBody.toString();

	}
	
	
	public JSONObject formatErrorResponse(String error, int statusCode) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("responseCode", statusCode);
		jsonObject.put("responseMsg", error);
		return jsonObject;
	}
	
	
	 /* public static void main(String[] args) {
	  
		  LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);;
	      File file = new File(new StringBuffer("C:\\Servion\\FARMERS_SO_CVP_UAT_NP\\Config\\").append("log4j2.xml").toString());
	      context.setConfigLocation(file.toURI());
	  String url = "https://api-ss.farmersinsurance.com/searchpolicyms/v1/household"; JSONObject
	  reqbody = new JSONObject();
	  reqbody.put("telephonenumber", "509-362-0218");
	  reqbody.put("dateofbirth", "05021964"); 
	  reqbody.put("postalcode", "99205");
	  JSONObject resp = null; 
	  String tid = "CiscoTest";
	  PolicyInquiry_RetrieveInsurancePoliciesbyParty_Post test = new PolicyInquiry_RetrieveInsurancePoliciesbyParty_Post();
	  resp = test.start(url, tid, "5555555555", 10000, 10000, context); 
	  } */
	 

}
