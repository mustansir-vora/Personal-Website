package com.farmers.shared.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class GET_SBP_MN_007 extends DecisionElementBase 
{
	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException 
	{

		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {

			String strReturnValue = (String) data.getElementData("SBP_MN_007_Call","Return_Value");
			data.addToLog(currElementName, "Menu ID : "+"SBP_MN_007"+" :: Menu Value : "+strReturnValue);
			data.setSessionData("SBP_MN_007_VALUE", strReturnValue);
			if(strReturnValue != null && !strReturnValue.equals("")) {
				strExitState = strReturnValue;
				if (strReturnValue.equalsIgnoreCase(Constants.NEWQUOTE)) {
					strExitState = Constants.NEWQUOTE;
				}else if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
					data.setSessionData("NoMatch", "0");
					data.setSessionData("NoInput", "1");
					strExitState = Constants.NOINPUT;
					
				}else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
					data.setSessionData("NoMatch", "1");
					data.setSessionData("NoInput", "0");
					strExitState = Constants.NOINPUT;
				}
			}

			data.addToLog(data.getCurrentElement(), "Exit State : "+strReturnValue);
		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in SBP_MN_007 :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"SBP_MN_007 :: "+strExitState);
		return strExitState;

	}

}