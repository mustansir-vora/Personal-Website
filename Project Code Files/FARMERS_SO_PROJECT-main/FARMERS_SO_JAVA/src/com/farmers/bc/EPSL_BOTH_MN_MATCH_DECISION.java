package com.farmers.bc;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class EPSL_BOTH_MN_MATCH_DECISION extends DecisionElementBase{

	public String doDecision(String currElementName, DecisionElementData data)
			throws Exception {
		String strExitState=Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);

		try{
			String strCallerInput1=(String) data.getSessionData(Constants.S_AGENTID_1);	
			String strCallerInput2=(String) data.getSessionData(Constants.S_AgentID_2);
			String tries = (String)data.getSessionData("S_ESPL_AGENT_MATCH_TRIES");
			data.addToLog(currElementName, "Value of S_ESPL_AGENT_MATCH_TRIES : "+tries);
			int triesCount = Integer.parseInt(tries);
			data.addToLog(currElementName, "Value of S_ESPL_AGENT_MATCH_TRIES after Integer Parsing : "+triesCount);
			if(strCallerInput1!=null && strCallerInput2!=null) {
				if(strCallerInput1.equals(strCallerInput2)) {
					data.setSessionData(Constants.S_AGENTID,strCallerInput1);
					strExitState="YES";
				}else if(triesCount==0) {
					triesCount=1;
					String stringValue = Integer.toString(triesCount);
					data.setSessionData("S_ESPL_AGENT_MATCH_TRIES", stringValue);
					data.addToLog(currElementName, "Value of Tries in First IF : "+stringValue);
					strExitState="NO";
				}else {
					triesCount=triesCount+1;
					String stringValue = Integer.toString(triesCount);
					data.setSessionData("S_ESPL_AGENT_MATCH_TRIES",stringValue);
					data.addToLog(currElementName, "Value of Tries in First ELSE : "+stringValue);
					strExitState="NO";
				}
				data.addToLog(currElementName, "Value of triesCount after condition : "+triesCount);
				if(triesCount==3) {
					strExitState="NO_MAXTRIES";
					data.addToLog(currElementName, "Value of Tries in Final IF : "+triesCount);
				}
			}
			data.addToLog(currElementName, "Value of S_ESPL_AGENT_MATCH_TRIES : "+triesCount);
		}
		catch(Exception e){
			data.addToLog(currElementName,"Exception in EPSL_BOTH_MN_MATCH_DECISION :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"EPSL_BOTH_MN_MATCH_DECISION :: "+strExitState);
		return strExitState;

	}
}
