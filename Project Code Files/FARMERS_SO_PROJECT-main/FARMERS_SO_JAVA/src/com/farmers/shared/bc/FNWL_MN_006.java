package com.farmers.shared.bc;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class FNWL_MN_006 extends DecisionElementBase {
	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws Exception {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		
		try {
			
			String returnValue = (String) data.getElementData("FNWL_MN_006_Call","Return_Value");
			data.addToLog(currElementName, "Menu ID : FNWL_MN_006 :: Menu Value : "+returnValue);
			data.setSessionData(Constants.FNWL_MN_006_VALUE, returnValue);
			
			if (null != returnValue && Constants.EmptyString != returnValue) {
				
				if (returnValue.equalsIgnoreCase(Constants.NOMATCH)) {
					strExitState = Constants.NOMATCH;
				}
				else if (returnValue.equalsIgnoreCase(Constants.NOINPUT)) {
					strExitState = Constants.NOINPUT;
				}
				else if (returnValue.equalsIgnoreCase(Constants.STRING_YES)) {
					strExitState = Constants.STRING_YES;
				}
				else if (returnValue.equalsIgnoreCase(Constants.STRING_NO)) {
					strExitState = Constants.STRING_NO;
				}
				else if (returnValue.equalsIgnoreCase(Constants.REPRESENTATIVE)) {
					strExitState = Constants.REPRESENTATIVE;
				}
			}
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in FNWL_MN_006 :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"FNWL_MN_006_VALUE :: "+strExitState);
		
		return strExitState;
	}

}
