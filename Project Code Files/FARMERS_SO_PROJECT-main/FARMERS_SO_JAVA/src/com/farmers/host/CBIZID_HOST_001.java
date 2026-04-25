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


public class CBIZID_HOST_001 extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);objHostDetails.setinitalValue();
		String strReqBody = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;
		data.setSessionData("S_IS_ELITE", "NON_ELITE");
		
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		
		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
		String region=null;
        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
        //END- UAT ENV SETUP CODE (PRIYA, SHAIK)
		try {
			if(data.getSessionData(Constants.S_COMMBIZ_AGENTLOOKUP_URL)!=null && data.getSessionData(Constants.S_CONN_TIMEOUT)!=null && data.getSessionData(Constants.S_READ_TIMEOUT)!=null){
				String url = (String) data.getSessionData(Constants.S_COMMBIZ_AGENTLOOKUP_URL);
				data.addToLog(currElementName, "url : "+url);
				String telephonenumber = (String) data.getSessionData(Constants.S_ANI);
				String bu = (String) data.getSessionData(Constants.S_BU);
				//For testing
				//telephonenumber = "3619069400";
				if(url.contains(Constants.S_URL_PHONENUM)) url = url.replace(Constants.S_URL_PHONENUM, telephonenumber);
				if(url.contains(Constants.S_BU)) url = url.replace(Constants.S_BU, bu);
				data.addToLog(currElementName, "url : "+url);
				String callerId = (String) data.getSessionData(Constants.S_CALLID);
				int conTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_CONN_TIMEOUT));
				int readTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_READ_TIMEOUT));
				LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
				CommercialBizAgent_Get obj = new CommercialBizAgent_Get();
				//Non prod changes-Priya
				data.addToLog("API URL: ", url);
				String prefix = "https://api-np-ss.farmersinsurance.com"; 
				String UAT_FLAG="";
		        if(url.startsWith(prefix)) {
		        	UAT_FLAG="YES";
		        }
				CommercialBizAgent_NP_Get objNP = new CommercialBizAgent_NP_Get();
				JSONObject resp=null ;
				if("YES".equalsIgnoreCase(UAT_FLAG)) {
					String Key =Constants.S_COMMERCIAL_BIZAGENT_URL;
					 region = regionDetails.get(Key);
					data.addToLog("Region for UAT endpoint: ", region);
					resp = objNP.start(url, callerId, conTimeout, readTimeout, context, region);
				}else {
					region="PROD";
				    resp = obj.start(url, callerId, conTimeout, readTimeout, context);
				}
				//Non prod changes-Priya
				data.addToLog(currElementName, " API response  :"+resp);	
				
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
				if(resp != null) {
					if(resp.containsKey(Constants.REQUEST_BODY)) strReqBody = resp.get(Constants.REQUEST_BODY).toString();
					if(resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200 && resp.containsKey(Constants.RESPONSE_BODY)) {
						data.addToLog(currElementName, "Set COMMBIZ_AGENTLOOKUP API Response into session with the key name of "+currElementName+Constants._RESP);
						strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
						data.setSessionData(currElementName+Constants._RESP, resp.get(Constants.RESPONSE_BODY));
						strExitState = apiResponseManipulation(data, caa, currElementName, strRespBody);
	
					}	
				}
			}
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in commercialbizagentlookup API call  :: "+e);
			caa.printStackTrace(e);
		}
		try {
			objHostDetails.startHostReport(currElementName,"commercialbizagentlookupAPI", strReqBody, region,(String) data.getSessionData(Constants.S_COMMBIZ_AGENTLOOKUP_URL));
			objHostDetails.endHostReport(data,strRespBody , strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for commercialbizagentlookupAPI call  :: "+e);
			caa.printStackTrace(e);
		}
		
	/*	String hostMSPEndKey = Constants.EmptyString;
		String isElite = (String) data.getSessionData("S_IS_ELITE");
		if(isElite.equalsIgnoreCase(Constants.ELITE)) hostMSPEndKey = Constants.ELITE;
		else hostMSPEndKey = Constants.NON_ELITE;
		data.setSessionData(Constants.S_MENU_SELCTION_KEY, data.getSessionData(Constants.S_BU)+":CBIZID_HOST_001:"+hostMSPEndKey);
		data.addToLog(currElementName,"S_MENU_SELCTION_KEY for  CBIZID_HOST_001 :: "+data.getSessionData(Constants.S_MENU_SELCTION_KEY));
	*/	
		return strExitState;
	}
	
	private String apiResponseManipulation(DecisionElementData data,CommonAPIAccess caa,String currElementName,String strRespBody){
		String strExitState = Constants.ER;
		try {
			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			JSONObject agent=(JSONObject)resp.get("agent");
		    	String segmentDesc = (String)agent.get("segmentDesc");
		    	data.addToLog(currElementName, "Segment Code - "+segmentDesc);
		    	if("Elite".equalsIgnoreCase(segmentDesc)){
		    		data.setSessionData("S_IS_ELITE","ELITE");
		    		data.addToLog(currElementName, "IS_ELITE : "+data.getSessionData("S_IS_ELITE"));
		    		strExitState = Constants.SU;
		    }
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in apiResponseManipulation method  ::"+e);
			caa.printStackTrace(e);
		}
		return strExitState;
	}

}
