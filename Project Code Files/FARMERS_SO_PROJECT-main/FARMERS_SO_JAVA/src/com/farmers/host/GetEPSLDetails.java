package com.farmers.host;
import java.util.HashMap;

import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.AdminAPI.GetEmployeeServiceLineDetails;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;
public class  GetEPSLDetails extends DecisionElementBase{
@Override
public String doDecision(String currElementName, DecisionElementData data) throws Exception {
	String StrExitState = Constants.ER;
	CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
	SetHostDetails objHostDetails = new SetHostDetails(caa);
	objHostDetails.setinitalValue();
	String strReqBody = Constants.EmptyString;
	String strRespBody = Constants.EmptyString;
	//Mustan - Alerting Mechanism ** Response Code Capture
	String apiRespCode = Constants.EmptyString;
	
	//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
	String region=null;
    HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
    //END- UAT ENV SETUP CODE (PRIYA, SHAIK)
	try {
		if(data.getSessionData(Constants.S_GET_EPSL_DETAILS_URL)  != null  && data.getSessionData(Constants.S_CONN_TIMEOUT) != null && data.getSessionData(Constants.S_READ_TIMEOUT) != null) {
	        String url=(String)data.getSessionData(Constants.S_GET_EPSL_DETAILS_URL);
			String callerId = (String) data.getSessionData(Constants.S_CALLID);
			String loginid=(String)data.getSessionData(Constants.S_AGENTID);
			int conTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_CONN_TIMEOUT));
			int readTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_READ_TIMEOUT));
			LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
			GetEmployeeServiceLineDetails test=new GetEmployeeServiceLineDetails();
			JSONObject resp=(JSONObject) test.start(url, callerId,loginid, conTimeout, readTimeout, context);
			strRespBody = resp.toString();
			data.addToLog(currElementName, "GetEPSLDetails API response  : "+resp);
			//Mustan - Alerting Mechanism ** Response Code Capture
			apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
			if(resp != null) {
				if(resp.containsKey(Constants.REQUEST_BODY)) strReqBody = resp.get(Constants.REQUEST_BODY).toString();
				if(resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200) {
					data.addToLog(currElementName, "Set EPSL_HOST_001  GetEPSLDetails Response into session with the key name of "+currElementName+Constants._RESP);
					strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
					data.setSessionData(currElementName+Constants._RESP, resp.get(Constants.RESPONSE_BODY));
					StrExitState = apiResponseManupulation(data, caa, currElementName, strRespBody, StrExitState);
				}
				
			}
		
		}
	}
	 catch (Exception e) {
			data.addToLog(currElementName,"Exception in GetEPSLDetails API call  :: "+e);
			caa.printStackTrace(e);
		}
	try {
		objHostDetails.startHostReport(currElementName,"GetEPSLDetails API", strReqBody, region,(String)data.getSessionData(Constants.S_GET_EPSL_DETAILS_URL));
		objHostDetails.endHostReport(data,strRespBody , StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
	} catch (Exception e) {
		data.addToLog(currElementName,"Exception while forming host reporting for GetEPSLDetails API call  :: "+e);
		caa.printStackTrace(e);
	}
	
return StrExitState;

}
private String apiResponseManupulation(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody, String strExitState) {
	JSONArray resArr = new JSONArray();
	try {	
		JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
		if (resp.containsKey("res")) {
			resArr = (JSONArray)resp.get("res");
			if(null != resArr && resArr.size() > 0) { 
				JSONObject stategObj = (JSONObject) resArr.get(0);
				if (stategObj.containsKey("externalid") && null != stategObj.get("externalid")) {
					data.setSessionData(Constants.S_IEXUSERID,stategObj.get("externalid"));
				}
				if (stategObj.containsKey("leaveapplied") && null != stategObj.get("leaveapplied")) {
					data.setSessionData(Constants.S_LEAVEAPPLIED, stategObj.get("leaveapplied"));
				}
				
				}
		}
		strExitState = Constants.SU;
		//if(null == stategObj.get("externalid") || ((String)stategObj.get("externalid")).isEmpty())
		//data.setSessionData(Constants.S_IEXUSERID,stategObj.get("externalid"));
		//if(null== stategObj.get("leaveapplied") || ((String)stategObj.get("leaveapplied")).isEmpty()) return Constants.ER;
		//data.setSessionData(Constants.S_LEAVEAPPLIED, stategObj.get("leaveapplied"));
		
	} catch (Exception e) {
		data.addToLog(currElementName,"Exception in apiResponseManupulation method  :: "+e);
		caa.printStackTrace(e);
	}
	return strExitState;
}
}
