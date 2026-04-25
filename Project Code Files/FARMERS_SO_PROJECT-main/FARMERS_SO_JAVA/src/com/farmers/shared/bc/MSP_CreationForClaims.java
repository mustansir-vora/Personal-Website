package com.farmers.shared.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class MSP_CreationForClaims extends DecisionElementBase {

	
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		
		try {
			caa.createMSPKey(caa, data, "SMM_MN_001", "CLAIMS");
			strExitState = Constants.SU;
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in MSP_CreationForClaims :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName, "MSP_CreationForClaims : strExitState : "+strExitState);
		return strExitState;
		
	}

}
