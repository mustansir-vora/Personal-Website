package com.farmers.shared.bc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class GET_SMM_MN_002 extends DecisionElementBase 
{

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException 
	{
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("SMM_MN_002_Call","Return_Value");
			data.addToLog(currElementName, "Menu ID : "+"SMM_MN_002_Call"+" :: Menu Value : "+strReturnValue);
			data.setSessionData(Constants.SMM_MN_002_VALUE, strReturnValue);
			
			String regex = "(.)*(\\d)(.)*";      
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(strReturnValue);
			if(matcher.matches()) {
				data.setSessionData(Constants.S_POLICY_NUM, strReturnValue);
				data.setSessionData(Constants.S_POPUP_TYPE,Constants.S_POPUP_POLICYNUM);
            	data.setSessionData(Constants.S_CALLER_INPUT,strReturnValue);
				strExitState = Constants.VALID;
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
			} else if(strReturnValue.equalsIgnoreCase(Constants.REPRENSTATIVE)) {
				strExitState = Constants.REPRENSTATIVE;
			} else if(strReturnValue.equalsIgnoreCase(Constants.DONTHAVE)) {	
				strExitState = Constants.DONTHAVE;
			}
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in GET_SIDA_MN_001 :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"GET_SIDA_MN_001 : strExitState :: "+strExitState);
		return strExitState;
	
	}
	
}

