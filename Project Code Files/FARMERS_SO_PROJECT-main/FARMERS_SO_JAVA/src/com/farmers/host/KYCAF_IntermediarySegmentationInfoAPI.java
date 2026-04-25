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
import com.farmers.AdminAPI.GetAgencySegmentationTableByKey;
import com.farmers.FarmersAPI.AgentInfo_Post;
import com.farmers.FarmersAPI_NP.AgentInfo_NP_Post;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

/*KYCAF_HOST_001*/
public class KYCAF_IntermediarySegmentationInfoAPI extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String StrExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);objHostDetails.setinitalValue();
		String strReqBody = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		
		String aceLookuUpResult = Constants.EmptyString;
		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
		String region=null;
        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
        //END- UAT ENV SETUP CODE (PRIYA, SHAIK)
		try {
			if(data.getSessionData(Constants.S_ACE_LOOKUP_URL) != null && data.getSessionData(Constants.S_CONN_TIMEOUT) != null
					&& data.getSessionData(Constants.S_READ_TIMEOUT) != null) {
				String url = (String) data.getSessionData(Constants.S_ACE_LOOKUP_URL);
				String callerId = (String) data.getSessionData(Constants.S_CALLID);


				String BU=(String) data.getSessionData(Constants.S_BU);
				String businessUnitConfig = "S_"+BU;
				data.addToLog("businessUnitConfig:", businessUnitConfig);
				String businessUnit=(String) data.getSessionData(businessUnitConfig);
				data.addToLog("businessUnitConfigValue:", businessUnit);
				if(businessUnit == null || businessUnit.equals("")) {
					ArrayList<String> strBristolCode = (ArrayList<String>) data.getSessionData("BRISTOL_LOB_LIST");
					ArrayList<String>  strFarmersCode = (ArrayList<String>) data.getSessionData("FARMER_LOB_LIST");
					ArrayList<String>  strForemostCode = (ArrayList<String>) data.getSessionData("FOREMOST_LOB_LIST");
					ArrayList<String>  strFWSCode = (ArrayList<String>) data.getSessionData("FWS_LOB_LIST");
					ArrayList<String>  str21stCode = (ArrayList<String>) data.getSessionData("21st_LOB_LIST");
					data.addToLog(currElementName, " Current BU : "+BU);
					data.addToLog(currElementName, " A_BRISTOL_LOB : "+strBristolCode);
					data.addToLog(currElementName, " A_FARMERS_LOB : "+strFarmersCode);
					data.addToLog(currElementName, " A_FOREMOST_LOB : "+strForemostCode);
					data.addToLog(currElementName, " A_FWS_LOB : "+strFWSCode);
					data.addToLog(currElementName, " A_21ST_LOB : "+str21stCode);
					if(strBristolCode!=null && null != BU && strBristolCode.contains(BU)) {
						businessUnit = "IVRBW";
					} else if(strFarmersCode!=null && BU!=null && strFarmersCode.contains(BU)) {
						businessUnit = "IVRPLA";
					} else if(strForemostCode!=null && BU!=null && strForemostCode.contains(BU)) {
						businessUnit = "IVRFRMST";
					} else if(strFWSCode!=null && BU!=null && strFWSCode.contains(BU)) {
						businessUnit = "IVRFWS";
					}  else {
						businessUnit = "IVRPLA";
					}
					data.addToLog("Final businessUnitConfigValue:", businessUnit);
				}
				String phoneNo = (String) data.getSessionData(Constants.S_ANI);
				aceLookuUpResult = (String)data.getSessionData("S_ACE_LOOKUP_RESULT");
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
		        data.addToLog(currElementName,"S_ACE_LOOKUP RESULT::"+aceLookuUpResult);
		        if(aceLookuUpResult!=null && !aceLookuUpResult.isEmpty()&& aceLookuUpResult.equalsIgnoreCase("FAILURE")) {
		        	
		        	phoneNo = (String)data.getSessionData(Constants.S_AOR_ANI);
		        	
		        	data.addToLog(currElementName, "CALLING ACE LOOKUP AGAIN WITH AOR ANI FROM AOR LOOKUP RESPONSE...");
		        	data.addToLog(currElementName, "ANI FOUND BY AOR RESPONSE::"+phoneNo);
		        	
		        }
				AgentInfo_NP_Post objNP = new AgentInfo_NP_Post();
				JSONObject resp=null ;
				if("YES".equalsIgnoreCase(UAT_FLAG)) {
					String Key =Constants.S_ACE_LOOKUP_URL;
					 region = regionDetails.get(Key);
					data.addToLog("Region for UAT endpoint: ", region);
					resp = objNP.start(url, callerId, businessUnit, phoneNo, Constants.USER_ID, Constants.SYSTEM_NAME, conTimeout, readTimeout, context, region);
				}else {
					region="PROD";
				    resp = obj.start(url, callerId, businessUnit, phoneNo, Constants.USER_ID, Constants.SYSTEM_NAME, conTimeout, readTimeout, context);
				}
                //Non prod changes-Priya
				data.addToLog(currElementName, "KYCAF_HOST_001 �PI response  :"+resp);
				
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
				if(resp != null) {
					if(resp.containsKey(Constants.REQUEST_BODY)) strReqBody = resp.get(Constants.REQUEST_BODY).toString();
					if(resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200 && resp.containsKey(Constants.RESPONSE_BODY)) {
						data.addToLog(currElementName, "Set KYCAF_HOST_001  RetrieveIntermediarySegmentationInfo API Response into session with the key name of "+currElementName+Constants._RESP);
						strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
						data.setSessionData(currElementName+Constants._RESP, resp.get(Constants.RESPONSE_BODY));
						StrExitState = apiResponseManupulation1(data, caa, currElementName, strRespBody);
					} else {
						strRespBody = resp.get(Constants.RESPONSE_MSG).toString();
					}

				}
			}
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in KYCAF_HOST_001  RetrieveIntermediarySegmentationInfo API call  :: "+e);
			caa.printStackTrace(e);
		}

		try {
			if(aceLookuUpResult!=null && !aceLookuUpResult.isEmpty()&& aceLookuUpResult.equalsIgnoreCase("FAILURE")) {
			if (null != StrExitState && Constants.SU.equalsIgnoreCase(StrExitState)) {
				objHostDetails.startHostReport(currElementName,"KYCAF_HOST_004  RetrieveIntermediarySegmentationInfo API", strReqBody,region, (String) data.getSessionData(Constants.S_ACE_LOOKUP_URL));
				objHostDetails.endHostReport(data,strRespBody , StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
			}
			else {
				objHostDetails.startHostReport(currElementName,"KYCAF_HOST_004  RetrieveIntermediarySegmentationInfo API", strReqBody,region, (String) data.getSessionData(Constants.S_ACE_LOOKUP_URL));
				objHostDetails.endHostReport(data,"DB-mismatch : " + strRespBody , StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
			}
		}else {
			if (null != data.getSessionData(Constants.S_PRODUCER_CODE_MATCH) && Constants.MATCH.equalsIgnoreCase((String) data.getSessionData(Constants.S_PRODUCER_CODE_MATCH))) {
				objHostDetails.startHostReport(currElementName,"KYCAF_HOST_001  RetrieveIntermediarySegmentationInfo API", strReqBody,region, (String) data.getSessionData(Constants.S_ACE_LOOKUP_URL));
				objHostDetails.endHostReport(data,strRespBody , StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
			}
			else {
				objHostDetails.startHostReport(currElementName,"KYCAF_HOST_001  RetrieveIntermediarySegmentationInfo API", strReqBody,region, (String) data.getSessionData(Constants.S_ACE_LOOKUP_URL));
				objHostDetails.endHostReport(data,"DB-mismatch : " + strRespBody , StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		}
		}	
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for KYCAF_HOST_001  RetrieveIntermediarySegmentationInfo API call  :: "+e);
			caa.printStackTrace(e);
		}
		/*
		String hostMSPEndKey = Constants.EmptyString;
		if(StrExitState.equalsIgnoreCase(Constants.ER)) hostMSPEndKey = Constants.API_FAILURE;
		data.setSessionData(Constants.S_MENU_SELCTION_KEY, data.getSessionData(Constants.S_BU)+":KYCAF_HOST_001:"+hostMSPEndKey);
		data.addToLog(currElementName,"S_MENU_SELCTION_KEY for  KYCAF_HOST_001 :: "+data.getSessionData(Constants.S_MENU_SELCTION_KEY));
		*/
		return StrExitState;
	}

	private String apiResponseManupulation(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {
		String strExitState = Constants.ER;
		try {
			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			JSONArray agentsArr = (JSONArray)resp.get("agents");
			if(agentsArr!=null && agentsArr.size()>0) {
				data.setSessionData("S_ANI_MATCH", "TRUE");
				String agentOfRecordId = (String)((JSONObject) agentsArr.get(0)).get("agentOfRecordId");
				String uniqueProducerNumber =(String)((JSONObject) agentsArr.get(0)).get("parentUniqueProducerNumber");
				boolean isPCaward=false;	
				String strPC_AdminYear = (String)data.getSessionData(Constants.S_PC_ADMIN_YEAR);
				JSONObject agentObj = (JSONObject) agentsArr.get(0);
				JSONArray awardsArr = (JSONArray) agentObj.get("awards");
				//caller Verification
				data.setSessionData(Constants.S_AgentType_CCAI, agentObj.get("agentType"));
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
//				if(isPCaward) {
//					data.setSessionData(Constants.S_AGENT_SEGMENTATION,Constants.S_FAS_PC);
//				} else if(null != agentObj.get(Constants.S_FAS_AGENTCODE) && ("DM".equalsIgnoreCase((String)agentObj.get(Constants.S_FAS_AGENTCODE)))) {
//					data.setSessionData(Constants.S_AGENT_SEGMENTATION,Constants.S_FAS_DM);
//				} else {
//					data.setSessionData(Constants.S_AGENT_SEGMENTATION,Constants.S_FAS_OTHERS);
//				}
				
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
				data.setSessionData("S_AGENTOFRECORDID", agentOfRecordId);
				if(null != data.getSessionData(Constants.S_PRODUCER_CODE) && "No Agent ID".equalsIgnoreCase((String)data.getSessionData(Constants.S_PRODUCER_CODE))) return Constants.SU;
				data.setSessionData(Constants.VXMLParam1, (String)data.getSessionData(Constants.S_PRODUCER_CODE));
				data.addToLog(currElementName, "KYCAF_IntermediarySegmentationInfoAPI : VXMLParam1 : "+data.getSessionData(Constants.VXMLParam1));
				String strProducerCode = (String)data.getSessionData(Constants.S_PRODUCER_CODE);
				if(strProducerCode.equalsIgnoreCase(agentOfRecordId)) {
					data.setSessionData(Constants.S_PRODUCER_CODE_MATCH, Constants.MATCH);
					data.setSessionData(Constants.S_AN1,strProducerCode);
					data.setSessionData(Constants.S_UPN1,uniqueProducerNumber);
				}
				else data.setSessionData(Constants.S_PRODUCER_CODE_MATCH, Constants.NOMATCH);
				strExitState = Constants.SU;
			}
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in apiResponseManupulation method  :: "+e);
			caa.printStackTrace(e);
		}
		return strExitState;
	}
	
	private String apiResponseManupulation1(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {
		String strExitState = Constants.ER;
		try {
			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			JSONArray agentsArr = (JSONArray)resp.get("agents");
			if(agentsArr!=null && agentsArr.size()>0) {
				String strProducerCode = (String)data.getSessionData(Constants.S_PRODUCER_CODE);
				for(Object agentObjTemp : agentsArr) {
					JSONObject agentObj = (JSONObject) agentObjTemp;
					data.setSessionData("isIAPremierAgent", agentObj.get("isIAPremierAgent"));//Premier agent Flag check Y/N
					data.addToLog("isIAPremierAgent flag:", "isIAPremierAgent");
					String agentOfRecordId = (String)agentObj.get("agentOfRecordId");
					if(strProducerCode.equalsIgnoreCase(agentOfRecordId)) {
						data.setSessionData(Constants.S_PRODUCER_CODE_MATCH, Constants.MATCH);
						data.setSessionData(Constants.S_AN1,strProducerCode);
						String uniqueProducerNumber =(String)agentObj.get("parentUniqueProducerNumber");
						data.setSessionData(Constants.S_UPN1,uniqueProducerNumber);
						
						boolean isPCaward=false;	
						String strPC_AdminYear = getPCAwardYearfromAdmin(data, currElementName);
						
						data.addToLog(currElementName, "Admin Award Year :: " + strPC_AdminYear);
						
						//caller Verification
						data.setSessionData(Constants.S_AgentType_CCAI, agentObj.get("agentType"));
						data.addToLog(currElementName, "Agent Type :: " + Constants.S_AgentType_CCAI);
						
						JSONArray awardsArr = (JSONArray) agentObj.get("awards");
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
						} else if(null != agentObj.get(Constants.S_FAS_AGENTCODE) && ("DM".equalsIgnoreCase((String)agentObj.get(Constants.S_FAS_AGENTCODE)))) {
							data.setSessionData(Constants.S_AGENT_SEGMENTATION,Constants.S_FAS_DM);
						}else if(null!=(String)data.getSessionData("S_PRIME_SEG_FLAG") && "Y".equalsIgnoreCase((String)data.getSessionData("S_PRIME_SEG_FLAG"))){
							data.setSessionData(Constants.S_AGENT_SEGMENTATION, "EA PRIME");
							caa.createMSPKey(caa, data, "IS_PRIME","PRIME_AGENT");
						} else {
							data.setSessionData(Constants.S_AGENT_SEGMENTATION,Constants.S_FAS_OTHERS);
						}

						data.addToLog(currElementName, "isPCaward : "+isPCaward+" :: S_AGENT_SEGMENTATION : "+data.getSessionData(Constants.S_AGENT_SEGMENTATION));
						data.setSessionData("IS_AGENTSEGMENTATION_DONE", Constants.STRING_YES);
						data.addToLog(currElementName, "AGENT SEGMENTATION DONE FLAG :: IS_AGENTSEGMENTATION_DONE :: " + data.getSessionData("IS_AGENTSEGMENTATION_DONE"));
						data.setSessionData("S_AGENTOFRECORDID", agentOfRecordId);
						//if(null != data.getSessionData(Constants.S_PRODUCER_CODE) && "No Agent ID".equalsIgnoreCase((String)data.getSessionData(Constants.S_PRODUCER_CODE))) return Constants.SU;
						break;
					} else {
						data.setSessionData(Constants.S_PRODUCER_CODE_MATCH, Constants.NOMATCH);
					}
				}
				strExitState = Constants.SU;
				data.setSessionData(Constants.VXMLParam1, (String)data.getSessionData(Constants.S_PRODUCER_CODE));
				data.addToLog(currElementName, "KYCAF_IntermediarySegmentationInfoAPI : VXMLParam1 : "+data.getSessionData(Constants.VXMLParam1));
			}
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in apiResponseManupulation method  :: "+e);
			caa.printStackTrace(e);
		}
		return strExitState;
	}
	
	public String getPCAwardYearfromAdmin(DecisionElementData data, String currElementName) {
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);objHostDetails.setinitalValue();objHostDetails.setinitalValue();
		String strRespBody = Constants.EmptyString;
		String strReqBody = Constants.EmptyString;
		String strExitState = Constants.ER;
		String awardYear = Constants.EmptyString;
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
		String region=null;
        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
        //END- UAT ENV SETUP CODE (PRIYA, SHAIK)
		try {
			if(data.getSessionData(Constants.S_AGENT_SEGMENTATION_URL)  != null && data.getSessionData(Constants.S_CONN_TIMEOUT) != null && data.getSessionData(Constants.S_READ_TIMEOUT) != null) {
				String url = (String)data.getSessionData(Constants.S_AGENT_SEGMENTATION_URL);
				String callerId = (String) data.getSessionData(Constants.S_CALLID);
				int conTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_CONN_TIMEOUT));
				int readTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_READ_TIMEOUT));
				LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
				GetAgencySegmentationTableByKey test = new GetAgencySegmentationTableByKey(); 
				
				JSONObject resp =  (JSONObject) test.start(url, callerId,Constants.tenantid, Constants.S_FAS_PC, conTimeout, readTimeout, context);
				strRespBody = resp.toString();
				data.addToLog(currElementName, "AgentSegmenation API response  :"+resp);
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
                  
				
				if(resp != null) {
					if(resp.containsKey(Constants.REQUEST_BODY)) strReqBody = resp.get(Constants.REQUEST_BODY).toString();
					if(resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200) {
						data.addToLog(currElementName, "Set FASMM_ADM_001  AgentSegmenation API Response into session with the key name of "+currElementName+Constants._RESP);
						strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
						data.setSessionData(currElementName+Constants._RESP, resp.get(Constants.RESPONSE_BODY));
						awardYear = AdminAgentSegmentationResponseManipulation(data, caa, currElementName, strRespBody);
						
						if (null != awardYear && !awardYear.isEmpty()) {
							strExitState = Constants.SU;
						}
					}
				}
			}
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in AgentSegmenation API call  :: "+e);
			caa.printStackTrace(e);
		}
		
			try {
				objHostDetails.startHostReport(currElementName,"AgentSegmenation API", strReqBody, " ",data.getSessionData(Constants.S_AGENT_SEGMENTATION_URL).toString().length() > 99 ?  (String)data.getSessionData(Constants.S_AGENT_SEGMENTATION_URL).toString().substring(0, 99) : (String)data.getSessionData(Constants.S_AGENT_SEGMENTATION_URL));
				objHostDetails.endHostReport(data,strRespBody , strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
			} catch (Exception e) {
				data.addToLog(currElementName,"Exception while forming host reporting for AgentSegmenation API call  :: "+e);
				caa.printStackTrace(e);
			}
			return awardYear;
	}
	
	
	
	private String AdminAgentSegmentationResponseManipulation(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {
		String strPC_AdminYear = Constants.EmptyString;
		try {
			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			JSONArray resArr =  (JSONArray) resp.get("res");
			if(resArr== null ||resArr.size() == 0 ) return  strPC_AdminYear;
			JSONObject resObj = (JSONObject) resArr.get(0);
			strPC_AdminYear = (String)resObj.get(Constants.S_FAS_PC_YEARS);
			data.setSessionData(Constants.S_PC_ADMIN_YEAR, strPC_AdminYear);
			data.addToLog(currElementName, "strPC_AdminYear :"+strPC_AdminYear);
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in apiResponseManupulation method  :: "+e);
			caa.printStackTrace(e);
		}
		return strPC_AdminYear;
	}
}
