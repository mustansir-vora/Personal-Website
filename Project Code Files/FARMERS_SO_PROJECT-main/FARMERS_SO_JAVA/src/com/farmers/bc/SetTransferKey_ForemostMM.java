package com.farmers.bc;

import com.audium.server.session.ActionElementData;
import com.audium.server.voiceElement.ActionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class SetTransferKey_ForemostMM extends ActionElementBase{

	@Override
	public void doAction(String name, ActionElementData data) throws Exception {
		String nextAction= Constants.EmptyString, menuExitState = Constants.EmptyString;
		String elementName = data.getCurrentElement();
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			nextAction = (String) data.getSessionData("NEXT_ACTION");
			
			if("FOREMOST_STAR".equalsIgnoreCase(nextAction)) {
				menuExitState = "Foremost Star";
			}else if("FOREMOST_CLAIMS".equalsIgnoreCase(nextAction)) {
				menuExitState = Constants.S_CLAIMS;
				caa.createMSPKey_NLU(caa, data, "FAMM_MN_001", "CLAIMS");
			}else if("FMST_AGENT_POLICY_SERVICE".equalsIgnoreCase(nextAction)) {
				menuExitState = Constants.S_POLICYSERVICES;
				caa.createMSPKey_NLU(caa, data, "FAMM_MN_001", "POLICY SERVICES");
			}else if("SCHEDULES".equalsIgnoreCase(nextAction)) {
				menuExitState = Constants.S_SCHEDULES;;
			}else if("FMST_BILLING".equalsIgnoreCase(nextAction)) {
				menuExitState = Constants.S_BILLING;
				caa.createMSPKey_NLU(caa, data, "FAMM_MN_001", "BILLING");
			}else {
				// Nothing to be done.
			}
		
			/*
			 * if(null != (String)data.getSessionData("FAMM_MN_001_"+menuExitState) &&
			 * !((String)data.getSessionData("FAMM_MN_001_"+menuExitState)).isEmpty())
			 * menuExitState = (String)data.getSessionData("FAMM_MN_001_"+menuExitState);
			 * data.addToLog(elementName,
			 * "Final Value of Menu Exit State for FAMM_MN_001: "+menuExitState);
			 * data.setSessionData(Constants.S_MENU_SELCTION_KEY,
			 * data.getSessionData(Constants.S_BU)+":FAMM_MN_001_:"+menuExitState);
			 */
		}catch (Exception e) {
			// TODO: handle exception
		}
		
	}

}
