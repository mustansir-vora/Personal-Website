package com.farmers.bc;

import com.audium.server.session.ActionElementData;
import com.audium.server.voiceElement.ActionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class SetTransferKey_Foremost_mh extends ActionElementBase{

	@Override
	public void doAction(String name, ActionElementData data) throws Exception {
		String nextAction= Constants.EmptyString, menuExitState = Constants.EmptyString;
		String elementName = data.getCurrentElement();
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {
			nextAction = (String) data.getSessionData("NEXT_ACTION");
			
			if("FMST_DIRECT_MOBILEHOME".equalsIgnoreCase(nextAction)) {
				menuExitState = Constants.S_MOBILE_HOME;
				caa.createMSPKey_NLU(caa, data, "FAMM_MN_002", "MOBILE HOME");
			}else {
				// Nothing to be done.
			}
		
			/*
			 * if(null != (String)data.getSessionData("FAMM_MN_002_"+menuExitState) &&
			 * !((String)data.getSessionData("FAMM_MN_002_"+menuExitState)).isEmpty())
			 * menuExitState = (String)data.getSessionData("FAMM_MN_002_"+menuExitState);
			 * data.addToLog(elementName,
			 * "Final Value of Menu Exit State for FAMM_MN_002: "+menuExitState);
			 * data.setSessionData(Constants.S_MENU_SELCTION_KEY,
			 * data.getSessionData(Constants.S_BU)+":FAMM_MN_002_:"+menuExitState);
			 */
		}catch (Exception e) {
			// TODO: handle exception
		}
		
	}

}
