package com.farmers.FarmersAPI;

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

public class PolicyInquiry_RetrieveBillingDetailsByPolicyNumber_Post {
	
	//private Logger logger = LogManager.getLogger(Constants.LOGGER_NAME);

	public JSONObject start(String url, String tid, String functionname, String agreementmode,String agreementsymbol, String policynumber, String postalcode, int conntimeout, int readtimeout, LoggerContext context) {

		Logger logger = context.getLogger(Constants.FARMERS_LOGGER_NAME);
		logger.info(tid+" : Parameters Passed from CVP : "+url+", "+tid+", "+functionname+", "+agreementmode+", "+agreementsymbol+", "+policynumber+", "+", "+conntimeout+", "+readtimeout);
		JSONObject response = new JSONObject();
		APIConnection connection = null;
		String Scope = "api.agentinfo.read";

		try {
			connection = new APIConnection();

			String requestBody = formRequestBody(functionname, policynumber, agreementmode, agreementsymbol, postalcode);

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

	private Map<String, String> getHeaders(String Scope, String tid, Logger logger) {

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
			headers.put("frms_target", "BW");
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

	public JSONObject formatErrorResponse(String error, int statusCode) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("responseCode", statusCode);
		jsonObject.put("responseMsg", error);
		return jsonObject;
	}

	public String formRequestBody(String functionname, String policynumber, String agreementmode,String agreementsymbol, String postalcode) {

		JSONObject searchCriteria = null;
		JSONObject reqBody = null;
		try {
			
			if (functionname.equalsIgnoreCase("general")) {
				searchCriteria = new JSONObject();
				searchCriteria.put("agreementMode", agreementmode);
				searchCriteria.put("agreementSymbol", agreementsymbol);
				
				if (policynumber == null || policynumber.isEmpty() || policynumber.equalsIgnoreCase("")) {
					searchCriteria.put("policyNumber", "default");
				}
				else{
					searchCriteria.put("policyNumber", policynumber);
				}
				
				reqBody = new JSONObject();
				reqBody.put("searchCriteria", searchCriteria);
			}
			else {
				searchCriteria = new JSONObject();
				searchCriteria.put("postalCode", postalcode);
				searchCriteria.put("agreementSymbol", agreementsymbol);
				
				if (policynumber == null || policynumber.isEmpty() || policynumber.equalsIgnoreCase("")) {
					searchCriteria.put("policyNumber", "default");
				}
				else{
					searchCriteria.put("policyNumber", policynumber);
				}
				
				reqBody = new JSONObject();
				reqBody.put("searchCriteria", searchCriteria);
			}
		} catch (Exception e) {

		}
		return reqBody.toString();

	}

	
	  public static void main(String[] args) {
	  
		  LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);;
	      File file = new File(new StringBuffer("C:\\Servion\\FARMERS_SO_CVP\\Config\\").append("log4j2.xml").toString());
	      context.setConfigLocation(file.toURI());
		  
	  
	  String url = "https://api-ss.farmersinsurance.com/searchpolicyms/v1/billingdetails?operation=retrieveByPolicyNumber"; // This URL is for General Lookup
	  //String url = "https://api-ss.farmersinsurance.com/searchpolicyms/v1/billingdetails?operation=retrieveByPolicyNumberForPayment"; //This URL is for Payment Lookup
	  JSONObject resp = null; 
	  String tid = "CiscoTest"; 
	  //String functionname = "general"; // For General lookup
	  String functionname = "payment"; // For Payment Lookup
	  String policynumber = "9666106";
	  String agreementmode = "09";
	  String agreementsymbol = "G00";
	  String postalcode = "32224";
	  PolicyInquiry_RetrieveBillingDetailsByPolicyNumber_Post test = new PolicyInquiry_RetrieveBillingDetailsByPolicyNumber_Post();
	  
	  resp = test.start(url, tid, functionname, agreementmode, agreementsymbol, policynumber, postalcode, 10000, 10000, context); 
	  }
	 

}
