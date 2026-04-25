package com.farmers.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class CMMF_MN_002_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("CMMF_MN_002_Call","Return_Value");
			data.addToLog(currElementName, " CMMF_MN_002_Call : Return_Value :: "+strReturnValue);
			data.setSessionData(Constants.CMMF_MN_002_VALUE, strReturnValue);
			if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
			} else if(strReturnValue.equalsIgnoreCase(Constants.CMMF_MN_002_PaymentMailingAddress)) {
				strExitState = Constants.CMMF_MN_002_PaymentMailingAddress;
			} else if(strReturnValue.equalsIgnoreCase(Constants.CMMF_MN_002_GenCorr)) {
				strExitState = Constants.CMMF_MN_002_GenCorr;
			} else {
				strExitState = Constants.ER;
			}
		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in CMMF_MN_002_VALUE :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"CMMF_MN_002_VALUE :: "+strExitState);
		/*
		String menuExsitState = strExitState;
		if(strExitState.contains(" ")) menuExsitState = menuExsitState.replaceAll(" ", "_");
		if(null != (String)data.getSessionData("CMMF_MN_002_"+menuExsitState) && !((String)data.getSessionData("CMMF_MN_002_"+menuExsitState)).isEmpty()) menuExsitState = (String)data.getSessionData("CMMF_MN_002_"+menuExsitState);
		data.addToLog(currElementName, "Final Value of Menu Exit State for CMMF_MN_002: "+menuExsitState);
		data.setSessionData(Constants.S_MENU_SELCTION_KEY, data.getSessionData(Constants.S_BU)+":CMMF_MN_002:"+menuExsitState);
		*/
		return strExitState;
	}
}