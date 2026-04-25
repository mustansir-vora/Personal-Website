package com.farmers.FarmersAPI_NP;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.json.simple.JSONObject;

import com.farmers.APIUtil.Constants;
import com.farmers.APIUtil.Encrypt_Decrypt;
import com.farmers.APIUtil.stacktrace;
import com.farmers.bean.RequestBean;

import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;

public class PointOrderIDCard_NP_Post {
	
	//private Logger logger = LogManager.getLogger(Constants.LOGGER_NAME);
	
	public JSONObject start(String url, String tid, String businessunit, String policycontractnumber, String mastercompany, String policysource, String deliverymethod, String size, String term, String documenttype, String faxnumber, int conntimeout, int readtimeout, LoggerContext context) {

		Logger logger = context.getLogger(Constants.FARMERS_LOGGER_NAME);
		logger.info(tid+" : Parameters Passed from CVP : " + url+", "+tid+", "+policycontractnumber+", "+mastercompany+", "+policysource+", "+deliverymethod+", "+size+", "+term+", "+documenttype+", "+faxnumber+", "+conntimeout+", "+readtimeout);
		JSONObject response = new JSONObject();
		APIConnection connection = null;
		String Scope = "api.idcard.create";

		try {
			connection = new APIConnection();

			String requestBody = formRequestBody(policycontractnumber, mastercompany, policysource, deliverymethod, size, term, documenttype, faxnumber);

			HashMap<String, String> headers = (HashMap<String, String>) getHeaders(Scope, tid,businessunit, logger);
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

	private Map<String, String> getHeaders(String Scope, String tid, String businessunit, Logger logger) {

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
			headers.put("client_id", client_id);
			headers.put("client_secret", client_secret);
			headers.put("Content-Type", "application/json");
			headers.put("frms_source", "CISCO");
			headers.put("frms_appid", "1");
			headers.put("frms_tid", tid);
			headers.put("frms_brand", businessunit);
			
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
	

	public String formRequestBody(String policycontractnumber, String mastercompany, String policysource, String deliverymethod, String size, String term, String documenttype, String faxnumber) {

		JSONObject idcardspec = null;
		JSONObject idCard = null;
		JSONObject reqBody = null;
		try {
			
			idcardspec = new JSONObject();
			idcardspec.put("size", size);
			idcardspec.put("term", term);
			
			idCard = new JSONObject();
			idCard.put("policyContractNumber", policycontractnumber);
			idCard.put("masterCompany", mastercompany);
			idCard.put("policySource", policysource);
			idCard.put("deliveryMethod", deliverymethod);
			idCard.put("idCardSpec", idcardspec);
			idCard.put("documentType", documenttype);
			idCard.put("faxNumber", faxnumber);
			
			reqBody = new JSONObject();
			reqBody.put("idCard", idCard);

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
	
	/*
	 * public void main(String[] args) {
	 * 
	 * String url = "https://api-ss.farmersinsurance.com/idcardms/v1/idcard";
	 * JSONObject reqbody = new JSONObject(); reqbody.put("policycontractnumber",
	 * "******"); reqbody.put("mastercompany", "90"); reqbody.put("policysource",
	 * "IVR"); reqbody.put("deliverymethod", "EMAIL"); reqbody.put("size", "LS");
	 * reqbody.put("term", "N"); reqbody.put("documenttype", "PolicyDocuments");
	 * reqbody.put("faxnumber", null); JSONObject resp = null; String tid =
	 * "CiscoTest"; String businessunit = "BW"; resp = start(url, tid, businessunit,
	 * "******", "90", "IVR", "EMAIL", "LS", "N", "PolicyDocuments", null, 10000,
	 * 10000); //System.out.println("final response = "+resp); }
	 */


}
