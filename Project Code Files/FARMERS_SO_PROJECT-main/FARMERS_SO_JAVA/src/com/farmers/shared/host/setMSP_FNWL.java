package com.farmers.shared.host;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class setMSP_FNWL extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws Exception {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		
		try {
			
			String priorityFlag = null != data.getSessionData(Constants.FNWL_PRIORITY_AGENT_FLAG) ? (String) data.getSessionData(Constants.FNWL_PRIORITY_AGENT_FLAG) : Constants.FALSE;
			
			if (currElementName.equalsIgnoreCase("Set_MSP_NoMatchCollectAOR")) {
				if (Constants.TRUE.equalsIgnoreCase(priorityFlag)) {
					caa.createMSPKey_FNWL(caa, data, "FNWL_MN_002", "TP_NOMATCH");
				}
				else {
					caa.createMSPKey_FNWL(caa, data, "FNWL_MN_002", "NP_NOMATCH");
				}
			}
			else if (currElementName.equalsIgnoreCase("Set_MSP_NoInputCollectAOR")) {
				if (Constants.TRUE.equalsIgnoreCase(priorityFlag)) {
					caa.createMSPKey_FNWL(caa, data, "FNWL_MN_002", "TP_NOINPUT");
				}
				else {
					caa.createMSPKey_FNWL(caa, data, "FNWL_MN_002", "NP_NOINPUT");
				}
			}
			else if (currElementName.equalsIgnoreCase("Set_MSP_Host_003_Failure")) {
				if (Constants.TRUE.equalsIgnoreCase(priorityFlag)) {
					caa.createMSPKey_FNWL(caa, data, "FNWL_HOST_003", "TP_API_FAILURE");
				}
				else {
					caa.createMSPKey_FNWL(caa, data, "FNWL_HOST_003", "NP_API_FAILURE");
				}
			}
			else if (currElementName.equalsIgnoreCase("setMSP_NonLifePolicyAgentDisambig")) {
				if (Constants.TRUE.equalsIgnoreCase(priorityFlag)) {
					caa.createMSPKey_FNWL(caa, data, "FNWL_AGENTPOLICYDISAMBIG", "TP_NON_LIFE_POLICY");
				}
				else {
					caa.createMSPKey_FNWL(caa, data, "FNWL_AGENTPOLICYDISAMBIG", "NP_NON_LIFE_POLICY");
				}
			}
			else if (currElementName.equalsIgnoreCase("setMSP_PolicyTypeAgentDisambig")) {
				caa.createMSPKey_FNWL(caa, data, "FNWL_AGENTPOLICYDISAMBIG", "VUL_POLICY");
			}
			else if (currElementName.equalsIgnoreCase("setMSP_issueDateAgentDisambig")) {
				caa.createMSPKey_FNWL(caa, data, "FNWL_AGENTPOLICYDISAMBIG", "IUL_POLICY_BEFORE_2019");
			}
			else if(currElementName.equalsIgnoreCase("setMSP__HOST_005_Failure")){
				caa.createMSPKey_FNWL(caa, data, "FNWL_HOST_005", "API_FAILURE");
			}
			else if (currElementName.equalsIgnoreCase("setMSP_NonLifePolicyIDProcess")) {
				caa.createMSPKey_FNWL(caa, data, "CUSTOMERID", "NON_LIFE_POLICY");
			}
			else if(currElementName.equalsIgnoreCase("setMSP__PolicyTypeIDProcess")){
				caa.createMSPKey_FNWL(caa, data, "CUSTOMERID", "VUL_POLICY");
			}
			else if(currElementName.equalsIgnoreCase("setMSP__IssueDateIDProcess")){
				caa.createMSPKey_FNWL(caa, data, "CUSTOMERID", "IUL_POLICY_BEFORE_2019");
			}
			else if(currElementName.equalsIgnoreCase("setMSP_AuthFailure") || currElementName.equalsIgnoreCase("setMSP_AuthFailure_2") || currElementName.equalsIgnoreCase("setMSP_AuthFailure_3")){
				caa.createMSPKey_FNWL(caa, data, "IDAUTHWPOLICY", "AUTH_FAILURE"); }
			else if(currElementName.equalsIgnoreCase("setMSP_HOST_006_Failure")){
				caa.createMSPKey_FNWL(caa, data, "FNWL_HOST_006", "API_FAILURE");
			}
			else if(currElementName.equalsIgnoreCase("setMSP_NoPolicyFoundPTIDProcess")){
				caa.createMSPKey_FNWL(caa, data, "FNWL_HOST_006", "NO_POLICY_FOUND");
			}
			else if(currElementName.equalsIgnoreCase("setMSP_MultiplePolicyFoundPTIDProcess")){
				caa.createMSPKey_FNWL(caa, data, "FNWL_HOST_006", "MULTIPLE_POLICIES_FOUND");
			}
			else if(currElementName.equalsIgnoreCase("setMSP_IssueDateCustDisambig")){
				caa.createMSPKey_FNWL(caa, data, "CUSTOMERPOLICYDISAMBIG", "IUL_POLICY_BEFORE_2019");
			}
			
			strExitState = Constants.SU;
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in setMSP_CollectAOR :: "+e);
			caa.printStackTrace(e);
		}
		
		return strExitState;
	}

}
