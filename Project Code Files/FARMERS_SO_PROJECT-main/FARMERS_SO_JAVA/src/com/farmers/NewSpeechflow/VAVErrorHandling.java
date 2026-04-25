package com.farmers.NewSpeechflow;

import com.audium.server.AudiumException;
import com.audium.server.session.ActionElementData;
import com.audium.server.voiceElement.ActionElementBase;

public class VAVErrorHandling extends ActionElementBase
{
	@Override
	public void doAction(String elementName, ActionElementData data) throws AudiumException {
		try
		{
			data.setSessionData("value", "error");	
			data.addToLog(data.getCurrentElement(), "Value Recieved from Virtual Agent Voice is error");
		}catch(Exception e)
		{
			data.addToLog(data.getCurrentElement(), "Exception : " + e);
		}
	}

}
