package com.farmers.adminHost;

import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.AdminAPI.GetStateTableByAreaCode;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class StateAreaCode extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);objHostDetails.setinitalValue();
		String strReqBody = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;
		
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		try {
		if(data.getSessionData(Constants.S_GET_STATE_BY_AREACODE_URL)  != null  && data.getSessionData(Constants.S_CONN_TIMEOUT) != null && data.getSessionData(Constants.S_READ_TIMEOUT) != null) {
			String url = (String)data.getSessionData(Constants.S_GET_STATE_BY_AREACODE_URL);
			String callerId = (String) data.getSessionData(Constants.S_CALLID);
			String strAreaCode = (String) data.getSessionData(Constants.S_AREACODE);
			int conTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_CONN_TIMEOUT));
			int readTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_READ_TIMEOUT));
			LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
			GetStateTableByAreaCode test = new GetStateTableByAreaCode(); 
			JSONObject resp =  (JSONObject) test.start(url, callerId, Constants.tenantid, strAreaCode , conTimeout, readTimeout, context);
			strRespBody = resp.toString();
			data.addToLog(currElementName, "StateAreaCode API response  :"+resp);
			//Mustan - Alerting Mechanism ** Response Code Capture
			apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
			if(resp != null) {
				if(resp.containsKey(Constants.REQUEST_BODY)) strReqBody = resp.get(Constants.REQUEST_BODY).toString();
				if(resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200) {
					data.addToLog(currElementName, "Set StateAreaCode API Response into session with the key name of "+currElementName+Constants._RESP);
					strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
					data.setSessionData(currElementName+Constants._RESP, resp.get(Constants.RESPONSE_BODY));
					strExitState = apiResponseManupulation(data, caa, currElementName, strRespBody);
				}
			}
		}
	} catch (Exception e) {
		data.addToLog(currElementName,"Exception in StateAreaCode API call  :: "+e);
		caa.printStackTrace(e);
	}
	
		try {
			objHostDetails.startHostReport(currElementName,"StateAreaCode API", strReqBody, "",(String)data.getSessionData(Constants.S_GET_STATE_BY_AREACODE_URL));
			objHostDetails.endHostReport(data,strRespBody , strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for StateAreaCode API call  :: "+e);
			caa.printStackTrace(e);
		}
		
	return strExitState;
	}
	
	private String apiResponseManupulation(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {
		String strExitState = Constants.ER;
		try {	
			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			JSONArray resArr = (JSONArray)resp.get("res");
			if(null == resArr || resArr.size() == 0 ) return Constants.ER;
			JSONObject stategObj = (JSONObject) resArr.get(0);
			//if(null == stategObj.get("name") || ((String)stategObj.get("name")).isEmpty() || "Toll-Free".equalsIgnoreCase((String)stategObj.get("name"))) return Constants.ER;
			if(null == stategObj.get("name") || ((String)stategObj.get("name")).isEmpty()) return Constants.ER;
			data.setSessionData(Constants.S_STATENAME, stategObj.get("name"));
			data.setSessionData("S_ORIGINAL_STATE_NAME", stategObj.get("name"));
			data.setSessionData(Constants.S_STATECODE, stategObj.get("code"));
			data.setSessionData(Constants.S_FARMERS_STATECODE, stategObj.get("farmerstatecode"));
			String stateName = (String)data.getSessionData(Constants.S_STATENAME);
			if(stateName.contains(" ")) stateName = stateName.replaceAll(" ", ".");
			data.setSessionData(Constants.VXMLParam1, stateName);
			data.setSessionData(Constants.VXMLParam2, "NA");
        	data.setSessionData(Constants.VXMLParam3, "NA");
        	data.setSessionData(Constants.VXMLParam4, "NA");
			data.addToLog(currElementName, "VXMLParam1 :"+data.getSessionData(Constants.S_STATENAME));
			data.addToLog(currElementName, "S_STATENAME :"+data.getSessionData(Constants.S_STATENAME)+" : S_STATECODE : "+data.getSessionData(Constants.S_STATECODE)+" : S_FARMERS_STATECODE : "+data.getSessionData(Constants.S_FARMERS_STATECODE));
			strExitState = Constants.SU;
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in apiResponseManupulation method  :: "+e);
			caa.printStackTrace(e);
		}
		return strExitState;
	}
	

}