package com.farmers.shared.host;

import java.util.HashMap;

import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.AdminAPI.GetAniValidation;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class CheckANIIsMobileGet extends DecisionElementBase{

	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException{
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);
		String strReqBody = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;
		
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		
		try {
			if(data.getSessionData(Constants.S_IVRTT_GET_ANI_CHECK) != null) {
			GetAniValidation objGetAniValidation = new GetAniValidation();
			int conTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_CONN_TIMEOUT));
			int readTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_READ_TIMEOUT));
			String callerId = (String) data.getSessionData(Constants.S_CALLID);
			String url = (String) data.getSessionData(Constants.S_IVRTT_GET_ANI_CHECK);
			String ani = (String)data.getSessionData(Constants.S_ANI);
			data.addToLog(currElementName, "ANI from session : " + ani);
			if(ani.length() == 10) ani = "1" + ani;
			data.addToLog(currElementName, "ANI for IsMobile Check : " + ani);
			LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
			
			JSONObject resp = objGetAniValidation.start(url, callerId, ani, conTimeout, readTimeout, context);
			data.addToLog(currElementName, "CheckANIIsMobileGet API response  :"+resp);
			
			//Mustan - Alerting Mechanism ** Response Code Capture
			apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
			if(resp != null) {
				if(resp.containsKey(Constants.REQUEST_BODY)) strReqBody = resp.get(Constants.REQUEST_BODY).toString();
				if(resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200 && resp.containsKey(Constants.RESPONSE_BODY)) {
					data.addToLog(currElementName, "Set CheckANIIsMobileGet  Response into session with the key name of "+currElementName+Constants._RESP);
					strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
					data.setSessionData(currElementName+Constants._RESP, resp.get(Constants.RESPONSE_BODY));
					strExitState = apiResponseManupulation(data, caa, currElementName, strRespBody);
				} else {
					strRespBody = resp.get(Constants.RESPONSE_MSG).toString();
				}
			}
			}
		} catch(Exception e) {
			data.addToLog(currElementName,"Exception in CheckANIIsMobileGet API call  :: "+e);
			caa.printStackTrace(e);
		}

		try {
			objHostDetails.startHostReport(currElementName,"IVRToTextIntents", strReqBody,"",(String) data.getSessionData(Constants.S_IVRTT_GET_ANI_CHECK));
			objHostDetails.endHostReport(data,strRespBody , strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for CheckANIIsMobileGet API call  :: "+e);
			caa.printStackTrace(e);
		}
		return strExitState;

	}
	
	private String apiResponseManupulation(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {
		String strExitState = Constants.ER;
		try {
			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			JSONArray resArr = (JSONArray)resp.get("res");
			if(null == resArr || resArr.size() == 0) return Constants.ER;
			data.addToLog(currElementName,"CheckANIIsMobileGet API resp size :"+resArr.size());
			JSONObject	tempObj = (JSONObject) resArr.get(0);
			if(tempObj.containsKey("ismobile") && null != tempObj.get("ismobile") && ((String)tempObj.get("ismobile")).equalsIgnoreCase(Constants.TRUE))
				data.setSessionData(Constants.IS_MOBILE_CALLER, Constants.STRING_YES);
			else data.setSessionData(Constants.IS_MOBILE_CALLER, Constants.STRING_NO);
			data.addToLog(currElementName, " IS_MOBILE_CALLER : "+data.getSessionData(Constants.IS_MOBILE_CALLER));
			strExitState = Constants.SU;
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in CheckANIIsMobileGet apiResponseManupulation method  :: "+e);
			caa.printStackTrace(e);
		}
		return strExitState;
	}
}
