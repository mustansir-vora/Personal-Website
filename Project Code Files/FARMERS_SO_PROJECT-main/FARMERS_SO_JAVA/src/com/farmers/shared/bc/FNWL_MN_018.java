package com.farmers.shared.bc;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class FNWL_MN_018 extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws Exception {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);

		try {

			String returnValue = (String) data.getElementData("FNWL_MN_018_Call", "Return_Value");
			data.addToLog(currElementName, "Menu ID : FNWL_MN_018 :: Menu Value : " + returnValue);
			data.setSessionData(Constants.FNWL_MN_018_VALUE, returnValue);

			if (null != returnValue && Constants.EmptyString != returnValue) {

				if (returnValue.equalsIgnoreCase(Constants.NOMATCH)) {
					strExitState = Constants.NOMATCH;
					caa.createMSPKey_FNWL(caa, data, "FNWL_MN_018", "NOMATCH");
				} else if (returnValue.equalsIgnoreCase(Constants.NOINPUT)) {
					strExitState = Constants.NOINPUT;
				} else if (returnValue.equalsIgnoreCase(Constants.CUSTOMER_SERVICE)) {
					strExitState = Constants.CUSTOMER_SERVICE;
					caa.createMSPKey_FNWL(caa, data, "FNWL_MN_018", "CUSTOMER_SERVICE");
				} else if (returnValue.equalsIgnoreCase(Constants.INITIAL_PAYMENT)) {
					strExitState = Constants.INITIAL_PAYMENT;
					caa.createMSPKey_FNWL(caa, data, "FNWL_MN_018", "INITIAL_PAYMENT");
				} else if (returnValue.equalsIgnoreCase(Constants.REPRESENTATIVE)) {
					strExitState = Constants.REPRESENTATIVE;
					caa.createMSPKey_FNWL(caa, data, "FNWL_MN_018", "REPRESENTATIVE");
				}
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in FNWL_MN_018 :: " + e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"FNWL_MN_018_VALUE :: "+strExitState);
		
		return strExitState;
	}

}
