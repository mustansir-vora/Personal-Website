package com.farmers.shared.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class GET_SPCQ_MN_002 extends DecisionElementBase 
{

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException 
	{
	
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		String mspValue = "";
		try {
			String returnValue = (String) data.getElementData("SPCQ_MN_002_Call","Return_Value");
			data.addToLog(currElementName, "Menu ID : "+"SPCQ_MN_002"+" :: Menu Value : "+returnValue);
			data.setSessionData(Constants.SPCQ_MN_002_VALUE, returnValue);
			if(returnValue != null && !returnValue.equals("")) {
				
				if(returnValue.equalsIgnoreCase(Constants.NOINPUT)) {	
					strExitState = Constants.NOINPUT;
				}else if(returnValue.equalsIgnoreCase(Constants.NOMATCH)) {
						strExitState = Constants.NOMATCH;
						mspValue = Constants.NOMATCH;
				}else if(returnValue.equalsIgnoreCase(Constants.STRING_YES)) {
						strExitState = Constants.STRING_YES;
						mspValue = Constants.MSP_POLICY_QUESTIONS;
				}else if(returnValue.equalsIgnoreCase(Constants.STRING_NO)){
						strExitState = Constants.STRING_NO;
						mspValue = Constants.MSP_COVERAGE_CHANGE;
				}else if(returnValue.equalsIgnoreCase(Constants.REPRESENTATIVE)) {
						strExitState = Constants.REPRESENTATIVE;
						mspValue = "REPRESENTATIVE";
				}else {
						strExitState = Constants.ER;
				}
			}
		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in SPCQ_MN_002 :: "+e);
			caa.printStackTrace(e);
		}
		caa.createMSPKey(caa, data, "SPCQ_MN_001", mspValue);
		return strExitState;
	
	}
	
}

