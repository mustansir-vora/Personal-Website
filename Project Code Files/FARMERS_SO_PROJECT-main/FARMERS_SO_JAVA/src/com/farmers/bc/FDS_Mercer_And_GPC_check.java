package com.farmers.bc;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;

//CS1175332-Farmers Direct Services (FDS/Personal Lines) - Billing & Payments - CA Payroll Deduct
public class FDS_Mercer_And_GPC_check  extends DecisionElementBase{

	@Override
	public String doDecision(String currentElementName, DecisionElementData data) throws Exception {
		String FDSMercerCheck = (String)data.getSessionData("S_FDS_Mercer_Check");
		String gpcCode = (String)data.getSessionData("S_FDS_GPC");
		String exitState = "No";
				if(null!=FDSMercerCheck && !FDSMercerCheck.isEmpty() && FDSMercerCheck.equalsIgnoreCase("Y")) {
					if(gpcCode !=null && !gpcCode.isEmpty() && gpcCode.equals("017")) {
						exitState = "Yes";
					}else {
						data.addToLog("GPC code null,empty or not matched with 017 ::",gpcCode);
					}
					
				}else {
					data.addToLog("FDS_Mercer_check is not available or flag not turned ON ::", FDSMercerCheck);
				}
		return exitState;
	}


}
