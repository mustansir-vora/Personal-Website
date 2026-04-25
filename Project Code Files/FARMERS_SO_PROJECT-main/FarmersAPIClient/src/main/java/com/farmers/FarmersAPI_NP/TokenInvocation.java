package com.farmers.FarmersAPI_NP;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.Properties;

import javax.net.ssl.HttpsURLConnection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.farmers.APIUtil.Constants;
import com.farmers.APIUtil.Encrypt_Decrypt;
import com.farmers.APIUtil.stacktrace;
import com.farmers.bean.TokenBean;

public class TokenInvocation {
	
	//private Logger logger = LogManager.getLogger(Constants.LOGGER_NAME);
	
    public String getAccessToken(String scope, Logger logger, String tid) {
    	Date timestamp;
        timestamp = new Date();
        
        String rtnToken = null;
        
        TokenBean storedTokenBean = null;
             
        storedTokenBean = TokenStorage.getInstance().getToken(scope, logger, tid);
        if(storedTokenBean != null) {
            String extoken = storedTokenBean.getToken();
            Date exDate = storedTokenBean.getTokenCreated();  
            logger.info(tid+" : Token Avaialable");
            String newToken = null;
            
            // Check for valid token and scope
            if (extoken != null && !isExpired(exDate)) {
            	logger.info(tid+" : Using existing token for scope : " + scope);
                rtnToken = extoken;
            } else {
            	logger.info(tid+" : Token Not Generated/Expired : Generating new token for scope : " + scope);
                // Fetch new token and save
            	if (scope.equalsIgnoreCase("afni")) {
            		newToken = getTokenAFNI(logger, tid);
				}
            	else {
            		newToken = getToken(scope, logger, tid);
            	}
                
                timestamp = new Date();
        		TokenBean tknBean = new TokenBean(newToken, timestamp);
        		TokenStorage.getInstance().putToken(scope, tknBean, logger, tid);
                rtnToken = newToken;
            } 
        } else {
			
        	//generate new token 
        	logger.info(tid+" : Generating new token for Scope : " + scope);
        	String token = null;
        	if (scope.equalsIgnoreCase("afni")) {
        		token = getTokenAFNI(logger, tid);
			}
        	else {
        		token = getToken(scope, logger, tid);
        	}
        	Date tStamp = new Date(); 
        	TokenBean tknBean = new TokenBean(token, tStamp);
        	TokenStorage.getInstance().putToken(scope, tknBean, logger, tid);
        	rtnToken = token;
        }
        return rtnToken;
    }
	

