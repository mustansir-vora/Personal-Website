package com.farmers.shared.bc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class GET_SIDC_MN_004 extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("SIDC_MN_004_Call","Return_Value");
			data.addToLog(currElementName, " SIDC_MN_004_Call : Return_Value :: "+strReturnValue);
			data.setSessionData(Constants.SIDC_MN_004_VALUE, strReturnValue);
			String regex = "(.)*(\\d)(.)*";      
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(strReturnValue);
			if(matcher.matches()) {
				strExitState = Constants.SU;
				data.setSessionData(Constants.S_FAX_NUMBER, strReturnValue);
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
			} else {
				strExitState = Constants.ER;
			}
		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in CBIZID_MN_001_VALUE :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"CBIZID_MN_001_VALUE :: "+strExitState);
		return strExitState;
	}
	
}

