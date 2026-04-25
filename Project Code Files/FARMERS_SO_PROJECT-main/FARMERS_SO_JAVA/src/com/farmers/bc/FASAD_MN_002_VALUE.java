package com.farmers.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class  FASAD_MN_002_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("FASAD_MN_002_Call","Return_Value");
			data.addToLog(currElementName, "  FASAD_MN_002_VALUE : Return_Value :: "+strReturnValue);
			data.setSessionData(Constants. FASAD_MN_002_VALUE, strReturnValue);
			if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
			} else if(strReturnValue.equalsIgnoreCase(Constants. FASAD_MN_002_Accounting)) {
				strExitState = Constants. FASAD_MN_002_Accounting;
				caa.createMSPKey(caa, data, "FASAD_MN_002", "ACCOUNTING");
			} else if(strReturnValue.equalsIgnoreCase(Constants.FASAD_MN_002_AgentServices )) {
				strExitState = Constants. FASAD_MN_002_AgentServices;
				data.addToLog(currElementName, "  Constant MainMenu Value :: "+Constants.FASAD_MN_002_MainMenu);
				caa.createMSPKey(caa, data, "FASAD_MN_002", "AGENCY SERVICES");
			} else if(strReturnValue.equalsIgnoreCase(Constants.FASAD_MN_002_MainMenu )) {
				strExitState = Constants. FASAD_MN_002_MainMenu;
			} else if(strReturnValue.equalsIgnoreCase(Constants. FASAD_MN_002_TechnicalSupport)) {
				strExitState = Constants. FASAD_MN_002_TechnicalSupport;
				caa.createMSPKey(caa, data, "FASAD_MN_002", "TECHNICAL SUPPORT");
			} else if(strReturnValue.equalsIgnoreCase(Constants. FASAD_MN_002_Underwriting)) {
				strExitState = Constants. FASAD_MN_002_Underwriting;
				caa.createMSPKey(caa, data, "FASAD_MN_002", "UNDERWRITING");
			} 
			else {
				strExitState = Constants.ER;
			}
		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in  FASAD_MN_002_VALUE :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName," FASAD_MN_002_VALUE :: "+strExitState);
		data.setSessionData(Constants.S_INTENET, strExitState);

//		String menuExsitState = strExitState;
//		if(strExitState.contains(" ")) menuExsitState = menuExsitState.replaceAll(" ", "_");
//		if(null != (String)data.getSessionData(" FASAD_MN_002_"+menuExsitState) && !((String)data.getSessionData(" FASAD_MN_002_"+menuExsitState)).isEmpty()) menuExsitState = (String)data.getSessionData(" FASAD_MN_002_"+menuExsitState);
//		data.addToLog(currElementName, "Value of  FASAD_MN_002 returned from Session : "+data.getSessionData(Constants. FASAD_MN_002_VALUE));
//		data.addToLog(currElementName, "Final Value of Menu Exit State for  FASAD_MN_002: "+menuExsitState);
//		if(null != menuExsitState && !menuExsitState.isEmpty()) data.setSessionData(Constants.S_MENU_SELCTION_KEY, data.getSessionData(Constants.S_BU)+": FASAD_MN_002:"+menuExsitState);

		return strExitState;
	}
}