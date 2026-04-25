package com.farmers.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class RepRequestCheckTries_BC extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		int repRequestTries = 0, reqRequestMaxTries = 2;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);

		try {
			repRequestTries = (int) data.getSessionData("REP_TRIES");
			repRequestTries = repRequestTries + 1;
			data.addToLog(currElementName, "RepRequestCheckTries_BC :: repRequestTries : " + repRequestTries);
			if(repRequestTries<=reqRequestMaxTries) {
				strExitState = "RETRY";
				data.setSessionData("REP_TRIES", repRequestTries);
				data.addToLog(currElementName, "RepRequestCheckTries_BC :: Retry");
			}else {
				strExitState = "MAXTRY";
				data.setSessionData("REP_TRIES", 0);
				data.addToLog(currElementName, "RepRequestCheckTries_BC :: MaxTry Exceeded");
			}
			
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in RepRequestCheckTries_BC :: " + e);
			caa.printStackTrace(e);
		}

		data.addToLog(currElementName, "RepRequestCheckTries_BC :: " + strExitState);
		return strExitState;
	}

}