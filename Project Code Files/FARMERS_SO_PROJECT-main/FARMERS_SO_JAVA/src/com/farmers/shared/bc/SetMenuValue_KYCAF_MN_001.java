package com.farmers.shared.bc;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.Constants;

public class SetMenuValue_KYCAF_MN_001 extends DecisionElementBase{
	
	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws Exception {
		String exitState = Constants.ER;
		
		if (null != data.getSessionData("KYCAF_MN_001_VALUE")) {
			data.setSessionData("KYCAF_MN_001_VALUE", (String) data.getSessionData("KYCAF_MN_001_VALUE") + " - DB MISTACH");
		}
		return Constants.SU;
	}

}
