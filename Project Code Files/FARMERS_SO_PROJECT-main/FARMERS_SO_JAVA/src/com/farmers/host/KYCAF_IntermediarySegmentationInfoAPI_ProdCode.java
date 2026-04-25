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
import com.farmers.client.Lookupcall;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

/*KYCAF_HOST_002*/
public class KYCAF_IntermediarySegmentationInfoAPI_ProdCode extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		
		

		String StrExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		com.farmers.report.SetHostDetails objHostDetails = new com.farmers.report.SetHostDetails(caa);
		String strReqBody = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;

		

		String mspBU="";

		try {
			
			data.setSessionData(Constants.S_USERID,"VRS-101");
			data.setSessionData(Constants.S_SYSTEM_NAME,"VRS");
			
			//String UserID = (String) data.getSessionData(Constants.USER_ID);
			//String systemname = (String) data.getSessionData(Constants.SYSTEM_NAME);
			
			String strBU = (String) data.getSessionData(Constants.S_BU);
			
			ArrayList<String> strBristolCode = (ArrayList<String>) data.getSessionData("BRISTOL_LOB_LIST");
			ArrayList<String>  strFarmersCode = (ArrayList<String>) data.getSessionData("FARMER_LOB_LIST");
			ArrayList<String>  strForemostCode = (ArrayList<String>) data.getSessionData("FOREMOST_LOB_LIST");
			ArrayList<String>  strFWSCode = (ArrayList<String>) data.getSessionData("FWS_LOB_LIST");
			ArrayList<String>  str21stCode = (ArrayList<String>) data.getSessionData("21st_LOB_LIST");
			
			data.addToLog(currElementName, " PolicyLookup : strBU :: "+strBU);
			data.addToLog(currElementName, " A_BRISTOL_LOB : "+strBristolCode);
			data.addToLog(currElementName, " A_FARMERS_LOB : "+strFarmersCode);
			data.addToLog(currElementName, " A_FOREMOST_LOB : "+strForemostCode);
			data.addToLog(currElementName, " A_FWS_LOB : "+strFWSCode);
			data.addToLog(currElementName, " A_21ST_LOB : "+str21stCode);
			
			if(strBristolCode!=null && null != strBU && strBristolCode.contains(strBU)) {
				data.setSessionData("S_FLAG_BW_BU", Constants.STRING_YES);
			} else if(strFarmersCode!=null && strBU!=null && strFarmersCode.contains(strBU)) {
			data.setSessionData("S_FLAG_FDS_BU", Constants.STRING_YES);
			} else if(strForemostCode!=null && strBU!=null && strForemostCode.contains(strBU)) {
				data.setSessionData("S_FLAG_FOREMOST_BU", Constants.STRING_YES);
			} else if(strFWSCode!=null && strBU!=null && strFWSCode.contains(strBU)) {
				data.setSessionData("S_FLAG_FWS_BU", Constants.STRING_YES);
			} else if(str21stCode!=null && strBU!=null && str21stCode.contains(strBU)) {
				data.setSessionData("S_FLAG_21ST_BU", Constants.STRING_YES);
			}
			data.addToLog(currElementName, " strBU : "+strBU+" :: S_FLAG_BW_BU : "+data.getSessionData("S_FLAG_BW_BU")
			+" :: S_FLAG_FDS_BU : "+ data.getSessionData("S_FLAG_FDS_BU")+" :: S_FLAG_FOREMOST_BU : "+ data.getSessionData("S_FLAG_FOREMOST_BU")
			+" :: S_FLAG_FWS_BU : "+ data.getSessionData("S_FLAG_FWS_BU")+" :: S_FLAG_21ST_BU : "+ data.getSessionData("S_FLAG_21ST_BU"));
		
			
			
			//String strBU = (String) data.getSessionData(Constants.S_BU);
			
			data.addToLog(currElementName,"strBU  :: "+strBU);
			
			String BW_BU_Flag = (String) data.getSessionData("S_FLAG_BW_BU");
			String FM_BU_Flag = (String) data.getSessionData("S_FLAG_FOREMOST_BU");
			String FDS_BU_Flag =(String) data.getSessionData("S_FLAG_FDS_BU");
			String FWS_BU_Flag =(String) data.getSessionData("S_FLAG_FWS_BU");
			data.addToLog(currElementName, "Bwistol_West_BU_Flag :: "+BW_BU_Flag);
			data.addToLog(currElementName, "Foremost_BU_Flag :: "+FM_BU_Flag);
			data.addToLog(currElementName, "Farmers_BU_Flag :: "+FDS_BU_Flag);
			data.addToLog(currElementName, "S_FLAG_FWS_BU :: "+FWS_BU_Flag);
			if (BW_BU_Flag != null && !BW_BU_Flag.isEmpty() && BW_BU_Flag.equalsIgnoreCase(Constants.STRING_YES)) {
				String BusinessUnit = "BW";
				mspBU="BW";
				StrExitState = Agents_POST(strRespBody, strRespBody, data, caa, currElementName, StrExitState, BusinessUnit);
			} else if (FM_BU_Flag != null && !FM_BU_Flag.isEmpty() && FM_BU_Flag.equalsIgnoreCase(Constants.STRING_YES)) {
				String BusinessUnit = "FM";
				mspBU="FOREMOST";
				StrExitState = Agents_POST(strRespBody, strRespBody, data, caa, currElementName, StrExitState, BusinessUnit);
			} else if (FDS_BU_Flag != null && !FDS_BU_Flag.isEmpty() && FDS_BU_Flag.equalsIgnoreCase(Constants.STRING_YES)) {
				String BusinessUnit = "FAR";
				mspBU="FARMERS";
				StrExitState = Agents_POST(strRespBody, strRespBody, data, caa, currElementName, StrExitState, BusinessUnit);
			}
			
			else if (FWS_BU_Flag != null && !FWS_BU_Flag.isEmpty() && FWS_BU_Flag.equalsIgnoreCase(Constants.STRING_YES)) {
				String BusinessUnit = "FSAH";
				mspBU="FARMERS";
				StrExitState = Agents_POST(strRespBody, strRespBody, data, caa, currElementName, StrExitState, BusinessUnit);
			}
			
			else {
				
				String BusinessUnit = "FAR";
				StrExitState = Agents_POST(strRespBody, strRespBody, data, caa, currElementName, StrExitState, BusinessUnit);
			}


		} catch (Exception e)
		{
			data.addToLog(currElementName,"Exception while forming host details for CV_HOST_001  :: "+e);
			caa.printStackTrace(e);
		}

