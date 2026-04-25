package com.farmers.shared.bc;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class FNWL_MN_017 extends DecisionElementBase {
	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws Exception {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);

		try {

			String returnValue = (String) data.getElementData("FNWL_MN_017_Call", "Return_Value");
			data.addToLog(currElementName, "Menu ID : FNWL_MN_017 :: Menu Value : " + returnValue);
			data.setSessionData(Constants.FNWL_MN_017_VALUE, returnValue);

			if (null != returnValue && Constants.EmptyString != returnValue) {

				if (returnValue.equalsIgnoreCase(Constants.NOMATCH)) {
					strExitState = Constants.NOMATCH;
					caa.createMSPKey_FNWL(caa, data, "FNWL_MN_017", "NOMATCH");
				} else if (returnValue.equalsIgnoreCase(Constants.NOINPUT)) {
					strExitState = Constants.NOINPUT;
					caa.createMSPKey_FNWL(caa, data, "FNWL_MN_017", "NOINPUT");
				} else if (returnValue.equalsIgnoreCase(Constants.CUSTOMER_SERVICE)) {
					strExitState = Constants.CUSTOMER_SERVICE;
					caa.createMSPKey_FNWL(caa, data, "FNWL_MN_017", "CUSTOMER_SERVICE");
				} else if (returnValue.equalsIgnoreCase(Constants.BILLING)) {
					strExitState = Constants.BILLING;
					data.setSessionData(Constants.S_API_NAME, "EPC Paymentus");
				} else if (returnValue.equalsIgnoreCase(Constants.REPRESENTATIVE)) {
					strExitState = Constants.REPRESENTATIVE;
					caa.createMSPKey_FNWL(caa, data, "FNWL_MN_017", "REPRESENTATIVE");
				}
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in FNWL_MN_017 :: " + e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"FNWL_MN_017_VALUE :: "+strExitState);
		
		return strExitState;
	}


}
