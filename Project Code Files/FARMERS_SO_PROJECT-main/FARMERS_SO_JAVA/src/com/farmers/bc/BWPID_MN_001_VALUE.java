package com.farmers.bc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;
import com.farmers.util.PolicyNumManupulation;

public class BWPID_MN_001_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("BWPID_MN_001_Call", "Return_Value");
			data.addToLog(currElementName, " BWPID_MN_001_Call : Return_Value :: " + strReturnValue);
			data.setSessionData("BWPID_MN_001_VALUE", strReturnValue);

			if (strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NO_INPUT;
			} else if (strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NO_MATCH;
			}  else if (strReturnValue.equalsIgnoreCase(Constants.DONTHAVE)) {
				strExitState = "DONTHAVE";
			}
			
			
			else if (strReturnValue.equalsIgnoreCase(Constants.New_Policy)) {
				strExitState = Constants.New_Policy;
				caa.createMSPKey(caa, data, "BWPID_MN_001", "New_Policy");
			}
			
			
			else {
				String regex = "(.)*(\\d)(.)*";
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(strReturnValue);
				if (matcher.matches()) {
					PolicyNumManupulation objPolicyNumManupulation = new PolicyNumManupulation();
					strReturnValue = objPolicyNumManupulation.policyNumManupulation(strReturnValue, data);
					data.setSessionData(Constants.S_POLICY_NUM, strReturnValue);
					strExitState = Constants.SU;
				}
				
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in BWPID_MN_001_VALUE :: " + e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName, "BWPID_MN_001_VALUE :: " + strExitState);
		return strExitState;
	}
}