package com.farmers.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class SGETSR_MN_001_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("SGETSR_MN_001_Call","Return_Value");
			data.addToLog(currElementName, " SGETSR_MN_001_Call : Return_Value :: "+strReturnValue);
			data.setSessionData(Constants.SGETSR_MN_001_VALUE, strReturnValue);
			if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
			}else {
				strExitState = Constants.SU;
				data.setSessionData("S_CALLER_INPUT", strReturnValue);
	           	data.setSessionData(Constants.S_POPUP_TYPE,Constants.S_POPUP_VENDOR);
            	data.setSessionData(Constants.S_CALLER_INPUT,strReturnValue);
			}
		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in SGETSR_MN_001_VALUE :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"SGETSR_MN_001_VALUE :: "+strExitState);
		
		String menuExsitState = strExitState;
		if(strExitState.contains(" ")) menuExsitState = menuExsitState.replaceAll(" ", "_");
		if(null != (String)data.getSessionData("SGETSR_MN_001_"+menuExsitState) && !((String)data.getSessionData("SGETSR_MN_001_"+menuExsitState)).isEmpty()) menuExsitState = (String)data.getSessionData("SGETSR_MN_001_"+menuExsitState);
		data.addToLog(currElementName, "Final Value of Menu Exit State for SGETSR_MN_001: "+menuExsitState);
		data.setSessionData(Constants.S_MENU_SELCTION_KEY, data.getSessionData(Constants.S_BU)+":SGETSR_MN_001:"+menuExsitState);
		
		return strExitState;
	}
	
	private boolean isInSequence(String numStr) {
        // Check if the number length is between 5 and 9 digits
        if (numStr.length() < 5 || numStr.length() > 9) {
            return false;
        }
        for (int i = 0; i < numStr.length() - 1; i++) {
            // Check if the current digit is exactly one less than the next digit
            if ((numStr.charAt(i + 1) - numStr.charAt(i)) != 1) {
                return false;
            }
        }
        return true;
    }
}