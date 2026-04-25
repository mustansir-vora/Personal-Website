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

public class NewClaimsLookup extends DecisionElementBase

{

//	public String getElementName() 
//	{
//		return "NewClaimsLookup";
//	}
//	public String getDisplayFolderName() 
//	{
//		return "FARMERSETCallFLOW";
//	}
//	public String getDescription() 
//	{
//		return "Retrieve the NewClaimsLookup webservice response";
//	}

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String StrExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);
		String strReqBody = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;
		// START- UAT ENV SETUP CODE (PRIYA, SHAIK)
		String region = null;
		HashMap<String, String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
		// END- UAT ENV SETUP CODE (PRIYA, SHAIK)
		String apiRespCode = "";
		try {

			if (data.getSessionData(Constants.S_NEWCLAIMS_LOOKUPURL) != null
					&& data.getSessionData(Constants.S_READ_TIMEOUT) != null
					&& data.getSessionData(Constants.S_CONN_TIMEOUT) != null
					&& data.getSessionData(Constants.S_ANI) != null && data.getSessionData(Constants.S_LOB) != null) {
				String wsurl = (String) data.getSessionData(Constants.S_NEWCLAIMS_LOOKUPURL);
				String telephoneNumber = (String) data.getSessionData(Constants.S_ANI);
				String functionName = (String) data.getSessionData(Constants.FunctionName);
				String claimNumber = (String) data.getSessionData("S_claimNumber");
				String policyNumber = (String) data.getSessionData("S_policyNumber");
				String conntimeout = (String) data.getSessionData(Constants.S_CONN_TIMEOUT);
				String readtimeout = (String) data.getSessionData(Constants.S_READ_TIMEOUT);
				String Callid = (String) data.getSessionData(Constants.S_CALLID);
				LoggerContext context = (LoggerContext) data.getApplicationAPI()
						.getApplicationData(Constants.A_CONTEXT);
				data.addToLog("Callid", Callid);
				data.addToLog("readtimeout", readtimeout);
				data.addToLog("conntimeout", conntimeout);
				data.addToLog("ani", telephoneNumber);
				data.addToLog("wsurl", wsurl);
				data.addToLog("functionName", functionName);
				data.addToLog("claimNumber", claimNumber);
				data.addToLog("policyNumber", policyNumber);

				Lookupcall lookups = new Lookupcall();
				// UAT EV CHANGE START
				data.addToLog("API URL: ", wsurl);
				String prefix = "https://api-np-ss.farmersinsurance.com";
				String UAT_FLAG = "";
				if (wsurl.startsWith(prefix)) {
					UAT_FLAG = "YES";
				}
				if ("YES".equalsIgnoreCase(UAT_FLAG)) {
					region = regionDetails.get(Constants.S_NEWCLAIMS_LOOKUPURL);
				} else {
					region = "PROD";
				}
				data.addToLog("Region: ", region);
				org.json.simple.JSONObject responses = lookups.GetClaimsLookup(wsurl, Callid, telephoneNumber,
						functionName, claimNumber, policyNumber, Integer.parseInt(conntimeout),
						Integer.parseInt(readtimeout), context, region, UAT_FLAG);
				data.addToLog("responses", responses.toString());
				apiRespCode = String.valueOf((int) responses.get(Constants.RESPONSE_CODE));

				if (responses != null) {
					if (responses.containsKey(Constants.REQUEST_BODY))
						strReqBody = responses.get(Constants.REQUEST_BODY).toString();
					if (responses.containsKey(Constants.RESPONSE_CODE)
							&& (int) responses.get(Constants.RESPONSE_CODE) == 200
							&& responses.containsKey(Constants.RESPONSE_BODY)) {
						data.addToLog(currElementName, "Set Claims �PI Response into session with the key name of "
								+ currElementName + Constants._RESP);
						strRespBody = responses.get(Constants.RESPONSE_BODY).toString();
						data.setSessionData(currElementName + Constants._RESP, responses.get(Constants.RESPONSE_BODY));
						StrExitState = apiResponseManupulation_ClaimsLookup(data, caa, currElementName, strRespBody);

					} else {
						strRespBody = responses.get(Constants.RESPONSE_MSG).toString();
					}

				}
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in NewClaimsAPI Response  :: " + e);
			caa.printStackTrace(e);
		}
		try {

			String apiUrl = data.getSessionData(Constants.S_NEWCLAIMS_LOOKUPURL).toString().length() > 99
					? (String) data.getSessionData(Constants.S_NEWCLAIMS_LOOKUPURL).toString().substring(0, 99)
					: (String) data.getSessionData(Constants.S_NEWCLAIMS_LOOKUPURL);
			data.addToLog("URL: ", apiUrl);
			objHostDetails.startHostReport(currElementName, "NewClaimsAPI Response", strReqBody, region, apiUrl);
			objHostDetails.endHostReport(data, strRespBody,
					StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode, "");
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception while forming host reporting for NewClaimsLookup call  :: " + e);
			caa.printStackTrace(e);
		}
		return StrExitState;
	}

	private String apiResponseManupulation_ClaimsLookup(DecisionElementData data, CommonAPIAccess caa,
			String currElementName, String strRespBody) throws ParseException, AudiumException

	{
		String strExitState = Constants.ER;
		try {
			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			JSONObject resultsObj = (JSONObject) resp.get("results");
			// JSONArray policiesArr = (JSONArray)resultsObj.get("policies");
			// JSONArray claimsArr = new JSONArray();
			JSONArray claimsArr = (JSONArray) resp.get("claims");
			String claimNumber = "";
			String claimType = null;
			String claimStatus = null;
			String policyNumber = "";
			if (claimsArr != null && claimsArr.size() > 0) {
				// data.setSessionData("S_ANI_MATCH", "TRUE");
				for (int i = 0; i < claimsArr.size(); i++) {
					JSONObject claimsObj = (JSONObject) claimsArr.get(i);
					claimNumber = (String) claimsObj.get("claimNumber");
					claimStatus = (String) claimsObj.get("claimStatus");
					claimType = (String) claimsObj.get("claimNumber");
					policyNumber = (String) claimsObj.get("policyNumber");
					data.addToLog("S_policyNumber", policyNumber);
					data.addToLog("claimType is", claimType);

					if (null != claimStatus && "Open".equalsIgnoreCase(claimStatus)) {
						data.setSessionData("S_ClaimStatus", claimStatus);
						data.addToLog("S_claimStatus", claimStatus);
						return Constants.SU;
					}

				}
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in AccLinkAniLookup method  :: " + e);
			caa.printStackTrace(e);
		}

		return strExitState;

	}

}
