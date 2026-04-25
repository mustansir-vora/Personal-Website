package com.farmers.shared.bc;

import java.util.ArrayList;

import com.audium.server.session.ActionElementData;
import com.audium.server.voiceElement.ActionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class SetTransferKey extends ActionElementBase{

	@Override
	public void doAction(String name, ActionElementData data) throws Exception {
		String nextAction= Constants.EmptyString, menuExitState = Constants.EmptyString;
		String elementName = data.getCurrentElement();
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			nextAction = (String) data.getSessionData("NEXT_ACTION");
			String strBU = (String) data.getSessionData(Constants.S_BU);
		
			
			if("WEBSITE_HELP".equalsIgnoreCase(nextAction)) {
				menuExitState = Constants.WEBSITEHELP;
				caa.createMSPKey_NLU(caa, data, "SMM_MN_001", "WEBSITEHELP/PASSWORDRESET");
			}else if("AGENT_INFO".equalsIgnoreCase(nextAction)) {
				menuExitState = Constants.AGENTINFORMATIONS;
				caa.createMSPKey_NLU(caa, data, "SMM_MN_001", "AGENTINFO/CONTACT AN AGENT");
			}else if("CANCEL_POLICY".equalsIgnoreCase(nextAction)) {
				menuExitState = Constants.CANCELPOLICY;
				caa.createMSPKey_NLU(caa, data, "SMM_MN_001", "CANCELPOLICY");
			}else if("CLAIMS_ROADSIDE_ASST".equalsIgnoreCase(nextAction)) {
				menuExitState = Constants.CLAIMS;
				caa.createMSPKey_NLU(caa, data, "SMM_MN_001", "CLAIMS");
			}else if("SHARED_MAIN_MENU_REP".equalsIgnoreCase(nextAction)) {
				menuExitState = Constants.REPRENSTATIVE;
				caa.createMSPKey_NLU(caa, data, "SMM_MN_001", "REPRESENTATIVE");
			}else {
				
				// Nothing to be done.
			}
		
			/*
			 * if(null != (String)data.getSessionData("SMM_MN_001_"+menuExitState) &&
			 * !((String)data.getSessionData("SMM_MN_001_"+menuExitState)).isEmpty())
			 * menuExitState = (String)data.getSessionData("SMM_MN_001_"+menuExitState);
			 * data.addToLog(elementName,
			 * "Final Value of Menu Exit State for SMM_MN_001: "+menuExitState);
			 * data.setSessionData(Constants.S_MENU_SELCTION_KEY,
			 * data.getSessionData(Constants.S_BU)+":SMM_MN_001:"+menuExitState);
			 */
		}catch (Exception e) {
			// TODO: handle exception
		}
		
	}

}
