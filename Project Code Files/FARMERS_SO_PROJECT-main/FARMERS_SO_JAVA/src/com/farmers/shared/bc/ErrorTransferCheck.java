package com.farmers.shared.bc;

import com.audium.server.AudiumException;

import com.audium.server.session.DecisionElementData;

import com.audium.server.voiceElement.DecisionElementBase;

import com.farmers.util.Constants;

public class ErrorTransferCheck extends DecisionElementBase{

	@Override
	
		
public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		
	
			
			String strExitState = "SU";
			
			
			
			try {
				
				String Transfer_Flag= (String) data.getSessionData(Constants.S_TRANSFER_ENTER);
				
				
				
			if(Transfer_Flag!=null && !Transfer_Flag.isEmpty() && Transfer_Flag.equalsIgnoreCase("Y") ) {
			
			
			
					
					strExitState="ER";
			
			}
		
			}
		
catch (Exception e) {
				
				data.addToLog(currElementName,"Exception in ErrorTransferCheck  :: "+e);
			}
			
			return strExitState;
			

	}
	
	
	
	
	
	
	
	

}
