package com.farmers.shared.bc;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class VIA_MN_AlphaNum extends DecisionElementBase{

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws Exception {

		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		
		try {
			String strReturnValue = (String) data.getElementData("VIA_MN_ALPHANUM_Call","Return_Value");
			data.addToLog(currElementName, " VIA_MN_ALPHANUM_Call : Return_Value :: "+strReturnValue);
			data.setSessionData("VIA_MN_ALPHANUM_VALUE", strReturnValue);
			if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
			} else if(strReturnValue.equalsIgnoreCase(Constants.STRING_YES)) {
				strExitState = "YES";
			} else if(strReturnValue.equalsIgnoreCase(Constants.STRING_NO)) {
				strExitState = "NO";
				data.setSessionData("SPECIALTY_COLLECTION_FLAG", "TRUE");
			} 
			else if (strReturnValue.equalsIgnoreCase(Constants.REPRESENTATIVE)) {
				strExitState = Constants.REPRESENTATIVE;
				data.setSessionData("LAST_MENU_NAME_REP", "VIA_MN_ALPHANUM");
			} 
			else {
				strExitState = Constants.ER;
			}
		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in VIA_MN_AlphaNum_VALUE :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"VIA_MN_AlphaNum_VALUE :: "+strExitState);
		
		return strExitState;

		
	}

}
