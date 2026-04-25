package com.farmers.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class KYC_IA_Handling_Check extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		
		String strExitState = Constants.ER;
		String agentType = (String) data.getSessionData(Constants.S_AgentType_CCAI);
		data.addToLog("Agent Type: ", agentType);
		String category = (String) data.getSessionData(Constants.S_ORIGINAL_CATEGORY);
		data.addToLog("Category: ", category);
		//String stateCodeCompare = "CO, ID, MO, NH, NM, VT, TN";
//		String stateCodeCompare = (String) data.getSessionData(Constants.S_IA_STATE_CODE);
		String BWStateCode=(String) data.getSessionData(Constants.BW_IA_STATE_CODE);
		 data.addToLog("BW AGENT STATE CODES: ", BWStateCode);
		String FWSStateCode=(String) data.getSessionData(Constants.FWS_IA_STATE_CODE);
		 data.addToLog("FWS AGENT STATE CODES: ", FWSStateCode);
		String FRMSTStateCode=(String) data.getSessionData(Constants.SPECIALITY_IA_STATE_CODE);
		 data.addToLog("Speciality AGENT STATE CODES: ", FRMSTStateCode);
//	    data.addToLog("IA AGENT STATE CODES: ", stateCodeCompare);
		String stateCodeFromSession = (String) data.getSessionData(Constants.S_STATECODE);
        data.addToLog("State code to be checked: ", stateCodeFromSession);
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);

		try {
			 if ("IA".equalsIgnoreCase(agentType)) {
				 if("BW_PROD".equalsIgnoreCase(category) && stateCodeFromSession != null && BWStateCode.contains(stateCodeFromSession)) {
			        strExitState = "BW";
			        data.setSessionData(Constants.S_AGENT_SEGMENTATION,"IA Agent");
			        caa.createMSPKey(caa, data, "KYC_IA_HANDLING", "IA_Agent");
			    }else if("FM-AGENT".equalsIgnoreCase(category) && stateCodeFromSession != null && FRMSTStateCode.contains(stateCodeFromSession)) {
			    	 strExitState = "FM";
				        data.setSessionData(Constants.S_AGENT_SEGMENTATION,"IA Agent");
				        caa.createMSPKey(caa, data, "KYC_IA_HANDLING", "IA_Agent");
			    }else if(("FWS_ARC".equalsIgnoreCase(category)||"FWS ARC IA".equalsIgnoreCase(category)||"FWS ARC EA".equalsIgnoreCase(category)) && stateCodeFromSession != null && FWSStateCode.contains(stateCodeFromSession)) {
			    	    strExitState = "FWS";
				        data.setSessionData(Constants.S_AGENT_SEGMENTATION,"IA Agent");
				        caa.createMSPKey(caa, data, "KYC_IA_HANDLING", "IA_Agent");
			    }
			 } 		
			else {
				strExitState = Constants.ER;
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in KYC_IA_Handling_Check :: " + e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName, "KYC_IA_Handling_Check :: " + strExitState);

		
		return strExitState;
	}
}