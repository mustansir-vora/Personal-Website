package com.farmers.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class BWBP_MN_011_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("BWBP_MN_011_Call", "Return_Value");
			data.addToLog(currElementName, "BWBP_MN_001_Call : Return_Value :: " + strReturnValue);
			data.setSessionData("BWBP_MN_001_VALUE", strReturnValue);
			String policyStatus=(String) data.getSessionData("S_POLICY_STATUS");
			data.addToLog(currElementName, "Policy Status :"  + policyStatus);
			if (strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			} else if (strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
			} else if (strReturnValue.equalsIgnoreCase(Constants.STRING_YES)) {
				strExitState = Constants.STRING_YES;
			} else if (strReturnValue.equalsIgnoreCase(Constants.STRING_NO)) {
				caa.createMSPKey(caa, data, "BWBP_MN_002", "OTHER BILLING QUESTIONS");
				strExitState = Constants.STRING_NO;
			}else {
				strExitState = Constants.ER;
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in BWBP_MN_001_VALUE :: " + e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName, "BWBP_MN_001_VALUE :: " + strExitState);

//		String menuExsitState = strExitState;
//		if (strExitState.contains(" "))
//			menuExsitState = menuExsitState.replaceAll(" ", "_");
//		if (null != (String) data.getSessionData("BWBP_MN_001_" + menuExsitState)
//				&& !((String) data.getSessionData("BWBP_MN_001_" + menuExsitState)).isEmpty())
//			menuExsitState = (String) data.getSessionData("BWBP_MN_001_" + menuExsitState);
//		data.addToLog(currElementName, "Final Value of Menu Exit State for BWBP_MN_001: " + menuExsitState);
//		data.setSessionData(Constants.S_MENU_SELECTION_KEY,
//				data.getSessionData(Constants.S_BU) + ":BWBP_MN_001:" + menuExsitState);
//		data.addToLog(currElementName,
//				"S_MENU_SELECTION_KEY BWBP_MN_001: " + (String) data.getSessionData("S_MENU_SELCTION_KEY"));
		return strExitState;
	}
}