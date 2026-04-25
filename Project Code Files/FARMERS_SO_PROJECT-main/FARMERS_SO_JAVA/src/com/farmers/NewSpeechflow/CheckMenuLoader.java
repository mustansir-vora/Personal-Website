package com.farmers.NewSpeechflow;
 
import java.util.Map;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.Constants;

public class CheckMenuLoader extends DecisionElementBase
{
 
	public String doDecision(String elementName, DecisionElementData data)
			throws AudiumException {
 
		String exitState="ER";
		Map<String, Map<String, Object>> mapMenuDetails = null;
		String getMenuID= (String) data.getElementData("NewSpeechDynamicMenu","MenuID");
		data.addToLog(data.getCurrentElement(), "getMenuID : " +getMenuID);
		String menuIdEventName=Constants.EmptyString,menuIdMode=Constants.EmptyString;
		try
		{
			data.setSessionData("SECURE_FLAG", "false");
			String globalConfig = (String) data.getApplicationAPI().getApplicationData("A_GLOBAL_CONFIG");
			data.addToLog(data.getCurrentElement(), "Value of A_GLOBAL_CONFIG : " +globalConfig);
			data.setSessionData("S_GLOBAL_CONFIG", globalConfig);
 
			mapMenuDetails = (Map<String, Map<String, Object>>) data.getApplicationAPI().getApplicationData("A_MENU_DETAILS");
			//data.addToLog(data.getCurrentElement(),"Menu Hash Map Details : " + mapMenuDetails);
			if(null != mapMenuDetails && mapMenuDetails.size()>1)
			{
				Map<String, Object> menuDetails = mapMenuDetails.get(getMenuID);
				data.addToLog(data.getCurrentElement(),"Specific Menu Hash Map Details : " + menuDetails);
				menuIdEventName = (String) menuDetails.get("MenuID_EventName");
				menuIdMode = (String) menuDetails.get("MenuID_Mode");
				data.addToLog(data.getCurrentElement(), "Value of Menu ID Event Name  : " +menuIdEventName);
				data.setSessionData("S_MENU_ID_EVENT_NAME", menuIdEventName);
				data.setSessionData("S_MENU_ID_MODE", menuIdMode);
			}
			//Secure Data 
			String secureEventID = (String) data.getApplicationAPI().getApplicationData("A_SECURE_EVENTS");
			data.addToLog(data.getCurrentElement(), "A_SECURE_EVENTS  : " +secureEventID);
			if(secureEventID!=null && menuIdEventName!=null && secureEventID.contains(menuIdEventName)) {
				data.setSessionData("SECURE_FLAG", "true");
			}else {
				data.setSessionData("SECURE_FLAG", "false");
			}
			data.addToLog(data.getCurrentElement(), "Secure flag : " +data.getSessionData("SECURE_FLAG"));
			exitState="done";
		}catch(Exception e)
		{
			data.addToLog(data.getCurrentElement(), "Exception : " + e);
		}
		return exitState;
	}
 
}