//		try {
//			objHostDetails.startHostReport(currElementName,"doDecision","", strReqBody, "");
//			objHostDetails.endHostReport(data,strRespBody , StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, "200","");
//		} catch (Exception e) 
//		{
//			data.addToLog(currElementName,"Exception while forming host reporting for CV_HOST_001 call  :: "+e);
//			caa.printStackTrace(e);
//		}

		
		return StrExitState;
	}
	
	private String Agents_POST(String strRespBody, String strRespBody2, DecisionElementData data, CommonAPIAccess caa, String currElementName, String strExitState, String BU) 
	{
		SetHostDetails objHostDetails = new SetHostDetails(caa);
		objHostDetails.setinitalValue();
		String strReqBody = Constants.EmptyString;
		
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		
		//UAT ENV CHANHE
		String region=null;
		HashMap<String,String> regionDetails=(HashMap<String,String>)data.getSessionData(Constants.RegionHM);
		//UAT CHANGE END
		try {			
			if(data.getSessionData(Constants.S_AGENTPOST_RETRIEVEBYAOR_URL) != null && data.getSessionData(Constants.S_USERID) != null
					&& data.getSessionData(Constants.S_SYSTEM_NAME) != null && data.getSessionData(Constants.S_CONN_TIMEOUT) != null && data.getSessionData(Constants.S_READ_TIMEOUT) != null 
					&& data.getSessionData(Constants.S_PRODUCER_CODE) != null && data.getSessionData(Constants.S_PRODUCER_CODE)!="")
			{
				String InputagentAORID = (String) data.getSessionData(Constants.S_PRODUCER_CODE);
				
				data.addToLog(currElementName, "AOR  :: "+InputagentAORID);
				
				String tid = (String) data.getSessionData(Constants.S_CALLID);
				String connTimeout = (String) data.getSessionData(Constants.S_CONN_TIMEOUT);
				String readTimeout = (String) data.getSessionData(Constants.S_READ_TIMEOUT);
				String wsurl = (String) data.getSessionData(Constants.S_AGENTPOST_RETRIEVEBYAOR_URL);
				
				
				StringBuilder outputBuilder = new StringBuilder();

		        for (char c : InputagentAORID.toCharArray()) {
		        	
		            outputBuilder.append(Character.isLowerCase(c) ? Character.toUpperCase(c) : c);
		        }

		        String agentAORID = outputBuilder.toString();
				
				String agentterminatedindicator = "false"; //default(Hardcoded) value - false
				String UserID = (String) data.getSessionData(Constants.S_USERID);
				String systemname = (String) data.getSessionData(Constants.S_SYSTEM_NAME);

				if (BU.equalsIgnoreCase("BW")) {
					UserID = "BW-101";
					systemname = BU;
				}

				data.addToLog(currElementName, "AOR  :: "+agentAORID);
				data.addToLog("KYCAF_HOST_003 flag is: ", (String)data.getSessionData("KYCAF_HOST_003_FLAG"));
				
				String Host_Flag= (String)data.getSessionData("KYCAF_HOST_003_FLAG");
				
				if("Y".equalsIgnoreCase(Host_Flag)){
					 agentAORID=agentAORID.replace("O","0");
					 data.addToLog(currElementName,"AOR after replacing O with 0: "+agentAORID);
						
			     }else if(agentAORID.contains("O")) {
					data.setSessionData("KYCAF_HOST_003_FLAG", "Y");
					data.addToLog(currElementName, "Enabling KYCAF_HOST_003 flag :"+(String)data.getSessionData("KYCAF_HOST_003_FLAG"));
				}
				
				data.addToLog(currElementName, "AgentTerminatedIndicator  :: "+agentterminatedindicator);
				data.addToLog(currElementName, "BusinessUnit  :: "+BU);
				data.addToLog(currElementName, "UserID  :: "+UserID);
				data.addToLog(currElementName, "systemname  :: "+systemname);
				data.addToLog(currElementName, "connTimeout  :: " + connTimeout);
				data.addToLog(currElementName, "readTimeout  :: " + readTimeout);
				data.addToLog(currElementName, "tid  :: "+tid);
				Lookupcall lookups = new Lookupcall();
				
				LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
				
				//UAT EV CHANGE START	
				data.addToLog("API URL: ", wsurl);
				String prefix = "https://api-np-ss.farmersinsurance.com"; 
				String UAT_FLAG="";
		        if(wsurl.startsWith(prefix)) {
		        	UAT_FLAG="YES";
		        }
				if("YES".equalsIgnoreCase(UAT_FLAG)) {
				region = regionDetails.get(Constants.S_AGENTPOST_RETRIEVEBYAOR_URL);
				}else {
					region="PROD";
				}
				data.addToLog("Region: ", region);
				org.json.simple.JSONObject responses = lookups.AgentInfoRetrieveByAOR(wsurl, tid, agentAORID, BU, UserID, systemname, agentterminatedindicator, Integer.parseInt(connTimeout),Integer.parseInt(readTimeout), context,region,UAT_FLAG);
				data.addToLog("responses", responses.toString());
				
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) responses.get(Constants.RESPONSE_CODE));
				if(responses != null)
				{
					if(responses.containsKey(Constants.REQUEST_BODY)) strReqBody = responses.get(Constants.REQUEST_BODY).toString();
					if(responses.containsKey(Constants.RESPONSE_CODE) && (int) responses.get(Constants.RESPONSE_CODE) == 200 && responses.containsKey(Constants.RESPONSE_BODY)) {
						data.addToLog(currElementName, "Set API Response into session with the key name of "+currElementName+Constants._RESP);
						strRespBody = responses.get(Constants.RESPONSE_BODY).toString();
						data.addToLog(currElementName, "Response Body ::"+  strRespBody);
						//data.setSessionData(Constants.S_KYC_AUTHENTICATED, Constants.YES);
						//data.setSessionData(Constants.S_KYC_AUTHENTICATED_CCAI, Constants.STRING_YES);
						data.setSessionData(currElementName+Constants._RESP, responses.get(Constants.RESPONSE_BODY));
						JSONParser parser =new JSONParser();
						JSONObject json = (JSONObject) parser.parse(strRespBody);
						if(json.containsKey("agents")) {
							data.setSessionData(Constants.S_PRODUCER_CODE_MATCH, Constants.MATCH);
							data.setSessionData("S_CALLER_AUTH", "Verified");
							data.addToLog(currElementName, "Agent");
							JSONArray resArr = (JSONArray)json.get("agents");
							if(resArr == null || resArr.size() == 0 ) {
								//data.setSessionData(Constants.S_CALLLER_TYPE,"03");
								return  Constants.ER;
							}
							JSONObject agentObj = (JSONObject) resArr.get(0);
							if(agentObj.containsKey("isIAPremierAgent")) {
							data.setSessionData("isIAPremierAgent", agentObj.get("isIAPremierAgent"));//Premier agent Flag check Y/N
							data.addToLog("isIAPremierAgent flag:", (String)data.getSessionData("isIAPremierAgent"));
							}
							//Agent type collecting by Arshath -start
							if(agentObj.containsKey("agentType")) {
							    data.setSessionData(Constants.S_AgentType_CCAI, agentObj.get("agentType"));
							    data.addToLog(currElementName, "Agent Type :: " + agentObj.get("agentType"));
							}
							//Agent type collecting by Arshath-End
							if (agentObj.containsKey("phones")) {
								data.addToLog(currElementName, "Contains phone ::");
								JSONArray phones = (JSONArray) agentObj.get("phones");
								data.addToLog(currElementName, "PHONES ARRAY ::"+phones);
								for (int singlePhone = 0; singlePhone < phones.size(); singlePhone++) {
									JSONObject singlePhoneObj = (JSONObject) phones.get(singlePhone);
                                     data.addToLog(currElementName,"SINGLE PHONE OBJ::"+singlePhoneObj);
									if (singlePhoneObj.containsKey("type")
											&& singlePhoneObj.containsKey("electronicType")) {
										data.addToLog(currElementName,"TYPE and ELEC Type PASSED");
										String type = (String) singlePhoneObj.get("type");
										String electronicType = (String) singlePhoneObj.get("electronicType");
										if (type != null && electronicType != null && !type.trim().isEmpty()
												&& !electronicType.trim().isEmpty() && type.trim().equalsIgnoreCase("O")
												&& electronicType.trim().equalsIgnoreCase("Voice")) {
											String number = (String) singlePhoneObj.get("number");
											data.addToLog(currElementName, "AOR ANI in final step:"+number);
											data.setSessionData(Constants.S_AOR_ANI, number);
											data.addToLog(currElementName,"TYPE:"+type+":ELECTRONIC TYPE ::"+electronicType+":NUMBER ::"+data.getSessionData(Constants.S_AOR_ANI));
										}
									}
								}
							}else {
								data.addToLog(currElementName, "agent object does not contain phones array");
							}
							strExitState = Constants.SU;
							data.setSessionData(Constants.VXMLParam1, (String)data.getSessionData(Constants.S_PRODUCER_CODE));
							data.addToLog(currElementName, "KYCAF_IntermediarySegmentationInfoAPI : VXMLParam1 : "+data.getSessionData(Constants.VXMLParam1));
						}else {
							data.setSessionData(Constants.S_PRODUCER_CODE_MATCH, Constants.NOMATCH);
						}
						strExitState = Constants.SU;
						data.setSessionData(Constants.VXMLParam1, (String)data.getSessionData(Constants.S_PRODUCER_CODE));
						data.addToLog(currElementName, "KYCAF_IntermediarySegmentationInfoAPI : VXMLParam1 : "+data.getSessionData(Constants.VXMLParam1));
					} else 
					{
						strRespBody = responses.get(Constants.RESPONSE_MSG).toString();
					}

				}


			}			
			
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in Ace lookup with AOR API call  :: "+e);
			caa.printStackTrace(e);
		}

		try {
			objHostDetails.startHostReport(currElementName, "intermediaterinformationmanagementv1-retrieveIntermediaries_AgentAPI-V2", strReqBody,region,(String) data.getSessionData(Constants.S_AGENTPOST_RETRIEVEBYAOR_URL));
			objHostDetails.endHostReport(data, strRespBody, strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e1) {
			data.addToLog(currElementName,
					"Exception while forming host reporting for intermediaterinformationmanagementv1-retrieveIntermediaries_AgentAPI-V2 :: " + e1);
			caa.printStackTrace(e1);
		}
		return strExitState;
	}
}