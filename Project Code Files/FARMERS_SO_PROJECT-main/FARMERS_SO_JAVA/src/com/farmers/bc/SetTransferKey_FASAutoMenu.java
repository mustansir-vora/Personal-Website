package com.farmers.bc;

import com.audium.server.session.ActionElementData;
import com.audium.server.voiceElement.ActionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class SetTransferKey_FASAutoMenu extends ActionElementBase{

	@Override
	public void doAction(String name, ActionElementData data) throws Exception {
		String nextAction= Constants.EmptyString, menuExitState = Constants.EmptyString;
		String elementName = data.getCurrentElement();
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			nextAction = (String) data.getSessionData("NEXT_ACTION");
			
			if("FAS_AUTO_UNDERWRITING".equalsIgnoreCase(nextAction)) {
				menuExitState ="Underwriting";
				caa.createMSPKey_NLU(caa, data, "FASAM_MN_001", "UNDERWRITING");
			}else if("FAS_ELIGIBILITY".equalsIgnoreCase(nextAction)) {
				menuExitState="Eligibility";
				caa.createMSPKey_NLU(caa, data, "FASAM_MN_001", "ELIGIBILITY");
			}else {
				// Nothing to be done.
			}
		
			/*
			 * if(null != (String)data.getSessionData("FASAM_MN_001_"+menuExitState) &&
			 * !((String)data.getSessionData("FASAM_MN_001_"+menuExitState)).isEmpty())
			 * menuExitState = (String)data.getSessionData("FASAM_MN_001_"+menuExitState);
			 * data.addToLog(elementName,
			 * "Final Value of Menu Exit State for FASAM_MN_001: "+menuExitState);
			 * data.setSessionData(Constants.S_MENU_SELCTION_KEY,
			 * data.getSessionData(Constants.S_BU)+":FASAM_MN_001_:"+menuExitState);
			 */
		}catch (Exception e) {
			// TODO: handle exception
		}
		
	}

}
