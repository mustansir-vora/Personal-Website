package com.farmers.shared.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class GET_SMM_MN_003 extends DecisionElementBase 
{
	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException 
	{

		String strExitState = Constants.ER;
		String mspValue = "NM/NI";
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {



			String returnValue = (String) data.getElementData("SMM_MN_003_Call","Return_Value");
			data.addToLog(currElementName, "Menu ID : "+"SMM_MN_003"+" :: Menu Value : "+returnValue);
			data.setSessionData(Constants.SMM_MN_003_VALUE, returnValue);
			if(returnValue != null && !returnValue.equals("")) 
			{

				if(returnValue.equalsIgnoreCase(Constants.NOINPUT)) {
					strExitState = Constants.NOINPUT;
				} else if (returnValue.equalsIgnoreCase(Constants.REPRESENTATIVE)) {
					strExitState = Constants.REPRESENTATIVE;
				}else if(returnValue.equalsIgnoreCase(Constants.NOMATCH)) {
					strExitState = Constants.NOMATCH;
				} else if(returnValue.equalsIgnoreCase(Constants.STRING_YES)) {
					mspValue = "BILLING";
					strExitState = Constants.STRING_YES;
					caa.createMSPKey(caa, data, "SMM_MN_003", "BILLING");
				}  else if(returnValue.equalsIgnoreCase(Constants.STRING_NO)){
					strExitState = Constants.STRING_NO;
					caa.createMSPKey(caa, data, "SMM_MN_003", "NM/NI");
				} 
			}

		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in SMM_MN_003 :: "+e);
			caa.printStackTrace(e);
		}
		/*data.addToLog(currElementName,"SMM_MN_003 :: "+strExitState);
		String menuExsitState = strExitState;
		if(strExitState.contains(" ")) menuExsitState = menuExsitState.replaceAll(" ", "_");
		if(null != (String)data.getSessionData("SMM_MN_003_"+menuExsitState) && !((String)data.getSessionData("SMM_MN_003_"+menuExsitState)).isEmpty()) menuExsitState = (String)data.getSessionData("SMM_MN_003_"+menuExsitState);
		data.addToLog(currElementName, "Final Value of Menu Exit State for SMM_MN_003: "+menuExsitState);
		data.setSessionData(Constants.S_MENU_SELCTION_KEY, data.getSessionData(Constants.S_BU)+":SMM_MN_003:"+menuExsitState);*/
		return strExitState;

	}

}


