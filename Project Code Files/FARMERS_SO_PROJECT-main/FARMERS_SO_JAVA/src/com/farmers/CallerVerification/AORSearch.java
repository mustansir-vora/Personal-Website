package com.farmers.CallerVerification;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.client.Lookupcall;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class AORSearch extends DecisionElementBase  {

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

		try {
			objHostDetails.startHostReport(currElementName,"doDecision","", strReqBody, "");
			objHostDetails.endHostReport(data,strRespBody , StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, "200","");
		} catch (Exception e) 
		{
			data.addToLog(currElementName,"Exception while forming host reporting for CV_HOST_001 call  :: "+e);
			caa.printStackTrace(e);
		}

		
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
			String InputagentAORID = (String) data.getSessionData(Constants.S_PRODUCER_CODE);
			
			data.addToLog(currElementName, "AOR  :: "+InputagentAORID);
			
			if(data.getSessionData(Constants.S_AGENTPOST_RETRIEVEBYAOR_URL) != null && data.getSessionData(Constants.S_USERID) != null
					&& data.getSessionData(Constants.S_SYSTEM_NAME) != null && data.getSessionData(Constants.S_CONN_TIMEOUT) != null && data.getSessionData(Constants.S_READ_TIMEOUT) != null 
					&& data.getSessionData(Constants.S_PRODUCER_CODE) != null && data.getSessionData(Constants.S_PRODUCER_CODE)!="")
			{

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
				data.addToLog(currElementName, "AgentTerminatedIndicator  :: "+agentterminatedindicator);
				data.addToLog(currElementName, "BusinessUnit  :: "+BU);
				data.addToLog(currElementName, "UserID  :: "+UserID);
				data.addToLog(currElementName, "systemname  :: "+systemname);
				data.addToLog(currElementName, "connTimeout  :: " + connTimeout);
				data.addToLog(currElementName, "readTimeout  :: " + readTimeout);
				data.addToLog(currElementName, "tid  :: "+tid);
				Lookupcall lookups = new Lookupcall();
				
				if(agentAORID.equalsIgnoreCase("NO AGENT ID")  || agentAORID.equalsIgnoreCase("NOMATCH") ||agentAORID.equalsIgnoreCase("NOINPUT") ) {
					
					
					data.setSessionData(Constants.S_PRODUCER_CODE, Constants.NA);

					data.addToLog(currElementName, "After checkin No agent id setting NA :: "+(String) data.getSessionData(Constants.S_PRODUCER_CODE));
					
					data.setSessionData(Constants.S_KYC_AUTHENTICATED_CCAI, Constants.STRING_NO);
				}
				
				else {
				LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
				
				//UAT EV CHANGE START	
				data.addToLog("API URL: ", wsurl);
				String prefix = "https://api-np-ss.farmersinsurance.com"; 
				String UAT_FLAG="";
		        if(wsurl.startsWith(prefix)) {
		        	UAT_FLAG="YES";
		        }
				if("YES".equalsIgnoreCase(UAT_FLAG)) {
				region = regionDetails.get(Constants.S_ACCLINK_ANILOOKUP_URL);
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
						data.addToLog(currElementName, "Set KYCBA_HOST_001 : MulesoftFarmerPolicyInquiry_Post �PI Response into session with the key name of "+currElementName+Constants._RESP);
						strRespBody = responses.get(Constants.RESPONSE_BODY).toString();
						//data.setSessionData(Constants.S_KYC_AUTHENTICATED, Constants.YES);
						//data.setSessionData(Constants.S_KYC_AUTHENTICATED_CCAI, Constants.STRING_YES);
						data.setSessionData(currElementName+Constants._RESP, responses.get(Constants.RESPONSE_BODY));
						strExitState = apiResponseManupulation_RetrieveAgentInformationbyAOR(data, caa, currElementName, strRespBody, strExitState);
					} else 
					{
						data.addToLog("response code not equal to 200", responses.toString());
						strRespBody = responses.get(Constants.RESPONSE_MSG).toString();
						data.setSessionData(Constants.S_KYC_AUTHENTICATED_CCAI, Constants.STRING_NO);
						data.setSessionData(Constants.S_KYC_AUTHENTICATED, Constants.NO);
						strExitState = Constants.SU;
					}

				}


			}
			}
			
			else {
				
				 strExitState = Constants.SU;
				
			}
			
			
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in CV_HOST_001 API call  :: "+e);
			caa.printStackTrace(e);
		}

		try {
			objHostDetails.startHostReport(currElementName, "CV_HOST_001", strReqBody,region,(String) data.getSessionData(Constants.S_AGENTPOST_RETRIEVEBYAOR_URL));
			objHostDetails.endHostReport(data, strRespBody, strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e1) {
			data.addToLog(currElementName,
					"Exception while forming host reporting for CV_HOST_001  PolicyInquiryAPI call  :: " + e1);
			caa.printStackTrace(e1);
		}
		return strExitState;
	}


	private String apiResponseManupulation_RetrieveAgentInformationbyAOR(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody, String strExitState) {
		String agentname = Constants.EmptyString;
		String phonenumber = Constants.EmptyString;
		JSONObject agentdetails = new JSONObject();
		
		try {

			 
			
			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			
		
			data.addToLog(currElementName, "Retreive Agent Details by AOR Parsed Response crossed resp:: " + resp);
			
			
			if (resp!=null) {
				
			JSONArray agentsArray = (JSONArray) resp.get("agents");
			
			if(agentsArray!=null) {
				
			data.addToLog(currElementName, "Retreive Agent Details by AOR Parsed Response crossed agent:: " + agentsArray.toString());
			
			StringBuilder formattedNames= new StringBuilder();
			StringBuilder formattedState= new StringBuilder();
			for (Object agentobj : agentsArray) {
				 agentdetails = (JSONObject) agentobj;
				
			JSONObject name = (JSONObject) agentdetails.get("name");
			data.addToLog(currElementName, "Retreive Agent Details by AOR Parsed Response name :: " + name);
					String firstname = (String) name.get("firstName");
					String lastname = (String) name.get("lastName");
					String fullName = firstname + " " + lastname;
					  if (firstname != null && !firstname.isEmpty() && lastname != null && !lastname.isEmpty() ) {
					formattedNames.append(fullName).append("|");
					  }
}	
			if (formattedNames.length() > 0 && formattedNames.charAt(formattedNames.length() - 1) == '|') {
				formattedNames.deleteCharAt(formattedNames.length() - 1);
				}
			
			data.setSessionData(Constants.S_CCAI_NAME, formattedNames.toString());
			data.addToLog(currElementName, "Appended First and Last Name  :: "+data.getSessionData(Constants.S_CCAI_NAME));
			
			 for (Object agentobj : agentsArray) {
						 agentdetails = (JSONObject) agentobj;
			
						 if (agentdetails.containsKey("address")) {
								
							 JSONArray addressdetails = (JSONArray) agentdetails.get("address");
							 data.addToLog(currElementName, "Retreive Agent Details by AOR Parsed Response address :: " + addressdetails);
							 Set<String> uniqueStates = new HashSet<>();
							 String previousState = null; 
							 for (Object objaddress : addressdetails) {
									JSONObject address = (JSONObject) objaddress;
								
									if (address.containsKey("state") )
											{
										String State = (String) address.get("state");
										
										  if (State != null && !State.isEmpty() && !State.equals(previousState)) {
										
										formattedState.append(State).append("|");
										previousState = State; 
										
										
										  }
									}
			
								}
						 }
			 }
			 if (formattedState.length() > 0 && formattedState.charAt(formattedState.length() - 1) == '|') {
				 formattedState.deleteCharAt(formattedState.length() - 1);
				}
				data.setSessionData(Constants.S_CCAI_STATE, formattedState.toString());
				data.setSessionData(Constants.S_KYC_AUTHENTICATED_CCAI, Constants.STRING_YES);
				data.setSessionData(Constants.S_KYC_AUTHENTICATED, Constants.YES);
				data.addToLog(currElementName, "Appended S_CCAI_STATE  :: "+data.getSessionData(Constants.S_CCAI_STATE));

for (Object agentobj : agentsArray) {
		agentdetails = (JSONObject) agentobj;			
		
		if(agentdetails.containsKey("intermediary")) {
			JSONObject intermediary = (JSONObject) agentdetails.get("intermediary");
			
			if (intermediary.containsKey("description")) {
				String description = (String) intermediary.get("description");
				
				if (description.trim().equalsIgnoreCase("Legal Business Entity")) {
					
					JSONObject name = (JSONObject) agentdetails.get("name");
					
					agentname = (String) name.get("agencyName");
					
					data.setSessionData(Constants.S_AGENCY_NAME, agentname);
					
					
					data.addToLog(currElementName, "S_API_AgentName :: "+data.getSessionData(Constants.S_AGENCY_NAME));
				}
			}
			strExitState = Constants.SU;

			}
		}

			}
			else {
				
				//data.setSessionData(Constants.S_PRODUCER_CODE, Constants.NA);

				data.addToLog(currElementName, "in Else Loop of Agent Array:: "+(String) data.getSessionData(Constants.S_PRODUCER_CODE));
				
				data.setSessionData(Constants.S_KYC_AUTHENTICATED_CCAI, Constants.STRING_NO);
				
				strExitState = Constants.ER;
			}}
		}
		 catch (Exception e) 
		{
			data.addToLog(currElementName,"Exception in apiResponseManupulation method  :: "+e);
			caa.printStackTrace(e);
		}

		return strExitState;
	}

}
