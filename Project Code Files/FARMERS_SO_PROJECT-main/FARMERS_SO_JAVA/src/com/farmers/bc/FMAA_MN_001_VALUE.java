package com.farmers.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class FMAA_MN_001_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("FMAA_MN_001_Call", "Return_Value");
			data.addToLog(currElementName, "FMAA_MN_001_Call : Return_Value :: " + strReturnValue);
			data.setSessionData("FMAA_MN_001_VALUE", strReturnValue);

			if (null != strReturnValue) {
				if (strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
					strExitState = Constants.NO_INPUT;
				} else if (strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
					strExitState = Constants.NO_MATCH;
				} else if (strReturnValue.equalsIgnoreCase(Constants.QUOTES)) {
					strExitState = Constants.QUOTES;
					data.setSessionData(Constants.S_INTENT, strReturnValue);
					caa.createMSPKey(caa, data, "FMAA_MN_001", "QUOTES");
				} else if (strReturnValue.equalsIgnoreCase(Constants.Make_a_Payment)) {
					strExitState = Constants.Make_a_Payment;
					data.setSessionData("FM_TRANSFER_ROUTE", "PAYMENT");
					data.setSessionData(Constants.S_INTENT, strReturnValue);
					data.setSessionData("INTENT", "BILLING AND PAYMENTS");
					caa.createMSPKey(caa, data, "FMAA_MN_001", "MAKE A PAYMENT");
				} else if (strReturnValue.equalsIgnoreCase(Constants.CLAIMS)) {
					strExitState = Constants.CLAIMS;
					data.setSessionData(Constants.S_INTENT, strReturnValue);
					caa.createMSPKey(caa, data, "FMAA_MN_001", "CLAIMS");
				} else if (strReturnValue.equalsIgnoreCase(Constants.LIENHOLDER)) {
					strExitState = Constants.LIENHOLDER;
					caa.createMSPKey(caa, data, "FMAA_MN_001", "LIENHOLDER");
					data.setSessionData("FM_TRANSFER_ROUTE", "BILLING");
					data.setSessionData(Constants.S_CALLLER_TYPE,"03");
					data.setSessionData(Constants.S_INTENT, strReturnValue);
				} else if (strReturnValue.equalsIgnoreCase(Constants.REPRENSTATIVE)) {
					strExitState = Constants.REPRENSTATIVE;
					data.setSessionData(Constants.S_INTENT, strReturnValue);
					caa.createMSPKey(caa, data, "FMAA_MN_001", "REPRESENTATIVE");
				} else if (strReturnValue.equalsIgnoreCase(Constants.OTHER_BILLING_QUESTIONS)) {
					strExitState = Constants.OTHER_BILLING_QUESTIONS;
					data.setSessionData("FM_TRANSFER_ROUTE", "BILLING");
					data.setSessionData(Constants.S_INTENT, strReturnValue);
					caa.createMSPKey(caa, data, "FMAA_MN_001", "OTHER BILLING QUESTIONS");
				} else if (strReturnValue.equalsIgnoreCase(Constants.EXISTING_POLICY)) {
					strExitState = Constants.EXISTING_POLICY;
					data.setSessionData(Constants.S_INTENT, strReturnValue);
					caa.createMSPKey(caa, data, "FMAA_MN_001", "EXISTING POLICY");
				} else if (strReturnValue.equalsIgnoreCase(Constants.WEBSITE_HELP)) {
					strExitState = Constants.WEBSITE_HELP;
					data.setSessionData(Constants.S_INTENT, strReturnValue);
					caa.createMSPKey(caa, data, "FMAA_MN_001", "WEBSITE HELP");
				} else if (strReturnValue.equalsIgnoreCase(Constants.CANCELLATIONS)) {
					strExitState = Constants.CANCELLATIONS;
					data.setSessionData(Constants.S_INTENT, strReturnValue);
					data.setSessionData("S_IA_CUSTOMER_CHECK", "Y");
					caa.createMSPKey(caa, data, "FMAA_MN_001", "CANCELLATION");
				} else if (strReturnValue.equalsIgnoreCase(Constants.ID_CARDS)) {
					strExitState = Constants.ID_CARDS;
					data.setSessionData(Constants.S_INTENT, strReturnValue);
					caa.createMSPKey(caa, data, "FMAA_MN_001", "ID CARDS");
				}
			} else {
				strExitState = Constants.ER;
				data.addToLog(currElementName, "FMAA_MN_001 if the value from GDF is null :: " + strReturnValue);
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in FMAA_MN_001_VALUE :: " + e);
			caa.printStackTrace(e);
		}

		data.addToLog(currElementName, "FMAA_MN_001_VALUE strExitState:: " + strExitState);

		//		String menuExsitState = strExitState;
		//		if (strExitState.contains(" "))
		//			menuExsitState = menuExsitState.replaceAll(" ", "_");
		//		if (null != (String) data.getSessionData("FMAA_MN_001_" + menuExsitState)
		//				&& !((String) data.getSessionData("FMAA_MN_001_" + menuExsitState)).isEmpty())
		//			menuExsitState = (String) data.getSessionData("FMAA_MN_001_" + menuExsitState);
		//		data.addToLog(currElementName, "Final Value of Menu Exit State for FMAA_MN_001: " + menuExsitState);
		//		data.setSessionData(Constants.S_MENU_SELECTION_KEY,
		//				data.getSessionData(Constants.S_BU) + ":FMAA_MN_001:" + menuExsitState);
		//		data.addToLog(currElementName,
		//				"S_MENU_SELECTION_KEY FMAA_MN_001: " + (String) data.getSessionData("S_MENU_SELCTION_KEY"));
		return strExitState;
	}
}