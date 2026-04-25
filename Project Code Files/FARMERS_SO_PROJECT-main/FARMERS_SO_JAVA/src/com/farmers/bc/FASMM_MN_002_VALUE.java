package com.farmers.bc;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class FASMM_MN_002_VALUE  extends DecisionElementBase{
	public String doDecision(String currElementName, DecisionElementData data)throws Exception{
		String strExitState=Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try{
			String strReturnValue = (String) data.getElementData("FASMM_MN_002_Call","Return_Value");
			data.addToLog(currElementName, " FASMM_MN_002_VALUE : Return_Value :: "+strReturnValue);
			data.setSessionData(Constants.FASMM_MN_002_VALUE, strReturnValue);
			if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
			} else if(strReturnValue.equalsIgnoreCase(Constants.STRING_YES)) {
				strExitState = Constants.STRING_YES;
				caa.createMSPKey(caa, data, "FASMM_MN_002", "YES");
			} else if(strReturnValue.equalsIgnoreCase(Constants.STRING_NO)) {
				strExitState = Constants.STRING_NO;
			}else if(strReturnValue.equalsIgnoreCase("Representative")) {
				strExitState = "representative";
				caa.createMSPKey(caa, data, "FASMM_MN_002", "REPRESENTATIVE");
			} else {
				strExitState = Constants.ER;
			}
		}
		catch (Exception e)
		{
			data.addToLog(currElementName,"Exception in FASMM_MN_002_VALUE :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"FASMM_MN_002_VALUE :: "+strExitState);

//		String menuExsitState = strExitState;
//      	if(strExitState.contains(" ")) menuExsitState = menuExsitState.replaceAll(" ", "_");
//		if(null != (String)data.getSessionData("FASMM_MN_002_"+menuExsitState) && !((String)data.getSessionData("FASMM_MN_002_"+menuExsitState)).isEmpty()) menuExsitState = (String)data.getSessionData("FASMM_MN_002_"+menuExsitState);
//		data.addToLog(currElementName, "Final Value of Menu Exit State for FASMM_MN_002: "+menuExsitState);
//		data.setSessionData(Constants.S_MENU_SELCTION_KEY, data.getSessionData(Constants.S_BU)+":FASMM_MN_002:"+menuExsitState);
		return strExitState;
	}
}