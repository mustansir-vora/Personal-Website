package com.farmers.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class Valid_10_digit_ANI extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.STRING_NO;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			String ani = (String) data.getSessionData("S_ANI");
			if(ani.length() == 10) strExitState = Constants.STRING_YES;
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in Valid_10_digit_ANI :: "+e);
			caa.printStackTrace(e);
		}
		return strExitState;
	}

}
