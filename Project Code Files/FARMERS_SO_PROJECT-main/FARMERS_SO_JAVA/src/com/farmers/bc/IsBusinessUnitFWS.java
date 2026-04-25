package com.farmers.bc;

import java.util.ArrayList;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class IsBusinessUnitFWS extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = "OTHERS";
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {

			String strBU = (String)data.getSessionData(Constants.S_BU);

			ArrayList<String>  strFWSCode = (ArrayList<String>) data.getSessionData("FWS_LOB_LIST");
			ArrayList<String>  str21stCode = (ArrayList<String>) data.getSessionData("21st_LOB_LIST");

			data.addToLog(currElementName, " Current : strBU :: "+strBU);
			data.addToLog(currElementName, " A_FWS_LOB : "+strFWSCode);
			data.addToLog(currElementName, " A_21ST_LOB : "+str21stCode);


			if(strFWSCode!=null && strBU!=null && (strFWSCode.contains(strBU))) {
				strExitState = Constants.BU_FWS;
				caa.createMSPKey(caa, data, "SACI_HOST_01", "DEFAULT");
			} else if(str21stCode!=null && strBU!=null && str21stCode.contains(strBU)) {
				strExitState = Constants.BU_21ST;
				caa.createMSPKey(caa, data, "SACI_HOST_01", "DEFAULT");
			} else {

			}

		} catch (Exception e) {
			data.addToLog(data.getCurrentElement(),"Exception in SMM_MN_003 :: "+e);
			caa.printStackTrace(e);
		}


		return strExitState;
	}
}