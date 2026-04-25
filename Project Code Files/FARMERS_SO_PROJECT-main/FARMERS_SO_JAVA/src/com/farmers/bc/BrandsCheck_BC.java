package com.farmers.bc;

import java.util.ArrayList;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class BrandsCheck_BC extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		String strExitStateMSP = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("SSAAH_MN_001_Call", "Return_Value");
			data.addToLog(currElementName, "SSAAH_MN_001_Call : Return_Value :: " + strReturnValue);

			String strBU = (String) data.getSessionData(Constants.S_BU);

			ArrayList<String> strBristolCode = (ArrayList<String>) data.getSessionData("BRISTOL_LOB_LIST");
			ArrayList<String>  strFarmersCode = (ArrayList<String>) data.getSessionData("FARMER_LOB_LIST");
			ArrayList<String>  strForemostCode = (ArrayList<String>) data.getSessionData("FOREMOST_LOB_LIST");
			ArrayList<String>  strFWSCode = (ArrayList<String>) data.getSessionData("FWS_LOB_LIST");
			ArrayList<String>  str21stCode = (ArrayList<String>) data.getSessionData("21st_LOB_LIST");

			data.addToLog(currElementName, " Current : strBU :: "+strBU);
			data.addToLog(currElementName, " A_BRISTOL_LOB : "+strBristolCode);
			data.addToLog(currElementName, " A_FARMERS_LOB : "+strFarmersCode);
			data.addToLog(currElementName, " A_FOREMOST_LOB : "+strForemostCode);
			data.addToLog(currElementName, " A_FWS_LOB : "+strFWSCode);
			data.addToLog(currElementName, " A_21ST_LOB : "+str21stCode);

			if (null != strBristolCode && null != strBU && strBristolCode.contains(strBU) && strReturnValue.equalsIgnoreCase(Constants.CLAIMS)) {
				strExitStateMSP = "BW Claims";
				data.addToLog(currElementName,"Brand is BW and intent is " + strReturnValue + " the string MSP is " + strExitStateMSP);
				strExitState = "BW";
				data.addToLog(currElementName,"The exist state " + strExitState + " if the brans is BW and intent is " + strReturnValue);
				caa.createMSPKey(caa, data, "SSAAH_MN_001", "BW CLAIMS");
			} else if (null != strBristolCode && null != strBU && strBristolCode.contains(strBU) && strReturnValue.equalsIgnoreCase(Constants.BILLING)) {
				strExitStateMSP = "BW Billing";
				data.addToLog(currElementName,"Brand is BW and intent is " + strReturnValue + " the string MSP is " + strExitStateMSP);
				strExitState = "BW";
				data.addToLog(currElementName,"The exist state " + strExitState + " if the brans is BW and intent is " + strReturnValue);
				caa.createMSPKey(caa, data, "SSAAH_MN_001", "BW BILLINGS");
			} else if (null != strForemostCode && null != strBU && strForemostCode.contains(strBU)
					&& strReturnValue.equalsIgnoreCase(Constants.BILLING)) {
				strExitStateMSP = "Specialty Billing";
				data.addToLog(currElementName,"Brand is Specialty and intent is " + strReturnValue + " the string MSP is " + strExitStateMSP);
				strExitState = "Specialty";
				data.addToLog(currElementName, "The exist state " + strExitState + " if the brans is Specialty and intent is " + strReturnValue);
				caa.createMSPKey(caa, data, "SSAAH_MN_001", "SPECIALTY BILLING");
			} else if (null != strForemostCode && null != strBU && strForemostCode.contains(strBU)
					&& strReturnValue.equalsIgnoreCase(Constants.CLAIMS)) {
				strExitStateMSP = "Specialty Claims";
				data.addToLog(currElementName,"Brand is Specialty and intent is " + strReturnValue + " the string MSP is " + strExitStateMSP);
				strExitState = "Specialty";
				data.addToLog(currElementName, "The exist state " + strExitState + " if the brans is Specialty and intent is " + strReturnValue);
				caa.createMSPKey(caa, data, "SSAAH_MN_001", "SPECIALTY CLAIMS");
			} else {
				strExitState = Constants.ER;
			}

		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in BrandsCheck_BC :: " + e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName, "BrandsCheck_BC :: " + strExitState);

		//		String menuExsitState = strExitStateMSP;
		//		if (strExitState.contains(" "))
		//			menuExsitState = menuExsitState.replaceAll(" ", "_");
		//		if (null != (String) data.getSessionData("SSAAH_MN_001_" + menuExsitState)
		//				&& !((String) data.getSessionData("SSAAH_MN_001_" + menuExsitState)).isEmpty())
		//			menuExsitState = (String) data.getSessionData("SSAAH_MN_001_" + menuExsitState);
		//		data.addToLog(currElementName, "Final Value of Menu Exit State for SSAAH_MN_001: " + menuExsitState);
		//		data.setSessionData(Constants.S_MENU_SELECTION_KEY,
		//				data.getSessionData(Constants.S_BU) + ":SSAAH_MN_001:" + menuExsitState);
		//		data.addToLog(currElementName,
		//				"S_MENU_SELECTION_KEY SSAAH_MN_001: " + (String) data.getSessionData("S_MENU_SELCTION_KEY"));

		return strExitState;
	}
}