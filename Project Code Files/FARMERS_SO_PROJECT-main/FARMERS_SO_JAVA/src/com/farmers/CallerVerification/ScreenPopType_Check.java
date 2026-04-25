package com.farmers.CallerVerification;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class ScreenPopType_Check extends DecisionElementBase{


	@Override
	public String doDecision(String currElementName, DecisionElementData data)  throws Exception {
		
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		
		try {
			
			String screenPopUp =(String) data.getSessionData(Constants.S_SCREEN_POPUP_KEY);
			String strScreenPopCheck = (String)data.getApplicationAPI().getApplicationData(Constants.A_CV_SCREENPOPUP_CHECK);
			
			data.addToLog(currElementName, "screenPopUp Key :"+screenPopUp);
			data.addToLog(currElementName, "A_CV_SCREENPOPUP_CHECK :"+strScreenPopCheck);
			
			if(strScreenPopCheck!=null &&screenPopUp!=null && !strScreenPopCheck.isEmpty() && !screenPopUp.isEmpty() && strScreenPopCheck.contains(screenPopUp)) {
				
				strExitState = Constants.SU;
				
			} else {
				
				strExitState = Constants.ER;
			} 
		}catch (Exception e) {
				data.addToLog(currElementName,"Exception in ScreenPopUp :: "+e);
				caa.printStackTrace(e);
			}
			data.addToLog(currElementName,"ScreenPopUp :: "+strExitState);
			
		
		
		
		
	
		return strExitState;
	}
	
	

}
