package com.farmers.bc;

import com.audium.server.session.ActionElementData;
import com.audium.server.voiceElement.ActionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class SetTransferKey_FASHomeMenu extends ActionElementBase{

	@Override
	public void doAction(String name, ActionElementData data) throws Exception {
		String nextAction= Constants.EmptyString, menuExitState = Constants.EmptyString;
		String elementName = data.getCurrentElement();
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			nextAction = (String) data.getSessionData("NEXT_ACTION");
			
			if("FAS_POLICY_HOME_INSPECTION".equalsIgnoreCase(nextAction)) {
				menuExitState = "Inspections";
				caa.createMSPKey_NLU(caa, data, "FASHM_MN_001", "INSPECTIONS");
			}else {
				// Nothing to be done.
			}
		
			/*
			 * if(null != (String)data.getSessionData("FASHM_MN_001_"+menuExitState) &&
			 * !((String)data.getSessionData("FASHM_MN_001_"+menuExitState)).isEmpty())
			 * menuExitState = (String)data.getSessionData("FASHM_MN_001_"+menuExitState);
			 * data.addToLog(elementName,
			 * "Final Value of Menu Exit State for FASHM_MN_001: "+menuExitState);
			 * data.setSessionData(Constants.S_MENU_SELCTION_KEY,
			 * data.getSessionData(Constants.S_BU)+":FASHM_MN_001_:"+menuExitState);
			 */
		}catch (Exception e) {
			// TODO: handle exception
		}
		
	}

}
