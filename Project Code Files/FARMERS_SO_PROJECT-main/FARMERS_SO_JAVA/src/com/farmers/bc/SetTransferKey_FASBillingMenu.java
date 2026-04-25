package com.farmers.bc;

import com.audium.server.session.ActionElementData;
import com.audium.server.voiceElement.ActionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class SetTransferKey_FASBillingMenu extends ActionElementBase{

	@Override
	public void doAction(String name, ActionElementData data) throws Exception {
		String nextAction= Constants.EmptyString, menuExitState = Constants.EmptyString;
		String elementName = data.getCurrentElement();
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			nextAction = (String) data.getSessionData("NEXT_ACTION");
			
			if("FAS_BILLING_REFUND".equalsIgnoreCase(nextAction)) {
				menuExitState = "Refunds";
				caa.createMSPKey_NLU(caa, data, "FASBM_MN_001", "REFUNDS");
			}else if("FAS_POLICY_BILLING_MISSPELLED".equalsIgnoreCase(nextAction)) {
				menuExitState="Misapplied Money";
				caa.createMSPKey_NLU(caa, data, "FASBM_MN_001", "MISAPPLIED MONEY");
			}else {
				// Nothing to be done.
			}
		
			/*
			 * if(null != (String)data.getSessionData("FASBM_MN_001_"+menuExitState) &&
			 * !((String)data.getSessionData("FASBM_MN_001_"+menuExitState)).isEmpty())
			 * menuExitState = (String)data.getSessionData("FASBM_MN_001_"+menuExitState);
			 * data.addToLog(elementName,
			 * "Final Value of Menu Exit State for FASBM_MN_001: "+menuExitState);
			 * data.setSessionData(Constants.S_MENU_SELCTION_KEY,
			 * data.getSessionData(Constants.S_BU)+":FASBM_MN_001_:"+menuExitState);
			 */
		}catch (Exception e) {
			// TODO: handle exception
		}
		
	}

}
