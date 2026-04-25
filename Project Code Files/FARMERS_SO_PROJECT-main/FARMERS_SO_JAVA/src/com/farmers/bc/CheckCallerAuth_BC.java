package com.farmers.bc;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class CheckCallerAuth_BC extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {

		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		String mspKey = Constants.EmptyString;
	
		String selectedBrand="";
		if(null!=data.getSessionData(Constants.S_POLICY_SOURCE) && !"".equalsIgnoreCase((String) data.getSessionData(Constants.S_POLICY_SOURCE))) { 
		selectedBrand = (String) data.getSessionData(Constants.S_POLICY_SOURCE);
		data.addToLog(currElementName, "Polciy Brand: "+selectedBrand);
		}
		try {
//			if(Constants.STRING_YES.equalsIgnoreCase((String)data.getSessionData("S_FLAG_BW_BU"))) {
//				mspkey = "FSEL:SALESTOSERVICE:BW";
//			}else if(Constants.STRING_YES.equalsIgnoreCase((String)data.getSessionData("S_FLAG_FDS_BU"))) {
//				mspkey = "FSEL:SALESTOSERVICE:Farmers";
//			}else if(Constants.STRING_YES.equalsIgnoreCase((String)data.getSessionData("S_FLAG_FOREMOST_BU"))) {
//				mspkey = "FSEL:SALESTOSERVICE:Specialty";
//			}else if(Constants.STRING_YES.equalsIgnoreCase((String)data.getSessionData("S_FLAG_FWS_BU"))) {
//				mspkey = "FSEL:SALESTOSERVICE:FWS";
//			}else {
//				mspkey = "FSEL:SALESTOSERVICE:TRANSFER";
//			}
			switch (selectedBrand) {
			case "BW":
			mspKey = "FSEL:SALESTOSERVICE:BW";
			break;
			case "FWS-ARS":
			mspKey = "FSEL:SALESTOSERVICE:FWS";
				break;
			case "FWS-A360":
			mspKey = "FSEL:SALESTOSERVICE:FWS";
				break;
			case "ARS":
			mspKey = "FSEL:SALESTOSERVICE:FWS";
				break;
			case "A360":
			mspKey = "FSEL:SALESTOSERVICE:FWS";
				break;		
			case "PLA":
			mspKey = "FSEL:SALESTOSERVICE:Farmers";
				break;
			case "GWPC":
			mspKey = "FSEL:SALESTOSERVICE:Farmers";
				break;
			case "FM":
			mspKey = "FSEL:SALESTOSERVICE:Specialty";
				break;
			default:
			mspKey = "FSEL:SALESTOSERVICE:TRANSFER";
			break;
			}


			data.addToLog(data.getCurrentElement(), "MSP KEY : "+mspKey);
			data.setSessionData(Constants.S_MENU_SELCTION_KEY, mspKey);
			data.addToLog(data.getCurrentElement(), "Value of S_LAST_5_MSP_VALUES :: " + data.getSessionData("S_LAST_5_MSP_VALUES"));
		
			Queue<String> lastFiveMSPValues = (Queue<String>) data.getSessionData("S_LAST_5_MSP_VALUES");
			data.addToLog(data.getCurrentElement(), "Value of lastFiveMSPValues Queue :: " + lastFiveMSPValues);
			if (lastFiveMSPValues == null) {
				lastFiveMSPValues = new LinkedList<>();
			}
		
			lastFiveMSPValues.offer(mspKey);
		
			while (lastFiveMSPValues.size() > 5) {
				lastFiveMSPValues.poll();
			}
			data.setSessionData("S_LAST_5_MSP_VALUES", lastFiveMSPValues);
		
			data.addToLog(data.getCurrentElement(), "Value of lastFiveMSPValues Queue After Operation :: " + lastFiveMSPValues);
		    strExitState= "SU";
		}
		catch (Exception e) {
			data.addToLog(data.getCurrentElement(),"Exception in CheckCallerAuth_BC :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName, "CheckCallerAuth_BC :: " + strExitState);
		return strExitState;
	}
	
}
