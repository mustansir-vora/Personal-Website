package com.farmers.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class FASAD_MN_001_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("FASAD_MN_001_Call","Return_Value");
			data.addToLog(currElementName, " FASAD_MN_001_VALUE : Return_Value :: "+strReturnValue);
			data.setSessionData(Constants.FASAD_MN_001_VALUE, strReturnValue);
			if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
			} else if(strReturnValue.equalsIgnoreCase(Constants.FASAD_MN_001_AgentSuport)) {
				strExitState = Constants.FASAD_MN_001_AgentSuport;
				//				caa.createMSPKey(caa, data, "FASAD_MN_001", "AGENT SUPPORT");
			} else if(strReturnValue.equalsIgnoreCase(Constants.FASAD_MN_001_BristolWest)) {
				strExitState = Constants.FASAD_MN_001_BristolWest;
				caa.createMSPKey(caa, data, "FASAD_MN_001", "BRISTOLWEST");
			} else if(strReturnValue.equalsIgnoreCase(Constants.FASAD_MN_001_Commercial)) {
				strExitState = Constants.FASAD_MN_001_Commercial;
				caa.createMSPKey(caa, data, "FASAD_MN_001", "COMMERCIAL");
			} else if(strReturnValue.equalsIgnoreCase(Constants.FASAD_MN_001_Extension)) {
				strExitState = Constants.FASAD_MN_001_Extension;
				caa.createMSPKey(caa, data, "FASAD_MN_001", "DIAL AN EXTENSION");
			} else if(strReturnValue.equalsIgnoreCase(Constants.FASAD_MN_001_FarmersNewWorldLife)) {
				strExitState = Constants.FASAD_MN_001_FarmersNewWorldLife;
				caa.createMSPKey(caa, data, "FASAD_MN_001", "FARMERS NEW WORLD LIFE");
			} else if(strReturnValue.equalsIgnoreCase(Constants.FASAD_MN_001_Flood_Svc_Center)) {
				strExitState = Constants.FASAD_MN_001_Flood_Svc_Center;
				caa.createMSPKey(caa, data, "FASAD_MN_001", "FLOOD");
			} else if(strReturnValue.equalsIgnoreCase(Constants.FASAD_MN_001_ForemostSpecialty)) {
				strExitState = Constants.FASAD_MN_001_ForemostSpecialty;
				caa.createMSPKey(caa, data, "FASAD_MN_001", "FOREMOST SPECIALTY");
			} 
			else if(strReturnValue.equalsIgnoreCase(Constants.FASAD_MN_001_HelpPoint)) {
				strExitState = Constants.FASAD_MN_001_HelpPoint;
				caa.createMSPKey(caa, data, "FASAD_MN_001", "HELP POINT");
			} else if(strReturnValue.equalsIgnoreCase("Claims")) {
				strExitState = "Claims";
				caa.createMSPKey(caa, data, "FASAD_MN_001", "HELP POINT");
			} 
			else {
				strExitState = Constants.ER;
			}
		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in FASAD_MN_001_VALUE :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"FASAD_MN_001_VALUE :: "+strExitState);
		data.setSessionData(Constants.S_INTENET, strExitState);

		//		String menuExsitState = strExitState;
		//		if(strExitState.contains(" ")) menuExsitState = menuExsitState.replaceAll(" ", "_");
		//		if(null != (String)data.getSessionData("FASAD_MN_001_"+menuExsitState) && !((String)data.getSessionData("FASAD_MN_001_"+menuExsitState)).isEmpty()) menuExsitState = (String)data.getSessionData("FASAD_MN_001_"+menuExsitState);
		//		data.addToLog(currElementName, "Value of FASAD_MN_001 returned from Session : "+data.getSessionData(Constants.FASAD_MN_001_VALUE));
		//		data.addToLog(currElementName, "Final Value of Menu Exit State for FASAD_MN_001: "+menuExsitState);
		//		if(null != menuExsitState && !menuExsitState.isEmpty()) data.setSessionData(Constants.S_MENU_SELCTION_KEY, data.getSessionData(Constants.S_BU)+":FASAD_MN_001:"+menuExsitState);

		return strExitState;
	}
}