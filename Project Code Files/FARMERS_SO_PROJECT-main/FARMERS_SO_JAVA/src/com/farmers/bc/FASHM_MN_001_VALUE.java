package com.farmers.bc;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class FASHM_MN_001_VALUE  extends DecisionElementBase{
	public String doDecision(String currElementName, DecisionElementData data)throws Exception{
		String strExitState=Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try{
			String strReturnValue = (String) data.getElementData("FASHM_MN_001_Call","Return_Value");
			data.addToLog(currElementName, " FASHM_MN_001_VALUE : Return_Value :: "+strReturnValue);
			data.setSessionData(Constants.FASHM_MN_001_VALUE, strReturnValue);
			if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
			} else if(strReturnValue.equalsIgnoreCase("Policy Changes")) {
				strExitState = "Policy Changes";
				caa.createMSPKey(caa, data, "FASHM_MN_001", "POLICY CHANGES");
			} else if(strReturnValue.equalsIgnoreCase("Billing")) {
				strExitState ="Billing";
			} else if(strReturnValue.equalsIgnoreCase("Eligibility")) {
				strExitState ="Eligibility";
				caa.createMSPKey(caa, data, "FASHM_MN_001", "ELIGIBILITY");
			} else if(strReturnValue.equalsIgnoreCase("Inspections")) {
				strExitState ="Inspections";
				caa.createMSPKey(caa, data, "FASHM_MN_001", "INSPECTIONS");
			} else if(strReturnValue.equalsIgnoreCase("Underwriting")) {
				strExitState ="Underwriting";
				caa.createMSPKey(caa, data, "FASHM_MN_001", "UNDERWRITING");
			}  else if(strReturnValue.equalsIgnoreCase("Discounts")) {
				strExitState ="Discounts";
				caa.createMSPKey(caa, data, "FASHM_MN_001", "DISCOUNTS");
			} else if(strReturnValue.equalsIgnoreCase("Rate Questions")) {
				strExitState ="Rate Questions";
				caa.createMSPKey(caa, data, "FASHM_MN_001", "RATE QUESTIONS");
			} else if(strReturnValue.equalsIgnoreCase("Surchanges")) {
				strExitState ="Surchanges";
				caa.createMSPKey(caa, data, "FASHM_MN_001", "SURCHARGES");
			} else if(strReturnValue.equalsIgnoreCase("360 Value")) {
				strExitState ="360 Value";
				caa.createMSPKey(caa, data, "FASHM_MN_001", "360 VALUE");
			}  else if(strReturnValue.equalsIgnoreCase("GUS")) {
				strExitState ="GUS";
				caa.createMSPKey(caa, data, "FASHM_MN_001", "G.U.S");
			} else if(strReturnValue.equalsIgnoreCase("Fire Lines")) {
				strExitState ="Fire Lines";
				caa.createMSPKey(caa, data, "FASHM_MN_001", "FIRE LINES");
			}else if(strReturnValue.equalsIgnoreCase("Representative")) {
				strExitState ="representative";
			}
			else {
				strExitState = Constants.ER;
			}
		}
		catch (Exception e)
		{
			data.addToLog(currElementName,"Exception in FASHM_MN_001_VALUE :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"FASHM_MN_001_VALUE :: "+strExitState);
//		String menuExsitState = strExitState;
//		if(strExitState.contains(" ")) menuExsitState = menuExsitState.replaceAll(" ", "_");
//		if(null != (String)data.getSessionData("FASHM_MN_001_"+menuExsitState) && !((String)data.getSessionData("FASHM_MN_001_"+menuExsitState)).isEmpty()) menuExsitState = (String)data.getSessionData("FASHM_MN_002_"+menuExsitState);
//		data.addToLog(currElementName, "Final Value of Menu Exit State for FASHM_MN_001: "+menuExsitState);
//		data.setSessionData(Constants.S_MENU_SELCTION_KEY, data.getSessionData(Constants.S_BU)+":FASHM_MN_001:"+menuExsitState);
		return strExitState;
	}
}