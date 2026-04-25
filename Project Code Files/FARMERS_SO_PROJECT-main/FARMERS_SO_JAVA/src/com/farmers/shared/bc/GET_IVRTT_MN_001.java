package com.farmers.shared.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class GET_IVRTT_MN_001 extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			String returnValue = (String) data.getElementData("IVRTT_MN_001_Call","Return_Value");
			data.addToLog(currElementName, "Menu ID : IVRTT_MN_001 :: Menu Value : "+returnValue);
			data.setSessionData(Constants.IVRTT_MN_001_VALUE, returnValue);
			if(returnValue != null && !returnValue.equals("")) {
				 if(returnValue.equalsIgnoreCase(Constants.NOINPUT)) strExitState = Constants.NOINPUT;
				 else if(returnValue.equalsIgnoreCase(Constants.NOMATCH)) strExitState = Constants.NOMATCH;
				 else if(returnValue.equalsIgnoreCase(Constants.STRING_YES)) strExitState = Constants.STRING_YES;
				 else if(returnValue.equalsIgnoreCase(Constants.STRING_NO))strExitState = Constants.STRING_NO;
			}
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in GET_IVRTT_MN_001 Value class  :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName," IVRTT_MN_001 :: "+strExitState);
		return strExitState;
	}
	
}

