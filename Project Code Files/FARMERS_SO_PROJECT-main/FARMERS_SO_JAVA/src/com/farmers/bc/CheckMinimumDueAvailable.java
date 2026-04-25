package com.farmers.bc;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.Constants;

public class CheckMinimumDueAvailable extends DecisionElementBase{

	@Override
	public String doDecision(String arg0, DecisionElementData data) throws Exception {
		String strExitState = Constants.ER;
		String strMinimumDueAmount = Constants.EmptyString;
		try {
			strMinimumDueAmount = (String) data.getSessionData("S_Current_Balance");
			data.addToLog(data.getCurrentElement(), "Minimum DUE from session :: " + strMinimumDueAmount);
			if(null != strMinimumDueAmount && !"".equalsIgnoreCase(strMinimumDueAmount) && !Constants.NA.equalsIgnoreCase(strMinimumDueAmount)) {
				data.addToLog(data.getCurrentElement(), "Minimum Due Amount Available :: " + strMinimumDueAmount);
				strExitState = "Yes";
			}else {
				data.addToLog(data.getCurrentElement(), " :: Minimum Due not available :: ");
				strExitState = "No";
			}
		}catch(Exception e) {
			data.addToLog(data.getCurrentElement(), "Exception in CheckMinimumDueAvailable :: " + e.getMessage());
		}
		return strExitState;
	}

}
