package com.farmers.shared.bc;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class FNWL_MN_007 extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws Exception {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {
			String returnValue = (String) data.getElementData("FNWL_MN_007_Call", "Return_Value");
			data.addToLog(currElementName, "Menu ID : FNWL_MN_007 :: Menu Value : " + returnValue);
			data.setSessionData(Constants.FNWL_MN_007_VALUE, returnValue);
			String priorityFlag = null != data.getSessionData(Constants.FNWL_PRIORITY_AGENT_FLAG) ? (String) data.getSessionData(Constants.FNWL_PRIORITY_AGENT_FLAG) : Constants.FALSE;

			if (null != returnValue && Constants.EmptyString != returnValue) {

				if (returnValue.equalsIgnoreCase(Constants.NOMATCH)) {
					strExitState = Constants.NOMATCH;
					if (Constants.TRUE.equalsIgnoreCase(priorityFlag)) {
						caa.createMSPKey_FNWL(caa, data, "FNWL_MN_007", "TP_NOMATCH");
					}
					else {
						caa.createMSPKey_FNWL(caa, data, "FNWL_MN_007", "NP_NOMATCH");
					}
				} else if (returnValue.equalsIgnoreCase(Constants.NOINPUT)) {
					strExitState = Constants.NOINPUT;
				} else if (returnValue.equalsIgnoreCase(Constants.PENDING_APPLICATIONS)) {
					strExitState = Constants.PENDING_APPLICATIONS;
					if (Constants.TRUE.equalsIgnoreCase(priorityFlag)) {
						caa.createMSPKey_FNWL(caa, data, "FNWL_MN_007", "TP_PENDING_APPLICATIONS");
					}
					else {
						caa.createMSPKey_FNWL(caa, data, "FNWL_MN_007", "NP_PENDING_APPLICATIONS");
					}
				} else if (returnValue.equalsIgnoreCase(Constants.FNWL_TECHNICAL_SUPPORT)) {
					strExitState = Constants.FNWL_TECHNICAL_SUPPORT;
					if (Constants.TRUE.equalsIgnoreCase(priorityFlag)) {
						caa.createMSPKey_FNWL(caa, data, "FNWL_MN_007", "TP_TECHNICAL_SUPPORT");
					}
					else {
						caa.createMSPKey_FNWL(caa, data, "FNWL_MN_007", "NP_TECHNICAL_SUPPORT");
					}
				} else if (returnValue.equalsIgnoreCase(Constants.POLICY_SERVICES)) {
					strExitState = Constants.POLICY_SERVICES;
					if (Constants.TRUE.equalsIgnoreCase(priorityFlag)) {
						caa.createMSPKey_FNWL(caa, data, "FNWL_MN_007", "TP_POLICY_SERVICES");
					}
					else {
						caa.createMSPKey_FNWL(caa, data, "FNWL_MN_007", "NP_POLICY_SERVICES");
					}
				} else if (returnValue.equalsIgnoreCase(Constants.REPRESENTATIVE)) {
					strExitState = Constants.REPRESENTATIVE;
					if (Constants.TRUE.equalsIgnoreCase(priorityFlag)) {
						caa.createMSPKey_FNWL(caa, data, "FNWL_MN_007", "TP_REPRESENTATIVE");
					}
					else {
						caa.createMSPKey_FNWL(caa, data, "FNWL_MN_007", "NP_REPRESENTATIVE");
					}
				}
			}
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in FNWL_MN_007 :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"FNWL_MN_007_VALUE :: "+strExitState);
		return strExitState;
	}

}
