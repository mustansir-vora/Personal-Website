package com.farmers.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class FMOM_MN_003_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		String strExitStateTemp = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("FMOM_MN_003_Call", "Return_Value");
			data.addToLog(currElementName, "FMOM_MN_003_Call : Return_Value :: " + strReturnValue);
			data.setSessionData("FMOM_MN_003_VALUE", strReturnValue);
			
			if (null != strReturnValue) {
				if (strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
					strExitState = Constants.NO_INPUT;
					strExitStateTemp = Constants.NO_INPUT;
					caa.createMSPKey(caa, data, "FMOM_MN_003", "NO");
				} else if (strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
					strExitState = Constants.NO_MATCH;
					strExitStateTemp = Constants.NO_MATCH;
					caa.createMSPKey(caa, data, "FMOM_MN_003", "NO");
				} else if (strReturnValue.equalsIgnoreCase(Constants.STRING_YES)) {
					strExitState = Constants.STRING_YES;
					strExitStateTemp = Constants.STRING_YES;
					caa.createMSPKey(caa, data, "FMOM_MN_003", "YES");
				} else if (strReturnValue.equalsIgnoreCase(Constants.STRING_NO)) {
					strExitState = Constants.STRING_NO;
					strExitStateTemp = Constants.STRING_NO;
					caa.createMSPKey(caa, data, "FMOM_MN_003", "NO");
				}else if (strReturnValue.equalsIgnoreCase(Constants.REPRENSTATIVE)) {
					strExitState = Constants.REPRESENTATIVE;
					strExitStateTemp = Constants.REPRESENTATIVE;
					caa.createMSPKey(caa, data, "FMOM_MN_003", "NO");
				}
			} else {
				strExitState = Constants.ER;
				strExitStateTemp = Constants.ER;
				data.addToLog(currElementName, "FMOM_MN_003 if the value from GDF is null :: " + strReturnValue);
			}

			data.setSessionData("S_FMOM_MN_003_RETURN_VALUE", strExitStateTemp);
			data.addToLog(currElementName, "S_FMOM_MN_003_RETURN_VALUE :: " + strExitStateTemp);
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in FMOM_MN_003_VALUE :: " + e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName, "FMOM_MN_003_VALUE :: " + strExitState);

//		String menuExsitState = strExitState;
//		if (strExitState.contains(" "))
//			menuExsitState = menuExsitState.replaceAll(" ", "_");
//		if (null != (String) data.getSessionData("FMOM_MN_003_" + menuExsitState)
//				&& !((String) data.getSessionData("FMOM_MN_003_" + menuExsitState)).isEmpty())
//			menuExsitState = (String) data.getSessionData("FMOM_MN_003_" + menuExsitState);
//		data.addToLog(currElementName, "Final Value of Menu Exit State for FMOM_MN_003: " + menuExsitState);
//		data.setSessionData(Constants.S_MENU_SELECTION_KEY,
//				data.getSessionData(Constants.S_BU) + ":FMOM_MN_003:" + menuExsitState);
//		data.addToLog(currElementName,
//				"S_MENU_SELECTION_KEY FMOM_MN_003: " + (String) data.getSessionData("S_MENU_SELCTION_KEY"));
		return strExitState;
	}
}