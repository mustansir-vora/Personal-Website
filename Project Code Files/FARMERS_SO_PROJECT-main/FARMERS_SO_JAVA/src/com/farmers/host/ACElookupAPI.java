package com.farmers.host;


import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.FarmersAPI.AgentInfo_Post;
import com.farmers.FarmersAPI_NP.AgentInfo_NP_Post;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

/*KYCMF_HOST_001*/
public class ACElookupAPI extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);objHostDetails.setinitalValue();
		String strReqBody = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		
		String region=null;
		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
        //END- UAT ENV SETUP CODE (PRIYA, SHAIK)
		try {
			if(data.getSessionData(Constants.S_ACE_LOOKUP_URL) != null && data.getSessionData(Constants.S_CONN_TIMEOUT) != null
					&& data.getSessionData(Constants.S_READ_TIMEOUT) != null) {
				String url = (String) data.getSessionData(Constants.S_ACE_LOOKUP_URL);
				String callerId = (String) data.getSessionData(Constants.S_CALLID);
				String businessUnit = (String) data.getSessionData(Constants.S_BU);

				ArrayList<String>  strBristolCode = (ArrayList<String>) data.getSessionData("BRISTOL_LOB_LIST");
				ArrayList<String>  strFarmersCode = (ArrayList<String>) data.getSessionData("FARMER_LOB_LIST");
				ArrayList<String>  strForemostCode = (ArrayList<String>) data.getSessionData("FOREMOST_LOB_LIST");
				ArrayList<String>  strFWSCode = (ArrayList<String>) data.getSessionData("FWS_LOB_LIST");
				ArrayList<String>  str21stCode = (ArrayList<String>) data.getSessionData("21st_LOB_LIST");

				if(strBristolCode!=null && null != businessUnit && strBristolCode.contains(businessUnit)) {
					businessUnit = "IVRBW";
					data.addToLog(currElementName, "BU :: Bristol West :: BU value sent in API ::"+businessUnit);
				} else if(strFarmersCode!=null && businessUnit!=null && strFarmersCode.contains(businessUnit)) {
					businessUnit = "IVRPLA";
					data.addToLog(currElementName, "BU :: Farmers :: BU value sent in API ::"+businessUnit);
				} else if(strForemostCode!=null && businessUnit!=null && strForemostCode.contains(businessUnit)) {
					businessUnit = "IVRFRMST";
					data.addToLog(currElementName, "BU :: Foremost :: BU value sent in API ::"+businessUnit);
				} else if(strFWSCode!=null && businessUnit!=null && strFWSCode.contains(businessUnit)) {
					businessUnit = "IVRFWS";
					data.addToLog(currElementName, "BU :: FWS :: BU value sent in API ::"+businessUnit);
				} else {
					businessUnit = "IVRPLA";
					data.addToLog(currElementName, "BU :: Didn't match :: Setting default BU value to be sent in API ::"+businessUnit);
				}

				String phoneNo = (String) data.getSessionData(Constants.S_ANI);
				int conTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_CONN_TIMEOUT));
				int readTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_READ_TIMEOUT));
				LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
				AgentInfo_Post obj = new AgentInfo_Post();
				//START- UAT ENV SETUP CODE (PRIYA, SHAIKH)
				data.addToLog("API URL: ", url);
				String prefix = "https://api-np-ss.farmersinsurance.com"; 
				String UAT_FLAG="";
		        if(url.startsWith(prefix)) {
		        	UAT_FLAG="YES";
		        }
				AgentInfo_NP_Post objNP = new AgentInfo_NP_Post();
				JSONObject resp=null ;
			    if("YES".equalsIgnoreCase(UAT_FLAG)) {
				String Key =Constants.S_ACE_LOOKUP_URL;
			    region = regionDetails.get(Key);
				data.addToLog("Region for UAT endpoint: ", region);
				   resp = objNP.start(url, callerId, businessUnit, phoneNo, Constants.USER_ID, Constants.SYSTEM_NAME, conTimeout, readTimeout, context,region);
								
			    }else {
			    	region="PROD";
					resp = obj.start(url, callerId, businessUnit, phoneNo, Constants.USER_ID, Constants.SYSTEM_NAME, conTimeout, readTimeout, context);
				}
				//END- UAT ENV SETUP CODE (PRIYA, SHAIK)
				data.addToLog(currElementName, "�PI response  :"+resp);
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
				if(resp != null) {
					if(resp.containsKey(Constants.REQUEST_BODY)) strReqBody = resp.get(Constants.REQUEST_BODY).toString();
					if(resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200 && resp.containsKey(Constants.RESPONSE_BODY)) {
						data.addToLog(currElementName, "Set ACElookupAPI API Response into session with the key name of "+currElementName+Constants._RESP);
						strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
						data.setSessionData(currElementName+Constants._RESP, resp.get(Constants.RESPONSE_BODY));
						strExitState = apiResponseManupulation(data, caa, currElementName, strRespBody);
					} else {
						//data.setSessionData(Constants.S_CALLLER_TYPE,"03");
						strRespBody = resp.get(Constants.RESPONSE_MSG).toString();
					}

				}
			}
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in ACElookup API call  :: "+e);
			caa.printStackTrace(e);
		}

		try {
			objHostDetails.startHostReport(currElementName,"ACElookupAPI", strReqBody,region, (String) data.getSessionData(Constants.S_ACE_LOOKUP_URL));
			objHostDetails.endHostReport(data,strRespBody , strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for ACElookup API call  :: "+e);
			caa.printStackTrace(e);
		}



		return strExitState;
	}

	private String apiResponseManupulation(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) throws AudiumException {
		String strExitState = Constants.ER;
		try {
			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			JSONArray resArr = (JSONArray)resp.get("agents");
			if(resArr == null || resArr.size() == 0 ) {
				//data.setSessionData(Constants.S_CALLLER_TYPE,"03");
				return  Constants.ER;
			}
			data.setSessionData("S_ANI_MATCH", "TRUE");
			JSONObject agentObj = (JSONObject) resArr.get(0);
			data.setSessionData(Constants.S_AGENT_OF_RECORDID, agentObj.get("agentOfRecordId"));
			data.setSessionData(Constants.S_AN1, agentObj.get("agentOfRecordId"));
			data.setSessionData(Constants.S_UPN1, agentObj.get("parentUniqueProducerNumber"));
			//caller Verification
			data.setSessionData(Constants.S_AgentType_CCAI, agentObj.get("agentType"));
			
			//CS1191070 - FWS, BW, Specialty - Add IA Premier Agent Routing 
			data.setSessionData("isIAPremierAgent", agentObj.get("isIAPremierAgent"));//Premier agent Flag check Y/N
			data.setSessionData("S_PRIME_SEG_FLAG", agentObj.get("primeSegmentIndicator"));//Checking Prime Segment Indicator - CS1253267 
			data.addToLog("isIAPremierAgent flag:", (String)data.getSessionData("isIAPremierAgent"));
			//CS1191070 - FWS, BW, Specialty - Add IA Premier Agent Routing 
			
			strExitState = Constants.SU;
		} catch (Exception e) {
			//data.setSessionData(Constants.S_CALLLER_TYPE,"03");
			data.addToLog(currElementName,"Exception in apiResponseManupulation method  :: "+e);
			caa.printStackTrace(e);
		}
		return strExitState;
	}

}
