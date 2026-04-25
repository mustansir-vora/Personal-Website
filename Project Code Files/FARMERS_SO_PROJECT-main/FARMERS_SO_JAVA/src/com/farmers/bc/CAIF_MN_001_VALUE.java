package com.farmers.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class CAIF_MN_001_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("CAIF_MN_001_Call","Return_Value");
			data.addToLog(currElementName, " CAIF_MN_001_Call : Return_Value :: "+strReturnValue);
			data.setSessionData(Constants.CAIF_MN_001_VALUE, strReturnValue);
			if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
			} else if(strReturnValue.equalsIgnoreCase(Constants.CAIF_MN_001_MakeAPayment)) {
				strExitState = Constants.CAIF_MN_001_MakeAPayment;
			} else if(strReturnValue.equalsIgnoreCase(Constants. CAIF_MN_002_MainMenu)) {
				strExitState = Constants. CAIF_MN_002_MainMenu;
			} else if(strReturnValue.equalsIgnoreCase(Constants. CAIF_MN_002_DifferentAccount)) {
				strExitState = Constants. CAIF_MN_002_DifferentAccount;
			} else {
				strExitState = Constants.ER;
			}
		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in CAIF_MN_001_VALUE :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"CAIF_MN_001_VALUE :: "+strExitState);
		data.setSessionData("S_SUBFLOW_RETURN", strExitState);
		data.setSessionData(Constants.S_INTENT, strExitState);
		/*
		String menuExsitState = strExitState;
		if(strExitState.contains(" ")) menuExsitState = menuExsitState.replaceAll(" ", "_");
		if(null != (String)data.getSessionData("CAIF_MN_001_"+menuExsitState) && !((String)data.getSessionData("CAIF_MN_001_"+menuExsitState)).isEmpty()) menuExsitState = (String)data.getSessionData("CAIF_MN_001_"+menuExsitState);
		data.addToLog(currElementName, "Final Value of Menu Exit State for CAIF_MN_001: "+menuExsitState);
		data.setSessionData(Constants.S_MENU_SELCTION_KEY, data.getSessionData(Constants.S_BU)+":CAIF_MN_001:"+menuExsitState);
		*/
		return strExitState;
	}
}