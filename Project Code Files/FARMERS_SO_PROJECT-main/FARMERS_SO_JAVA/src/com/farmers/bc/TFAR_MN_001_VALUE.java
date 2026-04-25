package com.farmers.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class TFAR_MN_001_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("TFAR_MN_001_Call", "Return_Value");
			data.addToLog(currElementName, " TFAR_MN_001_VALUE : Return_Value :: " + strReturnValue);
			data.setSessionData("TFAR_MN_001_VALUE", strReturnValue);

			if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
			} else if (strReturnValue.equalsIgnoreCase(Constants.S_PAYBYPHONE)) {
				strExitState = Constants.S_PAYBYPHONE;
				data.setSessionData(Constants.S_INTENT, strReturnValue);
				caa.createMSPKey(caa, data, "TFAR_MN_001", "PAY BY PHONE");
			} 
			else if (strReturnValue.equalsIgnoreCase(Constants.S_CLAIMS)) {
				strExitState = Constants.S_CLAIMS;
				data.setSessionData(Constants.S_INTENT, strReturnValue);
				caa.createMSPKey(caa, data, "TFAR_MN_001", "CLAIMS");
			}
			else if (strReturnValue.equalsIgnoreCase(Constants.S_POLICYCHANGES)) {
				strExitState = Constants.S_POLICYCHANGES;
				data.setSessionData(Constants.S_INTENT, strReturnValue);
				caa.createMSPKey(caa, data, "TFAR_MN_001", "POLICY CHANGES");
			}
			else if (strReturnValue.equalsIgnoreCase(Constants.S_PAYMENTADDRESS)) {
				strExitState = Constants.S_PAYMENTADDRESS;
				data.setSessionData(Constants.S_INTENT, strReturnValue);
				caa.createMSPKey(caa, data, "TFAR_MN_001", "PAYMENT ADDRESS");
			}
			else if (strReturnValue.equalsIgnoreCase(Constants.S_SOMETHINGELSE)) {
				strExitState = Constants.S_SOMETHINGELSE;
				data.setSessionData(Constants.S_INTENT, strReturnValue);
				caa.createMSPKey(caa, data, "TFAR_MN_001", "SOMETHING ELSE");
			} else {
				strExitState = Constants.ER;
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in TFAR_MN_001_VALUE :: " + e);
			caa.printStackTrace(e);
		}

		//		String menuExsitState = strExitState;
		//		if (strExitState.contains(" "))
		//			menuExsitState = menuExsitState.replaceAll(" ", "_");
		//		if (null != (String) data.getSessionData("TFAR_MN_001_" + menuExsitState)
		//				&& !((String) data.getSessionData("TFAR_MN_001_" + menuExsitState)).isEmpty())
		//			menuExsitState = (String) data.getSessionData("TFAR_MN_001_" + menuExsitState);
		//		data.addToLog(currElementName, "Final Value of Menu Exit State for TFAR_MN_001: " + menuExsitState);
		//		data.setSessionData(Constants.S_MENU_SELECTION_KEY,
		//				data.getSessionData(Constants.S_BU) + ":TFAR_MN_001:" + menuExsitState);
		//		
				data.addToLog(currElementName, "TFAR_MN_001_VALUE :: " + strExitState);
		return strExitState;
	}
}