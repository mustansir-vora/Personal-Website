package com.farmers.bc;

import java.util.ArrayList;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class KYC_IA_PremierAgent_Check extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		
		String strExitState = Constants.ER;
		String agentType = (String) data.getSessionData(Constants.S_AgentType_CCAI);
		String isIAPremierAgent = (String) data.getSessionData("isIAPremierAgent");
		data.addToLog("Agent Type: ", agentType);
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		ArrayList<String> strBristolCode = (ArrayList<String>) data.getSessionData("BRISTOL_LOB_LIST");
		ArrayList<String>  strForemostCode = (ArrayList<String>) data.getSessionData("FOREMOST_LOB_LIST");
		ArrayList<String>  strFWSCode = (ArrayList<String>) data.getSessionData("FWS_LOB_LIST");
		String strBU = (String) data.getSessionData("S_BU");
		data.addToLog("BU : ", strBU);
		data.addToLog("isIAPremierAgent flag:", (String)data.getSessionData("isIAPremierAgent"));

		try {
			 if ((strBristolCode.contains(strBU) || strForemostCode.contains(strBU)|| strFWSCode.contains(strBU))  
				        && "IA".equalsIgnoreCase(agentType) && "Y".equalsIgnoreCase(isIAPremierAgent)) {
				 
				        strExitState = Constants.SU;
				        data.setSessionData(Constants.S_AGENT_SEGMENTATION,"IA Premier");
				        caa.createMSPKey(caa, data, "KYC_IA_PREMIER_AGENT", "IA_Premier_Agent");
				    }

			
			else {
				strExitState = Constants.ER;
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in KYC_IA_PREMIER_AGENT_Check :: " + e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName, "KYC_IA_PREMIER_AGENT_CHECK :: " + strExitState);

		
		return strExitState;
	}
}