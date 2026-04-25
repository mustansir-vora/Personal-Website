package com.farmers.adminHost;

import java.util.ArrayList;

import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.AdminAPI.GetStateGroupTableByStateName;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class StateNameLookUP extends DecisionElementBase {

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
		if(data.getSessionData(Constants.S_GET_STATE_KEY_BY_NAME_URL)  != null  && data.getSessionData(Constants.S_CONN_TIMEOUT) != null && data.getSessionData(Constants.S_READ_TIMEOUT) != null) {
			String url = (String)data.getSessionData(Constants.S_GET_STATE_KEY_BY_NAME_URL);
			String callerId = (String) data.getSessionData(Constants.S_CALLID);
			String strStateName = (String) data.getSessionData(Constants.S_STATENAME);
			int conTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_CONN_TIMEOUT));
			int readTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_READ_TIMEOUT));
			LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
			GetStateGroupTableByStateName test = new GetStateGroupTableByStateName(); 
			JSONObject resp =  (JSONObject) test.start(url, callerId, Constants.tenantid, strStateName , conTimeout, readTimeout, context);
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
			objHostDetails.startHostReport(currElementName,"CheckAniExistByKey API", strReqBody,"", (String)data.getSessionData(Constants.S_GET_STATE_KEY_BY_NAME_URL));
			objHostDetails.endHostReport(data,strRespBody , strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for StateAreaCode API call  :: "+e);
			caa.printStackTrace(e);
		}
		
	return strExitState;
	}
	
	private String apiResponseManupulation(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {
		String strExitState = Constants.ER;
		ArrayList<String> stateGroup = new ArrayList<String>();
		try {	
			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			JSONArray resArr = (JSONArray)resp.get("res");
			if(resArr.size() == 0 ) return Constants.ER;
			for(int i = 0; i<resArr.size();i++) {
				JSONObject stategObj = (JSONObject) resArr.get(i);
				String key =(String)stategObj.get("key");
				stateGroup.add(key);
				data.addToLog(currElementName, "key :"+key);
			
			}
			data.addToLog(currElementName, "State Group :"+stateGroup.toString());
			data.setSessionData(Constants.S_STATE_GROUP, stateGroup);
			strExitState = Constants.SU;
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in apiResponseManupulation method  :: "+e);
			caa.printStackTrace(e);
		}
		return strExitState;
	}
	

}