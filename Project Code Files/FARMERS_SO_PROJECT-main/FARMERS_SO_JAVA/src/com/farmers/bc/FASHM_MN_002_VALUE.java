package com.farmers.bc;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class FASHM_MN_002_VALUE  extends DecisionElementBase{
	public String doDecision(String currElementName, DecisionElementData data)throws Exception{
		String strExitState=Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try{
			String strReturnValue = (String) data.getElementData("FASHM_MN_002_Call","Return_Value");
			data.addToLog(currElementName, " FASHM_MN_002_VALUE : Return_Value :: "+strReturnValue);
			data.setSessionData(Constants.FASHM_MN_002_VALUE, strReturnValue);

			if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
			} else if(strReturnValue.equalsIgnoreCase("360 Value")) {
				strExitState = "360 Value";
				caa.createMSPKey(caa, data, "FASHM_MN_002", "360 VALUE");
			} else if(strReturnValue.equalsIgnoreCase("Cancel a Policy")) {
				strExitState ="Cancel a Policy";
				caa.createMSPKey(caa, data, "FASHM_MN_002", "CANCEL A POLICY");
			} else if(strReturnValue.equalsIgnoreCase("Something Else")) {
				strExitState ="Something Else";
				caa.createMSPKey(caa, data, "FASHM_MN_002", "SOMETHING ELSE");
			} else if(strReturnValue.equalsIgnoreCase("Representative")) {
				strExitState ="representative";
			}else {
				strExitState = Constants.ER;
			}
		}
		catch (Exception e)
		{
			data.addToLog(currElementName,"Exception in FASHM_MN_002_VALUE :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"FASHM_MN_002_VALUE :: "+strExitState);
		
		String menuExsitState = strExitState;
      	if(strExitState.contains(" ")) menuExsitState = menuExsitState.replaceAll(" ", "_");
		if(null != (String)data.getSessionData("FASHM_MN_002_"+menuExsitState) && !((String)data.getSessionData("FASHM_MN_002_"+menuExsitState)).isEmpty()) menuExsitState = (String)data.getSessionData("FASHM_MN_002_"+menuExsitState);
		data.addToLog(currElementName, "Final Value of Menu Exit State for FASHM_MN_002: "+menuExsitState);
		//data.setSessionData(Constants.S_MENU_SELCTION_KEY, data.getSessionData(Constants.S_BU)+":FASHM_MN_002:"+menuExsitState);
		return strExitState;
	}
}