package com.farmers.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class CBIZ_MN_002_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("CBIZ_MN_002_Call","Return_Value");
			data.addToLog(currElementName, " CBIZ_MN_002_Call : Return_Value :: "+strReturnValue);
			data.setSessionData(Constants.CBIZ_MN_002_VALUE, strReturnValue);
			if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
			} else if(strReturnValue.equalsIgnoreCase(Constants.CBIZ_MN_002_AppetiteEligibility)) {
				strExitState = Constants.CBIZ_MN_002_AppetiteEligibility;
			} else if(strReturnValue.equalsIgnoreCase(Constants.CBIZ_MN_002_Endorsement)) {
				strExitState = Constants.CBIZ_MN_002_Endorsement;
			} else if(strReturnValue.equalsIgnoreCase(Constants.CBIZ_MN_002_Lienholder)) {
				strExitState = Constants.CBIZ_MN_002_Lienholder;
				data.setSessionData(Constants.S_CALLLER_TYPE,"03");
			} else if(strReturnValue.equalsIgnoreCase(Constants.CBIZ_MN_002_SignatureForms)) {
				strExitState = Constants.CBIZ_MN_002_SignatureForms;
			} else if(strReturnValue.equalsIgnoreCase(Constants.CBIZ_MN_002_SomethingElse)) {
				strExitState = Constants.CBIZ_MN_002_SomethingElse;
			} else if(strReturnValue.equalsIgnoreCase(Constants.REPRESENTATIVE)) {
				strExitState = Constants.REPRESENTATIVE;
			} 
			else {
				strExitState = Constants.ER;
			}
		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in CBIZ_MN_002_VALUE :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"CBIZ_MN_002_VALUE :: "+strExitState);

		String menuExsitState = strExitState;
		if(strExitState.contains(" ")) menuExsitState = menuExsitState.replaceAll(" ", "_");
		if(null != (String)data.getSessionData("CBIZ_MN_002_"+menuExsitState) && !((String)data.getSessionData("CBIZ_MN_002_"+menuExsitState)).isEmpty()) menuExsitState = (String)data.getSessionData("CBIZ_MN_002_"+menuExsitState);
		data.addToLog(currElementName, "Final Value of Menu Exit State for CBIZ_MN_002: "+menuExsitState);
		data.setSessionData(Constants.S_MENU_SELCTION_KEY, data.getSessionData(Constants.S_BU)+":CBIZ_MN_002:"+menuExsitState);

		return strExitState;
	}
}