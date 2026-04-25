package com.farmers.bc;
 
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;
 
public class ACHA_MN_006_VALUE extends DecisionElementBase {
 
	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		String strFirstName = Constants.EmptyString;
		String strLastName = Constants.EmptyString;
		try {
			String strReturnValue = (String) data.getElementData("ACHA_MN_006_Call", "Return_Value");
			data.addToLog(currElementName, " ACHA_MN_006_VALUE : Return_Value :: " + strReturnValue);

			if(!"".equalsIgnoreCase(strReturnValue)) {
            	data.setSessionData(Constants.S_RECORDFIRSTANDLASTNAME, strReturnValue);
            	if(null != strReturnValue && strReturnValue.contains(" ")) {
            		strFirstName = strReturnValue.split(" ")[0];
            		strLastName = strReturnValue.split(" ")[1];
            		
            		data.addToLog(currElementName, "First :: " + strFirstName + " :: Last name :: " + strLastName);
            		data.setSessionData("S_FIRST_NAME", strFirstName);
            		data.setSessionData("S_LAST_NAME", strLastName);
            	}else {
            		data.addToLog(currElementName, "Name doesn't contains any space and so setting the value as same");
            		data.setSessionData("S_FIRST_NAME", strReturnValue);
            		data.setSessionData("S_LAST_NAME", strReturnValue);
            	}
            	strExitState = Constants.SU;
            } else if( strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
			} else {
				strExitState = Constants.ER;
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in ACHA_MN_006_VALUE :: " + e);
			caa.printStackTrace(e);
		}

		
		data.addToLog(currElementName, "ACHA_MN_006_VALUE :: " + strExitState);
		return strExitState;
	}
}