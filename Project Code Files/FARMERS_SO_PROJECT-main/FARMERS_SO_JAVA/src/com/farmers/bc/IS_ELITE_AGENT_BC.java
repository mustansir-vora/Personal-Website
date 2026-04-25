package com.farmers.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class IS_ELITE_AGENT_BC extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.STRING_NO;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		String strReturnValue = Constants.EmptyString;
		try {
			strReturnValue = (String) data.getElementData("CBIZ_MN_001_Call","Return_Value");
			data.addToLog(currElementName, " CBIZ_MN_001_Call : Return_Value :: "+strReturnValue);			
			String isEliteAgent = (String) data.getSessionData("S_IS_ELITE");
			data.addToLog(currElementName, " S_IS_ELITE Value :: "+isEliteAgent);		
			if(null!=isEliteAgent && !isEliteAgent.isEmpty() && isEliteAgent.equalsIgnoreCase("ELITE")) {
				strExitState = Constants.STRING_YES;
			} 
		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in IS_ELITE_AGENT_BC :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"IS_ELITE_AGENT_BC :: "+strExitState);
		
		String menuExsitState = strReturnValue;
		if(strReturnValue.contains(" ")) menuExsitState = menuExsitState.replaceAll(" ", "_");
		if(null != (String)data.getSessionData("CBIZ_MN_001_"+menuExsitState+"_Elite") && !((String)data.getSessionData("CBIZ_MN_001_"+menuExsitState+"_Elite")).isEmpty()) menuExsitState = (String)data.getSessionData("CBIZ_MN_001_"+menuExsitState+"_Elite");
		data.addToLog(currElementName, "Final Value of Menu Exit State for IS_ELITE_AGENT_BC in CBIZ_MN_001: "+menuExsitState);
		data.setSessionData(Constants.S_MENU_SELCTION_KEY, data.getSessionData(Constants.S_BU)+":CBIZ_MN_001:"+menuExsitState);
		
		return strExitState;
	}
}