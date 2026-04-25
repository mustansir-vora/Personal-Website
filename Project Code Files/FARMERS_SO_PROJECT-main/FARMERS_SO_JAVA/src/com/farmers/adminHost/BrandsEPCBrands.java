package com.farmers.adminHost;

import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.AdminAPI.GetBrandTableByBusinessObjects;
import com.farmers.AdminAPI.GetEPCBrandTableByBusinessObjects;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

/*EM_ADM_002*/
public class BrandsEPCBrands extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String StrExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		String strReqBody = Constants.EmptyString;
		String strBrandRespBody = Constants.EmptyString;
		String strEPCRespBody = Constants.EmptyString;
		String url = Constants.EmptyString;
		String callerId = Constants.EmptyString;
		String dnis = Constants.EmptyString;
		String connTimeoutStr = Constants.EmptyString;
		String readTimeoutStr = Constants.EmptyString;
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		try {

			LoggerContext context = (LoggerContext) data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);

			if (data.getSessionData(Constants.S_BRANDSTABLE_URL) != null && data.getSessionData(Constants.S_DNIS) != null && data.getSessionData(Constants.S_CALLID) != null && data.getSessionData(Constants.S_DNISGROUP) != null && data.getSessionData(Constants.S_LOB) != null && data.getSessionData(Constants.S_CATEGORY) != null && data.getSessionData(Constants.S_CONN_TIMEOUT) != null && data.getSessionData(Constants.S_READ_TIMEOUT) != null) {
				
				url = (String) data.getSessionData(Constants.S_BRANDSTABLE_URL);
				callerId = (String) data.getSessionData(Constants.S_CALLID);
				dnis = (String) data.getSessionData(Constants.S_DNIS);
				String dnisgroup = (String) data.getSessionData(Constants.S_DNISGROUP);
				String category = (String) data.getSessionData(Constants.S_CATEGORY);
				String lob = (String) data.getSessionData(Constants.S_LOB);

				connTimeoutStr = (String) data.getSessionData(Constants.S_CONN_TIMEOUT);
				readTimeoutStr = (String) data.getSessionData(Constants.S_READ_TIMEOUT);

				GetBrandTableByBusinessObjects brandAPIObj = new GetBrandTableByBusinessObjects();
				JSONObject brandResp = (JSONObject) brandAPIObj.start(url, callerId, Constants.tenantid, dnis, dnisgroup, lob, category, Integer.parseInt(connTimeoutStr), Integer.parseInt(readTimeoutStr), context);
				strBrandRespBody = brandResp.toString();
				data.addToLog(currElementName, "BrandsByBusinessObjects Brands API response  :" + brandResp);
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) brandResp.get(Constants.RESPONSE_CODE));

				if (brandResp != null) {
					if (brandResp.containsKey(Constants.REQUEST_BODY)) {
						strReqBody = brandResp.get(Constants.REQUEST_BODY).toString();
					}
						
					if (brandResp.containsKey(Constants.RESPONSE_CODE) && (int) brandResp.get(Constants.RESPONSE_CODE) == 200) {
						
						data.addToLog(currElementName, "Set BrandsByBusinessObjects Brands API Response into session with the key name of " + currElementName + Constants._RESP);
						strBrandRespBody = brandResp.get(Constants.RESPONSE_BODY).toString();
						data.setSessionData(currElementName + Constants._RESP, brandResp.get(Constants.RESPONSE_BODY));

						if (brandResp.containsKey(Constants.RESPONSE_BODY)) {
							
							if (((JSONObject) brandResp.get(Constants.RESPONSE_BODY)).containsKey(("res"))) {
								
								JSONArray resArr = (JSONArray) ((JSONObject) brandResp.get(Constants.RESPONSE_BODY)).get("res");

								if (null != resArr && resArr.size() > 0) {

									data.setSessionData("S_BRAND_LABEL", ((JSONObject) resArr.get(0)).get("brandlabel").toString());
									data.addToLog(currElementName, "S_BRAND_LABEL ::"+ ((JSONObject) resArr.get(0)).get("brandlabel").toString());
								}

							}
						}
						StrExitState = Constants.SU;
						SetHostDetails objHostDetails = new SetHostDetails(caa);
						objHostDetails.setinitalValue();
						objHostDetails.startHostReport(currElementName, "GetBrandTableByBusinessObjects API", strReqBody,"", (String) data.getSessionData(Constants.S_BRANDSTABLE_URL));
						objHostDetails.endHostReport(data, strBrandRespBody, StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
						
					}
				}
			}
			if (data.getSessionData(Constants.S_EPC_BRANDS_URL) != null && data.getSessionData(Constants.S_DNIS) != null && data.getSessionData(Constants.S_CALLID) != null && data.getSessionData(Constants.S_CONN_TIMEOUT) != null && data.getSessionData(Constants.S_READ_TIMEOUT) != null) {

				GetEPCBrandTableByBusinessObjects epcBrandAPIObj = new GetEPCBrandTableByBusinessObjects();
				JSONObject epcBrandResp = (JSONObject) epcBrandAPIObj.start((String) data.getSessionData(Constants.S_EPC_BRANDS_URL), callerId, Constants.tenantid, dnis, Integer.parseInt(connTimeoutStr), Integer.parseInt(readTimeoutStr), context);

				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) epcBrandResp.get(Constants.RESPONSE_CODE));
				
				strEPCRespBody = epcBrandResp.toString();
				data.addToLog(currElementName, "EPCBrandsByBusinessObjects EPC Brands API response  :" + epcBrandResp);

				if (epcBrandResp != null) {
					
					if (epcBrandResp.containsKey(Constants.REQUEST_BODY)) {
						strReqBody = epcBrandResp.get(Constants.REQUEST_BODY).toString();
					}
						
					if (epcBrandResp.containsKey(Constants.RESPONSE_CODE) && (int) epcBrandResp.get(Constants.RESPONSE_CODE) == 200) {
						data.addToLog(currElementName, "Set BrandsEPCByDNIS API Response into session with the key name of " + currElementName + Constants._RESP);
						strEPCRespBody = epcBrandResp.get(Constants.RESPONSE_BODY).toString();
						data.setSessionData(currElementName + Constants._RESP, epcBrandResp.get(Constants.RESPONSE_BODY));

						if (epcBrandResp.containsKey(Constants.RESPONSE_BODY)) {
							
							if (((JSONObject) epcBrandResp.get(Constants.RESPONSE_BODY)).containsKey(("res"))) {
								
								JSONArray resArr = (JSONArray) ((JSONObject) epcBrandResp.get(Constants.RESPONSE_BODY)).get("res");

								if (null != resArr && resArr.size() > 0) {
									JSONObject ArrayObject = (JSONObject) resArr.get(0);
									String EPCBrand = (String) ArrayObject.get("key");
									data.setSessionData("S_EPC_BRAND_LABEL", EPCBrand);
									if (EPCBrand.contains("Foremost")) {

										data.setSessionData("S_SSAAH_MN_001_BRAND", "Specialty");
										data.addToLog(currElementName, "S_SSAAH_MN_001_BRAND :: " + (String) data.getSessionData("S_SSAAH_MN_001_BRAND"));

									} else {
										data.setSessionData("S_SSAAH_MN_001_BRAND", "BW");
										data.addToLog(currElementName, "S_SSAAH_MN_001_BRAND :: " + (String) data.getSessionData("S_SSAAH_MN_001_BRAND"));
									}

								}

							}
						}
						StrExitState = Constants.SU;
						SetHostDetails objHostDetails = new SetHostDetails(caa);
						objHostDetails.setinitalValue();
						objHostDetails.startHostReport(currElementName, "EPCBrandsByDNIS API", strReqBody,"", (String) data.getSessionData(Constants.S_EPC_BRANDS_URL));
						objHostDetails.endHostReport(data, strEPCRespBody, StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
					}
				}

			}
		} catch (

				Exception e) {
			data.addToLog(currElementName, "Exception in BrandsEPCByBusinessObjects API call  :: " + e);
			caa.printStackTrace(e);
		}

		if (currElementName.contains("FMAA_ADM_006")) {
			data.setSessionData(Constants.S_MENU_SELECTION_KEY,data.getSessionData(Constants.S_BU) + ":FMAA_ADM_006:DEFAULT");
			caa.createMSPKey(caa, data, "FMAA_ADM_006", "DEFAULT");
		} else if (currElementName.contains("FMOM_ADM_005")) {
			data.setSessionData(Constants.S_MENU_SELECTION_KEY,data.getSessionData(Constants.S_BU) + ":FMOM_ADM_005:DEFAULT");
			caa.createMSPKey(caa, data, "FMOM_ADM_005", "DEFAULT");
		} else if (currElementName.contains("FMOM_ADM_008")) {
			data.setSessionData(Constants.S_MENU_SELECTION_KEY,data.getSessionData(Constants.S_BU) + ":FMOM_ADM_008:DEFAULT");
			caa.createMSPKey(caa, data, "FMOM_ADM_008", "DEFAULT");
		} 
//		else if (currElementName.contains("SAAH_ADM_002")) {
//			data.setSessionData(Constants.S_MENU_SELECTION_KEY,data.getSessionData(Constants.S_BU) + ":SAAH_ADM_002:DEFAULT");
//			caa.createMSPKey(caa, data, "SAAH_ADM_002", "DEFAULT");
//		}

		return StrExitState;
	}
}