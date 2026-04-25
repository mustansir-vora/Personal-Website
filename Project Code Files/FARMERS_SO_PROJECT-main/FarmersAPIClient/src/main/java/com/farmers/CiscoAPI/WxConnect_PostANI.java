package com.farmers.CiscoAPI;

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
import com.farmers.APIUtil.stacktrace;
import com.farmers.FarmersAPI.APIConnection;
import com.farmers.bean.RequestBean;

public class WxConnect_PostANI {
	
	public  JSONObject start(String url, String tid, String ani, int conntimeout, int readtimeout, LoggerContext context) {

		Logger logger = context.getLogger(Constants.FARMERS_LOGGER_NAME);
		logger.info(tid+" : Parameters Passed from CVP : "+url+", "+tid+", "+ani+", "+conntimeout+", "+readtimeout);
		JSONObject response = new JSONObject();
		APIConnection connection = null;
		String Scope = "Cisco";

		try {
			connection = new APIConnection();

			String requestBody = formRequestBody(ani, tid, logger);

			HashMap<String, String> headers = (HashMap<String, String>) getHeaders(tid, logger);
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

	private  Map<String, String> getHeaders(String tid, Logger logger) {

		Map<String, String> headers = null;
		try {
			Properties prop = new Properties();
	        prop.load(new FileInputStream(Constants.CONFIG_PATH));
			
			headers = new HashMap<String, String>();
			headers.put("Key", prop.getProperty("CISCO.ISMOBILECALLER.KEY"));
			headers.put("Content-Type", "application/json");
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

	public  String formRequestBody(String ani, String tid, Logger logger) {

		JSONObject reqBody = new JSONObject();
		try {
			reqBody.put("Phone", ani);
		} catch (Exception e) {
			// Handle other exceptions
			logger.error(tid+" : Unexpected error occurred : " + e.getMessage());
			stacktrace.printStackTrace(logger, e);
			e.printStackTrace();
		}
		return reqBody.toString();

	}

	
	  public static void main(String[] args) {
	  
	  LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);;
	  File file = new File(new StringBuffer("C:\\Servion\\FARMERS_SO_CVP\\Config\\").append("log4j2.xml").toString());
	  context.setConfigLocation(file.toURI());
	  String url = "https://hooks.us.webexconnect.io/events/4TECZ8J7ZA";
	  JSONObject resp = null; 
	  String tid = "CiscoTest123"; 
	  String ani = "14084392410";
	  WxConnect_PostANI test = new WxConnect_PostANI();
	  resp = test.start(url, tid, ani, 10000, 10000 ,context);
	  }

}
