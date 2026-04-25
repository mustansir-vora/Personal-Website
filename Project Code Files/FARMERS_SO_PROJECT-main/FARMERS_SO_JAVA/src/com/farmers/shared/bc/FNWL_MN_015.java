package com.farmers.shared.bc;

import java.util.regex.Pattern;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class FNWL_MN_015 extends DecisionElementBase {
	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws Exception {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);

		try {
			String returnValue = (String) data.getElementData("FNWL_MN_015_Call", "Return_Value");
			data.addToLog(currElementName, "Menu ID : FNWL_MN_015 :: Menu Value : " + returnValue);
			data.setSessionData(Constants.FNWL_MN_015_VALUE, returnValue);

			if (null != returnValue && Constants.EmptyString != returnValue) {

				if (returnValue.equalsIgnoreCase(Constants.NOMATCH)) {
					strExitState = Constants.NOMATCH;
					caa.createMSPKey_FNWL(caa, data, "FNWL_MN_015", "NOMATCH");
				} else if (returnValue.equalsIgnoreCase(Constants.NOINPUT)) {
					strExitState = Constants.NOINPUT;
					caa.createMSPKey_FNWL(caa, data, "FNWL_MN_015", "NOINPUT");
				} else if (returnValue.equalsIgnoreCase(Constants.DONTHAVE)) {
					strExitState = Constants.DONTHAVE;
					caa.createMSPKey_FNWL(caa, data, "FNWL_MN_015", "DONT_HAVE");
				} else if (returnValue.equalsIgnoreCase(Constants.REPRESENTATIVE)) {
					strExitState = Constants.REPRESENTATIVE;
					caa.createMSPKey_FNWL(caa, data, "FNWL_MN_015", "REPRESENTATIVE");
				} else {
					Boolean isValidInput = checkInput(returnValue, data, caa, currElementName);
					
					if (isValidInput) {
						strExitState = Constants.SU;
						data.setSessionData(Constants.FNWL_USER_ENTERED_ZIP, returnValue);
					}
				}
			}

		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in FNWL_MN_015 :: " + e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"FNWL_MN_015_VALUE :: "+strExitState);
		
		return strExitState;
	}

	
	private Boolean checkInput(String returnValue, DecisionElementData data, CommonAPIAccess caa, String currElementName) {
		Boolean isValid = false;
		
		try {
			Pattern pattern = Pattern.compile("^[0-9]{5}$");
			isValid = pattern.matcher(returnValue).matches();
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in FNWL_MN_012 CheckInput Method :: "+e);
			caa.printStackTrace(e);
		}
		return isValid;
	}
}
