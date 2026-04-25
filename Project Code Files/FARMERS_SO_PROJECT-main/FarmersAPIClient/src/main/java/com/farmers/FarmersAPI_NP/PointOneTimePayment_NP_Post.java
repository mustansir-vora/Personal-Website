package com.farmers.FarmersAPI_NP;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONObject;

import com.farmers.APIUtil.Constants;
import com.farmers.APIUtil.Encrypt_Decrypt;
import com.farmers.APIUtil.stacktrace;
import com.farmers.bean.RequestBean;

public class PointOneTimePayment_NP_Post {

//private Logger logger = LogManager.getLogger(Constants.LOGGER_NAME);
	
	public JSONObject start(String url, String tid, String paymentamount, String firstname, String middlename, String lastname, String address, String city, String postalcode, String state, String phonenumber, String paymentmethodtypetype, String bankaccountnumber, String bankaccounttype, String routingnumber, String cardnumber, String cardexpirationdate, String cvvnumber, String additionalchargetype, String additionalchargeamount, String paymentid, int conntimeout, int readtimeout, LoggerContext context, String region) {

		Logger logger = context.getLogger(Constants.FARMERS_LOGGER_NAME);
		logger.info(tid+" : Parameters Passed from CVP : " + url+", "+paymentamount+", "+firstname+", "+middlename+", "+lastname+", "+", "+address+", "+city+", "+postalcode+", "+state+", "+phonenumber+", "+paymentmethodtypetype+", "+bankaccountnumber+", "+bankaccounttype+", "+routingnumber+", "+cardnumber+", "+cardexpirationdate+", "+cvvnumber+", "+additionalchargetype+", "+additionalchargeamount+", "+paymentid+", "+conntimeout+", "+readtimeout);
		JSONObject response = new JSONObject();
		APIConnection connection = null;
		String Scope = "api.policyinfo.read";

		try {
			connection = new APIConnection();

			String requestBody = formRequestBody(paymentamount, firstname, middlename, lastname, address, city, postalcode, state, phonenumber, paymentmethodtypetype, bankaccountnumber, bankaccounttype, routingnumber, cardnumber, cardexpirationdate, cvvnumber, additionalchargetype, additionalchargeamount, paymentid);
			
			HashMap<String, String> headers = (HashMap<String, String>) getHeaders(Scope, tid, url, logger, region);
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

	private Map<String, String> getHeaders(String Scope, String tid, String url, Logger logger, String region) {

		Map<String, String> headers = null;
		try {
			Properties prop = new Properties();
	        prop.load(new FileInputStream(Constants.CONFIG_PATH));
	        
	        String client_id = "";
	        String client_secret = "";
	        
	       
	        client_id = prop.getProperty("Farmers.Host.NonProd.ClientID");
		    client_secret = prop.getProperty("Farmers.Host.NonProd.ClientSecret");
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
			headers.put("frms_region",region);
			String access_token = null;

			TokenInvocationNonProd tokenivocation = new TokenInvocationNonProd();
			access_token = tokenivocation.getAccessToken(Scope, logger, tid);
		
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
	

	public String formRequestBody(String paymentamount, String firstname, String middlename, String lastname, String address, String city, String postalcode, String state, String phonenumber, String paymentmethodtypetype, String bankaccountnumber, String bankaccounttype, String routingnumber, String cardnumber, String cardexpirationdate, String cvvnumber, String additionalchargetype, String additionalchargeamount, String paymentid) {
		
		JSONObject payee = new JSONObject();
		JSONObject paymentmethod = new JSONObject();
		JSONObject additionalcharge = new JSONObject();
		
		payee.put("firstName", firstname);
		payee.put("middleName", middlename);
		payee.put("lastName", lastname);
		payee.put("address", address);
		payee.put("city", city);
		payee.put("postalCode", postalcode);
		payee.put("state", state);
		payee.put("phoneNumber", phonenumber);
		
		paymentmethod.put("type", paymentmethodtypetype);
		paymentmethod.put("bankAccountNumber", bankaccountnumber);
		paymentmethod.put("bankAccountType", bankaccounttype);
		paymentmethod.put("routingNumber", routingnumber);
		paymentmethod.put("cardNumber", cardnumber);
		paymentmethod.put("cardExpirationDate", cardexpirationdate);
		paymentmethod.put("cvvNumber", cvvnumber);
		
		additionalcharge.put("type", additionalchargetype);
		additionalcharge.put("amount", additionalchargeamount);
		
		List<Map<String, String>> payments = new ArrayList<Map<String, String>>();
		Map<String, String> list = new HashMap<String, String>();
		list.put("paymentAmount", paymentamount);
		list.put("payee", payee.toString());
		list.put("paymentMethod", paymentmethod.toString());
		list.put("additionalCharge", additionalcharge.toString());
		list.put("paymentId", paymentid);
		
		payments.add(list);
		
		JSONObject reqBody = new JSONObject();
		
		reqBody.put("payments", payments);
		
		return reqBody.toString();

	}
	
	
	public JSONObject formatErrorResponse(String error, int statusCode) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("responseCode", statusCode);
		jsonObject.put("responseMsg", error);
		return jsonObject;
	}
	
	/*
	 * public void main(String[] args) {
	 * 
	 * String url = "https://api-ss.farmersinsurance.com/pymntms/v1/paymentType?operation=validateCardNumber";
	 * JSONObject reqbody = new JSONObject(); reqbody.put("telephonenumber",
	 * "509-362-0218"); reqbody.put("dateofbirth", "05021964");
	 * reqbody.put("postalcode", "99205"); JSONObject resp = null; String tid =
	 * "CiscoTest"; resp = start(url, tid, "5555555555", "12272003", "12345", 10000,
	 * 10000); //System.out.println("final response = "+resp); }
	 */
	
}
