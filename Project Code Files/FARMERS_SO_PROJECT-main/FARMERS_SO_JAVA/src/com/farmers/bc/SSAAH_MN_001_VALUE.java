package com.farmers.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class SSAAH_MN_001_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("SSAAH_MN_001_Call", "Return_Value");
			data.addToLog(currElementName, "SSAAH_MN_001_Call : Return_Value :: " + strReturnValue);
			data.setSessionData("SSAAH_MN_001_VALUE", strReturnValue);

			data.setSessionData("S_MENU_SELCTION_KEY",(String) data.getSessionData(Constants.S_BU) + ":SSAAH_MN_001:" + strReturnValue);

			if(null != data.getSessionData("S_SSAAH_MN_001_BRAND")) {
				String brandCheck = (String) data.getSessionData("S_SSAAH_MN_001_BRAND");
				data.addToLog(currElementName, "Brand value from EPC API :: " + brandCheck);
			}else {
				data.addToLog(data.getCurrentElement(), "Brands Label is null");
			}

			if (strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NO_INPUT;
			} else if (strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NO_MATCH;
			} else if (strReturnValue.equalsIgnoreCase(Constants.REPRESENTATIVE)) {
				strExitState = Constants.REPRESENTATIVE;
			} else if (strReturnValue.equalsIgnoreCase(Constants.BILLING)) {
				strExitState = Constants.BILLING;
				data.setSessionData("FM_TRANSFER_ROUTE", "BILLING");data.setSessionData("INTENT", "BILLING AND PAYMENTS");
				data.setSessionData("INTENT", "BILLING AND PAYMENTS");
			} else if (strReturnValue.equalsIgnoreCase(Constants.CLAIMS)) {
				strExitState = Constants.CLAIMS;
			} else {
				strExitState = Constants.ER;
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in SSAAH_MN_001_VALUE :: " + e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName, "SSAAH_MN_001_VALUE :: " + strExitState);

//		String menuExsitState = strExitState;
//		if (strExitState.contains(" "))
//			menuExsitState = menuExsitState.replaceAll(" ", "_");
//		if (null != (String) data.getSessionData("SSAAH_MN_001_" + menuExsitState)
//				&& !((String) data.getSessionData("SSAAH_MN_001_" + menuExsitState)).isEmpty())
//			menuExsitState = (String) data.getSessionData("SSAAH_MN_001_" + menuExsitState);
//		data.addToLog(currElementName, "Final Value of Menu Exit State for SSAAH_MN_001: " + menuExsitState);
//		data.setSessionData(Constants.S_MENU_SELECTION_KEY,
//				data.getSessionData(Constants.S_BU) + ":SSAAH_MN_001:" + menuExsitState);
//		data.addToLog(currElementName,
//				"S_MENU_SELECTION_KEY SSAAH_MN_001: " + (String) data.getSessionData("S_MENU_SELECTION_KEY"));
		return strExitState;
	}
}