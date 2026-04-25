package com.farmers.host;

import java.util.HashMap;

import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;
import com.farmers.FarmersAPI.EPCPaymentusGroup_Get;
import com.farmers.FarmersAPI.FWSEPCPaymentus_Get;
import com.farmers.FarmersAPI_NP.EPCPaymentusGroup_NP_Get;
import com.farmers.report.SetHostDetails;

public class TBFP_HOST_002 extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {

		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);
		objHostDetails.setinitalValue();
		String strReqBody = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;
		
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
        
		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
		String region=null;
        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
        //END- UAT ENV SETUP CODE (PRIYA, SHAIK)
		try {
			if (data.getSessionData(Constants.S_CONN_TIMEOUT) != null && data.getSessionData(Constants.S_READ_TIMEOUT) != null) {


				String url = (String) data.getSessionData(Constants.S_EPCPaymentusGroup_URL_GET);

				String ANI = (String) data.getSessionData(Constants.S_ANI);

				data.addToLog(currElementName, "S_EPCPaymentusGroup_URL_GET: " + url);

				if(url.contains(Constants.S_ANI)) url = url.replace(Constants.S_ANI, ANI);

				data.addToLog(currElementName,"POLICYNUM API URL : "+ url);

				int conTimeout = Integer.valueOf((String) data.getSessionData(Constants.S_CONN_TIMEOUT));
				int readTimeout = Integer.valueOf((String) data.getSessionData(Constants.S_READ_TIMEOUT));
				String callerId = (String) data.getSessionData(Constants.S_CALLID);
				LoggerContext context = (LoggerContext) data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);

				EPCPaymentusGroup_Get obj = new EPCPaymentusGroup_Get();

				//START- UAT ENV SETUP CODE (PRIYA, SHAIKH)
				data.addToLog("API URL: ", url);
				String prefix = "https://api-np-ss.farmersinsurance.com"; 
				String UAT_FLAG="";
		        if(url.startsWith(prefix)) {
		        	UAT_FLAG="YES";
		        }
				EPCPaymentusGroup_NP_Get objNP = new EPCPaymentusGroup_NP_Get();
				JSONObject resp=null ;
				if("YES".equalsIgnoreCase(UAT_FLAG)) {
					String Key =Constants.S_EPCPaymentusGroup_URL_GET;
					 region = regionDetails.get(Key);
					data.addToLog("Region for UAT endpoint: ", region);
					resp = objNP.start(url, callerId, conTimeout, readTimeout, context, region);
				}else {
					region="PROD";
				    resp = obj.start(url, callerId, conTimeout, readTimeout, context);
				}
                  //END- UAT ENV SETUP CODE (PRIYA, SHAIK)

				data.addToLog(currElementName, " API response  :" + resp);
				
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));

				if (resp != null) {
					if (resp.containsKey(Constants.REQUEST_BODY)) {
						strReqBody = resp.get(Constants.REQUEST_BODY).toString();
					}
					if (resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200 && resp.containsKey(Constants.RESPONSE_BODY)) {
						data.addToLog(currElementName, "Set MS_EPCPaymentusGroup_URL_GET Summary Lookup API Response into session with the key name of " + currElementName + Constants._RESP);
						strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
						data.setSessionData(currElementName + Constants._RESP, resp.get(Constants.RESPONSE_BODY));

						strExitState = apiResponseManipulation(data, caa, currElementName, strRespBody);
					} else {
						strRespBody = resp.get(Constants.RESPONSE_MSG).toString();
						strExitState = Constants.ER;
					}

				}
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Set S_EPCPaymentusGroup_URL_GET Summary Lookup API Response into session with the key name of \"  :: " + e);
			caa.printStackTrace(e);
		}
		
		try {
			objHostDetails.startHostReport(currElementName, "epcPaymentusGroup-GET", strReqBody, region,(String) data.getSessionData(Constants.S_EPCPaymentusGroup_URL_GET));
			objHostDetails.endHostReport(data, strRespBody, strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e1) {
			data.addToLog(currElementName, "Exception in EPC_PaymentUS-Get API Call Method :: " + e1);
			caa.printStackTrace(e1);
		}

		data.setSessionData("S_EPCPaymentusGroup_URL_GET_API", "True");
		return strExitState;



	}

	private String apiResponseManipulation(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {

		String strExitState = Constants.ER;
		try {
			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			data.addToLog(currElementName, "Value of Response Body : " + resp);

			//JSONObject responseBody = (JSONObject) resp.get("responseBody");
			JSONObject transferStatus = (JSONObject) resp.get("transferStatus");
			String status = (String) transferStatus.get("status");

			if (status != null && (status.equalsIgnoreCase("ACCOUNT-VALIDATED") || status.equalsIgnoreCase("PAYMENT-SUBMITTED"))) {
				data.addToLog(currElementName, "Account validated successfully.");
				data.setSessionData("S_Payment_Success", "Y");
				strExitState = Constants.SU;
				//				String BU=(String) data.getSessionData("S_TRANS_BACK_FROM_PMTS_BU");
				//				if(null != BU && BU.equalsIgnoreCase("FOREMOST")) {
				//					data.setSessionData(Constants.S_MENU_SELCTION_KEY, data.getSessionData(Constants.S_BU)+":EPC Return call AARP "+status.replace("-", "_"));
				//				}else if(null != BU && BU.equalsIgnoreCase("BW")){
				//					data.setSessionData(Constants.S_MENU_SELCTION_KEY, data.getSessionData(Constants.S_BU)+":EPC Return call BW "+status.replace("-", "_"));
				//				}
				caa.createMSPKeyTransferBack(caa, data, "EPC Return call", status.replace("-", "_"));
			} else {
				data.addToLog(currElementName, "Account validation failed or status not found.");
				data.setSessionData("S_Payment_Success", "N");
				strExitState = Constants.ER;
				//				String BU=(String) data.getSessionData("S_TRANS_BACK_FROM_PMTS_BU");
				//				if(null != BU && BU.equalsIgnoreCase("FOREMOST")) {
				//					data.setSessionData(Constants.S_MENU_SELCTION_KEY, data.getSessionData(Constants.S_BU)+":EPC Return call AARP "+status.replace("-", "_"));
				//				}else if(null != BU && BU.equalsIgnoreCase("BW")){
				//					data.setSessionData(Constants.S_MENU_SELCTION_KEY, data.getSessionData(Constants.S_BU)+":EPC Return call BW "+status.replace("-", "_"));
				//				}
				caa.createMSPKeyTransferBack(caa, data, "EPC Return call", status.replace("-", "_"));
			}
			data.addToLog(currElementName, "FINAL VALUE OF MSP FORMED: "+(String)data.getSessionData(Constants.S_MENU_SELCTION_KEY));
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in apiResponseManupulation method  :: " + e);
			caa.printStackTrace(e);
		}
		return strExitState;
	}


}
