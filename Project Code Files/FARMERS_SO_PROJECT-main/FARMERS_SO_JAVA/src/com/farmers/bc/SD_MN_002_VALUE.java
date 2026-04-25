package com.farmers.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class SD_MN_002_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("SD_MN_002_Call","Return_Value");
			data.addToLog(currElementName, " SD_MN_002_Call : Return_Value :: "+strReturnValue);
			data.setSessionData(Constants.SD_MN_002_VALUE, strReturnValue);
			if(strReturnValue.length() > 0 && !strReturnValue.equalsIgnoreCase(Constants.NOINPUT) && !strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
            	data.setSessionData(Constants.S_FINAL_STATE, strReturnValue);
            	data.setSessionData(Constants.S_STATENAME, strReturnValue);
            	strExitState = Constants.SU;
            	
            } else if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
			} else {
				strExitState = Constants.ER;
			}
		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in SD_MN_002_VALUE :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"SD_MN_002_VALUE :: "+strExitState);
		
//		String menuExsitState = strExitState;
//		if(strExitState.contains(" ")) menuExsitState = menuExsitState.replaceAll(" ", "_");
//		if(null != (String)data.getSessionData("SD_MN_002_"+menuExsitState) && !((String)data.getSessionData("SD_MN_002_"+menuExsitState)).isEmpty()) menuExsitState = (String)data.getSessionData("SD_MN_002_"+menuExsitState);
//		data.addToLog(currElementName, "Final Value of Menu Exit State for SD_MN_002: "+menuExsitState);
//		data.setSessionData(Constants.S_MENU_SELCTION_KEY, data.getSessionData(Constants.S_BU)+":SD_MN_002:"+menuExsitState);
		
		return strExitState;
	}
}