package com.farmers.shared.host;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class FNWL_AuthCriteriaCheck extends DecisionElementBase{

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws Exception {
		String strExitState = Constants.STRING_NO;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);
		
		try {
			
			String policyviaANIFlag = null != (String) data.getSessionData(Constants.FNWL_POLICY_FOUND_VIA_ANI) ? (String) data.getSessionData(Constants.FNWL_POLICY_FOUND_VIA_ANI) : Constants.EmptyString;
			String policyviaPCNFlag = null != (String) data.getSessionData(Constants.FNWL_POLICY_FOUND_VIA_POLICY) ? (String) data.getSessionData(Constants.FNWL_POLICY_FOUND_VIA_POLICY) : Constants.EmptyString;
			String DOBmatchedFlag = null != (String) data.getSessionData(Constants.FNWL_DOB_MATCHED) ? (String) data.getSessionData(Constants.FNWL_DOB_MATCHED) : Constants.EmptyString;
			String ZIPmatchedFlag = null != (String) data.getSessionData(Constants.FNWL_ZIP_MATCHED) ? (String) data.getSessionData(Constants.FNWL_ZIP_MATCHED) : Constants.EmptyString;
			String SSNmatchedFlag = null != (String) data.getSessionData(Constants.FNWL_SSN_MATCHED) ? (String) data.getSessionData(Constants.FNWL_SSN_MATCHED) : Constants.EmptyString;
			
			if (Constants.TRUE.equalsIgnoreCase(policyviaANIFlag) && Constants.TRUE.equalsIgnoreCase(policyviaPCNFlag) && (Constants.TRUE.equalsIgnoreCase(DOBmatchedFlag) || Constants.TRUE.equalsIgnoreCase(ZIPmatchedFlag) || Constants.TRUE.equalsIgnoreCase(SSNmatchedFlag))) {
				data.addToLog(currElementName, "ANI + POLICY + At least 1 Attribute matched :: Exit State :: YES");
				strExitState = Constants.STRING_YES;
			}
			else if (Constants.TRUE.equalsIgnoreCase(policyviaPCNFlag) && ((Constants.TRUE.equalsIgnoreCase(DOBmatchedFlag) && Constants.TRUE.equalsIgnoreCase(ZIPmatchedFlag)) || (Constants.TRUE.equalsIgnoreCase(ZIPmatchedFlag) && Constants.TRUE.equalsIgnoreCase(SSNmatchedFlag)) || (Constants.TRUE.equalsIgnoreCase(DOBmatchedFlag) && Constants.TRUE.equalsIgnoreCase(SSNmatchedFlag)))) {
				data.addToLog(currElementName, "POLICY + At least 2 Attribute matched :: Exit State :: YES");
				strExitState = Constants.STRING_YES;
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in AuthCriteria Check Method :: " + e);
			caa.printStackTrace(e);
		}
		return strExitState;
	}
	
	

}
