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
import com.farmers.FarmersAPI.CommercialMDM_Post;
import com.farmers.FarmersAPI_NP.CommercialMDM_NP_Post;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

/*KYCAF_HOST_001*/
public class CommercialMDM extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String StrExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);objHostDetails.setinitalValue();
		String strReqBody = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
		String region=null;
        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
        //END- UAT ENV SETUP CODE (PRIYA, SHAIK)
		try {
		if(data.getSessionData(Constants.S_COMMERCIAL_MDM_URL) != null && data.getSessionData(Constants.S_CONN_TIMEOUT) != null && data.getSessionData(Constants.S_READ_TIMEOUT) != null) {
			String url = (String) data.getSessionData(Constants.S_COMMERCIAL_MDM_URL);
			String callerId = (String) data.getSessionData(Constants.S_CALLID);
			String telephonenumber = (String) data.getSessionData(Constants.S_ANI);
			data.addToLog(currElementName, "Actual telephone number  "+telephonenumber);
			if(telephonenumber.length() > 10 ) telephonenumber = telephonenumber.substring(telephonenumber.length()-10, telephonenumber.length());
			String num1 = telephonenumber.substring(0, 3);
			String num2 = telephonenumber.substring(3, 6);
			String num3 = telephonenumber.substring(6, 10);
			telephonenumber = num1+"-"+num2+"-"+num3;
			//For testing
			//telephonenumber = "775-691-7762";
			data.addToLog(currElementName, "reformed telephone number  "+telephonenumber);
			int conTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_CONN_TIMEOUT));
			int readTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_READ_TIMEOUT));
			LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
			CommercialMDM_Post obj = new CommercialMDM_Post();
			//START-UAT ENV CHANGE(SHAIK,PRIYA)
			data.addToLog("API URL: ", url);
			String prefix = "https://api-np-ss.farmersinsurance.com"; 
			String UAT_FLAG="";
	        if(url.startsWith(prefix)) {
	        	UAT_FLAG="YES";
	        }
			CommercialMDM_NP_Post objNP=new CommercialMDM_NP_Post();
			JSONObject resp=null;
			if("YES".equalsIgnoreCase(UAT_FLAG))
			{
				String Key=Constants.S_COMMERCIAL_MDM_URL;
				 region=regionDetails.get(Key);
				data.addToLog("Region for UAT endpoint: ", region);
				resp=objNP.start(url, callerId, telephonenumber, conTimeout, readTimeout, context, region);
			}
			else
			{
				region="PROD";
				resp=obj.start(url, callerId, telephonenumber, conTimeout, readTimeout, context);
			}
			data.addToLog(currElementName, "CommercialMDM �PI response  :"+resp);
			//Mustan - Alerting Mechanism ** Response Code Capture
			apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
			if(resp != null) {
				if(resp.containsKey(Constants.REQUEST_BODY)) strReqBody = resp.get(Constants.REQUEST_BODY).toString();
				if(resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200 && resp.containsKey(Constants.RESPONSE_BODY)) {
					data.addToLog(currElementName, "Set CommercialMDM API Response into session with the key name of "+currElementName+Constants._RESP);
					strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
					data.setSessionData(currElementName+Constants._RESP, resp.get(Constants.RESPONSE_BODY));
					StrExitState = apiResponseManupulation(data, caa, currElementName, strRespBody);
				} else {
					strRespBody = resp.get(Constants.RESPONSE_MSG).toString();
				}
				
			}
		}
	} catch (Exception e) {
		data.addToLog(currElementName,"Exception in CommercialMDM API call  :: "+e);
		caa.printStackTrace(e);
	}
	
		try {
			objHostDetails.startHostReport(currElementName,"CommercialMDM API", strReqBody,region, (String) data.getSessionData(Constants.S_COMMERCIAL_MDM_URL));
			objHostDetails.endHostReport(data,strRespBody , StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for CommercialMDM API call  :: "+e);
			caa.printStackTrace(e);
		}
		
		
		
	return StrExitState;
	}

	private String apiResponseManupulation(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {
		String StrExitState = Constants.ER;
		try {
			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			JSONArray policiesArr = (JSONArray)resp.get("policies");
			if(null == policiesArr || policiesArr.size() == 0 ) return Constants.ER;
			if(policiesArr.size() == 1) data.setSessionData(Constants.S_IS_SINGLE_POLICIY, Constants.STRING_YES);
			ArrayList<JSONObject> policiesObj = new ArrayList<JSONObject>();
			for(int i=0;i<policiesArr.size();i++) {
				JSONObject policyObj = (JSONObject) policiesArr.get(i);
				policiesObj.add(policyObj);
			}
			data.setSessionData(Constants.S_POLICY_LIST, policiesObj);
			data.addToLog(currElementName, "noofPolicies from MDM API : "+policiesObj.size());
			StrExitState = Constants.SU;
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in apiResponseManupulation method  :: "+e);
			caa.printStackTrace(e);
		}
		
		return StrExitState;
	}
	
	public static void main(String[] args) {
		String telephonenumber = "3412341234567890";
		if(telephonenumber.length() > 10 ) telephonenumber = telephonenumber.substring(telephonenumber.length()-10, telephonenumber.length());
		System.out.println(telephonenumber);
		String num1 = telephonenumber.substring(0, 3);
		String num2 = telephonenumber.substring(3, 6);
		String num3 = telephonenumber.substring(6, 10);
		System.out.println(num1+"-"+num2+"-"+num3);
	}
	
}
