package com.farmers.host;

import java.util.HashMap;

import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.FarmersAPI.CommercialBizAgent_Get;
import com.farmers.FarmersAPI_NP.CommercialBizAgent_NP_Get;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class CommercialBizAgent extends DecisionElementBase {

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
		if(data.getSessionData(Constants.S_COMMERCIAL_BIZAGENT_URL) != null && data.getSessionData(Constants.S_CONN_TIMEOUT) != null
				&& data.getSessionData(Constants.S_READ_TIMEOUT) != null) {
			String url = (String) data.getSessionData(Constants.S_COMMERCIAL_BIZAGENT_URL);
			if(url.contains(Constants.S_ANI)) url = url.replace(Constants.S_ANI, (String)data.getSessionData(Constants.S_ANI));
			if(url.contains(Constants.S_BU)) url = url.replace(Constants.S_BU, (String)data.getSessionData(Constants.S_BU));
			String tid = (String) data.getSessionData(Constants.S_CALLID);
			int conTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_CONN_TIMEOUT));
			int readTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_READ_TIMEOUT));
			LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
			CommercialBizAgent_Get obj = new CommercialBizAgent_Get();
			//START-UAT ENV CHANGE(PRIYA,SHAIK)
			data.addToLog("API URL: ", url);
			String prefix = "https://api-np-ss.farmersinsurance.com"; 
			String UAT_FLAG="";
	        if(url.startsWith(prefix)) {
	        	UAT_FLAG="YES";
	        }
			CommercialBizAgent_NP_Get objNP=new CommercialBizAgent_NP_Get();
			JSONObject resp=null;
			if("YES".equalsIgnoreCase(UAT_FLAG))
			{
			String Key=Constants.S_COMMERCIAL_BIZAGENT_URL;
					 region= regionDetails.get(Key);
					data.addToLog("Region for UAT endpoint: ", region);
					resp=objNP.start(url, tid, conTimeout, readTimeout, context, region);
			}
			else
			{
				region="PROD";
				resp=obj.start(url, tid, conTimeout, readTimeout, context);
			}
			
			//JSONObject resp = (JSONObject) obj .start(url, tid, conTimeout, readTimeout, context);
			data.addToLog(currElementName, "�PI response  :"+resp);
		   //END-UAT ENV CHANGE(PRIYA,SHAIK)
			data.addToLog(currElementName, "�PI response  :"+resp);
			//Mustan - Alerting Mechanism ** Response Code Capture
			apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
			if(resp != null) {
				if(resp.containsKey(Constants.REQUEST_BODY)) strReqBody = resp.get(Constants.REQUEST_BODY).toString();
				if(resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200 && resp.containsKey(Constants.RESPONSE_BODY)) {
					data.addToLog(currElementName, "Set CommercialBizAgent API Response into session with the key name of "+currElementName+Constants._RESP);
					strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
					data.setSessionData(currElementName+Constants._RESP, resp.get(Constants.RESPONSE_BODY));
					StrExitState = apiResponseManupulation(data, caa, currElementName, strRespBody);
				} else {
					strRespBody = resp.get(Constants.RESPONSE_MSG).toString();
				}
				
			}
		}
	} catch (Exception e) {
		data.addToLog(currElementName,"Exception in CommercialBizAgent API call  :: "+e);
		caa.printStackTrace(e);
	}
	
		try {
			objHostDetails.startHostReport(currElementName,"CommercialBizAgent API ", strReqBody,region, (String) data.getSessionData(Constants.S_COMMERCIAL_BIZAGENT_URL));
			objHostDetails.endHostReport(data,strRespBody , StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for CommercialBizAgent API call  :: "+e);
			caa.printStackTrace(e);
		}
		
		
		
	return StrExitState;
	}
	
	private String apiResponseManupulation(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {
		String StrExitState = Constants.ER;
		try {
			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			JSONObject agentObj = (JSONObject) resp.get("agent");
			if (agentObj.get("agencyName") != null) StrExitState = Constants.SU;
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in apiResponseManupulation method  :: "+e);
			caa.printStackTrace(e);
		}
		return StrExitState;
	}
}
