package com.farmers.bc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class OSPM_MN_015_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("OSPM_MN_015_Call", "Return_Value");
			data.addToLog(currElementName, "OSPM_MN_015_Call : Return_Value :: " + strReturnValue);
			data.setSessionData("OSPM_MN_015_VALUE", strReturnValue);

			String regex = "(.)*(\\d)(.)*";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(strReturnValue);
			if (matcher.matches()) {
				data.setSessionData(Constants.S_POLICY_NUM, strReturnValue);
				data.setSessionData(Constants.S_POPUP_TYPE,Constants.S_POPUP_POLICYNUM);
            	data.setSessionData(Constants.S_CALLER_INPUT,strReturnValue);
				data.addToLog(currElementName,
						"OSPM_MN_015 policy number :: " + (String) data.getSessionData(Constants.S_POLICY_NUM));
				strExitState = Constants.SUCCESSFUL;
			} 
			
			else if (strReturnValue.equalsIgnoreCase(Constants.DONTHAVE)) {
				strExitState = Constants.SUCCESSFUL;
			}
			else if (strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = "Unsuccessful";
			} else if (strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NO_MATCH;
			} else if (strReturnValue.equalsIgnoreCase(Constants.REPRESENTATIVE)) {
				strExitState = Constants.SUCCESSFUL;
			}else {
				strExitState = "Unsuccessful";
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in OSPM_MN_015_VALUE :: " + e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName, "OSPM_MN_015_VALUE :: " + strExitState);

//		String menuExsitState = strExitState;
//		if (strExitState.contains(" "))
//			menuExsitState = menuExsitState.replaceAll(" ", "_");
//		if (null != (String) data.getSessionData("OSPM_MN_015_" + menuExsitState)
//				&& !((String) data.getSessionData("OSPM_MN_015_" + menuExsitState)).isEmpty())
//			menuExsitState = (String) data.getSessionData("OSPM_MN_015_" + menuExsitState);
//		data.addToLog(currElementName, "Final Value of Menu Exit State for OSPM_MN_015: " + menuExsitState);
//		data.setSessionData(Constants.S_MENU_SELECTION_KEY,
//				data.getSessionData(Constants.S_BU) + ":OSPM_MN_015:" + menuExsitState);
//		data.addToLog(currElementName,
//				"S_MENU_SELECTION_KEY OSPM_MN_015: " + (String) data.getSessionData("S_MENU_SELCTION_KEY"));
		return strExitState;
	}
}