package com.farmers.bc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class TFGPA_MN_003_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("TFGPA_MN_003_Call", "Return_Value");
			data.addToLog(currElementName, " TFGPA_MN_002_VALUE : Return_Value :: " + strReturnValue);
			data.setSessionData("TFGPA_MN_003_VALUE", strReturnValue);

			String regex = "(.)*(\\d)(.)*";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(strReturnValue);
			if (matcher.matches()) {
				data.setSessionData("S_AMOUNT_VALIDATION_INVOKED_FROM", "TFGPA_MN_003");
				strExitState = "Payment Amount";
				
			} else if (strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			} else if (strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in TFGPA_MN_003_VALUE :: " + e);
			caa.printStackTrace(e);
		}

		data.addToLog(currElementName, "TFGPA_MN_003_VALUE :: " + strExitState);
		return strExitState;
	}
}