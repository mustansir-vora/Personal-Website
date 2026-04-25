package com.farmers.bc;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class ACHA_MN_001_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {
           String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("MMddyyyy"));
			data.addToLog(currElementName, "Current Date : " + currentDate);
			data.setSessionData("S_CURRENTDATE", currentDate);
			String strReturnValue = (String) data.getElementData("ACHA_MN_001_Call", "Return_Value");
			data.addToLog(currElementName, " ACHA_MN_001_VALUE : Return_Value :: " + strReturnValue);
			String regex = "(.)*(\\d)(.)*";      
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(strReturnValue);
			if(matcher.matches()) {
				data.setSessionData(Constants.S_BANK_ROUTING_NUMBER, strReturnValue);
				String lastFourDigits = strReturnValue.substring(strReturnValue.length() - 4);
				data.setSessionData("S_LAST_BACKROUTINGFOURDIGIT", lastFourDigits);
				strExitState = Constants.SU;
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			}
			else if(strReturnValue.equalsIgnoreCase("Dont Have it")) {
				strExitState = "Dont Have it";
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
			}else if(strReturnValue.equalsIgnoreCase("")) {
				strExitState = "";
			} else {
				strExitState = Constants.ER;
			}

		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in ACHA_MN_001_VALUE :: " + e);
			caa.printStackTrace(e);
		}
//		String menuExsitState = strExitState;
//		if (strExitState.contains(" "))
//			menuExsitState = menuExsitState.replaceAll(" ", "_");
//		if (null != (String) data.getSessionData("ACHA_MN_001_" + menuExsitState)
//				&& !((String) data.getSessionData("ACHA_MN_001_" + menuExsitState)).isEmpty())
//			menuExsitState = (String) data.getSessionData("ACHA_MN_001_" + menuExsitState);
//		data.addToLog(currElementName, "Final Value of Menu Exit State for ACHA_MN_001: " + menuExsitState);
//		data.setSessionData(Constants.S_MENU_SELECTION_KEY,
//				data.getSessionData(Constants.S_BU) + ":ACHA_MN_001:" + menuExsitState);

		data.addToLog(currElementName, "ACHA_MN_001_VALUE :: " + strExitState);
		return strExitState;
	}
}