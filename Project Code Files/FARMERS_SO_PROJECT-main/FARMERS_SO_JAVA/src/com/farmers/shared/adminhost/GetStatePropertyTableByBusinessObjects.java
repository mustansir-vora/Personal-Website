package com.farmers.shared.adminhost;


import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.client.Lookupcall;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class GetStatePropertyTableByBusinessObjects extends DecisionElementBase  {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException  {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);
		String strReqBody = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		
		try {
			if(data.getSessionData(Constants.S_GetStatePropertyTableByBusinessObjects_URL) != null && data.getSessionData(Constants.S_CONN_TIMEOUT) != null && data.getSessionData(Constants.S_READ_TIMEOUT) != null ) {
				String wsurl = (String) data.getSessionData(Constants.S_GetStatePropertyTableByBusinessObjects_URL);
				String callid = (String) data.getSessionData(Constants.S_CALLID);
				int conTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_CONN_TIMEOUT));
				int readTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_READ_TIMEOUT));
				String lob = (String) data.getSessionData(Constants.S_BU);
				//String  cateGory = "DED";
				String  cateGory =  (String) data.getSessionData(Constants.S_CATEGORY);
				LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
				Lookupcall lookups = new Lookupcall();
				JSONObject resp = lookups.GetStatePropertyTableByBusinessObjects(wsurl,callid,Constants.tenantid,lob,cateGory,conTimeout, readTimeout,context);
				data.addToLog(currElementName, "GetStatePropertyTableByBusinessObjects API response  :"+resp);
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
				
				if(resp != null) {
					if(resp.containsKey(Constants.REQUEST_BODY)) strReqBody = resp.get(Constants.REQUEST_BODY).toString();
					if(resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200 && resp.containsKey(Constants.RESPONSE_BODY)) {
						data.addToLog(currElementName, "Set GetStatePropertyTableByBusinessObjects  Response into session with the key name of "+currElementName+Constants._RESP);
						strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
						data.setSessionData(currElementName+Constants._RESP, resp.get(Constants.RESPONSE_BODY));

						data.setSessionData(Constants.ACCLINKANIJSONRESPSTRING, strRespBody);
						data.addToLog(currElementName, "GetStatePropertyTableByBusinessObjects String : "+data.getSessionData(Constants.ACCLINKANIJSONRESPSTRING));
						strExitState = apiResponseManupulation(data, caa, currElementName, strRespBody);

					} else {
						strRespBody = resp.get(Constants.RESPONSE_MSG).toString();
					}

				}
			}
		} catch (Exception e)
		{
			data.addToLog(currElementName,"Exception in GetStatePropertyTableByBusinessObjects API call  :: "+e);
			caa.printStackTrace(e);
		}
	
		try {
			objHostDetails.startHostReport(currElementName,"GetStatePropertyTableByBusinessObjects", strReqBody,"", (String) data.getSessionData(Constants.S_GetStatePropertyTableByBusinessObjects_URL));
			objHostDetails.endHostReport(data,strRespBody , strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for GetStatePropertyTableByBusinessObjects call  :: "+e);
			caa.printStackTrace(e);
		}
		
		return strExitState;
	}

	
	private String apiResponseManupulation(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {
		String strExitState = Constants.ER;
		boolean isEmailEnabled = false;
		boolean isMailEnabled = false;
		boolean isFaxEnabled = false;
		try {
			String strPolicyStateCode = (String) data.getSessionData(Constants.S_POLICY_STATE_CODE);
			if (null == strPolicyStateCode || "".equalsIgnoreCase(strPolicyStateCode)) {
				strPolicyStateCode = (String) data.getSessionData(Constants.S_STATE_CODE);
				

			//START : CS1169221 : Get state code for BW ID Card Email Enhancements
				if (null == strPolicyStateCode || "".equalsIgnoreCase(strPolicyStateCode)) {
					strPolicyStateCode = (String) data.getSessionData(Constants.S_STATECODE);
				     data.addToLog("Policy state Code S_STATECODE : ", strPolicyStateCode);
						        
				   } else {
				       data.addToLog("Policy state Code :", strPolicyStateCode);
							    }
			//END : CS1169221 : Get state code for BW ID Card Email Enhancements
			}
			data.addToLog("Policy state Code", strPolicyStateCode);
			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			JSONArray resArr = (JSONArray) resp.get("res");
			if(resArr!=null && resArr.size() > 0) {
				for (int i = 0;i<resArr.size();i++) {
					JSONObject statePropertyObj =  (JSONObject) resArr.get(i);
					if(null != statePropertyObj.get("state")) {
						JSONArray stateArr = (JSONArray)  statePropertyObj.get("state");
						JSONObject stateObj =  (JSONObject) stateArr.get(0);
						if(null != strPolicyStateCode && null != stateObj.get("code") && strPolicyStateCode.equalsIgnoreCase((String)stateObj.get("code"))) {
							data.addToLog(currElementName, "matched statePropertyObj based on policy state code : "+statePropertyObj.toString());
							if(((String)statePropertyObj.get("email")).equalsIgnoreCase("1")) isEmailEnabled = true;
							if(((String)statePropertyObj.get("mail")).equalsIgnoreCase("1")) isMailEnabled = true;
							if(((String)statePropertyObj.get("fax")).equalsIgnoreCase("1")) isFaxEnabled = true;
							break;
						}
						else {
							data.addToLog(currElementName, "StatePropertyObj did not match based on State Code :: " + strPolicyStateCode);
						}
					}
					else {
						data.addToLog(currElementName, "Could not find State Array in StatePropertyObj :: " + statePropertyObj);
					}
				}
			} else {
				data.addToLog(currElementName, "resArr is null or 0 size for State property list:");
			}
			
			if(isEmailEnabled && isMailEnabled && isFaxEnabled) data.setSessionData(Constants.S_EMAIL_MAIL_FAX_ENABLED, Constants.EMF);
			else if(isMailEnabled && isFaxEnabled) data.setSessionData(Constants.S_EMAIL_MAIL_FAX_ENABLED, Constants.FM);
			else if(isMailEnabled && isEmailEnabled) {
				data.setSessionData(Constants.S_EMAIL_MAIL_FAX_ENABLED, Constants.EM);
				if(null != data.getSessionData(Constants.S_BU) && Constants.FWS.equalsIgnoreCase((String) data.getSessionData(Constants.S_BU))) {
					data.setSessionData(Constants.S_COMMUNICATION_SOURCE, Constants.Mail);
				}
			} else if(isMailEnabled) {
				data.setSessionData(Constants.S_EMAIL_MAIL_FAX_ENABLED, Constants.M);
				data.setSessionData(Constants.S_COMMUNICATION_SOURCE, Constants.Mail);
			}
			data.addToLog(currElementName, "isEmailEnabled : "+isEmailEnabled+" :: isMailEnabled : "+isMailEnabled+" :: isFaxEnabled : "+isFaxEnabled+" S_EMAIL_MAIL_FAX_ENABLED : "+data.getSessionData(Constants.S_EMAIL_MAIL_FAX_ENABLED));
			strExitState = Constants.SU;
		} catch (Exception e)  {
			data.addToLog(currElementName,"Exception in apiResponseManupulation method  :: "+e);
			caa.printStackTrace(e);
		}
		return strExitState;
	}
	
	
}
