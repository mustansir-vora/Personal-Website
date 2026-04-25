package com.farmers.bc;

import java.util.ArrayList;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;
import com.sun.org.apache.bcel.internal.Const;

public class ValidateBU extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER, strBU = Constants.EmptyString;
		String strCategory = Constants.EmptyString;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {
			String flowName =  (String) data.getSessionData(Constants.S_FIND_IVR_MAINFLOW);

			data.addToLog(data.getCurrentElement(), "Flow Name : "+flowName);

			strBU = (String)data.getSessionData(Constants.S_BU);
			strCategory=(String)data.getSessionData(Constants.S_CATEGORY);

			ArrayList<String> strBristolCode = (ArrayList<String>) data.getSessionData("S_HOOP_BRISTOL_LOB_LIST");
			ArrayList<String>  strFarmersCode = (ArrayList<String>) data.getSessionData("S_HOOP_FARMER_LOB_LIST");
			ArrayList<String>  strForemostCode = (ArrayList<String>) data.getSessionData("S_FOREMOST_LOB_LIST");
			ArrayList<String>  strFWSCode = (ArrayList<String>) data.getSessionData("S_HOOP_FWS_LOB_LIST");
			ArrayList<String>  str21stCode = (ArrayList<String>) data.getSessionData("S_HOOP_21st_LOB_LIST");
			ArrayList<String>  strPerstCode = (ArrayList<String>) data.getSessionData("S_A_PER_LOB");
			
			data.addToLog(currElementName, " Current : strBU :: "+strBU);
			data.addToLog(currElementName, " Current : strCategory :: "+strCategory);
			data.addToLog(currElementName, " A_BRISTOL_LOB : "+strBristolCode);
			data.addToLog(currElementName, " A_FARMERS_LOB : "+strFarmersCode);
			data.addToLog(currElementName, " A_FOREMOST_LOB : "+strForemostCode);
			data.addToLog(currElementName, " A_FWS_LOB : "+strFWSCode);
			data.addToLog(currElementName, " A_21ST_LOB : "+str21stCode);

			if(flowName.equalsIgnoreCase("One Specialty")) {
				strBU = flowName;
			}else if(flowName.equalsIgnoreCase("Foremost AARP")) {
				strBU = flowName;
			}else if(flowName.equalsIgnoreCase("Foremost Agent")) {
				strBU = flowName;
			}else if(flowName.equalsIgnoreCase("BW Producer Main Menu")) {
				strBU = flowName;
			}else {
				if(strBristolCode!=null && strBU!=null && strBristolCode.contains(strBU)) {
					strBU = Constants.BU_BW;
				}else if(strPerstCode!=null && strBU!=null && str21stCode.contains(strBU) &&strCategory!="FarmersPL") {
					strBU = Constants.S_API_BU_FDS;
				} else if(strFWSCode!=null && strBU!=null &&( strFWSCode.contains(strBU) || strBU.contains("FWS"))) {
					strBU = Constants.BU_FWS;
				} else if(str21stCode!=null && strBU!=null && str21stCode.contains(strBU)) {
					strBU = Constants.BU_21ST;
				}
				else if(strFarmersCode!=null && strBU!=null && strFarmersCode.contains(strBU) ||
						strBU.equalsIgnoreCase(Constants.S_API_BU_FDS) || strBU.contains(Constants.S_API_BU_FDS) || Constants.S_API_BU_FDS.contains(strBU)) {
					strBU = Constants.S_API_BU_FDS;
				} 

				else {
					strBU = Constants.OTHER;
				}
				
				data.setSessionData("S_UNIQUE_BU", strBU);
			}
		}catch (Exception e) {
			data.addToLog(currElementName,"Exception while validating BU for after hours :: "+e);
			caa.printStackTrace(e);
		}

		return strBU;
	}
}