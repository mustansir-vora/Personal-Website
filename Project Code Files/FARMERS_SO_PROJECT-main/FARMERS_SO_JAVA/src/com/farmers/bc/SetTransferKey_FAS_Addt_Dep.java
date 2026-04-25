package com.farmers.bc;

import com.audium.server.session.ActionElementData;
import com.audium.server.voiceElement.ActionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class SetTransferKey_FAS_Addt_Dep extends ActionElementBase{

	@Override
	public void doAction(String name, ActionElementData data) throws Exception {
		String nextAction= Constants.EmptyString, menuExitState = Constants.EmptyString;
		String elementName = data.getCurrentElement();
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			nextAction = (String) data.getSessionData("NEXT_ACTION");
			
			if("FAS_ADD_DEPT".equalsIgnoreCase(nextAction)) {
				menuExitState = Constants.FASAD_MN_002_TechnicalSupport;
				caa.createMSPKey_NLU(caa, data, "FASAD_MN_002", "TECHNICAL SUPPORT");
			}else if("FARMER_PL_ACCT_TXR".equalsIgnoreCase(nextAction)) {
				menuExitState = Constants.FASAD_MN_002_Accounting;
				caa.createMSPKey_NLU(caa, data, "FASAD_MN_002", "ACCOUNTING");
			}else if("FAS_ADD_DEPT_FLOOD".equalsIgnoreCase(nextAction)) {
				menuExitState = Constants.FASAD_MN_001_Flood_Svc_Center;
				caa.createMSPKey_NLU(caa, data, "FASAD_MN_001", "FLOOD");
			}else if("FAS_ADD_DEPT_FNWL".equalsIgnoreCase(nextAction)) {
				menuExitState = Constants.FASAD_MN_001_FarmersNewWorldLife;
				caa.createMSPKey_NLU(caa, data, "FASAD_MN_001", "FARMERS NEW WORLD LIFE");
			}else if("FAS_ADD_DEPT_HELPPOINT".equalsIgnoreCase(nextAction)) {
				menuExitState = Constants.FASAD_MN_001_HelpPoint;
				caa.createMSPKey_NLU(caa, data, "FASAD_MN_001", "HELP POINT");
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
