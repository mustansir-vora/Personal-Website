package com.farmers.adminHost;

import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.AdminAPI.GetStateTableByAreaCode;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;
import com.farmers.report.SetHostDetails;

public class StateGroupTable extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String StrExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);
		objHostDetails.setinitalValue();
		String strReqBody = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;
		

		try {

			StrExitState = stateGroupTable(strRespBody, strRespBody, data, caa, currElementName, StrExitState);

		} catch (Exception e) {
			data.addToLog(currElementName, "Exception while forming host details for " + currElementName + " :: " + e);
			caa.printStackTrace(e);
		}


		if (currElementName.contains("FMAA_ADM_006")) {

			data.setSessionData(Constants.S_MENU_SELECTION_KEY,
					data.getSessionData(Constants.S_BU) + ":FMAA_ADM_006:DEFAULT");
			data.addToLog(currElementName,
					"S_MENU_SELECTION_KEY for  FMAA_ADM_006 :: " + data.getSessionData(Constants.S_MENU_SELECTION_KEY));

		} else if (currElementName.contains("FMOM_ADM_005")) {
			data.setSessionData(Constants.S_MENU_SELECTION_KEY,
					data.getSessionData(Constants.S_BU) + ":FMOM_ADM_005:DEFAULT");
			data.addToLog(currElementName,
					"S_MENU_SELECTION_KEY for  FMOM_ADM_005 :: " + data.getSessionData(Constants.S_MENU_SELECTION_KEY));
		} else if (currElementName.contains("FMOM_ADM_008")) {
			data.setSessionData(Constants.S_MENU_SELECTION_KEY,
					data.getSessionData(Constants.S_BU) + ":FMOM_ADM_008:DEFAULT");
			data.addToLog(currElementName,
					"S_MENU_SELECTION_KEY for  FMOM_ADM_008 :: " + data.getSessionData(Constants.S_MENU_SELECTION_KEY));
		} else if (currElementName.contains("SAAH_ADM_002")) {
			data.setSessionData(Constants.S_MENU_SELECTION_KEY,
					data.getSessionData(Constants.S_BU) + ":SAAH_ADM_002:DEFAULT");
			data.addToLog(currElementName,
					"S_MENU_SELECTION_KEY for  SAAH_ADM_002 :: " + data.getSessionData(Constants.S_MENU_SELECTION_KEY));
		}

		return StrExitState;
	}

	private String stateGroupTable(String strReqBody, String strRespBody, DecisionElementData data, CommonAPIAccess caa,
			String currElementName, String StrExitState) {
		
		
		SetHostDetails objHostDetails = new SetHostDetails(caa);
		objHostDetails.setinitalValue();
		
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		try {

			LoggerContext context = (LoggerContext) data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);

			if (data.getSessionData(Constants.S_STATEGROUPTABLE_URL) != null
					&& data.getSessionData(Constants.CALL_ID) != null
					&& data.getSessionData(Constants.S_AreaCode) != null
					&& data.getSessionData(Constants.S_CONN_TIMEOUT) != null
					&& data.getSessionData(Constants.S_READ_TIMEOUT) != null) {
				String url = (String) data.getSessionData(Constants.S_STATEGROUPTABLE_URL);
				String callId = (String) data.getSessionData(Constants.CALL_ID);
				String areaCode = (String) data.getSessionData(Constants.S_AreaCode);

				String connTimeoutStr = (String) data.getSessionData(Constants.S_CONN_TIMEOUT);
				String readTimeoutStr = (String) data.getSessionData(Constants.S_READ_TIMEOUT);

				GetStateTableByAreaCode apiObj = new GetStateTableByAreaCode();
				JSONObject resp = (JSONObject) apiObj.start(url, callId, Constants.tenantid, areaCode,
						Integer.parseInt(connTimeoutStr), Integer.parseInt(readTimeoutStr), context);

				data.addToLog(currElementName, currElementName + " : State Group Table API response  :" + resp);
				
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
				if (resp != null) {
					if (resp.containsKey(Constants.REQUEST_BODY))
						strReqBody = resp.get(Constants.REQUEST_BODY).toString();
					data.setSessionData(currElementName + Constants._RESP, resp.get(Constants.RESPONSE_BODY));
					if (resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200
							&& resp.containsKey(Constants.RESPONSE_BODY)) {

						strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
						data.setSessionData(currElementName + Constants._RESP, resp.get(Constants.RESPONSE_BODY));
						data.addToLog(currElementName,
								"Set " + currElementName
										+ " :  State Group Table API Response into session with the key name of "
										+ currElementName + Constants._RESP);
//
//						JSONObject respObj = (JSONObject) new JSONParser().parse(strRespBody);
//						JSONArray resArr = (JSONArray) respObj.get("res");

						StrExitState = Constants.SU;
					} else {

						strRespBody = resp.get(Constants.RESPONSE_MSG).toString();
					}

				}
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in " + currElementName + " State Group Table API call  :: " + e);
			caa.printStackTrace(e);
		}
		
		try {
			objHostDetails.startHostReport(currElementName, currElementName, strReqBody,"",(String) data.getSessionData(Constants.S_STATEGROUPTABLE_URL));
			objHostDetails.endHostReport(data, strRespBody, StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception while forming host reporting for " + currElementName
					+ " State Group Table API call  :: " + e);
			caa.printStackTrace(e);
		}

		return StrExitState;
	}
}
