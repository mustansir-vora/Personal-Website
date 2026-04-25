package com.farmers.shared.bc;

import com.audium.server.AudiumException;

import com.audium.server.session.DecisionElementData;

import com.audium.server.voiceElement.DecisionElementBase;

import com.farmers.util.Constants;

public class Commercial_BIZ_ID_Flow_KYC_check extends DecisionElementBase{

	@Override
	
		
public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		
	
			
			String strExitState = "ER";
			
			
			
			try {
				
				String KYC_Flag= (String) data.getSessionData(Constants.S_KYC_ENABLED);
				
				String Producer_Code=(String) data.getSessionData(Constants.S_PRODUCER_CODE);
				
			if(KYC_Flag!=null && !KYC_Flag.isEmpty() && KYC_Flag.equalsIgnoreCase("TRUE") ) 
			
			
			{
				if(Producer_Code!=null && !Producer_Code.isEmpty()) 
				{
					
					strExitState="SU";
				}
				
				
			}
			
				
					
				} 

		
		
		
catch (Exception e) {
				
				data.addToLog(currElementName,"Exception in Commercial_BIZ_ID_Flow_KYC Check  :: "+e);
			}
			
			return strExitState;
			

	}
	
	
	
	
	
	
	
	

}
