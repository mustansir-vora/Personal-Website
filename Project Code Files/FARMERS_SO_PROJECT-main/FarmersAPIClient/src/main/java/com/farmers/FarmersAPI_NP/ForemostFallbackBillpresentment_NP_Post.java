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

public class ForemostFallbackBillpresentment_NP_Post {
	
public JSONObject start(String url, String tid, String applicationtype, String systemname, boolean loopback, String policynumber, String productgroupname, int conntimeout, int readtimeout, LoggerContext context, String region) {
		
		Logger logger = context.getLogger(Constants.FARMERS_LOGGER_NAME);
		logger.info(tid+" : Parameters Passed from CVP : "+url+", "+tid+", "+applicationtype+", "+systemname+", "+loopback+", "+policynumber+", "+productgroupname+", "+conntimeout+", "+readtimeout);
		JSONObject response = new JSONObject();
		APIConnection connection = null;
		String Scope = "api.billinginfo.read";

		try {
			connection = new APIConnection();

			String requestBody = formRequestBody(applicationtype, systemname, loopback, policynumber, productgroupname);

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

	private  Map<String, String> getHeaders(String Scope, String tid, Logger logger, String region) {

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
			headers.put("frms_source", prop.getProperty("Farmers.Host.frms_source"));
			headers.put("frms_appid", prop.getProperty("Farmers.Host.frms_appid"));
			headers.put("frms_tid", tid);
			headers.put("frms_region", region);
			headers.put("Content-Type", "application/json");
			headers.put("client_id", client_id);
			headers.put("client_secret", client_secret);
			
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

	public  JSONObject formatErrorResponse(String error, int statusCode) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("responseCode", statusCode);
		jsonObject.put("responseMsg", error);
		return jsonObject;
	}

	@SuppressWarnings("unchecked")
	public  String formRequestBody(String applicationtype, String systemname, boolean loopback, String policynumber, String productgroupname) {

		JSONObject requestHeader = new JSONObject();
		JSONObject anchorContractSearchCriteria = new JSONObject();
		JSONObject retrieveBillingSummaryRequest = new JSONObject();
		JSONObject reqBody = new JSONObject();
		try {
			if (policynumber == null || policynumber.isEmpty() || policynumber.equalsIgnoreCase("")) {
				anchorContractSearchCriteria.put("policyNumber", "default");
			}
			else {
				anchorContractSearchCriteria.put("policyNumber", policynumber);
			}
			anchorContractSearchCriteria.put("productGroupName", productgroupname);
			requestHeader.put("applicationType", applicationtype);
			requestHeader.put("systemName", systemname);
			requestHeader.put("loopback", loopback);
			
			retrieveBillingSummaryRequest.put("anchorContractSearchCriteria", anchorContractSearchCriteria);
			retrieveBillingSummaryRequest.put("requestHeader", requestHeader);
			
			reqBody.put("retrieveBillingSummaryRequest", retrieveBillingSummaryRequest);

		} catch (Exception e) {

		}
		return reqBody.toString();

	}
	
	  public static void main(String[] args) {
		  
		  LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);;
	      File file = new File(new StringBuffer("C:\\Servion\\FARMERS_SO_CVP_UAT_NP\\Config\\").append("log4j2.xml").toString());
	      context.setConfigLocation(file.toURI());
		  
	  String url = "https://api-ss.farmersinsurance.com/billing/v1/eProxy/services/billpresentment";
	  JSONObject resp = null; 
	  String tid = "CiscoTest";
	  String applicationtype = "VRS-Foremost-101";
	  String systemname = "VRS-Foremost";
	  String policynumber = "5014764413";
	  boolean loopback = false;
	  String productgroupname = "FAB";
	  
	  ForemostFallbackBillpresentment_NP_Post test = new ForemostFallbackBillpresentment_NP_Post(); 
	  resp = test.start(url, tid, applicationtype, systemname, loopback, policynumber, productgroupname, 10000, 10000, context,"uat");
	  
	  
	  }  

}
