package com.farmers.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;
 
public class TFSAL_MN_001_VALUE extends DecisionElementBase {
 
	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("TFSAL_MN_001_Call", "Return_Value");
			data.addToLog(currElementName, " TFSAL_MN_001_VALUE : Return_Value :: " + strReturnValue);
			data.setSessionData("TFSAL_MN_001_VALUE", strReturnValue);
			
			 if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
					strExitState = Constants.NOINPUT;
				} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
					strExitState = Constants.NOMATCH;
			} else if (strReturnValue.equalsIgnoreCase(Constants.STRING_YES)) {
				strExitState = Constants.STRING_YES;
			} else if (strReturnValue.equalsIgnoreCase(Constants.STRING_NO)) {
				strExitState = Constants.STRING_NO;
			} else if (strReturnValue.equalsIgnoreCase(Constants.S_REPRESENTATIVE)) {
				strExitState = Constants.S_REPRESENTATIVE;
			}else if (strReturnValue.equalsIgnoreCase(Constants.S_DEFAULT)) {
				strExitState = Constants.S_DEFAULT;
			} else {
				strExitState = Constants.ER;
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in TFSAL_MN_001_VALUE :: " + e);
			caa.printStackTrace(e);
		}
		
		data.addToLog(currElementName, "TFSAL_MN_001_VALUE :: " + strExitState);
		return strExitState;
	}
}