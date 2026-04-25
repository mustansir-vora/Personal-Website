package com.farmers.bc;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class FWSARC_MN_002_VALUE extends DecisionElementBase{

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws Exception {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		
		try {
			
			String returnValue = (String) data.getElementData("FWSARC_MN_002_Call", "Return_Value");
			data.addToLog(currElementName, currElementName + " :: DTMF Menu Return Value :: " +returnValue);
			data.setSessionData(currElementName, returnValue);
			
			if(returnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
			}
			else if(returnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			}
			else if (returnValue.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_1)) {
				strExitState = Constants.DTMF_KEY_PRESS_1;
				caa.createMSPKey(caa, data, Constants.FWSARC_MN_002, Constants.FWSARS_POLICIES);
			}
			else if (returnValue.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_2)) {
				strExitState = Constants.DTMF_KEY_PRESS_2;
				caa.createMSPKey(caa, data, Constants.FWSARC_MN_002, Constants.FWSARS_NEW_BUSINESS);
			}
			else if (returnValue.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_3)) {
				strExitState = Constants.DTMF_KEY_PRESS_3;
				caa.createMSPKey(caa, data, Constants.FWSARC_MN_002, Constants.FWSARC_AGENT_360_POLICIES);
			}
			else if (returnValue.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_4)) {
				strExitState = Constants.DTMF_KEY_PRESS_4;
				caa.createMSPKey(caa, data, Constants.FWSARC_MN_002, Constants.FWSARC_AGENT_360_NEW_BUSINESS);
			}
			else if (returnValue.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_5)) {
				strExitState = Constants.DTMF_KEY_PRESS_5;
				caa.createMSPKey(caa, data, Constants.FWSARC_MN_002, Constants.FWSARC_PAK_II_OPEN);
			}
			else if (returnValue.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_6)) {
				strExitState = Constants.DTMF_KEY_PRESS_6;
				caa.createMSPKey(caa, data, Constants.FWSARC_MN_002, "UNDERWRITING APPROVAL");
			}
			else if (returnValue.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_7)) {
				strExitState = Constants.DTMF_KEY_PRESS_7;
				caa.createMSPKey(caa, data, Constants.FWSARC_MN_002, Constants.FWSARC_PWRESET);
			}
			else if (returnValue.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_8)) {
				strExitState = Constants.DTMF_KEY_PRESS_8;
				caa.createMSPKey(caa, data, Constants.FWSARC_MN_002, Constants.FWSARC_CLAIMS);
			}
			/*
			else if (returnValue.equalsIgnoreCase(Constants.DTMF_KEY_PRESS_0)) {
				strExitState = Constants.DTMF_KEY_PRESS_0;
				caa.createMSPKey(caa, data, Constants.FWSARC_MN_002, Constants.S_REPRESENTATIVE);
			}
			*/
		}
		catch (Exception e) {
			data.addToLog(currElementName, "Exception in " + currElementName + " Menu Selection Option Mapping :: " + e);
			caa.printStackTrace(e);
		}
		
		return strExitState;
	}

}
