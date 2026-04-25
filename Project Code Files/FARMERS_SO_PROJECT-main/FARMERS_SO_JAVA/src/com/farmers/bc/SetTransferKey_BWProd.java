package com.farmers.bc;

import com.audium.server.session.ActionElementData;
import com.audium.server.voiceElement.ActionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class SetTransferKey_BWProd extends ActionElementBase{

	@Override
	public void doAction(String name, ActionElementData data) throws Exception {
		String nextAction= Constants.EmptyString, menuExitState = Constants.EmptyString;
		String elementName = data.getCurrentElement();
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {
			nextAction = (String) data.getSessionData("NEXT_ACTION");
			
			if("POLICY_QUESTION".equalsIgnoreCase(nextAction)) {
				menuExitState = Constants.POLICY_QUESTIONS;
				caa.createMSPKey_NLU(caa, data, "BWPM_MN_001", "POLICY QUESTIONS");
			}else if("BW_PROD_CLAIMS".equalsIgnoreCase(nextAction)) {
				menuExitState = Constants.CLAIMS;
				caa.createMSPKey_NLU(caa, data, "BWPM_MN_001", "CLAIMS");
			}else if("BM_PROD_MM_COMMISSIONS".equalsIgnoreCase(nextAction)) {
				menuExitState = Constants.COMMISSIONS;
				caa.createMSPKey_NLU(caa, data, "BWPM_MN_001", "COMMISSIONS");
			}else if("TECH_SUP".equalsIgnoreCase(nextAction)) {
				menuExitState = Constants.TECHNICAL_SUPPORT;;
				caa.createMSPKey_NLU(caa, data, "BWPM_MN_001", "TECHNICAL SUPPORT");
			}else if("BW_PROD_MM_EXTENSION".equalsIgnoreCase(nextAction)) {
				menuExitState = Constants.EXTENSION;;
				caa.createMSPKey_NLU(caa, data, "BWPM_MN_001", "EXTENSION");
			}else {
				// Nothing to be done.
			}
		
			/*
			 * if(null != (String)data.getSessionData("BWPM_MN_001_"+menuExitState) &&
			 * !((String)data.getSessionData("BWPM_MN_001_"+menuExitState)).isEmpty())
			 * menuExitState = (String)data.getSessionData("BWPM_MN_001_"+menuExitState);
			 * data.addToLog(elementName,
			 * "Final Value of Menu Exit State for BWPM_MN_001_: "+menuExitState);
			 * data.setSessionData(Constants.S_MENU_SELCTION_KEY,
			 * data.getSessionData(Constants.S_BU)+":BWPM_MN_001_:"+menuExitState);
			 */
		}catch (Exception e) {
			// TODO: handle exception
		}
		
	}

}
