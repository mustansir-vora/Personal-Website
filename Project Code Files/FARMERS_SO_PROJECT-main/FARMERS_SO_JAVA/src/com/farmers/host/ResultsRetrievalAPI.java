package com.farmers.host;

import java.util.HashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

/*KYCMF_HOST_003 : KYCNP_HOST_001 : KYCNP_HOST_002*/
public class ResultsRetrievalAPI extends DecisionElementBase {

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
		if(data.getSessionData(Constants.S_RESULT_RETRIEVAL_URL) != null &&  data.getSessionData(Constants.ICM_TTID) != null
				&& data.getSessionData(Constants.S_CONN_TIMEOUT) != null && data.getSessionData(Constants.S_READ_TIMEOUT) != null) {
			String url = (String) data.getSessionData(Constants.S_RESULT_RETRIEVAL_URL);
			String ttid = (String) data.getSessionData(Constants.ICM_TTID);
			int conTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_CONN_TIMEOUT));
			int readTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_READ_TIMEOUT));
			
			//JSONObject resp = ResultsRetrieval_Post.start(url, ttid, conTimeout, readTimeout);
			JSONObject resp =null;
			data.addToLog(currElementName, "�PI response  :"+resp);
			//Mustan - Alerting Mechanism ** Response Code Capture
			apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
			if(resp != null) {
				if(resp.containsKey(Constants.REQUEST_BODY)) strReqBody = resp.get(Constants.REQUEST_BODY).toString();
				if(resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200 && resp.containsKey(Constants.RESPONSE_BODY)) {
					data.addToLog(currElementName, "Set ResultsRetrievalAPI  Response into session with the key name of "+currElementName+Constants._RESP);
					strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
					data.setSessionData(currElementName+Constants._RESP, resp.get(Constants.RESPONSE_BODY));
					apiResponseManupulation(data, caa, currElementName, strRespBody);
					StrExitState = Constants.SU;
				} else {
					strRespBody = resp.get(Constants.RESPONSE_MSG).toString();
				}
				
			}
		}
	} catch (Exception e) {
		data.addToLog(currElementName,"Exception in ResultsRetrievalAPI call  :: "+e);
		caa.printStackTrace(e);
	}
	
		try {
			objHostDetails.startHostReport(currElementName,"ResultsRetrievalAPI", strReqBody,region, (String) data.getSessionData(Constants.S_RESULT_RETRIEVAL_URL));
			objHostDetails.endHostReport(data,strRespBody , StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for ResultsRetrievalAPI call  :: "+e);
			caa.printStackTrace(e);
		}
		
	return StrExitState;
	}
	
private void apiResponseManupulation(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {
	try {
		JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
		int trustIndicator = (int) ((JSONObject) resp.get("results")).get("trustIndicator");
		data.setSessionData("S_TRUSTINDICATOR", trustIndicator);
	} catch (Exception e) {
		data.addToLog(currElementName,"Exception in apiResponseManupulation method  :: "+e);
		caa.printStackTrace(e);
	}
}

}