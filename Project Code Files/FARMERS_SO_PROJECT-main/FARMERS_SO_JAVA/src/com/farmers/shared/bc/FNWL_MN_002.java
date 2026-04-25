package com.farmers.shared.bc;

import java.util.regex.Pattern;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class FNWL_MN_002 extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws Exception {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		
		try {
			String returnValue = (String) data.getElementData("FNWL_MN_002_Call","Return_Value");
			data.addToLog(currElementName, "Menu ID : FNWL_MN_002 :: Menu Value : " + returnValue);
			data.setSessionData(Constants.FNWL_MN_002_VALUE, returnValue);
			String priorityFlag = null != data.getSessionData(Constants.FNWL_PRIORITY_AGENT_FLAG) ? (String) data.getSessionData(Constants.FNWL_PRIORITY_AGENT_FLAG) : Constants.FALSE;
			
			if (null != returnValue && Constants.EmptyString != returnValue) {
				
				if (returnValue.equalsIgnoreCase(Constants.NOMATCH)) {
					strExitState = Constants.NOMATCH;
				}
				else if (returnValue.equalsIgnoreCase(Constants.NOINPUT)) {
					strExitState = Constants.NOINPUT;
					if (Constants.TRUE.equalsIgnoreCase(priorityFlag)) {
						caa.createMSPKey_FNWL(caa, data, "FNWL_MN_002", "TP_NOINPUT");
					}
					else {
						caa.createMSPKey_FNWL(caa, data, "FNWL_MN_002", "NP_NOINPUT");
					}
				}
				else if (returnValue.equalsIgnoreCase(Constants.REPRESENTATIVE)) {
					strExitState = Constants.REPRENSTATIVE;
					caa.createMSPKey_FNWL(caa, data, "FNWL_MN_002", "REPRESENTATIVE");
					if (Constants.TRUE.equalsIgnoreCase(priorityFlag)) {
						caa.createMSPKey_FNWL(caa, data, "FNWL_MN_002", "TP_REPRESENTATIVE");
					}
					else {
						caa.createMSPKey_FNWL(caa, data, "FNWL_MN_002", "NP_REPRESENTATIVE");
					}
				}
				else {
					Boolean isValidInput = checkInput(returnValue, data, caa, currElementName);
					
					if (isValidInput) {
						strExitState = Constants.SU;
					}
				}
			}
			
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in FNWL_MN_002 :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"FNWL_MN_002_VALUE :: "+strExitState);
		
		return strExitState;
	}
	
	public static Boolean checkInput(String returnValue, DecisionElementData data, CommonAPIAccess caa, String currElementName) {
		Boolean isValid = false;
		
		try {
			Pattern pattern = Pattern.compile("^(?![a-zA-Z]{6})[a-zA-Z0-9]{6}$");
			isValid = pattern.matcher(returnValue).matches();
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in FNWL_MN_002 CheckInput Method :: "+e);
			caa.printStackTrace(e);
		}
		return isValid;
	}
}
