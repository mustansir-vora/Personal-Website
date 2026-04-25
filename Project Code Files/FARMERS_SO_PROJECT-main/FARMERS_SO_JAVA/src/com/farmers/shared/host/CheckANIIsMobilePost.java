package com.farmers.shared.host;


import java.util.HashMap;

import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONObject;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.CiscoAPI.WxConnect_PostANI;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class CheckANIIsMobilePost extends DecisionElementBase{

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException{
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);
		String strReqBody = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;
		
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		
		try {
			if(data.getSessionData(Constants.S_IVRTT_POST_ANI_CHECK) != null) {
				data.addToLog(currElementName, "Called ANIPost");
				String ani = (String)data.getSessionData(Constants.S_ANI);
				data.addToLog(currElementName, "ANI from session : " + ani);
				if(ani.length() == 10) {
					ani = "1" + ani;
				}
				data.addToLog(currElementName, "ANI for webhook : " + ani);
				
				LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
				
				int conTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_CONN_TIMEOUT));
				int readTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_READ_TIMEOUT));
				String callerId = (String) data.getSessionData(Constants.S_CALLID);
				String url = (String) data.getSessionData(Constants.S_IVRTT_POST_ANI_CHECK);
				
				WxConnect_PostANI objWxConnect_PostANI = new WxConnect_PostANI();
				JSONObject respObj = objWxConnect_PostANI.start(url, callerId, ani, conTimeout, readTimeout, context);
				data.addToLog(currElementName, "Response from ANI WebHook : " + respObj);
				
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) respObj.get(Constants.RESPONSE_CODE));
				if(respObj != null) {
					if(respObj.containsKey(Constants.REQUEST_BODY)) strReqBody = respObj.get(Constants.REQUEST_BODY).toString();
					if(respObj.containsKey(Constants.RESPONSE_CODE) && (int) respObj.get(Constants.RESPONSE_CODE) == 200) {
						data.addToLog(currElementName, "Check ANI Post Req : " + strReqBody);
						strRespBody = respObj.get(Constants.RESPONSE_BODY).toString();
						data.setSessionData(currElementName+Constants._RESP, respObj.get(Constants.RESPONSE_BODY));
						data.addToLog(currElementName, "Check ANI Post Res : " + strRespBody);
						strExitState = Constants.SU;
					}
				}
			}
		} catch(Exception e) {
			data.addToLog(currElementName,"Exception in CheckANIIsMobilePost API call  :: "+e);
			caa.printStackTrace(e);
		}

		try {
			objHostDetails.startHostReport(currElementName,"CheckANIIsMobilePost", strReqBody, "",(String) data.getSessionData(Constants.S_IVRTT_POST_ANI_CHECK));
			objHostDetails.endHostReport(data,strRespBody , strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for CheckANIIsMobilePost API call  :: "+e);
			caa.printStackTrace(e);
		}
		return strExitState;
	}	
}