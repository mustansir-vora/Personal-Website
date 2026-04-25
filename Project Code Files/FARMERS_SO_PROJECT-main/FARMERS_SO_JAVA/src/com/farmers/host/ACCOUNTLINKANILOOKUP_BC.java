package com.farmers.host;

import java.util.HashMap;

import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONObject;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.FarmersAPI.AccountLinkAniLookup_Post;
import com.farmers.FarmersAPI_NP.AccountLinkAniLookup_NP_Post;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;
import com.farmers.report.SetHostDetails;


public class ACCOUNTLINKANILOOKUP_BC extends DecisionElementBase {

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
		
		String region=null;
		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
        //END- UAT ENV SETUP CODE (PRIYA, SHAIK)
		try {
			
			if(null != data.getSessionData("S_ACCLINKANI_CALLED") && "TRUE".equalsIgnoreCase((String)data.getSessionData("S_ACCLINKANI_CALLED"))) {
				data.addToLog(currElementName, "AccountLinkANI is already called :: Response from session :: "+(String)data.getSessionData(Constants.ACCLINKANIJSONRESPSTRING));
				String resp = (String)data.getSessionData(Constants.ACCLINKANIJSONRESPSTRING);
				if (!resp.isEmpty()) {
					data.setSessionData("S_ANI_MATCH", "TRUE");
					return Constants.SU;
				}
			}
			
			LoggerContext context = (LoggerContext) data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
			if (data.getSessionData(Constants.S_ACCLINK_ANILOOKUP_URL) != null && data.getSessionData(Constants.S_CALLID) != null && data.getSessionData(Constants.S_READ_TIMEOUT) != null && data.getSessionData(Constants.S_CONN_TIMEOUT) != null && data.getSessionData(Constants.S_ANI) != null) {
				
				String url = (String) data.getSessionData(Constants.S_ACCLINK_ANILOOKUP_URL);
				String tid = (String) data.getSessionData(Constants.S_CALLID);
				String telephonenumber = (String) data.getSessionData(Constants.S_ANI);

				int conTimeout = Integer.valueOf((String) data.getSessionData(Constants.S_CONN_TIMEOUT));
				
				data.addToLog(currElementName, "Brands Table API conntimeout ::" + conTimeout);
				
				int readTimeout = Integer.valueOf((String) data.getSessionData(Constants.S_READ_TIMEOUT));
				
				data.addToLog(currElementName, "Brands Table API readtimeout ::" + readTimeout);
				
				AccountLinkAniLookup_Post apiObj=new AccountLinkAniLookup_Post();
				//START- Non prod changes-Priya
				data.addToLog("API URL: ", url);
				String prefix = "https://api-np-ss.farmersinsurance.com"; 
				String UAT_FLAG="";
		        if(url.startsWith(prefix)) {
		        	UAT_FLAG="YES";
		        }
				AccountLinkAniLookup_NP_Post objNP = new AccountLinkAniLookup_NP_Post();
				JSONObject resp=null ;
				if("YES".equalsIgnoreCase(UAT_FLAG)) {
					data.addToLog("S_ACCLINK_ANILOOKUP_URL: ", url);
					String Key =Constants.S_ACCLINK_ANILOOKUP_URL;
					region = regionDetails.get(Key);
					data.addToLog("Region for UAT endpoint: ", region);
					resp = objNP.start(url, tid, telephonenumber, conTimeout, readTimeout, context, region);
				}else {
					region="PROD";
				    resp = apiObj.start(url, tid, telephonenumber, conTimeout, readTimeout, context);
				}
				//END-Non prod changes-Priya
				
				data.addToLog(currElementName, "API response  :" + resp);
				
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
				
				if (resp != null) {
					
					if (resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200 && resp.containsKey(Constants.RESPONSE_BODY)) {
						
						data.setSessionData("S_ACCLINKANI_CALLED", "TRUE");
						data.addToLog(currElementName, "Set ACCOUNTLINKANILOOKUP_BC API Response into session with the key name of " + currElementName + Constants._RESP);
						
						JSONObject strRespBodyTemp = (JSONObject) resp.get(Constants.RESPONSE_BODY);
						
						strRespBody = (String) resp.get(Constants.RESPONSE_BODY).toString();
						strReqBody = (String) resp.get(Constants.REQUEST_BODY).toString();
						
						if (!strRespBodyTemp.isEmpty()) {
						
							StrExitState =Constants.SU;
							data.setSessionData(Constants.ACCLINKANIJSONRESPSTRING, strRespBody);

						}else {
								StrExitState = Constants.ER;
							}
						data.setSessionData(currElementName + Constants._RESP, resp.get(Constants.RESPONSE_BODY));
					} else {
						strRespBody = resp.get(Constants.RESPONSE_MSG).toString();
					}
				}
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in ACCOUNTLINKANILOOKUP_BC API call  :: " + e);
			caa.printStackTrace(e);
		}

		try {
			objHostDetails.startHostReport(currElementName, "ACCOUNTLINKANILOOKUP_BC", strReqBody,region, (String) data.getSessionData(Constants.S_ACCLINK_ANILOOKUP_URL));
			objHostDetails.endHostReport(data, strRespBody, StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception while forming host reporting for ACCOUNTLINKANILOOKUP_BC API call  :: " + e);
			caa.printStackTrace(e);
		}

		return StrExitState;
	}

}
