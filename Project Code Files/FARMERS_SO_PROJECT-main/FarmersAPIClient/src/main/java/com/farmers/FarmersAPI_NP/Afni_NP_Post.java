package com.farmers.FarmersAPI_NP;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONObject;

import com.farmers.APIUtil.Constants;
import com.farmers.APIUtil.stacktrace;
import com.farmers.bean.RequestBean;

public class Afni_NP_Post {
	
//private Logger logger = LogManager.getLogger(Constants.LOGGER_NAME);
	
	public  JSONObject start(String url, String tid, String callerani, String policynumber,String accountnumber,String originaldnis,String originaldnisdescription,String zipcode, String quotenumber, String state, int conntimeout, int readtimeout, LoggerContext context, String region) {
		
		Logger logger = context.getLogger(Constants.FARMERS_LOGGER_NAME);
		logger.info(tid+" : Parameters Passed from CVP : "+url+", "+tid+", "+callerani+", "+policynumber+", "+accountnumber+", "+originaldnis+", "+originaldnisdescription+", "+zipcode+", "+quotenumber+", "+state+", "+conntimeout+", "+readtimeout+","+region);
		JSONObject response = new JSONObject();
		APIConnection connection = null;
		String Scope = "afni";

		try {
			connection = new APIConnection();

			String requestBody = formRequestBody(callerani, policynumber, tid, accountnumber, originaldnis, originaldnisdescription, zipcode, quotenumber, state, logger);

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

	private  Map<String, String> getHeaders(String Scope, String tid, Logger logger, String region) {

		Map<String, String> headers = null;
		try {
			
			headers = new HashMap<String, String>();
			headers.put("Content-Type", "application/json");
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
			logger.info(tid+" : Unexpected error occurred : " + e.getMessage());
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
	public String formRequestBody(String callerani, String policynumber, String ivrcallid, String accountnumber, String originaldnis, String originaldnisdescription, String zipcode, String quotenumber, String state, Logger logger) {

		JSONObject reqBody = null;
		try {
			reqBody = new JSONObject();
			reqBody.put("ANI", callerani);
			reqBody.put("PolicyNumber", policynumber);
			reqBody.put("IVRCallID", ivrcallid);
			reqBody.put("AccountNumber", accountnumber);
			reqBody.put("OriginalDNIS", originaldnis);
			reqBody.put("OriginalDNISDescription", originaldnisdescription);
			reqBody.put("ZipCode", zipcode);
			reqBody.put("QuoteNumber", quotenumber);
			reqBody.put("State", state);
			

		} catch (Exception e) {
			logger.info(ivrcallid+" : Unexpected error occurred : " + e.getMessage());
			stacktrace.printStackTrace(logger, e);
			e.printStackTrace();
		}
		return reqBody.toString();

	}
	
	private boolean isExpired(Date storedTimestamp) {
        long timeElapsed = new Date().getTime() - storedTimestamp.getTime();
        //System.out.println("Date Time difference : " +timeElapsed+ "stored date : "+storedTimestamp);
        return timeElapsed > 3600000; // Expire after 1 hour
        //return timeElapsed > 4000; // Expire after 4 seconds
    }
	

	
	  public static void main(String[] args) {
	  
		  LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);;
	      File file = new File(new StringBuffer("C:\\Servion\\FARMERS_SO_CVP_UAT_NP\\Config\\").append("log4j2.xml").toString());
	      context.setConfigLocation(file.toURI());
		  
	  String url = "https://apigw-pod2.dm-us.informaticacloud.com/t/afni.com/CallDetailCapture";
	  String region ="uat";
	  JSONObject resp = null; 
	  String tid = "CiscoTest21";
	  
	  
	  Afni_NP_Post test = new Afni_NP_Post();
	  resp = test.start(url, tid, "5122333198", "", "", "15541452123", "", "", "", "", 10000, 10000, context, region); //
	  
	  
	  } 
	 

}
