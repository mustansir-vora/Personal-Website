package com.farmers.host;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.FarmersAPI.AgentInfo_Post;
import com.farmers.FarmersAPI_NP.AgentInfo_NP_Post;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;
import com.farmers.report.SetHostDetails;

public class AECLookup_BC extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String StrExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);
		objHostDetails.setinitalValue();
		String strReqBody = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;
		

		try {

			StrExitState = agentInfo(strRespBody, strRespBody, data, caa, currElementName, StrExitState);

		} catch (Exception e) {
			data.addToLog(currElementName, "Exception while forming host details for " + currElementName + " :: " + e);
			caa.printStackTrace(e);
		}

		return StrExitState;
	}

	private String agentInfo(String strReqBody, String strRespBody, DecisionElementData data, CommonAPIAccess caa, String currElementName, String StrExitState) {
		SetHostDetails objHostDetails = new SetHostDetails(caa);
		
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		
		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
		String region=null;
        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
        //END- UAT ENV SETUP CODE (PRIYA, SHAIK)
		try {

			LoggerContext context = (LoggerContext) data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);

			if (data.getSessionData(Constants.S_ACE_LOOKUP_URL) != null && data.getSessionData(Constants.S_CALLID) != null && data.getSessionData(Constants.S_ANI) != null && data.getSessionData(Constants.S_BU) != null && data.getSessionData(Constants.S_CONN_TIMEOUT) != null && data.getSessionData(Constants.S_READ_TIMEOUT) != null) {
				String url = (String) data.getSessionData(Constants.S_ACE_LOOKUP_URL);
				data.addToLog(currElementName, "ACELookup API URL ::" + url);
				String businesssUnit = (String) data.getSessionData(Constants.S_BU);
				data.addToLog(currElementName, "ACELookup API businesssUnit ::" + businesssUnit);
				
				ArrayList<String> strBristolCode = (ArrayList<String>) data.getSessionData("BRISTOL_LOB_LIST");
				ArrayList<String>  strFarmersCode = (ArrayList<String>) data.getSessionData("FARMER_LOB_LIST");
				ArrayList<String>  strForemostCode = (ArrayList<String>) data.getSessionData("FOREMOST_LOB_LIST");
				ArrayList<String>  strFWSCode = (ArrayList<String>) data.getSessionData("FWS_LOB_LIST");
				ArrayList<String>  str21stCode = (ArrayList<String>) data.getSessionData("21st_LOB_LIST");
				
				data.addToLog(currElementName, " ACELOOKUP CLASS : strBU :: "+businesssUnit);
				data.addToLog(currElementName, " A_BRISTOL_LOB : "+strBristolCode);
				data.addToLog(currElementName, " A_FARMERS_LOB : "+strFarmersCode);
				data.addToLog(currElementName, " A_FOREMOST_LOB : "+strForemostCode);
				data.addToLog(currElementName, " A_FWS_LOB : "+strFWSCode);
				data.addToLog(currElementName, " A_21ST_LOB : "+str21stCode);
				
				if(strBristolCode!=null && null != businesssUnit && strBristolCode.contains(businesssUnit)) {
					businesssUnit = "IVRBW";
				} else if(strFarmersCode!=null && businesssUnit!=null && strFarmersCode.contains(businesssUnit)) {
					businesssUnit = "IVRPLA";
				} else if(strForemostCode!=null && businesssUnit!=null && strForemostCode.contains(businesssUnit)) {
					businesssUnit = "IVRFM";
				} else if(strFWSCode!=null && businesssUnit!=null && strFWSCode.contains(businesssUnit)) {
					businesssUnit = "IVRFWS";
				} else if(str21stCode!=null && businesssUnit!=null && str21stCode.contains(businesssUnit)) {
					businesssUnit = "IVRPLA";
				} else {
					businesssUnit = "IVRPLA";
				}

				data.addToLog(currElementName, "ACELookup API businesssUnit after changing ::" + businesssUnit);

				// Add condition to BU
				String userId = Constants.USER_ID;// hardcode as VRS-101
				data.addToLog(currElementName, "ACELookup API USER_ID ::" + userId);

				String phoneNumber = (String) data.getSessionData(Constants.S_ANI);
				data.addToLog(currElementName, "ACELookup API phoneNumber ::" + phoneNumber);
				String tid = (String) data.getSessionData(Constants.S_CALLID);// Caller ID
				data.addToLog(currElementName, "ACELookup API tid ::" + tid);
				String sysName = Constants.SYS_NAME;// hardcode as VRS
				data.addToLog(currElementName, "ACELookup API sysName ::" + sysName);
				String connTimeoutStr = (String) data.getSessionData(Constants.S_CONN_TIMEOUT);
				data.addToLog(currElementName, "ACELookup API connTimeoutStr ::" + connTimeoutStr);
				String readTimeoutStr = (String) data.getSessionData(Constants.S_READ_TIMEOUT);
				data.addToLog(currElementName, "ACELookup API readTimeoutStr ::" + readTimeoutStr);

				AgentInfo_Post apiObj = new AgentInfo_Post();

				//Non prod changes-Priya
				data.addToLog("API URL: ", url);
				String prefix = "https://api-np-ss.farmersinsurance.com"; 
				String UAT_FLAG="";
		        if(url.startsWith(prefix)) {
		        	UAT_FLAG="YES";
		        }
				
				AgentInfo_NP_Post npObj=new AgentInfo_NP_Post();
				
				JSONObject resp=null ;
				if("YES".equalsIgnoreCase(UAT_FLAG))
				{
					String Key =Constants.S_ACE_LOOKUP_URL;
					 region = regionDetails.get(Key);
					data.addToLog("Region for UAT endpoint: ", region);
					resp = npObj.start(url, tid, businesssUnit, phoneNumber, userId, sysName, Integer.parseInt(connTimeoutStr), Integer.parseInt(readTimeoutStr), context, region);
				}
				else {
					region="PROD";
				    resp = apiObj.start(url, tid, businesssUnit, phoneNumber, userId, sysName, Integer.parseInt(connTimeoutStr), Integer.parseInt(readTimeoutStr), context);
				}
				//Non prod changes-Priya
				
				data.addToLog(currElementName, currElementName + " : ACELookup API response  :" + resp);
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
				
				if (null != resp) {
				
					if (resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200 && resp.containsKey(Constants.RESPONSE_BODY)) {

						strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
						JSONObject strRespBodyObj = (JSONObject) resp.get(Constants.RESPONSE_BODY);
						data.addToLog(currElementName, "ACELookup API Response Body :: " + strRespBody);
						data.setSessionData(currElementName + Constants._RESP, resp.get(Constants.RESPONSE_BODY));
						data.addToLog(currElementName, "Set " + currElementName + " : ACELookup API Response into session with the key name of " + currElementName + Constants._RESP);

						if (resp.containsKey(Constants.REQUEST_BODY)) {
							strReqBody = resp.get(Constants.REQUEST_BODY).toString();
							data.addToLog(currElementName, "ACELookup API Request Body :: " + strReqBody);
						}

						if (strRespBodyObj.containsKey("agents") && null != strRespBodyObj.get("agents") && ((JSONArray) strRespBodyObj.get("agents")).size() > 0) {
							StrExitState = Constants.SU;
							data.setSessionData("S_ANI_MATCH", "TRUE");
							data.setSessionData("ACE_RESP_EMPTY_CHECK", Constants.NOT_EMPTY);
							data.addToLog(currElementName, "ACELookup API ACE_RESP_EMPTY_CHECK session data if response is not empty :: " + (String) data.getSessionData("ACE_RESP_EMPTY_CHECK"));
						}
						else {
							data.setSessionData("ACE_RESP_EMPTY_CHECK", Constants.EMPTY);
							data.addToLog(currElementName, "ACE Lookup Response Code is 200 but does not contain Agent Array :: Considering ACE Lookup Response as Empty ::  " + resp);
						}
					}
					else {
						strRespBody = resp.get(Constants.RESPONSE_MSG).toString();
						data.setSessionData("ACE_RESP_EMPTY_CHECK", Constants.EMPTY);
						data.addToLog(currElementName, "ACE Lookup Resp is unsuccessful :: Response Code != 200 :: " + resp);
					}
				}
				else {
					data.setSessionData("ACE_RESP_EMPTY_CHECK", Constants.EMPTY);
					data.addToLog(currElementName, "ACE Lookup Resp is null :: Considering Ace Lookup Resp as Expty :: ACE_RESP_EMPTY_CHECK :: " + data.getSessionData("ACE_RESP_EMPTY_CHECK"));
				}
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in " + currElementName + "ACELookup API call  :: " + e);
			caa.printStackTrace(e);
		}
		
		try {
			objHostDetails.startHostReport(currElementName,"ACE Lookup Response", strReqBody,region, (String) data.getSessionData(Constants.S_ACE_LOOKUP_URL));
			objHostDetails.endHostReport(data,strRespBody , StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for AccLinkAniLookup call  :: "+e);
			caa.printStackTrace(e);
		}
		return StrExitState;
	}
}
