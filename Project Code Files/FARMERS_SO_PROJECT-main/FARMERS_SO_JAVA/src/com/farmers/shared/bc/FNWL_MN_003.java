package com.farmers.shared.bc;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class FNWL_MN_003 extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws Exception {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		
		try {
			String returnValue = (String) data.getElementData("FNWL_MN_003_Call","Return_Value");
			data.addToLog(currElementName, "Menu ID : FNWL_MN_003 :: Menu Value : "+returnValue);
			data.setSessionData(Constants.FNWL_MN_003_VALUE, returnValue);
			String priorityFlag = null != data.getSessionData(Constants.FNWL_PRIORITY_AGENT_FLAG) ? (String) data.getSessionData(Constants.FNWL_PRIORITY_AGENT_FLAG) : Constants.FALSE;
			
			if (null != returnValue && Constants.EmptyString != returnValue) {
				
				if (returnValue.equalsIgnoreCase(Constants.NOMATCH)) {
					strExitState = Constants.NOMATCH;
				}
				else if (returnValue.equalsIgnoreCase(Constants.NOINPUT)) {
					strExitState = Constants.NOINPUT;
				}
				else if (returnValue.equalsIgnoreCase(Constants.HOLD_ON)) {
					strExitState = Constants.HOLD_ON;
				}
				else if (returnValue.equalsIgnoreCase(Constants.DONTHAVE)) {
					strExitState = Constants.DONTHAVE;
				}
				else if (returnValue.equalsIgnoreCase(Constants.REPRESENTATIVE)) {
					strExitState = Constants.REPRESENTATIVE;
					if (Constants.TRUE.equalsIgnoreCase(priorityFlag)) {
						caa.createMSPKey_FNWL(caa, data, "FNWL_MN_003", "TP_REPRESENTATIVE");
					}
					else {
						caa.createMSPKey_FNWL(caa, data, "FNWL_MN_003", "NP_REPRESENTATIVE");
					}
				}
				else {
					data.setSessionData(Constants.FNWL_POLICY_NUM, returnValue);
					strExitState = Constants.SU;
				}
			}
			
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in FNWL_MN_003 :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"FNWL_MN_003_VALUE :: "+strExitState);
		
		return strExitState;
	}

}
