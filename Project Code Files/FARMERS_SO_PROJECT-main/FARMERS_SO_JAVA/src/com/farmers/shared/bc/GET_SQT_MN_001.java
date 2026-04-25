package com.farmers.shared.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class GET_SQT_MN_001 extends DecisionElementBase 
{

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException 
	{

		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {

			String returnValue = (String) data.getElementData("SQT_MN_001_Call","Return_Value");
			data.addToLog(currElementName, "Menu ID : "+"SQT_MN_001"+" :: Menu Value : "+returnValue);
			data.setSessionData(Constants.SQT_MN_001_VALUE, returnValue);
			if(returnValue != null && !returnValue.equals("")) 
			{	
				if(returnValue.equalsIgnoreCase(Constants.REPRENSTATIVE)){
					strExitState = Constants.REPRENSTATIVE;	
					caa.createMSPKey(caa, data, "SQT_MN_001", Constants.REPRENSTATIVE);
				}else if(returnValue.equalsIgnoreCase(Constants.STRING_YES)) {
					strExitState = Constants.STRING_YES;
					caa.createMSPKey(caa, data, "SQT_MN_002", Constants.NEWQUOTE);
				}  else if(returnValue.equalsIgnoreCase(Constants.STRING_NO)){
					strExitState = Constants.STRING_NO;
					caa.createMSPKey(caa, data, "SQT_MN_001", strExitState);
				} else if(returnValue.equalsIgnoreCase(Constants.NOINPUT)) {
					strExitState = Constants.NOINPUT;
				} else if(returnValue.equalsIgnoreCase(Constants.NOMATCH)) {
					strExitState = Constants.NOMATCH;
				} 			
			}
			else {
				strExitState = Constants.ER;
			}

		}
		catch (Exception e) {
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"SQT_MN_001 :: "+strExitState);
		

		return strExitState;

	}

}

