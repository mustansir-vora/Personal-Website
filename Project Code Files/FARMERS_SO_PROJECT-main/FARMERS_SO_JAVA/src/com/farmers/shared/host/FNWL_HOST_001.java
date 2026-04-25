package com.farmers.shared.host;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.bean.FNWL.AgentBean_via_ANI;
import com.farmers.client.Lookupcall;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class FNWL_HOST_001 extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws Exception {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);
		String strReqBody = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;
		JSONObject RespBody = null;
		String apiRespCode = Constants.EmptyString;
		String region=null;
		
		try {
			data.setSessionData("FNWL_Collection_Tries_Prompt", "I'm Sorry, I did not understand.");
	        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
			
			if (null != data.getSessionData(Constants.S_FNWL_ACE_ANI_LOOKUP_URL) && data.getSessionData(Constants.S_READ_TIMEOUT) != null && data.getSessionData(Constants.S_CONN_TIMEOUT) != null &&	data.getSessionData(Constants.S_ANI) != null) {
			
				String url = (String) data.getSessionData(Constants.S_FNWL_ACE_ANI_LOOKUP_URL);
				String ani = (String) data.getSessionData(Constants.S_ANI);
				String conntimeout = (String) data.getSessionData(Constants.S_CONN_TIMEOUT);
				String readtimeout = (String) data.getSessionData(Constants.S_READ_TIMEOUT);
				String Callid = (String) data.getSessionData(Constants.S_CALLID);
				LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
				String lookupType = "ANI";
				
				data.addToLog(currElementName, "Call ID :: " + Callid);
				data.addToLog(currElementName, "readTimeOut :: " + readtimeout);
				data.addToLog(currElementName, "connTimeOut :: " + conntimeout);
				data.addToLog(currElementName, "ANI :: " + ani);
				data.addToLog(currElementName, "LookupType :: " + lookupType);
				data.addToLog(currElementName, "URL :: " + url);
				
				String prefix = "https://api-np-ss.farmersinsurance.com"; 
				String UAT_FLAG="";
				
				if(url.startsWith(prefix)) {
		        	UAT_FLAG="YES";
		        }
				
                if("YES".equalsIgnoreCase(UAT_FLAG)) {
                	
                region = regionDetails.get(Constants.S_FNWL_ACE_ANI_LOOKUP_URL);
                }
                else {
                	region="PROD";
                }
				
				Lookupcall lookupCall = new Lookupcall();
				JSONObject resp = lookupCall.FNWLACELookup_Post(url, Callid, ani, "", lookupType, region, UAT_FLAG, Integer.parseInt(conntimeout), Integer.parseInt(readtimeout), context);
				
				if (null != resp && resp.containsKey(Constants.RESPONSE_CODE)) {
					apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
				}
				data.addToLog("FWNL Ace via ANI Resp :: ", resp.toString());
				
				if (null != resp) {
					
					if (resp.containsKey(Constants.REQUEST_BODY)) {
						strReqBody = resp.get(Constants.REQUEST_BODY).toString();
					}
					
					if (resp.containsKey(Constants.RESPONSE_BODY)) {
						strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
					}
					else {
						strRespBody = resp.get(Constants.RESPONSE_MSG).toString();
						data.addToLog(currElementName, "resp does not contain responseBody");
					}
					
					if (resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200 && resp.containsKey(Constants.RESPONSE_BODY)) 
					{
						data.setSessionData(Constants.FNWL_ACE_VIA_ANI_CALLED, Constants.TRUE);
						RespBody = (JSONObject) resp.get(Constants.RESPONSE_BODY);
						//data.setSessionData(Constants.FNWL_HOST_001_RESP, RespBody);
						strExitState = apiResponseManipulation(data, caa, currElementName, RespBody);
					}
				}
			}
			else {
				data.addToLog(currElementName, "API parameters either null/Empty");
			}
		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in FNWL_HOST_001 API Call :: " + e);
			caa.printStackTrace(e);
		}
		
		try {
			objHostDetails.startHostReport(currElementName,"FNWL_ACE Lookup by ANI", strReqBody, region, (String) data.getSessionData(Constants.S_FNWL_ACE_ANI_LOOKUP_URL));
			objHostDetails.endHostReport(data, strRespBody , strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode, "Awards: " + (String) data.getSessionData("Agent_awardList"));
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for FNWL ACE Lookup via ANI call  :: " + e);
			caa.printStackTrace(e);
		}
		
		return strExitState;
	}
	
	public String apiResponseManipulation(DecisionElementData data, CommonAPIAccess caa, String currElementName, JSONObject resp) {
		String strExitState = Constants.ER;
		JSONArray agentsArr = null;
		AgentBean_via_ANI agentDetails = new AgentBean_via_ANI();
		HashMap<String, AgentBean_via_ANI> agentMap = new HashMap<String, AgentBean_via_ANI>();
		HashSet<String> awardsList = new HashSet<>();
		try {
			
			if (null != resp) {
				
				if (resp.containsKey("agents")) {
					agentsArr = (JSONArray) resp.get("agents");
					
					if (null != agentsArr) {
						if (agentsArr.size() > 0) {
							data.addToLog(currElementName, "Agents Array size is greater than 0 :: Agent/Agents found :: " + agentsArr);
							
							for (int i = 0; i < agentsArr.size(); i++) {
								JSONObject agent = (JSONObject) agentsArr.get(i);
								
								agentDetails.setAORId(null != agent.get("agentOfRecordId") ? (String) agent.get("agentOfRecordId") : Constants.EmptyString);
								agentDetails.setUPN(null != agent.get("parentUniqueProducerNumber") ? (String) agent.get("parentUniqueProducerNumber") : Constants.EmptyString);
								agentDetails.setAgentCode(null != agent.get("agentCode") ? (String) agent.get("agentCode") : Constants.EmptyString);
								agentDetails.setAgentType(null != agent.get("agentType") ? (String) agent.get("agentType") : Constants.EmptyString);
								agentDetails.setAwards((boolean) agent.containsKey("awards") ? (JSONArray) agent.get("awards") : null);
								
								agentMap.put((String) agent.get("agentOfRecordId"), agentDetails);
							}
							
							data.setSessionData(Constants.FNWL_AGENTMAP_VIA_ANI, agentMap);
							data.addToLog(currElementName, "Agents via ANI Hashmap :: " + agentMap);
							
							if (null != agentMap & agentMap.size() > 0) {
								data.setSessionData(Constants.FNWL_AOR_FOUND_VIA_ANI, Constants.TRUE);
								boolean awardFound = false;
								
								for (Map.Entry<String, AgentBean_via_ANI> entry : agentMap.entrySet()) {
									String agentId = entry.getKey();
									AgentBean_via_ANI agentDet = entry.getValue();
									JSONArray awards = agentDet.getAwards();
									
									if (null != awards && awards.size() > 0) {
										data.addToLog(currElementName, "Award Array size is greater than zero :: Award/Awards Found for agent :: " + agentId);
										
										for (int i = 0; i < awards.size(); i++) {
											JSONObject awardObj = (JSONObject) awards.get(i);
											String awardName = (String) awardObj.get("award");
											String awardyear = (String) awardObj.get("awardYear");
											
											if (null != awardName && !awardName.equalsIgnoreCase(Constants.EmptyString)) {
												awardFound = true;
												data.addToLog(currElementName, "Agent ID :: " + agentId + " has award :: " + awardName + " in year :: " + awardyear);
												awardsList.add(awardName);
											}
										}
										data.setSessionData("Agent_awardList", awardsList.toString());
									}
									else {
										data.addToLog(currElementName, "Awards Array is null/Empty for Agent :: " + agentId);
									}
								}
								
								if (awardFound) {
									data.setSessionData(Constants.FNWL_PRIORITY_AGENT_FLAG, Constants.TRUE);
								}
								else {
									data.setSessionData(Constants.FNWL_PRIORITY_AGENT_FLAG, Constants.FALSE);
								}
							}
						}
						else {
							data.addToLog(currElementName, "agents array size is not greater than zero");
						}
					}
					else {
						data.addToLog(currElementName, "Agents Array is null");
					}
				}
				else {
					data.addToLog(currElementName, "Response does not contain Agents Array");
				}
			}
			else {
				data.addToLog(currElementName, "Response Body is null");
			}
			strExitState = Constants.SU;
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in FNWL_HOST_001 apiResponseManipulation method  :: " + e);
			caa.printStackTrace(e);
		}
		
		return strExitState;
	}

}
