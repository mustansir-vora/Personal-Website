package com.farmers.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;
 
public class TFMP_MN_001_VALUE extends DecisionElementBase {
 
	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("TFMP_MN_001_Call", "Return_Value");
			data.addToLog(currElementName, " TFMP_MN_001_VALUE : Return_Value :: " + strReturnValue);
			data.setSessionData("TFMP_MN_001_VALUE", strReturnValue);
			
			if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
			} else if (strReturnValue.equalsIgnoreCase(Constants.S_CANCEL)) {
				strExitState = Constants.S_CANCEL;
			} else if (strReturnValue.equalsIgnoreCase(Constants.S_MAKEAPAYMENT)) {
				strExitState = Constants.S_MAKEAPAYMENT;
			} else {
				strExitState = Constants.ER;
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in TFMP_MN_001_VALUE :: " + e);
			caa.printStackTrace(e);
		}
	
		data.addToLog(currElementName, "TFMP_MN_001_VALUE :: " + strExitState);
		return strExitState;
	}
}