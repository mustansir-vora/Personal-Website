package com.farmers.shared.bc;

import java.util.ArrayList;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class BU_Check_MDM extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
	
		
		try {

			String dnisbrand = (String) data.getSessionData(Constants.S_BU);
//			String strBristolCode = (String) data.getApplicationAPI().getApplicationData("A_KYCBA_BRISTOLWEST_LOB");
//			String strFarmersCode = (String) data.getApplicationAPI().getApplicationData("A_FARMERS_LOB");
//			String strForemostCode = (String) data.getApplicationAPI().getApplicationData("A_KYCBA_FOREMOST_LOB");
//			String strFWSCode = (String) data.getApplicationAPI().getApplicationData("A_KYCBA_FWS_LOB");
//			String str21stCode = (String) data.getApplicationAPI().getApplicationData("A_KYCBA_21ST_LOB");

			ArrayList<String> strBristolCode = (ArrayList<String>) data.getSessionData("BRISTOL_LOB_LIST");
			ArrayList<String> strFarmersCode = (ArrayList<String>) data.getSessionData("FARMER_LOB_LIST");
			ArrayList<String> strForemostCode = (ArrayList<String>) data.getSessionData("FOREMOST_LOB_LIST");
			ArrayList<String> strFWSCode = (ArrayList<String>) data.getSessionData("FWS_LOB_LIST");


			data.addToLog(currElementName, " PolicyLookup : strBU :: " + dnisbrand);
			data.addToLog(currElementName, " A_BRISTOL_LOB : " + strBristolCode);
			data.addToLog(currElementName, " A_FARMERS_LOB : " + strFarmersCode);
			data.addToLog(currElementName, " A_FOREMOST_LOB : " + strForemostCode);
			data.addToLog(currElementName, " A_FWS_LOB : " + strFWSCode);
			

			if (dnisbrand != null && strFarmersCode != null && (strFarmersCode.contains(dnisbrand))) {
				strExitState = "FDS";
				data.setSessionData("S_FLAG_FDS_BU", Constants.STRING_YES);
				data.addToLog("S_FLAG_FDS_BU: ", (String) data.getSessionData("S_FLAG_FDS_BU"));
				caa.createMSPKey(caa, data, "WATANILOOKUP", "TRANSFER");
			} else if (dnisbrand != null && strBristolCode != null && strBristolCode.contains(dnisbrand)) {
				strExitState = "BW";
				data.setSessionData("S_FLAG_BW_BU", Constants.STRING_YES);
				data.addToLog("S_FLAG_BW_BU: ", (String) data.getSessionData("S_FLAG_BW_BU"));
				caa.createMSPKey(caa, data, "WATANILOOKUP", "TRANSFER");
			} else if (dnisbrand != null && strForemostCode != null && strForemostCode.contains(dnisbrand)) {
				strExitState = "FM";
				data.setSessionData("S_FLAG_FOREMOST_BU", Constants.STRING_YES);
				data.addToLog("S_FLAG_FOREMOST_BU: ", (String) data.getSessionData("S_FLAG_FOREMOST_BU"));
				caa.createMSPKey(caa, data, "WATANILOOKUP", "TRANSFER");
			} else if (dnisbrand != null && strFWSCode != null && strFWSCode.contains(dnisbrand)) {
				strExitState = "FWS";
				data.setSessionData("S_FLAG_FWS_BU", Constants.STRING_YES);
				data.addToLog("S_FLAG_FWS_BU: ", (String) data.getSessionData("S_FLAG_FWS_BU"));
				caa.createMSPKey(caa, data, "WATANILOOKUP", "TRANSFER");
			}
			

			

			data.addToLog(currElementName, "dnisbrand :: " + dnisbrand);
		} catch (Exception e) {
			data.addToLog(currElementName,
					"Exception while forming  BU_Check MDM AniLookup details  :: " + e);
			caa.printStackTrace(e);
		}

		return strExitState;
	}
}

