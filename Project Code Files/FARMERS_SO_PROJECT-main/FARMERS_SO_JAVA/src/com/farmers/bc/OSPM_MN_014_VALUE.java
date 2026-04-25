package com.farmers.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class OSPM_MN_014_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("OSPM_MN_014_Call", "Return_Value");
			data.addToLog(currElementName, "OSPM_MN_014_Call : Return_Value :: " + strReturnValue);
			data.setSessionData("OSPM_MN_014_VALUE", strReturnValue);

			if (strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NO_INPUT;
			} else if (strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NO_MATCH;
				caa.createMSPKey(caa, data, "OSPM_MN_014", "CUSTOMER");
			} else if (strReturnValue.equalsIgnoreCase(Constants.AGENT)) {
				strExitState = Constants.AGENT;
				data.setSessionData(Constants.S_CALLLER_TYPE,"02");//CS1160011:FWS ARC-Agent caller type 02
			} else if (strReturnValue.equalsIgnoreCase(Constants.LIENHOLDER)) {
				strExitState = Constants.LIENHOLDER;
				caa.createMSPKey(caa, data, "OSPM_MN_014", "LIENHOLDER");
				data.setSessionData(Constants.S_CALLLER_TYPE,"03");
			} else if (strReturnValue.equalsIgnoreCase(Constants.CUSTOMER)) {
				strExitState = Constants.CUSTOMER;
				caa.createMSPKey(caa, data, "OSPM_MN_014", "CUSTOMER");
				data.setSessionData(Constants.S_CALLLER_TYPE,"01");//CS1160011:FWS ARC-Customer caller type 01
				data.setSessionData("S_AGENT_OPTED_NO", "Y");//CS1160011:All Brands-Customer caller type 01
				data.addToLog(currElementName,"S_AGENT_OPTED_NO :: "+data.getSessionData("S_AGENT_OPTED_NO"));//CS1160011:All Brands-Customer caller type 01
				
			} else if (strReturnValue.equalsIgnoreCase(Constants.REPRESENTATIVE)) {
				strExitState = Constants.REPRESENTATIVE;
				caa.createMSPKey(caa, data, "OSPM_MN_014", "CUSTOMER");
			}else {
				strExitState = Constants.ER;
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in OSPM_MN_014_VALUE :: " + e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName, "OSPM_MN_014_VALUE :: " + strExitState);

		//		String S_BU = (String) data.getSessionData(Constants.S_BU);
		//		String finalS_BU = S_BU.toUpperCase();
		//		String menuExsitState = strExitState;
		//		if(null != strExitState && (!Constants.REPRESENTATIVE.equalsIgnoreCase(strExitState)) && (!Constants.NO_MATCH.equalsIgnoreCase(strExitState))
		//				&& (!Constants.NO_INPUT.equalsIgnoreCase(strExitState))) {
		//			if (strExitState.contains(" "))
		//				menuExsitState = menuExsitState.replaceAll(" ", "_");
		//			if (null != (String) data.getSessionData("OSPM_MN_014_" + menuExsitState)
		//					&& !((String) data.getSessionData("OSPM_MN_014_" + menuExsitState)).isEmpty())
		//				menuExsitState = (String) data.getSessionData("OSPM_MN_014_" + menuExsitState);
		//			data.addToLog(currElementName, "Final Value of Menu Exit State for OSPM_MN_014: " + menuExsitState);
		//			data.setSessionData(Constants.S_MENU_SELECTION_KEY,
		//					finalS_BU + ":OSPM_MN_014:" + menuExsitState);
		//			data.addToLog(currElementName,
		//					"S_MENU_SELECTION_KEY OSPM_MN_014: " + (String) data.getSessionData("S_MENU_SELCTION_KEY"));
		//		}else {
		//			data.addToLog(currElementName, "Exitstate is either representative or NoMatch or NoInput, so skipping the MSP setting :: " + strExitState);
		//		}
		return strExitState;
	}
}