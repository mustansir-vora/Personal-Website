package com.farmers.shared.bc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.bean.PolicyBean;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class ContinuePolicyLoop_BC extends DecisionElementBase {

	@SuppressWarnings("unchecked")
	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {

	    String strExitState = Constants.ER;
	    CommonAPIAccess caa = CommonAPIAccess.getInstance(data);

	    try {
	        HashMap<String, PolicyBean> remainingPoliciesMap = (HashMap<String, PolicyBean>) data.getSessionData("S_REMAININGPOLICIES");

	        if (remainingPoliciesMap != null && !remainingPoliciesMap.isEmpty()) {
	            data.addToLog(currElementName, "Remaining Policy Count: " + remainingPoliciesMap.size());
                
	            int i = 1;
	            String prompt = "";
	            String lang = (String) data.getSessionData("S_PREF_LANG");

	            HashMap<String, PolicyBean> nextRemainingPolicies = new HashMap<>();

	            for (String key1 : remainingPoliciesMap.keySet()) {
	            	data.addToLog("Policy number :", key1);
	            	if (i > 5) {
	                	
	                    nextRemainingPolicies.put(key1, remainingPoliciesMap.get(key1));
	                    continue; // skip prompt build if over 5
	                }

	            	//if case for spanish added- Kirthik
					if(lang.equalsIgnoreCase("SP")) {
						if(i==1) {
							prompt = "Puedes decir uno para la póliza que termina en "+spellChar(key1.substring(key1.length()-4));	
						}else if(i==2) {
							prompt = prompt+" Di dos para la póliza que termina en "+spellChar(key1.substring(key1.length()-4));	
						}else if(i==3) {
							prompt = prompt+" Di tres para la póliza que termina en "+spellChar(key1.substring(key1.length()-4));	
						}else if(i==4) {
							prompt= prompt+" Di cuatro para la póliza que termina en "+spellChar(key1.substring(key1.length()-4));	
						}else if(i==5) {
							prompt= prompt+" Di cinco para la póliza que termina en "+spellChar(key1.substring(key1.length()-4));	
						}
							
						
					}else {
						if(i==1) {
							prompt = "You can say one for policy ending in "+spellChar(key1.substring(key1.length()-4));	
						}else if(i==2) {
							prompt = prompt+" say two for policy ending in "+spellChar(key1.substring(key1.length()-4));	
						}else if(i==3) {
							prompt = prompt+" say three for policy ending in "+spellChar(key1.substring(key1.length()-4));	
						}else if(i==4) {
							prompt= prompt+" say four for policy ending in "+spellChar(key1.substring(key1.length()-4));	
						}else if(i==5) {
							prompt= prompt+" say five for policy ending in "+spellChar(key1.substring(key1.length()-4));	
						}
						 
					    }
					

	                i++;
	            }

	            // set the new remaining map for next time
	            if (!nextRemainingPolicies.isEmpty()) {
	                data.setSessionData("S_REMAININGPOLICIES", nextRemainingPolicies);
	                data.addToLog(currElementName, "Policies left for next loop: " + nextRemainingPolicies.size());
	            } 

	            prompt = "\"" + prompt + "\"";
	            prompt = prompt.replaceAll("0", " zero ");
	            data.setSessionData(Constants.VXMLParam1, prompt);
	            data.setSessionData(Constants.VXMLParam2, "NA");
	            data.setSessionData(Constants.VXMLParam3, "NA");
	            data.setSessionData(Constants.VXMLParam4, "NA");
	            data.setSessionData(Constants.VXMLParam5, "NA");
                
	            //Check the size of remainingPoliciesMap
	            //if >5 the return "morethanfive"
	            //else return "lessthanfive"
	            //strExitState = Constants.SU;
	            if(remainingPoliciesMap.size() > 5) {
	            	strExitState = Constants.MorethanFive;
					data.addToLog(currElementName, "Morethan5 polices count: " + remainingPoliciesMap.size());
					}
				else {	
					strExitState = Constants.LessthanFive;
					data.setSessionData("S_LessthanFivePolicy", "Y");
					
					data.addToLog(currElementName, "S_LessthanFive Policy Count = " + remainingPoliciesMap.size());
				}
	        } else {
	            data.addToLog(currElementName, "No remaining policies to process.");
	            data.setSessionData("S_REMAININGPOLICIES", null);
	        }

	    } catch (Exception e) {
	        data.addToLog(currElementName, "Exception in ContinuePolicyLoop_BC: " + e.getMessage());
	        caa.printStackTrace(e);
	    }

	    return strExitState;
	}

	public String spellChar(String number) { 
		String returnString =number;
		if(number!=null) {
		char[] spellChar = number.toCharArray();
		returnString = "";
		for (char c : spellChar) {
			returnString = returnString +" "+c;
		}
		
		}
		return returnString;
	}
	}
