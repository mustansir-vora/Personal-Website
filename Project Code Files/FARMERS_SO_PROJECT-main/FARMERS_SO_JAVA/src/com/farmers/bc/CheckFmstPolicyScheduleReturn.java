package com.farmers.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class CheckFmstPolicyScheduleReturn extends DecisionElementBase {
	public String doDecision(String elementName, DecisionElementData data) throws AudiumException {
		String strExitState = "ER";
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		String strProductCode = Constants.EmptyString, strMHCodes = Constants.EmptyString, strSDCodes = Constants.EmptyString, strRPCodes = Constants.EmptyString, strAuto = Constants.EmptyString;
		try {
			String strPolicyFound = (String) data.getElementData("FmstCollectPolicyOnly_Schedule", "POLICY_FOUND");
			/*
			 * strMHCodes = (String) data.getSessionData("S_MH_PRODUCT_CODES"); strSDCodes =
			 * (String) data.getSessionData("S_SD_PRODUCT_CODES"); strRPCodes = (String)
			 * data.getSessionData("S_RP_PRODUCT_CODES"); strAuto = (String)
			 * data.getSessionData("S_AUTO_PRODUCT_CODES");
			 */

			data.addToLog(elementName, " FmstCollectPolicyOnly_Schedule :: Return Value " + strPolicyFound);

			/*
			 * data.addToLog(elementName, " MH Codes " + strMHCodes);
			 * data.addToLog(elementName, " SD Codes " + strSDCodes);
			 * data.addToLog(elementName, " RP Codes " + strRPCodes);
			 * data.addToLog(elementName, " Auto Code " + strAuto);
			 */

			if(strPolicyFound!=null && !Constants.EmptyString.equalsIgnoreCase(strPolicyFound)) {
				if("Y".equalsIgnoreCase(strPolicyFound)) {
					strProductCode = (String) data.getSessionData("S_PRODUCT_CODE");
					data.addToLog(elementName, " Product Code " + strProductCode);
					/*
					 * if(strMHCodes.contains(strProductCode)) { strExitState =
					 * Constants.S_MOBILE_HOME; data.setSessionData("TRANSFER_KEY", strExitState);
					 * }else if(strSDCodes.contains(strProductCode)) { strExitState =
					 * Constants.S_SPECIALTY_DWELLING; data.setSessionData("TRANSFER_KEY",
					 * strExitState); }else if(strRPCodes.contains(strProductCode)) { strExitState =
					 * Constants.S_RECREATIONALPRODUCTS; data.setSessionData("TRANSFER_KEY",
					 * strExitState); }else if(strAuto.contains(strProductCode)) { strExitState =
					 * Constants.S_AUTO; data.setSessionData("TRANSFER_KEY", strExitState); }else {
					 * strExitState = "NO_POLICY_FOUND"; }
					 */

					if ((strProductCode.equals("105") || strProductCode.equals("103") ||
							strProductCode.equals("107") || strProductCode.equals("106") ||
							strProductCode.equals("672") || strProductCode.equals("444"))) {
						strExitState =
								Constants.S_MOBILE_HOME;
						data.setSessionData("TRANSFER_KEY", strExitState);
					}

					else if (strProductCode.equals("381")) { strExitState
						= Constants.S_SPECIALTY_DWELLING;
					data.setSessionData("TRANSFER_KEY", strExitState);}
					else if (strProductCode.equals("276")) {
						strExitState = Constants.S_AUTO;
					}
					else { strExitState = Constants.ER; }

				}else {
					data.addToLog(elementName, " No Policy Found");
					strExitState = "NO_POLICY_FOUND";
				}
			}else {
				data.addToLog(elementName, " FmstCollectPolicyOnly :: Return Value is null or empty");
				strExitState = "NO_POLICY_FOUND";
			}

		} catch (Exception e) {
			data.addToLog(elementName,"Exception in CheckFmstPolicyReturn :: "+e);
			caa.printStackTrace(e);
		}
		return strExitState;
	}
}
