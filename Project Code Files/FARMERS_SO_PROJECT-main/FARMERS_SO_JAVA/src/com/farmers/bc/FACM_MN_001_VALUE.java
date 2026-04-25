package com.farmers.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class FACM_MN_001_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("FACM_MN_001_Call", "Return_Value");
			data.addToLog(currElementName, " FACM_MN_001_VALUE : Return_Value :: " + strReturnValue);
			data.setSessionData("FACM_MN_001_VALUE", strReturnValue);

			if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
				caa.createMSPKey(caa, data, "FACM_MN_001", "AGENT");
			} else if (strReturnValue.equalsIgnoreCase("Agent")) {
				strExitState = "Agent";
				caa.createMSPKey(caa, data, "FACM_MN_001", "AGENT");
				data.setSessionData(Constants.S_CALLLER_TYPE,"02");//CS1160011:All Brands-Agent caller type 02
				
			} else if (strReturnValue.equalsIgnoreCase(Constants.S_CUSTOMER)) {
				strExitState = Constants.S_CUSTOMER;
				caa.createMSPKey(caa, data, "FACM_MN_001", "CUSTOMER");
				data.setSessionData(Constants.S_CALLLER_TYPE,"01");//CS1160011:All Brands-Customer caller type 01
				data.setSessionData("S_AGENT_OPTED_NO", "Y");//CS1160011:All Brands-Customer caller type 01
				data.addToLog(currElementName,"S_AGENT_OPTED_NO :: "+data.getSessionData("S_AGENT_OPTED_NO"));//CS1160011:All Brands-Customer caller type 01
				
			}else if (strReturnValue.equalsIgnoreCase(Constants.S_LINEHOLDER)) {
				strExitState = Constants.S_LINEHOLDER;
				data.setSessionData("FM_TRANSFER_ROUTE", "BILLING");
				caa.createMSPKey(caa, data, "FACM_MN_001", "LIENHOLDER");
				data.setSessionData(Constants.S_CALLLER_TYPE,"03");
			} else if (strReturnValue.equalsIgnoreCase(Constants.S_REPRESENTATIVE)) {
				strExitState = Constants.S_REPRESENTATIVE;
				caa.createMSPKey(caa, data, "FACM_MN_001", "AGENT");
			} else if (strReturnValue.equalsIgnoreCase(Constants.S_DEFAULT)) {
				strExitState = Constants.S_DEFAULT;
				caa.createMSPKey(caa, data, "FACM_MN_001", "AGENT");
			}else {
				strExitState = Constants.ER;
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in FACM_MN_001_VALUE :: " + e);
			caa.printStackTrace(e);
		}

		//		String menuExsitState = strExitState;
		//		if (strExitState.contains(" "))
		//			menuExsitState = menuExsitState.replaceAll(" ", "_");
		//		if (null != (String) data.getSessionData("FACM_MN_001_" + menuExsitState)
		//				&& !((String) data.getSessionData("FACM_MN_001_" + menuExsitState)).isEmpty())
		//			menuExsitState = (String) data.getSessionData("FACM_MN_001_" + menuExsitState);
		//		data.addToLog(currElementName, "Final Value of Menu Exit State for FACM_MN_001: " + menuExsitState);
		//		data.setSessionData(Constants.S_MENU_SELECTION_KEY,
		//				data.getSessionData(Constants.S_BU) + ":FACM_MN_001:" + menuExsitState);

		data.addToLog(currElementName, "FACM_MN_001_VALUE :: " + strExitState);
		return strExitState;
	}
}