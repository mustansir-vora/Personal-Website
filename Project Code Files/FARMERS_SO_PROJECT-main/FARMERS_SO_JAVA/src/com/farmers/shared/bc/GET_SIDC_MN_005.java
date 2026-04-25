package com.farmers.shared.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class GET_SIDC_MN_005 extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("SIDC_MN_005_Call","Return_Value");
			data.addToLog(currElementName, " SIDC_MN_005_Call : Return_Value :: "+strReturnValue);
			data.setSessionData(Constants.SIDC_MN_005_VALUE, strReturnValue);
			if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
			} else if(strReturnValue.equalsIgnoreCase(Constants.SIDC_MN_005_MainMenu)) {
				strExitState = Constants.SIDC_MN_005_MainMenu;
			} else if(strReturnValue.equalsIgnoreCase(Constants.REPRESENTATIVE)) {
				strExitState = Constants.REPRESENTATIVE;
				caa.createMSPKey(caa, data, "SIDC_MN_005", "REPRESENTATIVE");
			} else {
				strExitState = Constants.ER;
			}
		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in SIDC_MN_005_VALUE :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"SIDC_MN_005_VALUE :: "+strExitState);
//		String menuExsitState = strExitState;
//		if(strExitState.contains(" ")) menuExsitState = menuExsitState.replaceAll(" ", "_");
//		if(null != (String)data.getSessionData("SIDC_MN_005_"+menuExsitState) && !((String)data.getSessionData("SIDC_MN_005_"+menuExsitState)).isEmpty()) menuExsitState = (String)data.getSessionData("SIDC_MN_005_"+menuExsitState);
//		data.addToLog(currElementName, "Final Value of Menu Exit State for SIDC_MN_005: "+menuExsitState);
//		data.setSessionData(Constants.S_MENU_SELCTION_KEY, data.getSessionData(Constants.S_BU)+":SIDC_MN_005:"+menuExsitState);
		
		return strExitState;
	}
	
}


