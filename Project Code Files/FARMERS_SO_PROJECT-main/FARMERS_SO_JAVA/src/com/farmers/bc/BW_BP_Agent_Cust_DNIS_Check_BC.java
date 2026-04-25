package com.farmers.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class BW_BP_Agent_Cust_DNIS_Check_BC extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = "ER";
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {
			String strReturnValue = (String) data.getElementData("BWBP_MN_002_Call", "Return_Value");
			data.addToLog(currElementName, " BWBP_MN_002_Call : Return_Value :: " + strReturnValue);

			String producerLine = (String) data.getSessionData("S_PRODUCER_LINE");
			data.addToLog(currElementName, "The Value of S_PRODUCER_LINE is : " + producerLine);

			if (null!=producerLine && !producerLine.isEmpty() && "TRUE".equalsIgnoreCase(producerLine)) {
				if (strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {

				} else if (strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {

				} else if (strReturnValue.equalsIgnoreCase(Constants.LOOKUP_ANOTHER_POLICY)) {
					caa.createMSPKey(caa, data, "BWBP_MN_002", "LOOKUP ANOTHER POLICY");
				} else if (strReturnValue.equalsIgnoreCase(Constants.OTHER_BILLING_QUESTIONS)) {
					caa.createMSPKey(caa, data, "BWBP_MN_002", "OTHER BILLING QUESTIONS");
				}  else if (strReturnValue.equalsIgnoreCase(Constants.MAIN_MENU)) {

				} else if (strReturnValue.equalsIgnoreCase(Constants.REPRESENTATIVE)) {
					caa.createMSPKey(caa, data, "BWBP_MN_002", "REPRESENTATIVE");
				} else if (strReturnValue.equalsIgnoreCase(Constants.LIENHOLDER)) {
					caa.createMSPKey(caa, data, "BWBP_MN_002", "LIENHOLDER");
					data.setSessionData(Constants.S_CALLLER_TYPE,"03");
				} else if (strReturnValue.equalsIgnoreCase(Constants.REPEAT)) {

				}
				data.addToLog(currElementName,"StrExitState If the DNIS does not have the caller type menu :: " + strExitState);
				strExitState = "Agent_DNIS";
			} else {
				if (strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {

				} else if (strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {

				} else if (strReturnValue.equalsIgnoreCase(Constants.LOOKUP_ANOTHER_POLICY)) {
					caa.createMSPKey(caa, data, "BWBP_MN_002", "LOOKUP ANOTHER POLICY_CUST");
				} else if (strReturnValue.equalsIgnoreCase(Constants.OTHER_BILLING_QUESTIONS)) {
					caa.createMSPKey(caa, data, "BWBP_MN_002", "OTHER BILLING QUESTIONS_CUST");
				}  else if (strReturnValue.equalsIgnoreCase(Constants.MAIN_MENU)) {

				} else if (strReturnValue.equalsIgnoreCase(Constants.REPRESENTATIVE)) {
					caa.createMSPKey(caa, data, "BWBP_MN_002", "REPRESENTATIVE_CUST");
				} else if (strReturnValue.equalsIgnoreCase(Constants.LIENHOLDER)) {
					caa.createMSPKey(caa, data, "BWBP_MN_002", "LIENHOLDER_CUST");
					data.setSessionData(Constants.S_CALLLER_TYPE,"03");
				} else if (strReturnValue.equalsIgnoreCase(Constants.REPEAT)) {

				}
				strExitState = "Customer_DNIS";
				data.addToLog(currElementName,"StrExitState If DNIS whic have the caller type menu :: " + strExitState);
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in CallerTypeMenuDNISCheck_BC :: " + e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName, "CallerTypeMenuDNISCheck_BC :: " + strExitState);

		return strExitState;
	}
}