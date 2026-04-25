package com.farmers.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class KYCNP_MN_004_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("KYCNP_MN_004_Call","Return_Value");
			data.addToLog(currElementName, " KYCNP_MN_004_Call Before : Return_Value :: "+strReturnValue);
			data.setSessionData(Constants.KYCNP_MN_004_VALUE, strReturnValue);
			if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
			} else if(strReturnValue.equalsIgnoreCase(Constants.REPRESENTATIVE)) {
				strExitState = Constants.REPRESENTATIVE;
			} else if (strReturnValue.toLowerCase().contains("none") || strReturnValue.toLowerCase().contains("none of these")) {
				strExitState = Constants.NONE_OF_THESE;
			} else {
				data.setSessionData(Constants.PREVIOUS_MENU_BRANDCHECK, Constants.KYCNP_MN_004_MENUNAME);
				strExitState = Constants.ONE_TO_FIVE_ALLOFTHESE;
			}
		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in KYCNP_MN_004_VALUE :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"KYCNP_MN_004_VALUE :: "+strExitState);
		return strExitState;
	}
}