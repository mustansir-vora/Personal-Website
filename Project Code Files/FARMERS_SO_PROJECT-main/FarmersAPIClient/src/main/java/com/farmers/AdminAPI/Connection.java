package com.farmers.AdminAPI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
//import java.net.http.HttpConnectTimeoutException;
import java.security.KeyStore;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.apache.logging.log4j.core.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.farmers.APIUtil.GetSSLSocketFactory;
import com.farmers.APIUtil.stacktrace;
import com.farmers.bean.RequestBean;




public class Connection {
	
	HostnameVerifier validHosts = new HostnameVerifier() {
		  @Override
		  public boolean verify(String arg0, SSLSession arg1) {
		   return true;
		  }
		  };
		  
	
	@SuppressWarnings("unchecked")
	public JSONObject sendHttpsRequest(RequestBean requestBean, Logger logger, String callid, int conntimeout, int readtimeout)  {
        
        HttpsURLConnection connection = null;
        BufferedReader reader = null;
        JSONParser parser = null;
        URL url = null;
        JSONObject reqbody = null;
        String apistarttime = null;
        String apiendtime = null;
        JSONObject jsonResp = new JSONObject();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:MM:ss.SSS");
        HttpsURLConnection.setDefaultHostnameVerifier(validHosts);

        try {
            parser = new JSONParser();
            url = new URL(requestBean.getUrl());
            
            logger.info(callid+" : API url = "+url);
            
            LocalDateTime currentTime = LocalDateTime.now();
            apistarttime = sdf.format(new Date());
            logger.info(callid+" : Logging API Start Time : "+apistarttime);
            
            connection = (HttpsURLConnection) url.openConnection();
            
            SSLSocketFactory sslSocketFactory = GetSSLSocketFactory.getInstance().getsslsocket(callid, logger);
            
            // Set connection and read timeouts (in milliseconds)
            connection.setConnectTimeout(conntimeout); // 10 seconds
            connection.setReadTimeout(readtimeout); // 10 seconds
            connection.setSSLSocketFactory(sslSocketFactory);
            connection.setRequestMethod(requestBean.getMethodType());
            logger.info(callid+" : ConnTimeOut : "+conntimeout+", ReadTimeOut : "+readtimeout+", MethodType : "+requestBean.getMethodType());
            setRequestHeaders(connection, requestBean.getHeaders());

            if (requestBean.getRequestBody() != null) {
            	logger.info(callid+" : Request Body : "+requestBean.getRequestBody());
                if (requestBean.getMethodType().equalsIgnoreCase("POST")) {
                	reqbody = (JSONObject) parser.parse(requestBean.getRequestBody());
                    connection.setDoOutput(true);
                    OutputStream outputStream = connection.getOutputStream();
                    outputStream.write(requestBean.getRequestBody().getBytes());
                    outputStream.flush();
                    outputStream.close();
                }
            }

            int responseCode = connection.getResponseCode();
            logger.info(callid+" : Response Code : "+responseCode);
            
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
            
            if (responseCode == 200 && response != null && response.toString() != "") {
                jsonResp = (JSONObject) parser.parse(response.toString());
                logger.info(callid+" : API Response = "+jsonResp);
                jsonResp = formatSuccessResponse(jsonResp, responseCode, reqbody, apistarttime, requestBean.getUrl());
            } else {
                jsonResp = formatErrorResponse(response.toString(), responseCode, reqbody, apistarttime, requestBean.getUrl());
                logger.info(callid+" : API Response = "+jsonResp);
            }
        }
        catch (SocketTimeoutException e) {
        	logger.error(callid+" : Socket timeout Exception error occurred : " + e.getMessage());
        	jsonResp = formatErrorResponse("TimeOut Exception Occured", 408, reqbody, apistarttime, apiendtime);
			stacktrace.printStackTrace(logger, e);
			e.printStackTrace();
		}
        catch (Exception e) {
            // Handle other exceptions
            jsonResp = formatErrorResponse(callid+" : Unexpected error: " + e.getMessage(), 500, reqbody, apistarttime, requestBean.getUrl());
            logger.error(callid+" : Exception :"+e);
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
                logger.info(callid+" : Logging API end time : "+apiendtime);
                jsonResp.put("APIEndTime", apiendtime);
            }
        }
        return jsonResp;
    }
	
	
	public JSONObject formatSuccessResponse(JSONObject resp,int responsecode, JSONObject requestbody, String apistarttime, String url) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("responseCode", responsecode);
		jsonObject.put("responseMsg", "Success");
		jsonObject.put("responseBody", resp);
		if (null ==requestbody) {
			jsonObject.put("requestBody", url);
		}else {
			jsonObject.put("requestBody", requestbody);
		}
		jsonObject.put("APIStartTime", apistarttime);
		
		return jsonObject;
	}
	
	public JSONObject formatErrorResponse(String error,int statusCode, JSONObject requestbody, String apistarttime, String url) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("responseCode", statusCode);
		jsonObject.put("responseMsg", error);
		if (null ==requestbody) {
			jsonObject.put("requestBody", url);
		}else {
			jsonObject.put("requestBody", requestbody);
		}
		jsonObject.put("APIStartTime", apistarttime);
		return jsonObject;
	}
	
	private void setRequestHeaders(HttpURLConnection connection, Map<String, String> headers) {
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
    }

}
