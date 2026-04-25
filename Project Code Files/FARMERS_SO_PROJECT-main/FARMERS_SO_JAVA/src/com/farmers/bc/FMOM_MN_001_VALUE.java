package com.farmers.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class FMOM_MN_001_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("FMOM_MN_001_Call", "Return_Value");
			data.addToLog(currElementName, "FMOM_MN_001_Call : Return_Value :: " + strReturnValue);
			data.setSessionData("FMOM_MN_001_VALUE", strReturnValue);

			if (null != strReturnValue) {
				if (strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
					strExitState = Constants.NO_INPUT;
				} else if (strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
					strExitState = Constants.NO_MATCH;
				} else if (strReturnValue.equalsIgnoreCase(Constants.QUOTES)) {
					strExitState = Constants.QUOTES;
					data.setSessionData(Constants.S_INTENT, strReturnValue);
					caa.createMSPKey(caa, data, "FMOM_MN_001", "QUOTES");
				} else if (strReturnValue.equalsIgnoreCase(Constants.Make_a_Payment)) {
					strExitState = Constants.Make_a_Payment;
					data.setSessionData("FM_TRANSFER_ROUTE", "PAYMENT");
					data.setSessionData(Constants.S_INTENT, strReturnValue);
					data.setSessionData("INTENT", "BILLING AND PAYMENTS");
					caa.createMSPKey(caa, data, "FMOM_MN_001", "MAKE A PAYMENT");
				} else if (strReturnValue.equalsIgnoreCase(Constants.BILLING_QUESTIONS)) {
					strExitState = Constants.BILLING_QUESTIONS;
					data.setSessionData("FM_TRANSFER_ROUTE", "BILLING");
					data.setSessionData(Constants.S_INTENT, strReturnValue);
					data.setSessionData("INTENT", "BILLING AND PAYMENTS");
					caa.createMSPKey(caa, data, "FMOM_MN_001", "BILLING QUESTIONS");
				} else if (strReturnValue.equalsIgnoreCase(Constants.POLICY_QUESTIONS)) {
					strExitState = Constants.POLICY_QUESTIONS;
					data.setSessionData(Constants.S_INTENT, strReturnValue);
					caa.createMSPKey(caa, data, "FMOM_MN_001", "POLICY QUESTIONS");
				} else if (strReturnValue.equalsIgnoreCase(Constants.CLAIMS)) {
					strExitState = Constants.CLAIMS;
					data.setSessionData(Constants.S_INTENT, strReturnValue);
					caa.createMSPKey(caa, data, "FMOM_MN_001", "CLAIMS");
				} else if (strReturnValue.equalsIgnoreCase(Constants.LIENHOLDER)) {
					strExitState = Constants.LIENHOLDER;
					data.setSessionData(Constants.S_CALLLER_TYPE,"03");
					data.setSessionData(Constants.S_INTENT, strReturnValue);
					caa.createMSPKey(caa, data, "FMOM_MN_001", "LIENHOLDER");
				} else if (strReturnValue.equalsIgnoreCase(Constants.REPRESENTATIVE)) {
					strExitState = Constants.REPRESENTATIVE;
					data.setSessionData(Constants.S_INTENT, strReturnValue);
					caa.createMSPKey(caa, data, "FMOM_MN_001", "REPRESENTATIVE");
				} else {
					strExitState = Constants.ER;
					data.addToLog(currElementName, "FMOM_MN_001 if the value from GDF is null :: " + strReturnValue);
				}
			} else {
				strExitState = Constants.ER;
				data.addToLog(currElementName, "FMOM_MN_001 if the value from GDF is null :: " + strReturnValue);
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in FMOM_MN_001_VALUE :: " + e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName, "FMOM_MN_001_VALUE :: " + strExitState);

//		String menuExsitState = strExitState;
//		if (strExitState.contains(" "))
//			menuExsitState = menuExsitState.replaceAll(" ", "_");
//		if (null != (String) data.getSessionData("FMOM_MN_001_" + menuExsitState)
//				&& !((String) data.getSessionData("FMOM_MN_001_" + menuExsitState)).isEmpty())
//			menuExsitState = (String) data.getSessionData("FMOM_MN_001_" + menuExsitState);
//		data.addToLog(currElementName, "Final Value of Menu Exit State for FMOM_MN_001: " + menuExsitState);
//		data.setSessionData(Constants.S_MENU_SELECTION_KEY,
//				data.getSessionData(Constants.S_BU) + ":FMOM_MN_001:" + menuExsitState);
//		data.addToLog(currElementName,
//				"S_MENU_SELECTION_KEY FMOM_MN_001: " + (String) data.getSessionData("S_MENU_SELCTION_KEY"));
		return strExitState;
	}
}