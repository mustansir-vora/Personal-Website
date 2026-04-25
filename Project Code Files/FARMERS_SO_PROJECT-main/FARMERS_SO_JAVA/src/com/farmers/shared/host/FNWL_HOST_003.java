package com.farmers.shared.host;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.bean.FNWL.AgentBean_via_ANI;
import com.farmers.bean.FNWL.AgentBean_via_AOR;
import com.farmers.client.Lookupcall;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class FNWL_HOST_003 extends DecisionElementBase {

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
			data.setSessionData("Previous_element", "FNWL_HOST_002");
			HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
			
			if (null != data.getSessionData(Constants.S_FNWL_ACE_AOR_LOOKUP_URL) && data.getSessionData(Constants.S_READ_TIMEOUT) != null && data.getSessionData(Constants.S_CONN_TIMEOUT) != null &&	data.getSessionData(Constants.S_ANI) != null) {
				
				String url = (String) data.getSessionData(Constants.S_FNWL_ACE_AOR_LOOKUP_URL);
				String conntimeout = (String) data.getSessionData(Constants.S_CONN_TIMEOUT);
				String readtimeout = (String) data.getSessionData(Constants.S_READ_TIMEOUT);
				String Callid = (String) data.getSessionData(Constants.S_CALLID);
				LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
				String lookupType = "AOR";
				String AORID = null != (String) data.getSessionData(Constants.FNWL_MN_002_VALUE) ? (String) data.getSessionData(Constants.FNWL_MN_002_VALUE) : Constants.DEFAULT;
				AORID = AORID.toUpperCase();
				AORID = AORID.replaceAll("[^a-zA-Z0-9 ]", "");
				
				data.addToLog(currElementName, "Call ID :: " + Callid);
				data.addToLog(currElementName, "readTimeOut :: " + readtimeout);
				data.addToLog(currElementName, "connTimeOut :: " + conntimeout);
				data.addToLog(currElementName, "URL :: " + url);
				data.addToLog(currElementName, "LookupType :: " + lookupType);
				data.addToLog(currElementName, "AOR ID :: " + AORID);
				
				String prefix = "https://api-np-ss.farmersinsurance.com"; 
				String UAT_FLAG="";
				
				if(url.startsWith(prefix)) {
		        	UAT_FLAG="YES";
		        }
				
                if("YES".equalsIgnoreCase(UAT_FLAG)) {
                	
                region = regionDetails.get(Constants.S_FNWL_ACE_AOR_LOOKUP_URL);
                }
                else {
                	region="PROD";
                }
				
				Lookupcall lookupCall = new Lookupcall();
				JSONObject resp = lookupCall.FNWLACELookup_Post(url, Callid, "", AORID, lookupType, region, UAT_FLAG, Integer.parseInt(conntimeout), Integer.parseInt(readtimeout), context);
				
				if (null != resp && resp.containsKey(Constants.RESPONSE_CODE)) {
					apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
				}
				data.addToLog("FWNL Ace via AOR Resp :: ", resp.toString());
				
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
						RespBody = (JSONObject) resp.get(Constants.RESPONSE_BODY);
						strExitState = apiResponseManipulation(data, caa, currElementName, RespBody);
					}
				}
			}
			
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in FNWL_HOST_003 API Call :: " + e);
			caa.printStackTrace(e);
		}
		
		try {
			objHostDetails.startHostReport(currElementName,"FNWL_ACE Lookup by AOR", strReqBody, region, (String) data.getSessionData(Constants.S_FNWL_ACE_AOR_LOOKUP_URL));
			objHostDetails.endHostReport(data, strRespBody , strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode, "Awards: " + (String) data.getSessionData("Agent_awardList"));
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for FNWL ACE Lookup via AOR call  :: " + e);
			caa.printStackTrace(e);
		}
		
		return strExitState;
	}
	
	public String apiResponseManipulation(DecisionElementData data, CommonAPIAccess caa, String currElementName, JSONObject resp) {
		String strExitState = Constants.ER;
		JSONArray agentsArr = null;
		String AORID = null != (String) data.getSessionData(Constants.FNWL_MN_002_VALUE) ? (String) data.getSessionData(Constants.FNWL_MN_002_VALUE) : Constants.EmptyString;
		AgentBean_via_AOR agentBean = new AgentBean_via_AOR();
		HashMap<String, AgentBean_via_AOR> agentMap = new HashMap<String, AgentBean_via_AOR>();
		HashSet<String> awardsList = new HashSet<>();
		try {
			
			if (null != resp) {
				
				if (resp.containsKey("agents")) {
					agentsArr = (JSONArray) resp.get("agents");
					
					if (null != agentsArr) {
						
						if (agentsArr.size() > 0) {
							data.addToLog(currElementName, "Agents Array size is greater than 0 :: Agent/Agents found");
							
							for (int i = 0; i < agentsArr.size(); i++) {
								JSONObject agent = (JSONObject) agentsArr.get(i);
								
								agentBean.setAORId(null != agent.get("agentOfRecordId") ? (String) agent.get("agentOfRecordId") : Constants.EmptyString);
								agentBean.setUPN(null != agent.get("parentUniqueProducerNumber") ? (String) agent.get("parentUniqueProducerNumber") : Constants.EmptyString);
								agentBean.setAgentCode(null != agent.get("agentCode") ? (String) agent.get("agentCode") : Constants.EmptyString);
								agentBean.setAgentType(null != agent.get("agentType") ? (String) agent.get("agentType") : Constants.EmptyString);
								agentBean.setAwards((boolean) agent.containsKey("awards") ? (JSONArray) agent.get("awards") : null);
								
								agentMap.put((String) agent.get("agentOfRecordId"), agentBean);
							}
							
							data.setSessionData(Constants.FNWL_AGENTMAP_VIA_AOR, agentMap);
							data.addToLog(currElementName, "Agents via AOR Hashmap :: " + agentMap);
							
							for (Map.Entry<String, AgentBean_via_AOR> entry : agentMap.entrySet()) {
								String agentId = entry.getKey();
								AgentBean_via_AOR agentDet = entry.getValue();
								
								if (null != agentId && agentId.equalsIgnoreCase(AORID)) {
									data.setSessionData(Constants.FNWL_AOR_FOUND_VIA_AOR, Constants.TRUE);
									data.setSessionData(Constants.FNWL_AOR_ID, agentId);
									data.setSessionData(Constants.FNWL_UPN, agentDet.getUPN());
									data.setSessionData(Constants.FNWL_AGENT_CODE, agentDet.getAgentCode());
									data.setSessionData(Constants.FNWL_AGENT_TYPE, agentDet.getAgentType());
									
									boolean awardFound = false;
									if (null != agentDet.getAwards()) {
										data.setSessionData(Constants.FNWL_AGENT_AWARDS, agentDet.getAwards());
										
										if (null != agentDet.getAwards() && agentDet.getAwards().size() > 0) {
											
											for (int i = 0; i < agentDet.getAwards().size(); i++) {
												JSONObject awardsObj = (JSONObject) agentDet.getAwards().get(i);
												String awardName = (String) awardsObj.get("award");
												String awardYear = (String) awardsObj.get("awardYear");
												data.addToLog(currElementName, "Agent ID :: " + agentId + " has award :: " + awardName + " in year :: " + awardYear);
											
												if (null != awardName && !awardName.equalsIgnoreCase(Constants.EmptyString)) {
													awardFound = true;
													awardsList.add(awardName);
												}
											}
											data.setSessionData("Agent_awardList", awardsList.toString());
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
						}
						else {
							data.addToLog(currElementName, "Agents array size is not greater than zero");
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
			data.addToLog(currElementName,"Exception in FNWL_HOST_003 apiResponseManipulation method  :: " + e);
			caa.printStackTrace(e);
		}
		
		return strExitState;		
	}
}
