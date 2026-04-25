package com.farmers.util;

import com.audium.server.session.ActionElementData;
import com.audium.server.voiceElement.ActionElementBase;

public class PreTransfer extends ActionElementBase {

	@Override
	public void doAction(String currElementName, ActionElementData data) throws Exception {

		try {
			String BE = (String) data.getSessionData(Constants.BE) != null ? (String) data.getSessionData(Constants.BE) : Constants.BE_DefaultValue;
			String DRFlag = (String) data.getSessionData(Constants.DRFlag) != null ? (String) data.getSessionData(Constants.DRFlag) : Constants.DR_DefaultValue;
			String destNum = (String) data.getSessionData(Constants.FinalDestNum) != null ? (String) data.getSessionData(Constants.FinalDestNum) : Constants.EmptyString;
			String OCallID = (String) data.getSessionData(Constants.getOCallID) != null ? (String) data.getSessionData(Constants.getOCallID) : Constants.EmptyString;
			String stateCode = (String) data.getSessionData(Constants.StateCode) != null ? (String) data.getSessionData(Constants.StateCode) : Constants.EmptyString;
			String BUID = data.getSessionData(Constants.BUID) == null || data.getSessionData(Constants.BUID).toString().equalsIgnoreCase("") || data.getSessionData(Constants.BUID).toString().equalsIgnoreCase("$session.params.strBUid") ? (String) data.getSessionData(Constants.getBUID) : (String) data.getSessionData(Constants.BUID);
			String GUID = (String) data.getSessionData(Constants.getCGUID) != null ? (String) data.getSessionData(Constants.getCGUID) : Constants.EmptyString;
			String DNIS = (String) data.getSessionData(Constants.getOriginalDNIS) != null ? (String) data.getSessionData(Constants.getOriginalDNIS) : Constants.EmptyString;
			String ANI = (String) data.getSessionData(Constants.getOriginalANI) != null ? (String) data.getSessionData(Constants.getOriginalANI) : Constants.EmptyString;
			String routingKey = (String) data.getSessionData(Constants.RoutingKey) != null ? (String) data.getSessionData(Constants.RoutingKey) : Constants.EmptyString;
			String activeLang = data.getSessionData(Constants.ActiveLang) == null || data.getSessionData(Constants.ActiveLang).toString().equalsIgnoreCase("") || data.getSessionData(Constants.ActiveLang).toString().equalsIgnoreCase("$session.params.strActiveLang") ? (String) data.getSessionData(Constants.Lang) : (String) data.getSessionData(Constants.ActiveLang);
			
			if (ANI.length() == 10) {
				ANI = "1" + ANI;
			}
			
			if (activeLang.equalsIgnoreCase(Constants.es_US)) {
				activeLang = Constants.SP;
			}
			else {
				activeLang = Constants.EN;
			}

			String VXML0=new StringBuffer(destNum).append('|').append(OCallID).append('|').append(activeLang).append('|').append(stateCode).append('|').append(BUID).append('|').append(BE).append('|').append(DRFlag).toString();
			String VXML1=new StringBuffer(GUID).append('|').append(DNIS).append('|').append(ANI).toString();
			String VXML2=new StringBuffer(routingKey).toString();
			
			data.addToLog(currElementName, "S_VXML_0 value = " + VXML0);
			data.addToLog(currElementName, "S_VXML_1 value = " + VXML1);
			data.addToLog(currElementName, "S_VXML_2 value = " + VXML2);
			
			data.setSessionData(Constants.S_VXML0, VXML0);
			data.setSessionData(Constants.S_VXML1, VXML1);
			data.setSessionData(Constants.S_VXML2, VXML2);
			
			data.addToLog(currElementName, "VXML values set into session");
			
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception occurred in PreTransfer :: Exception :: " + e);
		}
		
	}
	
	 

}
