package com.farmers.fws.fgia;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class GetZipCode extends DecisionElementBase{

	@Override
	public String doDecision(String currElementName, DecisionElementData decisionElementData) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(decisionElementData);
		try {

			String zipCode  = decisionElementData.getElementData("FWSSZ_MN_001", "Return_Value");
			decisionElementData.addToLog(decisionElementData.getCurrentElement(), "Element Name : FWSSZ_MN_001"+" , ZIP Code : "+zipCode);
			if(zipCode != null && !zipCode.equals("")) {
				
				if (zipCode.equalsIgnoreCase(Constants.NOMATCH)) {
					strExitState = Constants.NOMATCH;
				}
				else if (zipCode.equalsIgnoreCase(Constants.NOINPUT)) {
					strExitState = Constants.NOINPUT;
				}
				else {
					decisionElementData.addToLog(decisionElementData.getCurrentElement(), "Zip Code : "+zipCode);
					decisionElementData.setSessionData(Constants.S_FWS_ZIP_CODE, zipCode);
					strExitState = Constants.SU;
				}
				
			}
			
		} catch (Exception e) {
			decisionElementData.addToLog(currElementName,"Exception in GET_FWSARC_MN_002_VALUE :: "+e);
			caa.printStackTrace(e);
		}
		return strExitState;
	}

}
