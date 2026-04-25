package com.farmers.afterHoursCallBack;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class SAHCB_MN_001_VALUE  extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("SAHCB_MN_001_Call","Return_Value");
			data.setSessionData("SAHCB_MN_001_VALUE", strReturnValue);
			if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
			}
			else if(strReturnValue.equalsIgnoreCase(Constants.STRING_YES)) {
				strExitState = Constants.STRING_YES;
			}  else if(strReturnValue.equalsIgnoreCase(Constants.STRING_NO)) {
				
				strExitState = Constants.STRING_NO;
			} else {
				strExitState = Constants.ER;
				data.setSessionData("SAHCB_MN_001_VALUE", "ER");
			} 
			data.addToLog(currElementName,"SAHCB_MN_001_VALUE :: "+strExitState);
		}
		catch(Exception e) {
			data.addToLog(currElementName,"Exception in SAHCB_MN_001_VALUE :: "+e);
			caa.printStackTrace(e);
		}
		return strExitState;
	}
}
