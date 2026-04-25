package com.farmers.nlu;


import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class CheckNextAction_BC extends DecisionElementBase{



	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		String nextAction = Constants.EmptyString; 
		try {
			
			nextAction = (String) data.getSessionData(Constants.NEXT_ACTION);
			if(nextAction!=null && !Constants.EmptyString.equalsIgnoreCase(nextAction)) {
				data.addToLog(currElementName, " Next Action :: " + nextAction );
				strExitState = Constants.DONE;
			} else {
				data.addToLog(currElementName,"Next Action is either null or empty");
			}
		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in CheckNextAction_BC :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"CheckNextAction_BC :: "+strExitState);
		return strExitState;
	}

}
