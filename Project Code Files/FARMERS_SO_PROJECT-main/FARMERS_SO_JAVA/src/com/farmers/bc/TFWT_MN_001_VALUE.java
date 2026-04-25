package com.farmers.bc;
 
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;
 
public class TFWT_MN_001_VALUE extends DecisionElementBase {
 
	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {
			
			String strReturnValue = (String) data.getElementData("TFWT_MN_001_Call", "Return_Value");
			data.addToLog(currElementName, " TFWT_MN_001_VALUE : Return_Value :: " + strReturnValue);
			data.setSessionData("TFWT_MN_001_VALUE", strReturnValue);
			String regex = "(.)*(\\d)(.)*";      
	        Pattern pattern = Pattern.compile(regex);
	        Matcher matcher = pattern.matcher(strReturnValue);
			if(matcher.matches()) {
				data.setSessionData(Constants.S_POPUP_TYPE,Constants.S_POPUP_POLICYNUM);
            	data.setSessionData(Constants.S_CALLER_INPUT,strReturnValue);
             	data.setSessionData("S_POLICY_NUM",strReturnValue);
            	strExitState = Constants.SU;
            } else if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
			} else {
				strExitState = Constants.ER;
			}
			
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in TFWT_MN_001_VALUE :: " + e);
			caa.printStackTrace(e);
		}
	
		
		data.addToLog(currElementName, "TFWT_MN_001_VALUE :: " + strExitState);
		return strExitState;
	}
}