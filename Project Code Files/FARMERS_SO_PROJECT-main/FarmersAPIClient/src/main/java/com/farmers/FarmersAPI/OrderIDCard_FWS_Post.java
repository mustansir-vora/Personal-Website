package com.farmers.FarmersAPI;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

public class OrderIDCard_FWS_Post {
	
	public JSONObject start(String url, String tid, String policynumber, String policysource, String lob, String policystate, String policysuffix, String deliverymethod, String effectivedate, String faxnumber, String firstname, String lastname, String middlename, String internalpolicynumber, String internalpolicyversion,String emailAddress, int conntimeout, int readtimeout, LoggerContext context) {

		Logger logger = context.getLogger(Constants.FARMERS_LOGGER_NAME);
		logger.info(tid+" : Parameters Passed from CVP : " + url+", "+tid+", "+policynumber+", "+policysource+", "+lob+", "+policystate+", "+policysuffix+", "+deliverymethod+", "+effectivedate+", "+faxnumber+", "+firstname+", "+lastname+", "+middlename+", "+internalpolicynumber+", "+internalpolicyversion+", "+conntimeout+", "+readtimeout);
		JSONObject response = new JSONObject();
		APIConnection connection = null;
		String Scope = "api.idcard.create";

		try {
			connection = new APIConnection();

			String requestBody = formRequestBody(policynumber, policysource, policysuffix, deliverymethod, effectivedate, faxnumber, firstname, lastname, middlename, internalpolicynumber, internalpolicyversion,emailAddress);

			HashMap<String, String> headers = (HashMap<String, String>) getHeaders(Scope, tid, lob, policystate, policysource, logger);
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

	private Map<String, String> getHeaders(String Scope, String tid, String lob, String policystate, String policysource, Logger logger) {

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
			headers.put("frms_brand", "FWS");
			headers.put("frms_lob", lob);
			headers.put("frms_state", policystate);
			headers.put("frms_systemCode", "IVR");
			headers.put("frms_transactionType", "orderIDCard");
			headers.put("frms_policySource", policysource);
			headers.put("frms_region", "PROD");
			
			
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
	

	public String formRequestBody(String policynumber, String policysource, String policysuffix, String deliverymethod, String effectivedate, String faxnumber, String firstname, String lastname, String middlename, String internalpolicynumber, String internalpolicyversion,String emailAddress) {
		
		JSONObject reqBody = new JSONObject();
		
		if (policysource.equalsIgnoreCase("ARS")) {
			
			if (deliverymethod.equalsIgnoreCase("mail")) {
				
				List<Map<String, String>> customproperties = new ArrayList<Map<String, String>>();  
				customproperties.add(createCustomProperty("internalPolicyNumber", internalpolicynumber));
				customproperties.add(createCustomProperty("internalPolicyVersion", internalpolicyversion));
				
				JSONObject idcardspec = new JSONObject();
				idcardspec.put("size", null);
				idcardspec.put("term", null);
				
				JSONObject recipient = new JSONObject();
				recipient.put("firstName", firstname);
				recipient.put("lastName", lastname);
				recipient.put("middleName", middlename);
				
				JSONObject idcard = new JSONObject();
				idcard.put("policyContractNumber", policynumber);
				idcard.put("policySource", "ARS");
				idcard.put("policySuffix", policysuffix);
				idcard.put("deliveryMethod", deliverymethod);
				idcard.put("idCardSpec", idcardspec);
				idcard.put("effectiveDate", effectivedate);
				idcard.put("faxNumber", null);
				idcard.put("recipient", recipient);
				idcard.put("documentType", null);
				idcard.put("customproperties", customproperties);
				
				reqBody.put("idCard", idcard);
			}else if(deliverymethod.equalsIgnoreCase("email")) {

				List<Map<String, String>> customproperties = new ArrayList<Map<String, String>>();  
				customproperties.add(createCustomProperty("jobName", "200511231057A"));
				
				JSONObject idcard = new JSONObject();
				idcard.put("policyContractNumber", policynumber);
				idcard.put("policySource", policysource);
				idcard.put("policySuffix", policysuffix);
				idcard.put("deliveryMethod", deliverymethod);
				idcard.put("idCardSpec", null);
				idcard.put("faxNumber", faxnumber);
				idcard.put("recipient", null);
				idcard.put("documentType", "IVRFax_IDCard");
				idcard.put("customProperties", customproperties);
				idcard.put("emailAddress", emailAddress);
				idcard.put("cardType","Temporary");
				reqBody.put("idCard", idcard);
			}
			else {
				
				List<Map<String, String>> customproperties = new ArrayList<Map<String, String>>();  
				customproperties.add(createCustomProperty("jobName", "200511231057A"));
				customproperties.add(createCustomProperty("internalPolicyNumber", internalpolicynumber));
				customproperties.add(createCustomProperty("internalPolicyVersion", internalpolicyversion));
				customproperties.add(createCustomProperty("letterName", ""));
				customproperties.add(createCustomProperty("senderPhone", ""));
				
				JSONObject idcard = new JSONObject();
				idcard.put("policyContractNumber", policynumber);
				idcard.put("policySource", "ARS");
				idcard.put("policySuffix", policysuffix);
				idcard.put("deliveryMethod", deliverymethod);
				idcard.put("idCardSpec", null);
				idcard.put("faxNumber", faxnumber);
				idcard.put("recipient", null);
				idcard.put("documentType", "IVRFax_IDCard");
				idcard.put("customProperties", customproperties);
				
				reqBody.put("idCard", idcard);
			}
		}
		else {
			
			if (deliverymethod.equalsIgnoreCase("mail")) {
				
				JSONObject idcardspec = new JSONObject();
				idcardspec.put("size", null);
				idcardspec.put("term", null);
				
				JSONObject recipient = new JSONObject();
				recipient.put("firstName", firstname);
				recipient.put("lastName", lastname);
				recipient.put("middleName", middlename);
				
				JSONObject idcard = new JSONObject();
				idcard.put("policyContractNumber", policynumber);
				idcard.put("policySource", "Met360");
				idcard.put("policySuffix", policysuffix);
				idcard.put("deliveryMethod", deliverymethod);
				idcard.put("idCardSpec", idcardspec);
				idcard.put("effectiveDate", effectivedate);
				idcard.put("faxNumber", null);
				idcard.put("recipient", recipient);
				idcard.put("documentType", null);
				idcard.put("customProperties", null);
				
				reqBody.put("idCard", idcard);
			}else if(deliverymethod.equalsIgnoreCase("email")) {

				List<Map<String, String>> customproperties = new ArrayList<Map<String, String>>();  
				customproperties.add(createCustomProperty("jobName", "200511231057A"));
				
				JSONObject idcard = new JSONObject();
				idcard.put("policyContractNumber", policynumber);
				idcard.put("policySource", policysource);
				idcard.put("policySuffix", policysuffix);
				idcard.put("deliveryMethod", deliverymethod);
				idcard.put("idCardSpec", null);
				idcard.put("faxNumber", faxnumber);
				idcard.put("recipient", null);
				idcard.put("documentType", "IVRFax_IDCard");
				idcard.put("customProperties", customproperties);
				idcard.put("emailAddress", emailAddress);
				idcard.put("cardType","Temporary");
				reqBody.put("idCard", idcard);
			}
			else {
				
				List<Map<String, String>> customproperties = new ArrayList<Map<String, String>>();  
				customproperties.add(createCustomProperty("jobName", "200511231057A"));
				
				JSONObject idcard = new JSONObject();
				idcard.put("policyContractNumber", policynumber);
				idcard.put("policySource", "Met360");
				idcard.put("policySuffix", policysuffix);
				idcard.put("deliveryMethod", deliverymethod);
				idcard.put("idCardSpec", null);
				idcard.put("faxNumber", faxnumber);
				idcard.put("recipient", null);
				idcard.put("documentType", "IVRFax_IDCard");
				idcard.put("customProperties", customproperties);
				
				reqBody.put("idCard", idcard);
			}
			
		}
		
		return reqBody.toString();
	}
	
	
	private JSONObject createCustomProperty(String name, String value) {
	    JSONObject prop = new JSONObject();
	    prop.put("name", name);
	    prop.put("value", value);
	    return prop;
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
		  
	  String url = "https://api-ss.farmersinsurance.com/idcardms/v1/idcard";
	  JSONObject resp = null; 
	  String tid = "test123"; 
	  String policynumber = "8421037960";
	  String policysource = "ARS";
	  String lob = "A"; 
	  String policystate = "WA"; 
	  String policysuffix = "0";
	  String deliverymethod = "Fax"; //Can be Mail or Fax - FWS Does not support email. //Case Sensitive
	  String effectivedate = "09-01-2024"; //- Format for date
	  String faxnumber = "1111111111"; //null when deliverymethod is mail
	  String firstname = "ESTHER";
	  String lastname = "KIRBY";
	  String middlename = "";
	  String internalpolicynumber = "";
	  String internalpolicyversion = "";
	  String emailAddress="abc@gmail.com";
	  
	  OrderIDCard_FWS_Post test = new OrderIDCard_FWS_Post();
	  resp = test.start(url, tid, policynumber, policysource, lob, policystate, policysuffix, deliverymethod, effectivedate, faxnumber, firstname, lastname, middlename, internalpolicynumber, internalpolicyversion,emailAddress, 10000, 10000, context);
	  } 
	 

}
