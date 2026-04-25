package com.farmers.bc;
 
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;
 
public class ACHA_MN_005_VALUE extends DecisionElementBase {
 


	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("ACHA_MN_005_Call", "Return_Value");
			data.addToLog(currElementName, " ACHA_MN_005_VALUE : Return_Value :: " + strReturnValue);
			
			String regex = "(.)*(\\d)(.)*";      
	        Pattern pattern = Pattern.compile(regex);
	        Matcher matcher = pattern.matcher(strReturnValue);
			if(matcher.matches()) {
				data.setSessionData(Constants.S_BANKACCOUNTNUMBER, strReturnValue);
            	String lastFourDigits = strReturnValue.substring(strReturnValue.length() - 4);
            	data.setSessionData("S_LAST_FOUR_BANKACCNUMBER", lastFourDigits);
            	strExitState = Constants.SU;
            } else if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
			} else {
				strExitState = Constants.ER;
			}
			}catch (Exception e) {
			data.addToLog(currElementName, "Exception in ACHA_MN_005_VALUE :: " + e);
			caa.printStackTrace(e);
		}
		
		
		data.addToLog(currElementName, "ACHA_MN_005_VALUE :: " + strExitState);
		return strExitState;
	}
}