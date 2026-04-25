package com.farmers.bc;

import com.audium.server.session.ActionElementData;
import com.audium.server.voiceElement.ActionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class SetTransferKey_21stSalesVanityFF extends ActionElementBase{

	@Override
	public void doAction(String name, ActionElementData data) throws Exception {
		String nextAction= Constants.EmptyString, menuExitState = Constants.EmptyString;
		String elementName = data.getCurrentElement();
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {
			nextAction = (String) data.getSessionData("NEXT_ACTION");
			
			if("21ST_SALES_EXTENSION".equalsIgnoreCase(nextAction)) {
				menuExitState = Constants.S_EXTENTION;
				caa.createMSPKey_NLU(caa, data, "TFFNF_MN_001", "EXTENSION");
			}else {
				// Nothing to be done.
			}
			/*
			 * if(null != (String)data.getSessionData("TFFNF_MN_001_"+menuExitState) &&
			 * !((String)data.getSessionData("TFFNF_MN_001_"+menuExitState)).isEmpty())
			 * menuExitState = (String)data.getSessionData("TFFNF_MN_001_"+menuExitState);
			 * data.addToLog(elementName,
			 * "Final Value of Menu Exit State for TFFNF_MN_001: "+menuExitState);
			 * data.setSessionData(Constants.S_MENU_SELCTION_KEY,
			 * data.getSessionData(Constants.S_BU)+":TFFNF_MN_001_:"+menuExitState);
			 */
		}catch (Exception e) {
			// TODO: handle exception
		}
		
	}

}
