package com.farmers.AdminAPI;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONObject;

import com.farmers.APIUtil.Constants;
import com.farmers.APIUtil.stacktrace;
import com.farmers.bean.RequestBean;

public class InsertCallDataToQueue {
	
	public JSONObject start(String url, String callid, JSONObject input, int conntimeout, int readtimeout, LoggerContext context) {
		Logger logger = context.getLogger(Constants.ADMIN_LOGGER_NAME);
		logger.info(callid + " : Params passed = "+url+" ,"+input+", "+conntimeout+", "+readtimeout);
		JSONObject resp = new JSONObject();
		
		HashMap<String, String> headers = (HashMap<String, String>) getHeaders(logger, callid);
		try {
			RequestBean requestBean = new RequestBean(url, input.toString(), "POST", headers);
			Connection connection = new Connection();
			logger.info(callid+" : Headers = "+headers);
			resp = connection.sendHttpsRequest(requestBean, logger, callid, conntimeout, readtimeout);
			
			logger.info(callid+" : Final Response = "+resp);
		}
		catch (Exception e) {
            // Handle other exceptions
            logger.error(callid+" : Exception :"+e);
            stacktrace.printStackTrace(logger, e);
        }
		return resp;
	}
	
	
	private Map<String, String> getHeaders(Logger logger, String callid) {

		Map<String,String> headers = null;
		try {
			headers = new HashMap<String, String>();
			headers.put("Content-Type", "application/json");

			TokenGeneration accesstokeninstance = new TokenGeneration();
			String access_token = accesstokeninstance.getAccessToken(logger, callid);
			
			if (null != access_token) {
				headers.put("Authorization", "Bearer " + access_token);
			} else {
				headers.put("TOKEN_ERROR", "true");
			}
			logger.info(callid+" : Headers : "+headers);
		} catch (Exception e) {
            // Handle other exceptions
            logger.error(callid+" : Exception :"+e);
            stacktrace.printStackTrace(logger, e);
        }
		return headers;
	}
	
	/*  public static void main(String args[]) {
		  
	  LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);;
      File file = new File(new StringBuffer("C:\\Servion\\FARMERS_SO_CVP\\Config\\").append("log4j2.xml").toString());
      context.setConfigLocation(file.toURI());
		  
	  String url = "http://10.201.33.196/MSMQAPI/api/MSMQ/InsertCallDataToQueue"; 
	  JSONObject input = new JSONObject();
	  String callid = "test123";
	  
	  InsertCallDataToQueue objInsertCallDataToQueue = new InsertCallDataToQueue();
	  JSONObject resp = new JSONObject();
	  resp = objInsertCallDataToQueue.start(url, callid, input, 0, 0, context);
	  } */

}
