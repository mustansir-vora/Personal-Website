package com.farmers.shared.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class GET_SQT_MN_002 extends DecisionElementBase 
{

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException 
	{

		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {

			String returnValue = (String) data.getElementData("SQT_MN_002_Call","Return_Value");
			data.addToLog(currElementName, "Menu ID : "+"SQT_MN_002"+" :: Menu Value : "+returnValue);
			data.setSessionData(Constants.SQT_MN_002_VALUE, returnValue);
			if(returnValue != null && !returnValue.equals("")) 
			{
				if(returnValue.equalsIgnoreCase(Constants.NEWQUOTE)){
					strExitState = Constants.NEWQUOTE;	
					caa.createMSPKey(caa, data, "SQT_MN_002", Constants.NEWQUOTE);
				}else if(returnValue.equalsIgnoreCase(Constants.POLICYCHANGE)){
					strExitState = Constants.POLICYCHANGE;	
					caa.createMSPKey(caa, data, "SQT_MN_002", Constants.POLICYCHANGE);
				}else if(returnValue.equalsIgnoreCase(Constants.POLICYQUESTION)){
					strExitState = Constants.POLICYQUESTION;	
					caa.createMSPKey(caa, data, "SQT_MN_002", Constants.POLICYQUESTION);
				}else if(returnValue.equalsIgnoreCase(Constants.NOINPUT)) {
					strExitState = Constants.NOINPUT;
				}else if(returnValue.equalsIgnoreCase(Constants.NOMATCH)) {	
					strExitState = Constants.NOMATCH;
				}else {
					strExitState = Constants.ER;
				}
			}
		}
		catch (Exception e) {

			data.addToLog(currElementName,"Exception in SQT_MN_002 :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"SQT_MN_002 :: "+strExitState);
		return strExitState;
	}

}
