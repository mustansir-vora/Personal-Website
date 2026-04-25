package com.farmers.pewc;

import java.util.Properties;

import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONObject;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.FarmersAPI.MulesoftCustomerConsentData_Post;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;
import com.farmers.util.GlobalsCommon;

public class Validate_PCDS_HA_001 extends DecisionElementBase{

	@Override
	public String doDecision(String currentElement, DecisionElementData decisionData) throws Exception {
		String strExitState = Constants.FAILURE;
		String strReqBody = Constants.EMPTY_STRING;
		String strRespBody = Constants.EMPTY_STRING;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(decisionData);
		SetHostDetails objHostDetails = new SetHostDetails(caa);
		objHostDetails.setinitalValue();
		try {

			LoggerContext context = (LoggerContext) decisionData.getApplicationAPI().getApplicationData("LOGGER");
			Properties ivrProperties = (Properties) decisionData.getApplicationAPI().getApplicationData(Constants.A_IVR_PROP);
			if(decisionData.getApplicationAPI().getApplicationData(Constants.A_IVR_PROP) != null && ivrProperties.containsKey(Constants.S_CUST_CONSENT_URL) && ivrProperties.containsKey(Constants.S_DISCLAIME_RMSG)) {
				String url = ivrProperties.getProperty(Constants.S_CUST_CONSENT_URL);
				String msg = ivrProperties.getProperty(Constants.S_DISCLAIME_RMSG);
				String phoneNo = (String) decisionData.getSessionData(Constants.S_MOBILE_NO);


				String selectedMenu = decisionData.getElementData("PCDS_MN_002", "value");
				decisionData.addToLog(decisionData.getCurrentElement(), "Selected Option in PCDS_MN_002 : "+selectedMenu);
				String flagValue = "N";
				if(selectedMenu.equalsIgnoreCase("1")) {
					flagValue = "Y";
				}

				decisionData.addToLog(decisionData.getCurrentElement(), "URL = "+url);	
				decisionData.addToLog(decisionData.getCurrentElement(), "CALL ID = "+(String)decisionData.getSessionData(Constants.S_CALL_ID));	
				decisionData.addToLog(decisionData.getCurrentElement(), "Message = "+msg);				
				decisionData.addToLog(decisionData.getCurrentElement(), "Phone No = "+phoneNo);
				decisionData.addToLog(decisionData.getCurrentElement(), "Flag Value No = "+flagValue);
				MulesoftCustomerConsentData_Post objMulesoftCustomerConsentData_Post = new MulesoftCustomerConsentData_Post();
				JSONObject resp =   objMulesoftCustomerConsentData_Post.start(url, (String)decisionData.getSessionData(Constants.S_CALL_ID), null, "0.0.0.0", "1", "IVR", "IVR", null, null, phoneNo, msg, flagValue, 10000, 10000, context);
				decisionData.addToLog(decisionData.getCurrentElement(), "Resp : "+resp.toString());
				if(resp.containsKey(Constants.REQUEST_BODY)) {
					strReqBody = resp.get(Constants.REQUEST_BODY).toString();
				}
				if(resp != null && resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200 
						&& resp.containsKey(Constants.RESPONSE_BODY) ) {
					strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
					strExitState = Constants.SUCCESS;		
				}
			}

		} catch (Exception e) {
			decisionData.addToLog(decisionData.getCurrentElement(), GlobalsCommon.getExceptionTrace(e));
		}
		
		try {
			decisionData.addToLog(decisionData.getCurrentElement(), "Reporting STARTS for Mulesoft Customer Consent Data Post");
			objHostDetails.startHostReport(decisionData.getCurrentElement(), "PCDS_HOST_001", strReqBody);
			objHostDetails.endHostReport(decisionData, strRespBody, strExitState.equalsIgnoreCase(Constants.FAILURE) ? Constants.ER : "SU");
			decisionData.addToLog(decisionData.getCurrentElement(), "Reporting ENDS for 21st Mulesoft Customer Consent Data Post call LookUp :: " + objHostDetails);
		} catch (Exception e1) {
			decisionData.addToLog(decisionData.getCurrentElement(),
					"Exception while forming host reporting for PCDS_HOST_001  Mulesoft Customer Consent Data Post call  :: " + e1);
			caa.printStackTrace(e1);
		}
		return strExitState;
	}

}