	public String getToken(String Scope, Logger logger, String tid) {
		
		JSONParser parser = new JSONParser();
		JSONObject JSONResponse = new JSONObject();
		String token = null;
		
		try {
			Properties prop = new Properties();
	        prop.load(new FileInputStream(Constants.CONFIG_PATH));
	        
	        String Endpoint = prop.getProperty("Farmers.Token.URL");
			String Base64 = prop.getProperty("Farmers.Host.Auth");
			String Client_ID = prop.getProperty("Farmers.Host.ClientID");
			String Client_Secret = prop.getProperty("Farmers.Host.ClientSecret");
			String frms_region = prop.getProperty("Farmers.Host.frms_region");
			String Content_Type = prop.getProperty("Farmers.Host.content_type");
			String frms_appid = prop.getProperty("Farmers.Host.frms_appid");
			String frms_source = prop.getProperty("Farmers.Host.frms_source");
			String charset = prop.getProperty("Farmers.Host.charset");
			String URLParameters = prop.getProperty("Farmers.Host.URLParameters")+Scope;
			
			logger.info(tid+" : Encrypted client id : "+Client_ID+", Decrypted client secret : "+Client_Secret);
			
			Encrypt_Decrypt Decryptor = new Encrypt_Decrypt();
			Client_ID = Decryptor.decrypt(Client_ID);
			Client_Secret = Decryptor.decrypt(Client_Secret);
			
			// URL Formation
			URL url = null;
			url = new URL(Endpoint);
			
			// Form Connection
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", Content_Type);
			conn.setRequestProperty("Authorization", "Basic" + Base64);
			conn.setRequestProperty("client_id", Client_ID);
			conn.setRequestProperty("client_secret", Client_Secret);
			conn.setRequestProperty("frms_appid", frms_appid);
			conn.setRequestProperty("frms_source", frms_source);
			conn.setRequestProperty("charset", charset);
			conn.setRequestProperty("frms_tid", tid);
			conn.setRequestProperty("frms_region", frms_region);
			// True value indicates that we wish to pass request body
			conn.setDoOutput(true);

			// Request Body -
			byte[] reqBody = URLParameters.getBytes(StandardCharsets.UTF_8);
			
			// Write RequestBody in OutputStream
			OutputStream oss = conn.getOutputStream();
			oss.write(reqBody);
			oss.flush();

			logger.info(tid+" : Token Response code : "+conn.getResponseCode());
			if (conn.getResponseCode() == 201) {
				// Read the response directly into a JSON object
				InputStream inputStream = conn.getInputStream();
				JSONResponse = (JSONObject) parser.parse(new InputStreamReader(inputStream));
				logger.info(tid+" : Response = " + JSONResponse);
				logger.info(tid+" : Scope = "+Scope);
				
				if (JSONResponse.containsKey("access_token")) {
					token = (String) JSONResponse.get("access_token");
				}
			}

			else {
				InputStream inputStream = conn.getInputStream();
				JSONResponse = (JSONObject) parser.parse(new InputStreamReader(inputStream));
				logger.info(tid+" : Response = " + JSONResponse);
			}

		} 
		catch (Exception e) {
			// Handle other exceptions
			logger.error(tid+" : Unexpected error occurred : " + e.getMessage());
			stacktrace.printStackTrace(logger, e);
		}
		return token;
		
	}
	
	
	public String getTokenAFNI(Logger logger, String tid) {
		
		JSONParser parser = new JSONParser();
		JSONObject JSONResponse = null;
		String token = null;

		// Form Connection
		try {
			Properties prop = new Properties();
	        prop.load(new FileInputStream(Constants.CONFIG_PATH));
	        
	        String Endpoint = prop.getProperty("Afni.Token.URL");
	        URL url = null;
	        logger.info("URL fetched from property file :: "+Endpoint);
			url = new URL(Endpoint);
			
	        String userid = prop.getProperty("Afni.Token.Basic.userid");
	        logger.info("User ID fetched from property file :: "+userid);
			String password = prop.getProperty("Afni.Token.Basic.Password");
			logger.info("password fetched from property file :: "+password);
			String encodedAuth = Base64.getEncoder().encodeToString((userid + ":" + password).getBytes());
			logger.info(tid+" : Encoded Auth String : "+encodedAuth);
			
			String Content_Type = prop.getProperty("Afni.Token.Content_Type");
			logger.info("content type fetched from property file :: "+Content_Type);
			String charset = prop.getProperty("Afni.Token.Charset");
			logger.info("charset fetched from property file :: "+charset);
			String URLParameters = prop.getProperty("Afni.Token.URLParameters");
			logger.info("urlparameters fetched from property file :: "+URLParameters);
			
			// Request Body -
			byte[] reqBody = URLParameters.getBytes(StandardCharsets.UTF_8);
			
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", Content_Type);
			conn.setRequestProperty("Authorization", "Basic " + encodedAuth);
			conn.setRequestProperty("charset", charset);

			// True value indicates true to pass request body
			conn.setDoOutput(true);

			// Write RequestBody in OutputStream
			OutputStream oss = conn.getOutputStream();
			oss.write(reqBody);
			oss.flush();
			logger.info(tid+" : Token Response code : "+conn.getResponseCode());
			if (conn.getResponseCode() == 200) {
				// Read the response directly into a JSON object
				InputStream inputStream = conn.getInputStream();
				JSONResponse = (JSONObject) parser.parse(new InputStreamReader(inputStream));
				logger.info(tid+" : Response = " + JSONResponse);
				
				if (JSONResponse.containsKey("access_token")) {
					token = (String) JSONResponse.get("access_token");
				}
				else {
					logger.info(tid+" : OAuth API Response = " + JSONResponse);
					token = "TOKEN_ERROR";
				}
			}

			else {
				InputStream inputStream = conn.getInputStream();
				JSONResponse = (JSONObject) parser.parse(new InputStreamReader(inputStream));
				logger.info(tid+" : Response = " + JSONResponse);
				token = "ErrorResponse";
			}

		} 
		catch (Exception e) {
			// Handle other exceptions
			logger.error(tid+" : Unexpected error occurred : " + e.getMessage());
			stacktrace.printStackTrace(logger, e);
		}
		return token;
		
	}
	
	
	private boolean isExpired(Date storedTimestamp) {
        long timeElapsed = new Date().getTime() - storedTimestamp.getTime();
        return timeElapsed > 3600000; // Expire after 1 hour
        //return timeElapsed > 4000; // Expire after 4 seconds
    }
	
	
	
	/*
	 * public void main(String args[]) { String Scope =
	 * "api.policyinfo.read"; String access_token =
	 * TokenInvocation.getInstance().getAccessToken(Scope); }
	 */
	 

}
