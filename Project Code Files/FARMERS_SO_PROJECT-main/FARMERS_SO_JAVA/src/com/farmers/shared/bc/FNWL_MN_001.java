package com.farmers.shared.bc;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;
import com.servion.platform.utilities.ReturnVal;

public class FNWL_MN_001 extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws Exception {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		
		try {
			String returnValue = (String) data.getElementData("FNWL_MN_001_Call","Return_Value");
			data.addToLog(currElementName, "Menu ID : FNWL_MN_001 :: Menu Value : "+returnValue);
			data.setSessionData(Constants.FNWL_MN_001_VALUE, returnValue);
			String priorityFlag = null != data.getSessionData(Constants.FNWL_PRIORITY_AGENT_FLAG) ? (String) data.getSessionData(Constants.FNWL_PRIORITY_AGENT_FLAG) : Constants.FALSE;
			
			if (null != returnValue && Constants.EmptyString != returnValue) {
				
				if (returnValue.equalsIgnoreCase(Constants.NOMATCH)) {
					strExitState = Constants.NOMATCH;
					if (Constants.TRUE.equalsIgnoreCase(priorityFlag)) {
						caa.createMSPKey_FNWL(caa, data, "FNWL_MN_001", "TP_NOMATCH");
					}
					else {
						caa.createMSPKey_FNWL(caa, data, "FNWL_MN_001", "NP_NOMATCH");
					}
				}
				else if (returnValue.equalsIgnoreCase(Constants.NOINPUT)) {
					strExitState = Constants.NOINPUT;
					if (Constants.TRUE.equalsIgnoreCase(priorityFlag)) {
						caa.createMSPKey_FNWL(caa, data, "FNWL_MN_001", "TP_NOINPUT");
					}
					else {
						caa.createMSPKey_FNWL(caa, data, "FNWL_MN_001", "NP_NOINPUT");
					}
				}
				else if (returnValue.equalsIgnoreCase(Constants.REPRESENTATIVE)) {
					strExitState = Constants.REPRENSTATIVE;
					if (Constants.TRUE.equalsIgnoreCase(priorityFlag)) {
						caa.createMSPKey_FNWL(caa, data, "FNWL_MN_001", "TP_REPRESENTATIVE");
					}
					else {
						caa.createMSPKey_FNWL(caa, data, "FNWL_MN_001", "NP_REPRESENTATIVE");
					}
				}
				else if (returnValue.equalsIgnoreCase(Constants.FNWL_AGENT)) {
					strExitState = Constants.FNWL_AGENT;
					if (Constants.TRUE.equalsIgnoreCase(priorityFlag)) {
						caa.createMSPKey_FNWL(caa, data, "FNWL_MN_001", "TP_AGENT");
					}
					else {
						caa.createMSPKey_FNWL(caa, data, "FNWL_MN_001", "NP_AGENT");
					}
				}
				else if (returnValue.equalsIgnoreCase(Constants.FNWL_CUSTOMER)) {
					strExitState = Constants.FNWL_CUSTOMER;
					caa.createMSPKey_FNWL(caa, data, "FNWL_MN_001", "CUSTOMER");
				}
			}
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in FNWL_MN_001 :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"FNWL_MN_001_VALUE :: "+strExitState);
		
		return strExitState;
	}

}
