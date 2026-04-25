package com.farmers.bc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class KYCCF_MN_001_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("KYCCF_MN_001_Call","Return_Value");
			data.addToLog(currElementName, " KYCCF_MN_001_Call : Return_Value :: "+strReturnValue);
			data.setSessionData(Constants.KYCCF_MN_001_VALUE, strReturnValue);
			if(strReturnValue.equalsIgnoreCase("I don")) strReturnValue = strReturnValue.replace("\\'", "");
			String regex = "(.)*(\\d)(.)*";      
	        Pattern pattern = Pattern.compile(regex);
	        Matcher matcher = pattern.matcher(strReturnValue);
            if(matcher.matches()) {
            	data.setSessionData(Constants.S_MN_FEIN_SSN_VALUE, strReturnValue);
            	strExitState = Constants.SU;
            } else if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
			} else if(strReturnValue.equalsIgnoreCase(Constants.agent)) {
				strExitState = Constants.agent;
			} else if(strReturnValue.equalsIgnoreCase(Constants.Idontknow)) {
				strExitState = Constants.Idontknow;
			} else {
				strExitState = Constants.ER;
			}
		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in KYCCF_MN_001_VALUE :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"KYCCF_MN_001_VALUE :: "+strExitState);
		/*
		String menuExsitState = strExitState;
		if(strExitState.contains(" ")) menuExsitState = menuExsitState.replaceAll(" ", "_");
		if(null != (String)data.getSessionData("KYCCF_MN_001_"+menuExsitState) && !((String)data.getSessionData("KYCCF_MN_001_"+menuExsitState)).isEmpty()) menuExsitState = (String)data.getSessionData("KYCCF_MN_001_"+menuExsitState);
		data.addToLog(currElementName, "Final Value of Menu Exit State for KYCCF_MN_001: "+menuExsitState);
		data.setSessionData(Constants.S_MENU_SELCTION_KEY, data.getSessionData(Constants.S_BU)+":KYCCF_MN_001:"+menuExsitState);
		*/
		return strExitState;
	}
}