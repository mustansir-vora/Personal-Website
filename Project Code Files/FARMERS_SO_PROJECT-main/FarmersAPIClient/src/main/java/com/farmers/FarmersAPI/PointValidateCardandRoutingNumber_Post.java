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

public class PointValidateCardandRoutingNumber_Post {
	
//private Logger logger = LogManager.getLogger(Constants.LOGGER_NAME);
	
	public JSONObject start(String url, String tid, String accountnumberlength, String cardnumber, String routingnumber, String paymentmethodtype, int conntimeout, int readtimeout, LoggerContext context) {

		Logger logger = context.getLogger(Constants.FARMERS_LOGGER_NAME);
		logger.info(tid+" : Parameters Passed from CVP : " + url+", "+accountnumberlength+", "+cardnumber+", "+routingnumber+", "+paymentmethodtype+", "+conntimeout+", "+readtimeout);
		JSONObject response = new JSONObject();
		APIConnection connection = null;
		String Scope = "api.policyinfo.read";

		try {
			connection = new APIConnection();

			String requestBody = formRequestBody(accountnumberlength, cardnumber, routingnumber, paymentmethodtype);

			HashMap<String, String> headers = (HashMap<String, String>) getHeaders(Scope, tid, url, logger);
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

	private Map<String, String> getHeaders(String Scope, String tid, String url, Logger logger) {

		Map<String, String> headers = null;
		try {
			Properties prop = new Properties();
	        prop.load(new FileInputStream(Constants.CONFIG_PATH));
	        
	        String client_id = "";
	        String client_secret = "";
	        if (url.equalsIgnoreCase("https://api-np-ss.farmersinsurance.com/PLE/pymtms/v1/paymentType?operation=validateCardNumber") || url.equalsIgnoreCase("https://api-np-ss.farmersinsurance.com/PLE/pymtms/v1/paymentType?operation=validateRoutingNumber")) {
	        	client_id = prop.getProperty("Farmers.Host.NonProd.ClientID");
		        client_secret = prop.getProperty("Farmers.Host.NonProd.ClientSecret");
			}
	        else {
	        	client_id = prop.getProperty("Farmers.Host.ClientID");
		        client_secret = prop.getProperty("Farmers.Host.ClientSecret");
	        }
	        
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
			headers.put("frms_brand", "21C");
			
			String access_token = null;
			if (url.equalsIgnoreCase("https://api-np-ss.farmersinsurance.com/PLE/pymtms/v1/paymentType?operation=validateCardNumber") || url.equalsIgnoreCase("https://api-np-ss.farmersinsurance.com/PLE/pymtms/v1/paymentType?operation=validateRoutingNumber")) {
				TokenInvocationNonProd tokenivocation = new TokenInvocationNonProd();
				access_token = tokenivocation.getAccessToken(Scope, logger, tid);
			}
			else {
				TokenInvocation tokeninvocation = new TokenInvocation();
				access_token = tokeninvocation.getAccessToken(Scope, logger, tid);
			}
			
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
	

	public String formRequestBody(String accountnumberlength, String cardnumber, String routingnumber, String paymentmethodtype) {

		JSONObject paymentmethod = null;
		JSONObject reqBody = null;
		try {
			
			paymentmethod = new JSONObject();
			paymentmethod.put("accountNumberLength", accountnumberlength);
			paymentmethod.put("cardNumber", cardnumber);
			paymentmethod.put("routingNumber", routingnumber);
			paymentmethod.put("paymentMethodType", paymentmethodtype);
			
			reqBody = new JSONObject();
			reqBody.put("paymentMethod", paymentmethod);

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
	
	
	  public static void main(String[] args) {
	  LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);;
	  File file = new File(new StringBuffer("C:\\Servion\\FARMERS_SO_CVP\\Config\\").append("log4j2.xml").toString());
	  context.setConfigLocation(file.toURI());
	      
	  String url = "https://api-ss.farmersinsurance.com/pymtms/v1/paymentType?operation=validateCardNumber";
	  JSONObject resp = null; 
	  String tid = "CiscoTest"; 
	  String accountnumber = null;
	  String cardnumber = "4147202591935687";
	  String routingnumber = null;
	  String paymentmethodtype = "CC";
	  
	  PointValidateCardandRoutingNumber_Post test = new PointValidateCardandRoutingNumber_Post();
	  resp = test.start(url, tid, accountnumber, cardnumber, routingnumber, paymentmethodtype, 10000, 10000, context); //System.out.println("final response = "+resp); }
	 
	  }
}
