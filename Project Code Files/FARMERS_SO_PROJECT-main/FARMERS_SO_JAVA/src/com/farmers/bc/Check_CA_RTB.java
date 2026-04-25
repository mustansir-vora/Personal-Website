package com.farmers.bc;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class Check_CA_RTB extends DecisionElementBase {
   public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
      String strExitState = "NO";
      CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
      String rtbCallerFlag = "";
      String callerAuth = "";
      String msp= (String) data.getSessionData(Constants.S_MENU_SELCTION_KEY);
      try {
         rtbCallerFlag = (String)data.getSessionData("S_RTB_CALLER_FLAG");
         callerAuth = (String)data.getSessionData("S_CALLER_AUTH");
         data.addToLog(currElementName, "Is caller RTB ::" + rtbCallerFlag);
         data.addToLog(currElementName, "Is caller Identified ::" + callerAuth);
         if (callerAuth != null && rtbCallerFlag != null && !callerAuth.isEmpty() && !rtbCallerFlag.isEmpty() && rtbCallerFlag.equalsIgnoreCase("Y") && callerAuth.equalsIgnoreCase("Identified")) {
            data.setSessionData("S_POLICY_ATTRIBUTES", "RTB");
            data.addToLog(currElementName, "S_POLICY_ATTRIBUTES :: " + data.getSessionData("S_POLICY_ATTRIBUTES"));
            data.addToLog("Current MSP: ", msp);
             if(msp.contains("OTHER BILLING")) {
       		 msp= msp.replaceAll("OTHER BILLING", "REAL TIME BILLING");
       		 data.setSessionData(Constants.S_MENU_SELCTION_KEY, msp);
       		 Queue<String> MSPQueue = (Queue<String>) data.getSessionData("S_LAST_5_MSP_VALUES");
				data.addToLog(currElementName, "MSP Queue Before BU was switched :: " +MSPQueue);
				Queue<String> tempQueue = new LinkedList<>();
				if (null != MSPQueue) {
					Iterator<String> iterator = MSPQueue.iterator();
			        data.addToLog(currElementName, "Iterating through the current Queue while replacing old BU (FARMERS) to new BU (FWS)");
			        while (iterator.hasNext()) {
			        	String oldMSPEntry= iterator.next();
			        	data.addToLog(currElementName, "Old MSP entry " +oldMSPEntry);
							String	tempKey = oldMSPEntry.replaceAll("REAL TIME BILLING", "OTHER BILLING");
			                tempQueue.add(tempKey);
			        }
			        MSPQueue = tempQueue;
			        data.setSessionData("S_LAST_5_MSP_VALUES", MSPQueue);
			        data.addToLog(currElementName, "New MSP Queue post replacing the Old BU with New one :: " + data.getSessionData("S_LAST_5_MSP_VALUES"));
				}
           	 data.addToLog("Updated MSP: ", msp);         	 
       	      }
            strExitState = "YES";
            
         }else if(callerAuth != null && !callerAuth.isEmpty() && callerAuth.equalsIgnoreCase("Identified")) {    	 
        	 data.addToLog("Current MSP: ", msp);
        	 if(msp.contains("REAL TIME BILLING")) {
        		 msp= msp.replaceAll("REAL TIME BILLING", "OTHER BILLING");
        		 data.setSessionData(Constants.S_MENU_SELCTION_KEY, msp);
        		 Queue<String> MSPQueue = (Queue<String>) data.getSessionData("S_LAST_5_MSP_VALUES");
					data.addToLog(currElementName, "MSP Queue Before BU was switched :: " +MSPQueue);
					Queue<String> tempQueue = new LinkedList<>();
					if (null != MSPQueue) {
						Iterator<String> iterator = MSPQueue.iterator();
				        data.addToLog(currElementName, "Iterating through the current Queue while replacing old BU (FARMERS) to new BU (FWS)");
				        while (iterator.hasNext()) {
				        	String oldMSPEntry= iterator.next();
				        	data.addToLog(currElementName, "Old MSP entry " +oldMSPEntry);
								String	tempKey = oldMSPEntry.replaceAll("REAL TIME BILLING", "OTHER BILLING");
				                tempQueue.add(tempKey);
				        }
				        MSPQueue = tempQueue;
				        data.setSessionData("S_LAST_5_MSP_VALUES", MSPQueue);
				        data.addToLog(currElementName, "New MSP Queue post replacing the Old BU with New one :: " + data.getSessionData("S_LAST_5_MSP_VALUES"));
					}
					
            	 data.addToLog("Updated MSP: ", msp);            	 
        	 }else if(msp.contains("FARMERS REAL TIME BILLING")) {
        		 msp= msp.replaceAll("FARMERS REAL TIME BILLING", "OTHER BILLING");
        		 data.setSessionData(Constants.S_MENU_SELCTION_KEY, msp);
        		 Queue<String> MSPQueue = (Queue<String>) data.getSessionData("S_LAST_5_MSP_VALUES");
					data.addToLog(currElementName, "MSP Queue Before BU was switched :: " +MSPQueue);
					Queue<String> tempQueue = new LinkedList<>();
					if (null != MSPQueue) {
						Iterator<String> iterator = MSPQueue.iterator();
				        data.addToLog(currElementName, "Iterating through the current Queue while replacing old BU (FARMERS) to new BU (FWS)");
				        while (iterator.hasNext()) {
				        	String oldMSPEntry= iterator.next();
				        	data.addToLog(currElementName, "Old MSP entry " +oldMSPEntry);
								String	tempKey = oldMSPEntry.replaceAll("FARMERS REAL TIME BILLING", "OTHER BILLING");
				                tempQueue.add(tempKey);
				        }
				        MSPQueue = tempQueue;
				        data.setSessionData("S_LAST_5_MSP_VALUES", MSPQueue);
				        data.addToLog(currElementName, "New MSP Queue post replacing the Old BU with New one :: " + data.getSessionData("S_LAST_5_MSP_VALUES"));
					}
					
            	 data.addToLog("Updated MSP: ", msp);            	 
        	 }

         }
      } catch (Exception var8) {
         data.addToLog(currElementName, "Exception in Check_CA_RTB :: " + var8);
         caa.printStackTrace(var8);
      }

      data.addToLog(currElementName, "Check_CA_RTB :: " + strExitState);
      return strExitState;
   }
}