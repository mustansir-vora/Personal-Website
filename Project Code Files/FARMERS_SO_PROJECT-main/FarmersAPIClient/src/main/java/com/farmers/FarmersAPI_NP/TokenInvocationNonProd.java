package com.farmers.FarmersAPI_NP;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Properties;

import org.apache.logging.log4j.core.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.farmers.APIUtil.Constants;
import com.farmers.APIUtil.Encrypt_Decrypt;
import com.farmers.APIUtil.stacktrace;
import com.farmers.bean.TokenBean;

public class TokenInvocationNonProd {
	
	public String getAccessToken(String scope, Logger logger, String tid) {
    	Date timestamp;
        timestamp = new Date();
        
        String rtnToken = null;
        
        TokenBean storedTokenBean = null;
             
        storedTokenBean = TokenStorageNP.getInstance().getToken(scope, logger, tid);
        if(storedTokenBean != null) {
            String extoken = storedTokenBean.getToken();
            Date exDate = storedTokenBean.getTokenCreated();  
            logger.info(tid+" : Token Avaialable");

            
            // Check for valid token and scope
            if (extoken != null && !isExpired(exDate)) {
            	logger.info(tid+" : Using existing token for scope : " + scope);
                rtnToken = extoken;
            } else {
            	logger.info(tid+" : Token Not Generated/Expired : Generating new token for scope : " + scope);
                // Fetch new token and save
                String newToken = getToken(scope, logger, tid);
                timestamp = new Date();
        		TokenBean tknBean = new TokenBean(newToken, timestamp);
        		TokenStorageNP.getInstance().putToken(scope, tknBean, logger, tid);
                rtnToken = newToken;
            } 
        } else {
			
        	//generate new token 
        	logger.info(tid+" : Generating new token for Scope : " + scope);
        	TokenInvocationNonProd tokeninvocation = new TokenInvocationNonProd();
        	String token = tokeninvocation.getToken(scope, logger, tid); 
        	Date tStamp = new Date(); 
        	TokenBean tknBean = new TokenBean(token, tStamp);
        	TokenStorageNP.getInstance().putToken(scope, tknBean, logger, tid);
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
	        
	        String Endpoint = prop.getProperty("Farmers.Token.NonProd.URL");
			String Base64 = prop.getProperty("Farmers.Host.NonProd.Auth");
			String Client_ID = prop.getProperty("Farmers.Host.NonProd.ClientID");
			String Client_Secret = prop.getProperty("Farmers.Host.NonProd.ClientSecret");
			String frms_region = prop.getProperty("Farmers.Host.NonProd.frms_region");
			String Content_Type = prop.getProperty("Farmers.Host.content_type");
			String frms_appid = prop.getProperty("Farmers.Host.frms_appid");
			String frms_source = prop.getProperty("Farmers.Host.frms_source");
			String charset = prop.getProperty("Farmers.Host.charset");
			String URLParameters = prop.getProperty("Farmers.Host.URLParameters")+Scope;
			
			logger.info(tid+" : Non Prod Token URL :: "+Endpoint);
			
			logger.info(tid+" : Token Encrypted client id : "+Client_ID+", Token Encrypted client secret : "+Client_Secret);
			
			Encrypt_Decrypt Decryptor = new Encrypt_Decrypt();
			Client_ID = Decryptor.decrypt(Client_ID);
			Client_Secret = Decryptor.decrypt(Client_Secret);
			
			logger.info(tid+" : Token Decrypted client id : "+Client_ID+", Token Decrypted client secret : "+Client_Secret);
			
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
