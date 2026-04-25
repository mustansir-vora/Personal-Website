package com.farmers.fws.coverage;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class GetValueFromMN001 extends DecisionElementBase{

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			String returnValue = (String) data.getElementData("FWSRCD_MN_001_Call","Return_Value");
			data.addToLog(currElementName, "Menu ID : FWSRCD_MN_001 :: Menu Value : "+returnValue);
			if(returnValue != null && !returnValue.equals("")) {
				
				if (returnValue.equalsIgnoreCase(Constants.NOINPUT)) {
					strExitState = Constants.NOINPUT;
				}
				else if (returnValue.equalsIgnoreCase(Constants.NOMATCH)) {
					strExitState = Constants.NOMATCH;
				}
				else if(returnValue.equalsIgnoreCase(Constants.REPRENSTATIVE)) {
					caa.createMSPKey(caa, data, "FWSRCD_MN_001", Constants.REPRESENTATIVE);
					strExitState = "Representative";
				}
				else if (returnValue.equalsIgnoreCase(Constants.MAINMENU)) {
					strExitState = Constants.MAINMENU;
				}
				else if (returnValue.equalsIgnoreCase(Constants.HANGUP)) {
					strExitState = Constants.HANGUP;
				}
			}

		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in FWSRCD_MN_001_Call :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"KYCCF_MN_001_VALUE :: "+strExitState);
		return strExitState;
	}
}


