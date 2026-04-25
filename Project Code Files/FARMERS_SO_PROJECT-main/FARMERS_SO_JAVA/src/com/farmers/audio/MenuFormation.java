package com.farmers.audio;

import java.util.HashMap;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class MenuFormation extends DecisionElementBase {

	@Override
	public String doDecision(String elementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		String strElementName = Constants.EmptyString;
		CommonAPIAccess caa = null; 
		HashMap hmAudio = null;
		String strMenuId = null;
		HashMap<String, String> menuDetailaMap = null;
		try {
			caa=CommonAPIAccess.getInstance(data);
			strElementName = data.getCurrentElement();
			strMenuId = (String) strElementName.split(Constants._Formation)[0];
			hmAudio = (HashMap) data.getApplicationAPI().getApplicationData(Constants.A_AUDIO_MAP);
			String strGrammer = (String) hmAudio.get(strMenuId+Constants._DTMF);
			menuDetailaMap = new HashMap<String, String>();
			menuDetailaMap.put(Constants.MenuID, strMenuId);
			menuDetailaMap.put(Constants.MenuID_Event, strMenuId+Constants._Event);
			menuDetailaMap.put(Constants.MenuID_InitialPrompt, (String) hmAudio.get(strMenuId));
			menuDetailaMap.put(Constants.MenuID_NoInputPrompt_1, (String) data.getSessionData(Constants.S_NOINPUT_PROMPT));
			menuDetailaMap.put(Constants.MenuID_NoInputPrompt_2, (String) data.getSessionData(Constants.S_NOINPUT_PROMPT));
			menuDetailaMap.put(Constants.MenuID_NoMatchPrompt_1, (String) data.getSessionData(Constants.S_NOMATCH_PROMPT));
			menuDetailaMap.put(Constants.MenuID_NoMatchPrompt_2, (String) data.getSessionData(Constants.S_NOMATCH_PROMPT));
			menuDetailaMap.put(Constants.MenuID_Tries, (String) (strGrammer.split(":")[3]));
			menuDetailaMap.put(Constants.MenuID_DTMF_Grammar, (String) (strGrammer.split(":")[2]));
			data.setSessionData(Constants.S_MENUMAP, menuDetailaMap);
			strExitState = Constants.SU;
		} catch (Exception e) {
			data.addToLog(strElementName,"Exception in MenuFormation :: "+e);
			caa.printStackTrace(e);
		}
		return strExitState;
	}
	
}