package com.farmers.bc;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class OSPM_MN_001_SuccessMSP extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws Exception {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {
			caa.createMSPKey(caa, data, "OSPM_MN_001", "Make a payment/Billing:Authenticated");
			strExitState = Constants.SU;
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in FMOM_MN_004_VALUE :: " + e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName, "FMOM_MN_004_VALUE :: " + strExitState);

		return strExitState;
	}

}
