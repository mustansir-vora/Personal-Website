package com.farmers.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class ACHA_MN_002_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("ACHA_MN_002_Call", "Return_Value");
			data.addToLog(currElementName, " ACHB_MN_001_VALUE : Return_Value :: " + strReturnValue);


			if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
			} else if (strReturnValue.equalsIgnoreCase(Constants.STRING_YES)) {
				strExitState = Constants.STRING_YES;
			} else if (strReturnValue.equalsIgnoreCase(Constants.STRING_NO)) {
				strExitState = Constants.STRING_NO;
				caa.createMSPKey(caa, data, "ACHA_MN_002", "NO");
			} else {
				strExitState = Constants.ER;
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in ACHA_MN_002_VALUE :: " + e);
			caa.printStackTrace(e);
		}
		//		String menuExsitState = strExitState;
		//		if (strExitState.contains(" "))
		//			menuExsitState = menuExsitState.replaceAll(" ", "_");
		//		if (null != (String) data.getSessionData("ACHA_MN_002_" + menuExsitState)
		//				&& !((String) data.getSessionData("ACHA_MN_002_" + menuExsitState)).isEmpty())
		//			menuExsitState = (String) data.getSessionData("ACHA_MN_002_" + menuExsitState);
		//		data.addToLog(currElementName, "Final Value of Menu Exit State for ACHA_MN_002: " + menuExsitState);
		//		data.setSessionData(Constants.S_MENU_SELECTION_KEY,
		//				data.getSessionData(Constants.S_BU) + ":ACHA_MN_002:" + menuExsitState);

		data.addToLog(currElementName, "ACHA_MN_002_VALUE :: " + strExitState);
		return strExitState;
	}
}