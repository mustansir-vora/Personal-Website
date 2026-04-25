package com.farmers.adminHost;

import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.AdminAPI.CheckAniExistByKey;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

/* SANI_ADM_001 */
public class AniGroup extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String StrExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);objHostDetails.setinitalValue();objHostDetails.setinitalValue();
		
		String strReqBody = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		try {
		if(data.getSessionData(Constants.S_CHECK_ANI_EXISTS_URL)  != null &&  data.getSessionData(Constants.S_ANI) != null
				&& data.getSessionData(Constants.S_CONN_TIMEOUT) != null && data.getSessionData(Constants.S_READ_TIMEOUT) != null) {
			String url = (String)data.getSessionData(Constants.S_CHECK_ANI_EXISTS_URL);
			String callerId = (String) data.getSessionData(Constants.S_CALLID);
			String key = (String) data.getSessionData(Constants.S_ANI);
			int conTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_CONN_TIMEOUT));
			int readTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_READ_TIMEOUT));
			LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
			CheckAniExistByKey test = new CheckAniExistByKey(); 
			JSONObject resp =  (JSONObject) test.start(url, callerId,Constants.tenantid, key, conTimeout, readTimeout, context);
			strRespBody = resp.toString();
			data.addToLog(currElementName, "CheckAniExistByKey API response  :"+resp);
			//Mustan - Alerting Mechanism ** Response Code Capture
			apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
			if(resp != null) {
				if(resp.containsKey(Constants.REQUEST_BODY)) strReqBody = resp.get(Constants.REQUEST_BODY).toString();
				if(resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200) {
					data.addToLog(currElementName, "Set SANI_ADM_001  CheckAniExistByKey API Response into session with the key name of "+currElementName+Constants._RESP);
					strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
					data.setSessionData(currElementName+Constants._RESP, resp.get(Constants.RESPONSE_BODY));
					apiResponseManupulation(data, caa, currElementName, strRespBody);
					StrExitState = Constants.SU;
				}
			}
		}
	} catch (Exception e) {
		data.addToLog(currElementName,"Exception in CheckAniExistByKey API call  :: "+e);
		caa.printStackTrace(e);
	}
	
		try {
			objHostDetails.startHostReport(currElementName,"CheckAniGroupByKey API", strReqBody,"", (String)data.getSessionData(Constants.S_CHECK_ANI_EXISTS_URL));
			objHostDetails.endHostReport(data,strRespBody , StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for CheckAniExistByKey API call  :: "+e);
			caa.printStackTrace(e);
		}
		
	return StrExitState;
	}
	
	private void apiResponseManupulation(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {
		try {
			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			boolean checkAniExistByKey = (boolean) resp.get("res");
			if (checkAniExistByKey) {
				data.setSessionData(Constants.S_ANI_EXIST, Constants.STRING_YES);
			} else {
				data.setSessionData(Constants.S_ANI_EXIST, Constants.STRING_NO);
			}
			data.addToLog(currElementName, "checkAniExistByKey :"+checkAniExistByKey);
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in apiResponseManupulation method  :: "+e);
			caa.printStackTrace(e);
		}
	}

}