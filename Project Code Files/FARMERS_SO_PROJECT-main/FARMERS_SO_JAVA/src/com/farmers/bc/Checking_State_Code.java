package com.farmers.bc;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class Checking_State_Code extends DecisionElementBase{
@Override
public String doDecision(String currElementName, DecisionElementData data) throws Exception {
	String strExitState = Constants.STRING_NO, listOfStates = Constants.EmptyString;
	CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
	try{
		String code=(String)data.getSessionData("S_STATECODE");
		listOfStates = (String) data.getSessionData("S_LIST_OF_STATES");
		data.addToLog(currElementName,"List of States"
				+ " :: "+listOfStates +"::"+code);
				//get("S_LIST_OF_STATES");
	    if(code!=null && listOfStates.contains(code)){
	    	strExitState="YES";
	    }
	    else {
	    	strExitState="NO";
	    }
	}
	catch(Exception e)
	{
		data.addToLog(currElementName,"Exception in Checking_State_Code :: "+e);
		caa.printStackTrace(e);
	}
	data.addToLog(currElementName,"Checking_State_Code :: "+strExitState);
	return strExitState;
}
}
