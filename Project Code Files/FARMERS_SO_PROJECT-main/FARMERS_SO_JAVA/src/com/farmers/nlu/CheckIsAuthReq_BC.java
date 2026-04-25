package com.farmers.nlu;


import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class CheckIsAuthReq_BC extends DecisionElementBase{



	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		String strIsAuthReq = Constants.EmptyString; 
		try {
			strIsAuthReq = (String) data.getSessionData(Constants.IS_AUTHREQ);
			if(strIsAuthReq!=null && !Constants.EmptyString.equalsIgnoreCase(strIsAuthReq)) {
				data.addToLog(currElementName, " Is Auth Required :: " + strIsAuthReq );
				strExitState = strIsAuthReq;
			} else {
				data.addToLog(currElementName,"Is Auth Required is either null or empty");
			}
		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in CheckIsAuthReq_BC :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"CheckIsAuthReq_BC :: "+strExitState);
		return strExitState;
	}

}
