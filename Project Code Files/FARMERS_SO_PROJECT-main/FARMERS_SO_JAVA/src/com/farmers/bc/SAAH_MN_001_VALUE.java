package com.farmers.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class SAAH_MN_001_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("SAAH_MN_001_Call", "Return_Value");
			data.addToLog(currElementName, "SAAH_MN_001_Call : Return_Value :: " + strReturnValue);
			data.setSessionData("SAAH_MN_001_VALUE", strReturnValue);

			if (null != strReturnValue) {
				if (strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
					strExitState = Constants.NO_INPUT;
				} else if (strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
					strExitState = Constants.NO_MATCH;
				} else if (strReturnValue.equalsIgnoreCase(Constants.Make_a_Payment)) {
					strExitState = Constants.Make_a_Payment;
					data.setSessionData("FM_TRANSFER_ROUTE", "PAYMENT");
					data.setSessionData(Constants.S_INTENT, strReturnValue);
					data.setSessionData("INTENT", "BILLING AND PAYMENTS");
					caa.createMSPKey(caa, data, "SAAH_MN_001", "MAKE A PAYMENT");
				} else if (strReturnValue.equalsIgnoreCase(Constants.BILLING_STATUS)) {
					strExitState = Constants.BILLING_STATUS;
					data.setSessionData("FM_TRANSFER_ROUTE", "BILLING");
					data.setSessionData(Constants.S_INTENT, strReturnValue);
					data.setSessionData("INTENT", "BILLING AND PAYMENTS");
					caa.createMSPKey(caa, data, "SAAH_MN_001", "BILLING STATUS");
				} else if (strReturnValue.equalsIgnoreCase(Constants.PAYMENT_ADDRESS)) {
					strExitState = Constants.PAYMENT_ADDRESS;
					data.setSessionData(Constants.S_INTENT, strReturnValue);
					caa.createMSPKey(caa, data, "SAAH_MN_001", "PAYMENT ADDRESS");
				} else if (strReturnValue.equalsIgnoreCase(Constants.CLAIMS)) {
					strExitState = Constants.CLAIMS;
					data.setSessionData(Constants.S_INTENT, strReturnValue);
					caa.createMSPKey(caa, data, "SAAH_MN_001", "CLAIMS");
				}
			} else {
				strExitState = Constants.ER;
				data.addToLog(currElementName, "SAAH_MN_001 if the value from GDF is null :: " + strReturnValue);
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in SAAH_MN_001_VALUE :: " + e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName, "SAAH_MN_001_VALUE :: " + strExitState);

		//		String menuExsitState = strExitState;
		//		if (strExitState.contains(" "))
		//			menuExsitState = menuExsitState.replaceAll(" ", "_");
		//		if (null != (String) data.getSessionData("SAAH_MN_001_" + menuExsitState)
		//				&& !((String) data.getSessionData("SAAH_MN_001_" + menuExsitState)).isEmpty())
		//			menuExsitState = (String) data.getSessionData("SAAH_MN_001_" + menuExsitState);
		//		data.addToLog(currElementName, "Final Value of Menu Exit State for SAAH_MN_001: " + menuExsitState);
		//		data.setSessionData(Constants.S_MENU_SELECTION_KEY,
		//				data.getSessionData(Constants.S_BU) + ":SAAH_MN_001:" + menuExsitState);
		//		data.addToLog(currElementName,
		//				"S_MENU_SELECTION_KEY SAAH_MN_001: " + (String) data.getSessionData("S_MENU_SELCTION_KEY"));
		return strExitState;
	}
}