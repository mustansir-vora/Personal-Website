package com.farmers.bc;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class EPSL_MN_004_VALUE extends DecisionElementBase
{
	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws Exception {
		String strExitState=Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("EPSL_MN_004_Call","Return_Value");
			data.addToLog(currElementName, " EPSL_MN_004 : Return_Value :: "+strReturnValue);
			data.setSessionData("EPSL_MN_004_VALUE", strReturnValue);
			if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;

			}else if(strReturnValue.equalsIgnoreCase("YES")) {
				strExitState="YES";
			}
			else if(strReturnValue.equalsIgnoreCase("NO")) {
				strExitState="NO";
			}
			else strExitState=Constants.ER;
		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in EPSL_MN_004_VALUE  :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"EPSL_MN_004_VALUE :: "+strExitState);

//		String menuExitState = strExitState;
//		if(strExitState.contains(" ")) menuExitState = menuExitState.replaceAll(" ", "_");
//		if(null != (String)data.getSessionData("EPSL_MN_004_"+menuExitState) && !((String)data.getSessionData("EPSL_MN_004_"+menuExitState)).isEmpty()) menuExitState = (String)data.getSessionData("EPSL_MN_004_"+menuExitState);
//		data.addToLog(currElementName, "Final Value of Menu Exit State for EPSL_MN_004: "+menuExitState);
//		data.setSessionData(Constants.S_MENU_SELCTION_KEY, data.getSessionData(Constants.S_BU)+":EPSL_MN_004:"+menuExitState);
		return strExitState;

	}
}


