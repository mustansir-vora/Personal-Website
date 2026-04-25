package com.farmers.bc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class FASPC_MN_001_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("FASPC_MN_001_Call", "Return_Value");
			data.addToLog(currElementName, " FASPC_MN_001_Call : Return_Value :: " + strReturnValue);
			data.setSessionData("FASPC_MN_001_VALUE", strReturnValue);

			String regex = "(.)*(\\d)(.)*";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(strReturnValue);
			if (matcher.matches()) {
				data.setSessionData(Constants.S_POLICY_NUM, strReturnValue);
				strExitState = Constants.GOT_POLICY;
//				caa.createMSPKey(caa, data, "FASPC_MN_001", "POLICY NUMBER");
			} else if (strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NO_INPUT;
			} else if (strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NO_MATCH;
			} else if (strReturnValue.equalsIgnoreCase("Dont have it")) {
				strExitState = "Dont have it";
			}
			
			
			 else if (strReturnValue.equalsIgnoreCase(Constants.New_Policy)) {
					strExitState = Constants.New_Policy;
					caa.createMSPKey(caa, data, "FASPC_MN_001", "New_Policy");
				}
			
			
			
			else if (strReturnValue.equalsIgnoreCase("Representative")) {
				strExitState = "representative";
			}
			else {
				strExitState = Constants.ER;
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in FASPC_MN_001_VALUE :: " + e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName, "FASPC_MN_001_VALUE :: " + strExitState);

//		data.addToLog(currElementName,"S_MENU_SELECTION_KEY FASPC_MN_001: " + (String) data.getSessionData("S_MENU_SELCTION_KEY"));
		return strExitState;
	}
}