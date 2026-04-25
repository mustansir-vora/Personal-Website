package com.farmers.afterHoursCallBack;

import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.AdminAPI.GetStateGroupTableByKey;
import com.farmers.AdminAPI.GetStateTableByAreaCode;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class IsStateExcluded extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.STRING_NO;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);objHostDetails.setinitalValue();
		String strReqBody = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;
		
		String apiRespCode = Constants.EmptyString;
		try {
			if(data.getSessionData(Constants.S_GET_STATE_GROUP_TABLE_BY_KEY)  != null  && data.getSessionData(Constants.S_CONN_TIMEOUT) != null && data.getSessionData(Constants.S_READ_TIMEOUT) != null) {
				String url = (String)data.getSessionData(Constants.S_GET_STATE_GROUP_TABLE_BY_KEY);	
				String callerId = (String) data.getSessionData(Constants.S_CALLID);
				
				int conTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_CONN_TIMEOUT));
				int readTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_READ_TIMEOUT));
				LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
				
				String key=(String) data.getSessionData(Constants.S_SALES_CALLBACK_KEY);
				data.addToLog(currElementName,"The Key for API is ::"+key);
				GetStateGroupTableByKey test = new GetStateGroupTableByKey();
				JSONObject resp =  (JSONObject) test.start(url, callerId, Constants.tenantid, key , conTimeout, readTimeout, context);
				strRespBody = resp.toString();
				data.addToLog(currElementName, "State Exclusion API response  :"+resp);
				apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
				if(resp != null) {
					if(resp.containsKey(Constants.REQUEST_BODY)) strReqBody = resp.get(Constants.REQUEST_BODY).toString();
					if(resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200) {
						data.addToLog(currElementName, "Set State Exclusion API Response into session  "+currElementName+Constants._RESP);
						strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
						data.setSessionData(currElementName+Constants._RESP, resp.get(Constants.RESPONSE_BODY));
						strExitState = apiResponseManupulation(data, caa, currElementName, strRespBody);
					}
				}
			}
		}
		catch(Exception e) {
			data.addToLog(currElementName,"Exception in State Exclusion  API call  :: "+e);
			caa.printStackTrace(e);
		}
		try {
			objHostDetails.startHostReport(currElementName,"State Exclusion API", strReqBody, "",(String)data.getSessionData(Constants.S_GET_STATE_GROUP_TABLE_BY_KEY));
			objHostDetails.endHostReport(data,strRespBody , strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode," ");
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for StateAreaCode API call  :: "+e);
			caa.printStackTrace(e);
		}
		return strExitState;
		
		
	}
	private String apiResponseManupulation(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {
		String strExitState = Constants.STRING_NO;
		String strStateCode= (String)data.getSessionData(Constants.S_STATECODE);
		try {
			
			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			 JSONArray resArray = (JSONArray) resp.get("res");
			 JSONObject resObj = (JSONObject) resArray.get(0);
			 JSONArray stateArray = (JSONArray) resObj.get("state");
			 if (stateArray != null && stateArray.size()>0 ) {
			 boolean found = false;
			 for (Object s : stateArray) {
                 JSONObject stateObj = (JSONObject) s;
                 String strcode = (String) stateObj.get("code");
                 if (strStateCode.equalsIgnoreCase(strcode)) {
                     found = true;
                     strExitState = Constants.STRING_YES;
                     data.addToLog(currElementName, "State is Excluded::"+strcode);
                     break;
                 }
			 }
			 if (!found) {
				 strExitState = Constants.STRING_NO;
				 data.addToLog(currElementName, " The State is Not  Excluded ::"+strStateCode);
			 }
			 }
			 else {
				 strExitState = Constants.STRING_NO;
				 data.addToLog(currElementName, "No State is Excluded for the Scenario");
			 }
			
		}
		catch(Exception e) {
			data.addToLog(currElementName,"Exception in State Exclusion  API Manipulation   :: "+e);
			caa.printStackTrace(e);
		}
		return strExitState;
		
	}
}
