package com.farmers.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class OSPM_MN_005_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("OSPM_MN_005_Call", "Return_Value");
			data.addToLog(currElementName, "OSPM_MN_005_Call : Return_Value :: " + strReturnValue);
			data.setSessionData("OSPM_MN_005_VALUE", strReturnValue);

			if (null != strReturnValue) {
				if (strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
					strExitState = Constants.NO_INPUT;
				} else if (strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
					strExitState = Constants.NO_MATCH;
				} else if (strReturnValue.equalsIgnoreCase(Constants.STRING_YES)) {
					strExitState = Constants.STRING_YES;
					caa.createMSPKey(caa, data, "OSPM_MN_005", "POLICY YES");
				} else if (strReturnValue.equalsIgnoreCase(Constants.STRING_NO)) {
					strExitState = Constants.STRING_NO;
					caa.createMSPKey(caa, data, "OSPM_MN_005", "POLICY NO");
				}else if (strReturnValue.equalsIgnoreCase(Constants.REPRESENTATIVE)) {
					strExitState = Constants.REPRESENTATIVE;
					caa.createMSPKey(caa, data, "OSPM_MN_005", "POLICY NO");
				} else {
					strExitState = Constants.ER;
					data.addToLog(currElementName, "OSPM_MN_005 if the value from GDF is ER :: " + strReturnValue);
				}
			} else {
				strExitState = Constants.ER;
				data.addToLog(currElementName, "OSPM_MN_005 if the value from GDF is null :: " + strReturnValue);
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in OSPM_MN_005_VALUE :: " + e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName, "OSPM_MN_005_VALUE :: " + strExitState);

//		String menuExsitState = strExitState;
//		if (strExitState.contains(" "))
//			menuExsitState = menuExsitState.replaceAll(" ", "_");
//		if (null != (String) data.getSessionData("OSPM_MN_005_" + menuExsitState)
//				&& !((String) data.getSessionData("OSPM_MN_005_" + menuExsitState)).isEmpty())
//			menuExsitState = (String) data.getSessionData("OSPM_MN_005_" + menuExsitState);
//		data.addToLog(currElementName, "Final Value of Menu Exit State for OSPM_MN_005: " + menuExsitState);
//		data.setSessionData(Constants.S_MENU_SELECTION_KEY,
//				data.getSessionData(Constants.S_BU) + ":OSPM_MN_005:" + menuExsitState);
//		data.addToLog(currElementName,
//				"S_MENU_SELECTION_KEY OSPM_MN_005: " + (String) data.getSessionData("S_MENU_SELCTION_KEY"));
		return strExitState;
	}
}