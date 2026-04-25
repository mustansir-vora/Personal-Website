package com.farmers.bc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;
import com.farmers.util.PolicyNumManupulation;

public class FCPO_MN_001_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("FCPO_MN_001_Call", "Return_Value");
			data.addToLog(currElementName, " FCPO_MN_001_VALUE : Return_Value :: " + strReturnValue);
			data.setSessionData("FCPO_MN_001_VALUE", strReturnValue);

			String regex = "(.)*(\\d)(.)*";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(strReturnValue);
			if (matcher.matches()) {
				strReturnValue = strReturnValue.toUpperCase();
				if (strReturnValue.startsWith("G01") || strReturnValue.startsWith("G00")|| strReturnValue.startsWith("G55") || strReturnValue.startsWith("G88") || strReturnValue.startsWith("M00") || strReturnValue.startsWith("W25") || strReturnValue.startsWith("W37") || strReturnValue.startsWith("NAC")) {
				    data.setSessionData(Constants.BW_POLICY_NUMBER, strReturnValue);
				    data.addToLog(currElementName, "BW_POLICY_NUMBER :: " + strReturnValue);
					data.setSessionData(Constants.S_POPUP_TYPE,Constants.S_POPUP_POLICYNUM);
	            	data.setSessionData(Constants.S_CALLER_INPUT,strReturnValue);
				    strExitState = "BW Policy Number";
				} else {
				    //data.setSessionData(Constants.S_POLICY_NUM, strReturnValue);
				    data.setSessionData(Constants.S_POPUP_TYPE,Constants.S_POPUP_POLICYNUM);
	            	data.setSessionData(Constants.S_CALLER_INPUT,strReturnValue);
				    data.addToLog(currElementName, "S_POLICY_NUM :: " + strReturnValue);
				  //Foremost policies are 15 digits but caller can enter the full 15 digits, 13 digits, 12 digits, OR 10 digits.
				    if (null != strReturnValue) {
				    	if(strReturnValue.length() == 12) {
							strReturnValue = strReturnValue.substring(0, 10);
							data.addToLog(currElementName, "Considered 10 digit policynumber from 12 digits :: "+strReturnValue);
						} else if(strReturnValue.length() == 13) {
							data.setSessionData("S_EPC_PAYMENT_POLICYNUM", strReturnValue.substring(0, 13));
							strReturnValue = strReturnValue.substring(3, 13);
							data.addToLog(currElementName, "Considered 10 digit policynumber from 13 digits :: "+strReturnValue);
						} else if(strReturnValue.length() == 15) {
							data.setSessionData("S_EPC_PAYMENT_POLICYNUM", strReturnValue.substring(0, 13));
							strReturnValue = strReturnValue.substring(3, 13);
							data.addToLog(currElementName, "Considered 10 digit policynumber from 15 digits :: "+strReturnValue);
						}
					}
					
				    data.setSessionData(Constants.S_POLICY_NUM, strReturnValue);
				    strExitState = "Foremost Policy Number";
				}
				
			} else if (strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			} else if (strReturnValue.equalsIgnoreCase(Constants.REPRESENTATIVE)) {
				strExitState = Constants.REPRESENTATIVE;
			} 
			
			else if (strReturnValue.equalsIgnoreCase(Constants.New_Policy)) {
				strExitState = Constants.New_Policy;
				caa.createMSPKey(caa, data, "FCPO_MN_001", "New_Policy");
			}
			
			
			
			else if (strReturnValue.equalsIgnoreCase(Constants.DEFAULT)) {
				strExitState = Constants.DEFAULT;
			} else if (strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in FCPO_MN_001_VALUE :: " + e);
			caa.printStackTrace(e);
		}

		data.addToLog(currElementName, "FCPO_MN_001_VALUE :: " + strExitState);
		return strExitState;
	}
}