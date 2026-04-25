package com.farmers.AdminAPI;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;

import org.apache.logging.log4j.core.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.farmers.APIUtil.stacktrace;
import com.farmers.bean.TokenBean;
import com.farmers.APIUtil.Constants;
import com.farmers.APIUtil.Encrypt_Decrypt;
import com.farmers.APIUtil.GetSSLSocketFactory;

public class TokenGeneration {
	
	HostnameVerifier validHosts = new HostnameVerifier() {
		  @Override
		  public boolean verify(String arg0, SSLSession arg1) {
		   return true;
		  }
		  };


	public String getAccessToken(Logger logger, String callid) {

		Date timestamp;
		timestamp = new Date();
		String rtnToken = null;
		TokenBean storedTokenBean = null;
		storedTokenBean = TokenStorage.getInstance().getToken("Admin", logger, callid);
		if (storedTokenBean != null) {
			String extoken = storedTokenBean.getToken();
			Date exDate = storedTokenBean.getTokenCreated();
			logger.info(callid+" : Token Avaialable");

			// Check for valid token and scope
			if (extoken != null && !isExpired(exDate)) {
				logger.info(callid+" : Using existing token");
				rtnToken = extoken;
			} else {
				logger.info(callid+" : Token Not Generated/Expired : Generating new token");
				// Fetch new token and save
				String newToken = getToken(logger, callid);
				timestamp = new Date();
				TokenBean tknBean = new TokenBean(newToken, timestamp);
				TokenStorage.getInstance().putToken("Admin", tknBean, logger, callid);
				rtnToken = newToken;
			}
		} else {
			//generate new token
			logger.info(callid+" : Generating new token");
			String token = getToken(logger, callid);
			Date tStamp = new Date();
			TokenBean tknBean = new TokenBean(token, tStamp);
			TokenStorage.getInstance().putToken("Admin", tknBean, logger, callid);
			rtnToken = token;
	} 
		return rtnToken;

	}

	public String getToken(Logger logger, String callid) {
		JSONObject jsonResp = null;
		HttpsURLConnection connection = null;
		BufferedReader reader = null;
		JSONParser parser = null;
		URL url = null;
		JSONObject reqbody = null;
		
		String token = null;
		String apistarttime = null;
        String apiendtime = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:MM:ss.SSS");
        HttpsURLConnection.setDefaultHostnameVerifier(validHosts);
        
		try {
			Properties prop = new Properties();
	        prop.load(new FileInputStream(Constants.CONFIG_PATH));
	        String urls = prop.getProperty("Admin.Token.URL");
	        //	String urls = "https://EC2AMAZ-ALDVADR/fiivrapi/api/JWTAuthentication/GetAccessToken";
			
			parser = new JSONParser();
			url = new URL(urls);
			reqbody = getrequestBody(prop, logger, callid);
			logger.info(callid+" : OAuth url = " + url);
			
			apistarttime = sdf.format(new Date());
            logger.info(callid+" : Logging OAuth API Start Time : "+apistarttime);
			connection = (HttpsURLConnection) url.openConnection();
			
			SSLSocketFactory sslSocketFactory = GetSSLSocketFactory.getInstance().getsslsocket(callid, logger);

			// Set connection and read timeouts (in milliseconds)
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setSSLSocketFactory(sslSocketFactory);
			connection.setRequestMethod("POST");

			connection.setDoOutput(true);
			OutputStream outputStream = connection.getOutputStream();
			outputStream.write(reqbody.toString().getBytes());
			outputStream.flush();
			outputStream.close();

			int responseCode = connection.getResponseCode();
			logger.info(callid+" : Token Response Code : " + responseCode);

			if (responseCode == 200) {
				reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			} else {
				reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
			}

			StringBuilder response = new StringBuilder();
			String line;

			while ((line = reader.readLine()) != null) {
				response.append(line);
			}

			if (responseCode == 200 && response != null && !response.toString().isEmpty()) {
				jsonResp = (JSONObject) parser.parse(response.toString());
				logger.info(callid+" : OAuth API Response = " + jsonResp);
				if (jsonResp.containsKey("accessToken")) {
					token = (String) jsonResp.get("accessToken");
				}
				else {
					logger.info(callid+" : OAuth API Response = " + jsonResp);
					//token = "TOKEN_ERROR";
				}
			} else {
				logger.info(callid+" : OAuth API Response = " + jsonResp);
				//token = "TOKEN_ERROR";
			}
		} catch (Exception e) {
			// Handle other exceptions
			logger.error(callid+" : Unexpected error occurred : " + e.getMessage());
			stacktrace.printStackTrace(logger, e);
		}

		finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (Exception e) {
				}
			}
			if (connection != null) {
				connection.disconnect();
				apiendtime = sdf.format(new Date());
                logger.info(callid+" : Logging OAuth API end time : "+apiendtime);
                jsonResp.put("APIEndTime", apiendtime);
			}
		}
		logger.info(callid+" : token = " + token);
		return token;
	}

	public static JSONObject getrequestBody(Properties prop, Logger logger, String callid) {
		JSONObject reqbody = new JSONObject();
		try {
			Encrypt_Decrypt Decryptor = new Encrypt_Decrypt();

			String username = prop.getProperty("Admin.OAuth.userid");
			String password = prop.getProperty("Admin.OAuth.password");
			
			logger.info("Encrypted Username = "+username+" , Encrypted password = "+password);
			
			username = Decryptor.decrypt(username);
			password = Decryptor.decrypt(password);
			
			reqbody.put("username", username);
			reqbody.put("password", password);
		} catch (Exception e) {
			logger.error(callid+" : Unexpected error occurred : " + e.getMessage());
			stacktrace.printStackTrace(logger, e);
		}
		return reqbody;
	}

	private static boolean isExpired(Date storedTimestamp) {
		long timeElapsed = new Date().getTime() - storedTimestamp.getTime();
		return timeElapsed > 3600000; // Expire after 60 minutes
		 //return timeElapsed > 4000; // Expire after 4 seconds
	}

	/*
	 * public static void main(String[] args) {
	 * 
	 * String token = getAccessToken();
	 * 
	 * logger.info("Final token = "+token);
	 * 
	 * }
	 */
}
