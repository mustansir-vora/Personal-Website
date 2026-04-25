package com.farmers.bc;

import java.util.ArrayList;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class CancelPolicyIntentCheck_BC extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {

		String strExitState = Constants.ER;
		String apptag = Constants.EmptyString;
		double percentile = 0.0;
		String cancelIntent="";
		String dnisbrand = (String) data.getSessionData(Constants.S_BU);
		ArrayList<String> strFarmersCode = (ArrayList<String>) data.getSessionData("FARMER_LOB_LIST");
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		String policyStatus = "";
		try {
            
			//BU
			data.addToLog(currElementName, "BU :: " + dnisbrand);
			
			//APPTAG
			if (null != data.getSessionData(Constants.APPTAG)) {
				apptag = (String) data.getSessionData(Constants.APPTAG);
				data.addToLog(currElementName, "NLU Intent :: " + apptag);
			}
			
			//intent check
			if (null != data.getSessionData("S_CANCEL_POLICY_INTENT")) {
				cancelIntent = (String) data.getSessionData("S_CANCEL_POLICY_INTENT");
				data.addToLog(currElementName, "Cancelpolicy Intent :: " + cancelIntent);
			}
			
			//percentilw value
			if (null != data.getSessionData("percentileValue")) {
				percentile = (Double) data.getSessionData("percentileValue");
				data.addToLog(currElementName, "Percentile Value: " + percentile);

			}else {
				data.addToLog(currElementName, "Percentile Value :: " + null);
			}
			
			//policy status
			if (null != data.getSessionData(Constants.S_MDM_POLICY_STATUS)) {
				policyStatus = (String) data.getSessionData(Constants.S_MDM_POLICY_STATUS);
				data.addToLog(currElementName, "S_MDM_POLICY_STATUS: " + policyStatus);
			}
			
            //REP HANDLING AT VIA FOR CANCEL POLICY INTENT
			if (dnisbrand != null && strFarmersCode != null && (strFarmersCode.contains(dnisbrand)) && "Y".equalsIgnoreCase(cancelIntent)
					&& null!=data.getSessionData("S_REP_REQ") && "Y".equalsIgnoreCase((String)data.getSessionData("S_REP_REQ"))) {
				 data.addToLog(currElementName, "Entered into scenario Rep->"+"BU:FDS, Rep Req:Y, percentile:null/<=0.9");
					if(percentile <= 0.90 || null == data.getSessionData("percentileValue")){
						
						data.setSessionData(Constants.S_POLICY_ATTRIBUTES, "Retention Eligible"); 
						strExitState ="NO";
					}
			}// CANCEL POLICY DISAMBIG FLOW ROUTING CCHECk
			else if (dnisbrand != null && strFarmersCode != null && (strFarmersCode.contains(dnisbrand)) && "Y".equalsIgnoreCase(cancelIntent)
					&& ("active".equalsIgnoreCase(policyStatus) || "A".equalsIgnoreCase(policyStatus))
					&& (percentile <= 0.90 || null == data.getSessionData("percentileValue"))) {
				
                 data.addToLog(currElementName, "Entered into scenario 1->"+"BU:FDS, Status:Active, percentile:null/<=0.9");
                	 strExitState = Constants.STRING_YES;

			} else if (dnisbrand != null && strFarmersCode != null && (strFarmersCode.contains(dnisbrand)) && "Y".equalsIgnoreCase(cancelIntent)
					&& (null == data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE)||null== data.getSessionData(Constants.S_MDM_POLICY_STATUS))) {

				data.addToLog(currElementName, "Entered into scenario 2->"+"BU:FDS, Intent:Cancel Policy, policy:notfound");
					strExitState = Constants.STRING_YES;
			}else if(dnisbrand != null && strFarmersCode != null && (strFarmersCode.contains(dnisbrand)) && "Y".equalsIgnoreCase(cancelIntent)
					&& (!Constants.STRING_YES.equalsIgnoreCase((String)data.getSessionData("VIA_MN_008_VALUE")))){
				
				data.addToLog(currElementName, "Entered into scenario 3->"+"BU:FDS, Intent:Cancel Policy, VIA_MN_008!=Yes");
				strExitState = Constants.STRING_YES;
			} else {
				data.addToLog(currElementName, "Entered into scenario no condition match");
				strExitState = Constants.STRING_NO;

			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in CancelPolicyIntentCheck_BC :: " + e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName, "CancelPolicyIntentCheck_BC :: " + strExitState);
		return strExitState;
	}

}
