package com.farmers.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class KYCBA_MN_001_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("KYCBA_MN_001_Call","Return_Value");
			data.addToLog(currElementName, " KYCBA_MN_001_Call : Return_Value :: "+strReturnValue);
			data.setSessionData(Constants.S_CALLER_INPUT_DOB, strReturnValue);
			data.setSessionData(Constants.S_DOB, strReturnValue);
			data.setSessionData(Constants.KYCBA_MN_001_VALUE, strReturnValue);
			if(strReturnValue.equalsIgnoreCase(Constants.KYCBA_MN_001_Dontknow)) {
				strExitState = Constants.KYCBA_MN_001_Dontknow;
			}else if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;	
			}
			
			
			else if(strReturnValue.equalsIgnoreCase(Constants.S_REPRESENTATIVE)) {
				strExitState = Constants.S_REPRESENTATIVE;
			}
			else {
				strExitState =  Constants.SU;
			}
		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in KYCBA_MN_001_VALUE :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"KYCBA_MN_001_VALUE :: "+strExitState);
		/*
		String menuExsitState = strExitState;
		if(strExitState.contains(" ")) menuExsitState = menuExsitState.replaceAll(" ", "_");
		if(null != (String)data.getSessionData("KYCBA_MN_001_"+menuExsitState) && !((String)data.getSessionData("KYCBA_MN_001_"+menuExsitState)).isEmpty()) menuExsitState = (String)data.getSessionData("KYCBA_MN_001_"+menuExsitState);
		data.addToLog(currElementName, "Final Value of Menu Exit State for KYCBA_MN_001: "+menuExsitState);
		data.setSessionData(Constants.S_MENU_SELCTION_KEY, data.getSessionData(Constants.S_BU)+":KYCBA_MN_001:"+menuExsitState);
		*/
		return strExitState;
	}
}