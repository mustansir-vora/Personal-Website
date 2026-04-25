package com.farmers.host;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.FarmersAPI.PointPolicyInquiry_Get;
import com.farmers.FarmersAPI_NP.PointPolicyInquiry_NP_Get;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;
import com.farmers.report.SetHostDetails;

public class PointPolicyEnquiry_BC extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String StrExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);
		objHostDetails.setinitalValue();
		String strReqBody = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;
		
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		
		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
		String region=null;
        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
        //END- UAT ENV SETUP CODE (PRIYA, SHAIK)
		try {

			LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);

			data.addToLog(data.getCurrentElement(), "URL :: " + data.getSessionData(Constants.S_POINT_POLICYINQUIIRY_URL) + 
					" :: Call ID :: " + data.getSessionData(Constants.S_CALLID));
			String strIsNoinput = (String) data.getSessionData("TFWT_MN_001_VALUE_IS_NOINPUT");
			if(null != strIsNoinput && !Constants.EmptyString.equalsIgnoreCase(strIsNoinput) && "Y".equalsIgnoreCase(strIsNoinput)) {
				data.setSessionData("TFWT_MN_001_VALUE_IS_NOINPUT", "N");
				return Constants.NOINPUT;
			}

			if (data.getSessionData(Constants.S_POINT_POLICYINQUIIRY_URL) != null
					&& data.getSessionData(Constants.S_CALLID) != null
					&& data.getSessionData(Constants.S_CONN_TIMEOUT) != null
					&& data.getSessionData(Constants.S_READ_TIMEOUT) != null) {
//				String url = (String) data.getSessionData(Constants.S_POINT_POLICYINQUIIRY_URL);
				String url = (String)data.getSessionData(Constants.S_POINT_POLICYINQUIIRY_URL);
				url=url.replace("S_POLICY_NUM", (String)data.getSessionData("S_POLICY_NUM"));
				data.addToLog(currElementName, "FINAL url : "+url);
				String tid = (String) data.getSessionData(Constants.S_CALLID);

				String connTimeoutStr = (String) data.getSessionData(Constants.S_CONN_TIMEOUT);
				String readTimeoutStr = (String) data.getSessionData(Constants.S_READ_TIMEOUT);

				PointPolicyInquiry_Get apiObj = new PointPolicyInquiry_Get();
				//Non prod changes-Priya
				data.addToLog("API URL: ", url);
				String prefix = "https://api-np-ss.farmersinsurance.com"; 
				String UAT_FLAG="";
		        if(url.startsWith(prefix)) {
		        	UAT_FLAG="YES";
		        }
				PointPolicyInquiry_NP_Get objNP = new PointPolicyInquiry_NP_Get();
				JSONObject resp=null ;
				if("YES".equalsIgnoreCase(UAT_FLAG)) {
					String Key =Constants.S_POINT_POLICYINQUIIRY_URL;
					 region = regionDetails.get(Key);
					data.addToLog("Region for UAT endpoint: ", region);
					resp = objNP.start(url, tid, Integer.parseInt(connTimeoutStr), Integer.parseInt(readTimeoutStr), context, region);
				}else {
					region="PROD";
				    resp = apiObj.start(url, tid, Integer.parseInt(connTimeoutStr), Integer.parseInt(readTimeoutStr), context);
				}
				//Non prod changes-Priya
				data.addToLog(currElementName, "�PI response  :" + resp);
				
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
				if (resp != null) {
					if(resp.containsKey(Constants.REQUEST_BODY)) strReqBody = resp.get(Constants.REQUEST_BODY).toString();
					if (resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200
							&& resp.containsKey(Constants.RESPONSE_BODY)) {
						data.addToLog(currElementName,
								"Set PointPolicyEnquiry_BC API Response into session with the key name of "
										+ currElementName + Constants._RESP);

						JSONObject strRespBodyTemp = (JSONObject) resp.get(Constants.RESPONSE_BODY);
						strRespBody =  resp.get(Constants.RESPONSE_BODY).toString();
						if (strRespBodyTemp.containsKey("policySummary")) {
							JSONObject policySummary = (JSONObject) strRespBodyTemp.get("policySummary");
							JSONArray addressArray = (JSONArray) policySummary.get("address");
							JSONObject addressObject = (JSONObject) addressArray.get(0);
//							JSONObject driver = ((JSONArray) policySummary.get("autoPolicy")).getJSONObject(0).getJSONArray("drivers").getJSONObject(0);
//				            String firstName = (String) driver.getJSONObject("person").get("firstName");
//				            String middleName = (String) driver.getJSONObject("person").get("middleName");
//				            String lastName = (String) driver.getJSONObject("person").get("lastName");
				            String address = (String) addressObject.get("streetAddress1");
				            String city = (String) addressObject.get("city");
				            String postalCode = (String) addressObject.get("zip");
				            String state = (String) addressObject.get("state");
				            
				            data.setSessionData("S_FIRSTNAME", "firstName");
				            data.setSessionData("S_MIDDLENAME", "middleName");
				            data.setSessionData("S_LASTNAME", "lastName");
				            data.setSessionData("S_ADDRESS", "address");
				            data.setSessionData("S_CITY", "city");
				            data.setSessionData("S_POSTALCODE", "postalCode");
				            data.setSessionData("S_STATE", "state");
				          
							if (policySummary.containsKey("policyNumber")) {
								StrExitState = Constants.SU;
								data.addToLog(currElementName, "Success we got policy ");
							}else {
								data.addToLog(currElementName, "Invalid policyNumber param");
								StrExitState = Constants.NOMATCH;
							}
						}else {
							data.addToLog(currElementName, "Invlaid policySummary param");
							StrExitState = Constants.NOMATCH;
						}
						data.setSessionData(currElementName + Constants._RESP, resp.get(Constants.RESPONSE_BODY));

					} else {
						data.addToLog(data.getCurrentElement(), "Response is in incorrect structure :: "+ resp);
						strRespBody = resp.get(Constants.RESPONSE_MSG).toString();
					}

				}else {
					data.addToLog(data.getCurrentElement(), "Response is null");
				}
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in PointPolicyEnquiry_BC API call  :: " + e);
			caa.printStackTrace(e);
		}

		try {
			objHostDetails.startHostReport(currElementName, "PointPolicyEnquiry_BC", strReqBody, region,(String)data.getSessionData(Constants.S_POINT_POLICYINQUIIRY_URL));
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
