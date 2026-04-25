package com.farmers.FarmersAPI_NP;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.json.simple.JSONObject;

import com.farmers.APIUtil.Constants;
import com.farmers.APIUtil.Encrypt_Decrypt;
import com.farmers.APIUtil.stacktrace;
import com.farmers.bean.RequestBean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;

public class KM2_NP_POST {

	// private Logger logger = LogManager.getLogger(Constants.LOGGER_NAME);

	public  JSONObject start(String url, String tid, String callerani, String destinationapp, String tollfreenumber, String callerinput, String poptype, String calledinto, String calledintime, String tollfreedescription, String callertype, String language, String an1, String upn1, String transferreason, String intent , String brand , boolean validpolicynumber, String callerrisklevel, int conntimeout, int readtimeout, LoggerContext context,
			String	CCAI_Agent_FirstName,
			String CCAI_Agent_LastName,
			String CCAI_Agent_State,
			String CCAI_Agent_Code_Producer_Code,
			String CCAI_Agency_Name,
			String CCAI_Agent_Verified,
			String CCAI_Agent_Policy_Number,
			String CCAI_Customer_FirstName,
			String CCAI_Customer_LastName,
			String CCAI_Customer_SSN_Last_4,
			String CCAI_Customer_DOB,
			String CCAI_Customer_DLNumber,
			String CCAI_Customer_Address_Street_1,
			String CCAI_Customer_Address_Street_2,
			String CCAI_Customer_Address_City,
			String CCAI_Customer_Address_State,
			String CCAI_Customer_Address_ZipCode,
			String CCAI_Customer_Address_Country,
			String CCAI_Customer_Address_Type,
			String CCAI_Customer_Policy_Number,
			String CCAI_Customer_BillingAccount_Number,
			String CCAI_Customer_Verified,
			String CCAI_Customer_ECN,
			String region) {
					
		Logger logger = context.getLogger(Constants.FARMERS_LOGGER_NAME);
		logger.info(tid+" : Parameters Passed from CVP : "+url+", "+tid+", "+destinationapp+", "+tollfreenumber+", "+callerinput+", "+poptype+", "+calledinto+", "+calledintime+", "+tollfreedescription+", "+callertype+", "+language+", "+an1+", "+upn1+", "+transferreason+", "+intent+", "+brand+", "+validpolicynumber+", "+callerrisklevel+", "+conntimeout+", "+readtimeout);
		JSONObject response = new JSONObject();
		APIConnection connection = null;
		String Scope = "api.ivrcallinfo.create";

		try {
			connection = new APIConnection();
			String requestBody = formRequestBody(tid, callerani, destinationapp, tollfreenumber, callerinput, poptype, calledinto, calledintime, tollfreedescription, callertype, language, an1, upn1, transferreason, intent, brand, validpolicynumber, callerrisklevel,
					CCAI_Agent_FirstName,
					CCAI_Agent_LastName,
					CCAI_Agent_State,
					CCAI_Agent_Code_Producer_Code,
					CCAI_Agency_Name,
					CCAI_Agent_Verified,
					CCAI_Agent_Policy_Number,
					CCAI_Customer_FirstName,
					CCAI_Customer_LastName,
					CCAI_Customer_SSN_Last_4,
					CCAI_Customer_DOB,
					CCAI_Customer_DLNumber,
					CCAI_Customer_Address_Street_1,
					CCAI_Customer_Address_Street_2,
					CCAI_Customer_Address_City,
					CCAI_Customer_Address_State,
					CCAI_Customer_Address_ZipCode,
					CCAI_Customer_Address_Country,
					CCAI_Customer_Address_Type,
					CCAI_Customer_Policy_Number,
					CCAI_Customer_BillingAccount_Number,
					CCAI_Customer_Verified,
					CCAI_Customer_ECN, logger);
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
			logger.info(tid+" : Unexpected error occurred : " + e.getMessage());
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
			headers.put("frms_target", prop.getProperty("Farmers.Host.KM2.frms_target"));
			headers.put("frms_appid", prop.getProperty("Farmers.Host.frms_appid"));
			headers.put("frms_tid", tid);
			headers.put("frms_region", region);
			headers.put("frms_ipaddress", "0.0.0.0");
			headers.put("client_id", client_id);
			headers.put("client_secret", client_secret);
			headers.put("Content-Type", "application/json");
			
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
	public  String formRequestBody(String ivrcallid, String callerani, String destinationapp, String tollfreenumber, String callerinput, String poptype, String calledinto, String calledintime, String tollfreedescription, String callertype, String language, String an1, String upn1, String transferreason, String intent , String brand , boolean validpolicynumber, String callerrisklevel,
			String	CCAI_Agent_FirstName,
			String CCAI_Agent_LastName,
			String CCAI_Agent_State,
			String CCAI_Agent_Code_Producer_Code,
			String CCAI_Agency_Name,
			String CCAI_Agent_Verified,
			String CCAI_Agent_Policy_Number,
			String CCAI_Customer_FirstName,
			String CCAI_Customer_LastName,
			String CCAI_Customer_SSN_Last_4,
			String CCAI_Customer_DOB,
			String CCAI_Customer_DLNumber,
			String CCAI_Customer_Address_Street_1,
			String CCAI_Customer_Address_Street_2,
			String CCAI_Customer_Address_City,
			String CCAI_Customer_Address_State,
			String CCAI_Customer_Address_ZipCode,
			String CCAI_Customer_Address_Country,
			String CCAI_Customer_Address_Type,
			String CCAI_Customer_Policy_Number,
			String CCAI_Customer_BillingAccount_Number,
			String CCAI_Customer_Verified,
			String CCAI_Customer_ECN, Logger logger) {

		JSONObject IVRInfo = null;
		JSONObject reqBody = null;
		try {
			
			
			
			Properties prop = new Properties();
	        prop.load(new FileInputStream(Constants.CONFIG_PATH));
			if (prop.getProperty("Farmers.Host.KM2.frms_target") != null) {
				destinationapp = prop.getProperty("Farmers.Host.KM2.frms_target");
			}
			else {
				destinationapp = "SRM_KM2";
			}
			
			
			
			
			boolean agentverified=false;
			boolean Customerverfied=false;
			
			if(CCAI_Agent_Verified.equalsIgnoreCase("true"))
			{
				agentverified=true;
				
			}
		
			if(CCAI_Customer_Verified.equalsIgnoreCase("true"))
			{
				Customerverfied=true;
				
			}
			
			// Create objects for additionalProperties -> agent
	        JSONObject agent = new JSONObject();
	        agent.put("firstName", CCAI_Agent_FirstName);
	        agent.put("lastName", CCAI_Agent_LastName);
	        agent.put("producerCode", CCAI_Agent_Code_Producer_Code);
	        agent.put("state",CCAI_Agent_State);
	        agent.put("agencyName", CCAI_Agency_Name);
	        agent.put("verified", agentverified); // Use JSONObject.NULL for null values
	        agent.put("policyNumber", CCAI_Agent_Policy_Number);

	        // Create nested address object
	        JSONObject address = new JSONObject();
	        address.put("addressLine1", CCAI_Customer_Address_Street_1);
	        address.put("addressLine2", CCAI_Customer_Address_Street_2);
	        address.put("city", CCAI_Customer_Address_City);
	        address.put("state", CCAI_Customer_Address_State);
	        address.put("zipCode", CCAI_Customer_Address_ZipCode);
	        address.put("country", CCAI_Customer_Address_Country);
	        address.put("type", CCAI_Customer_Address_Type);

	        // Create objects for additionalProperties -> customer
	        JSONObject customer = new JSONObject();
	        customer.put("firstName", CCAI_Customer_FirstName);
	        customer.put("lastName", CCAI_Customer_LastName);
	        customer.put("ssn", CCAI_Customer_SSN_Last_4);
	        customer.put("dateOfBirth", CCAI_Customer_DOB); // Corrected date format
	        customer.put("licenseNumber", CCAI_Customer_DLNumber);
	        customer.put("policyNumber", CCAI_Customer_Policy_Number);
	        customer.put("billingAccountNumber", CCAI_Customer_BillingAccount_Number);
	        customer.put("verified", Customerverfied);
	        customer.put("ecn", CCAI_Customer_ECN);
	        customer.put("address", address); // Add the nested address object

	        // Create additionalProperties object
	        JSONObject additionalProperties = new JSONObject();
	        additionalProperties.put("agent", agent);
	        additionalProperties.put("customer", customer);
			
			
			
			
	        
			IVRInfo = new JSONObject();
			IVRInfo.put("IVRCallID", ivrcallid);
			IVRInfo.put("DestinationApp", destinationapp);
			IVRInfo.put("TollFreeNumber", tollfreenumber);
			if (callerinput.length() > 20) {
				IVRInfo.put("CallerInput", callerinput.substring(0, 20));
			}
			else {
				IVRInfo.put("CallerInput", callerinput);
			}
			IVRInfo.put("PopType", poptype);
			IVRInfo.put("CallerANI", callerani);
			IVRInfo.put("CalledInto", calledinto);
			IVRInfo.put("CalledInTime", calledintime);
			IVRInfo.put("TollFreeDescription", tollfreedescription);
			IVRInfo.put("CallerType", callertype);
			IVRInfo.put("Language", language);
			IVRInfo.put("AN1", an1);
			IVRInfo.put("UPN1", upn1);
			IVRInfo.put("TransferReason", transferreason);
			IVRInfo.put("Intent", intent);
			IVRInfo.put("Brand", brand);
			IVRInfo.put("ValidPolicyNumber", validpolicynumber);
			IVRInfo.put("CallerRiskLevel", callerrisklevel);
			IVRInfo.put("additionalProperties", additionalProperties);
			reqBody = new JSONObject();
			reqBody.put("IVRInfo", IVRInfo);

		} catch (Exception e) {
			// Handle other exceptions
			logger.error(ivrcallid+" : Unexpected error occurred : " + e.getMessage());
			stacktrace.printStackTrace(logger, e);
			e.printStackTrace();
		}
		return reqBody.toString();

	}

	
	 public static void main(String[] args) {
	  
		  LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);;
	      File file = new File(new StringBuffer("C:\\Servion\\FARMERS_SO_CVP_UAT_NP\\Config\\").append("log4j2.xml").toString());
	      context.setConfigLocation(file.toURI());
	  String url = "https://api-ss.farmersinsurance.com/ivrms/v1/ivrrecords/CiscoTest";
	  JSONObject resp = null; 
	  String tid = "CiscoTest";
	  
	  String brand = "FWS";
	  String TollFreeNumber = "8004386381";
	  String poptype = "01";
	  String CallerType = "01";
	  String ANI = "7864673453";
	  String DestinationApp = "";
	  boolean ValidPolicyNumber = false;
	 
	  String UPN1 = "";
	  String AN1 = "";
	  String CallerInput = "6673528680";
	  String CalledInto = "99510804010";
	  String CalledInTime = "2024-04-25T14:02:59";
	  String Intent = "";
	  String TransferReason = "";
	  String TollFreeDescription = "FWS Service IVR - 8004386381";
	  String Language = "en-us";
	  String callerrisklevel = "MEDIUM";
	  String CCAI_Agent_FirstName="";
	  String CCAI_Agent_LastName="";
		String CCAI_Agent_State="";
		String CCAI_Agent_Code_Producer_Code="";
		String CCAI_Agency_Name="";
		String CCAI_Agent_Verified="";
		String CCAI_Agent_Policy_Number="";
		String CCAI_Customer_FirstName="";
		String CCAI_Customer_LastName="";
		String CCAI_Customer_SSN_Last_4="";
		String CCAI_Customer_DOB="";
		String CCAI_Customer_DLNumber="";
		String CCAI_Customer_Address_Street_1="";
		String CCAI_Customer_Address_Street_2="";
		String CCAI_Customer_Address_City="";
		String CCAI_Customer_Address_State="";
		String CCAI_Customer_Address_ZipCode="";
		String CCAI_Customer_Address_Country="";
		String CCAI_Customer_Address_Type="";
		String CCAI_Customer_Policy_Number="";
		String CCAI_Customer_BillingAccount_Number="";
		String CCAI_Customer_Verified="";
		String CCAI_Customer_ECN="";
		  
	  
	  KM2_NP_POST test = new KM2_NP_POST();
	  
	  resp = test.start(url, tid,  ANI, DestinationApp, TollFreeNumber, CallerInput, poptype, CalledInto, CalledInTime, TollFreeDescription, CallerType, Language, AN1, UPN1, TransferReason, Intent , brand , ValidPolicyNumber, callerrisklevel, 10000, 10000, context,
			  CCAI_Agent_FirstName,
			  CCAI_Agent_LastName,
			  CCAI_Agent_State,
			  CCAI_Agent_Code_Producer_Code,
			  CCAI_Agency_Name,
			  CCAI_Agent_Verified,
			  CCAI_Agent_Policy_Number,
			  CCAI_Customer_FirstName,
			  CCAI_Customer_LastName,
			  CCAI_Customer_SSN_Last_4,
			  CCAI_Customer_DOB,
			  CCAI_Customer_DLNumber,
			  CCAI_Customer_Address_Street_1,
			  CCAI_Customer_Address_Street_2,
			  CCAI_Customer_Address_City,
			  CCAI_Customer_Address_State,
			  CCAI_Customer_Address_ZipCode,
			  CCAI_Customer_Address_Country,
			  CCAI_Customer_Address_Type,
			  CCAI_Customer_Policy_Number,
			  CCAI_Customer_BillingAccount_Number,
			  CCAI_Customer_Verified,
			  CCAI_Customer_ECN, "uat"
			  
			  
			  
				); 
	  } 
}
