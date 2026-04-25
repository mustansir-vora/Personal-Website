package com.farmers.bc;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.Constants;

public class Check_FmstAgent_CACallers extends DecisionElementBase{

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws Exception {
		// TODO Auto-generated method stub
		String policyNum="";
		String callerState="";
		if(null!=data.getSessionData(Constants.S_POLICY_NUM)||"".equalsIgnoreCase((String)data.getSessionData(Constants.S_POLICY_NUM))){
		 policyNum = (String)data.getSessionData(Constants.S_POLICY_NUM);
		}
		
		if(null!=data.getSessionData(Constants.S_STATECODE)||"".equalsIgnoreCase((String)data.getSessionData(Constants.S_STATECODE))){
		 callerState= (String)data.getSessionData(Constants.S_STATECODE);
		}
		
		data.addToLog("Policy Num: "+policyNum+" ,State:", callerState);
		
		if(policyNum.startsWith("48") && "CA".equalsIgnoreCase(callerState)) {
			return "CA Caller";
		}
		
		return "Others";
	}

}
