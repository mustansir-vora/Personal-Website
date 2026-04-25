package com.farmers.bc;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class Award_DM_or_PC extends DecisionElementBase{
@Override
public String doDecision(String currElementName, DecisionElementData data) throws Exception {
	String strExitState = Constants.ER;
	CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
	try{
		String agentsegmentation=(String)data.getSessionData("S_AGENT_SEGMENTATION");
		String award=(String)data.getSessionData("S_AWARD_VALUE");
		boolean PC_or_Not = false;
		if(data.getSessionData("S_PC_OR_NOT")!=null)
			PC_or_Not=(boolean)data.getSessionData("S_PC_OR_NOT");
		
		if(PC_or_Not){
			strExitState="Award_DM_Or_PC";
		}
		else{
			strExitState="Other";
		}
	}
	catch(Exception e)
	{
		data.addToLog(currElementName,"Exception in Award_DM_or_PC :: "+e);
		caa.printStackTrace(e);
	}
	data.addToLog(currElementName,"Award_DM_or_PC :: "+strExitState);
	return strExitState;
}
}
