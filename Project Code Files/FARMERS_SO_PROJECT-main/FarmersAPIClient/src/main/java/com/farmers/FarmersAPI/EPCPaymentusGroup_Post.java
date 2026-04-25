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

public class EPCPaymentusGroup_Post {
	
	//private Logger logger = LogManager.getLogger(Constants.LOGGER_NAME);
	
	public  JSONObject start(String url, String tid, String operation, String id, String authtoken, String languagepreference, String brandcode, String firstname, String lastname, long phonenumber, String email, String addressline1, String state, String zipcode, String city, String country, int conntimeout, int readtimeout, LoggerContext context) {

		Logger logger = context.getLogger(Constants.FARMERS_LOGGER_NAME);
		logger.info(tid+" : Parameters Passed from CVP : " +url+", "+tid+", "+operation+", "+id+", "+authtoken+", "+languagepreference+", "+brandcode+", "+firstname+", "+lastname+", "+phonenumber+", "+email+", "+addressline1+", "+state+", "+zipcode+", "+city+", "+country);
		JSONObject response = new JSONObject();
		APIConnection connection = null;
		String Scope = "api.payment.create";

		try {
			connection = new APIConnection();
			String requestBody = formRequestBody(operation, id, authtoken, languagepreference, brandcode, firstname, lastname, phonenumber, email, addressline1, state, zipcode, city, country);
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

	private  Map<String, String> getHeaders(String Scope, String tid, Logger logger) {

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
			headers.put("frms_region", prop.getProperty("Farmers.Host.frms_region"));
			headers.put("frms_target", "SRM");
			headers.put("frms_ipadress", "0.0.0.0");
			
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
	

	public  String formRequestBody(String operation, String id, String authtoken, String languagepreference, String brandcode, String firstname, String lastname, long phonenumber, String email, String addressline1, String state, String zipcode, String city, String country) {

		JSONObject address = null;
		JSONObject customer = null;
		JSONObject callinfo = null;
		JSONObject reqBody = null;
		
		try {
			
			address = new JSONObject();
			address.put("line1", addressline1);
			address.put("state", state);
			address.put("zipCode", zipcode);
			address.put("city", city);
			address.put("country", country);
			
			customer = new JSONObject();
			customer.put("firstName", firstname);
			customer.put("lastName", lastname);
			customer.put("phoneNumber", phonenumber);
			customer.put("email", email);
			customer.put("address", address);
			
			callinfo = new JSONObject();
			callinfo.put("operation", operation);
			callinfo.put("id", id);
			callinfo.put("authToken", authtoken);
			callinfo.put("languagePreference", languagepreference);
			callinfo.put("brandCode", brandcode);
			callinfo.put("customer", customer);
			
			reqBody = new JSONObject();
			reqBody.put("callInfo", callinfo);
			
		} catch (Exception e) {

		}
		return reqBody.toString();

	}
	
	public  JSONObject formatErrorResponse(String error, int statusCode) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("responseCode", statusCode);
		jsonObject.put("responseMsg", error);
		return jsonObject;
	}
	
	
	  public static void main(String[] args) {
	  
		  LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);;
	      File file = new File(new StringBuffer("C:\\Servion\\FARMERS_SO_CVP\\Config\\").append("log4j2.xml").toString());
	      context.setConfigLocation(file.toURI());
		  
	  String url = "https://api-ss.farmersinsurance.com/ivrms/v1/calls/0077785084?idType=accountNumber&operation=paymenttransfer";
	  String operation = "SALE";
	  String id = "0077785084";
	  String authtoken = "778339308";
	  String languagepreference = "en";
	  String brandcode = "FM";
	  String firstname = "";
	  String lastname = "";
	  long phonenumber = 4088354360L; 
	  String email = "";
	  String addressline1 = "";
	  String state = "";
	  String zipcode = "778339308";
	  String city = "";
	  String country = "";
	  String tid = "CiscoTest"; 
	  
	  EPCPaymentusGroup_Post test = new EPCPaymentusGroup_Post();
	  JSONObject resp = null; 
	  resp = test.start(url, tid, operation, id, authtoken, languagepreference, brandcode, firstname, lastname, phonenumber, email, addressline1, state, zipcode, city, country, 10000, 10000, context);
	  }
	 

}
