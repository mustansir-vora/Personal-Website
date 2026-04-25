package com.farmers.bc;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class FWSC_MN_002_VALUE extends DecisionElementBase{
	
	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws Exception {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		
		try {
			
			String returnValue = (String) data.getElementData("FWSCM_MN_002_Call", "Return_Value");
			data.addToLog(currElementName, currElementName + " :: DTMF Menu Return Value :: " +returnValue);
			data.setSessionData("FWSCM_MN_002_VALUE", returnValue);
			
			if (returnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
			}
			else if (returnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			}
			else if (returnValue.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_1)) {
				strExitState = Constants.DTMF_KEY_PRESS_1;
				caa.createMSPKey(caa, data, Constants.FWSCM_MN_002, Constants.FWSCM_BROKER_INQUIRY);
			}
			else if (returnValue.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_2)) {
				strExitState = Constants.DTMF_KEY_PRESS_2;
				caa.createMSPKey(caa, data, Constants.FWSCM_MN_002, Constants.FWSCM_BROKER_ISSUE);
			}
			
		}
		catch (Exception e) {
			data.addToLog(currElementName, "Exception in " + currElementName + " Menu Selection Option Mapping :: " + e);
			caa.printStackTrace(e);
		}
		
		return strExitState;
	}

}
