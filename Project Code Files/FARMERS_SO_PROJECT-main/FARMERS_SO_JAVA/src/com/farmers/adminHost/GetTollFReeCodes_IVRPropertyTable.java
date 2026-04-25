package com.farmers.adminHost;

import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.AdminAPI.GetIVRPropertyByKey;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class GetTollFReeCodes_IVRPropertyTable extends DecisionElementBase{
	
	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String StrExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);objHostDetails.setinitalValue();
		String strReqBody = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		try {
		if(data.getSessionData(Constants.S_IVR_PROPERTIES_URL) != null && data.getSessionData(Constants.S_CONN_TIMEOUT) != null && data.getSessionData(Constants.S_READ_TIMEOUT) != null) {
			String url = (String)data.getSessionData(Constants.S_IVR_PROPERTIES_URL);
			String callerId = (String) data.getSessionData(Constants.S_CALLID);
			int conTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_CONN_TIMEOUT));
			int readTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_READ_TIMEOUT));
			LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
			GetIVRPropertyByKey test = new GetIVRPropertyByKey();
			JSONObject resp =  (JSONObject) test.start(url, callerId, Constants.tenantid, "TollFree Area Codes", conTimeout, readTimeout, context);
			strRespBody = resp.toString();
			data.addToLog(currElementName, "GetIVRPropertyByKey API response  :"+resp);
			//Mustan - Alerting Mechanism ** Response Code Capture
			apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
			if(resp != null) {
				if(resp.containsKey(Constants.REQUEST_BODY)) strReqBody = resp.get(Constants.REQUEST_BODY).toString();
				if(resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200) {
					data.addToLog(currElementName, "Set EM_ADM_002  GetIVRPropertyByKey API Response into session with the key name of "+currElementName+Constants._RESP);
					strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
					data.setSessionData(currElementName+Constants._RESP, resp.get(Constants.RESPONSE_BODY));
					StrExitState = apiResponseManupulation(data, caa, currElementName, strRespBody);
				}
			}
		}
	} catch (Exception e) {
		data.addToLog(currElementName,"Exception in GetIVRPropertyByKey API call  :: "+e);
		caa.printStackTrace(e);
	}
	
		try {
			objHostDetails.startHostReport(currElementName,"GetIVRPropertyByKey API", strReqBody, "",(String)data.getSessionData(Constants.S_IVR_PROPERTIES_URL));
			objHostDetails.endHostReport(data,strRespBody , StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for GetIVRPropertyByKey API call  :: "+e);
			caa.printStackTrace(e);
		}
		
	return StrExitState;
	}
	
	private String apiResponseManupulation(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {
		String StrExitState = Constants.ER;
		try {
			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			JSONArray resArr = (JSONArray)resp.get("res");
			if(resArr.size() == 0 ) return Constants.ER;
			JSONObject obj = (JSONObject) resArr.get(0);
			String tollFreeCodes = (String) obj.get("propertyvalue");
			data.addToLog(currElementName, "Property Value returned from API :: " + tollFreeCodes);
			String phoneNumber = (String) data.getSessionData(Constants.S_ANI);
			data.addToLog(currElementName, "Phone Number from session :: " + phoneNumber);
			phoneNumber = null != phoneNumber && phoneNumber.length() >= 3 ? phoneNumber.substring(0, 3) : Constants.EmptyString;
			if (null != tollFreeCodes && tollFreeCodes.contains(phoneNumber)) {
				data.setSessionData("IVRPROPERTY_ANIBYPASS", "YES");
				data.addToLog(currElementName, "IVR PROPERTY TABLE :: ANI BYPASS :: " + data.getSessionData("IVRPROPERTY_ANIBYPASS"));
			}
			else {
				data.setSessionData("IVRPROPERTY_ANIBYPASS", "NO");
				data.addToLog(currElementName, "IVR PROPERTY TABLE :: ANI BYPASS :: " + data.getSessionData("IVRPROPERTY_ANIBYPASS"));
			}
			StrExitState = Constants.SU;
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in apiResponseManupulation method  :: "+e);
			caa.printStackTrace(e);
		}
		return StrExitState;
	}

}
