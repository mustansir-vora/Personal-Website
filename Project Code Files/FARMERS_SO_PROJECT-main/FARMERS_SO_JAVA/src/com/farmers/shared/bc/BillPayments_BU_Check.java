package com.farmers.shared.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class BillPayments_BU_Check extends DecisionElementBase {

	String strReqBody = Constants.EmptyString;

	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);

		try {

			String strBU = (String) data.getSessionData(Constants.S_BU);

			String strBristolCode = (String) data.getApplicationAPI().getApplicationData("A_BRISTOL_LOB");
			String strFarmersCode = (String) data.getApplicationAPI().getApplicationData("A_FARMERS_LOB");
			String strForemostCode = (String) data.getApplicationAPI().getApplicationData("A_FOREMOST_LOB");
			String strFWSCode = (String) data.getApplicationAPI().getApplicationData("A_FWS_LOB");
			String str21stCode = (String) data.getApplicationAPI().getApplicationData("A_21ST_LOB");

			data.addToLog(currElementName, " PolicyLookup : strBU :: " + strBU);
			data.addToLog(currElementName, " A_BRISTOL_LOB : " + strBristolCode);
			data.addToLog(currElementName, " A_FARMERS_LOB : " + strFarmersCode);
			data.addToLog(currElementName, " A_FWS_LOB : " + strFWSCode);
			data.addToLog(currElementName, " A_21ST_LOB : " + str21stCode);

			data.addToLog(data.getCurrentElement(), "BU : " + strBU);
			if (strFarmersCode != null && strBU != null && strFarmersCode.contains(strBU)
					|| strBU.equalsIgnoreCase(Constants.S_API_BU_FDS) || strBU.contains(Constants.S_API_BU_FDS)
					|| Constants.S_API_BU_FDS.contains(strBU)) {
				strExitState = Constants.BU_FARMERS;
				strBU = Constants.BU_FARMERS;
			} else if (strForemostCode != null && strBU != null && strForemostCode.contains(strBU)) {
				strExitState = Constants.BU_FOREMOST;
				strBU = Constants.BU_FOREMOST;
			} else if (strFWSCode != null && strBU != null && (strFWSCode.contains(strBU) || strBU.contains(Constants.FWS))) {
				strExitState = Constants.BU_FWS;
				strBU = Constants.BU_FWS;
			} else if (str21stCode != null && strBU != null && str21stCode.contains(strBU)) {
				strExitState = Constants.BU_21ST;
				strBU = Constants.BU_21ST;
			}else if (strBristolCode != null && strBU != null && strBristolCode.contains(strBU)) {
				strExitState = Constants.BU_BW;
				strBU = Constants.BU_BW;
			} else {
				strExitState = Constants.BU_NOT_MATCH;
			}

			data.setSessionData("S_UNIQUE_BU", strBU);
			data.addToLog(data.getCurrentElement(), "strBU : "+strBU+"EXIT STATE : " + strExitState);
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception while forming BillPayments_BU_Check details  :: " + e);
			caa.printStackTrace(e);
		}
		return strExitState;
	}

}
