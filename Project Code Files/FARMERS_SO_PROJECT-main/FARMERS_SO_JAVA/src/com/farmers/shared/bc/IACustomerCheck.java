package com.farmers.shared.bc;


import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class IACustomerCheck extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {

		String strExitState = Constants.STRING_NO;
		String policySource = Constants.EmptyString;
		String policyCategory = Constants.EmptyString ;
		String isIAcheckEnabled = Constants.EmptyString ;
		
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {
			
			if (null != data.getSessionData(Constants.S_POLICY_SOURCE)) {
				policySource = (String) data.getSessionData(Constants.S_POLICY_SOURCE);
				data.addToLog(currElementName, "S_POLICY_SOURCE :: " + policySource);
			}
			
			if (null != data.getSessionData(Constants.S_POLICY_CATEGORY)) {
				policyCategory = (String) data.getSessionData(Constants.S_POLICY_CATEGORY);
				data.addToLog(currElementName, "S_POLICY_CATEGORY:: " + policyCategory);
			}
			
			if (null != data.getSessionData("S_IA_CUSTOMER_CHECK")) {
				isIAcheckEnabled = (String) data.getSessionData("S_IA_CUSTOMER_CHECK");
				data.addToLog(currElementName, "S_IA_CUSTOMER_CHECK:: " + isIAcheckEnabled);
			}
			
			
			if(("BW".equalsIgnoreCase(policySource)||"FM".equalsIgnoreCase(policySource)) && "IA".equalsIgnoreCase(policyCategory)
					&&"Y".equalsIgnoreCase(isIAcheckEnabled)) {
				data.setSessionData(Constants.S_AGENT_SEGMENTATION , "IA Customer"); 
				data.addToLog(currElementName,"S_AGENT_SEGMENTATION  ::" +(String)data.getSessionData(Constants.S_AGENT_SEGMENTATION ));
				strExitState = Constants.STRING_YES;
			}
			else {
				strExitState = Constants.STRING_NO;
			}

			
		}
		catch (Exception e) {
			data.addToLog(currElementName, "Exception in IACustomerCheck :: " + e);
			caa.printStackTrace(e);
		}
		
		
		return strExitState;
	}

}
