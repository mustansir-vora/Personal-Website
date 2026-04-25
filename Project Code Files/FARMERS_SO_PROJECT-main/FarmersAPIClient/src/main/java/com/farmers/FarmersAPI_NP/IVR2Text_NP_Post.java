package com.farmers.FarmersAPI_NP;

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

public class IVR2Text_NP_Post {
	
//private Logger logger = LogManager.getLogger(Constants.LOGGER_NAME);
	
public  JSONObject start(String url, String tid, String callerani, String destinationapp, String tollfreenumber, String callerinput, String poptype, String calledinto, String calledintime, String tollfreedescription, String callertype, String language, String an1, String upn1, String transferreason, String intent , String brand , boolean validpolicynumber, String callerrisklevel, int conntimeout, int readtimeout, LoggerContext context, String region) {
		
		Logger logger = context.getLogger(Constants.FARMERS_LOGGER_NAME);
		logger.info(tid+" : Parameters Passed from CVP : "+url+", "+tid+", "+destinationapp+", "+tollfreenumber+", "+callerinput+", "+poptype+", "+calledinto+", "+calledintime+", "+tollfreedescription+", "+callertype+", "+language+", "+an1+", "+upn1+", "+transferreason+", "+intent+", "+brand+", "+validpolicynumber+", "+callerrisklevel+", "+conntimeout+", "+readtimeout);
		JSONObject response = new JSONObject();
		APIConnection connection = null;
		String Scope = "api.ivrcallinfo.create";

		try {
			connection = new APIConnection();
			String requestBody = formRequestBody(tid, callerani, destinationapp, tollfreenumber, callerinput, poptype, calledinto, calledintime, tollfreedescription, callertype, language, an1, upn1, transferreason, intent, brand, validpolicynumber, callerrisklevel);

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
			logger.error(tid+" : Unexpected error occurred : " + e.getMessage());
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
			headers.put("frms_target", prop.getProperty("Farmers.Host.IVR2TEXT.frms_target"));
			headers.put("frms_appid", prop.getProperty("Farmers.Host.frms_appid"));
			headers.put("frms_tid", tid);
			headers.put("frms_region",region);
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
			logger.error(tid+" : Unexpected error occurred : " + e.getMessage());
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
	public  String formRequestBody(String ivrcallid, String callerani, String destinationapp, String tollfreenumber, String callerinput, String poptype, String calledinto, String calledintime, String tollfreedescription, String callertype, String language, String an1, String upn1, String transferreason, String intent , String brand , boolean validpolicynumber, String callerrisklevel) {

		JSONObject IVRInfo = null;
		JSONObject reqBody = null;
		try {
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
			
			reqBody = new JSONObject();
			reqBody.put("IVRInfo", IVRInfo);

		} catch (Exception e) {

		}
		return reqBody.toString();

	}

	
	 public static void main(String[] args) {
	  
		  LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);;
	      File file = new File(new StringBuffer("C:\\Servion\\FARMERS_SO_CVP_UAT_NP\\Config\\").append("log4j2.xml").toString());
	      context.setConfigLocation(file.toURI());
	  String url = "https://api-ss.farmersinsurance.com/ivrms/v1/ivrrecords/F49D676D00CC11EFB5CBFFE689638000";
	  JSONObject resp = null; 
	  String tid = "F49D676D00CC11EFB5CBFFE689638000";
	  
	  String brand = "FDS/SO";
	  String TollFreeNumber = "8888401248";
	  String poptype = "04";
	  String CallerType = "02";
	  String ANI = "18208928683";
	  String DestinationApp = "SRM";
	  boolean ValidPolicyNumber = false;
	  String UPN1 = "";
	  String AN1 = "";
	  String CallerInput = "311703474";
	  String CalledInto = "99510408101";
	  String CalledInTime = "2024-04-16T17:49:58";
	  String Intent = "Autopay Change";
	  String TransferReason = "IVR to Text";
	  String TollFreeDescription = "";
	  String Language = "en-us";
	  String callerrisklevel = "MEDIUM";
	  
	  IVR2Text_NP_Post test = new IVR2Text_NP_Post();
	  resp = test.start(url, tid, ANI, DestinationApp, TollFreeNumber, CallerInput, poptype, CalledInto, CalledInTime, TollFreeDescription, CallerType, Language, AN1, UPN1, TransferReason, Intent , brand , ValidPolicyNumber, callerrisklevel, 10000, 10000, context, "uat"); 
	  } 
}
	 


