package com.farmers.afterHoursCallBack;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class SAHCB_MN_005_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("SAHCB_MN_005_Call","Return_Value");
			data.setSessionData("SAHCB_MN_005_VALUE", strReturnValue);
			if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
			}else if(!strReturnValue.equalsIgnoreCase(null)) {
				  String[] parts = strReturnValue.split(" ");
			        String firstName = "";
			        String lastName = "";
			        if (parts.length == 2) {
			            firstName = parts[0];
			            lastName = parts[1];
			            data.addToLog(currElementName, "The First Name:"+firstName+"The Last Name:"+lastName);
			            data.setSessionData(Constants.S_FIRSTNAME, firstName);
			            data.setSessionData(Constants.S_LASTNAME, lastName);
			            strExitState = "done";
			        } 
			        else if (parts.length > 2) {
			            firstName = parts[0];
			            data.setSessionData(Constants.S_FIRSTNAME, firstName);
			            StringBuilder sb = new StringBuilder();
			            for (int i = 1; i < parts.length; i++) {
			                sb.append(parts[i]);
			            }
			            lastName = sb.toString(); // combine remaining parts
			            data.setSessionData(Constants.S_LASTNAME, lastName);
			            data.addToLog(currElementName, "The First Name:"+firstName+"The Last Name:"+lastName);
			        }
			        else  if (parts.length==1){
			        	firstName = parts[0];
			        	lastName = parts[0];
			            data.addToLog(currElementName, "The First Name:"+firstName+"The Last Name:"+lastName);
			            data.setSessionData(Constants.S_FIRSTNAME, firstName);
			            data.setSessionData(Constants.S_LASTNAME, lastName);
			            strExitState = "done";
			        }
			}
			 else {
				strExitState = Constants.ER;
				data.setSessionData("SAHCB_MN_005_VALUE", "ER");
			} 
			data.addToLog(currElementName,"SAHCB_MN_005_VALUE :: "+strExitState);
		}
		catch(Exception e) {
			data.addToLog(currElementName,"Exception in SAHCB_MN_005_VALUE :: "+e);
			caa.printStackTrace(e);
		}
		return strExitState;
	}

	
}
