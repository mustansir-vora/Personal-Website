package com.farmers.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class CBIZ_MN_001_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("CBIZ_MN_001_Call","Return_Value");
			data.addToLog(currElementName, " CBIZ_MN_001_Call : Return_Value :: "+strReturnValue);
			data.setSessionData(Constants.CBIZ_MN_001_VALUE, strReturnValue);
			if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
			} else if(strReturnValue.equalsIgnoreCase(Constants.CBIZ_MN_001_DialAnExtension)) {
				strExitState = Constants.CBIZ_MN_001_DialAnExtension;
			} else if(strReturnValue.equalsIgnoreCase(Constants.CBIZ_MN_001_PropertyAndCasualty)) {
				strExitState = Constants.CBIZ_MN_001_PropertyAndCasualty;
			} else if(strReturnValue.equalsIgnoreCase(Constants.CBIZ_MN_001_WorkersComp)) {
				strExitState = Constants.CBIZ_MN_001_WorkersComp;
			} else if(strReturnValue.equalsIgnoreCase(Constants.CBIZ_MN_001_BillingandReinstatement)) {
				strExitState = Constants.CBIZ_MN_001_BillingandReinstatement;
			} else if(strReturnValue.equalsIgnoreCase(Constants.CBIZ_MN_001_RealTimeBilling)) {
				strExitState = Constants.CBIZ_MN_001_RealTimeBilling;
			} else if(strReturnValue.equalsIgnoreCase(Constants.CBIZ_MN_001_ContactInformation)) {
				strExitState = Constants.CBIZ_MN_001_ContactInformation;
			} else if(strReturnValue.equalsIgnoreCase(Constants.REPRESENTATIVE)) {
				strExitState = Constants.REPRESENTATIVE;
			}
			else {
				strExitState = Constants.ER;
			}
		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in CBIZ_MN_001_VALUE :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"CBIZ_MN_001_VALUE :: "+strExitState);
		data.setSessionData(Constants.S_INTENT, strExitState);
		
		String menuExsitState = strExitState;
		if(strExitState.contains(" ")) menuExsitState = menuExsitState.replaceAll(" ", "_");
		if(null != (String)data.getSessionData("CBIZ_MN_001_"+menuExsitState) && !((String)data.getSessionData("CBIZ_MN_001_"+menuExsitState)).isEmpty()) menuExsitState = (String)data.getSessionData("CBIZ_MN_001_"+menuExsitState);
		data.addToLog(currElementName, "Final Value of Menu Exit State for CBIZ_MN_001: "+menuExsitState);
		data.setSessionData(Constants.CBIZ_MN_001_ExitState,menuExsitState);
		data.setSessionData(Constants.S_MENU_SELCTION_KEY, data.getSessionData(Constants.S_BU)+":CBIZ_MN_001:"+menuExsitState);

		return strExitState;
	}
}