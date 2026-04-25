package com.farmers.bc;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class Chk_Renewal_Current_Balance extends DecisionElementBase{

	@Override
	public String doDecision(String arg0, DecisionElementData data) throws Exception {
		String strExitState = "ER";
		String strCurrentBalance = Constants.EmptyString;
		String strFullBalance = Constants.EmptyString;
		String currElementName = data.getCurrentElement();
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try{
			strFullBalance = (String) data.getSessionData("S_Renewal_Balance");
			strCurrentBalance = (String) data.getSessionData("S_Current_Balance");
			data.addToLog(currElementName, "Current balance :: " + strCurrentBalance + " :: Full balance :: " + strFullBalance );
			if(null != strFullBalance && (!"".equalsIgnoreCase(strCurrentBalance)) && Float.parseFloat(strFullBalance) > 0 && Float.parseFloat(strCurrentBalance) > 0) {
				strExitState = "SU";
				if(null != strCurrentBalance && (!"".equalsIgnoreCase(strCurrentBalance))) {
					strExitState = "SU";
				}
			}
		}catch(Exception e) {
			data.addToLog(currElementName,"Exception in Chk_Renewal_Current_Balance :: "+e);
			caa.printStackTrace(e);
		}
		return strExitState;
	}

}
