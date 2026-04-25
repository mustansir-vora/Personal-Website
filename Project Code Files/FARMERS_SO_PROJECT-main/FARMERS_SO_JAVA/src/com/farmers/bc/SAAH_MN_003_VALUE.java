package com.farmers.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class SAAH_MN_003_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("SAAH_MN_003_Call", "Return_Value");
			data.addToLog(currElementName, "SAAH_MN_003_Call : Return_Value :: " + strReturnValue);
			data.setSessionData("SAAH_MN_003_VALUE", strReturnValue);

			if (null != strReturnValue) {
				if (strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
					strExitState = Constants.NOINPUT;
				} else if (strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
					strExitState = Constants.NOMATCH;
				} else if (strReturnValue.equalsIgnoreCase(Constants.MAINMENU)) {
					strExitState = Constants.MAINMENU;
				}
			} else {
				strExitState = Constants.ER;
				data.addToLog(currElementName, "SAAH_MN_003 if the value from GDF is null :: " + strReturnValue);
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in SAAH_MN_003_VALUE :: " + e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName, "SAAH_MN_003_VALUE :: " + strExitState);

		String menuExsitState = strExitState;
		if (strExitState.contains(" "))
			menuExsitState = menuExsitState.replaceAll(" ", "_");
		if (null != (String) data.getSessionData("SAAH_MN_003_" + menuExsitState)
				&& !((String) data.getSessionData("SAAH_MN_003_" + menuExsitState)).isEmpty())
			menuExsitState = (String) data.getSessionData("SAAH_MN_003_" + menuExsitState);
		data.addToLog(currElementName, "Final Value of Menu Exit State for SAAH_MN_003: " + menuExsitState);
		data.setSessionData(Constants.S_MENU_SELECTION_KEY,
				data.getSessionData(Constants.S_BU) + ":SAAH_MN_003:" + menuExsitState);
		data.addToLog(currElementName,
				"S_MENU_SELECTION_KEY SAAH_MN_003: " + (String) data.getSessionData("S_MENU_SELCTION_KEY"));
		return strExitState;
	}
}