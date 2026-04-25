package com.farmers.bc;


import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class PayplanCheck extends DecisionElementBase{

	@Override
	public String doDecision(String currentElementName, DecisionElementData data) throws Exception {
		String exitState = "No";
		String payPlan = (String)data.getSessionData("S_FDS_PAYPLAN");
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		
		if(payPlan !=null && !payPlan.isEmpty() && (payPlan.equalsIgnoreCase("PA") || payPlan.equalsIgnoreCase("PB"))) {
			caa.createMSPKey(caa, data, "MERCER_HANDLING", "FDS MERCER");
			exitState = "Yes";
			
		}else {
			data.setSessionData("S_FDS_Mercer_Handling", Constants.Y_FLAG);
		}
		return exitState;
		
	}

}
