package com.farmers.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;
 
public class TFPC_MN_005_VALUE extends DecisionElementBase {
 
	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("TFPC_MN_005_Call", "Return_Value");
			data.addToLog(currElementName, " TFPC_MN_005_VALUE : Return_Value :: " + strReturnValue);
			
			 if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
					strExitState = Constants.NOINPUT;
				} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
					strExitState = Constants.NOMATCH;
			} else if (strReturnValue.equalsIgnoreCase(Constants.S_DIFFERENTCARD)) {
				strExitState = Constants.S_DIFFERENTCARD;
			} else if (strReturnValue.equalsIgnoreCase(Constants.S_BANKACCOUNT)) {
				strExitState = Constants.S_BANKACCOUNT;
			} else {
				strExitState = Constants.ER;
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in TFPC_MN_005_VALUE :: " + e);
			caa.printStackTrace(e);
		}
		
		data.addToLog(currElementName, "TFPC_MN_005_VALUE :: " + strExitState);
		return strExitState;
	}
}