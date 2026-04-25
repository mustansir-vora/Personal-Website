package com.farmers.shared.host;


import java.util.HashMap;

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

public class POST_Agent extends DecisionElementBase  {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		data.setSessionData(Constants.S_USERID,"VRS-101");
		data.setSessionData(Constants.S_SYSTEM_NAME,"VRS");

		String StrExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		com.farmers.report.SetHostDetails objHostDetails = new com.farmers.report.SetHostDetails(caa);
		String strReqBody = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;

		String BW_BU_Flag = (String) data.getSessionData("S_FLAG_BW_BU");
		String FM_BU_Flag = (String) data.getSessionData("S_FLAG_FOREMOST_BU");
		String FDS_BU_Flag =(String) data.getSessionData("S_FLAG_FDS_BU");

		String mspBU="";

		try {
			String strBU = (String) data.getSessionData(Constants.S_BU);
			data.addToLog(currElementName,"strBU  :: "+strBU);

			data.addToLog(currElementName, "Bwistol_West_BU_Flag :: "+BW_BU_Flag);
			data.addToLog(currElementName, "Foremost_BU_Flag :: "+FM_BU_Flag);
			data.addToLog(currElementName, "Farmers_BU_Flag :: "+FDS_BU_Flag);

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

		} catch (Exception e)
		{
			data.addToLog(currElementName,"Exception while forming host details for SACI_HOST_002  :: "+e);
			caa.printStackTrace(e);
		}

