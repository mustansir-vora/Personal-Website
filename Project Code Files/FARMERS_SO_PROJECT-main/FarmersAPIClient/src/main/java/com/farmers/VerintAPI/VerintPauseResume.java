package com.farmers.VerintAPI;

import java.util.HashMap;

import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONObject;

import com.farmers.APIUtil.Constants;
import com.farmers.APIUtil.stacktrace;
import com.farmers.FarmersAPI.APIConnectionXML;
import com.farmers.bean.RequestBean;

public class VerintPauseResume {

	public boolean verintPauseResume(String url, String action, String guid, LoggerContext context, int conntimeout, int readtimeout) {
		boolean returnValue = false;
		
		Logger logger = context.getLogger(Constants.FARMERS_LOGGER_NAME);
		logger.info(guid + " : Verint Pause/Resume start, action called : " + action);
		
		String  respValue = "";
		
		url = url.replace("&&ACTION&&", action);
		url = url.replace("&&GUID&&", guid);
		
		logger.info(guid + " : Final URL called : " + url);
		
		APIConnectionXML connection = null;
		
		try {
			connection = new APIConnectionXML();
			HashMap<String, String> headers = new HashMap<String, String>();
			//headers.put("Authorization", getAuthHeader(url, action, guid, logger));
			RequestBean requestBean = new RequestBean(url, null, "GET", headers);
			
			JSONObject response = connection.sendHttpRequest(requestBean, logger, guid, conntimeout, readtimeout);
			logger.info(guid + " : Final Response = " + response);
			respValue = (String)response.get("responseBody");
			
			if(respValue.contains("<success>true</success><isMaster>true</isMaster>")) {
				returnValue = true;
			}
		} catch (Exception e) {
			logger.error(guid+" : Unexpected error occurred : " + e.getMessage());
			stacktrace.printStackTrace(logger, e);
			e.printStackTrace();
		}
		return returnValue;
	}
}
