package com.farmers.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class PolicyStatus_Check_BC extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {
			
			String policyStatus=(String) data.getSessionData("S_POLICY_STATUS");
			data.addToLog(currElementName, "Policy Status :"  + policyStatus);
			if (policyStatus.equalsIgnoreCase("ACT")) {
				strExitState = "ACT";
			} else if (policyStatus.equalsIgnoreCase("PEN")) {
				strExitState = "PEN";
			} else if (policyStatus.equalsIgnoreCase("REN")) {
				strExitState = "REN";
			} else if (policyStatus.equalsIgnoreCase("CAN")) {
				caa.createMSPKey(caa, data, "BWBP_MN_002", "OTHER BILLING QUESTIONS_CUST");
				strExitState = "CAN";
			}else {
				strExitState = Constants.ER;
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in Polciy status check :: " + e);
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