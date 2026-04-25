package com.farmers.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;
 
public class TFSP_MN_002_VALUE extends DecisionElementBase {
 
	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("TFSP_MN_002_Call", "Return_Value");
			data.addToLog(currElementName, " TFSP_MN_002_VALUE : Return_Value :: " + strReturnValue);
			data.setSessionData("TFSP_MN_002_VALUE", strReturnValue);
			
			 if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
					strExitState = Constants.NOINPUT;
				} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
					strExitState = Constants.NOMATCH;
			} else if (strReturnValue.equalsIgnoreCase(Constants.S_MAINMENU)) {
				strExitState = Constants.S_MAINMENU;
			} else if (strReturnValue.equalsIgnoreCase(Constants.S_HANGUP)) {
				strExitState = Constants.S_HANGUP;
			} else if (strReturnValue.equalsIgnoreCase(Constants.S_REPEAT)) {
				strExitState = Constants.S_REPEAT;
			}else {
				strExitState = Constants.ER;
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in TFSP_MN_002_VALUE :: " + e);
			caa.printStackTrace(e);
		}
		
		data.addToLog(currElementName, "TFSP_MN_002_VALUE :: " + strExitState);
		return strExitState;
	}
}