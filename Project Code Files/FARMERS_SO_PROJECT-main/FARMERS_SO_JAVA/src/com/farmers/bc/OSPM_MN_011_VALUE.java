package com.farmers.bc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class OSPM_MN_011_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("OSPM_MN_011_Call", "Return_Value");
			data.addToLog(currElementName, "OSPM_MN_011_Call : Return_Value :: " + strReturnValue);
			data.setSessionData("OSPM_MN_011_VALUE", strReturnValue);

			String regex = "(.)*(\\d)(.)*";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(strReturnValue);
			if (matcher.matches()) {
				data.setSessionData(Constants.S_POLICY_NUM, strReturnValue);
				data.setSessionData(Constants.S_CALLER_INPUT,strReturnValue);
				data.addToLog(currElementName,
						"OSPM_MN_011 policy number :: " + (String) data.getSessionData(Constants.S_POLICY_NUM));
				strExitState = Constants.SUCCESSFUL;
			} else if (strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = "Unsuccessful";
			} else if (strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NO_MATCH;
			}else if (strReturnValue.equalsIgnoreCase(Constants.REPRESENTATIVE)) {
				strExitState = Constants.REPRESENTATIVE;
			}else if(strReturnValue.equalsIgnoreCase("Dont have it")) {
				strExitState = "Unsuccessful";
			}
			else {
				strExitState = Constants.ER;
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in OSPM_MN_011_VALUE :: " + e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName, "OSPM_MN_011_VALUE :: " + strExitState);
		return strExitState;
	}
}