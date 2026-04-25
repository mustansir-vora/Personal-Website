package com.farmers.bc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class CIDA_MN_003_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("CIDA_MN_003_Call","Return_Value");
			data.addToLog(currElementName, " CIDA_MN_003_Call : Return_Value :: "+strReturnValue);
			data.setSessionData(Constants.CIDA_MN_003_VALUE, strReturnValue);
			String regex = "(.)*(\\d)(.)*";      
	        Pattern pattern = Pattern.compile(regex);
	        Matcher matcher = pattern.matcher(strReturnValue);
            if(matcher.matches()) {
            	data.setSessionData(Constants.S_ZIP_CODE, strReturnValue);
            	strExitState = Constants.SU;
            } else if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
			} else if(strReturnValue.equalsIgnoreCase(Constants.CIDA_MN_003_NoZip)) {
				strExitState = Constants.CIDA_MN_003_NoZip;
			} else {
				strExitState = Constants.ER;
			}
		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in CIDA_MN_003_VALUE :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"CIDA_MN_003_VALUE :: "+strExitState);
		/*
		String menuExsitState = strExitState;
		if(strExitState.contains(" ")) menuExsitState = menuExsitState.replaceAll(" ", "_");
		if(null != (String)data.getSessionData("CIDA_MN_003_"+menuExsitState) && !((String)data.getSessionData("CIDA_MN_003_"+menuExsitState)).isEmpty()) menuExsitState = (String)data.getSessionData("CIDA_MN_003_"+menuExsitState);
		data.addToLog(currElementName, "Final Value of Menu Exit State for CIDA_MN_003: "+menuExsitState);
		data.setSessionData(Constants.S_MENU_SELCTION_KEY, data.getSessionData(Constants.S_BU)+":CIDA_MN_003:"+menuExsitState);
		*/
		return strExitState;
	}
}