package com.farmers.bc;
 
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;
 
public class TFPC_MN_001_VALUE extends DecisionElementBase {
 
	private static final String S_LAST_FOUR_CARDDIGITS = "S_LAST_FOUR_CARDDIGITS";

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("TFPC_MN_001_Call", "Return_Value");
			
			
			String regex = "(.)*(\\d)(.)*";      
	        Pattern pattern = Pattern.compile(regex);
	        Matcher matcher = pattern.matcher(strReturnValue);
			if(matcher.matches()) {
				data.setSessionData(Constants.S_CARD_NUMBER, strReturnValue);
            	String lastFourDigits = strReturnValue.substring(strReturnValue.length() - 4);
            	data.setSessionData(S_LAST_FOUR_CARDDIGITS, lastFourDigits);
            	data.addToLog(currElementName, " TFPC_MN_001_VALUE : Return_Value :: " + lastFourDigits);
            	strExitState = Constants.SU;
            }  else  if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
			} else {
				strExitState = Constants.ER;
			}
			} catch (Exception e) {
			data.addToLog(currElementName, "Exception in TFPC_MN_001_VALUE :: " + e);
			caa.printStackTrace(e);
		}
		
		
		data.addToLog(currElementName, "TFPC_MN_001_VALUE :: " + strExitState);
		return strExitState;
	}
}