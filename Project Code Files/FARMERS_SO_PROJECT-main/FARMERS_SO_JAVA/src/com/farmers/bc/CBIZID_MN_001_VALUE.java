package com.farmers.bc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class CBIZID_MN_001_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("CBIZID_MN_001_Call","Return_Value");
			data.addToLog(currElementName, " CBIZID_MN_001_Call : Return_Value :: "+strReturnValue);
			data.setSessionData(Constants.S_PRODUCER_CODE, strReturnValue);
			data.setSessionData(Constants.CBIZID_MN_001_VALUE, strReturnValue);
			String regex = "(.)*(\\d)(.)*";      
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(strReturnValue);
			if(matcher.matches()) {
				strExitState = Constants.SU;
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
			} else if(strReturnValue.equalsIgnoreCase(Constants.CBIZID_MN_001_NoAgentId)) {
				strExitState = Constants.CBIZID_MN_001_NoAgentId;
			} else if(strReturnValue.equalsIgnoreCase(Constants.REPRESENTATIVE)) {
				strExitState = Constants.REPRESENTATIVE;
			}
			else {
				strExitState = Constants.ER;
			}
		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in CBIZID_MN_001_VALUE :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"CBIZID_MN_001_VALUE :: "+strExitState);
		/*
		String menuExsitState = strExitState;
		if(strExitState.contains(" ")) menuExsitState = menuExsitState.replaceAll(" ", "_");
		if(null != (String)data.getSessionData("CBIZID_MN_001_"+menuExsitState) && !((String)data.getSessionData("CBIZID_MN_001_"+menuExsitState)).isEmpty()) menuExsitState = (String)data.getSessionData("CBIZID_MN_001_"+menuExsitState);
		data.addToLog(currElementName, "Final Value of Menu Exit State for CBIZID_MN_001: "+menuExsitState);
		data.setSessionData(Constants.S_MENU_SELCTION_KEY, data.getSessionData(Constants.S_BU)+":CBIZID_MN_001:"+menuExsitState);
		*/
		return strExitState;
	}

	public static void main(String[] args) {
		String strReturnValue = "q";
		String regex = "(.)*(\\d)(.)*";      
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(strReturnValue);
		System.out.println(matcher.matches());
	}
}