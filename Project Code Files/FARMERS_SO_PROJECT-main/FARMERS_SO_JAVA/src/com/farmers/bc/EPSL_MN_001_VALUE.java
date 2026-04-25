package com.farmers.bc;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class EPSL_MN_001_VALUE extends DecisionElementBase{

	public String doDecision(String currElementName, DecisionElementData data)
			throws Exception {

		String strExitState=Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try{

			String strReturnValue = (String) data.getElementData("EPSL_MN_001_Call","Return_Value");
			data.addToLog(currElementName, " EPSL_MN_001 : Return_Value :: "+strReturnValue);
			String strAgentIDLength = (String) data.getSessionData("S_AGENT_ID_LENGTH");
			data.setSessionData("EPSL_MN_001_VALUE", strReturnValue);
			
			if(null == strAgentIDLength) {
				
			}
			if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;

			}else if(strReturnValue.length() == 9 && strReturnValue.startsWith("1")) {
				data.setSessionData(Constants.S_AGENTID_1,strReturnValue);
				strExitState="SU";
			}
			else strExitState=Constants.ER;
		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in EPSL_MN_001_VALUE  :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"EPSL_MN_001_VALUE  :: "+strExitState);

//		String menuExitState = strExitState;
//		if(strExitState.contains(" ")) menuExitState = menuExitState.replaceAll(" ", "_");
//		if(null != (String)data.getSessionData("EPSL_MN_001_"+menuExitState) && !((String)data.getSessionData("EPSL_MN_001_"+menuExitState)).isEmpty()) menuExitState = (String)data.getSessionData("EPSL_MN_001_"+menuExitState);
//		data.addToLog(currElementName, "Final Value of Menu Exit State for EPSL_MN_001: "+menuExitState);
//		data.setSessionData(Constants.S_MENU_SELCTION_KEY, data.getSessionData(Constants.S_BU)+":EPSL_MN_001:"+menuExitState);
		return strExitState;


	}

}


