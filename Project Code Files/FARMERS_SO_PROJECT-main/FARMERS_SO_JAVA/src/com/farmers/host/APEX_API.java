package com.farmers.host;

import java.util.HashMap;

import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONObject;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.FarmersAPI.Apex_Post;
import com.farmers.FarmersAPI_NP.Apex_NP_Post;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class APEX_API extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String StrExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);
		String strReqBody = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		
		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
		String region=null;
        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
        //END- UAT ENV SETUP CODE (PRIYA, SHAIK)
		try {
		if(data.getSessionData(Constants.S_APEX_URL) != null && data.getSessionData(Constants.S_CONN_TIMEOUT) != null && data.getSessionData(Constants.S_READ_TIMEOUT) != null) {
			String url = (String) data.getSessionData(Constants.S_APEX_URL);
			String callid=((String)data.getSessionData(Constants.S_CGUID)!=null)?((String)data.getSessionData(Constants.S_CGUID)) : Constants.EmptyString;
			if(url.contains(Constants.S_CALLID)) url = url.replace(Constants.S_CALLID, callid);
			String destinationapp = ((String) data.getSessionData(Constants.S_DESTINATION_APP)!=null)?((String)data.getSessionData(Constants.S_DESTINATION_APP)) : "APEX";
			
			/** Brand **/
			String brand = Constants.EmptyString;
			if (null != data.getSessionData("S_FLAG_FDS_BU") && data.getSessionData("S_FLAG_FDS_BU").toString().equalsIgnoreCase(Constants.STRING_YES)) {
				brand = "FDS";
			}
			else if (null != data.getSessionData("S_FLAG_BW_BU") && data.getSessionData("S_FLAG_BW_BU").toString().equalsIgnoreCase(Constants.STRING_YES)) {
				brand = "BW";
			}
			else if(null != data.getSessionData("S_FLAG_FOREMOST_BU") && data.getSessionData("S_FLAG_FOREMOST_BU").toString().equalsIgnoreCase(Constants.STRING_YES)) {
				brand = "FM";
			}
			else if(null != data.getSessionData("S_FLAG_FWS_BU") && data.getSessionData("S_FLAG_FWS_BU").toString().equalsIgnoreCase(Constants.STRING_YES)) {
				brand = "FWS";
			}
			else if(null != data.getSessionData("S_FLAG_21ST_BU") && data.getSessionData("S_FLAG_21ST_BU").toString().equalsIgnoreCase(Constants.STRING_YES)) {
				brand = "21C";
			}
			
			
			String dnis = ((String) data.getSessionData(Constants.S_DNIS)!=null)?((String)data.getSessionData(Constants.S_DNIS)) : Constants.EmptyString;
			String tollfreenumber = ((String) data.getSessionData(Constants.S_TOLLFREE_NUM)!=null)?((String)data.getSessionData(Constants.S_TOLLFREE_NUM)) : Constants.EmptyString;
			String policynumber = ((String) data.getSessionData(Constants.S_POLICY_NUM)!=null)?((String)data.getSessionData(Constants.S_POLICY_NUM)) : Constants.EmptyString;
			String callerani = ((String) data.getSessionData(Constants.S_ANI)!=null)?((String)data.getSessionData(Constants.S_ANI)) : Constants.EmptyString;
			
			String state = ((String) data.getSessionData(Constants.S_STATE)!=null)?((String)data.getSessionData(Constants.S_STATE)) : Constants.EmptyString;
			boolean validpolicynumber = false;
			if ( null != data.getSessionData("IS_CALLED_SHARED_ID_AUTH") && data.getSessionData("IS_CALLED_SHARED_ID_AUTH").toString().equalsIgnoreCase("TRUE")) {
				validpolicynumber = true;
			}
			else {
				validpolicynumber = false;
			}
			String dnisdescription = ((String) data.getSessionData(Constants.S_DNIS_DESCRIPTION)!=null)?((String)data.getSessionData(Constants.S_DNIS_DESCRIPTION)) : Constants.EmptyString;

			
			
			int conTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_CONN_TIMEOUT));
			int readTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_READ_TIMEOUT));
			LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
			Apex_Post  obj= new Apex_Post(); 
			//Non prod changes-Priya
			data.addToLog("API URL: ", url);
			String prefix = "https://api-np-ss.farmersinsurance.com"; 
			String UAT_FLAG="";
	        if(url.startsWith(prefix)) {
	        	UAT_FLAG="YES";
	        }
			Apex_NP_Post objNP = new Apex_NP_Post();
			JSONObject resp=null ;
			if("YES".equalsIgnoreCase(UAT_FLAG)) {
			String Key =Constants.S_APEX_URL;
			 region = regionDetails.get(Key);
			data.addToLog("Region for UAT endpoint: ", region);
			resp = objNP.start(url, callid, callerani, brand, state, destinationapp, dnis, dnisdescription, policynumber, tollfreenumber, validpolicynumber,  conTimeout, readTimeout,context, region);
			}else {
				region="PROD";
			resp = obj.start(url, callid, callerani, brand, state, destinationapp, dnis, dnisdescription, policynumber, tollfreenumber, validpolicynumber,  conTimeout, readTimeout,context);
			}
			//Non prod changes-Priya
			strRespBody = resp.toString();
			data.addToLog(currElementName, "APEX_API response  :"+resp);
			//Mustan - Alerting Mechanism ** Response Code Capture
			apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
			if(resp != null) {
				strRespBody = resp.toString();
				if(resp.containsKey(Constants.REQUEST_BODY)) strReqBody = resp.get(Constants.REQUEST_BODY).toString();
				if(resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200) {
					StrExitState = Constants.SU;
				}
			}
		}
	} catch (Exception e) {
		data.addToLog(currElementName,"Exception in APEX_API call  :: "+e);
		caa.printStackTrace(e);
	}
	
		try {
			objHostDetails.startHostReport(currElementName,"APEX_API", strReqBody,region,(String) data.getSessionData(Constants.S_APEX_URL));
			objHostDetails.endHostReport(data,strRespBody , StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for APEX_API call  :: "+e);
			caa.printStackTrace(e);
		}
		
	return StrExitState;
	}

}