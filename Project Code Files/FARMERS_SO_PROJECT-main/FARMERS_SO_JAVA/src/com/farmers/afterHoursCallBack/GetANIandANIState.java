package com.farmers.afterHoursCallBack;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class GetANIandANIState  extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		 String last4digit="";
		 String prompt="";
		try {
			String strANI = (String)data.getSessionData(Constants.S_ANI);
			data.addToLog(currElementName, "ANI is :"+data.getSessionData(Constants.S_ANI));
			if (strANI != null && strANI.length() == 10) {
	             last4digit= strANI.substring(strANI.length() - 4);
	        } 
		prompt= spellChar(last4digit);
		prompt = "\""+prompt+"\"";
		prompt = prompt.replaceAll("0", " zero ");
			data.setSessionData(Constants.VXMLParam1, prompt);
			data.setSessionData(Constants.VXMLParam2, "NA");
	    	data.setSessionData(Constants.VXMLParam3, "NA");
	    	data.setSessionData(Constants.VXMLParam4, "NA");
	    	data.addToLog(currElementName, "VXMLParam1 :"+data.getSessionData(Constants.S_ANI));
	    	if(!strANI.equalsIgnoreCase(null)&&!strANI.equalsIgnoreCase("")) {
	    		strExitState= "done";	
	    	}
	    	else {
	    		data.addToLog(currElementName,"The ANI is not setted in the Session!!");
	    	}
	    	
			}
			catch(Exception e) {
				data.addToLog(currElementName,"Exception in Getting the ANI State  :: "+e);
				caa.printStackTrace(e);
			}
		return strExitState ;
	}
	public String spellChar(String number) { 
		String returnString =number;
		if(number!=null) {
		char[] spellChar = number.toCharArray();
		returnString = "";
		for (char c : spellChar) {
			returnString = returnString +" "+c;
		}
		
		}
		return returnString;
	}
}
