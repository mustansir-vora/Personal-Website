package com.farmers.bc;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class CCSC_MN_001_VALUE extends DecisionElementBase{

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws Exception {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		
		try {
			
			String strReturnValue = (String) data.getElementData("CCSC_MN_001_Call", "Return_Value");
			data.addToLog(currElementName, "CCSC_MN_001 DTMF Menu Return Value :: " +strReturnValue);
			data.setSessionData("CCSC_MN_001_VALUE", strReturnValue);
			
			if (strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
			}
			else if (strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			}
			else if (strReturnValue.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_1)) {
				strExitState = Constants.DTMF_KEY_PRESS_1;
			}
			else if (strReturnValue.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_2)) {
				strExitState = Constants.DTMF_KEY_PRESS_2;
			}
			else if (strReturnValue.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_3)) {
				strExitState = Constants.DTMF_KEY_PRESS_3;
			}
		}
		catch (Exception e) {
			data.addToLog(currElementName, "Exception in CCSC_MN_001_VALUE :: " + e);
			caa.printStackTrace(e);
		}
		
		return strExitState;
	}

}