		if(Constants.ER.equalsIgnoreCase(StrExitState)) {
			String state = (String) data.getSessionData("S_STATENAME");
			data.addToLog(data.getCurrentElement(), "Value of S_STATENAME : "+state);
			if("Hawaii".equalsIgnoreCase(state)) mspBU = "BWHI";
			
			if((BW_BU_Flag != null && !BW_BU_Flag.isEmpty() && BW_BU_Flag.equalsIgnoreCase(Constants.STRING_YES))|| (FDS_BU_Flag != null && !FDS_BU_Flag.isEmpty() && FDS_BU_Flag.equalsIgnoreCase(Constants.STRING_YES))) {
				data.setSessionData(Constants.S_MENU_SELCTION_KEY, mspBU+":SACI_HOST_002:"+Constants.FAILURE);
				data.addToLog(currElementName,"S_MENU_SELCTION_KEY for  SACI_HOST_002 :: "+data.getSessionData(Constants.S_MENU_SELCTION_KEY));
			}
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
		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
		String region=null;
        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
        //END- UAT ENV SETUP CODE (PRIYA, SHAIK)
		try {
			if(data.getSessionData(Constants.S_AGENTPOST_RETRIEVEBYAOR_URL) != null && data.getSessionData(Constants.S_USERID) != null
					&& data.getSessionData(Constants.S_SYSTEM_NAME) != null&& data.getSessionData(Constants.S_CONN_TIMEOUT) != null && data.getSessionData(Constants.S_READ_TIMEOUT) != null)
			{

				String tid = (String) data.getSessionData(Constants.S_CALLID);
				String connTimeout = (String) data.getSessionData(Constants.S_CONN_TIMEOUT);
				String readTimeout = (String) data.getSessionData(Constants.S_READ_TIMEOUT);
				String wsurl = (String) data.getSessionData(Constants.S_AGENTPOST_RETRIEVEBYAOR_URL);
				String agentAORID = (String) data.getSessionData("S_AGENT_ID");
				String agentterminatedindicator = "false"; //default(Hardcoded) value - false
				String UserID = (String) data.getSessionData(Constants.USER_ID);
				String systemname = (String) data.getSessionData(Constants.SYSTEM_NAME);

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
				 //UAT ENV CHANGE START(SHAK,PRIYA)			
				data.addToLog("API URL: ", wsurl);
				String prefix = "https://api-np-ss.farmersinsurance.com"; 
				String UAT_FLAG="";
		        if(wsurl.startsWith(prefix)) {
		        	UAT_FLAG="YES";
		        }
				if("YES".equalsIgnoreCase(UAT_FLAG)){
					 region = regionDetails.get(Constants.S_AGENTPOST_RETRIEVEBYAOR_URL);
					}else {
						region="PROD";
					}
				LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
				org.json.simple.JSONObject responses = lookups.AgentInfoRetrieveByAOR(wsurl, tid, agentAORID, BU, UserID, systemname, agentterminatedindicator, Integer.parseInt(connTimeout),Integer.parseInt(readTimeout), context,region,UAT_FLAG);
				data.addToLog("responses", responses.toString());
			//END 
				
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) responses.get(Constants.RESPONSE_CODE));
				
				if(responses != null)
				{
					if(responses.containsKey(Constants.REQUEST_BODY)) strReqBody = responses.get(Constants.REQUEST_BODY).toString();
					if(responses.containsKey(Constants.RESPONSE_CODE) && (int) responses.get(Constants.RESPONSE_CODE) == 200 && responses.containsKey(Constants.RESPONSE_BODY)) {
						data.addToLog(currElementName, "Set KYCBA_HOST_001 : MulesoftFarmerPolicyInquiry_Post �PI Response into session with the key name of "+currElementName+Constants._RESP);
						strRespBody = responses.get(Constants.RESPONSE_BODY).toString();
						data.setSessionData(currElementName+Constants._RESP, responses.get(Constants.RESPONSE_BODY));
						strExitState = apiResponseManupulation_RetrieveAgentInformationbyAOR(data, caa, currElementName, strRespBody, strExitState);
					} else 
					{
						strRespBody = responses.get(Constants.RESPONSE_MSG).toString();
					}

				}


			}
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in SACI_HOST_002 API call  :: "+e);
			caa.printStackTrace(e);
		}

		try {
			objHostDetails.startHostReport(currElementName, "SACI_HOST_002", strReqBody, region,(String) data.getSessionData(Constants.S_AGENTPOST_RETRIEVEBYAOR_URL));
			objHostDetails.endHostReport(data, strRespBody, strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e1) {
			data.addToLog(currElementName,
					"Exception while forming host reporting for SBP_HOST_001  PolicyInquiryAPI call  :: " + e1);
			caa.printStackTrace(e1);
		}
		return strExitState;
	}


	private String apiResponseManupulation_RetrieveAgentInformationbyAOR(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody, String strExitState) {
		String agentname = Constants.EmptyString;
		String phonenumber = Constants.EmptyString;
		String CleanAgentName =Constants.EmptyString;
		JSONObject agentdetails = new JSONObject();
		try {

			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			data.addToLog(currElementName, "Retreive Agent Details by AOR Parsed Response :: " + resp);
			JSONArray agentsArray = (JSONArray) resp.get("agents");

			for (Object agentobj : agentsArray) {
				agentdetails = (JSONObject) agentobj;
				if (agentdetails.containsKey("name")) {
					JSONObject name = (JSONObject) agentdetails.get("name");

					if (name.containsKey("agencyName")) {
						agentname = (String) name.get("agencyName");
						CleanAgentName = agentname.replaceAll("[^a-zA-Z0-9 ]", "");
						data.addToLog(currElementName, "Agency Name Available - Considering Agency Name to be played :: "+CleanAgentName);
						data.setSessionData("S_API_AgentName", CleanAgentName);
						data.addToLog(currElementName, "S_API_AgentName :: "+ data.getSessionData("S_API_AgentName"));
					}
					else if(agentdetails.containsKey("intermediary")) {
						JSONObject intermediary = (JSONObject) agentdetails.get("intermediary");
						if (intermediary.containsKey("description")) {
							String description = (String) intermediary.get("description");
							if (description.trim().equalsIgnoreCase("Agent")) {
								String firstname = (String) name.get("firstName");
								String lastname = (String) name.get("lastName");
								agentname = firstname+" "+lastname;
								CleanAgentName = agentname.replaceAll("[^a-zA-Z0-9 ]", "");
								data.addToLog(currElementName, "Agency Name Not Available - Considering Agent Name to be played :: "+CleanAgentName);
								data.setSessionData("S_API_AgentName", CleanAgentName);
								data.addToLog(currElementName, "S_API_AgentName :: "+data.getSessionData("S_API_AgentName"));
							}
						}

					}

					if (agentdetails.containsKey("phones") && !agentname.isEmpty()) {
						JSONArray phonedetails = (JSONArray) agentdetails.get("phones");

						for (Object objphone : phonedetails) {
							JSONObject phone = (JSONObject) objphone;

							if (phone.containsKey("type") && phone.containsKey("number") && phone.containsKey("electronicType")) {
								String type = (String) phone.get("type");
								String phoneNum = (String) phone.get("number");
								String electronictype = (String) phone.get("electronicType");
								data.addToLog(currElementName, "ContactPointType :: "+type+", ElectonicType :: "+electronictype);

								if (type.contains("O") && electronictype.equalsIgnoreCase("Voice")) {
									phonenumber = phoneNum;
									data.addToLog(currElementName, "ContactPointType is O & ElectronicType is Voice :: Consider the phone number returned along with it." + phonenumber);
									data.setSessionData("S_API_PhoneNumber", phonenumber);
									data.addToLog(currElementName, "S_API_PhoneNumber :: " + data.getSessionData("S_API_PhoneNumber"));
								}
							}
						}
					}
				}

				else {
					data.addToLog(currElementName, "Name Field Not Available In API response :: " + agentdetails);
				}
			}
			if (!agentname.isEmpty() &&  !phonenumber.isEmpty()) {
				CleanAgentName = CleanAgentName.replaceAll(" ", ".");
				data.setSessionData(Constants.VXMLParam1, CleanAgentName);
				data.setSessionData(Constants.VXMLParam2, phonenumber);
				data.addToLog(currElementName, "VXMLPARAM 1 :: " + CleanAgentName  + " :: VXMLPARAM 2 :: " + phonenumber);
				strExitState = Constants.SU;
			}

		} catch (Exception e) 
		{
			data.addToLog(currElementName,"Exception in apiResponseManupulation method  :: "+e);
			caa.printStackTrace(e);
		}

		return strExitState;
	}

}
