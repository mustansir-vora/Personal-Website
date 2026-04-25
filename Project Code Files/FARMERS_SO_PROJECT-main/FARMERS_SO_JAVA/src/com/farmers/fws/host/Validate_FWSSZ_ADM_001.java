package com.farmers.fws.host;

import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.AdminAPI.CheckFWSZipCode;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class Validate_FWSSZ_ADM_001 extends DecisionElementBase{

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws Exception {
		String strExitState = Constants.FWS_ZIP_NO_MATCH;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			
			String url = (String) data.getSessionData("S_FWS_ZIP_CODE_URL");
			String zipCode = (String) data.getSessionData(Constants.S_FWS_ZIP_CODE);
			String callID = (String) data.getSessionData(Constants.S_CALLID);
			int conTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_CONN_TIMEOUT));
			int readTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_READ_TIMEOUT));
			LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
			CheckFWSZipCode objCheckFWSZipCode = new CheckFWSZipCode();
			JSONObject responseJSON =  objCheckFWSZipCode.start(url, callID, Constants.tenantid, zipCode, conTimeout, readTimeout, context);
			data.addToLog(data.getCurrentElement(), "ZIP Code Response : "+responseJSON.toString());
			if(responseJSON != null && responseJSON.get(Constants.RESPONSE_BODY) != null && responseJSON.containsKey(Constants.RESPONSE_CODE)) {
				JSONObject resp = (JSONObject) new JSONParser().parse(responseJSON.get(Constants.RESPONSE_BODY).toString());
				if(resp != null && resp.containsKey("res") && resp.get("res").toString().equalsIgnoreCase("True")) {
					strExitState = Constants.FWS_ZIP_MATCH;
				}
			}
			data.setSessionData(Constants.S_MENU_SELCTION_KEY, "FWS:FWSSZ_MN_001:"+strExitState);
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in Validate_FWSARC_HOST_001 :: "+e);
			caa.printStackTrace(e);
		}
		return strExitState;
	}

}
