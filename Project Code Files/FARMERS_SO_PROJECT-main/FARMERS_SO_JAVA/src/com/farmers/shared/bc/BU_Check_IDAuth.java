package com.farmers.shared.bc;

import java.util.ArrayList;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class BU_Check_IDAuth extends DecisionElementBase{

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String exitState = "OTHERS";
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		String strBU = (String) data.getSessionData("S_BU");
		
		try {
			
			data.setSessionData("SIDA_PA_018", "Sorry.");
			data.setSessionData("SIDA_PA_019", "Let's try this one more time.");
			data.setSessionData("SIDA_PA_020", "Looks like I'm still having trouble. Lets try this another way.");
			data.setSessionData("SIDA_PA_021", "Sorry.");
			data.setSessionData("SIDA_PA_022", "Let's try this one more time.");
			data.setSessionData("SIDA_PA_023", "I was not able to find any policies with that information");
			data.setSessionData("SIDA_PA_024", "I recognize you're asking for a representative but providing your information will allow me to better assist you.");
			
			ArrayList<String> strBristolCode = (ArrayList<String>) data.getSessionData("BRISTOL_LOB_LIST");
			ArrayList<String>  strFarmersCode = (ArrayList<String>) data.getSessionData("FARMER_LOB_LIST");
			ArrayList<String>  strForemostCode = (ArrayList<String>) data.getSessionData("FOREMOST_LOB_LIST");
			ArrayList<String>  strFWSCode = (ArrayList<String>) data.getSessionData("FWS_LOB_LIST");
			ArrayList<String>  str21stCode = (ArrayList<String>) data.getSessionData("21st_LOB_LIST");
			
			data.addToLog(currElementName, " PolicyLookup : strBU :: "+strBU);
			data.addToLog(currElementName, " A_BRISTOL_LOB : "+strBristolCode);
			data.addToLog(currElementName, " A_FARMERS_LOB : "+strFarmersCode);
			data.addToLog(currElementName, " A_FOREMOST_LOB : "+strForemostCode);
			data.addToLog(currElementName, " A_FWS_LOB : "+strFWSCode);
			data.addToLog(currElementName, " A_21ST_LOB : "+str21stCode);
			
			if (null != data.getSessionData("S_FLAG_FOREMOST_BU") && Constants.STRING_YES.equalsIgnoreCase((String) data.getSessionData("S_FLAG_FOREMOST_BU"))) {
				exitState = "SPECIALTY";
			}
			else if (strForemostCode!=null && strBU!=null && strForemostCode.contains(strBU)) {
				exitState = "SPECIALTY";
			}else if (null != data.getSessionData("S_FLAG_BW_BU") && Constants.STRING_YES.equalsIgnoreCase((String) data.getSessionData("S_FLAG_BW_BU"))) {
				exitState = "BW";
				
			}else if (strBristolCode!=null && strBU!=null && strBristolCode.contains(strBU)) {
				exitState = "BW";
				
			}
			else {
				data.setSessionData("SPECIALTY_COLLECTION_FLAG", "FALSE");
			}
			data.addToLog(currElementName, "SPECIALTY_COLLECTION_FLAG VALUE :: " + data.getSessionData("SPECIALTY_COLLECTION_FLAG"));
			data.addToLog(currElementName, "Exit State for BU Check Before Policy Collection :: " + exitState);
		}
		catch (Exception e) {
			data.addToLog(currElementName, "Exception while forming BillPayments_BU_Check details  :: " + e);
			caa.printStackTrace(e);
		}
		return exitState;
	}

}
