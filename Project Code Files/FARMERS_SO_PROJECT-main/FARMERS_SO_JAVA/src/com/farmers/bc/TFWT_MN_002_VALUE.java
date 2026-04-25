package com.farmers.bc;
 
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;
 
public class TFWT_MN_002_VALUE extends DecisionElementBase {
 
	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {
			
			String strReturnValue = (String) data.getElementData("TFWT_MN_002_Call", "Return_Value");
			data.addToLog(currElementName, " TFWT_MN_001_VALUE : Return_Value :: " + strReturnValue);
			data.setSessionData("TFWT_MN_002_VALUE", strReturnValue);
			String regex = "(.)*(\\d)(.)*";      
	        Pattern pattern = Pattern.compile(regex);
	        Matcher matcher = pattern.matcher(strReturnValue);
			if(matcher.matches()) {
            	data.setSessionData(Constants.S_AREACODE, strReturnValue);
            	strExitState = Constants.SU;
            } else if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
			} else {
				strExitState = Constants.ER;
			}
			
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in TFWT_MN_002__VALUE :: " + e);
			caa.printStackTrace(e);
		}
//		String menuExsitState = strExitState;
//		if (strExitState.contains(" "))
//			menuExsitState = menuExsitState.replaceAll(" ", "_");
//		if (null != (String) data.getSessionData("TFWT_MN_002_" + menuExsitState)
//				&& !((String) data.getSessionData("TFWT_MN_002_" + menuExsitState)).isEmpty())
//			menuExsitState = (String) data.getSessionData("TFWT_MN_002_" + menuExsitState);
//		data.addToLog(currElementName, "Final Value of Menu Exit State for TFWT_MN_002: " + menuExsitState);
//		data.setSessionData(Constants.S_MENU_SELECTION_KEY,
//				data.getSessionData(Constants.S_BU) + ":TFWT_MN_002:" + menuExsitState);
		
		data.addToLog(currElementName, "TFWT_MN_002_VALUE :: " + strExitState);
		return strExitState;
	}
}