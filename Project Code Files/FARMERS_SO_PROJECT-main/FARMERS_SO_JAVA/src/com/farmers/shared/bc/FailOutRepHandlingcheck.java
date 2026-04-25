package com.farmers.shared.bc;

import com.audium.server.AudiumException;

import com.audium.server.session.DecisionElementData;

import com.audium.server.voiceElement.DecisionElementBase;

import com.farmers.util.Constants;

public class FailOutRepHandlingcheck extends DecisionElementBase{

	@Override
	
		
public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		
	
			
			String strExitState = "SU";
			
			
			
			try {
				
			
				
				String returnValue = (String) data.getSessionData(Constants.S_FAILOUT_REP_HANDLING_COUNTER_FLAG);

				
				data.addToLog(currElementName, "Fail Out Rep Handling Counter value: " + returnValue);
				
				data.setSessionData(Constants.S_FAILOUT_REP_HANDLING_COUNTER_FLAG, "Y");
				
				
				data.setSessionData(Constants.S_CALLER_VERIFICATION, "N");
				
				
				data.addToLog(currElementName, "After setting the Fail Out Rep Handling Counter value: " + (String) data.getSessionData(Constants.S_FAILOUT_REP_HANDLING_COUNTER_FLAG));
				
				 strExitState = "SU";
				
				} 


		
		
		
catch (Exception e) {
				
				data.addToLog(currElementName,"Exception in RepHandling Check  :: "+e);
			}
			
			return strExitState;
			

	}
	
	
	
	
	
	
	
	

}
