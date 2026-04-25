package com.farmers.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class FMAM_MN_001_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		String strExitStateTemp = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("FMAM_MN_001_Call", "Return_Value");
			data.addToLog(currElementName, " FMAM_MN_001_Call : Return_Value :: " + strReturnValue);

			if (null != strReturnValue) {
				if (strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
					strExitState = Constants.NO_INPUT;
					strExitStateTemp = Constants.NO_INPUT;
					caa.createMSPKey(caa, data, "FMAM_MN_001", "DEFAULT");
				} else if (strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
					strExitState = Constants.NO_MATCH;
					strExitStateTemp = Constants.NO_MATCH;
					caa.createMSPKey(caa, data, "FMAM_MN_001", "DEFAULT");
				} else if (strReturnValue.equalsIgnoreCase(Constants.MOBILE_HOME)) {
					strExitState = Constants.MOBILE_HOME;
					strExitStateTemp = Constants.MOBILE_HOME;
					caa.createMSPKey(caa, data, "FMAM_MN_001", "MOBILE HOME");
				} else if (strReturnValue.equalsIgnoreCase(Constants.MOTORCYCLE)) {
					strExitState = Constants.MOTORCYCLE;
					strExitStateTemp = Constants.MOTORCYCLE;
					caa.createMSPKey(caa, data, "FMAM_MN_001", "MOTORCYCLE");
				} else if (strReturnValue.equalsIgnoreCase(Constants.ALL_OTHER_PRODUCTS)) {
					strExitState = Constants.ALL_OTHER_PRODUCTS;
					strExitStateTemp = Constants.ALL_OTHER_PRODUCTS;
					caa.createMSPKey(caa, data, "FMAM_MN_001", "ALL OTHER PRODUCTS");
				} else if (strReturnValue.equalsIgnoreCase(Constants.REPRENSTATIVE)) {
					strExitState = "Default";
					strExitStateTemp = "Default";
					caa.createMSPKey(caa, data, "FMAM_MN_001", "DEFAULT");
				} else if (strReturnValue.equalsIgnoreCase("Default")) {
					strExitState = "Default";
					strExitStateTemp = "Default";
					caa.createMSPKey(caa, data, "FMAM_MN_001", "DEFAULT");
				} else if (strReturnValue.equalsIgnoreCase("I Dont Know")) {
					strExitState = "I Dont Know";
					strExitStateTemp = "I Dont Know";
					caa.createMSPKey(caa, data, "FMAM_MN_001", "DEFAULT");
				}
			} else {
				strExitState = Constants.ER;
				strExitStateTemp = Constants.ER;
				data.addToLog(currElementName, "FMAM_MN_001 If the Return Value from GDF is null :: " + strExitState);
			}

			data.setSessionData("S_FMAM_MN_001__RETURN_VALUE", strExitStateTemp);
			data.addToLog(currElementName, "S_FMAM_MN_001__RETURN_VALUE :: " + strExitStateTemp);
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in FMAM_MN_001_VALUE :: " + e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName, "FMAM_MN_001_VALUE :: " + strExitState);

		//		String menuExsitState = strExitState;
		//		if (strExitState.contains(" "))
		//			menuExsitState = menuExsitState.replaceAll(" ", "_");
		//		if (null != (String) data.getSessionData("FMAM_MN_001_" + menuExsitState)
		//				&& !((String) data.getSessionData("FMAM_MN_001_" + menuExsitState)).isEmpty())
		//			menuExsitState = (String) data.getSessionData("FMAM_MN_001_" + menuExsitState);
		//		data.addToLog(currElementName, "Final Value of Menu Exit State for FMAM_MN_001: " + menuExsitState);
		//		data.setSessionData(Constants.S_MENU_SELECTION_KEY,
		//				data.getSessionData(Constants.S_BU) + ":FMAM_MN_001:" + menuExsitState);
		//		data.addToLog(currElementName,
		//				"S_MENU_SELECTION_KEY FMAM_MN_001: " + (String) data.getSessionData("S_MENU_SELCTION_KEY"));
		return strExitState;
	}
}