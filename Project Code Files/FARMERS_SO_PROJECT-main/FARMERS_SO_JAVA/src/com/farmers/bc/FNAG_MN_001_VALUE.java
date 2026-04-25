package com.farmers.bc;


import java.io.PrintWriter;
import java.io.StringWriter;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;
public class FNAG_MN_001_VALUE extends DecisionElementBase{

	public String doDecision(String currElementName, DecisionElementData data)
			throws Exception {
		String strExitState=Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try{

			String strReturnValue = (String) data.getElementData("FNAG_MN_001_Call","Return_Value");
			data.addToLog(currElementName, " FNAG_MN_001 : Return_Value :: "+strReturnValue);
			data.setSessionData("FNAG_MN_001_VALUE", strReturnValue);
			if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
			} 
			else if(strReturnValue.equalsIgnoreCase("New Business")) {
				strExitState ="New Business";
				data.setSessionData(Constants.S_INTENT, strReturnValue);
				data.setSessionData(Constants.FNAG_MN_001_VALUE, strReturnValue);
			} else if(strReturnValue.equalsIgnoreCase("Dial an Extension")){
				strExitState ="Dial an Extension";
				data.setSessionData(Constants.S_INTENT, strReturnValue);
				data.setSessionData(Constants.FNAG_MN_001_VALUE, strReturnValue);
			}
			else if(strReturnValue.equalsIgnoreCase("Underwriting")) {
				strExitState="Underwriting";
				data.setSessionData(Constants.S_INTENT, strReturnValue);
				data.setSessionData(Constants.FNAG_MN_001_VALUE, strReturnValue);
			}else if(strReturnValue.equalsIgnoreCase("Policy Services")) {
				strExitState="Policy Services";
				data.setSessionData(Constants.S_INTENT, strReturnValue);
				data.setSessionData(Constants.FNAG_MN_001_VALUE, strReturnValue);
			}
			else if(strReturnValue.equalsIgnoreCase("Default"))
			{
				strExitState="Default";
				data.setSessionData(Constants.FNAG_MN_001_VALUE, strReturnValue);
			}
			else if(strReturnValue.equalsIgnoreCase("Pre Sales Support")) {
				strExitState ="Pre Sales Support";
				data.setSessionData(Constants.S_INTENT, strReturnValue);
				data.setSessionData(Constants.FNAG_MN_001_VALUE, strReturnValue);
			} else if(strReturnValue.equalsIgnoreCase("Commissions")){
				strExitState ="Commissions";
				data.setSessionData(Constants.S_INTENT, strReturnValue);
				data.setSessionData(Constants.FNAG_MN_001_VALUE, strReturnValue);
			}
			else if(strReturnValue.equalsIgnoreCase("Annuities")) {
				strExitState="Annuities";
				data.setSessionData(Constants.S_INTENT, strReturnValue);
				data.setSessionData(Constants.FNAG_MN_001_VALUE, strReturnValue);
			}else if(strReturnValue.equalsIgnoreCase("Claims")) {
				strExitState="Claims";
				data.setSessionData(Constants.S_INTENT, strReturnValue);
				data.setSessionData(Constants.FNAG_MN_001_VALUE, strReturnValue);
			}
			else strExitState=Constants.ER;
		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in FNAG_MN_001_VALUE :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"FNAG_MN_001_VALUE :: "+strExitState);

		return strExitState;

	}
}





