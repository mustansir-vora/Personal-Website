package com.farmers.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class KYCMF_MN_002_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("KYCMF_MN_002_Call","Return_Value");
			data.addToLog(currElementName, " KYCMF_MN_002_VALUE : Return_Value :: " + strReturnValue);
			
			data.setSessionData(Constants.KYCMF_MN_002_VALUE, strReturnValue);
			if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
				
			} else if (strReturnValue.equalsIgnoreCase("Agent")) {
				strExitState = "Agent";
				//caa.createMSPKey(caa, data, "KYCMF_MN_002", "AGENT");
				data.setSessionData(Constants.S_CALLLER_TYPE,"02");
				
			} else if (strReturnValue.equalsIgnoreCase(Constants.S_CUSTOMER)) {
				strExitState = Constants.S_CUSTOMER;
				//caa.createMSPKey(caa, data, "KYCMF_MN_002", "CUSTOMER");
				data.setSessionData(Constants.S_CALLLER_TYPE,"01");
				data.setSessionData("S_AGENT_OPTED_NO", "Y");
				data.addToLog(currElementName,"S_AGENT_OPTED_NO :: "+data.getSessionData("S_AGENT_OPTED_NO"));
				
			}else if (strReturnValue.equalsIgnoreCase(Constants.S_LINEHOLDER)) {
				strExitState = Constants.S_LINEHOLDER;
				//caa.createMSPKey(caa, data, "KYCMF_MN_002", "LIENHOLDER");
				data.setSessionData("S_AGENT_OPTED_NO", "Y");
				data.setSessionData(Constants.S_CALLLER_TYPE,"03");
				
			} else {
				strExitState = Constants.ER;
			}
		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in KYCMF_MN_002_VALUE :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"KYCMF_MN_002_VALUE :: "+strExitState);
		/*
		String menuExsitState = strExitState;
		if(strExitState.contains(" ")) menuExsitState = menuExsitState.replaceAll(" ", "_");
		if(null != (String)data.getSessionData("KYCMF_MN_001_"+menuExsitState) && !((String)data.getSessionData("KYCMF_MN_001_"+menuExsitState)).isEmpty()) menuExsitState = (String)data.getSessionData("KYCMF_MN_001_"+menuExsitState);
		data.addToLog(currElementName, "Final Value of Menu Exit State for KYCMF_MN_001: "+menuExsitState);
		data.setSessionData(Constants.S_MENU_SELCTION_KEY, data.getSessionData(Constants.S_BU)+":KYCMF_MN_001:"+menuExsitState);
		*/
		return strExitState;
	}
}
