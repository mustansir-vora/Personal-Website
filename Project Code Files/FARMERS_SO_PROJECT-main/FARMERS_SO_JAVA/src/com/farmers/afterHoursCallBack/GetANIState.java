package com.farmers.afterHoursCallBack;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class GetANIState extends DecisionElementBase{
	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
		String stateName = (String)data.getSessionData(Constants.S_STATENAME);
		data.addToLog(currElementName, "S_STATENAME :"+data.getSessionData(Constants.S_STATENAME));
		if(stateName.contains(" ")) stateName = stateName.replaceAll(" ", ".");
		data.setSessionData(Constants.VXMLParam1, stateName);
		data.setSessionData(Constants.VXMLParam2, "NA");
    	data.setSessionData(Constants.VXMLParam3, "NA");
    	data.setSessionData(Constants.VXMLParam4, "NA");
    	data.addToLog(currElementName, "VXMLParam1 :"+data.getSessionData(Constants.S_STATENAME));
    	if(!stateName.equalsIgnoreCase(null)&&!stateName.equalsIgnoreCase("")) {
    		strExitState= "ANI_STATE_FOUND";	
    	}
    	else {
    		data.addToLog(currElementName,"The State Name is not setted in the Session!!");
    	}
    	
		}
		catch(Exception e) {
			data.addToLog(currElementName,"Exception in Getting the ANI State  :: "+e);
			caa.printStackTrace(e);
		}
		return strExitState;
		
	}

}
