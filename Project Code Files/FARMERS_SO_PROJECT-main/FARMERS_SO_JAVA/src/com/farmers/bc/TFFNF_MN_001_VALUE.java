package com.farmers.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;
 
public class TFFNF_MN_001_VALUE extends DecisionElementBase {
 
	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("TFFNF_MN_001_Call", "Return_Value");
			data.addToLog(currElementName, " TFFNF_MN_001_VALUE : Return_Value :: " + strReturnValue);
			data.setSessionData("TFFNF_MN_001_VALUE", strReturnValue);
			

			 if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
					strExitState = Constants.NOINPUT;
				} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
					strExitState = Constants.NOMATCH;
			} else if (strReturnValue.equalsIgnoreCase(Constants.S_SALES)) {
				strExitState = Constants.S_SALES;
				caa.createMSPKey(caa, data, "TFFNF_MN_001", "SALES");
			} else if (strReturnValue.equalsIgnoreCase(Constants.S_SERVICE)) {
				strExitState = Constants.S_SERVICE;
				caa.createMSPKey(caa, data, "TFFNF_MN_001", "SERVICE");
			} else if (strReturnValue.equalsIgnoreCase(Constants.S_CLAIMS)) {
				strExitState = Constants.S_CLAIMS;
				caa.createMSPKey(caa, data, "TFFNF_MN_001", "CLAIMS");
			}  else if (strReturnValue.equalsIgnoreCase(Constants.S_REPRESENTATIVE)) {
				strExitState = Constants.S_REPRESENTATIVE;
				caa.createMSPKey(caa, data, "TFFNF_MN_001", "REPRESENTATIVE");
			}  else if (strReturnValue.equalsIgnoreCase("Extension")) {
				strExitState = Constants.S_EXTENTION;
				caa.createMSPKey(caa, data, "TFFNF_MN_001", "EXTENSION");
			}
			else {
				strExitState = Constants.ER;
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in TFFNF_MN_001_VALUE :: " + e);
			caa.printStackTrace(e);
		}
		
//		String menuExsitState = strExitState;
//		if (strExitState.contains(" "))
//			menuExsitState = menuExsitState.replaceAll(" ", "_");
//		if (null != (String) data.getSessionData("TFFNF_MN_001_" + menuExsitState)
//				&& !((String) data.getSessionData("TFFNF_MN_001_" + menuExsitState)).isEmpty())
//			menuExsitState = (String) data.getSessionData("TFFNF_MN_001_" + menuExsitState);
//		data.addToLog(currElementName, "Final Value of Menu Exit State for TFFNF_MN_001: " + menuExsitState);
//		data.setSessionData(Constants.S_MENU_SELECTION_KEY,
//				data.getSessionData(Constants.S_BU) + ":TFFNF_MN_001:" + menuExsitState);
		
		data.addToLog(currElementName, "TFFNF_MN_001_VALUE :: " + strExitState);
		return strExitState;
	}
}