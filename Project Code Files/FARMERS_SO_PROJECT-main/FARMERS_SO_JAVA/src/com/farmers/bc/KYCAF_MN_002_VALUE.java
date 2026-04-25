package com.farmers.bc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class KYCAF_MN_002_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("KYCAF_MN_002_Call","Return_Value");
			data.addToLog(currElementName, " KYCAF_MN_002_Call : Return_Value :: "+strReturnValue);
			data.setSessionData(Constants.S_PRODUCER_CODE, strReturnValue);
			data.setSessionData(Constants.KYCAF_MN_002_VALUE, strReturnValue);
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
			}else if(strReturnValue.equalsIgnoreCase(Constants.S_REPRESENTATIVE)) {
				strExitState = Constants.S_REPRESENTATIVE;
			}
			else {
				strExitState = Constants.ER;
			}
		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in KYCAF_MN_002_VALUE :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"KYCAF_MN_002_VALUE :: "+strExitState);
		/*
		String menuExsitState = strExitState;
		if(strExitState.contains(" ")) menuExsitState = menuExsitState.replaceAll(" ", "_");
		if(null != (String)data.getSessionData("KYCAF_MN_002_"+menuExsitState) && !((String)data.getSessionData("KYCAF_MN_002_"+menuExsitState)).isEmpty()) menuExsitState = (String)data.getSessionData("KYCAF_MN_002_"+menuExsitState);
		data.addToLog(currElementName, "Final Value of Menu Exit State for KYCAF_MN_001: "+menuExsitState);
		data.setSessionData(Constants.S_MENU_SELCTION_KEY, data.getSessionData(Constants.S_BU)+":KYCAF_MN_002:"+menuExsitState);
		*/
		return strExitState;
	}
}