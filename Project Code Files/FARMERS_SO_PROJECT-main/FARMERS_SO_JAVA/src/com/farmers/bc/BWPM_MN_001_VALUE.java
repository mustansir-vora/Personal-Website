package com.farmers.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class BWPM_MN_001_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("BWPM_MN_001_Call", "Return_Value");
			data.addToLog(currElementName, "BWPM_MN_001_Call : Return_Value :: " + strReturnValue);
			data.setSessionData("BWPM_MN_001_VALUE", strReturnValue);

			if (strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NO_INPUT;
			} else if (strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NO_MATCH;
			} else if (strReturnValue.equalsIgnoreCase(Constants.COMMISSIONS)) {
				strExitState = Constants.COMMISSIONS;
				data.setSessionData(Constants.S_INTENT, strReturnValue);
				caa.createMSPKey(caa, data, "BWPM_MN_001", "COMMISSIONS");
			} else if (strReturnValue.equalsIgnoreCase(Constants.TECHNICAL_SUPPORT)) {
				strExitState = Constants.TECHNICAL_SUPPORT;
				data.setSessionData(Constants.S_INTENT, strReturnValue);
				caa.createMSPKey(caa, data, "BWPM_MN_001", "TECHNICAL SUPPORT");
			} else if (strReturnValue.equalsIgnoreCase(Constants.CLAIMS)) {
				strExitState = Constants.CLAIMS;
				data.setSessionData(Constants.S_INTENT, strReturnValue);
				caa.createMSPKey(caa, data, "BWPM_MN_001", "CLAIMS");
			} else if (strReturnValue.equalsIgnoreCase(Constants.AGENCY_CONTRACT_APPOINTMENT)) {
				strExitState = Constants.AGENCY_CONTRACT_APPOINTMENT;
				data.setSessionData(Constants.S_INTENT, strReturnValue);
				caa.createMSPKey(caa, data, "BWPM_MN_001", "AGENCY CONTRACT APPOINTMENT");
			} else if (strReturnValue.equalsIgnoreCase(Constants.BILLING)) {
				strExitState = Constants.BILLING;
				data.setSessionData(Constants.S_INTENT, strReturnValue);
				data.setSessionData("INTENT", "BILLING AND PAYMENTS");
				caa.createMSPKey(caa, data, "BWPM_MN_001", "BILLING");
			} else if (strReturnValue.equalsIgnoreCase(Constants.POLICY_QUESTIONS)) {
				strExitState = Constants.POLICY_QUESTIONS;
				data.setSessionData(Constants.S_INTENT, strReturnValue);
				caa.createMSPKey(caa, data, "BWPM_MN_001", "POLICY QUESTIONS");
			} else if (strReturnValue.equalsIgnoreCase(Constants.EXTENSION)) {
				strExitState = Constants.EXTENSION;
				data.setSessionData(Constants.S_INTENT, strReturnValue);
				caa.createMSPKey(caa, data, "BWPM_MN_001", "EXTENSION");
			}else if (strReturnValue.equalsIgnoreCase(Constants.REPRESENTATIVE)) {
				strExitState = Constants.NO_MATCH;
				//caa.createMSPKey(caa, data, "BWPM_MN_001", "EXTENSION");
			} 
			else {
				strExitState = Constants.ER;
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in BWPM_MN_001_VALUE :: " + e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName, "BWPM_MN_001_VALUE :: " + strExitState);

		//		String menuExsitState = strExitState;
		//		if (strExitState.contains(" "))
		//			menuExsitState = menuExsitState.replaceAll(" ", "_");
		//		if (null != (String) data.getSessionData("BWPM_MN_001_" + menuExsitState)
		//				&& !((String) data.getSessionData("BWPM_MN_001_" + menuExsitState)).isEmpty())
		//			menuExsitState = (String) data.getSessionData("BWPM_MN_001_" + menuExsitState);
		//		data.addToLog(currElementName, "Final Value of Menu Exit State for BWPM_MN_001: " + menuExsitState);
		//		data.setSessionData(Constants.S_MENU_SELECTION_KEY,
		//				data.getSessionData(Constants.S_BU) + ":BWPM_MN_001:" + menuExsitState);
		//		data.addToLog(currElementName,
		//				"S_MENU_SELECTION_KEY BWPM_MN_001: " + (String) data.getSessionData("S_MENU_SELECTION_KEY"));
		return strExitState;
	}
}