package com.farmers.bc;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class EPSL_MN_005_VALUE extends DecisionElementBase{

	public String doDecision(String currElementName, DecisionElementData data)
			throws Exception {

		String strExitState=Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try{
			String strReturnValue = (String) data.getElementData("EPSL_MN_005_Call","Return_Value");
			data.addToLog(currElementName, " EPSL_MN_005 : Return_Value :: "+strReturnValue);
			data.setSessionData("EPSL_MN_005_VALUE", strReturnValue);
			if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
				data.addToLog(currElementName, "First IF Condition"+strReturnValue);
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
				data.addToLog(currElementName,"Second IF Condition"+strReturnValue);

			}else if(strReturnValue.equalsIgnoreCase("Bereavement")) {
				strExitState="Bereavement";
				data.addToLog(currElementName, "Third IF Condition"+strReturnValue);

			}
			else if(strReturnValue.equalsIgnoreCase("Unpaid Time Off")) {
				strExitState="Unpaid Time Off";
				data.addToLog(currElementName, "Third IF Condition"+strReturnValue);
			}
			else if(strReturnValue.equalsIgnoreCase("PTO")) {
				strExitState="PTO";
				data.addToLog(currElementName, "Third IF Condition"+strReturnValue);
			}
			strReturnValue=strReturnValue.replaceAll(" ",".");
			data.setSessionData(Constants.S_LEAVETYPE,strReturnValue);
			data.setSessionData(Constants.VXMLParam1, (String)data.getSessionData(Constants.S_LEAVETYPE));
			data.setSessionData(Constants.VXMLParam2, "NA");
			data.setSessionData(Constants.VXMLParam3, "NA");
			data.setSessionData(Constants.VXMLParam4, "NA");

			data.addToLog(currElementName, "EPSL_MN_005 : VXMLParam1 : "+data.getSessionData(Constants.VXMLParam1));
		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in EPSL_MN_005_VALUE  :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"EPSL_MN_005_VALUE :: "+strExitState);

//		String menuExitState = strExitState;
//		if(strExitState.contains(" ")) menuExitState = menuExitState.replaceAll(" ", "_");
//		if(null != (String)data.getSessionData("EPSL_MN_005_"+menuExitState) && !((String)data.getSessionData("EPSL_MN_005_"+menuExitState)).isEmpty()) menuExitState = (String)data.getSessionData("EPSL_MN_005_"+menuExitState);
//		data.addToLog(currElementName, "Final Value of Menu Exit State for EPSL_MN_005: "+menuExitState);
//		data.setSessionData(Constants.S_MENU_SELCTION_KEY, data.getSessionData(Constants.S_BU)+":EPSL_MN_005:"+menuExitState);
		return strExitState;
	}
}
