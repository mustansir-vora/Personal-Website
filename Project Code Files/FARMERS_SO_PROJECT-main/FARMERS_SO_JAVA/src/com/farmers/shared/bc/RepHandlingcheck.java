package com.farmers.shared.bc;

import com.audium.server.AudiumException;

import com.audium.server.session.DecisionElementData;

import com.audium.server.voiceElement.DecisionElementBase;

import com.farmers.util.Constants;

public class RepHandlingcheck extends DecisionElementBase{

	@Override
	
		
public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		
	
			
			String strExitState = "CONT";
			
			
			
			try {
				
			
				
				String returnValue = (String) data.getSessionData(Constants.S_REP_HANDLING_COUNTER);

				
				data.addToLog(currElementName, "Rep Handling Counter value: " + returnValue);

				
				if (returnValue != null && !returnValue.trim().isEmpty()) {
				    
				    switch (returnValue.trim()) {
				        case "0":
				        case "1":
				            strExitState = "CONT";
				            break;
				        case "2":
				           
				        	strExitState = "TR";
				            
				            data.setSessionData(Constants.S_REP_HANDLING_COUNTER_FLAG, "Y");
				            
				            data.addToLog(currElementName, "S_REP_HANDLING_COUNTER_FLAG: " + data.getSessionData(Constants.S_REP_HANDLING_COUNTER_FLAG));
				            
				            break;
				        default:

				        	
				        	
				        	strExitState = "CONT";
				        	
				            data.addToLog(currElementName, "Unexpected counter value: " + returnValue);
				            
				            break;
				    }
				} else {
					strExitState = "CONT";
				    data.addToLog(currElementName, "Rep Handling Counter value is null or empty");
				}
		
		
		
		
		
	}
			
			catch (Exception e) {
				
				data.addToLog(currElementName,"Exception in RepHandling Check  :: "+e);
			}
			
			return strExitState;
			

	}
	
	
	
	
	
	
	
	

}
