package com.farmers.bc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class FMOM_MN_002_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("FMOM_MN_002_Call", "Return_Value");
			data.addToLog(currElementName, "FMOM_MN_002_Call : Return_Value :: " + strReturnValue);
			data.setSessionData("FMOM_MN_002_VALUE", strReturnValue);
			
			if (null != strReturnValue) {
				String regex = "(.)*(\\d)(.)*";
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(strReturnValue);
				if (matcher.matches()) {
					data.setSessionData(Constants.S_POLICY_NUM, strReturnValue);
					strExitState = Constants.COLLECTED;
				} else if (strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
					strExitState = "Not collected";
				} else if (strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
					strExitState = Constants.NO_MATCH;
				}else if (strReturnValue.equalsIgnoreCase(Constants.REPRENSTATIVE)) {
					strExitState = "Representative";
				}
			} else {
				strExitState = Constants.ER;
				data.addToLog(currElementName, "FMOM_MN_001 if the value from GDF is null :: " + strReturnValue);
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in FMOM_MN_002_VALUE :: " + e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName, "FMOM_MN_002_VALUE :: " + strExitState);

		return strExitState;
	}
}