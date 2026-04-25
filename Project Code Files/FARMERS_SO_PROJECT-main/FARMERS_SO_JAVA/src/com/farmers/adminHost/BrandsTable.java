package com.farmers.adminHost;

import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.AdminAPI.GetEPCBrandTableByBusinessObjects;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;
import com.farmers.report.SetHostDetails;

/*EM_ADM_002*/
public class BrandsTable extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String StrExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);
		objHostDetails.setinitalValue();
		String strReqBody = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		try {
			if (data.getSessionData(Constants.S_EPC_BRANDS_URL) != null && data.getSessionData(Constants.S_DNIS) != null && data.getSessionData(Constants.S_CALLID) != null && data.getSessionData(Constants.S_CONN_TIMEOUT) != null && data.getSessionData(Constants.S_READ_TIMEOUT) != null) {
				
				String url = (String) data.getSessionData(Constants.S_EPC_BRANDS_URL);
				String callerId = (String) data.getSessionData(Constants.S_CALLID);
				String dnis = (String) data.getSessionData(Constants.S_DNIS);

				String connTimeoutStr = (String) data.getSessionData(Constants.S_CONN_TIMEOUT);
				String readTimeoutStr = (String) data.getSessionData(Constants.S_READ_TIMEOUT);

				LoggerContext context = (LoggerContext) data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);

				GetEPCBrandTableByBusinessObjects apiObj = new GetEPCBrandTableByBusinessObjects();
				JSONObject resp = (JSONObject) apiObj.start(url, callerId, Constants.tenantid, dnis, Integer.parseInt(connTimeoutStr), Integer.parseInt(readTimeoutStr), context);

				strRespBody = resp.toString();
				data.addToLog(currElementName, "EPC BrandsByDNIS API response  :" + resp);
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
				if (resp != null) {
					
					if (resp.containsKey(Constants.REQUEST_BODY)) {
						strReqBody = resp.get(Constants.REQUEST_BODY).toString();
					}
						
					if (resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200) {
						
						data.addToLog(currElementName, "Set EPC BrandsByDNIS API Response into session with the key name of "+ currElementName + Constants._RESP);
						strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
						data.setSessionData(currElementName + Constants._RESP, resp.get(Constants.RESPONSE_BODY));

						if (resp.containsKey(Constants.RESPONSE_BODY)) {
							
							if (((JSONObject) resp.get(Constants.RESPONSE_BODY)).containsKey(("res"))) {
								
								JSONArray resArr = (JSONArray) ((JSONObject) resp.get(Constants.RESPONSE_BODY)).get("res");

								if (null != resArr && resArr.size() > 0) {

									JSONObject ArrayObject = (JSONObject) resArr.get(0);
									String EPCBrand = (String) ArrayObject.get("key");
									data.setSessionData("S_EPC_BRAND_LABEL", EPCBrand);
									data.addToLog(currElementName, "S_EPC_BRAND_LABEL :: " + EPCBrand);
								}
							}
						}
						StrExitState = Constants.SU;
					}
				}
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in EPC BrandsByDNIS API call  :: " + e);
			caa.printStackTrace(e);
		}

		try {
			objHostDetails.startHostReport(currElementName, "EPCBrandsByDNIS API", strReqBody, "",(String) data.getSessionData(Constants.S_EPC_BRANDS_URL));
			objHostDetails.endHostReport(data, strRespBody, StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception while forming host reporting for EPC BrandsByDNIS API call  :: " + e);
			caa.printStackTrace(e);
		}

		if (currElementName.contains("FMAA_ADM_006")) {
			data.setSessionData(Constants.S_MENU_SELECTION_KEY, data.getSessionData(Constants.S_BU) + ":FMAA_ADM_006:DEFAULT");
			caa.createMSPKey(caa, data, "FMAA_ADM_006", "DEFAULT");
		} else if (currElementName.contains("FMOM_ADM_005")) {
			data.setSessionData(Constants.S_MENU_SELECTION_KEY, data.getSessionData(Constants.S_BU) + ":FMOM_ADM_005:DEFAULT");
			caa.createMSPKey(caa, data, "FMOM_ADM_005", "DEFAULT");
		} //else if (currElementName.contains("FMOM_ADM_008")) {
			//data.setSessionData(Constants.S_MENU_SELECTION_KEY, data.getSessionData(Constants.S_BU) + ":FMOM_ADM_008:DEFAULT");
			//caa.createMSPKey(caa, data, "FMOM_ADM_008", "DEFAULT");
		//} 
		else if (currElementName.contains("SAAH_ADM_002")) {
			data.setSessionData(Constants.S_MENU_SELECTION_KEY, data.getSessionData(Constants.S_BU) + ":SAAH_ADM_002:DEFAULT");
			caa.createMSPKey(caa, data, "SAAH_ADM_002", "DEFAULT");
		}

		return StrExitState;
	}
}