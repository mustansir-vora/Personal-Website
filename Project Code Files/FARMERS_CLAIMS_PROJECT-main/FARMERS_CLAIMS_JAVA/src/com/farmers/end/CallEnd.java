package com.farmers.end;

import java.io.Console;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONObject;

import com.audium.server.AudiumException;
import com.audium.server.proxy.EndCallInterface;
import com.audium.server.session.CallEndAPI;
import com.farmers.DialogFlowAPI.DialogflowHTTPS;
import com.farmers.util.Constants;

public class CallEnd implements EndCallInterface{

	public void onEndCall(CallEndAPI callEndAPI) throws AudiumException {
		
		try {
			
			String call_Outcome = callEndAPI.getHowCallEnded();
			callEndAPI.addToLog(callEndAPI.getCurrentElement(), "Call End reason :: " + call_Outcome);
			String strDR = (String) callEndAPI.getSessionData(Constants.CISCO_DR_FLAG) != null ? (String) callEndAPI.getSessionData(Constants.CISCO_DR_FLAG) : Constants.DR_DefaultValue;
			callEndAPI.addToLog(callEndAPI.getCurrentElement(), "DR Flag :: " + strDR);
			
			if (call_Outcome.equalsIgnoreCase(Constants.HANGUP) || strDR.equalsIgnoreCase("Y")) {
				
				if (strDR.equalsIgnoreCase("Y")) {
					callEndAPI.addToLog(callEndAPI.getCurrentElement(), "Call End reason is DR :: Invoking Detect Intent API");
				}
				else {
					callEndAPI.addToLog(callEndAPI.getCurrentElement(), "Call End reason is hangup :: Invoking Detect Intent API");
				}
				
				String language = (String) callEndAPI.getSessionData(Constants.ActiveLang) != null ? (String) callEndAPI.getSessionData(Constants.ActiveLang) : Constants.en_US;
				String URL = (String) callEndAPI.getSessionData(Constants.S_DF_ENDPOINT) != null ? (String) callEndAPI.getSessionData(Constants.S_DF_ENDPOINT) : Constants.EmptyString;
				callEndAPI.addToLog(callEndAPI.getCurrentElement(), "DF URL fetched from config :: " + URL);
				String projectID = (String) callEndAPI.getSessionData(Constants.S_GDF_AGENT_PROJECT_ID) != null ? (String) callEndAPI.getSessionData(Constants.S_GDF_AGENT_PROJECT_ID) : Constants.EmptyString;
				String agentLocation = (String) callEndAPI.getSessionData(Constants.S_GDF_AGENT_LOCATION) != null ? (String) callEndAPI.getSessionData(Constants.S_GDF_AGENT_LOCATION) : Constants.EmptyString;
				String agentID = (String) callEndAPI.getSessionData(Constants.S_GDF_AGENT_ID) != null ? (String) callEndAPI.getSessionData(Constants.S_GDF_AGENT_ID) : Constants.EmptyString;
				String sessionID = (String) callEndAPI.getSessionData(Constants.DF_SESSIONID) != null ? (String) callEndAPI.getSessionData(Constants.DF_SESSIONID) : Constants.EmptyString;
				if (sessionID.equalsIgnoreCase(Constants.EmptyString)) {
					sessionID = (String) callEndAPI.getSessionData(Constants.SESSION_PREFIX) + (String) callEndAPI.getSessionData(Constants.DF_GUID);
				}
				
				if (null != URL) {
					URL = URL.replace(Constants.PROJECT_ID, projectID).replace(Constants.AGENT_LOCATION, agentLocation).replace(Constants.AGENT_ID, agentID).replace(Constants.SESSION_ID, sessionID);
				}
				callEndAPI.addToLog(callEndAPI.getCurrentElement(), "DF URL post manipulation :: " + URL);
				
				String GUID = (String) callEndAPI.getSessionData(Constants.getCGUID) != null ? (String) callEndAPI.getSessionData(Constants.getCGUID) : Constants.EmptyString;
				String eventName = Constants.EmptyString;
				
				if (strDR.equalsIgnoreCase("Y")) {
					eventName = (String) callEndAPI.getSessionData(Constants.S_DR_EVENT_NAME) != null ? (String) callEndAPI.getSessionData(Constants.S_DR_EVENT_NAME) : Constants.EmptyString;
				}
				else {
					eventName = (String) callEndAPI.getSessionData(Constants.S_END_EVENT_NAME) != null ? (String) callEndAPI.getSessionData(Constants.S_END_EVENT_NAME) : Constants.EmptyString;
				}
				

				// Claims -CS1359601 Adding Parameters in Detect Intent API
				//Query Params:
				String strOriginalANI=(String) callEndAPI.getSessionData(Constants.DF_ORIGINALANI) != null ? (String) callEndAPI.getSessionData(Constants.DF_ORIGINALANI) : Constants.EmptyString;
				String strOriginalDNIS=(String) callEndAPI.getSessionData(Constants.DF_ORIGINALDNIS) != null ? (String) callEndAPI.getSessionData(Constants.DF_ORIGINALDNIS) : Constants.EmptyString;
				String strBUid=(String) callEndAPI.getSessionData(Constants.DF_BUID) != null ? (String) callEndAPI.getSessionData(Constants.DF_BUID) : Constants.EmptyString;	
				String strANI=(String) callEndAPI.getSessionData(Constants.DF_OANI) != null ? (String) callEndAPI.getSessionData(Constants.DF_OANI) : Constants.EmptyString;
				String strDNIS=(String) callEndAPI.getSessionData(Constants.DF_ODNIS) != null ? (String) callEndAPI.getSessionData(Constants.DF_ODNIS) : Constants.EmptyString;
				String strOCallID=(String) callEndAPI.getSessionData(Constants.DF_ONPREMCALLID) != null ? (String) callEndAPI.getSessionData(Constants.DF_ONPREMCALLID) : Constants.EmptyString;
				//Claims -CS1359601 Adding Parameters in Detect Intent API
				
				String token_Scope = (String) callEndAPI.getSessionData(Constants.S_DF_TOKEN_SCOPE) != null ? (String) callEndAPI.getSessionData(Constants.S_DF_TOKEN_SCOPE) : Constants.EmptyString;
				String connTimeout = (String) callEndAPI.getSessionData(Constants.S_CONN_TIMEOUT) != null ? (String) callEndAPI.getSessionData(Constants.S_CONN_TIMEOUT) : Constants.EmptyString;
				String readTimeout = (String) callEndAPI.getSessionData(Constants.S_READ_TIMEOUT) != null ? (String) callEndAPI.getSessionData(Constants.S_READ_TIMEOUT) : Constants.EmptyString;
				
				callEndAPI.addToLog(callEndAPI.getCurrentElement(), "Language :: " + language);
				callEndAPI.addToLog(callEndAPI.getCurrentElement(), "URL :: " + URL);
				callEndAPI.addToLog(callEndAPI.getCurrentElement(), "GUID :: " + GUID);
				callEndAPI.addToLog(callEndAPI.getCurrentElement(), "Event Name :: " + eventName);
				callEndAPI.addToLog(callEndAPI.getCurrentElement(), "Token Scope :: " + token_Scope);
				callEndAPI.addToLog(callEndAPI.getCurrentElement(), "Connection Timeout :: " + connTimeout);
				callEndAPI.addToLog(callEndAPI.getCurrentElement(), "Read Timeout :: " + readTimeout);
				
				callEndAPI.addToLog(callEndAPI.getCurrentElement(), "Original ANI :: " + strOriginalANI);
				callEndAPI.addToLog(callEndAPI.getCurrentElement(), "Original DNIS :: " + strOriginalDNIS);
				callEndAPI.addToLog(callEndAPI.getCurrentElement(), "BU ID is :: " + strBUid);
				callEndAPI.addToLog(callEndAPI.getCurrentElement(), "ANI  :: " + strANI);
				callEndAPI.addToLog(callEndAPI.getCurrentElement(), "DNIS :: " + strDNIS);
				callEndAPI.addToLog(callEndAPI.getCurrentElement(), "OnPrem call ID :: " + strOCallID);
				
				LoggerContext context = (LoggerContext)callEndAPI.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
				
				
				
				DialogflowHTTPS callAPI = new DialogflowHTTPS();
				JSONObject resp = callAPI.start(URL, GUID,strOriginalANI, strOriginalDNIS,strBUid,strANI,strDNIS,strOCallID,eventName ,language, token_Scope, Integer.parseInt(readTimeout), Integer.parseInt(connTimeout), context);
				
				callEndAPI.addToLog(callEndAPI.getCurrentElement(), "Detect Intent API response code :: " + resp.get("responseCode"));
				callEndAPI.addToLog(callEndAPI.getCurrentElement(), "Detect Intent API response :: " + resp);
			}
			else {
				callEndAPI.addToLog(callEndAPI.getCurrentElement(), "Not invoking Detect Intent API since call end reason was not user Disconnect/Hangup");
			}
			
			
			
		} catch (Exception e) {
			callEndAPI.addToLog(callEndAPI.getCurrentElement(), "Exception occured in onEndCall method :: Exception : " + e.getMessage());
			StringWriter stw = new java.io.StringWriter();
			PrintWriter pw = new java.io.PrintWriter(stw);
			e.printStackTrace(pw);
			callEndAPI.addToLog(callEndAPI.getCurrentElement(), stw.toString());
			try{
				stw.close();
				pw.close();
			}
			catch (Exception e1) {
				callEndAPI.addToLog(callEndAPI.getCurrentElement(), "Exception occurred while closing string writer :: " + e1.getMessage());
			}
		}
		
	}

}
