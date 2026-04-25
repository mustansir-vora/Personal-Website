package com.farmers.adminHost;

import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.AdminAPI.GetEPCBrandTableByBusinessObjects;
import com.farmers.AdminAPI.GetStateTableByAreaCode;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;
import com.farmers.report.SetHostDetails;

/*EM_ADM_002*/
public class BrandsStateGroup extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String StrExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		
		String strReqBody = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		try {

			LoggerContext context = (LoggerContext) data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);

			// EPC Brands Table
			if (data.getSessionData(Constants.S_EPC_BRANDS_URL) != null && data.getSessionData(Constants.S_DNIS) != null && data.getSessionData(Constants.S_CALLID) != null && data.getSessionData(Constants.S_CONN_TIMEOUT) != null && data.getSessionData(Constants.S_READ_TIMEOUT) != null) {
				
				SetHostDetails objHostDetails = new SetHostDetails(caa);
				objHostDetails.setinitalValue();

				String url = (String) data.getSessionData(Constants.S_EPC_BRANDS_URL);
				String callerId = (String) data.getSessionData(Constants.S_CALLID);
				String dnis = (String) data.getSessionData(Constants.S_DNIS);

				String connTimeoutStr = (String) data.getSessionData(Constants.S_CONN_TIMEOUT);
				String readTimeoutStr = (String) data.getSessionData(Constants.S_READ_TIMEOUT);

				GetEPCBrandTableByBusinessObjects apiObj = new GetEPCBrandTableByBusinessObjects();
				JSONObject resp = (JSONObject) apiObj.start(url, callerId, Constants.tenantid, dnis, Integer.parseInt(connTimeoutStr), Integer.parseInt(readTimeoutStr), context);
				strRespBody = resp.toString();
				data.addToLog(currElementName, "EPCBrandsByBusinessObjects API response  :" + resp);
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
				
				if (resp != null) {
					
					if (resp.containsKey(Constants.REQUEST_BODY)) {
						strReqBody = resp.get(Constants.REQUEST_BODY).toString();
					}
						
					if (resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200) {
						data.addToLog(currElementName, "Set BrandsByBusinessObjects API Response into session with the key name of " + currElementName + Constants._RESP);
						strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
						data.setSessionData(currElementName + Constants._RESP, resp.get(Constants.RESPONSE_BODY));

						if (resp.containsKey(Constants.RESPONSE_BODY)) {
							
							if (((JSONObject) resp.get(Constants.RESPONSE_BODY)).containsKey(("res"))) {
								
								JSONArray resArr = (JSONArray) ((JSONObject) resp.get(Constants.RESPONSE_BODY)).get("res");

								if (null != resArr && resArr.size() > 0) {
									JSONObject ArrayObject = (JSONObject) resArr.get(0);
									String EPCBrand = (String) ArrayObject.get("key");
									data.setSessionData("S_EPC_BRAND_LABEL", EPCBrand);
									data.addToLog(currElementName, "S_EPC_BRAND_LABEL ::" + EPCBrand);
								}

							}
						}
						StrExitState = Constants.SU;
					}
				}
				
				try {
					objHostDetails.startHostReport(currElementName, "EPCBrandsByDNIS API", strReqBody, "",(String) data.getSessionData(Constants.S_EPC_BRANDS_URL));
					objHostDetails.endHostReport(data, strRespBody,
							StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
				} catch (Exception e) {
					data.addToLog(currElementName, "Exception while forming host reporting for EPCBrandsByDNIS API call  :: " + e);
					caa.printStackTrace(e);
				}
			}
			

			// StateGroup Table
			if (data.getSessionData(Constants.S_STATEGROUPTABLE_URL) != null && data.getSessionData(Constants.CALL_ID) != null && data.getSessionData(Constants.S_AreaCode) != null && data.getSessionData(Constants.S_CONN_TIMEOUT) != null && data.getSessionData(Constants.S_READ_TIMEOUT) != null) {
				
				SetHostDetails objHostDetails = new SetHostDetails(caa);
				objHostDetails.setinitalValue();
				
				
				String url = (String) data.getSessionData(Constants.S_STATEGROUPTABLE_URL);
				String callId = (String) data.getSessionData(Constants.CALL_ID);
				String areaCode = (String) data.getSessionData(Constants.S_AreaCode);

				String connTimeoutStr = (String) data.getSessionData(Constants.S_CONN_TIMEOUT);
				String readTimeoutStr = (String) data.getSessionData(Constants.S_READ_TIMEOUT);

				GetStateTableByAreaCode apiObj = new GetStateTableByAreaCode();
				JSONObject resp = (JSONObject) apiObj.start(url, callId, Constants.tenantid, areaCode, Integer.parseInt(connTimeoutStr), Integer.parseInt(readTimeoutStr), context);
				data.addToLog(currElementName, currElementName + " : State Group Table API response  :" + resp);
				

				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
				
				if (resp != null) {
					
					if (resp.containsKey(Constants.REQUEST_BODY)) {
						strReqBody = resp.get(Constants.REQUEST_BODY).toString();
					}
						
					data.setSessionData(currElementName + Constants._RESP, resp.get(Constants.RESPONSE_BODY));
					
					if (resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200 && resp.containsKey(Constants.RESPONSE_BODY)) {

						strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
						data.setSessionData(currElementName + Constants._RESP, resp.get(Constants.RESPONSE_BODY));
						data.addToLog(currElementName, "Set " + currElementName + " :  State Group Table API Response into session with the key name of " + currElementName + Constants._RESP);
						
						// JSONObject respObj = (JSONObject) new JSONParser().parse(strRespBody);
						// JSONArray resArr = (JSONArray) respObj.get("res");

						StrExitState = Constants.SU;
					} else {

						strRespBody = resp.get(Constants.RESPONSE_MSG).toString();
					}

				}
				try {
					objHostDetails.startHostReport(currElementName, "StateTableByAreaCode API", strReqBody,"", (String) data.getSessionData(Constants.S_STATEGROUPTABLE_URL));
					objHostDetails.endHostReport(data, strRespBody,
							StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
				} catch (Exception e) {
					data.addToLog(currElementName,
							"Exception while forming host reporting for StateTableByAreaCode API call  :: " + e);
					caa.printStackTrace(e);
				}
				
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in BrandsByBusinessObjects API call  :: " + e);
			caa.printStackTrace(e);
		}

		
		if (currElementName.contains("FMAA_ADM_006")) {
			data.setSessionData(Constants.S_MENU_SELECTION_KEY, data.getSessionData(Constants.S_BU) + ":FMAA_ADM_006:DEFAULT");
			data.addToLog(currElementName,"S_MENU_SELECTION_KEY for  FMAA_ADM_006 :: " + data.getSessionData(Constants.S_MENU_SELECTION_KEY));
		} else if (currElementName.contains("FMOM_ADM_005")) {
			data.setSessionData(Constants.S_MENU_SELECTION_KEY, data.getSessionData(Constants.S_BU) + ":FMOM_ADM_005:DEFAULT");
			data.addToLog(currElementName,"S_MENU_SELECTION_KEY for  FMOM_ADM_005 :: " + data.getSessionData(Constants.S_MENU_SELECTION_KEY));
		} //else if (currElementName.contains("FMOM_ADM_008")) {
			//data.setSessionData(Constants.S_MENU_SELECTION_KEY, data.getSessionData(Constants.S_BU) + ":FMOM_ADM_008:DEFAULT");
			//data.addToLog(currElementName,"S_MENU_SELECTION_KEY for  FMOM_ADM_008 :: " + data.getSessionData(Constants.S_MENU_SELECTION_KEY));
		//} 
		else if (currElementName.contains("SAAH_ADM_002")) {
			data.setSessionData(Constants.S_MENU_SELECTION_KEY, data.getSessionData(Constants.S_BU) + ":SAAH_ADM_002:DEFAULT");
			data.addToLog(currElementName,"S_MENU_SELECTION_KEY for  SAAH_ADM_002 :: " + data.getSessionData(Constants.S_MENU_SELECTION_KEY));
		}
		return StrExitState;
	}
}
