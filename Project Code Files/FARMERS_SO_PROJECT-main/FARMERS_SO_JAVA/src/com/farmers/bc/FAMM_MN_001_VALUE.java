package com.farmers.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class FAMM_MN_001_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("FAMM_MN_001_Call", "Return_Value");
			data.addToLog(currElementName, " FAMM_MN_001_VALUE : Return_Value :: " + strReturnValue);
			data.setSessionData("FAMM_MN_001_VALUE", strReturnValue);

			if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
				caa.createMSPKey(caa, data, "FAMM_MN_001", "REPRESENTATIVE");
			}else if (strReturnValue.equalsIgnoreCase(Constants.S_POLICYSERVICES)) {
				strExitState = Constants.S_POLICYSERVICES;
				data.setSessionData(Constants.S_INTENT, strReturnValue);
				caa.createMSPKey(caa, data, "FAMM_MN_001", "POLICY SERVICES");
			} else if (strReturnValue.equalsIgnoreCase(Constants.S_BILLING)) {
				strExitState = Constants.S_BILLING;
				data.setSessionData(Constants.S_INTENT, strReturnValue);
				data.setSessionData("FM_TRANSFER_ROUTE", "BILLING");
				data.setSessionData("INTENT", "BILLING AND PAYMENTS");
				caa.createMSPKey(caa, data, "FAMM_MN_001", "BILLING");
			}else if (strReturnValue.equalsIgnoreCase("Foremost Star")) {
				strExitState = "Foremost Star";
				data.setSessionData(Constants.S_INTENT, strReturnValue);
				caa.createMSPKey(caa, data, "FAMM_MN_001", "FOREMOST STAR");
			}else if (strReturnValue.equalsIgnoreCase(Constants.S_CLAIMS)) {
				strExitState = Constants.S_CLAIMS;
				data.setSessionData(Constants.S_INTENT, strReturnValue);
				caa.createMSPKey(caa, data, "FAMM_MN_001", "CLAIMS");
			}else if (strReturnValue.equalsIgnoreCase(Constants.S_SUPPLIES)) {
				strExitState = Constants.S_SUPPLIES;
				data.setSessionData(Constants.S_INTENT, strReturnValue);
				caa.createMSPKey(caa, data, "FAMM_MN_001", "SUPPLIES");
			}else if (strReturnValue.equalsIgnoreCase(Constants.S_ANTIQUEAUTO)) {
				strExitState = Constants.S_ANTIQUEAUTO;
				data.setSessionData(Constants.S_INTENT, strReturnValue);
				caa.createMSPKey(caa, data, "FAMM_MN_001", "ANTIQUE AUTO");
			}else if (strReturnValue.equalsIgnoreCase(Constants.S_MAKEAPAYMENT)) {
				strExitState = Constants.S_MAKEAPAYMENT;
				data.setSessionData("FM_TRANSFER_ROUTE", "PAYMENT");
				data.setSessionData(Constants.S_INTENT, strReturnValue);
				data.setSessionData("INTENT", "BILLING AND PAYMENTS");
				caa.createMSPKey(caa, data, "FAMM_MN_001", "MAKE A PAYMENT");
			}else if (strReturnValue.equalsIgnoreCase(Constants.S_SCHEDULES)) {
				strExitState = Constants.S_SCHEDULES;
				data.setSessionData(Constants.S_INTENT, strReturnValue);
			}else if (strReturnValue.equalsIgnoreCase(Constants.S_REPRESENTATIVE)) {
				strExitState = Constants.S_REPRESENTATIVE;
				caa.createMSPKey(caa, data, "FAMM_MN_001", "REPRESENTATIVE");
			}else if (strReturnValue.equalsIgnoreCase(Constants.S_DEFAULT)) {
				strExitState = Constants.S_DEFAULT;
				caa.createMSPKey(caa, data, "FAMM_MN_001", "REPRESENTATIVE");
			} 
			else {
				strExitState = Constants.ER;
			}



		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in FAMM_MN_001_VALUE :: " + e);
			caa.printStackTrace(e);
		}

		//		String menuExsitState = strExitState;
		//		if (strExitState.contains(" ")) {
		//			menuExsitState = menuExsitState.replaceAll(" ", "_");
		//		}
		//			
		//		if (null != (String) data.getSessionData("FAMM_MN_001_" + menuExsitState)
		//				&& !((String) data.getSessionData("FAMM_MN_001_" + menuExsitState)).isEmpty()) {
		//			menuExsitState = (String) data.getSessionData("FAMM_MN_001_" + menuExsitState);
		//			data.addToLog(currElementName, "Final Value of Menu Exit State for FAMM_MN_001: " + menuExsitState);
		//			data.setSessionData(Constants.S_MENU_SELECTION_KEY,
		//					data.getSessionData(Constants.S_BU) + ":FAMM_MN_001:" + menuExsitState);
		//		}


		data.addToLog(currElementName, "FAMM_MN_001_VALUE :: " + strExitState);
		return strExitState;
	}
}