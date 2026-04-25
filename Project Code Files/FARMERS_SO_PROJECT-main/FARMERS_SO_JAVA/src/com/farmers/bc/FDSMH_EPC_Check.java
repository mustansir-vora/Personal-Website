package com.farmers.bc;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class FDSMH_EPC_Check extends DecisionElementBase{

	@Override
	public String doDecision(String currentElementName, DecisionElementData data) throws Exception {
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		String fdsMercerHandlingFlag = (String)data.getSessionData("S_FDS_Mercer_Handling");
		String exitState = "NO";
		String epcPaymentUSFlag = (String)data.getSessionData("S_EPC_PaymentUS");
		if(fdsMercerHandlingFlag!=null && !fdsMercerHandlingFlag.isEmpty() && fdsMercerHandlingFlag.equalsIgnoreCase(Constants.Y_FLAG)) {
			if(epcPaymentUSFlag !=null && !epcPaymentUSFlag.isEmpty() && epcPaymentUSFlag.equals("No")) {
				data.setSessionData("S_SERVICING_PHONE_NUMBER_ENABLED", "Y");
				caa.createMSPKey(caa, data, "MERCER_HANDLING", "FDS MERCER");
				data.addToLog(currentElementName, "Servicing phone number enabled FDSMH flag ::"+fdsMercerHandlingFlag+": EpcPaymentUSFlag ::"+epcPaymentUSFlag);
				return exitState = "YES";
			}else {
				data.setSessionData("S_SERVICING_PHONE_NUMBER_ENABLED", "N");
				return exitState;
			}
		}
		return exitState;
	}

}
