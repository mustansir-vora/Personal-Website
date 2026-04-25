package com.farmers.FarmersAPI_NP;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.net.URL;
//import java.net.http.HttpConnectTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.apache.logging.log4j.core.Logger;
import org.json.simple.JSONObject;

import org.json.simple.parser.JSONParser;

import com.farmers.APIUtil.stacktrace;
import com.farmers.bean.RequestBean;


public class APIConnectionXML {
	
	
	public JSONObject sendHttpRequest(RequestBean requestBean, Logger logger, String tid, int conntimeout, int readtimeout)  {
		
		JSONObject jsonResp = new JSONObject();
        HttpsURLConnection connection = null;
        BufferedReader reader = null;
        JSONParser parser = null;
        URL url = null;
        JSONObject reqbody = null;
        Date date = new Date();
        int responseCode = 0;
        String apistarttime = null;
        String apiendtime = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:MM:ss.SSS");
        
        
        try {
            url = new URL(requestBean.getUrl());
            
            logger.info(tid+" : API url = "+url);
            
            apistarttime = sdf.format(new Date());
            logger.info(tid+" : Logging API Start Time : "+apistarttime);
            connection = (HttpsURLConnection) url.openConnection();
            
            // Set connection and read timeouts (in milliseconds)
            connection.setConnectTimeout(conntimeout); // 10 seconds
            connection.setReadTimeout(readtimeout); // 10 seconds
            connection.setRequestMethod(requestBean.getMethodType());
            logger.info(tid+" : ConnTimeOut : "+conntimeout+", ReadTimeOut : "+readtimeout+", MethodType : "+requestBean.getMethodType());
            setRequestHeaders(connection, requestBean.getHeaders(), logger);

            if (requestBean.getRequestBody() != null) {
            	reqbody = (JSONObject) parser.parse(requestBean.getRequestBody());
            	logger.info(tid+" : Request Body : "+requestBean.getRequestBody());
                if (requestBean.getMethodType().equalsIgnoreCase("POST")) {
                    connection.setDoOutput(true);
                    OutputStream outputStream = connection.getOutputStream();
                    outputStream.write(requestBean.getRequestBody().getBytes());
                    outputStream.flush();
                    outputStream.close();
                }
            }

            responseCode = connection.getResponseCode();
            logger.info(tid+" : API Response Code = "+responseCode);

            if (responseCode == 200) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }

            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
                logger.info(tid + " : Raw response - "+response);
            }
            
            if (responseCode == 200 && response != null && !response.toString().isEmpty()) {
                logger.info(tid+" : API Response = "+response);
                jsonResp = formatSuccessResponse(response.toString(), responseCode, "", apistarttime, requestBean.getUrl());
            } else {
                logger.info(tid+" : API Response Code = " + responseCode + ", API Response = "+response);
                jsonResp = formatErrorResponse(response.toString(), responseCode, "", apistarttime, requestBean.getUrl());
                
            }
        }
        catch (SocketTimeoutException e) {
        	logger.error(tid+" : Socket timeout Exception error occurred : " + e.getMessage());
        	jsonResp = formatErrorResponse("TimeOut Exception Occured", 408, reqbody.toString(), apistarttime, requestBean.getUrl());
			stacktrace.printStackTrace(logger, e);
			e.printStackTrace();
		}
        catch (Exception e) {
			// Handle other exceptions
			logger.error(tid+" : Unexpected error occurred : " + e.getMessage());
			stacktrace.printStackTrace(logger, e);
			e.printStackTrace();
		} 
        
        finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                	logger.error(tid+ " : Unexpected error occurred : " + e.getMessage());
        			stacktrace.printStackTrace(logger, e);
                }
            }
            if (connection != null) {
                connection.disconnect();
                apiendtime = sdf.format(new Date());
                logger.info(tid + " : Logging API end time : "+apiendtime);
                jsonResp.put("APIEndTime", apiendtime);
                }
            }
        return jsonResp;
    }
	
	public JSONObject formatSuccessResponse(String resp,int responsecode, String requestbody, String apistarttime, String url) {
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
	
	public JSONObject formatErrorResponse(String error,int statusCode, String requestbody, String apistarttime, String url) {
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
	
	private void setRequestHeaders(HttpsURLConnection connection, Map<String, String> headers, Logger logger) {
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
                logger.info("Header : " + entry.getKey());   
                logger.info("Value : " + entry.getValue());
            }
        }
    }
	

	/*
	 * public void main(String[] args) {
	 * String Scope = "api.agentinfo.read"; 
	 * String access_token=TokenInvocation.getToken(Scope);
	 * System.out.println("token = "+access_token); }
	 */
 
 }
