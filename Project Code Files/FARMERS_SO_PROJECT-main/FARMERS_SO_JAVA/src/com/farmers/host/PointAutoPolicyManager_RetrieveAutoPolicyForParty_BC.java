package com.farmers.host;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.FarmersAPI.PointAutoPolicyManager_RetrieveAutoPolicyForParty;
import com.farmers.FarmersAPI.PointPolicyInquiry_Get;
import com.farmers.FarmersAPI_NP.PointAutoPolicyManager_RetrieveAutoPolicyForParty_NP;
import com.farmers.FarmersAPI_NP.PointPolicyInquiry_NP_Get;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;


public class PointAutoPolicyManager_RetrieveAutoPolicyForParty_BC extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String StrExitState = Constants.ER;

		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);
		objHostDetails.setinitalValue();
		String strReqBody = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;
		String strPolicyFound = "No";
		String url = Constants.EmptyString;
		String tid = Constants.EmptyString;
		String firstname = Constants.EmptyString;
		String lastname = Constants.EmptyString;
		String telephonenumber = Constants.EmptyString;
		String conTimeoutStr = Constants.EmptyString;
		String readTimeoutStr = Constants.EmptyString;
		String userid = Constants.EmptyString;
		String sysname = Constants.EmptyString;
		String policyNumber = Constants.EmptyString;
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		
		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
		String region=null;
        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
        //END- UAT ENV SETUP CODE (PRIYA, SHAIK)
		try {
			LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);

			url = (String) data.getSessionData(Constants.S_POINTAUTOPOLICYMANAGER_URL);
			tid = (String) data.getSessionData(Constants.S_CALLID);
			userid = "138";// need to confirm 138
			sysname = "IVR"; // IVR
			telephonenumber = (String) data.getSessionData(Constants.S_ANI);
			conTimeoutStr = (String) data.getSessionData(Constants.S_CONN_TIMEOUT);
			readTimeoutStr = (String) data.getSessionData(Constants.S_READ_TIMEOUT);

			data.addToLog(currElementName,
					" : Request body forming :: url ::" + url + " : tid ::" + tid + " : userid :: " + userid
					+ " : sysname ::" + sysname + " : firstname :: " + firstname + " : lastname ::"
					+ lastname + " : telephonenumber :: " + telephonenumber + " : conTimeoutStr :: "
					+ conTimeoutStr + " : readTimeoutStr :: " + readTimeoutStr);

			if (data.getSessionData(Constants.S_POINTAUTOPOLICYMANAGER_URL) != null
					&& data.getSessionData(Constants.S_CONN_TIMEOUT) != null
					&& data.getSessionData(Constants.S_READ_TIMEOUT) != null
					&& data.getSessionData(Constants.S_ANI) != null) {

				PointAutoPolicyManager_RetrieveAutoPolicyForParty apiObj = new PointAutoPolicyManager_RetrieveAutoPolicyForParty();
				//START- UAT ENV SETUP CODE (PRIYA, SHAIKH)
				data.addToLog("API URL: ", url);
				String prefix = "https://api-np-ss.farmersinsurance.com"; 
				String UAT_FLAG="";
		        if(url.startsWith(prefix)) {
		        	UAT_FLAG="YES";
		        }
				PointAutoPolicyManager_RetrieveAutoPolicyForParty_NP objNP = new PointAutoPolicyManager_RetrieveAutoPolicyForParty_NP();
				JSONObject resp=null ;
				if("YES".equalsIgnoreCase(UAT_FLAG)) {
					String Key =Constants.S_POINTAUTOPOLICYMANAGER_URL;
					 region = regionDetails.get(Key);
					data.addToLog("Region for UAT endpoint: ", region);
					resp = objNP.start(url, tid, userid, sysname, firstname, lastname, telephonenumber,Integer.parseInt(conTimeoutStr), Integer.parseInt(readTimeoutStr), context, region);
				}else {
					region="PROD";
				    resp = apiObj.start(url, tid, userid, sysname, firstname, lastname, telephonenumber,Integer.parseInt(conTimeoutStr), Integer.parseInt(readTimeoutStr), context);
				}
				//END- UAT ENV SETUP CODE (PRIYA, SHAIK)
				data.addToLog(currElementName, "�PI response  :" + resp);
				
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
				if (null != resp) {

					if (resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200
							&& resp.containsKey(Constants.RESPONSE_BODY)) {

						JSONObject strRespBodyTemp = (JSONObject) resp.get(Constants.RESPONSE_BODY);

						strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
						data.addToLog(currElementName,
								"PointAutoPolicyManager_RetrieveAutoPolicyForParty_BC API  strRespBody" + strRespBody);

						if (null != strRespBodyTemp && !strRespBodyTemp.isEmpty()) {
							if (strRespBodyTemp.containsKey("policySearchResponse")) {
								JSONObject policySearchResponse = (JSONObject) strRespBodyTemp
										.get("policySearchResponse");
								if (policySearchResponse.containsKey("policies")) {
									JSONArray policies = (JSONArray) policySearchResponse.get("policies");
									
									JSONObject policy = (JSONObject) policies.get(0);

									if(policy.containsKey("policyNumber")) {
										policyNumber = (String) policy.get("policyNumber");
									}else {
										data.addToLog(data.getCurrentElement(), "PolicyNumber tag is not avaliable in the response ");
									}
									data.addToLog(data.getCurrentElement(), "Policy Number from host :: " +policyNumber);
									if ((!Constants.EmptyString.equalsIgnoreCase(policyNumber)) && policies.size()>0) {
										StrExitState = checkpolicystatus(policyNumber, data);
									}else {
										data.addToLog(currElementName,"Length of poilicies is not greater than 0 :: " + policies.size());
									}
								}else {
									data.addToLog(currElementName,"Response doesn't has POLICES :: " + policySearchResponse);
								}
							}else {
								data.addToLog(currElementName, "Response doesn't has policySearchResponse :: " + strRespBodyTemp);
							}
						}else {
							data.addToLog(currElementName, "Response is null :: " + strRespBodyTemp);
						}
					} else {
						strRespBody = resp.get(Constants.RESPONSE_MSG).toString();
						data.setSessionData(currElementName,
								"PointAutoPolicyManager_RetrieveAutoPolicyForParty_BC API strRespBody" + strRespBody);
					}
				}else {
					data.addToLog(currElementName, "Null response from API");
				}
			}
		} catch (Exception e) {
			data.addToLog(currElementName,
					"Exception in PointAutoPolicyManager_RetrieveAutoPolicyForParty_BC API call  :: " + e);
			caa.printStackTrace(e);
		}

		try {
			objHostDetails.startHostReport(currElementName, "PointAutoPolicyManager_RetrieveAutoPolicyForParty_BC",
					strReqBody,region, (String) data.getSessionData(Constants.S_POINTAUTOPOLICYMANAGER_URL));
			objHostDetails.endHostReport(data, strRespBody,
					StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName,
					"Exception while forming host reporting for PointAutoPolicyManager_RetrieveAutoPolicyForParty_BC API call  :: "
							+ e);
			caa.printStackTrace(e);
		}

		return StrExitState;
	}




	public String checkpolicystatus(String policyNumber, DecisionElementData data) {

		String StrExitState = "Cancelled_policy_not_found";
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);
		objHostDetails.setinitalValue();
		String strReqBody = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;
		String currElementName = data.getCurrentElement();
		
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;

		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
				String region=null;
		        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
		        //END- UAT ENV SETUP CODE (PRIYA, SHAIK)
		try {

			LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);

			data.addToLog(data.getCurrentElement(),"URL :: " + data.getSessionData(Constants.S_POINT_POLICYINQUIIRY_URL) + " :: Call ID :: "+ data.getSessionData(Constants.S_CALLID));

			if (data.getSessionData(Constants.S_POINT_POLICYINQUIIRY_URL) != null
					&& data.getSessionData(Constants.S_CALLID) != null
					&& data.getSessionData(Constants.S_CONN_TIMEOUT) != null
					&& data.getSessionData(Constants.S_READ_TIMEOUT) != null) {

				String url = (String) data.getSessionData(Constants.S_POINT_POLICYINQUIIRY_URL);
				String tid = (String) data.getSessionData(Constants.S_CALLID);
				
				url = url.replace("S_POLICY_NUM", policyNumber);
				
				data.addToLog(url,"URL for checkpolicystatus");

				String connTimeoutStr = (String) data.getSessionData(Constants.S_CONN_TIMEOUT);
				String readTimeoutStr = (String) data.getSessionData(Constants.S_READ_TIMEOUT);

				PointPolicyInquiry_Get apiObj = new PointPolicyInquiry_Get();
				PointPolicyInquiry_NP_Get apiObjNP = new PointPolicyInquiry_NP_Get();
				JSONObject resp=null;
				//Non prod changes-Priya
				data.addToLog("API URL: ", url);
				String prefix = "https://api-np-ss.farmersinsurance.com"; 
				String UAT_FLAG="";
		        if(url.startsWith(prefix)) {
		        	UAT_FLAG="YES";
		        }
				if("YES".equalsIgnoreCase(UAT_FLAG)) {
					String Key =Constants.S_POINTONETIMEPAYMENT_URL;
					 region = regionDetails.get(Key);
					data.addToLog("Region for UAT endpoint: ", region);
					resp = apiObjNP.start(url, tid,
							Integer.parseInt(connTimeoutStr), Integer.parseInt(readTimeoutStr), context,region);
				}else {
					region="PROD";
				    resp =apiObj.start(url, tid,
							Integer.parseInt(connTimeoutStr), Integer.parseInt(readTimeoutStr), context);
				}
				
				//Non prod changes-Priya
				data.addToLog(currElementName, "�PI response  :" + resp);
				
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));

				if (null!=resp && !resp.isEmpty()) {

					if (resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200
							&& resp.containsKey(Constants.RESPONSE_BODY)) {

						data.addToLog(currElementName,
								"Set PointPolicyEnquiry_BC API Response into session with the key name of "
										+ currElementName + Constants._RESP);

						JSONObject strRespBodyTemp = (JSONObject) resp.get(Constants.RESPONSE_BODY);
						strRespBody = resp.get(Constants.RESPONSE_BODY).toString();

						if (strRespBodyTemp.containsKey("policySummary")) {
							JSONObject policySummary = (JSONObject) strRespBodyTemp.get("policySummary");
							JSONArray addressArray = (JSONArray) policySummary.get("address");
							JSONObject addressObject = (JSONObject) addressArray.get(0);
							JSONObject autoPolicy = (JSONObject) policySummary.get("autoPolicy");
							JSONArray drivers = (JSONArray) autoPolicy.get("drivers");
							JSONObject driversFirstObj = (JSONObject) drivers.get(0);
							JSONObject personObj = (JSONObject) driversFirstObj.get("person");
							String firstName = personObj.get("firstName").toString();
							String middleName = personObj.get("middleName").toString();
							String lastName = personObj.get("lastName").toString();
							String address = (String) addressObject.get("streetAddress1");
							String city = (String) addressObject.get("city");
							String postalCode = (String) addressObject.get("zip");
							String state = (String) addressObject.get("state");
							String policyStatus = (String) policySummary.get("policyStatus");
							String ContinousInsuredIndicator = (String) policySummary.get("ContinousInsuredIndicator");

							data.setSessionData("S_FIRSTNAME", firstName);
							data.setSessionData("S_MIDDLENAME", middleName);
							data.setSessionData("S_LASTNAME", lastName);
							data.setSessionData("S_ADDRESS", address);
							data.setSessionData("S_CITY", city);
							data.setSessionData("S_POSTALCODE", postalCode);
							data.setSessionData("S_STATE", state);

							if (policyStatus.equalsIgnoreCase("CANCELLED POLICY") || policyStatus.equalsIgnoreCase("CANCEL")
									|| policyStatus.equalsIgnoreCase("XLC")) {
								if (ContinousInsuredIndicator.equalsIgnoreCase("CanceledGT30")) {
									StrExitState = "Cancelled_policy_found";
								} else {
									data.addToLog(currElementName,"ContinousInsuredIndicator not equals to CanceledGT30"+ ContinousInsuredIndicator);
								}
							} else {
								data.addToLog(currElementName,"policyStatus not equals to neither CANCELLED POLICY nor CANCEL :: "+ policyStatus);
							}

						} else {
							data.addToLog(currElementName, "Invlaid policySummary param");
						}
					} else {
						data.addToLog(data.getCurrentElement(), "Response is in incorrect structure :: " + resp);
						strRespBody = resp.get(Constants.RESPONSE_MSG).toString();
					}

				} else {
					data.addToLog(data.getCurrentElement(), "Response is null");
				}
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in PointPolicyEnquiry_BC API call  :: " + e);
			caa.printStackTrace(e);
		}

		try {
			objHostDetails.startHostReport(currElementName, "PointPolicyEnquiry_BC", strReqBody,region, (String) data.getSessionData(Constants.S_POINT_POLICYINQUIIRY_URL));
			objHostDetails.endHostReport(data, strRespBody,
					StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName,
					"Exception while forming host reporting for PointPolicyEnquiry_BC API call  :: " + e);
			caa.printStackTrace(e);
		}

		return StrExitState;

	}

}
