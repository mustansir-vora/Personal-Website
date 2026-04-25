package com.farmers.verint;

import org.apache.logging.log4j.core.LoggerContext;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.VerintAPI.VerintPauseResume;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class recordingDisable extends DecisionElementBase{

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String StrExitState = "ER";
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		data.setSessionData("VERINT_PAUSED", "false");
		try {
			//Verint start
			int conTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_CONN_TIMEOUT));
			int readTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_READ_TIMEOUT));
		
			String callerId = (String) data.getSessionData(Constants.S_CALLID);
			String verintUrl = (String) data.getSessionData("S_VERINT_URL");
		
			LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
			

			data.addToLog(currElementName, "Verint URL : " + verintUrl);
			VerintPauseResume objVerintPauseResume = new VerintPauseResume();
			data.addToLog(currElementName, "Verint 1 : ");
			boolean resp = objVerintPauseResume.verintPauseResume(verintUrl, "PauseRecord", callerId, context, conTimeout, readTimeout);
			data.addToLog(currElementName, "Verint 2 : " + resp);
				
			if (resp) {
				StrExitState = "SU";
				data.setSessionData("VERINT_PAUSED", "true");
			}
			
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in recordingDisable call  :: "+e);
			caa.printStackTrace(e);
		}
		
		return StrExitState;
	}
}
