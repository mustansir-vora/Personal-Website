package com.farmers.shared.adminhost;

import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.AdminAPI.GetEPCBrandTableByBusinessObjects;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class BW_Billing_EPCBrandsTableLookup extends DecisionElementBase{

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
				data.addToLog(currElementName, "EPCBrands API response  :" + resp);
				
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
				if (resp != null) {
					
					if (resp.containsKey(Constants.REQUEST_BODY)) {
						strReqBody = resp.get(Constants.REQUEST_BODY).toString();
					}
						
					
					if (resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200) {
						data.addToLog(currElementName, "Set EPCBrands API Response into session with the key name of " + currElementName + Constants._RESP);
						strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
						data.setSessionData(currElementName + Constants._RESP, resp.get(Constants.RESPONSE_BODY));

						if (resp.containsKey(Constants.RESPONSE_BODY)) {
							
							if (((JSONObject) resp.get(Constants.RESPONSE_BODY)).containsKey(("res"))) {
							
								JSONArray resArr = (JSONArray) ((JSONObject) resp.get(Constants.RESPONSE_BODY)).get("res");

								if (null != resArr && resArr.size() > 0) {
									
									JSONObject EPCBrandObj = (JSONObject) resArr.get(0);
									String EPCBrand = (String) EPCBrandObj.get("key");
									data.addToLog(currElementName, "EPC Brand :: " + EPCBrand);
									
									data.setSessionData("S_EPC_BRAND_LABEL", EPCBrand);
								}
							}
						}
						StrExitState = Constants.SU;
					}
					else {
						strRespBody = (String) resp.get("responseMsg");
					}
				}

			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in EPCBrands API call  :: " + e);
			caa.printStackTrace(e);
		}

		try {
			objHostDetails.startHostReport(currElementName, "EPCBrandsByDNIS API", strReqBody,"",(String) data.getSessionData(Constants.S_EPC_BRANDS_URL));
			objHostDetails.endHostReport(data, strRespBody,
			StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception while forming host reporting for EPCBrands API call  :: " + e);
			caa.printStackTrace(e);
		}

		return StrExitState;
	}
}
