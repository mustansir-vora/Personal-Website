package com.farmers.shared.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class GET_SIDC_MN_003 extends DecisionElementBase 
{

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("SIDC_MN_003_Call","Return_Value");
			data.addToLog(currElementName, " SIDC_MN_003_Call : Return_Value :: "+strReturnValue);
			data.setSessionData(Constants.SIDC_MN_003_VALUE, strReturnValue);
			data.setSessionData(Constants.S_CARD_DELIVERY_METHOD, strReturnValue);
			if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
			}   else if(strReturnValue.equalsIgnoreCase(Constants.Email)) {
				strExitState = Constants.Email;
				data.setSessionData(Constants.S_COMMUNICATION_SOURCE, Constants.Email);
			} else if(strReturnValue.equalsIgnoreCase(Constants.Mail)) {
				strExitState = Constants.Mail;
				data.setSessionData(Constants.S_COMMUNICATION_SOURCE, Constants.Mail);
			}else {
				strExitState = Constants.ER;
			}
		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in SIDC_MN_003_VALUE :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"SIDC_MN_003_VALUE :: "+strExitState);
		return strExitState;
	}
	
}
