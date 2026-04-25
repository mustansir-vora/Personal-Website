package com.farmers.shared.bc;

import java.util.ArrayList;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class BU_Check_IVRToText extends DecisionElementBase{

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws Exception {
		String strExitState = Constants.STRING_NO;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		String strBU = Constants.EmptyString;
		
		try {
			
			strBU = (String) data.getSessionData("S_BU");
			
			ArrayList<String> strBristolCode = (ArrayList<String>) data.getSessionData("BRISTOL_LOB_LIST");
			ArrayList<String>  strFarmersCode = (ArrayList<String>) data.getSessionData("FARMER_LOB_LIST");
			
			data.addToLog(currElementName, " strBU :: "+strBU);
			data.addToLog(currElementName, " A_BRISTOL_LOB : "+strBristolCode);
			data.addToLog(currElementName, " A_FARMERS_LOB : "+strFarmersCode);
			
			if (null != data.getSessionData("S_FLAG_FDS_BU") && Constants.STRING_YES.equalsIgnoreCase((String) data.getSessionData("S_FLAG_FDS_BU"))) {
				strExitState = "YES";
			}
			else if (strFarmersCode!=null && strBU!=null && strFarmersCode.contains(strBU)) {
				//CS1300148, IVR to text issue -Arshath start
				data.setSessionData("S_FLAG_FDS_BU", Constants.STRING_YES);
				//CS1300148, IVR to text issue -Arshath End
				strExitState = "YES";
			}
			else if (null != data.getSessionData("S_FLAG_BW_BU") && Constants.STRING_YES.equalsIgnoreCase((String) data.getSessionData("S_FLAG_BW_BU"))) {
				strExitState = "YES";
			}
			else if (strBristolCode!=null && strBU!=null && strBristolCode.contains(strBU)) {
				//CS1300148, IVR to text issue -Arshath start
				data.setSessionData("S_FLAG_BW_BU", Constants.STRING_YES);
				//CS1300148, IVR to text issue -Arshath End
				strExitState = "YES";
			}
			
			data.addToLog(currElementName, "Exit State for BU Check Before IVR To Text :: " + strExitState);
			
		}
		catch (Exception e) {
			data.addToLog(currElementName, "Exception while forming BillPayments_BU_Check details  :: " + e);
			caa.printStackTrace(e);
		}
		
		return strExitState;
	}

}
