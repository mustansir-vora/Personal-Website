package com.farmers.bc;

import java.util.ArrayList;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class CheckLOB_BC extends DecisionElementBase{

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws Exception {
		String strExitState = Constants.ER, strBU = Constants.EmptyString;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			// TODO Auto-generated method stub
			ArrayList<String> strBristolCode = (ArrayList<String>) data.getSessionData("BRISTOL_LOB_LIST");
			ArrayList<String>  strForemostCode = (ArrayList<String>) data.getSessionData("FOREMOST_LOB_LIST");
			ArrayList<String>  strFWSCode = (ArrayList<String>) data.getSessionData("FWS_LOB_LIST");
			strBU = (String) data.getSessionData(Constants.S_BU);

			data.addToLog(currElementName, " PolicyLookup : strBU :: "+strBU);
			data.addToLog(currElementName, " BRISTOL_LOB_LIST : "+strBristolCode);
			data.addToLog(currElementName, " FWS_LOB_LIST : "+strFWSCode);
			data.addToLog(currElementName, " FOREMOST_LOB_LIST : "+strForemostCode);

			if(strBristolCode!=null && null != strBU && strBristolCode.contains(strBU)) {
				strExitState = "BW";
				data.setSessionData("S_TRANS_BACK_FROM_PMTS_BU", strExitState);
			} else if(strForemostCode!=null && strBU!=null && strForemostCode.contains(strBU)) {
				strExitState = "FOREMOST";
				data.setSessionData("S_TRANS_BACK_FROM_PMTS_BU", strExitState);
			} else if(strFWSCode!=null && strBU!=null && strFWSCode.contains(strBU)) {
				strExitState = "FWS";
				data.setSessionData("S_TRANS_BACK_FROM_PMTS_BU", strExitState);
			} else {
				strExitState = "T";
			}

			data.addToLog(currElementName, " VALUE OF S_TRANS_BACK_FROM_PMTS_BU : "+(String)data.getSessionData("S_TRANS_BACK_FROM_PMTS_BU"));
			data.addToLog(currElementName, " Exit State : "+strExitState);
		}catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host details for CheckLOB_BC  :: "+ e);
			caa.printStackTrace(e);
		}
		return strExitState;		
	}

}