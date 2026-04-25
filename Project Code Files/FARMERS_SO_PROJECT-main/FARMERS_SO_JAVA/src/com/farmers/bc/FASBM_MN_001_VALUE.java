package com.farmers.bc;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class FASBM_MN_001_VALUE extends DecisionElementBase{

	public String doDecision(String currElementName, DecisionElementData data)
			throws Exception {
		String strExitState=Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {

			String strReturnValue = (String) data.getElementData("FASBM_MN_001_Call","Return_Value");
			data.addToLog(currElementName, " FASBM_MN_001 : Return_Value :: "+strReturnValue);
			data.setSessionData("FASBM_MN_001_VALUE", strReturnValue);
			if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
			} else if(strReturnValue.equalsIgnoreCase("Payments")) {
				strExitState = "Payments";
				caa.createMSPKey(caa, data, "FASBM_MN_001", "PAYMENTS");
			}  else if(strReturnValue.equalsIgnoreCase("Refunds")) {
				strExitState = "Refunds";
				caa.createMSPKey(caa, data, "FASBM_MN_001", "REFUNDS");
			} else if(strReturnValue.equalsIgnoreCase("Other Billing Questions")){
				strExitState ="Other Billing Questions";
				caa.createMSPKey(caa, data, "FASBM_MN_001", "OTHER BILLING QUESTIONS");
			}
			else if(strReturnValue.equalsIgnoreCase("Misapplied Money")) {
				strExitState="Misapplied Money";
				caa.createMSPKey(caa, data, "FASBM_MN_001", "MISAPPLIED MONEY");
			}else if(strReturnValue.equalsIgnoreCase("EFT")) {
				strExitState="EFT";
				caa.createMSPKey(caa, data, "FASBM_MN_001", "EFT");
			}
			else if(strReturnValue.equalsIgnoreCase("Installment Questions"))
			{
				strExitState="Installment Questions";
				caa.createMSPKey(caa, data, "FASBM_MN_001", "INSTALLMENT QUESTIONS");
			}
			else if(strReturnValue.equalsIgnoreCase("Representative"))
			{
				strExitState="Representative";
			}
			else strExitState=Constants.ER;
		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in FASBM_MN_001_VALUE :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"FASBM_MN_001_VALUE :: "+strExitState);

//		String menuExitState = strExitState;
//		if(strExitState.contains(" ")) menuExitState = menuExitState.replaceAll(" ", "_");
//		if(null != (String)data.getSessionData("FASBM_MN_001"+menuExitState) && !((String)data.getSessionData("FASBM_MN_001_"+menuExitState)).isEmpty()) menuExitState = (String)data.getSessionData("FASBM_MN_001_"+menuExitState);
//		data.addToLog(currElementName, "Final Value of Menu Exit State for FASBM_MN_001: "+menuExitState);
//		data.setSessionData(Constants.S_MENU_SELCTION_KEY, data.getSessionData(Constants.S_BU)+":FASBM_MN_001:"+menuExitState);
		return strExitState;
	}

}


