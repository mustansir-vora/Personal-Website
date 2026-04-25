package com.farmers.FarmersAPI;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONObject;

import com.farmers.APIUtil.Constants;
import com.farmers.APIUtil.Encrypt_Decrypt;
import com.farmers.APIUtil.stacktrace;
import com.farmers.bean.RequestBean;

public class FNWLEPCPaymentus_Post {
	
	public JSONObject start(String url, String tid, String operation, String id, String authtoken, String languagepreference, Double balanceDue, String policySource, String existingCustomer, String firstName, String lastName, long phoneNumber, String email, String line1, String state, String city, String zip, String country, int conntimeout, int readtimeout, LoggerContext context) {

		Logger logger = context.getLogger(Constants.FARMERS_LOGGER_NAME);
		logger.info(tid+" : Parameters Passed from CVP :: URL =  " + url + ", call id = " + tid + ", operation = " + operation + ", id = " + id + ", authToken = " + authtoken + ", languagepreference = " + languagepreference + ", balanceDue = " + balanceDue + ", policySource = " + policySource + ", existingCustomer = " + existingCustomer + ", firstname = " + firstName + ", lastname = " + lastName + ", phonenumber = " + phoneNumber + ", email = " + email + ", line1 = " + line1 + ", state = " + state + ", city = " + city + ", zip = " + zip + ", country = " + country + ", connTimeOut = " + conntimeout + ", readTimeOut = " + readtimeout);
		JSONObject response = new JSONObject();
		APIConnection connection = null;
		String Scope = "api.payment.create";

		try {
			connection = new APIConnection();
			String requestBody = formRequestBody(operation, id, authtoken, languagepreference, balanceDue, policySource, existingCustomer, firstName, lastName, phoneNumber, email, line1, state, zip, city, country);
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
			headers.put("frms_source", prop.getProperty("Farmers.Host.frms_source"));
			headers.put("frms_appid", prop.getProperty("Farmers.Host.frms_appid"));
			headers.put("frms_tid", tid);
			headers.put("frms_region", prop.getProperty("Farmers.Host.frms_region"));
			headers.put("Content-Type", "application/json");
			headers.put("frms_brand", prop.getProperty("Farmers.FNWL.frms_brand"));
			
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
	

	public  String formRequestBody(String operation, String id, String authtoken, String languagepreference, Double balanceDue, String policySource, String existingCustomer, String firstName, String lastName, long phoneNumber, String email, String line1, String state, String zip, String city, String country) {

		JSONObject address = null;
		JSONObject customer = null;
		JSONObject callinfo = null;
		JSONObject reqBody = null;
		
		try {
			
			address = new JSONObject();
			address.put("line1", line1);
			address.put("state", state);
			address.put("zipCode", zip);
			address.put("city", city);
			address.put("country", country);
			
			customer = new JSONObject();
			customer.put("firstName", firstName);
			customer.put("lastName", lastName);
			customer.put("phoneNumber", phoneNumber);
			customer.put("email", email);
			customer.put("address", address);
			
			callinfo = new JSONObject();
			callinfo.put("operation", operation);
			callinfo.put("id", id);
			callinfo.put("authToken", authtoken);
			callinfo.put("languagePreference", languagepreference);
			callinfo.put("balanceDueAmount", balanceDue);
			callinfo.put("policySource", policySource);
			callinfo.put("existingCustomerInd", existingCustomer);
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

}
