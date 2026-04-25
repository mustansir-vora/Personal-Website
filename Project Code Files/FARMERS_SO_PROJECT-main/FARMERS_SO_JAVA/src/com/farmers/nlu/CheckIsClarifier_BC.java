package com.farmers.nlu;


import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class CheckIsClarifier_BC extends DecisionElementBase{



	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		String strClarifier = Constants.EmptyString; 
		try {
			strClarifier = (String) data.getSessionData(Constants.IS_CLARIFIER);
			if(strClarifier!=null && !Constants.EmptyString.equalsIgnoreCase(strClarifier)) {
				data.addToLog(currElementName, " Clarifier :: " + strClarifier );
				if(!Constants.NA.equalsIgnoreCase(strClarifier)) {
					strExitState = Constants.AVAILABLE;
					data.setSessionData(Constants.CLARIFIER_PP, strClarifier);
					data.setSessionData(Constants.VXMLParam1, strClarifier);
					data.addToLog(currElementName,"Clarifier prompt applicable and text is : " + strClarifier);
					
				
				}else {
					data.addToLog(currElementName,"Clarifier is not applicable");
					strExitState = strClarifier;
				}
			} else {
				data.addToLog(currElementName,"Clarifier is either null or empty");
			}
		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in CheckIsClarifier_BC :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"CheckIsClarifier_BC :: "+strExitState);
		return strExitState;
	}

}
