package com.farmers.shared.bc;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;

public class gdfhoteventTransferCheck extends DecisionElementBase {

	@Override
	public String doDecision(String arg0, DecisionElementData data) throws Exception {
		// TODO Auto-generated method stub
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		String hoteventtransferFlag= (String)data.getSessionData("S_HOTEVENT_TRANSFER");
		if(null!=hoteventtransferFlag && "Y".equalsIgnoreCase(hoteventtransferFlag)) {
			return "YES";
		}
		return "NO";
	}

}
