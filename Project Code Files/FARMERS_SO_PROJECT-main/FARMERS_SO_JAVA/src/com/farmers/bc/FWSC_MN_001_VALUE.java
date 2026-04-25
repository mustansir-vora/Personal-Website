package com.farmers.bc;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class FWSC_MN_001_VALUE extends DecisionElementBase{

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws Exception {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		
		try {
			
			String returnValue = (String) data.getElementData("FWSC_MN_001_Call", "Return_Value");
			data.addToLog(currElementName, currElementName + " :: DTMF Menu Return Value :: " +returnValue);
			data.setSessionData("FWSC_MN_001_VALUE", returnValue);
			
			if (returnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
			}
			else if (returnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			}
			else if (returnValue.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_1)) {
				strExitState = Constants.DTMF_KEY_PRESS_1;
				caa.createMSPKey(caa, data, Constants.FWSC_MN_001, Constants.FWSC_COMMISSION);
			}
			else if (returnValue.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_2)) {
				strExitState = Constants.DTMF_KEY_PRESS_2;
				caa.createMSPKey(caa, data, Constants.FWSC_MN_001, Constants.FWSC_INDEPENDENT_AGENT);
			}
			else if (returnValue.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_3)) {
				strExitState = Constants.DTMF_KEY_PRESS_3;
				caa.createMSPKey(caa, data, Constants.FWSC_MN_001, Constants.FWSC_PARTY_EXTENSION);
			}
		}
		catch (Exception e) {
			data.addToLog(currElementName, "Exception in " + currElementName + " Menu Selection Option Mapping :: " + e);
			caa.printStackTrace(e);
		}
		
		return strExitState;
	}

}
