package com.farmers.bc;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class FASMM_MN_001_VALUE  extends DecisionElementBase{
	public String doDecision(String currElementName, DecisionElementData data)throws Exception{
		String strExitState=Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try{
			String strReturnValue = (String) data.getElementData("FASMM_MN_001_Call","Return_Value");
			data.addToLog(currElementName, " FASMM_MN_001_VALUE : Return_Value :: "+strReturnValue);
			data.setSessionData(Constants.FASMM_MN_001_VALUE, strReturnValue);
			if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			}
			else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
			}else if(strReturnValue.equalsIgnoreCase("Auto")) {
				strExitState = "Auto";
				data.setSessionData(Constants.S_INTENT, strReturnValue);
				caa.createMSPKey(caa, data, "FASMM_MN_001", "AUTO");
			}  else if(strReturnValue.equalsIgnoreCase("Home")) {
				strExitState = "Home";
				data.setSessionData(Constants.S_INTENT, strReturnValue);
				caa.createMSPKey(caa, data, "FASMM_MN_001", "HOME");
			} else if(strReturnValue.equalsIgnoreCase("Umberlla")) {
				strExitState = "Umberlla";
				data.setSessionData(Constants.S_INTENT, strReturnValue);
				caa.createMSPKey(caa, data, "FASMM_MN_001", "UMBRELLA");
			} else if(strReturnValue.equalsIgnoreCase("Billing")) {
				strExitState = "Billing";
				data.setSessionData(Constants.S_INTENT, strReturnValue);
				caa.createMSPKey(caa, data, "FASMM_MN_001", "BILLING");
			} else if(strReturnValue.equalsIgnoreCase("underwriting")) {
				strExitState = "underwriting";
				data.setSessionData(Constants.S_INTENT, strReturnValue);
				caa.createMSPKey(caa, data, "FASMM_MN_001", "UNDERWRITING");
			} else if(strReturnValue.equalsIgnoreCase("Representative")) {
				strExitState = "representative";
			}
			else if(strReturnValue.equalsIgnoreCase("More Options")) {
				strExitState = "More Options";
				caa.createMSPKey(caa, data, "FASMM_MN_001", "MORE OPTIONS");
			} else {
				strExitState = Constants.ER;
			}
		}
		catch (Exception e)
		{
			data.addToLog(currElementName,"Exception in FASMM_MN_001_VALUE :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"FASMM_MN_001_VALUE :: "+strExitState);

		//		String menuExsitState = strExitState;
		//		if(strExitState.contains(" ")) menuExsitState = menuExsitState.replaceAll(" ", "_");
		//		if(null != (String)data.getSessionData("FASMM_MN_001_"+menuExsitState) && !((String)data.getSessionData("FASMM_MN_001_"+menuExsitState)).isEmpty()) menuExsitState = (String)data.getSessionData("FASMM_MN_001_"+menuExsitState);
		//		data.addToLog(currElementName, "Final Value of Menu Exit State for FASMM_MN_001: "+menuExsitState);
		//		data.setSessionData(Constants.S_MENU_SELCTION_KEY, data.getSessionData(Constants.S_BU)+":FASMM_MN_001:"+menuExsitState);
		return strExitState;
	}
}