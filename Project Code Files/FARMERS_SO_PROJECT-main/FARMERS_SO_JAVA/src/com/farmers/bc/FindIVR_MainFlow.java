package com.farmers.bc;

import org.apache.logging.log4j.core.lookup.MainMapLookup;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class FindIVR_MainFlow extends DecisionElementBase  {

	@Override
	public String doDecision(String arg0, DecisionElementData data) throws AudiumException {
		String StrExitState = Constants.ER;
		String strElementName = Constants.EmptyString;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		
		try {
			strElementName = data.getCurrentElement();
			StrExitState = (String)data.getSessionData(Constants.S_FIND_IVR_MAINFLOW);
			data.addToLog(strElementName,"S_FIND_IVR_MAINFLOW :: "+StrExitState);
			String specialCharRegex = "&";
			String specialChar = "-";
			StrExitState = StrExitState.replaceAll(specialChar, "");
			StrExitState = StrExitState.replaceAll("[()]", "");
			
			if(StrExitState.equals("Welcome and Transfer w/ANI Lookup")) {
				StrExitState= "Welcome and Transfer with ANI Lookup";
			}else if(StrExitState.equals(StrExitState.replaceAll(specialCharRegex, ""))) {
			    
			} else{
				StrExitState = StrExitState.replaceAll(specialCharRegex, "");
				data.addToLog(strElementName,"S_FIND_IVR_MAINFLOW : Re formed :: "+StrExitState);
			}
		} catch (Exception e) {
			StrExitState = "Direct Transfer";
			data.addToLog(strElementName,"Exception in FindIVR_MainFlow :: "+e);
			caa.printStackTrace(e);
		} finally {
		}
		return StrExitState;
		
		
	}


	public static void main(String[] args) {
		
	}
}


