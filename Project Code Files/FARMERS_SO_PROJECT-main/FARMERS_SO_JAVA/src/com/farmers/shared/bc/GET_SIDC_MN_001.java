package com.farmers.shared.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class GET_SIDC_MN_001 extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
	
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			
			String returnValue = (String) data.getElementData("SIDC_MN_001_Call","Return_Value");
			data.addToLog("SIDC_MN_001_CHECKS",returnValue);
			data.setSessionData(Constants.SIDC_MN_001_VALUE, returnValue);
			data.setSessionData(Constants.S_CARD_DELIVERY_METHOD, returnValue);
			if(returnValue.equalsIgnoreCase(Constants.NOINPUT)) strExitState = Constants.NOINPUT;
			else if(returnValue.equalsIgnoreCase(Constants.NOMATCH)) strExitState = Constants.NOMATCH;
			else if(returnValue.equalsIgnoreCase(Constants.Email)) {
				strExitState = Constants.Email;
				data.setSessionData(Constants.S_COMMUNICATION_SOURCE, Constants.Email);
			}
			else if(returnValue.equalsIgnoreCase(Constants.Mail)) {
				strExitState = Constants.Mail;
				data.setSessionData(Constants.S_COMMUNICATION_SOURCE, Constants.Mail);
			}
			else if(returnValue.equalsIgnoreCase(Constants.Fax)) {
				strExitState = Constants.Fax;
				data.setSessionData(Constants.S_COMMUNICATION_SOURCE, Constants.Fax);
			}
		} catch (Exception e) 
		{
			data.addToLog(currElementName,"Exception in SIDC_MN_001 :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"SIDC_MN_001 :: "+strExitState);
		return strExitState;
	}
	
}

