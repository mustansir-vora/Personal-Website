package com.farmers.bc;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class Check_Agent_Customer_Other extends DecisionElementBase{

	@Override
	public String doDecision(String arg0, DecisionElementData data) throws Exception {
		String strExitState = "Other";
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		String strIsKYCEnabled = Constants.EmptyString;
		String strCallerType = Constants.EmptyString;
		String strIsKYCDisabledAgent = Constants.EmptyString;
		String strAgentOptedNo = Constants.EmptyString;
		String currElementName = data.getCurrentElement();
		try {
			
			String isCallerAgentMenuOpted = (String) data.getSessionData("S_KYCMF_MN_001");
			  if(isCallerAgentMenuOpted!=null && !isCallerAgentMenuOpted.isEmpty() && isCallerAgentMenuOpted.equalsIgnoreCase("N")) {
				 data.addToLog(data.getCurrentElement(), "KYCMF_MN_001 menu is not opted to customer. So IVR will offer FACM_MN_001 menu");
				  strExitState="Other";
			  }else {
				  strIsKYCEnabled = (String) data.getSessionData("S_KYC_ENABLED");
					strCallerType = (String) data.getSessionData("S_CALLLER_TYPE");
					strIsKYCDisabledAgent = (String) data.getSessionData("S_KYC_DISABLED_AGENT");
					strAgentOptedNo = (String) data.getSessionData("S_AGENT_OPTED_NO");
					data.addToLog(currElementName, "KYC Disabled Agent Flag :: " + strIsKYCDisabledAgent + " :: KYC Enabled :: " + strIsKYCEnabled + " :: Caller Type :: " + strCallerType + " :: Agent Opted No :: " + strAgentOptedNo);
					if(null != strIsKYCEnabled && "TRUE".equalsIgnoreCase(strIsKYCEnabled)) {
						if(null != strCallerType && "02".equalsIgnoreCase(strCallerType)) {
							if(null != strAgentOptedNo && !strAgentOptedNo.isEmpty() && "Y".equalsIgnoreCase(strAgentOptedNo)) {
								strExitState = "Customer";
							}else {
								strExitState = "Agent";
							}
						}else if(null != strCallerType && "01".equalsIgnoreCase(strCallerType)) {
							strExitState = "Customer";
						}else {
							strExitState="Other";
						}
					}else {
						if(null != strIsKYCDisabledAgent && "Y".equalsIgnoreCase(strIsKYCDisabledAgent)) {
							strExitState = "Agent";
						}else {
							strExitState="Other";
						}
					}
			  }
			
		}catch(Exception e) {
			data.addToLog(currElementName, "Exception while forming host reporting for Intermediaterinformationmanagement API call  :: " + e);
			caa.printStackTrace(e);
		}
		return strExitState;
	}
	

}
