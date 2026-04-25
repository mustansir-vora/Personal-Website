package com.farmers.bc;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;



import com.audium.server.cvpUtil.DateTimeUtil;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class EPSL_MN_007_VALUE extends DecisionElementBase{

	public String doDecision(String currElementName, DecisionElementData data)
			throws Exception {
		String strExitState=Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try{
			String strReturnValue = (String) data.getElementData("EPSL_MN_007_Call","Return_Value");
			data.addToLog(currElementName, " EPSL_MN_007 : Return_Value :: "+strReturnValue);
			data.setSessionData("EPSL_MN_007_VALUE", strReturnValue);
			if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;

			}else if(strReturnValue!=null) {
				String[] timeParts = strReturnValue.split(":");
				int hours = Integer.parseInt(timeParts[0]);
				int minutes = Integer.parseInt(timeParts[1]);
				String convertedTime = String.format("%02d:%02d", hours, minutes);
				data.addToLog(currElementName, "Value of convertedTime : "+convertedTime);
					data.setSessionData(Constants.S_LEAVESTARTTIME,convertedTime);
					String prompt=convertedTime.replaceAll(":", ".");
					data.setSessionData(Constants.VXMLParam3, prompt);
					data.setSessionData(Constants.VXMLParam4, "NA");
					
					strExitState=Constants.SU;
			}
			else strExitState=Constants.ER;
			data.addToLog(currElementName, "Value of VXMLParam3 : "+(String)data.getSessionData(Constants.VXMLParam3));

		}
		catch(Exception e){
			data.addToLog(currElementName,"Exception in EPSL_MN_007_VALUE :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"EPSL_MN_007_VALUE :: "+strExitState);

//		String menuExitState = strExitState;
//		if(strExitState.contains(" ")) menuExitState = menuExitState.replaceAll(" ", "_");
//		if(null != (String)data.getSessionData("EPSL_MN_007_"+menuExitState) && !((String)data.getSessionData("EPSL_MN_007_"+menuExitState)).isEmpty()) menuExitState = (String)data.getSessionData("EPSL_MN_007_"+menuExitState);
//		data.addToLog(currElementName, "Final Value of Menu Exit State for EPSL_MN_007: "+menuExitState);
//		data.setSessionData(Constants.S_MENU_SELCTION_KEY, data.getSessionData(Constants.S_BU)+":EPSL_MN_007:"+menuExitState);


		return strExitState;
	}
	


}
