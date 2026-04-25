package com.farmers.afterHoursCallBack;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class SAHCB_MN_004_VALUE  extends DecisionElementBase {
	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("SAHCB_MN_004_Call", "Return_Value");
			data.addToLog(currElementName,
					"Menu ID : " + "SAHCB_MN_004_Call" + " :: Menu Value : " + strReturnValue);
			data.setSessionData("SAHCB_MN_004_VALUE", strReturnValue);
			String regex = "(.)*(\\d)(.)*";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(strReturnValue);
			if (matcher.matches()) {
				data.setSessionData(Constants.S_ANI, strReturnValue);
				strExitState = Constants.VALID;
			} else if (strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			} else if (strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
			}
			else {
				strExitState = Constants.ER;
				data.setSessionData("SAHCB_MN_004_VALUE", "ER");
			}
	
		}
		catch(Exception e) {
			data.addToLog(currElementName,"Exception in SAHCB_MN_003_VALUE :: "+e);
			caa.printStackTrace(e);
		}
		return strExitState;
	}

}
