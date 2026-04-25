package com.farmers.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;
 
public class TFSP_MN_001_VALUE extends DecisionElementBase {
 
	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("TFSP_MN_001_Call", "Return_Value");
			data.addToLog(currElementName, " TFSP_MN_001_Value : Return_Value :: " + strReturnValue);
			data.setSessionData("TFSP_MN_001_VALUE", strReturnValue);
			
			 if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
					strExitState = Constants.NOINPUT;
				} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
					strExitState = Constants.NOMATCH;
			} else if (strReturnValue.equalsIgnoreCase(Constants.S_DIFFERENTCARD)) {
				strExitState = Constants.S_DIFFERENTCARD;
			} else if (strReturnValue.equalsIgnoreCase(Constants.S_BANKACCOUNT)) {
				strExitState = Constants.S_BANKACCOUNT;
			} else if (strReturnValue.equalsIgnoreCase(Constants.S_REPRESENTATIVE)) {
				strExitState = Constants.S_REPRESENTATIVE;
				caa.createMSPKey(caa, data, "TFSP_MN_001", "REPRESENTATIVE");
			} else {
				strExitState = Constants.ER;
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in TFSP_MN_001_Value :: " + e);
			caa.printStackTrace(e);
		}
		
//		String menuExsitState = strExitState;
//		if (strExitState.contains(" "))
//			menuExsitState = menuExsitState.replaceAll(" ", "_");
//		if (null != (String) data.getSessionData("TFSP_MN_001_" + menuExsitState)
//				&& !((String) data.getSessionData("TFSP_MN_001_" + menuExsitState)).isEmpty())
//			menuExsitState = (String) data.getSessionData("TFSP_MN_001_" + menuExsitState);
//		data.addToLog(currElementName, "Final Value of Menu Exit State for TFSP_MN_001: " + menuExsitState);
//		data.setSessionData(Constants.S_MENU_SELECTION_KEY,
//				data.getSessionData(Constants.S_BU) + ":TFSP_MN_001:" + menuExsitState);
		
		data.addToLog(currElementName, "TFSP_MN_001_Value :: " + strExitState);
		return strExitState;
	}
}