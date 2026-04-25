package com.farmers.DialogFlowAPI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.farmers.APIUtil.Constants;
import com.farmers.APIUtil.TokenBean;
import com.farmers.APIUtil.stacktrace;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;

public class DialogflowHTTPS {
	
	
	public JSONObject start(String url, String tid,String strOriginalANI,String strOriginalDNIS,String strBUid,String strANI,String strDNIS,String strOCallID, String eventParam, String lang, String df_token_scope, int readTimeout, int connTimeOut, LoggerContext context) {
		//public JSONObject start(String url, String tid, String eventParam, String lang, String df_token_scope, int readTimeout, int connTimeOut, LoggerContext context) {
		
		Logger logger = context.getLogger(Constants.FARMERS_LOGGER_NAME);
		logger.info(tid + " : Parameters Passed from CVP : " + url + ", " + tid + ", " + eventParam + ", " + lang);
		JSONParser parser = null;
		BufferedReader br = null;
		HttpsURLConnection connection = null;
		String apistarttime = null;
        String apiendtime = null;
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_TIME_FORMAT);
        String finalToken = null; 
		
		JSONObject response = new JSONObject();
		int responseCode = 0;
		
		try {
			//START - Token Logic --> Do not generate new token each time. Save token and check for expiry which is 1 hour
			TokenBean storedTokenBean = null;
			storedTokenBean = TokenStorage.getInstance().getTokenBean(df_token_scope, logger, tid);
		     
			if(storedTokenBean != null) {
				String extoken = storedTokenBean.getToken();
				Date exDate = storedTokenBean.getTokenCreated();  
				logger.info(tid+" : Token bean Avaialable");
		    	 
				// Check existing token
				if (extoken != null && !isExpired(exDate)) {
					logger.info(tid+" : Using existing token for scope : " + df_token_scope);
					finalToken = extoken;
				}
				else {
					// generate new token and save
					logger.info(tid+" : Token null/Expired : Generating new token for scope : " + df_token_scope);
					// Load the service account key file
					GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(Constants.GDF_CONFIG_PATH)).createScoped(Collections.singleton(df_token_scope));
					// Get the access token
					AccessToken accessToken = credentials.refreshAccessToken();
					// Store token in token bean
					TokenBean tknBean = new TokenBean(accessToken.getTokenValue(), accessToken.getExpirationTime());
					// Store token bean in singleton class
					TokenStorage.getInstance().putToken(df_token_scope, tknBean, logger, tid);
					finalToken = accessToken.getTokenValue();
				}
			}
			else {
				// generate new token 
				logger.info(tid+" : Generating new token for Scope : " + df_token_scope);
				// Load the service account key file
				GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(Constants.GDF_CONFIG_PATH)).createScoped(Collections.singleton(df_token_scope));
				// Get the access token
				AccessToken accessToken = credentials.refreshAccessToken();
				// Store token in token bean
				TokenBean tknBean = new TokenBean(accessToken.getTokenValue(), accessToken.getExpirationTime());
				// Store token bean in singleton class
				TokenStorage.getInstance().putToken(df_token_scope, tknBean, logger, tid);
				finalToken = accessToken.getTokenValue();
			}
			// END - Token Logic
	        
			
			JSONObject requestBody = new JSONObject();
			 
			
			JSONObject queryInput = new JSONObject();
			 
			
			JSONObject event = new JSONObject();
			event.put("event", eventParam); 
			 
			
			queryInput.put("event", event);
			queryInput.put("languageCode", lang);
			 
		
			requestBody.put("queryInput", queryInput);
			 
			// Create queryParams section
			JSONObject queryParams = new JSONObject();
			JSONObject parameters = new JSONObject();
			 
			// Add all parameters under queryParams.parameters
			parameters.put("strOriginalANI", strOriginalANI);
			parameters.put("strOriginalDNIS", strOriginalDNIS);
			parameters.put("strBUid", strBUid);
			parameters.put("strCGUID", tid);
			parameters.put("strANI", strANI);
			parameters.put("strDNIS", strDNIS);
			parameters.put("strOCallID", strOCallID);
			parameters.put("strActiveLang", lang);
			 
			// Add parameters to queryParams
			queryParams.put("parameters", parameters);
			 
			// Add queryParams to request body
			requestBody.put("queryParams", queryParams);
			
			
			
			
			
			
	        
			logger.info(tid + " : request body :: " + requestBody);
	        
			// Create the URL connection
			URL finalURL = new URL(url);
	        
			apistarttime = sdf.format(new Date());
			logger.info(tid + " : Logging API Start Time : "+apistarttime);
            
			connection = (HttpsURLConnection) finalURL.openConnection();
			connection.setConnectTimeout(connTimeOut);
			connection.setReadTimeout(readTimeout);
			connection.setRequestMethod(Constants.REST_METHOD_POST);
			connection.setRequestProperty(Constants.REST_HEADER_AUTHORIZATIION, Constants.REST_HEADER_BEARER + finalToken);
			connection.setRequestProperty(Constants.REST_HEADER_CONTENTTYPE, Constants.REST_HEADER_APPLICATIONJSON);
			connection.setDoOutput(true);
	        
			logger.info(tid + " : Headers :: " + connection.getRequestProperties());
	        
			// Write the request body
			try (OutputStream os = connection.getOutputStream()) {
				byte[] input = requestBody.toString().getBytes(Constants.UTF_8);
				os.write(input, 0, input.length);
				os.flush();
				os.close();
			}
	        
			responseCode = connection.getResponseCode();
			logger.info(tid + " : response code :: " + responseCode);
		     
			if (responseCode == 200) {
				br = new BufferedReader(new InputStreamReader(connection.getInputStream(), Constants.UTF_8));
			} else {
				br = new BufferedReader(new InputStreamReader(connection.getErrorStream(), Constants.UTF_8));
			}
	        
			StringBuilder resp = new StringBuilder();
			String responseLine;
		     
			while ((responseLine = br.readLine()) != null) {
				resp.append(responseLine.trim());
			}
	        
			parser = new JSONParser();
	        
			if (responseCode == 200 && resp != null && !resp.toString().isEmpty()) {
				response = (JSONObject) parser.parse(resp.toString());
				response = formatSuccessResponse(response, responseCode, requestBody, apistarttime, url);
				logger.info(tid + " : Success : Response JSON = " + response);
			}
			else {
				response = formatErrorResponse(resp.toString(), responseCode, requestBody, apistarttime, url);
				logger.info(tid + " : Failure : API Message = "+response);
			}
			
		} catch (Exception e) {
			logger.error(tid + " : Unexpected error occurred : " + e.getMessage());
			stacktrace.printStackTrace(logger, e);
			e.printStackTrace();
		}
		
		finally {
			try {
				if (br != null) {
					try {
						br.close();
					} catch (Exception e) {
						logger.error(tid + " : Unexpected error occurred : " + e.getMessage());
						stacktrace.printStackTrace(logger, e);
					}
				}
				if (connection != null) {
	                connection.disconnect();
	                apiendtime = sdf.format(new Date());
	                logger.info(tid + " : Logging API end time : "+apiendtime);
	                response.put(Constants.APIENDTIME, apiendtime);
				}
			} catch (Exception e2) {
				logger.error(tid + " : Unexpected error occurred : " + e2.getMessage());
				stacktrace.printStackTrace(logger, e2);
				e2.printStackTrace();
			}
		}
		
		return response;
	}
	
	public JSONObject formatSuccessResponse(JSONObject resp,int responsecode, JSONObject requestbody, String apistarttime, String url) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(Constants.RESPONSECODE, responsecode);
		jsonObject.put(Constants.RESPONSEMSG, Constants.SUCCESS);
		jsonObject.put(Constants.RESPONSEBODY, resp);
		jsonObject.put(Constants.REQUESTBODY, requestbody);
		jsonObject.put(Constants.APISTARTTIME, apistarttime);
		
		return jsonObject;
	}
	
	public JSONObject formatErrorResponse(String error,int statusCode, JSONObject requestbody, String apistarttime, String url) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(Constants.RESPONSECODE, statusCode);
		jsonObject.put(Constants.RESPONSEMSG, error);
		jsonObject.put(Constants.REQUESTBODY, requestbody);
		jsonObject.put(Constants.APISTARTTIME, apistarttime);
		return jsonObject;
	}
	
	private boolean isExpired(Date storedTimestamp) {
		Date currentTime = new Date();
		return currentTime.after(storedTimestamp);
	}
	
	public static void main(String[] args) {
		 LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);;
	     File file = new File(new StringBuffer("C:\\Servion\\FARMERS_CLAIMS_CVP\\Config\\").append("log4j2.xml").toString());
	     context.setConfigLocation(file.toURI());
		 String tid = "test12817281729";
		 String eventParam = "CALLER_DISCONNECT";
		 String lang = "en-us";
		 String url = "https://dialogflow.googleapis.com/v3/projects/farmers-cx-lab/locations/global/agents/66c0da6b-01e4-4331-8027-7144bdc1d79c/environments/draft/sessions/iuwiuwndoiewdiuwi2j:detectIntent";
		 String token_Scope = "https://www.googleapis.com/auth/dialogflow";
		 
		 
		 JSONObject resp = new JSONObject();
		 DialogflowHTTPS test = new DialogflowHTTPS();
		 
		 //resp = test.start(url, tid, eventParam, lang, token_Scope, 10000, 10000, context);
		 System.out.println("resp :: " + resp);
	}

}
