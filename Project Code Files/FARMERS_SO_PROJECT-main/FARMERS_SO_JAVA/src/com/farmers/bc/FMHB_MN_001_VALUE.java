package com.farmers.bc;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class FMHB_MN_001_VALUE  extends DecisionElementBase{
	public String doDecision(String currElementName, DecisionElementData data)throws Exception{
		String strExitState=Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try{
			String strReturnValue = (String) data.getElementData("FMHB_MN_001_Call","Return_Value");
			data.addToLog(currElementName, " FMHB_MN_001_VALUE : Return_Value :: "+strReturnValue);
			data.setSessionData(Constants.FMHB_MN_001_VALUE, strReturnValue);

			if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
			} else if(strReturnValue.equalsIgnoreCase("Dial an Extension")) {
				strExitState = "Dial an Extension";
				data.setSessionData(Constants.S_INTENT, strReturnValue);
				caa.createMSPKey(caa, data, "FMHB_MN_001", "DIAL AN EXTENSION");
			} else if(strReturnValue.equalsIgnoreCase("Personal")) {
				strExitState ="Personal";
				data.setSessionData(Constants.S_INTENT, strReturnValue);
				caa.createMSPKey(caa, data, "FMHB_MN_001", "PERSONAL");
			} else if(strReturnValue.equalsIgnoreCase("Commercial")) {
				strExitState ="Commercial";
				data.setSessionData(Constants.S_INTENT, strReturnValue);
				caa.createMSPKey(caa, data, "FMHB_MN_001", "COMMERCIAL");
			} else if(strReturnValue.equalsIgnoreCase("New Auto Quote")) {
				strExitState ="New Auto Quote";
				data.setSessionData(Constants.S_INTENT, strReturnValue);
				caa.createMSPKey(caa, data, "FMHB_MN_001", "NEW AUTO QUOTE");
			} else if(strReturnValue.equalsIgnoreCase("Representative")) {
				strExitState ="Representative";
				caa.createMSPKey(caa, data, "FMHB_MN_001", "REPRESENTATIVE");
			} else {
				strExitState = Constants.ER;
			}
		}
		catch (Exception e)
		{
			data.addToLog(currElementName,"Exception in FMHB_MN_001_VALUE :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"FMHB_MN_001_VALUE :: "+strExitState);

		//		String menuExsitState = strExitState;
		//      	if(strExitState.contains(" ")) menuExsitState = menuExsitState.replaceAll(" ", "_");
		//		if(null != (String)data.getSessionData("FMHB_MN_001_"+menuExsitState) && !((String)data.getSessionData("FMHB_MN_001_"+menuExsitState)).isEmpty()) menuExsitState = (String)data.getSessionData("FMHB_MN_001_"+menuExsitState);
		//		data.addToLog(currElementName, "Final Value of Menu Exit State for FMHB_MN_001: "+menuExsitState);
		//		data.setSessionData(Constants.S_MENU_SELCTION_KEY, data.getSessionData(Constants.S_BU)+":FMHB_MN_001:"+menuExsitState);
		return strExitState;	
	}
}
