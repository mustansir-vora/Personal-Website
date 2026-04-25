package com.farmers.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class TFPM_MN_001_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("TFPM_MN_001_Call", "Return_Value");
			data.addToLog(currElementName, " TFPM_MN_001_VALUE : Return_Value :: " + strReturnValue);
			data.setSessionData("TFPM_MN_001_VALUE", strReturnValue);

			if (strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			} else if (strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
			}else if (strReturnValue.equalsIgnoreCase(Constants.S_MAILINGADDRESS)) {
				strExitState = Constants.S_MAILINGADDRESS;
				caa.createMSPKey(caa, data, "TFPM_MN_001", "MAILING ADDRESS");
			} else if (strReturnValue.equalsIgnoreCase("Bank Account")) {
				strExitState = "Bank Account";
			} else if (strReturnValue.equalsIgnoreCase(Constants.S_CREDITCARD)) {
				strExitState = Constants.S_CREDITCARD;
				caa.createMSPKey(caa, data, "TFPM_MN_001", "REPRESENTATIVE");
			} else if (strReturnValue.equalsIgnoreCase(Constants.S_DEBITCARD)) {
				strExitState = Constants.S_DEBITCARD;
				caa.createMSPKey(caa, data, "TFPM_MN_001", "REPRESENTATIVE");
			} else if (strReturnValue.equalsIgnoreCase(Constants.S_REPRESENTATIVE)) {
				strExitState = Constants.S_REPRESENTATIVE;
				caa.createMSPKey(caa, data, "TFPM_MN_001", "REPRESENTATIVE");
			} else if (strReturnValue.equalsIgnoreCase("Billing Details")) {
				strExitState = "Billing Details";
			}
			else {
				strExitState = Constants.ER;
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in TFPM_MN_001_VALUE :: " + e);
			caa.printStackTrace(e);
		}

		//		String menuExsitState = strExitState;
		//		if (strExitState.contains(" "))
		//			menuExsitState = menuExsitState.replaceAll(" ", "_");
		//		if (null != (String) data.getSessionData("TFPM_MN_001_" + menuExsitState)
		//				&& !((String) data.getSessionData("TFPM_MN_001_" + menuExsitState)).isEmpty())
		//			menuExsitState = (String) data.getSessionData("TFPM_MN_001_" + menuExsitState);
		//		data.addToLog(currElementName, "Final Value of Menu Exit State for TFPM_MN_001: " + menuExsitState);
		//		data.setSessionData(Constants.S_MENU_SELECTION_KEY,
		//				data.getSessionData(Constants.S_BU) + ":TFPM_MN_001:" + menuExsitState);

		data.addToLog(currElementName, "TFPM_MN_001_VALUE :: " + strExitState);
		return strExitState;
	}
}