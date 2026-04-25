package com.farmers.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class BWBM_MN_001_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("BWBM_MN_001_Call", "Return_Value");
			data.addToLog(currElementName, "BWBM_MN_001_Call : Return_Value :: " + strReturnValue);
			data.setSessionData("BWBM_MN_001_VALUE", strReturnValue);

			//			data.setSessionData("S_MENU_SELECTION_KEY",
			//					(String) data.getSessionData(Constants.S_BU) + ":BWBM_MN_001:" + strReturnValue);

			if (strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NO_INPUT;
			} else if (strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NO_MATCH;

			} else if (strReturnValue.equalsIgnoreCase(Constants.CUSTOMER_BILLING)) {
				strExitState = Constants.CUSTOMER_BILLING;
				caa.createMSPKey(caa, data, "BWBM_MN_001", "CUSTOMER BILLING");
			} else if (strReturnValue.equalsIgnoreCase(Constants.POLICY_STATUS)) {
				strExitState = Constants.POLICY_STATUS;
				caa.createMSPKey(caa, data, "BWBM_MN_001", "POLICY STATUS");
			} else if (strReturnValue.equalsIgnoreCase(Constants.REFUNDS)) {
				strExitState = Constants.REFUNDS;
				caa.createMSPKey(caa, data, "BWBM_MN_001", "REFUNDS");
			} else if (strReturnValue.equalsIgnoreCase(Constants.COMMISSIONS)) {
				strExitState = Constants.COMMISSIONS;
				caa.createMSPKey(caa, data, "BWBM_MN_001", "COMMISSIONS");
			} else if (strReturnValue.equalsIgnoreCase(Constants.OTHER_BILLING)) {
				strExitState = Constants.OTHER_BILLING;
				caa.createMSPKey(caa, data, "BWBM_MN_001", "OTHER BILLING");
			} else {
				strExitState = Constants.ER;
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in BWBM_MN_001_VALUE :: " + e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName, "BWBM_MN_001_VALUE :: " + strExitState);

		//		String menuExsitState = strExitState;
		//		if (strExitState.contains(" "))
		//			menuExsitState = menuExsitState.replaceAll(" ", "_");
		//		if (null != (String) data.getSessionData("BWBM_MN_001_" + menuExsitState)
		//				&& !((String) data.getSessionData("BWBM_MN_001_" + menuExsitState)).isEmpty())
		//			menuExsitState = (String) data.getSessionData("BWBM_MN_001_" + menuExsitState);
		//		data.addToLog(currElementName, "Final Value of Menu Exit State for BWBM_MN_001: " + menuExsitState);
		//		data.setSessionData(Constants.S_MENU_SELECTION_KEY,
		//				data.getSessionData(Constants.S_BU) + ":BWBM_MN_001:" + menuExsitState);
		//		data.addToLog(currElementName,
		//				"S_MENU_SELECTION_KEY BWBM_MN_001: " + (String) data.getSessionData("S_MENU_SELECTION_KEY"));
		return strExitState;
	}
}