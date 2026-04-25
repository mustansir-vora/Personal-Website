package com.farmers.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class SetAudioPathWithSP extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {
			caa.setDefaultAudioPath("SP",(String)data.getSessionData(Constants.S_VXML_SERVER_IP));
			strExitState = Constants.SU;
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in SetAudioPathWithSP :: " + e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName, "SetAudioPathWithSP :: " + strExitState);
		return strExitState;
	}
}