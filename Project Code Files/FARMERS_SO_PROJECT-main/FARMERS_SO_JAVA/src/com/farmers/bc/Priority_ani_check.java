package com.farmers.bc;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class Priority_ani_check extends DecisionElementBase{


	public String doDecision(String currElementName, DecisionElementData data) throws Exception {
		String  strExitState=Constants.STRING_NO;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			String strAniExist = (String) data.getSessionData(Constants.S_ANI_EXIST);
			if(strAniExist.equals(Constants.STRING_YES)) strExitState=Constants.STRING_YES;
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in Priority_ani_check  :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"Priority_ani_check :: "+strExitState);
		String strMenuValue = (String) data.getSessionData(Constants.FNAG_MN_001_VALUE);
		data.addToLog(currElementName, "Value of FNAG_MN_001_VALUE : in Priority Check Class : "+strMenuValue);
		//		if(strMenuValue.contains(" ")) strMenuValue = strMenuValue.replaceAll(" ", "_");
		//		data.addToLog(currElementName, "Value of  FNAG_MN_001: 1 : "+strMenuValue);
		//		if(null != (String)data.getSessionData("FNAG_MN_001_"+strMenuValue) && !((String)data.getSessionData("FNAG_MN_001_"+strMenuValue)).isEmpty()) {
		//			strMenuValue = (String)data.getSessionData("FNAG_MN_001_"+strMenuValue);
		//		}	
		//		data.addToLog(currElementName, "Value of  FNAG_MN_001: 2 : "+strMenuValue);

		if(strExitState.equals(Constants.STRING_YES)) {
			strMenuValue = strMenuValue+"_PRIORITY_ANI";
		}	
		else {
			strMenuValue = strMenuValue+"_NON_PRIORITY_ANI";
		}
		data.addToLog(currElementName, "Final Value of Menu Exit State for FNAG_MN_001: "+strMenuValue);
		//		data.setSessionData(Constants.S_MENU_SELCTION_KEY, data.getSessionData(Constants.S_BU)+":FNAG_MN_001:"+strMenuValue);
		caa.createMSPKey(caa, data, "FNAG_MN_001", strMenuValue);
		return strExitState;
	}


}