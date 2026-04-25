package com.farmers.bc;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class FASAM_MN_002_VALUE extends DecisionElementBase{

	public String doDecision(String currElementName, DecisionElementData data)
			throws Exception {
		String strExitState=Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try{


			String strReturnValue = (String) data.getElementData("FASAM_MN_002_Call","Return_Value");
			data.addToLog(currElementName, " FASAM_MN_002 : Return_Value :: "+strReturnValue);
			data.setSessionData("FASAM_MN_002_VALUE", strReturnValue);
			if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
			} else if(strReturnValue.equalsIgnoreCase("Out Of Household")) {
				strExitState ="Out Of Household";
				caa.createMSPKey(caa, data, "FASAM_MN_002", "OUT OF HOUSEHOLD");
			}  else if(strReturnValue.equalsIgnoreCase("Add Or Remove Driver")) {
				strExitState ="Add Or Remove Driver";
				caa.createMSPKey(caa, data, "FASAM_MN_002", "ADD OR REMOVE DRIVER");
			} else if(strReturnValue.equalsIgnoreCase("Cancel A Policy")) {
				strExitState="Cancel A Policy";
				caa.createMSPKey(caa, data, "FASAM_MN_002", "CANCEL A POLICY");
			}else if(strReturnValue.equalsIgnoreCase("Something Else")) {
				strExitState="Something Else";
				caa.createMSPKey(caa, data, "FASAM_MN_002", "SOMETHING ELSE");
			}else if(strReturnValue.equalsIgnoreCase("Representative")) {
				strExitState = "representative";
			}

			else strExitState=Constants.ER;
		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in FASAM_MN_002_VALUE :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"FASAM_MN_002_VALUE :: "+strExitState);

//		String menuExitState = strExitState;
//		if(strExitState.contains(" ")) menuExitState = menuExitState.replaceAll(" ", "_");
//		if(null != (String)data.getSessionData("FASAM_MN_002_"+menuExitState) && !((String)data.getSessionData("FASAM_MN_002_"+menuExitState)).isEmpty()) menuExitState = (String)data.getSessionData("FASAM_MN_002_"+menuExitState);
//		data.addToLog(currElementName, "Final Value of Menu Exit State for FASAM_MN_002: "+menuExitState);
//		data.setSessionData(Constants.S_MENU_SELCTION_KEY, data.getSessionData(Constants.S_BU)+":FASAM_MN_002:"+menuExitState);
		return strExitState;
	}

}



