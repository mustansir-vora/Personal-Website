package com.farmers.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class CallerTypeMenuDNISCheck_BC extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.STRING_YES;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {
			String NoCallerTypeMenuDNIS = (String) data.getSessionData("S_NO_CALLER_TYPE_MENU_DNIS");
			data.addToLog(currElementName, "S_NO_CALLER_TYPE_MENU_DNIS from Config file :: " + NoCallerTypeMenuDNIS);

			String callerDNIS = (String) data.getSessionData("S_DNIS");
			data.addToLog(currElementName, "S_DNIS of the caller :: " + callerDNIS);

			if (NoCallerTypeMenuDNIS.contains(callerDNIS)) {
				strExitState = Constants.STRING_NO;
				data.addToLog(currElementName,
						"StrExitState If the DNIS does not have the caller type menu :: " + strExitState);

			} else {
				strExitState = Constants.STRING_YES;
				data.addToLog(currElementName,
						"StrExitState If DNIS whic have the caller type menu :: " + strExitState);

			}

		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in CallerTypeMenuDNISCheck_BC :: " + e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName, "CallerTypeMenuDNISCheck_BC :: " + strExitState);

		return strExitState;
	}
}