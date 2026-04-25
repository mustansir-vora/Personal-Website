package com.farmers.shared.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class FDS_SBP_MN_007 extends DecisionElementBase 
{
	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException 
	{

		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {

			String strReturnValue = (String) data.getElementData("FDS_SBP_MN_007_Call","Return_Value");
			data.addToLog(currElementName, "Menu ID : "+"FDS_SBP_MN_007"+" :: Menu Value : "+strReturnValue);
			data.setSessionData("FDS_SBP_MN_007_VALUE", strReturnValue);
			if(strReturnValue != null && !strReturnValue.equals("")) {
				strExitState = strReturnValue;
				if (strReturnValue.equalsIgnoreCase(Constants.NEWQUOTE)) {
					strExitState = Constants.NEWQUOTE;
				}
				else if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
					strExitState = Constants.NOINPUT;
					
				}
				else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
					strExitState = Constants.NOMATCH;
				}
			}

			data.addToLog(data.getCurrentElement(), "Exit State : "+strReturnValue);
		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in FWS_SBP_MN_007 :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"FWS_SBP_MN_007 :: "+strExitState);
		return strExitState;

	}

}