package com.servion.farmers;

import com.audium.server.session.DecisionElementData;

public class IVR2TextDecider {

	
	public boolean offerIvr2Text(String ivrIntent, String percentage, DecisionElementData data) {
		data.addToLog(data.getCurrentElement(), "IVR2Text called for Intent <" + ivrIntent + "> with allocation <" + percentage +">");
		boolean offerTextDeflection = false;
		if(percentage!=null && ivrIntent!=null && !"".equalsIgnoreCase(percentage) && !"".equalsIgnoreCase(ivrIntent)) {
			IVR2TextWeightage objWeightage = IVR2TextStorage.getInstance().getIVR2TextIntent(ivrIntent);
			if(objWeightage == null) {
				IVR2TextWeightage objIvr2TextWeightage = new IVR2TextWeightage();
				objIvr2TextWeightage.setIVR2TextIntent(ivrIntent);
				objIvr2TextWeightage.setTotalCalls(0f);
				objIvr2TextWeightage.setCallsOffered(0f);
				objIvr2TextWeightage.setWeightage(Float.parseFloat(percentage)/100f);
				objWeightage = objIvr2TextWeightage;
			}
			data.addToLog(data.getCurrentElement(), "Weightage obj considered for call : " + objWeightage);
			
			//incrementing Total Calls
			float totalCalls = objWeightage.getTotalCalls();
			totalCalls = totalCalls + 1f;
			objWeightage.setTotalCalls(totalCalls);
			
			float callsOffered = objWeightage.getCallsOffered();
			
			//forcefully setting Weightage to the value retrieved from API
			float weightage = Float.parseFloat(percentage)/100f;
			data.addToLog(data.getCurrentElement(), "Weightage considered for decision : " + weightage);
			data.addToLog(data.getCurrentElement(), "Total calls : " + totalCalls);
			data.addToLog(data.getCurrentElement(), "Calls Offered : " + callsOffered);
	    	float decision = callsOffered/totalCalls;
	    	data.addToLog(data.getCurrentElement(), "Decision : " + decision);
	    	totalCalls++;
	       
	    	// Check if the call should be offered based on Weightage
	        if (decision < weightage) {
	            callsOffered++;
	            offerTextDeflection = true;
	        } else {
	        	offerTextDeflection = false;
	        }
	        data.addToLog(data.getCurrentElement(), "Total calls after decision : " + totalCalls);
			data.addToLog(data.getCurrentElement(), "Calls Offered after decision : " + callsOffered);
	        //Setting values after decision
			objWeightage.setCallsOffered(callsOffered);
			objWeightage.setTotalCalls(totalCalls);
			objWeightage.setWeightage(weightage);
	        
			//Setting object back to memory for retrieval later
			data.addToLog(data.getCurrentElement(), "Weightage obj pushed to memory at end : " + objWeightage);
			IVR2TextStorage.getInstance().putIVR2TextIntent(ivrIntent, objWeightage);
			data.addToLog(data.getCurrentElement(), "IVR2Text Result returned :" + offerTextDeflection);
		}else {
			data.addToLog(data.getCurrentElement(), "Either Percentage or IVR Intent is null or empty");
		}
		
		return offerTextDeflection;
	}
}