package com.farmers.shared.bc;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class FNWL_MN_014 extends DecisionElementBase {
	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws Exception {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);

		try {
			String returnValue = (String) data.getElementData("FNWL_MN_014_Call", "Return_Value");
			data.addToLog(currElementName, "Menu ID : FNWL_MN_014 :: Menu Value : " + returnValue);
			data.setSessionData(Constants.FNWL_MN_014_VALUE, returnValue);
			String priorityFlag = null != data.getSessionData(Constants.FNWL_PRIORITY_AGENT_FLAG) ? (String) data.getSessionData(Constants.FNWL_PRIORITY_AGENT_FLAG) : Constants.FALSE;

			if (null != returnValue && Constants.EmptyString != returnValue) {

				if (returnValue.equalsIgnoreCase(Constants.NOMATCH)) {
					strExitState = Constants.NOMATCH;
					caa.createMSPKey_FNWL(caa, data, "FNWL_MN_014", "NOMATCH");
				} else if (returnValue.equalsIgnoreCase(Constants.NOINPUT)) {
					strExitState = Constants.NOINPUT;
					caa.createMSPKey_FNWL(caa, data, "FNWL_MN_014", "NOINPUT");
				} else if (returnValue.equalsIgnoreCase(Constants.DONTHAVE)) {
					strExitState = Constants.DONTHAVE;
					caa.createMSPKey_FNWL(caa, data, "FNWL_MN_014", "DONT_HAVE");
				} else if (returnValue.equalsIgnoreCase(Constants.REPRESENTATIVE)) {
					strExitState = Constants.REPRESENTATIVE;
					caa.createMSPKey_FNWL(caa, data, "FNWL_MN_014", "REPRESENTATIVE");
				} else {
					strExitState = Constants.SU;
					data.setSessionData(Constants.FNWL_USER_ENTERED_DOB, returnValue);
				}
			}

		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in FNWL_MN_014 :: " + e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"FNWL_MN_014_VALUE :: "+strExitState);
		
		return strExitState;
	}

}
