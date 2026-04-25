package com.farmers.shared.bc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class VIA_MN_002_VALUE extends DecisionElementBase 
{
	 static int MAX_VALID_YR = 9999; 
	 static int MIN_VALID_YR = 1800; 

		@Override
		public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {

			String strExitState = Constants.ER;
			CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
			try {
				String strReturnValue = (String) data.getElementData("VIA_MN_002_Call", "Return_Value");
				data.addToLog(currElementName,
						"Menu ID : " + "VIA_MN_002_Call" + " :: Menu Value : " + strReturnValue);
				data.setSessionData("VIA_MN_002_VALUE", strReturnValue);

				String regex = "(.)*(\\d)(.)*";
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(strReturnValue);
				if (matcher.matches()) {
					data.setSessionData(Constants.S_TELEPHONENUMBER, strReturnValue);
					data.setSessionData(Constants.S_POLICY_NUM, null);
					strExitState = Constants.VALID;
				} else if (strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
					strExitState = Constants.NOINPUT;
				} else if (strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
					strExitState = Constants.NOMATCH;
				} else if (strReturnValue.equalsIgnoreCase(Constants.REPRENSTATIVE)) {
					strExitState = Constants.REPRENSTATIVE;
					data.setSessionData("LAST_MENU_NAME_REP", "VIA_MN_002");
				} else if (strReturnValue.equalsIgnoreCase(Constants.KYCBA_MN_001_Dontknow)) {
					strExitState = Constants.DONTHAVE;
				}else if(strReturnValue.equalsIgnoreCase("Hold on")) {	
					strExitState = "Hold on";
				}
			} catch (Exception e) {
				data.addToLog(currElementName, "Exception in VIA_MN_002 :: " + e);
				caa.printStackTrace(e);
			}
			data.addToLog(currElementName, "VIA_MN_002 : strExitState :: " + strExitState);
			data.setSessionData("VIA_MN_002_ExitState", strExitState);
			return strExitState;

		}

	}