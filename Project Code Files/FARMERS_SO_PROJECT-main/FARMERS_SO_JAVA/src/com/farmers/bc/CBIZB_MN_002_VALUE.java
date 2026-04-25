package com.farmers.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class CBIZB_MN_002_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("CBIZB_MN_002_Call","Return_Value");
			data.addToLog(currElementName, " CBIZB_MN_002_Call : Return_Value :: "+strReturnValue);
			data.setSessionData(Constants.CBIZB_MN_002_VALUE, strReturnValue);
			if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
			} else if(strReturnValue.equalsIgnoreCase("Invoice and Electronic Payments")) {
				strExitState = "Invoice and Electronic Payments";
			} else if(strReturnValue.equalsIgnoreCase(Constants.CBIZB_MN_002_Reinstatement)) {
				strExitState = Constants.CBIZB_MN_002_Reinstatement;
			} else if(strReturnValue.equalsIgnoreCase(Constants.CBIZB_MN_002_AddressandPaymentPlanChanges)) {
				strExitState = Constants.CBIZB_MN_002_AddressandPaymentPlanChanges;
			} else if(strReturnValue.equalsIgnoreCase(Constants.CBIZB_MN_002_SomethingElse)) {
				strExitState = Constants.CBIZB_MN_002_SomethingElse;
			} else if(strReturnValue.equalsIgnoreCase(Constants.CBIZB_MN_002_StandardPay)) {
				strExitState = Constants.CBIZB_MN_002_StandardPay;
			} else {
				strExitState = Constants.ER;
			}
		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in CBIZB_MN_001_VALUE :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"CBIZB_MN_001_VALUE :: "+strExitState);
		
		String menuExsitState = strExitState;
		if(strExitState.contains(" ")) menuExsitState = menuExsitState.replaceAll(" ", "_");
		if(null != (String)data.getSessionData("CBIZB_MN_002_"+menuExsitState) && !((String)data.getSessionData("CBIZB_MN_002_"+menuExsitState)).isEmpty()) menuExsitState = (String)data.getSessionData("CBIZB_MN_002_"+menuExsitState);
		data.addToLog(currElementName, "Final Value of Menu Exit State for CBIZB_MN_002: "+menuExsitState);
		data.setSessionData(Constants.S_MENU_SELCTION_KEY, "COMMERCIAL"+":CBIZB_MN_002:"+menuExsitState);
		
		return strExitState;
	}
}