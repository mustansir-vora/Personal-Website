package com.farmers.FarmersAPI_NP;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONObject;

import com.farmers.APIUtil.Constants;
import com.farmers.APIUtil.Encrypt_Decrypt;
import com.farmers.APIUtil.stacktrace;

import com.farmers.bean.RequestBean;
//Created by balaji
public class MdmLinkAniLookup_NP_Post {
	
	//private static Logger logger = LogManager.getLogger(AccountLinkAniLookup_Post.class.getName());
	
	public JSONObject start(String url, String tid, String telephonenumber, int conntimeout, int readtimeout, LoggerContext context, String excludedSource, String region) {
		Logger logger = context.getLogger(Constants.FARMERS_LOGGER_NAME);
		logger.info("Start of the asynchronous method::",telephonenumber+":Method start time: "+LocalDateTime.now().toString());
         
		logger.info(tid + " : Parameters Passed from CVP : " + url + ", " + tid + ", " + telephonenumber + ", "
				+ conntimeout + ", " + readtimeout);
		JSONObject response = new JSONObject();
		APIConnection connection = null;
		String Scope = "api.agentinfo.read";
		logger.info(tid + " : Scope = " + Scope);
		ExecutorService executor = null;
		
		Properties prop = new Properties();
		
		try {
			
	        prop.load(new FileInputStream(Constants.CONFIG_PATH));
		}
		catch (Exception e) {
			logger.error(tid+" : Unexpected error occurred : " + e.getMessage());
			stacktrace.printStackTrace(logger, e);
			e.printStackTrace();
		}
        
		String threadCount = prop.getProperty("Farmers.MDM.ThreadCount");
		logger.info("Thread Count from Config :: " + threadCount);
		if (threadCount != null && !threadCount.isEmpty() && !threadCount.equalsIgnoreCase("")) {
			//logger.info("before If thread count::", threadCount);
		    executor = Executors.newFixedThreadPool(Integer.parseInt(threadCount));
		   // logger.info("after If thread count::", executor.toString());
		} else {
		    executor = Executors.newFixedThreadPool(500);
		    logger.info("else thread count::", executor.toString());
		}
	
		try {
			long asyncStartTime = System.currentTimeMillis(); 
			logger.info("START ASYNCHRONOUS: {}", asyncStartTime);

			CompletableFuture<JSONObject> MdmFuture = CompletableFuture.supplyAsync(() -> {
				return asyncMdmAniLookup(connection, telephonenumber, excludedSource, Scope, tid, logger, response, url,
						conntimeout, readtimeout, region);
			}, executor);

			JSONObject result = MdmFuture.join();

			long asyncEndTime = System.currentTimeMillis(); // Capture end time
			logger.info("END ASYNCHRONOUS: {}", asyncEndTime);
			logger.info("MDM Async API Execution Time: " + (asyncEndTime - asyncStartTime) + " milliseconds");

			return result;

		} finally {
	        logger.info("Initiating executor shutdown process...");
	        executor.shutdown();
	        try {
	            if (!executor.awaitTermination(20, TimeUnit.SECONDS)) {
	                logger.warn("Executor did not terminate in the specified time. Forcing shutdown...");
	                executor.shutdownNow();
	                logger.info("Executor forcibly shut down.");
	            } else {
	                logger.info("Executor terminated successfully.");
	            }
	        } catch (InterruptedException e) {
	            executor.shutdownNow();
	            logger.error("Executor was interrupted during shutdown", e);
	        }
	    }
		 
	}
	private JSONObject asyncMdmAniLookup(APIConnection connection,String telephonenumber,String excludedSource,String Scope,String tid,Logger logger,JSONObject response,String url,int conntimeout, int readtimeout, String region) {
		try {
			connection = new APIConnection();

			String requestBody = formRequestBody(telephonenumber,excludedSource,logger);

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
	        
	        String client_id = prop.getProperty("Farmers.Mdm.Host.ClientID");
	        String client_secret = prop.getProperty("Farmers.Mdm.Host.ClientSecret");
	        Encrypt_Decrypt Decryptor = new Encrypt_Decrypt();
	        client_id = Decryptor.decrypt(client_id);
	        client_secret = Decryptor.decrypt(client_secret);
	        
	        headers = new HashMap<String, String>();
	        headers.put("client_id", client_id);
			headers.put("client_secret", client_secret);
			headers.put("frms_source", prop.getProperty("Farmers.Host.frms_source"));
			headers.put("frms_appid", prop.getProperty("Farmers.Mdm.Host.frms_appid"));
			headers.put("frms_tid", tid);
			headers.put("frms_region", region);
			headers.put("Content-Type", "application/json");
			headers.put("criteria", "phonenumber");
			
			TokenInvocationMDM tokeninvocation = new TokenInvocationMDM();
			String access_token = tokeninvocation.getAccessToken(Scope, logger, tid);
			logger.info("Access token from invocation::", access_token);
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

	public  String formRequestBody(String telephonenumber,String excludedSource,Logger logger) {

		JSONObject searchCriteria = null;
		JSONObject reqBody = null;
		
		searchCriteria = new JSONObject();
		String mobileNumber = String.format("%s-%s-%s", 
				telephonenumber.substring(0, 3), 
				telephonenumber.substring(3, 6), 
				telephonenumber.substring(6, 10));
		
		if ((telephonenumber == null || telephonenumber.isEmpty() || telephonenumber.equalsIgnoreCase("")) 
				&& (excludedSource ==null || excludedSource.isEmpty() || excludedSource.equalsIgnoreCase(""))) {
			searchCriteria.put("phoneNumber", "default");
			searchCriteria.put("excludedSource","Life");
		}
		else {
			searchCriteria.put("phoneNumber", mobileNumber);
			searchCriteria.put("excludedSource",excludedSource);
		}
		reqBody = new JSONObject();
		reqBody.put("contacts", searchCriteria);
		logger.info(" : form request body  : "+reqBody);
		return reqBody.toString();

	}

	
	 public static void main(String[] args) {
	  
		  
		  LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);;
	      File file = new File(new StringBuffer("C:\\Servion\\FARMERS_SO_CVP_UAT_NP\\Config\\").append("log4j2.xml").toString());
	      context.setConfigLocation(file.toURI());
	  String url = "https://api-np-ss.farmersinsurance.com/PLE/plcyms/v3/policies?operation=searchByCriteria";
	  String telephonenumber = "832-422-3340"; String tid = "CiscoTest21"; JSONObject
	  resp = null;
	  
	  com.farmers.FarmersAPI_NP.MdmLinkAniLookup_NP_Post test = new com.farmers.FarmersAPI_NP.MdmLinkAniLookup_NP_Post(); resp =
	  test.start(url, tid, telephonenumber, 10000, 10000, context,"Life","UAT");

} 
}
