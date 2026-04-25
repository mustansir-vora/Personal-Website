package com.farmers.bc;

import java.io.PrintWriter;
import java.io.StringWriter;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;
public class FNAC_MN_001_VALUE extends DecisionElementBase{

	public String doDecision(String currElementName, DecisionElementData data)
			throws Exception {
		String strExitState=Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try{
			
			
			String strReturnValue = (String) data.getElementData("FNAC_MN_001_Call","Return_Value");
			data.addToLog(currElementName, " FNAC_MN_001 : Return_Value :: "+strReturnValue);
			data.setSessionData("FNAC_MN_001_VALUE", strReturnValue);
			
			if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
			} else if(strReturnValue.equalsIgnoreCase("NO")) {
				strExitState ="NO";
			}  else if(strReturnValue.equalsIgnoreCase("YES")) {
				strExitState ="YES";
				data.setSessionData("S_IS_AGENT", strExitState);
			} else if(strReturnValue.equalsIgnoreCase("Representative")){
				strExitState ="Representative";
				data.setSessionData("S_IS_AGENT", strExitState);
				
			}
			else strExitState=Constants.ER;
		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in FNAC_MN_001_VALUE :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"FNAC_MN_001_VALUE :: "+strExitState);

//		String menuExitState = strExitState;
//		if(strExitState.contains(" ")) menuExitState = menuExitState.replaceAll(" ", "_");
//		if(null != (String)data.getSessionData("FNAC_MN_001_"+menuExitState) && !((String)data.getSessionData("FNAC_MN_001_"+menuExitState)).isEmpty()) menuExitState = (String)data.getSessionData("FNAC_MN_001_"+menuExitState);
//		data.addToLog(currElementName, "Final Value of Menu Exit State for FNAC_MN_001: "+menuExitState);
//		data.setSessionData(Constants.S_MENU_SELCTION_KEY, data.getSessionData(Constants.S_BU)+":FNAC_MN_001:"+menuExitState);
		return strExitState;

		}
		
}




