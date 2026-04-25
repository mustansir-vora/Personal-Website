package com.farmers.host;


import java.util.HashMap;

import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;
import com.farmers.FarmersAPI.AgentInfo_Post;
import com.farmers.FarmersAPI_NP.AgentInfo_NP_Post;
import com.farmers.report.SetHostDetails;

public class AGENTINFO_POST extends DecisionElementBase {
	//FASMM_HOST_001 API: intermediaterinformationmanagementv1-retrieveIntermediarySegmentationInfo 
	@Override
	public String doDecision(String currElementName, DecisionElementData data)throws Exception {
		String StrExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);objHostDetails.setinitalValue();
		String strReqBody = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
	
		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
		String region=null;
        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
        //END- UAT ENV SETUP CODE (PRIYA, SHAIK)
		
		try {
			data.addToLog(currElementName, "ACE Lookup ");
			if (data.getSessionData(Constants.S_ACE_LOOKUP_URL)!=null && data.getSessionData(Constants.S_CONN_TIMEOUT)!=null && data.getSessionData(Constants.S_READ_TIMEOUT)!=null) {
				String url = (String) data.getSessionData(Constants.S_ACE_LOOKUP_URL);
				data.addToLog(currElementName, "ACE Lookup "+url);
				
				String businessunit="IVRPLA";
				String callerId=(String)data.getSessionData(Constants.S_CALLID);
				String phonenumber=(String)data.getSessionData(Constants.S_ANI);
				int conTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_CONN_TIMEOUT));
				int readTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_READ_TIMEOUT));
				LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
				AgentInfo_Post obj = new AgentInfo_Post();
				//Non prod changes-Priya
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
					resp = objNP.start(url, callerId,businessunit, phonenumber, Constants.USER_ID, Constants.SYSTEM_NAME, conTimeout, readTimeout,context, region);
				}else {
					region="PROD";
				    resp = obj.start(url, callerId,businessunit, phonenumber, Constants.USER_ID, Constants.SYSTEM_NAME, conTimeout, readTimeout,context);
				}
				//Non prod changes-Priya
				data.addToLog(currElementName, "API response  :"+resp);
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
				if(resp != null) {
					if(resp.containsKey(Constants.REQUEST_BODY)) strReqBody = resp.get(Constants.REQUEST_BODY).toString();
					if(resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200 && resp.containsKey(Constants.RESPONSE_BODY)) {
						data.addToLog(currElementName, "Set AgentInfo API Response into session with the key name of "+currElementName+Constants._RESP);
						strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
						data.setSessionData(currElementName+Constants._RESP, resp.get(Constants.RESPONSE_BODY));
						StrExitState =apiResponseManupulation(data, caa, currElementName, strRespBody);
					} else {
						strRespBody = resp.get(Constants.RESPONSE_MSG).toString();
					}		
				}
			}	
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in AGENTINFO_POST API call  :: "+e);
			caa.printStackTrace(e);
		}
		try {
			objHostDetails.startHostReport(currElementName,": AGENTINFO_POST API", strReqBody, region, (String) data.getSessionData(Constants.S_ACE_LOOKUP_URL));
			objHostDetails.endHostReport(data,strRespBody , StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for : AGENTINFO_POST API call  :: "+e);
			caa.printStackTrace(e);

		}
		return StrExitState;
	}
	
	private String apiResponseManupulation(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {
		String StrExitState = Constants.ER;
		try {
			
			String isAgentSegmentationDone = (String) data.getSessionData("IS_AGENTSEGMENTATION_DONE");
			boolean isPCaward=false;	
			String strPC_AdminYear = (String)data.getSessionData(Constants.S_PC_ADMIN_YEAR);
			
			
			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			JSONArray agentsArr = null;
			if (null != resp && resp.containsKey("agents")) {
				agentsArr = (JSONArray) resp.get("agents");
			}
			if(agentsArr == null || agentsArr.size() == 0 ) return  Constants.ER;
			
			data.setSessionData("S_ANI_MATCH", "TRUE");
			if (isAgentSegmentationDone != null && isAgentSegmentationDone.equalsIgnoreCase(Constants.STRING_YES)) {
				data.addToLog(currElementName, "Agent Segmentation is already done in KYC :: Skipping Response Maniupation in FAS MAIN MENU :: IS_AGENTSEGMENTATION_DONE FLAG :: " + isAgentSegmentationDone);
				return Constants.SU;
			}
			JSONObject agentObj = (JSONObject) agentsArr.get(0);
			JSONArray awardsArr = (JSONArray) agentObj.get("awards");
			//caller Verification
			data.setSessionData(Constants.S_AgentType_CCAI, agentObj.get("agentType"));
			//Checking Prime Segment Indicator - CS1253267 
			data.setSessionData("S_PRIME_SEG_FLAG", agentObj.get("primeSegmentIndicator"));
			
			for (Object awardObj : awardsArr) {
				JSONObject award = (JSONObject) awardObj;
				if ("Presidents Council".equals(award.get("award"))){
					String awardYear = (String) award.get("awardYear");
					if(null != strPC_AdminYear && null != awardYear && strPC_AdminYear.contains(awardYear)) {
						isPCaward = true;
						String awardValue = (String) award.get("award");
						data.setSessionData("S_AWARD_YEAR", awardYear);
						data.setSessionData("S_AWARD_VALUE", awardValue);
						data.addToLog(currElementName, "Set session data: " + currElementName + "S_AWARD_YEAR = " + awardYear);
						data.addToLog(currElementName, "Set session data: " + currElementName + "S_AWARD_VALUE = " + awardValue);
						break;
					} else {
						data.addToLog(currElementName, "awardYear not macthed for Presidents Council");
					}
				}
			}
			if(isPCaward) {
				data.setSessionData(Constants.S_AGENT_SEGMENTATION,Constants.S_FAS_PC);
				caa.createMSPKey(caa, data, "FASMM_HOST_001", Constants.S_FAS_PC);
			} else if(null != agentObj.get(Constants.S_FAS_AGENTCODE) && ("DM".equalsIgnoreCase((String)agentObj.get(Constants.S_FAS_AGENTCODE)))) {
				data.setSessionData(Constants.S_AGENT_SEGMENTATION,Constants.S_FAS_DM);
				caa.createMSPKey(caa, data, "FASMM_HOST_001", Constants.S_FAS_DM);
			}else if(null!=(String)data.getSessionData("S_PRIME_SEG_FLAG") && "Y".equalsIgnoreCase((String)data.getSessionData("S_PRIME_SEG_FLAG"))){
				data.setSessionData(Constants.S_AGENT_SEGMENTATION, "EA PRIME");
				caa.createMSPKey(caa, data, "IS_PRIME","PRIME_AGENT");
			} else {
				data.setSessionData(Constants.S_AGENT_SEGMENTATION,Constants.S_FAS_OTHERS);
			}
			data.addToLog(currElementName, "isPCaward : "+isPCaward+" :: S_AGENT_SEGMENTATION : "+data.getSessionData(Constants.S_AGENT_SEGMENTATION));
			StrExitState=Constants.SU;
		}catch (Exception e) {
			data.addToLog(currElementName,"Exception in apiResponseManupulation method  :: "+e);
			caa.printStackTrace(e);
		}
		return StrExitState;
	}	
}