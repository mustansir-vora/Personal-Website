package com.farmers.shared.bc;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class FNWL_MN_009 extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws Exception {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		
		try {
			String returnValue = (String) data.getElementData("FNWL_MN_009_Call","Return_Value");
			data.addToLog(currElementName, "Menu ID : FNWL_MN_009 :: Menu Value : "+returnValue);
			data.setSessionData(Constants.FNWL_MN_009_VALUE, returnValue);
			String priorityFlag = null != data.getSessionData(Constants.FNWL_PRIORITY_AGENT_FLAG) ? (String) data.getSessionData(Constants.FNWL_PRIORITY_AGENT_FLAG) : Constants.FALSE;
			
			if (null != returnValue && Constants.EmptyString != returnValue) {
				
				if (returnValue.equalsIgnoreCase(Constants.NOMATCH)) {
					strExitState = Constants.NOMATCH;
					if (Constants.TRUE.equalsIgnoreCase(priorityFlag)) {
						caa.createMSPKey_FNWL(caa, data, "FNWL_MN_009", "TP_NOMATCH");
					}
					else {
						caa.createMSPKey_FNWL(caa, data, "FNWL_MN_009", "NP_NOMATCH");
					}
				}
				else if (returnValue.equalsIgnoreCase(Constants.NOINPUT)) {
					strExitState = Constants.NOINPUT;
				}
				else if (returnValue.equalsIgnoreCase(Constants.REPRESENTATIVE)) {
					strExitState = Constants.REPRESENTATIVE;
					if (Constants.TRUE.equalsIgnoreCase(priorityFlag)) {
						caa.createMSPKey_FNWL(caa, data, "FNWL_MN_009", "TP_REPRESENTATIVE");
					}
					else {
						caa.createMSPKey_FNWL(caa, data, "FNWL_MN_009", "NP_REPRESENTATIVE");
					}
				}
				else if (returnValue.equalsIgnoreCase(Constants.FNWL_TECHNICAL_SUPPORT)) {
					strExitState = Constants.FNWL_TECHNICAL_SUPPORT;
					if (Constants.TRUE.equalsIgnoreCase(priorityFlag)) {
						caa.createMSPKey_FNWL(caa, data, "FNWL_MN_009", "TP_TECHNICAL_SUPPORT");
					}
					else {
						caa.createMSPKey_FNWL(caa, data, "FNWL_MN_009", "NP_TECHNICAL_SUPPORT");
					}
				}
				else if (returnValue.equalsIgnoreCase(Constants.FNWL_SALES_SUPPORT)) {
					strExitState = Constants.FNWL_SALES_SUPPORT;
					if (Constants.TRUE.equalsIgnoreCase(priorityFlag)) {
						caa.createMSPKey_FNWL(caa, data, "FNWL_MN_009", "TP_SALES_SUPPORT");
					}
					else {
						caa.createMSPKey_FNWL(caa, data, "FNWL_MN_009", "NP_SALES_SUPPORT");
					}
				}
				else if (returnValue.equalsIgnoreCase(Constants.FNWL_UNDERWRITING_QUESTIONS)) {
					strExitState = Constants.FNWL_UNDERWRITING_QUESTIONS;
					if (Constants.TRUE.equalsIgnoreCase(priorityFlag)) {
						caa.createMSPKey_FNWL(caa, data, "FNWL_MN_009", "TP_UNDERWRITING");
					}
					else {
						caa.createMSPKey_FNWL(caa, data, "FNWL_MN_009", "NP_UNDERWRITING");
					}
				}
			}
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in FNWL_MN_009 :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"FNWL_MN_009_VALUE :: "+strExitState);
		
		return strExitState;
	}

}
