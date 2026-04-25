package com.farmers.bc;

import java.util.ArrayList;
import java.util.regex.Pattern;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class IsAfterHoursSelfServiceAvailable extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.STRING_NO;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			String flowName =  (String) data.getSessionData(Constants.S_FIND_IVR_MAINFLOW);
			data.addToLog(data.getCurrentElement(), "Flow Name : "+flowName);
			flowName = flowName.replaceAll(" ", "");
			data.addToLog(data.getCurrentElement(), "Flow Name post manupulation: "+flowName);
			String AfeterHours_SS_Availble = (String)data.getSessionData("S_AfeterHours_SS_Availble");
			String[] strAllFlowArr = (Pattern.compile("\\|").split(AfeterHours_SS_Availble));
			for(String key : strAllFlowArr) {
				if(flowName.equalsIgnoreCase(key)) {
					data.setSessionData(Constants.S_AFTERHOURS_SELFSERVICE_AVAILABLE, Constants.STRING_YES);
					strExitState = Constants.STRING_YES;
				}
			}
			data.addToLog(data.getCurrentElement(), " Based on Flow name :: S_AFTERHOURS_SELFSERVICE_AVAILABLE :: "+data.getSessionData(Constants.S_AFTERHOURS_SELFSERVICE_AVAILABLE));
			
			if(strExitState.equalsIgnoreCase(Constants.STRING_NO)) {
				String strBU = (String)data.getSessionData(Constants.S_BU);
				String strCategory=(String)data.getSessionData(Constants.S_CATEGORY);
				ArrayList<String> strBristolCode = (ArrayList<String>) data.getSessionData("S_HOOP_BRISTOL_LOB_LIST");
				ArrayList<String>  strFarmersCode = (ArrayList<String>) data.getSessionData("S_HOOP_FARMER_LOB_LIST");
				ArrayList<String>  strFWSCode = (ArrayList<String>) data.getSessionData("S_HOOP_FWS_LOB_LIST");
				ArrayList<String>  str21stCode = (ArrayList<String>) data.getSessionData("S_HOOP_21st_LOB_LIST");

				data.addToLog(currElementName, " Current : strBU :: "+strBU);
				data.addToLog(currElementName, " Current : strCategory :: "+strCategory);
				data.addToLog(currElementName, " S_HOOP_BRISTOL_LOB_LIST : "+strBristolCode);
				data.addToLog(currElementName, " S_HOOP_FARMER_LOB_LIST : "+strFarmersCode);
				data.addToLog(currElementName, " S_HOOP_FWS_LOB_LIST : "+strFWSCode);
				data.addToLog(currElementName, " S_HOOP_21st_LOB_LIST : "+str21stCode);
				
				if((strBristolCode!=null && strBU!=null && strBristolCode.contains(strBU)) 
						|| (strFarmersCode!=null && strBU!=null && strFarmersCode.contains(strBU) 
						|| strBU.equalsIgnoreCase(Constants.S_API_BU_FDS) || strBU.contains(Constants.S_API_BU_FDS) || Constants.S_API_BU_FDS.contains(strBU)) 
						|| (strFWSCode!=null && strBU!=null &&( strFWSCode.contains(strBU) || strBU.contains("FWS")))  
						|| (str21stCode!=null && strBU!=null && str21stCode.contains(strBU))) {
					data.setSessionData(Constants.S_AFTERHOURS_SELFSERVICE_AVAILABLE, Constants.STRING_YES);
					strExitState = Constants.STRING_YES;
				}
				data.addToLog(data.getCurrentElement(), " Based on bussiness unit :: S_AFTERHOURS_SELFSERVICE_AVAILABLE :: "+data.getSessionData(Constants.S_AFTERHOURS_SELFSERVICE_AVAILABLE));
			}
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in IsAfterHoursSelfServiceAvailable method  :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName," IsAfterHoursSelfServiceAvailable  strExitState :: "+strExitState);
		return strExitState;
	}

}