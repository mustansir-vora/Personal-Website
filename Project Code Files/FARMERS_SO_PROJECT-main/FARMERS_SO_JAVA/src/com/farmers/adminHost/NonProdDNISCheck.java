package com.farmers.adminHost;

import java.util.HashMap;

import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.AdminAPI.GetNonProdDnisDetailsByDnisKey;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class NonProdDNISCheck extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws Exception {
		// TODO Auto-generated method stub
		String StrExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);
		objHostDetails.setinitalValue();
		
		String strReqBody = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		data.addToLog(currElementName, "DNIS CHECK started");
		
		try {
			if(data.getSessionData(Constants.NONPROD_DNISCHECK_URL) == null ) {
				data.addToLog(currElementName, "Non prod DNIS check URL is null");
//				String url ="https://104.156.46.210/IVRAPI_DEV/api/FrmsApiEndpoint/GetFrmsApiEndpointTableByKey";
				String url ="https://104.156.47.196/IVRAPI_UAT/api/FrmsApiEndpoint/GetFrmsApiEndpointTableByKey";
				data.setSessionData(Constants.NONPROD_DNISCHECK_URL, url);				
			}
			if(data.getSessionData(Constants.NONPROD_DNISCHECK_URL) != null && data.getSessionData(Constants.S_CONN_TIMEOUT) != null && data.getSessionData(Constants.S_READ_TIMEOUT) != null) {
				String url = (String)data.getSessionData(Constants.NONPROD_DNISCHECK_URL);
				
				String callerId = (String) data.getSessionData(Constants.S_CALLID);
				data.addToLog(currElementName, "callerID: "+callerId);
				int conTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_CONN_TIMEOUT));
				
				int readTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_READ_TIMEOUT));
				
				String strDnis = (String)data.getSessionData(Constants.S_DNIS);
				data.addToLog(currElementName, "DNIS: "+strDnis);
				LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);

				data.addToLog(currElementName, "strDnis  :"+strDnis+ " :: API url : "+data.getSessionData(Constants.NONPROD_DNISCHECK_URL)+ " :: conTimeout : "+conTimeout);
				GetNonProdDnisDetailsByDnisKey test = new GetNonProdDnisDetailsByDnisKey();
		          
				JSONObject resp =  (JSONObject) test.start(url, callerId, Constants.tenantid, strDnis , conTimeout, readTimeout,context);
				strRespBody = resp.toString();
				data.addToLog(currElementName, "GetNonProdDnisDetailsByDnisKey API response  :"+resp);
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
				if(resp != null) {
					if(resp.containsKey(Constants.REQUEST_BODY)) strReqBody = resp.get(Constants.REQUEST_BODY).toString();
					if(resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200) {
						data.addToLog(currElementName, "Set DNISCHECK_ADM_001  GetNonProdDnisDetailsByDnisKey API Response into session with the key name of "+currElementName+Constants._RESP);
						
						strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
						data.setSessionData(currElementName+Constants._RESP, resp.get(Constants.RESPONSE_BODY));
						StrExitState = apiResponseManupulation(data, caa, currElementName, strRespBody);
					}
				}
			}
		}catch (Exception e) {
			data.addToLog(currElementName,"Exception in GetNonProdDnisDetailsByDnisKey API call  :: "+e);
			caa.printStackTrace(e);
		}
		
		try {
			objHostDetails.startHostReport(currElementName,"GetNonProdDnisDetailsByDnisKey API", strReqBody,"",(String)data.getSessionData(Constants.NONPROD_DNISCHECK_URL));
			objHostDetails.endHostReport(data,strRespBody , StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for GetNonProdDnisDetailsByDnisKey API call  :: "+e);
			caa.printStackTrace(e);
		}
		return StrExitState;
	}

	private String apiResponseManupulation(DecisionElementData data, CommonAPIAccess caa, String currElementName,
			String strRespBody) {
		// TODO Auto-generated method stub
		String strExitState=Constants.ER;
		HashMap<String, String> apiRegion = new HashMap<>();
		
		try {
			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			JSONArray resArr = (JSONArray)resp.get("res");
			
			if(resArr.size()==0) {
				return Constants.ER;
			}
     		data.addToLog("Set IS_UAT_DNIS:  ", (String)data.getSessionData(Constants.IS_UAT_DNIS));
			for(int i=0;i<resArr.size();i++) {
				JSONObject obj = (JSONObject) resArr.get(i);
				String sessionkey =(String) obj.get("servionapiname");
				String url =(String) obj.get("endpoint");
				String region =(String) obj.get("environment");
				apiRegion.put(sessionkey, region);
				
				data.setSessionData(sessionkey, url);
				data.addToLog(sessionkey+",", (String) data.getSessionData(sessionkey));
						
			}
			data.setSessionData(Constants.RegionHM, apiRegion);
			data.addToLog("HashMap", data.getSessionData(Constants.RegionHM).toString());
			strExitState= Constants.SU;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			data.addToLog(currElementName,"Exception in apiResponseManupulation method  :: "+e);
			e.printStackTrace();
		}
		
		return strExitState;
	}

}
