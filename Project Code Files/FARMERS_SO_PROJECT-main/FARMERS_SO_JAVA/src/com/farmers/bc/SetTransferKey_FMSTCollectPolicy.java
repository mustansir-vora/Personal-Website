package com.farmers.bc;

import com.audium.server.session.ActionElementData;
import com.audium.server.voiceElement.ActionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class SetTransferKey_FMSTCollectPolicy extends ActionElementBase{
	@Override
	public void doAction(String name, ActionElementData data) throws Exception {
		String nextAction= Constants.EmptyString, menuExitState = Constants.EmptyString;
		String elementName = data.getCurrentElement();
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		boolean repFlag = false;
		try {
			
			if(data.getSessionData(Constants.NEXT_ACTION) != null) {
				nextAction = (String) data.getSessionData(Constants.NEXT_ACTION);
				if(nextAction.equalsIgnoreCase("FMST_REP")) {
					data.addToLog(data.getCurrentElement(), "Representative tag has been indentified. SO transferring the call.");
					caa.createMSPKey_NLU(caa, data, "FAMM_MN_001", "REPRESENTATIVE");
					repFlag = true;
				}
			}
			
			
			if(!repFlag) {
				menuExitState = (String) data.getSessionData("TRANSFER_KEY");
				if(menuExitState!=null && !Constants.EmptyString.equalsIgnoreCase(menuExitState)) {
					// takes menu exit state by default
				}else {
					// Mobile Home is set as default
					menuExitState = Constants.S_MOBILE_HOME;

				}
				caa.createMSPKey_NLU(caa, data,"FAMM_MN_002",menuExitState);
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

	}}
