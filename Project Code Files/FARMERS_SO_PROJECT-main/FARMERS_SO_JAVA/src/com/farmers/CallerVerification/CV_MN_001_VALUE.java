package com.farmers.CallerVerification;


import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class CV_MN_001_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);
		objHostDetails.setinitalValue();
		 
		 String strRespBody="200";
		
		
		try {
			
			String strReturnValue = (String) data.getElementData("CV_MN_001_Call","Return_Value");
			
			
			
			data.addToLog(currElementName, " CV_MN_001_Call : Return_Value :: "+strReturnValue);
			data.setSessionData(Constants.CV_MN_001_VALUE, strReturnValue);
			if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.STRING_YES;
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.STRING_YES;
			} else if(strReturnValue.equalsIgnoreCase(Constants.SU)) {
				strExitState = Constants.STRING_YES;
			}  else if(strReturnValue.equalsIgnoreCase(Constants.DONE)) {
				strExitState = Constants.STRING_YES;
			} else {
				strExitState = Constants.STRING_NO;
			}
		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in CV_MN_001_VALUE :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"CV_MN_001_VALUE :: "+strExitState);
		
		try {
			String CCAI_Event_Data = (String) data.getSessionData(Constants.CCAI_Event_Data);
			objHostDetails.startHostReport(currElementName,"CCAI_OUTPUT_CISCO", CCAI_Event_Data,"", "");
			objHostDetails.endHostReport(data,strRespBody , strExitState.equalsIgnoreCase(Constants.NO) ? Constants.NO : Constants.YES, "200","");
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming CV_MN_001_Call   :: "+e);
			caa.printStackTrace(e);
		}
		
		
		/*
		String menuExsitState = strExitState;
		if(strExitState.contains(" ")) menuExsitState = menuExsitState.replaceAll(" ", "_");
		if(null != (String)data.getSessionData("CV_MN_001_"+menuExsitState) && !((String)data.getSessionData("CV_MN_001_"+menuExsitState)).isEmpty()) menuExsitState = (String)data.getSessionData("CV_MN_001_"+menuExsitState);
		data.addToLog(currElementName, "Final Value of Menu Exit State for CV_MN_001: "+menuExsitState);
		data.setSessionData(Constants.S_MENU_SELCTION_KEY, data.getSessionData(Constants.S_BU)+":CV_MN_001:"+menuExsitState);
		*/
		return strExitState;
	}
}
