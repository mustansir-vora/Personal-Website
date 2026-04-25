	package com.farmers.shared.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class FDS_SBP_MN_002 extends DecisionElementBase 
{
	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException 
	{

		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {

			String strReturnValue = (String) data.getElementData("FDS_SBP_MN_002_Call","Return_Value");
			data.addToLog(currElementName, "Menu ID : "+"FDS_SBP_MN_002_VALUE"+" :: Menu Value : "+strReturnValue);
			data.setSessionData("FDS_SBP_MN_002_VALUE", strReturnValue);
			if(strReturnValue != null && !strReturnValue.equals("")) {
				//strExitState = strReturnValue;
				if (strReturnValue.equalsIgnoreCase(Constants.STRING_YES)) {
					strExitState = Constants.STRING_YES;
				} 
				else if (strReturnValue.equalsIgnoreCase(Constants.STRING_NO)) {
					strExitState = Constants.STRING_NO;
				}
				else if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
					strExitState = Constants.NOINPUT;
				}
				else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
					strExitState = Constants.NOMATCH;
				}
				else {
					strExitState = Constants.ER;
				}
			}

		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in SBP_MN_002 :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"SBP_MN_002 :: "+strExitState);
		return strExitState;

	}

}