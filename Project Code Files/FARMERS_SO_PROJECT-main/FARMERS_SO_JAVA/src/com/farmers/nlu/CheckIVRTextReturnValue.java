package com.farmers.nlu;


import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class CheckIVRTextReturnValue extends DecisionElementBase{



	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER, strReturnValue = Constants.EmptyString;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			//strReturnValue = data.getElementData("SharedIVRText", "RETURN_VALUE_FOR_NLU");
			strReturnValue = (String)data.getSessionData("RETURN_VALUE_FOR_NLU");
			if(strReturnValue!=null && !Constants.EmptyString.equalsIgnoreCase(strReturnValue) && !Constants.NA.equalsIgnoreCase(strReturnValue)) {
				data.addToLog(currElementName, "SharedIVRText - RETURN_VALUE_FOR_NLU :: " + strReturnValue );
			
				if("T".equalsIgnoreCase(strReturnValue)) {
					strExitState = "T";
				}else if("R".equalsIgnoreCase(strReturnValue)) {
					strExitState = "R";
				}else{
					// Value other than T or R
				}
				
			} else {
				data.addToLog(currElementName,"Return Value is either null or empty");
			}
		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in CheckIVRTextReturnValue :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"CheckIVRTextReturnValue :: "+strExitState);
		return strExitState;
	}

}
