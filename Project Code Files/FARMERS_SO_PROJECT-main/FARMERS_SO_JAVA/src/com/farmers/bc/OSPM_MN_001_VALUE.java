package com.farmers.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class OSPM_MN_001_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("OSPM_MN_001_Call", "Return_Value");
			data.addToLog(currElementName, "OSPM_MN_001_Call : Return_Value :: " + strReturnValue);
			data.setSessionData("OSPM_MN_001_VALUE", strReturnValue);
			
			if (strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NO_INPUT;
			} else if (strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NO_MATCH;
				caa.createMSPKey(caa, data, "OSPM_MN_001", "DEFAULT/REPRESENTATIVE");
			} else if (strReturnValue.equalsIgnoreCase(Constants.QUOTES)) {
				strExitState = Constants.QUOTES;
				data.setSessionData(Constants.S_INTENT, strReturnValue);
				caa.createMSPKey(caa, data, "OSPM_MN_001", "QUOTES");
			} else if (strReturnValue.equalsIgnoreCase(Constants.Make_a_Payment)) {
				strExitState = Constants.Make_a_Payment;
				data.setSessionData("FM_TRANSFER_ROUTE", "PAYMENT");
				data.setSessionData("INTENT", "BILLING AND PAYMENTS");
				data.setSessionData(Constants.S_INTENT, strReturnValue);
				caa.createMSPKey(caa, data, "OSPM_MN_001", "MAKE A PAYMENT BILLING");
			} else if (strReturnValue.equalsIgnoreCase(Constants.CLAIMS)) {
				strExitState = Constants.CLAIMS;
				data.setSessionData(Constants.S_INTENT, strReturnValue);
				caa.createMSPKey(caa, data, "OSPM_MN_001", "CLAIMS");
			} else if (strReturnValue.equalsIgnoreCase(Constants.BILLING)) {
				strExitState = Constants.BILLING;
				data.setSessionData(Constants.S_INTENT, strReturnValue);
				data.setSessionData("FM_TRANSFER_ROUTE", "BILLING");
				data.setSessionData("INTENT", "BILLING AND PAYMENTS");
				caa.createMSPKey(caa, data, "OSPM_MN_001", "BILLING");
			} else if (strReturnValue.equalsIgnoreCase(Constants.WEBSITE_HELP)) {
				strExitState = Constants.WEBSITE_HELP;
				data.setSessionData(Constants.S_INTENT, strReturnValue);
				caa.createMSPKey(caa, data, "OSPM_MN_001", "WEBSITE HELP");
			} else if (strReturnValue.equalsIgnoreCase(Constants.CANCELLATIONS)) {
				strExitState = Constants.CANCELLATIONS;
				data.setSessionData(Constants.S_INTENT, strReturnValue);
				data.setSessionData("S_IA_CUSTOMER_CHECK", "Y");
				caa.createMSPKey(caa, data, "OSPM_MN_001", "CANCELLATION");
			} else if (strReturnValue.equalsIgnoreCase(Constants.ID_CARDS)) {
				strExitState = Constants.ID_CARDS;
				data.setSessionData(Constants.S_INTENT, strReturnValue);
				caa.createMSPKey(caa, data, "OSPM_MN_001", "ID CARDS");
			} else if (strReturnValue.equalsIgnoreCase(Constants.REPRESENTATIVE)) {
				strExitState = Constants.REPRESENTATIVE;
				data.setSessionData(Constants.S_INTENT, strReturnValue);
				caa.createMSPKey(caa, data, "OSPM_MN_001", "DEFAULT/REPRESENTATIVE");
			} else if (strReturnValue.equalsIgnoreCase(Constants.POLICY_ASSISTANCE)) {
				strExitState = Constants.POLICY_ASSISTANCE;
				data.setSessionData(Constants.S_INTENT, strReturnValue);
				 data.setSessionData("SHARED_AUTH_RETURN",Constants.STRING_YES);
				 caa.createMSPKey(caa, data, "OSPM_MN_001", "POLICY ASSISTANCE");
			} else {
				strExitState = Constants.ER;
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in OSPM_MN_001_VALUE :: " + e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName, "OSPM_MN_001_VALUE :: " + strExitState);

//		String menuExsitState = strExitState;
//		if (strExitState.contains(" "))
//			menuExsitState = menuExsitState.replaceAll(" ", "_");
//		if (null != (String) data.getSessionData("OSPM_MN_001_" + menuExsitState)
//				&& !((String) data.getSessionData("OSPM_MN_001_" + menuExsitState)).isEmpty())
//			menuExsitState = (String) data.getSessionData("OSPM_MN_001_" + menuExsitState);
//		data.addToLog(currElementName, "Final Value of Menu Exit State for OSPM_MN_001: " + menuExsitState);
//		data.setSessionData(Constants.S_MENU_SELECTION_KEY,
//				data.getSessionData(Constants.S_BU) + ":OSPM_MN_001:" + menuExsitState);
//		data.addToLog(currElementName,
//				"S_MENU_SELECTION_KEY OSPM_MN_001: " + (String) data.getSessionData("S_MENU_SELCTION_KEY"));
		return strExitState;
	}
}