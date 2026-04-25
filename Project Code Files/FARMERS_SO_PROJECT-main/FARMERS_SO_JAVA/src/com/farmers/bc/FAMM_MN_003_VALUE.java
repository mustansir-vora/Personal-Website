package com.farmers.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class FAMM_MN_003_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("FAMM_MN_003_Call", "Return_Value");
			data.addToLog(currElementName, " FAMM_MN_003_VALUE : Return_Value :: " + strReturnValue);
			data.setSessionData("FAMM_MN_003_VALUE", strReturnValue);
				
			if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
				caa.createMSPKey(caa, data, "FAMM_MN_003", "REPRESENTATIVE");
			} else if (strReturnValue.equalsIgnoreCase(Constants.S_MOBILE_HOME)) {
				strExitState = Constants.S_MOBILE_HOME;
				caa.createMSPKey(caa, data, "FAMM_MN_003", "MOBILE HOME");
			} else if (strReturnValue.equalsIgnoreCase(Constants.S_SPECIALTY_DWELLING)) {
				strExitState = Constants.S_SPECIALTY_DWELLING;
				caa.createMSPKey(caa, data, "FAMM_MN_003", "SPECIALTY DWELLING");
			}else if (strReturnValue.equalsIgnoreCase(Constants.REPRENSTATIVE)) {
				strExitState = Constants.REPRENSTATIVE;
				caa.createMSPKey(caa, data, "FAMM_MN_003", "REPRESENTATIVE");
			}else if (strReturnValue.equalsIgnoreCase(Constants.S_DEFAULT)) {
				strExitState = Constants.S_DEFAULT;
				caa.createMSPKey(caa, data, "FAMM_MN_003", "REPRESENTATIVE");
			}  else {
				strExitState = Constants.ER;
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in FAMM_MN_003_VALUE :: " + e);
			caa.printStackTrace(e);
		}

		//		String menuExsitState = strExitState;
		//		if (strExitState.contains(" "))
		//			menuExsitState = menuExsitState.replaceAll(" ", "_");
		//		if (null != (String) data.getSessionData("FAMM_MN_003_" + menuExsitState)
		//				&& !((String) data.getSessionData("FAMM_MN_003_" + menuExsitState)).isEmpty())
		//			menuExsitState = (String) data.getSessionData("FAMM_MN_003_" + menuExsitState);
		//		data.addToLog(currElementName, "Final Value of Menu Exit State for FAMM_MN_003: " + menuExsitState);
		//		data.setSessionData(Constants.S_MENU_SELECTION_KEY,
		//				data.getSessionData(Constants.S_BU) + ":FAMM_MN_003:" + menuExsitState);

		data.addToLog(currElementName, "FAMM_MN_003_VALUE :: " + strExitState);
		return strExitState;
	}
}