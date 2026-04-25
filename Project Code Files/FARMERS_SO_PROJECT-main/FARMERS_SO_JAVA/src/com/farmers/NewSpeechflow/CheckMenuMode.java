package com.farmers.NewSpeechflow;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.session.ElementAPI;
import com.audium.server.voiceElement.DecisionElementBase;
import com.audium.server.xml.VoiceElementConfig;
import com.farmers.util.Constants;

public class CheckMenuMode extends DecisionElementBase{

	@Override
	public String doDecision(String elementName, DecisionElementData data) throws AudiumException {
		
		String ExitState = "OTHERS";
		Map<String, Map<String, Object>> mapMenuDetails = null;
		String getMenuID= (String) data.getElementData("NewSpeechDynamicMenu","MenuID");
		data.addToLog(data.getCurrentElement(), "getMenuID : " +getMenuID);
		String menuIdEventName=Constants.EmptyString,menuIdMode=Constants.EmptyString;
		
		try {
			
			mapMenuDetails = (Map<String, Map<String, Object>>) data.getApplicationAPI().getApplicationData("A_MENU_DETAILS");
			
			if(null != mapMenuDetails && mapMenuDetails.size()>1) {
				Map<String, Object> menuDetails = mapMenuDetails.get(getMenuID);
				data.addToLog(data.getCurrentElement(),"Specific Menu Hash Map Details : " + menuDetails);
				menuIdEventName = (String) menuDetails.get("MenuID_EventName");
				menuIdMode = (String) menuDetails.get("MenuID_Mode");
				data.addToLog(data.getCurrentElement(), "Value of Menu ID Event Name  : " +menuIdEventName);
				data.setSessionData("S_MENU_ID_EVENT_NAME", menuIdEventName);
				data.setSessionData("S_MENU_ID_MODE", menuIdMode);
			
				if ("PURE_DTMF".equalsIgnoreCase(menuIdMode)) {
					ExitState = "PURE_DTMF";
					data.addToLog(getMenuID, " : Exit State for Menu :: " + getMenuID + " :: " + ExitState);
				}
			}
		}
		catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			data.addToLog(data.getCurrentElement(), "Exception : " + e + " :: Full Exception :: " + sw.toString());
		}
		return ExitState;
	}
	
	

}
