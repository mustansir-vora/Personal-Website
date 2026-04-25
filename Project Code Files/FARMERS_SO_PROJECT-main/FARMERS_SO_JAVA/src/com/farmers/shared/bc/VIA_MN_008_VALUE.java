package com.farmers.shared.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class VIA_MN_008_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("VIA_MN_008_Call","Return_Value");
			data.setSessionData("VIA_MN_008_VALUE", strReturnValue);
			if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
			} else if(strReturnValue.equalsIgnoreCase(Constants.REPRESENTATIVE)) {
				data.setSessionData("LAST_MENU_NAME_REP", "VIA_MN_008");
				strExitState = Constants.REPRESENTATIVE;
			}  else if(strReturnValue.equalsIgnoreCase(Constants.STRING_YES)) {
				strExitState = Constants.STRING_YES;
			}  else if(strReturnValue.equalsIgnoreCase(Constants.STRING_NO)) {
				
				strExitState = Constants.STRING_NO;
			} else {
				strExitState = Constants.ER;
			} 
			data.addToLog(currElementName,"VIA_MN_008_VALUE :: "+strExitState);
		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in VIA_MN_008_VALUE :: "+e);
			caa.printStackTrace(e);
		}
		return strExitState;		
	}
}