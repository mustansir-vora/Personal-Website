package com.farmers.shared.host;

import java.util.HashMap;

import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.client.Lookupcall;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class ACCOUNTLINKANILOOKUP_POST extends DecisionElementBase 


{

	public String getElementName() 
	{
		return "ACCOUNTLINKANILOOKUP_POST";
	}
	public String getDisplayFolderName() 
	{
		return "FARMERSETCallFLOW";
	}
	public String getDescription() 
	{
		return "Retrieve the ACCOUNTLINKANILOOKUP_POST webservice response";
	}

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException 
	{
						String StrExitState = Constants.ER;
						CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
						SetHostDetails objHostDetails = new SetHostDetails(caa);
						String strReqBody = Constants.EmptyString;
						String strRespBody = Constants.EmptyString;
						//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
						String region=null;
				        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
				        //END- UAT ENV SETUP CODE (PRIYA, SHAIK)
						try {
							
							if(null != data.getSessionData("S_ACCLINKANI_CALLED") && "TRUE".equalsIgnoreCase((String)data.getSessionData("S_ACCLINKANI_CALLED"))) {
								data.addToLog(currElementName, "AccountLinkANI is already called :: Response from session :: "+(String)data.getSessionData(Constants.ACCLINKANIJSONRESPSTRING));
								String resp = (String)data.getSessionData(Constants.ACCLINKANIJSONRESPSTRING);
								apiResponseManupulation_AccountLookup(data, caa, currElementName, resp);
								return Constants.SU;
							}
							
							if(data.getSessionData(Constants.S_ACCOUNT_LOOKUPURL) != null && 
									data.getSessionData(Constants.S_READ_TIMEOUT) != null && 
									data.getSessionData(Constants.S_CONN_TIMEOUT) != null && 
									data.getSessionData(Constants.S_ANI) != null && 
									data.getSessionData(Constants.S_LOB) != null)
							{
							String wsurl = (String) data.getSessionData(Constants.S_ACCOUNT_LOOKUPURL);
							String  ani = (String) data.getSessionData(Constants.S_ANI);
							String  conntimeout = (String) data.getSessionData(Constants.S_CONN_TIMEOUT);
							String  readtimeout = (String) data.getSessionData(Constants.S_READ_TIMEOUT);
							String  Callid = (String) data.getSessionData(Constants.S_CALLID);
							LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
							data.addToLog("Callid", Callid);
							data.addToLog("readtimeout", readtimeout);
							data.addToLog("conntimeout", conntimeout);
							data.addToLog("ani", ani);
							data.addToLog("wsurl", wsurl);

							Lookupcall lookups = new Lookupcall();
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
							org.json.simple.JSONObject responses = lookups.GetAccountLookup(wsurl,Callid,ani,Integer.parseInt(conntimeout), Integer.parseInt(readtimeout),context,region,UAT_FLAG);
							data.addToLog("responses", responses.toString());
								//JSONObject jsonobj = new JSONObject(responses);
								Integer HTTPStatusCode = (Integer) responses.get("responseCode");
								String HTTPRespMsg = (String) responses.get("responseMsg");
								
								if(responses != null)
								{
									if(responses.containsKey(Constants.REQUEST_BODY)) strRespBody = responses.get(Constants.REQUEST_BODY).toString();
									if(responses.containsKey(Constants.RESPONSE_CODE) && (int) responses.get(Constants.RESPONSE_CODE) == 200 && responses.containsKey(Constants.RESPONSE_BODY)) 
									{
										data.addToLog(currElementName, "Set ForemostPolicyInquiry �PI Response into session with the key name of "+currElementName+Constants._RESP);
										strRespBody = responses.get(Constants.RESPONSE_BODY).toString();
										data.setSessionData(currElementName+Constants._RESP, responses.get(Constants.RESPONSE_BODY));
										data.setSessionData(Constants.ACCLINKANIJSONRESPSTRING, strRespBody);
										data.setSessionData("S_ACCLINKANI_CALLED", "TRUE");
										apiResponseManupulation_AccountLookup(data, caa, currElementName, strRespBody);
										StrExitState = Constants.SU;
									} else
									{
										strRespBody = responses.get(Constants.RESPONSE_MSG).toString();
									}
									
								}
							}
						} catch (Exception e) {
							data.addToLog(currElementName,"Exception in KYCAF_HOST_001  ForemostPolicyInquiry API call  :: "+e);
							caa.printStackTrace(e);
						}
						return StrExitState;
					}
	private void apiResponseManupulation_AccountLookup(DecisionElementData data, CommonAPIAccess caa,String currElementName, String strRespBody) throws ParseException, AudiumException 
	
	{
		data.addToLog("S_ClaimLookup","NO");
		data.setSessionData("S_ClaimLookup","NO");
		JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
		if(resp.containsKey("claims"))
		{
			if(resp.get("claims")!=null)
			{
			
			data.addToLog("S_ClaimLookup","YES");
			data.setSessionData("S_ClaimLookup","YES");	
			}
		}
		
		JSONObject resultsObj = (JSONObject) resp.get("results");
		JSONArray policiesArr = (JSONArray)resultsObj.get("policies");
		if(policiesArr != null && policiesArr.size() > 0) {
			data.setSessionData("S_ANI_MATCH", "TRUE");
			for(int i = 0; i < policiesArr.size(); i++) {
				JSONObject policydata = (JSONObject) policiesArr.get(i);
				if (policydata.containsKey("insuredDetails")) {
					JSONObject insuredDetailsObj = (JSONObject) policydata.get("insuredDetails");
					if (policydata.containsKey("postalAddress")) {
						JSONObject postalAddressObj = (JSONObject) insuredDetailsObj.get("postalAddress");
						if(policydata.containsKey("email") && null != postalAddressObj.get("email")) {
							String strEmail = (String) postalAddressObj.get("email");
							if(!strEmail.isEmpty()) {
								data.setSessionData(Constants.S_POLICY_EMAIL, strEmail);
								data.setSessionData(Constants.S_IS_POLICY_EMAIL_AVIALABLE, Constants.STRING_YES);
							}
						}
						
						
					}
				}
			}
		}


	}			
						
	
	
}

