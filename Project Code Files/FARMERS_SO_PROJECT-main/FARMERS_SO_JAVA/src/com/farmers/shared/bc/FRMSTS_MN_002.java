package com.farmers.shared.bc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class FRMSTS_MN_002 extends DecisionElementBase 
{


	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException 
	{

		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {

			String strReturnValue = (String) data.getElementData("FRMST_SBP_MN_002_Call","Return_Value");

			data.addToLog(currElementName, "Menu ID : "+"FRMST_SBP_MN_002_Call"+" :: Menu Value : "+strReturnValue);
			if(strReturnValue != null && !strReturnValue.equals("")) {
				strExitState = strReturnValue;
				if (strReturnValue.equalsIgnoreCase(Constants.NEWQUOTE)) {
					strExitState = Constants.NEWQUOTE;
				}else if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
					strExitState = Constants.NOINPUT;
					
				}else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
					strExitState = Constants.NOINPUT;
				}
			}

			else 
			{
				strExitState = Constants.ER;
				data.addToLog(currElementName, "EXITSTATE FRMST_PENDING_MN_001 :: "+strExitState);
			}
		}

		catch (Exception e) {
			data.addToLog(currElementName,"Exception in FRMST_PENDING_MN_001 :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"FRMST_PENDING_MN_001 :: "+strExitState);
		//		String menuExsitState = strExitState;
		//		if(strExitState.contains(" ")) menuExsitState = menuExsitState.replaceAll(" ", "_");
		//		if(null != (String)data.getSessionData("FSLS_MN_001_"+menuExsitState) && !((String)data.getSessionData("FSLS_MN_001_"+menuExsitState)).isEmpty()) menuExsitState = (String)data.getSessionData("FSLS_MN_001_"+menuExsitState);
		//		data.addToLog(currElementName, "Final Value of Menu Exit State for FSLS_MN_001: "+menuExsitState);
		//		data.setSessionData(Constants.S_MENU_SELCTION_KEY, data.getSessionData(Constants.S_BU)+":FSLS_MN_001:"+menuExsitState);
		//		data.addToLog(Constants.S_MENU_SELCTION_KEY, data.getSessionData(Constants.S_BU)+":FSLS_MN_001:"+menuExsitState);

		return strExitState;

	}

}

