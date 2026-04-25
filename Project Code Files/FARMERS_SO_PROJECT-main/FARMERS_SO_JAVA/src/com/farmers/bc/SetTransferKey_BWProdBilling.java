package com.farmers.bc;

import com.audium.server.session.ActionElementData;
import com.audium.server.voiceElement.ActionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class SetTransferKey_BWProdBilling extends ActionElementBase{

	@Override
	public void doAction(String name, ActionElementData data) throws Exception {
		String nextAction= Constants.EmptyString, menuExitState = Constants.EmptyString;
		String elementName = data.getCurrentElement();
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {
			nextAction = (String) data.getSessionData("NEXT_ACTION");
			
			if("BM_PROD_BILLING_COMMISSIONS".equalsIgnoreCase(nextAction)) {
				menuExitState = Constants.COMMISSIONS;
				caa.createMSPKey_NLU(caa, data, "BWBM_MN_001", "COMMISSIONS");
			}else if("REFUND".equalsIgnoreCase(nextAction)) {
				menuExitState = Constants.REFUNDS;
				caa.createMSPKey_NLU(caa, data, "BWBM_MN_001", "REFUNDS");
			}else if("OTHER_BILLING".equalsIgnoreCase(nextAction)) {
				menuExitState = Constants.OTHER_BILLING;
				caa.createMSPKey_NLU(caa, data, "BWBM_MN_001", "OTHER BILLING");
			}else if("POLICY_STATUS".equalsIgnoreCase(nextAction)) {
				menuExitState = Constants.POLICY_STATUS;
				caa.createMSPKey_NLU(caa, data, "BWBM_MN_001", "POLICY STATUS");
			}else {
				// Nothing to be done.
			}
			
			/*
			 * if(null != (String)data.getSessionData("BWBM_MN_001_"+menuExitState) &&
			 * !((String)data.getSessionData("BWBM_MN_001_"+menuExitState)).isEmpty())
			 * menuExitState = (String)data.getSessionData("BWBM_MN_001_"+menuExitState);
			 * data.addToLog(elementName,
			 * "Final Value of Menu Exit State for BWBM_MN_001: "+menuExitState);
			 * data.setSessionData(Constants.S_MENU_SELCTION_KEY,
			 * data.getSessionData(Constants.S_BU)+":BWBM_MN_001_:"+menuExitState);
			 */
		}catch (Exception e) {
			// TODO: handle exception
		}
		
	}

}
