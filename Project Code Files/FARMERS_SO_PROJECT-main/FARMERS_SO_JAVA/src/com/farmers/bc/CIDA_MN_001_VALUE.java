package com.farmers.bc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class CIDA_MN_001_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("CIDA_MN_001_Call","Return_Value");
			data.addToLog(currElementName, " CIDA_MN_001_Call Before : Return_Value :: "+strReturnValue);
			data.setSessionData(Constants.CIDA_MN_001_VALUE, strReturnValue);
			if (!strReturnValue.isEmpty()) {
				strReturnValue = Character.toUpperCase(strReturnValue.charAt(0)) + strReturnValue.substring(1);
			}
		
			String regex = "(.)*(\\d)(.)*";      
	        Pattern pattern = Pattern.compile(regex);
	        Matcher matcher = pattern.matcher(strReturnValue);
            if(matcher.matches()) {
            	if(strReturnValue.contains("-")) strReturnValue =strReturnValue.split("\\-")[0];
            	if(strReturnValue.length()==10 && strReturnValue.startsWith("3")) {
            		strReturnValue=strReturnValue.replaceFirst("3", "F");
            	}
            	data.setSessionData(Constants.S_ACCNUM, strReturnValue);
            	data.setSessionData(Constants.S_IS_ACCNO_PROVIDED, Constants.TRUE);
            	data.addToLog(currElementName, " CIDA_MN_001_Call After : Return_Value :: "+strReturnValue);
            	strExitState = Constants.SU;
            } else  if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
			} else if(strReturnValue.equalsIgnoreCase(Constants.CIDA_MN_001_RepresentativeRequest)) {
				strExitState = Constants.CIDA_MN_001_RepresentativeRequest;
			} else if(strReturnValue.equalsIgnoreCase(Constants.CIDA_MN_001_Dontknow)) {
				strExitState = Constants.CIDA_MN_001_Dontknow;
			} else if(strReturnValue.contains("Policy")||strReturnValue.trim().equalsIgnoreCase("policy")){
				strExitState = Constants.CIDA_MN_001_Dontknow;
			}else {
				strExitState = Constants.ER;
			}
		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in CIDA_MN_001_VALUE :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"CIDA_MN_001_VALUE :: "+strExitState);
		data.setSessionData(Constants.S_INTENT, strExitState);
		/*
		String menuExsitState = strExitState;
		if(strExitState.contains(" ")) menuExsitState = menuExsitState.replaceAll(" ", "_");
		if(null != (String)data.getSessionData("CIDA_MN_001_"+menuExsitState) && !((String)data.getSessionData("CIDA_MN_001_"+menuExsitState)).isEmpty()) menuExsitState = (String)data.getSessionData("CIDA_MN_001_"+menuExsitState);
		data.addToLog(currElementName, "Final Value of Menu Exit State for CIDA_MN_001: "+menuExsitState);
		data.setSessionData(Constants.S_MENU_SELCTION_KEY, data.getSessionData(Constants.S_BU)+":CIDA_MN_001:"+menuExsitState);
		*/
		return strExitState;
	}
	
	public static void main(String[] args) {
		String strReturnValue = "6000333300";
		if(strReturnValue.length()==10 && strReturnValue.startsWith("3")) {
			strReturnValue=strReturnValue.replaceFirst("3", "F");
    	}
		System.out.println(strReturnValue);
	}
}