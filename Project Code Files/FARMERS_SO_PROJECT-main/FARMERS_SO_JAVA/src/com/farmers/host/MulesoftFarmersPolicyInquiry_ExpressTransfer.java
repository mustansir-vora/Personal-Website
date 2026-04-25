package com.farmers.host;

import java.util.HashMap;

import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.FarmersAPI.MulesoftFarmerPolicyInquiry_Post;
import com.farmers.FarmersAPI_NP.MulesoftFarmerPolicyInquiry_NP_Post;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class MulesoftFarmersPolicyInquiry_ExpressTransfer extends DecisionElementBase {

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

			LoggerContext context = (LoggerContext) data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);

			data.addToLog(currElementName, "Value of S_MULESOFT_FARMER_POLICYINQUIRY_URL : "+data.getSessionData(Constants.S_MULESOFT_FARMER_POLICYINQUIRY_URL));
			data.addToLog(currElementName, "Value of S_ANI : "+data.getSessionData(Constants.S_ANI));
			data.addToLog(currElementName, "Value of S_POLICY_NUM : "+data.getSessionData(Constants.S_POLICY_NUM));
			data.addToLog(currElementName, "Value of S_CALLID : "+data.getSessionData(Constants.S_CALLID));
			data.addToLog(currElementName, "Value of S_CONN_TIMEOUT : "+data.getSessionData(Constants.S_CONN_TIMEOUT));
			data.addToLog(currElementName, "Value of S_READ_TIMEOUT : "+data.getSessionData(Constants.S_READ_TIMEOUT));
			data.addToLog(currElementName, "Value of S_BILLING_ACC_NUM : "+data.getSessionData(Constants.S_BILLING_ACC_NUM));
			
			if (data.getSessionData(Constants.S_MULESOFT_FARMER_POLICYINQUIRY_URL) != null
					&& data.getSessionData(Constants.S_ANI) != null && data.getSessionData(Constants.S_CALLID) != null
					&& data.getSessionData(Constants.S_POLICY_NUM) != null
					&& data.getSessionData(Constants.S_CONN_TIMEOUT) != null
					&& data.getSessionData(Constants.S_READ_TIMEOUT) != null
					|| data.getSessionData(Constants.S_BILLING_ACC_NUM) != null) {

				String url = (String) data.getSessionData(Constants.S_MULESOFT_FARMER_POLICYINQUIRY_URL);
				String policycontractnumber = (String) data.getSessionData(Constants.S_POLICY_NUM);// if empty not to
				// send
				String billingaccountnumber = (String) data.getSessionData(Constants.S_BILLING_ACC_NUM);
				String telephonenumber = (String) data.getSessionData(Constants.S_ANI);// if Pl num empty take ANI
				String tid = (String) data.getSessionData(Constants.S_CALLID);// if any one has value send other two as
				// null

				data.addToLog(data.getCurrentElement(), "Policy Number from Session :: " + policycontractnumber + " :: Billing Number :: " + billingaccountnumber + 
						" :: ANI :: " + telephonenumber);
				if(null != policycontractnumber) {
					telephonenumber = null;
					billingaccountnumber = null;
					data.addToLog(data.getCurrentElement(), "Policy Number is there and so sending the Policy Number");
				}else if(null != billingaccountnumber) {
					telephonenumber = null;
					data.addToLog(data.getCurrentElement(), "Policy Number is not there but the billing number is there and so sending the Billing Number");
				}
				else {
					data.addToLog(data.getCurrentElement(), "Policy Number and billing numbers are not there and so sending the ANI");
				}

				String connTimeoutStr = (String) data.getSessionData(Constants.S_CONN_TIMEOUT);
				String readTimeoutStr = (String) data.getSessionData(Constants.S_READ_TIMEOUT);

				MulesoftFarmerPolicyInquiry_Post apiObject = new MulesoftFarmerPolicyInquiry_Post();

				//Non prod changes-Priya
				data.addToLog("API URL: ", url);
				String prefix = "https://api-np-ss.farmersinsurance.com"; 
				String UAT_FLAG="";
		        if(url.startsWith(prefix)) {
		        	UAT_FLAG="YES";
		        }
				MulesoftFarmerPolicyInquiry_NP_Post objNP = new MulesoftFarmerPolicyInquiry_NP_Post();
				JSONObject resp=null ;
				if("YES".equalsIgnoreCase(UAT_FLAG)) {
					String Key =Constants.S_MULESOFT_FARMER_POLICYINQUIRY_URL;
					 region = regionDetails.get(Key);
					data.addToLog("Region for UAT endpoint: ", region);
					resp = objNP.start(url, tid, policycontractnumber, billingaccountnumber,telephonenumber, Integer.parseInt(connTimeoutStr), Integer.parseInt(readTimeoutStr), context, region);
				}else {
					region="PROD";
				    resp = apiObject.start(url, tid, policycontractnumber, billingaccountnumber,telephonenumber, Integer.parseInt(connTimeoutStr), Integer.parseInt(readTimeoutStr), context);
				}
				//Non prod changes-Priya

				data.addToLog(currElementName,
						currElementName + " : MulesoftFarmerPolicyInquiry_Post API response  :" + resp);
				
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
				if (resp != null) {
					if (resp.containsKey(Constants.REQUEST_BODY))
						strReqBody = resp.get(Constants.REQUEST_BODY).toString();
					if (resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200
							&& resp.containsKey(Constants.RESPONSE_BODY)) {
						data.addToLog(currElementName, "Set " + currElementName
								+ " : MulesoftFarmerPolicyInquiry_Post API Response into session with the key name of "
								+ currElementName + Constants._RESP);
						strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
						data.setSessionData(currElementName + Constants._RESP, resp.get(Constants.RESPONSE_BODY));

						JSONObject respObj = (JSONObject) new JSONParser().parse(strRespBody);
						if (respObj.containsKey("policies")) {
							JSONArray agentsArr = (JSONArray) respObj.get("policies");
							if (null != agentsArr && agentsArr.size() > 0) {

								data.setSessionData("S_LOB", ((JSONObject) agentsArr.get(0)).get("policySource"));
								data.addToLog(currElementName,
										"S_LOB:: " + ((JSONObject) agentsArr.get(0)).get("policySource"));

								data.setSessionData("S_MULESOFT_POLICY_FOUND", "TRUE");
								data.addToLog(currElementName, "Value of S_MULESOFT_POLICY_FOUND : "+ (String) data.getSessionData("S_MULESOFT_POLICY_FOUND"));

								data.setSessionData("S_LOB", ((JSONObject) agentsArr.get(0)).get("lineOfBusiness"));
								data.addToLog(currElementName, "Value of S_LOB : "+ (String) data.getSessionData("S_LOB"));
								StrExitState = Constants.SU;

							} else {
								data.setSessionData("S_MULESOFT_POLICY_FOUND", "FALSE");
								data.addToLog(currElementName, "Value of S_MULESOFT_POLICY_FOUND : "+ (String) data.getSessionData("S_MULESOFT_POLICY_FOUND"));
								StrExitState = Constants.SU;
							}
						} else {
							StrExitState = Constants.SU;
						}
					} else {
						strRespBody = resp.get(Constants.RESPONSE_MSG).toString();
					}
				}
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception while forming host details for " + currElementName + " :: " + e);
			caa.printStackTrace(e);
		}
		try {
			objHostDetails.startHostReport(currElementName, currElementName, strReqBody, region,(String) data.getSessionData(Constants.S_MULESOFT_FARMER_POLICYINQUIRY_URL));
			objHostDetails.endHostReport(data, strRespBody,
					StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.SU : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception while forming host reporting for " + currElementName
					+ "  MulesoftFarmerPolicyInquiry_Post API call  :: " + e);
			caa.printStackTrace(e);
		}

		return StrExitState;
	}

}
