package com.farmers.speechflow;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;

public class CheckPrefLang extends DecisionElementBase
{

	public String doDecision(String elementName, DecisionElementData data)
			throws AudiumException {

		String exitState="";
		try
		{
			String prefLang = (String) data.getSessionData("S_PREF_LANG");
			data.addToLog(data.getCurrentElement(), "Value of S_PREF_LANG : " +prefLang);
			if("EN".equalsIgnoreCase(prefLang))
			{
				exitState="ENG";
			}
			else{
				exitState="SPA";
			}
		}catch(Exception e)
		{
			data.addToLog(data.getCurrentElement(), "Exception : " + e);
		}
		return exitState;
	}

}
