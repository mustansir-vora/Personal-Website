package com.farmers.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class CPD_MN_002_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("CPD_MN_002_Call","Return_Value");
			data.addToLog(currElementName, "CPD_MN_002_Call : Return_Value :: "+strReturnValue);
			data.setSessionData(Constants.CPD_MN_002_VALUE, strReturnValue);
			  
			if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
			    data.setSessionData(Constants.S_POLICY_ATTRIBUTES, "Retention Eligible");	            
			}else if (strReturnValue.equalsIgnoreCase(Constants.REPRESENTATIVE)) {
				strExitState = Constants.REPRESENTATIVE;
				data.setSessionData(Constants.S_POLICY_ATTRIBUTES, "Retention Eligible"); 
			} else if(strReturnValue.equalsIgnoreCase(Constants.STRING_YES)) {
				strExitState = Constants.STRING_YES;
				data.setSessionData(Constants.S_POLICY_ATTRIBUTES, "Retention Eligible");
	           
			}  else if(strReturnValue.equalsIgnoreCase(Constants.STRING_NO)) {
				strExitState = Constants.STRING_NO;
			} else {
				strExitState = Constants.ER;
				data.setSessionData(Constants.S_POLICY_ATTRIBUTES, "Retention Eligible");  
			}
		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in CPD_MN_002_VALUE :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"CPD_MN_002_VALUE :: "+strExitState);
		/*
		String menuExsitState = strExitState;
		if(strExitState.contains(" ")) menuExsitState = menuExsitState.replaceAll(" ", "_");
		if(null != (String)data.getSessionData("SD_MN_001_"+menuExsitState) && !((String)data.getSessionData("SD_MN_001_"+menuExsitState)).isEmpty()) menuExsitState = (String)data.getSessionData("SD_MN_001_"+menuExsitState);
		data.addToLog(currElementName, "Final Value of Menu Exit State for SD_MN_001: "+menuExsitState);
		data.setSessionData(Constants.S_MENU_SELCTION_KEY, data.getSessionData(Constants.S_BU)+":SD_MN_001:"+menuExsitState);
		*/
		return strExitState;
	}
}