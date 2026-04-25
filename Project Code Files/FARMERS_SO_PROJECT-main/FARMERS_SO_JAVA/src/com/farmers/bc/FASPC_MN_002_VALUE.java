package com.farmers.bc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class FASPC_MN_002_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {
			
			String strReturnValue = (String) data.getElementData("FASPC_MN_002_Call", "Return_Value");
			
			data.addToLog(currElementName, " FASPC_MN_002_Call : Return_Value :: " + strReturnValue);
			
			

			if (strReturnValue.equalsIgnoreCase("auto policy")) {
				strExitState = Constants.AUTO;
				
				caa.createMSPKey(caa, data, "FASAM_MN_001", "UNDERWRITING");
				
			} else if (strReturnValue.equalsIgnoreCase("home policy")) {
				strExitState = Constants.HOME;
				caa.createMSPKey(caa, data, "FASHM_MN_001", "UNDERWRITING");
			} else if (strReturnValue.equalsIgnoreCase("Umberlla Policy")) {
				strExitState = "Umberlla";
				caa.createMSPKey(caa, data, "FASUM_MN_001", "UNDERWRITING");
			}
		
			else {
				strExitState = Constants.ER;
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in FASPC_MN_002_VALUE :: " + e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName, "FASPC_MN_002_VALUE :: " + strExitState);

//		data.addToLog(currElementName,"S_MENU_SELECTION_KEY FASPC_MN_001: " + (String) data.getSessionData("S_MENU_SELCTION_KEY"));
		return strExitState;
	}
}