package com.farmers.adminHost;

import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;
import com.farmers.AdminAPI.GetBrandTableByBusinessObjects;

public class GetBrandByBusinessObjects extends DecisionElementBase{
@Override
public String doDecision(String currElementName, DecisionElementData data) throws Exception {
String StrExitState = Constants.ER;
CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
SetHostDetails objHostDetails = new SetHostDetails(caa);objHostDetails.setinitalValue();
String strReqBody = Constants.EmptyString;
String strRespBody = Constants.EmptyString;
//Mustan - Alerting Mechanism ** Response Code Capture
String apiRespCode = Constants.EmptyString;
try {
	data.addToLog(currElementName, "GetBrandTableByBusinessObjects");
	if (data.getSessionData(Constants.S_BRANDSTABLE_URL) != null
			&& data.getSessionData(Constants.S_DNIS) != null && data.getSessionData(Constants.S_CALLID) != null
			&& data.getSessionData(Constants.S_DNISGROUP) != null
			&& data.getSessionData(Constants.S_LOB) != null && data.getSessionData(Constants.S_CATEGORY) != null
			&& data.getSessionData(Constants.S_CONN_TIMEOUT) != null) {
			String url = (String) data.getSessionData(Constants.S_BRANDSTABLE_URL);
			String callId = (String) data.getSessionData(Constants.S_CALLID);
			int conTimeout = (int) data.getSessionData(Constants.S_CONN_TIMEOUT);
			int readTimeout = (int) data.getSessionData(Constants.S_READ_TIMEOUT);
		    LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
		data.addToLog(currElementName, "GetBrandTableByBusinessObjects "+url);
		GetBrandTableByBusinessObjects obj = new GetBrandTableByBusinessObjects();
		JSONObject resp = obj.start(url,callId, Constants.tenantid,Constants.S_DNIS,Constants.S_DNISGROUP,Constants.S_LOB,Constants.S_CATEGORY, conTimeout, readTimeout, context);
		
		data.addToLog(currElementName, "GetBrandTableByBusinessObjects  :"+resp);
		//Mustan - Alerting Mechanism ** Response Code Capture
		apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
		if(resp != null) {
			if(resp.containsKey(Constants.REQUEST_BODY)) strReqBody = resp.get(Constants.REQUEST_BODY).toString();
			if(resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200 && resp.containsKey(Constants.RESPONSE_BODY)) {
				data.addToLog(currElementName, "Set GetBrandTableByBusinessObjects API Response into session with the key name of "+currElementName+Constants._RESP);
				strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
				data.setSessionData(currElementName+Constants._RESP, resp.get(Constants.RESPONSE_BODY));
				StrExitState =apiResponseManupulation(data, caa, currElementName, strRespBody);
			} else {
				strRespBody = resp.get(Constants.RESPONSE_MSG).toString();
			}		
		}
	}	
} catch (Exception e) {
	data.addToLog(currElementName,"Exception in GetBrandTableByBusinessObjects API call  :: "+e);
	caa.printStackTrace(e);
}
try {
	objHostDetails.startHostReport(currElementName,": GetBrandTableByBusinessObjects API", strReqBody,"", (String) data.getSessionData(Constants.S_BRANDSTABLE_URL));
	objHostDetails.endHostReport(data,strRespBody , StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
} catch (Exception e) {
	data.addToLog(currElementName,"Exception while forming host reporting for : GetBrandTableByBusinessObjects API call  :: "+e);
	caa.printStackTrace(e);

}
return StrExitState;
}
private String apiResponseManupulation(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {
String StrExitState = Constants.ER;
try {
	 JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
     JSONObject responseBody = (JSONObject) resp.get("responseBody");
     JSONArray resArray = (JSONArray) responseBody.get("res");
     if(resArray== null ||resArray.size() == 0 ) return  Constants.ER;
     JSONObject firstObject = (JSONObject) resArray.get(0);
     String lob = (String) firstObject.get("lob");
     String brandlabel = (String) firstObject.get("brandlabel");
     data.setSessionData("S_BRAND_LABEL_BU", brandlabel);
     data.setSessionData("S_LOB_BU", lob);
	StrExitState=Constants.SU;
}catch (Exception e) {
	data.addToLog(currElementName,"Exception in apiResponseManupulation method  :: "+e);
	caa.printStackTrace(e);
}
return StrExitState;
}		
}