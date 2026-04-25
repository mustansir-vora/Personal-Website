package com.farmers.CallerVerification;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class Caller_Type_check_Commercial extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws Exception {
		
		String strexitstate="ER";
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		
		String screenPopUp = (String) data.getSessionData(Constants.S_SCREEN_POPUP_KEY);
		String strScreenPopCheck = (String)data.getApplicationAPI().getApplicationData(Constants.A_CV_SCREENPOPUP_CHECK_COMM);
		
		String Callertype=(String) data.getSessionData(Constants.S_CALLLER_TYPE);
		
		data.addToLog(currElementName,"screenPopUp Key :"+ screenPopUp);
		data.addToLog(currElementName,"A_CV_SCREENPOPUP_CHECK_COMM :"+strScreenPopCheck);
		try {
			
		if(strScreenPopCheck!=null && screenPopUp!=null && !strScreenPopCheck.isEmpty() && !screenPopUp.isEmpty() && strScreenPopCheck.contains(screenPopUp)) {
			
			if (Callertype.equalsIgnoreCase("02")) {
				
		
			
			strexitstate = Constants.SU;
			}
		
			
		}  
		
		else if(strScreenPopCheck!=null &&screenPopUp!=null && !strScreenPopCheck.isEmpty() && !screenPopUp.isEmpty() && strScreenPopCheck.contains(screenPopUp)) {
			
			if (Callertype.equalsIgnoreCase("01")) {
				
		
			
			strexitstate = Constants.ER;
			}
		
			
		}  
		
		
		
		
		
		
		
		else{
			
			strexitstate = Constants.SU;
		} 
	}catch (Exception e) {
			data.addToLog(currElementName,"Exception in ScreenPopUp :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"ScreenPopUp :: "+strexitstate);
		
		
		
		return strexitstate;
	}

}
