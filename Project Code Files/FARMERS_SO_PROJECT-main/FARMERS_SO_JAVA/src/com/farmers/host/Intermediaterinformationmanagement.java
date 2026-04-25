package com.farmers.host;


import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.FarmersAPI.AgentInfo_Post;
import com.farmers.FarmersAPI_NP.AgentInfo_NP_Post;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

/*KYCMF_HOST_001*/

public class Intermediaterinformationmanagement extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String StrExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);
		String strReqBody = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;
		String businesssUnit = Constants.EmptyString;
		
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		
		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
		String region=null;
        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
        //END- UAT ENV SETUP CODE (PRIYA, SHAIK)
		try {
			LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);
			
			data.addToLog(data.getCurrentElement(), "URL :: " + data.getSessionData(Constants.S_INTERMEDIATERINFORMATIONMGMT_URL) + " :: Call ID :: " + data.getSessionData(Constants.S_CALLID)
					+ " :: BU :: " + data.getSessionData(Constants.S_BU) + " :: ANI :: " + (String) data.getSessionData(Constants.S_ANI) + " :: SYSTEM NAME :: " + Constants.SYSTEM_NAME
					+ " :: User ID :: " + Constants.USER_ID);
			data.setSessionData("S_KYC_DISABLED_AGENT", "N");
			if (data.getSessionData(Constants.S_INTERMEDIATERINFORMATIONMGMT_URL) != null
					&& data.getSessionData(Constants.S_CALLID) != null && data.getSessionData(Constants.S_BU) != null
					&& data.getSessionData(Constants.S_ANI) != null 
					&& data.getSessionData(Constants.S_CONN_TIMEOUT) != null
					&& data.getSessionData(Constants.S_READ_TIMEOUT) != null) {
				String url = (String) data.getSessionData(Constants.S_INTERMEDIATERINFORMATIONMGMT_URL);

				String tid = (String) data.getSessionData(Constants.S_CALLID);
				businesssUnit = (String) data.getSessionData(Constants.S_BU);
				data.addToLog(data.getCurrentElement(), "Business UNIT Value from the session :: " + businesssUnit);
				
				if (businesssUnit.contains("FWS")) {
					businesssUnit = "IVRFWS";
				} else if (businesssUnit.contains("BW")) {
					businesssUnit = "IVRBW";
				} else if (businesssUnit.contains("Foremost")) {
					businesssUnit = "IVRFRMST";
				} else if (businesssUnit.contains("Farmers")) {
					businesssUnit = "IVRPLA";
				} else if (businesssUnit.contains("Commercial")) {
					businesssUnit = "IVRPLA";
				}else {
					businesssUnit = "IVRPLA";
				}
				data.addToLog(data.getCurrentElement(), "Business UNIT Value to be sent :: " + businesssUnit);
				
				String phonenumber = (String) data.getSessionData(Constants.S_ANI);
				String sysname = Constants.SYSTEM_NAME;
				String userid = Constants.USER_ID;

				int conTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_CONN_TIMEOUT));
				int readTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_READ_TIMEOUT));

				AgentInfo_Post apiObj = new AgentInfo_Post();
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
					String Key =Constants.S_INTERMEDIATERINFORMATIONMGMT_URL;
					 region = regionDetails.get(Key);
					data.addToLog("Region for UAT endpoint: ", region);
					resp = objNP.start(url, tid, businesssUnit, phonenumber, userid, sysname, conTimeout,readTimeout, context, region);
				}else {
				    resp = apiObj.start(url, tid, businesssUnit, phonenumber, userid, sysname, conTimeout,readTimeout, context);
				}
				//Non prod changes-Priya
				data.addToLog(currElementName, "�PI response  :" + resp);
				
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
				if (resp != null) {
					strReqBody= resp.get(Constants.REQUEST_BODY).toString();
					if (resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200
							&& resp.containsKey(Constants.RESPONSE_BODY)) {
						
						strRespBody=resp.get(Constants.RESPONSE_BODY).toString();
						data.addToLog(currElementName,"Set Intermediaterinformationmanagement API Response into session with the key name of " +  Constants._RESP);
						String strRespBodyTemp = resp.get(Constants.RESPONSE_BODY).toString();
						data.addToLog(currElementName, "Response Body ::"+  strRespBodyTemp);
						JSONParser parser =new JSONParser();
						JSONObject json = (JSONObject) parser.parse(strRespBodyTemp);
						if(json.containsKey("agents")) {
							data.setSessionData("S_ANI_MATCH", "TRUE");
							data.addToLog(currElementName, "Agent");
							data.setSessionData("S_KYC_DISABLED_AGENT", "Y");
							StrExitState = Constants.SU;
						}else {
							data.addToLog(currElementName, "Not a agent");
						}
						
					} else {
						strRespBody = resp.get(Constants.RESPONSE_MSG).toString();
					}
				}
			}else {
				data.addToLog(data.getCurrentElement(), "API Details is null");
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in Intermediaterinformationmanagement API call  :: " + e);
			caa.printStackTrace(e);
		}

		try {
			objHostDetails.startHostReport(currElementName, "Intermediaterinformationmanagement", strReqBody,region, (String) data.getSessionData(Constants.S_INTERMEDIATERINFORMATIONMGMT_URL));
			objHostDetails.endHostReport(data, strRespBody,
					StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception while forming host reporting for Intermediaterinformationmanagement API call  :: " + e);
			caa.printStackTrace(e);
		}

		return StrExitState;
	}
	
	public static void main(String[] args) {
		String strJSON = "{\r\n"
				+ "    \"agents\": [\r\n"
				+ "        {\r\n"
				+ "            \"agentOfRecordId\": \"162561\",\r\n"
				+ "            \"parentUniqueProducerNumber\": \"40223\",\r\n"
				+ "            \"agentCode\": \"04\",\r\n"
				+ "            \"agentType\": \"EA\",\r\n"
				+ "            \"awards\": [\r\n"
				+ "                {\r\n"
				+ "                    \"award\": \"Championship\",\r\n"
				+ "                    \"awardYear\": \"2017\"\r\n"
				+ "                },\r\n"
				+ "                {\r\n"
				+ "                    \"award\": \"Presidents Council\",\r\n"
				+ "                    \"awardYear\": \"2022\"\r\n"
				+ "                },\r\n"
				+ "                {\r\n"
				+ "                    \"award\": \"Topper Club\",\r\n"
				+ "                    \"awardYear\": \"2022\"\r\n"
				+ "                },\r\n"
				+ "                {\r\n"
				+ "                    \"award\": \"Blue Vase\"\r\n"
				+ "                }\r\n"
				+ "            ]\r\n"
				+ "        }\r\n"
				+ "    ]\r\n"
				+ "}";
		
		String strJSON1 = "{\r\n"
				+ "    \"transactionNotification\": {\r\n"
				+ "        \"transactionCode\": \"E\",\r\n"
				+ "        \"transactionStatus\": \"Error\",\r\n"
				+ "        \"remark\": {\r\n"
				+ "            \"messageCode\": \"IVR101\",\r\n"
				+ "            \"messageDate\": \"2024-04-09T18:42:05.193Z\",\r\n"
				+ "            \"messageText\": \"Information- Phone number did not match or no phone number exists\"\r\n"
				+ "        }\r\n"
				+ "    }\r\n"
				+ "}";
		
		JSONParser parser =new JSONParser();
		try {
			JSONObject json = (JSONObject) parser.parse(strJSON);
			if(json.containsKey("agents")) {
				System.out.println("Agent");
			}else {
				System.out.println("Someone");
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
