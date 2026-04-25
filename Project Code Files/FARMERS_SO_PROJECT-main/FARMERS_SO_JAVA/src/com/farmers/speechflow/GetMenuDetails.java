package com.farmers.speechflow;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;

public class GetMenuDetails extends DecisionElementBase
{

	public String doDecision(String elementName, DecisionElementData data)
			throws AudiumException {

		String exitState="";
		try
		{
			String returnValue = (String) data.getSessionData("Return_Value");
			data.addToLog(data.getCurrentElement(), "Value of returnValue : " +returnValue);
			
		}catch(Exception e)
		{
			data.addToLog(data.getCurrentElement(), "Exception : " + e);
		}
		return exitState;
	}

}
