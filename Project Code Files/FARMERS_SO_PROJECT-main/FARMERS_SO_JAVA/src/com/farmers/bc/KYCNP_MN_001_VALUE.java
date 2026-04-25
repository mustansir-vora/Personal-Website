package com.farmers.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class KYCNP_MN_001_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("KYCNP_MN_001_Call","Return_Value");
			data.setSessionData(Constants.KYCNP_MN_001_VALUE, strReturnValue);
			if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
				data.setSessionData("S_LOOKUP_ANOTHER_POLICY", "Y");
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
				data.setSessionData("S_LOOKUP_ANOTHER_POLICY", "Y");
			} else if(strReturnValue.equalsIgnoreCase(Constants.REPRESENTATIVE)) {
				strExitState = Constants.REPRESENTATIVE;
			}  else if(strReturnValue.equalsIgnoreCase(Constants.STRING_YES)) {
				strExitState = Constants.STRING_YES;
				
				String policySource = (String) data.getSessionData(Constants.S_FINAL_BRAND);
				if (null != policySource) {
					
					switch (policySource) {
					case "BW":
						data.setSessionData(Constants.S_BU, "Foremost Auto");
						data.setSessionData(Constants.S_LOB,"Foremost Auto");
						data.setSessionData(Constants.S_CATEGORY,"FM_AUTO");
						data.setSessionData("S_FLAG_BW_BU", Constants.STRING_YES);
						data.addToLog(currElementName, "User has selected BW Policy in KYC :: Overriding BU :: " + data.getSessionData(Constants.S_BU) + " :: Overriding Category :: " + data.getSessionData(Constants.S_CATEGORY));
						break;
					case "FWS-ARS":
						data.setSessionData(Constants.S_BU, "FWS Service");
						data.setSessionData(Constants.S_LOB, "FWS Service");
						data.setSessionData(Constants.S_CATEGORY,"FWS_SVC");
						data.setSessionData("S_FLAG_FWS_BU", Constants.STRING_YES);
						data.addToLog(currElementName, "User has selected FWS Policy in KYC :: Overriding BU :: " + data.getSessionData(Constants.S_BU) + " :: Overriding Category :: " + data.getSessionData(Constants.S_CATEGORY));
						break;
					case "FWS-A360":
						data.setSessionData(Constants.S_BU, "FWS Service");
						data.setSessionData(Constants.S_LOB, "FWS Service");
						data.setSessionData(Constants.S_CATEGORY,"FWS_SVC");
						data.setSessionData("S_FLAG_FWS_BU", Constants.STRING_YES);
						data.addToLog(currElementName, "User has selected FWS Policy in KYC :: Overriding BU :: " + data.getSessionData(Constants.S_BU) + " :: Overriding Category :: " + data.getSessionData(Constants.S_CATEGORY));
						break;
					case "PLA":
						data.setSessionData(Constants.S_BU, "Farmers");
						data.setSessionData(Constants.S_LOB, "Farmers");
						data.setSessionData(Constants.S_CATEGORY,"FarmersSRV");
						data.setSessionData("S_FLAG_FDS_BU", Constants.STRING_YES);
						data.addToLog(currElementName, "User has selected Farmers Policy in KYC :: Overriding BU :: " + data.getSessionData(Constants.S_BU) + " :: Overriding Category :: " + data.getSessionData(Constants.S_CATEGORY));
						break;
					case "GWPC":
						data.setSessionData(Constants.S_BU, "Farmers");
						data.setSessionData(Constants.S_LOB, "Farmers");
						data.setSessionData(Constants.S_CATEGORY,"FarmersSRV");
						data.setSessionData("S_FLAG_FDS_BU", Constants.STRING_YES);
						data.addToLog(currElementName, "User has selected Farmers Policy in KYC :: Overriding BU :: " + data.getSessionData(Constants.S_BU) + " :: Overriding Category :: " + data.getSessionData(Constants.S_CATEGORY));
						break;
					case "AUTO":
						data.setSessionData(Constants.S_BU, "Auto-Service");
						data.setSessionData(Constants.S_LOB, "Auto-Service");
						data.setSessionData("S_FLAG_21ST_BU", Constants.STRING_YES);
						break;
					case "FM":
						data.setSessionData(Constants.S_BU, "Foremost");
						data.setSessionData(Constants.S_LOB, "Foremost");
						data.setSessionData(Constants.S_CATEGORY,"FM-SLS-SRV");
						data.setSessionData("S_FLAG_FOREMOST_BU", Constants.STRING_YES);
						data.addToLog(currElementName, "User has selected Foremost Policy in KYC :: Overriding BU :: " + data.getSessionData(Constants.S_BU) + " :: Overriding Category :: " + data.getSessionData(Constants.S_CATEGORY));
						break;
					default:
						break;
					}
					data.addToLog(currElementName, "Bristol Flag :: " + data.getSessionData("S_FLAG_BW_BU") + " :: Farmers Flag :: " + data.getSessionData("S_FLAG_FDS_BU") + " :: Foremost Flag :: " + data.getSessionData("S_FLAG_FOREMOST_BU") + " :: FWS Flag :: "  + data.getSessionData("S_FLAG_FWS_BU") + " :: 21st Flag :: " + data.getSessionData("S_FLAG_21ST_BU"));
				}
			}  else if(strReturnValue.equalsIgnoreCase(Constants.STRING_NO)) {
				data.setSessionData("S_LOOKUP_ANOTHER_POLICY", "Y");
				
				strExitState = Constants.STRING_NO;
			} else {
				strExitState = Constants.ER;
			} 
			data.addToLog(currElementName,"KYCNP_MN_001_VALUE :: "+strExitState);
		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in KYCNP_MN_001_VALUE :: "+e);
			caa.printStackTrace(e);
		}
		return strExitState;		
	}
}