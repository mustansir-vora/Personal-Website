package com.farmers.nlu;


import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class CheckIsImmediateTxf_BC extends DecisionElementBase{



	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		String isImmediateTxf = Constants.EmptyString; 
		try {
			isImmediateTxf = (String) data.getSessionData(Constants.IS_IMMEDIATE_TXF);
			if(isImmediateTxf!=null && !Constants.EmptyString.equalsIgnoreCase(isImmediateTxf)) {
				data.addToLog(currElementName, " Is Immediate Transfer :: " + isImmediateTxf );
				strExitState = isImmediateTxf;
			} else {
				data.addToLog(currElementName,"Immediate Transfer is either null or empty");
			}
		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in CheckIsImmediateTxf_BC :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"CheckIsImmediateTxf_BC :: "+strExitState);
		return strExitState;
	}

}
