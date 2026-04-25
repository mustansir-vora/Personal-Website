package com.farmers.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.Constants;

public class CBIZ_EliteMSPkey extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.STRING_NO;
		String strIsElite = (String)data.getSessionData(Constants.S_IS_ELITE);
		if(null!=strIsElite && !strIsElite.isEmpty() && Constants.ELITE.equalsIgnoreCase(strIsElite)) {
			strExitState = Constants.STRING_YES;
			data.setSessionData(Constants.S_MENU_SELCTION_KEY, data.getSessionData(Constants.S_BU)+":CBIZ_MN_001:"+data.getSessionData(Constants.CBIZ_MN_001_ExitState)+" ELITE AGENT");
		}
		data.addToLog(currElementName, "CBIZ_EliteMSPkey : strExitState : "+strExitState);
		data.addToLog(currElementName, "CBIZ_MN_001 : "+strExitState+" :S_MENU_SELCTION_KEY: "+data.getSessionData(Constants.S_MENU_SELCTION_KEY));
		return strExitState;
		
	}

}
