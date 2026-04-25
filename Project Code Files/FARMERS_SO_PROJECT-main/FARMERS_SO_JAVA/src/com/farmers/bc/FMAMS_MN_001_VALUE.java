package com.farmers.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class FMAMS_MN_001_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {

			String productCode = (String) data.getSessionData("S_PRODUCT_CODE");

			if (productCode != null && !productCode.isEmpty()) {

				if ((productCode.equals("105") || productCode.equals("103") || productCode.equals("107")
						|| productCode.equals("106") || productCode.equals("672") || productCode.equals("444"))) {
					strExitState = Constants.MOBILE_HOME;
				} else if (productCode.equals("381")) {
					strExitState = Constants.SPECIALTY_DWELLING;
				} else {
					strExitState = Constants.ER;
				}
			} else {

				String strReturnValue = (String) data.getElementData("FMAMS_MN_001_Call", "Return_Value");
				data.addToLog(currElementName, "FMAMS_MN_001_Call : Return_Value :: " + strReturnValue);

				if (strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
					strExitState = Constants.NO_INPUT;
				} else if (strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
					strExitState = Constants.NO_MATCH;
				} else if (strReturnValue.equalsIgnoreCase(Constants.MOBILE_HOME)) {
					strExitState = Constants.MOBILE_HOME;
					caa.createMSPKey(caa, data, "FMAMS_MN_001", "MOBILE HOME");
				} else if (strReturnValue.equalsIgnoreCase(Constants.SPECIALTY_DWELLING)) {
					strExitState = Constants.SPECIALTY_DWELLING;
					caa.createMSPKey(caa, data, "FMAMS_MN_001", "SPECIALITY DWELLING");
				} else {
					strExitState = Constants.ER;
				}
			}
		} catch (

		Exception e) {
			data.addToLog(currElementName, "Exception in FMAMS_MN_001_VALUE :: " + e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName, "FMAMS_MN_001_VALUE :: " + strExitState);

//		String menuExsitState = strExitState;
//		if (strExitState.contains(" "))
//			menuExsitState = menuExsitState.replaceAll(" ", "_");
//		if (null != (String) data.getSessionData("FMAMS_MN_001_" + menuExsitState)
//				&& !((String) data.getSessionData("FMAMS_MN_001_" + menuExsitState)).isEmpty())
//			menuExsitState = (String) data.getSessionData("FMAMS_MN_001_" + menuExsitState);
//		data.addToLog(currElementName, "Final Value of Menu Exit State for FMAMS_MN_001: " + menuExsitState);
//		data.setSessionData(Constants.S_MENU_SELECTION_KEY,
//				data.getSessionData(Constants.S_BU) + ":FMAMS_MN_001:" + menuExsitState);
//		data.addToLog(currElementName,
//				"S_MENU_SELECTION_KEY FMAMS_MN_001: " + (String) data.getSessionData("S_MENU_SELCTION_KEY"));
		return strExitState;
	}
}