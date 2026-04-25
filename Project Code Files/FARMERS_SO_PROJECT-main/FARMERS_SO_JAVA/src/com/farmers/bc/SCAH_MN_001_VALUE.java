package com.farmers.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class SCAH_MN_001_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("SCAH_MN_001_Call", "Return_Value");
			data.addToLog(currElementName, "SCAH_MN_001_Call : Return_Value :: " + strReturnValue);
			data.setSessionData("SCAH_MN_001_VALUE", strReturnValue);

			if (strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NO_INPUT;
			} else if (strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NO_MATCH;
			} else if (strReturnValue.equalsIgnoreCase(Constants.Make_a_Payment)) {
				strExitState = Constants.Make_a_Payment;
				data.setSessionData("FM_TRANSFER_ROUTE", "PAYMENT");
				data.setSessionData(Constants.S_INTENT, strReturnValue);
				data.setSessionData("INTENT", "BILLING AND PAYMENTS");
				caa.createMSPKey(caa, data, "SCAH_MN_001", "MAKE A PAYMENT");
			} else if (strReturnValue.equalsIgnoreCase(Constants.BILLING)) {
				strExitState = Constants.BILLING;
				data.setSessionData("FM_TRANSFER_ROUTE", "BILLING");
				data.setSessionData(Constants.S_INTENT, strReturnValue);
				data.setSessionData("INTENT", "BILLING AND PAYMENTS");
				caa.createMSPKey(caa, data, "SCAH_MN_001", "BILLNG");
			} else if (strReturnValue.equalsIgnoreCase(Constants.CLAIMS)) {
				strExitState = Constants.CLAIMS;
				data.setSessionData(Constants.S_INTENT, strReturnValue);
				caa.createMSPKey(caa, data, "SCAH_MN_001", "CLAIMS");
			} else {
				strExitState = Constants.ER;
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in SCAH_MN_001_VALUE :: " + e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName, "SCAH_MN_001_VALUE :: " + strExitState);

		//		String menuExsitState = strExitState;
		//		if (strExitState.contains(" "))
		//			menuExsitState = menuExsitState.replaceAll(" ", "_");
		//		if (null != (String) data.getSessionData("SCAH_MN_001_" + menuExsitState)
		//				&& !((String) data.getSessionData("SCAH_MN_001_" + menuExsitState)).isEmpty())
		//			menuExsitState = (String) data.getSessionData("SCAH_MN_001_" + menuExsitState);
		//		data.addToLog(currElementName, "Final Value of Menu Exit State for SCAH_MN_001: " + menuExsitState);
		//		data.setSessionData(Constants.S_MENU_SELECTION_KEY,
		//				data.getSessionData(Constants.S_BU) + ":SCAH_MN_001:" + menuExsitState);
		//		data.addToLog(currElementName,
		//				"S_MENU_SELECTION_KEY SCAH_MN_001: " + (String) data.getSessionData("S_MENU_SELECTION_KEY"));
		return strExitState;
	}
}