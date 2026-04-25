package com.servion.farmers;

import java.util.List;

import com.audium.server.session.DecisionElementData;

public class DestinationDecider {

	
	public String getDestinationBasedOnHistory(List<DestinationWeightage> hostWeightageList, DecisionElementData data) {
	    
	    // assume 0th index as default 
	    DestinationWeightage hostWithMaxAccWeight = hostWeightageList.get(0);
	    data.addToLog(data.getCurrentElement(),"DestList before decision : " + hostWithMaxAccWeight);
	    // choose the host with the largest percentageAccum
	    for (int i = 1; i < hostWeightageList.size(); i++) {
	        if (hostWeightageList.get(i).getWeightageUsedSoFar() >= hostWithMaxAccWeight.getWeightageUsedSoFar()){
	            hostWithMaxAccWeight = hostWeightageList.get(i);
	        }
	    }
	 
	    // subtract 100 from the percentageAccum for the chosen host
	    int inverseAccWeight = hostWithMaxAccWeight.getWeightageUsedSoFar() - 100;
	    hostWithMaxAccWeight.setWeightageUsedSoFar(inverseAccWeight);
	    
	    // add percentageLoad to percentageAccum for all hosts, including the chosen host
	    for (DestinationWeightage weightedHost : hostWeightageList) {
	    	int weight = weightedHost.getWeightage();
		    int accWeight = weightedHost.getWeightageUsedSoFar();
	        weightedHost.setWeightageUsedSoFar(weight + accWeight);
	    }
	    data.addToLog(data.getCurrentElement(),"DestList after decision : " + hostWithMaxAccWeight);
	    return hostWithMaxAccWeight.getDestinationIdentifier();
	}
}
