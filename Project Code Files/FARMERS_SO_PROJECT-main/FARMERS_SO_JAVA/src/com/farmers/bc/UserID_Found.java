package com.farmers.bc;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class UserID_Found extends DecisionElementBase{

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws Exception {
		String strExitState=Constants.STRING_NO;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try{
		if((String)data.getSessionData(Constants.S_IEXUSERID)!=null) {
		data.setSessionData(Constants.VXMLParam1,(String)data.getSessionData(Constants.S_IEXUSERID));
		data.setSessionData(Constants.VXMLParam2, "NA");
		data.setSessionData(Constants.VXMLParam3, "NA");
		data.setSessionData(Constants.VXMLParam4, "NA");
		data.addToLog(currElementName, "UserID_Found : VXMLParam1 : "+data.getSessionData(Constants.VXMLParam1));
		
		strExitState=Constants.STRING_YES;
		}
		else
			strExitState=Constants.STRING_NO;
			
		}
			catch (Exception e) {
				data.addToLog(currElementName,"Exception in  UserID_Found :: "+e);
				caa.printStackTrace(e);
			}
			data.addToLog(currElementName,"UserID_Found  :: "+strExitState);
            return strExitState;
	}

}
