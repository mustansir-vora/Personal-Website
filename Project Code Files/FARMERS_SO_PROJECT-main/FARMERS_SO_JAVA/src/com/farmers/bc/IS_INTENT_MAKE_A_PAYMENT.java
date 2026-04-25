package com.farmers.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class IS_INTENT_MAKE_A_PAYMENT extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.STRING_NO;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		String strReturnValue = Constants.EmptyString;
		try {		
			String isIntent = (String) data.getSessionData(Constants.S_INTENT);
			data.addToLog(currElementName, " S_INTENT Value :: "+isIntent);		
			if(null!=isIntent && !isIntent.isEmpty() && isIntent.equalsIgnoreCase("MAKE_A_PAYMENT")) {
				strExitState = Constants.STRING_YES;
			} else {
				strReturnValue = Constants.DoNotMakePayment;
				data.addToLog(currElementName, "Value of Menu in Intent :"+strReturnValue);	
				strExitState = Constants.STRING_NO;
			}
		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in IS_INTENT_MAKE_A_PAYMENT :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"IS_INTENT_MAKE_A_PAYMENT :: "+strExitState);
		/*
		String menuExsitState = strReturnValue;
		if(strReturnValue.contains(" ")) menuExsitState = menuExsitState.replaceAll(" ", "_");
		if(null != (String)data.getSessionData("CIDA_MN_001_"+menuExsitState) && !((String)data.getSessionData("CIDA_MN_001_"+menuExsitState)).isEmpty()) menuExsitState = (String)data.getSessionData("CIDA_MN_001_"+menuExsitState);
		data.addToLog(currElementName, "Final Value of Menu Exit State for IS_ELITE_AGENT_BC in CIDA_MN_001: "+menuExsitState);
		data.setSessionData(Constants.S_MENU_SELCTION_KEY, data.getSessionData(Constants.S_BU)+":CIDA_MN_001:"+menuExsitState);
		*/
		return strExitState;
	}
}