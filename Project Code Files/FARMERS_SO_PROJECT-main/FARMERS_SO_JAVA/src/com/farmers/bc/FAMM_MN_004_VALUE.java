package com.farmers.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class FAMM_MN_004_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {


			String strReturnValue = (String) data.getElementData("FAMM_MN_004_Call", "Return_Value");
			data.addToLog(currElementName, " FAMM_MN_004_VALUE : Return_Value :: " + strReturnValue);
			data.setSessionData("FAMM_MN_004_VALUE", strReturnValue);
			
			if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
				caa.createMSPKey(caa, data, "FAMM_MN_002", "REPRESENTATIVE");
			} else if (strReturnValue.equalsIgnoreCase(Constants.S_MOBILE_HOME)) {
				strExitState = Constants.S_MOBILE_HOME;
				caa.createMSPKey(caa, data, "FAMM_MN_002", "MOBILE HOME");
			} else if (strReturnValue.equalsIgnoreCase(Constants.SPECIALTY_DWELLING)) {
				strExitState = Constants.SPECIALTY_DWELLING;
				caa.createMSPKey(caa, data, "FAMM_MN_002", "SPECIALITY DWELLING");
			} else if (strReturnValue.equalsIgnoreCase(Constants.S_RECREATIONALPRODUCTS)) {
				strExitState = Constants.S_RECREATIONALPRODUCTS;
				caa.createMSPKey(caa, data, "FAMM_MN_002", "RECREATIONAL PRODUCTS");
			} else if (strReturnValue.equalsIgnoreCase(Constants.S_AUTO)) {
				strExitState = Constants.S_AUTO;
				caa.createMSPKey(caa, data, "FAMM_MN_002", "AUTO");
			}else if (strReturnValue.equalsIgnoreCase("Earthquake")) {
				strExitState = "Earthquake";
				//All other option will use existing MSPs for FAMM_MN_002 menu, New MSP will only be created for Earthquake option
				caa.createMSPKey(caa, data, "FAMM_MN_004", "EARTHQUAKE"); 
				
				// Setting agent segmentation
				String agentType ="";
				if(null!=data.getSessionData(Constants.S_AgentType_CCAI)) {
				 agentType = (String) data.getSessionData(Constants.S_AgentType_CCAI);
				 data.addToLog("Agent type: ", agentType);
				}else {
					data.addToLog("Agent type is not found", "");
				}								
				if(null==data.getSessionData(Constants.S_AGENT_SEGMENTATION)&& "IA".equalsIgnoreCase(agentType)) {
					data.setSessionData(Constants.S_AGENT_SEGMENTATION, "IA agent");
					data.addToLog("Agent segmentation set to : ", (String)data.getSessionData(Constants.S_AGENT_SEGMENTATION));
				}else if(null==data.getSessionData(Constants.S_AGENT_SEGMENTATION)&& "EA".equalsIgnoreCase(agentType)) {
					data.setSessionData(Constants.S_AGENT_SEGMENTATION, "EA agent");
					data.addToLog("Agent segmentation set to : ", (String)data.getSessionData(Constants.S_AGENT_SEGMENTATION));
				}
			} else if (strReturnValue.equalsIgnoreCase(Constants.S_REPRESENTATIVE)) {
				strExitState = Constants.S_REPRESENTATIVE;
				caa.createMSPKey(caa, data, "FAMM_MN_002", "REPRESENTATIVE");
			} else if (strReturnValue.equalsIgnoreCase(Constants.S_DEFAULT)) {
				strExitState = Constants.S_DEFAULT;
				caa.createMSPKey(caa, data, "FAMM_MN_002", "REPRESENTATIVE");
			} else {
				strExitState = Constants.ER;
			}


		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in FAMM_MN_004_VALUE :: " + e);
			caa.printStackTrace(e);
		}

		//		String menuExsitState = strExitState;
		//		if (strExitState.contains(" "))
		//			menuExsitState = menuExsitState.replaceAll(" ", "_");
		//		if (null != (String) data.getSessionData("FAMM_MN_002_" + menuExsitState)
		//				&& !((String) data.getSessionData("FAMM_MN_002_" + menuExsitState)).isEmpty())
		//			menuExsitState = (String) data.getSessionData("FAMM_MN_002_" + menuExsitState);
		//		data.addToLog(currElementName, "Final Value of Menu Exit State for FAMM_MN_002: " + menuExsitState);
		//		data.setSessionData(Constants.S_MENU_SELECTION_KEY,
		//				data.getSessionData(Constants.S_BU) + ":FAMM_MN_002:" + menuExsitState);

		data.addToLog(currElementName, "FAMM_MN_002_VALUE :: " + strExitState);
		return strExitState;
	}
}