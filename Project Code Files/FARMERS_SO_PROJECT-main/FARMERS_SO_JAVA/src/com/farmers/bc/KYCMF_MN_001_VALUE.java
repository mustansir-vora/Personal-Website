package com.farmers.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class KYCMF_MN_001_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("KYCMF_MN_001_Call","Return_Value");
			data.setSessionData(Constants.KYCMF_MN_001_VALUE, strReturnValue);
			if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
			} else if(strReturnValue.equalsIgnoreCase(Constants.STRING_YES)) {
				strExitState = Constants.STRING_YES;
			}  else if(strReturnValue.equalsIgnoreCase(Constants.STRING_NO)) {
				strExitState = Constants.STRING_NO;
				data.setSessionData(Constants.S_CALLLER_TYPE,"01");//CS1160011:All Brands-Customer caller type 01
				data.setSessionData("S_AGENT_OPTED_NO", "Y");//CS1160011:All Brands-Customer caller type 01
				data.addToLog(currElementName,"S_AGENT_OPTED_NO :: "+data.getSessionData("S_AGENT_OPTED_NO"));//CS1160011:All Brands-Customer caller type 01
			} else {
				strExitState = Constants.ER;
			}
		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in KYCMF_MN_001_VALUE :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"KYCMF_MN_001_VALUE :: "+strExitState);
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