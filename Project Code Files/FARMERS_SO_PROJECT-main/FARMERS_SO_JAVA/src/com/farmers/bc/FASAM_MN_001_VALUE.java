package com.farmers.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class FASAM_MN_001_VALUE extends DecisionElementBase{

	public String doDecision(String currElementName, DecisionElementData data){

		String strExitState=Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try{

			String strReturnValue = (String) data.getElementData("FASAM_MN_001_Call","Return_Value");
			data.addToLog(currElementName, " FASAM_MN_001 : Return_Value :: "+strReturnValue);
			data.setSessionData("FASAM_MN_001_VALUE", strReturnValue);
			
			if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			}else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
			}else if(strReturnValue.equalsIgnoreCase("Billing")) {
				strExitState ="Billing";
			}else if(strReturnValue.equalsIgnoreCase("Policy Changes")) {
				strExitState ="Policy Changes";
				caa.createMSPKey(caa, data, "FASAM_MN_001", "POLICY CHANGES");
			}else if(strReturnValue.equalsIgnoreCase("Discounts")){
				strExitState = "Discounts";
				caa.createMSPKey(caa, data, "FASAM_MN_001", "DISCOUNTS");
			}else if(strReturnValue.equalsIgnoreCase("Rate Questions")) {
				strExitState="Rate Questions";
				caa.createMSPKey(caa, data, "FASAM_MN_001", "RATE QUESTIONS");
			}else if(strReturnValue.equalsIgnoreCase("Surcharges")) {
				strExitState="Surcharges";
				caa.createMSPKey(caa, data, "FASAM_MN_001", "SURCHARGES");
			}else if(strReturnValue.equalsIgnoreCase("Eligibility")) {
				strExitState="Eligibility";
				caa.createMSPKey(caa, data, "FASAM_MN_001", "ELIGIBILITY");
			}else if(strReturnValue.equalsIgnoreCase("Representative")) {
				strExitState = "representative";
			}
			else if(strReturnValue.equalsIgnoreCase("Underwriting"))
			{
				strExitState="Underwriting";
				caa.createMSPKey(caa, data, "FASAM_MN_001", "UNDERWRITING");
			}
			else strExitState=Constants.ER;

		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in FASBM_MN_001_VALUE :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"FASAM_MN_001_VALUE :: "+strExitState);

		//		String menuExitState = strExitState;
		//		if(null != strExitState && (!"representative".equalsIgnoreCase(strExitState)) && (!Constants.NOMATCH.equalsIgnoreCase(strExitState))
		//				&& (!Constants.NOINPUT.equalsIgnoreCase(strExitState))) {
		//			if(strExitState.contains(" ")) menuExitState = menuExitState.replaceAll(" ", "_");
		//			if(null != (String)data.getSessionData("FASAM_MN_001_"+menuExitState) && !((String)data.getSessionData("FASAM_MN_001_"+menuExitState)).isEmpty()) menuExitState = (String)data.getSessionData("FASBM_MN_001_"+menuExitState);
		//			data.addToLog(currElementName, "Final Value of Menu Exit State for FASAM_MN_001: "+menuExitState);
		//			try {
		//				data.setSessionData(Constants.S_MENU_SELCTION_KEY, data.getSessionData(Constants.S_BU)+":FASAM_MN_001:"+menuExitState);
		//			} catch (AudiumException e) {
		//				data.addToLog(currElementName,"Exception in FASBM_MN_001_VALUE :: "+e);
		//				caa.printStackTrace(e);
		//			}
		//		}else {
		//			data.addToLog(currElementName, "Exitstate is either representative or NoMatch or NoInput, so skipping the MSP setting :: " + strExitState);
		//		}
		return strExitState;
	}


}


