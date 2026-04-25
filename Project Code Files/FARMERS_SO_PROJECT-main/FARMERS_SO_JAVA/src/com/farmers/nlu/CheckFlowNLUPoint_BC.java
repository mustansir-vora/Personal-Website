package com.farmers.nlu;


import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class CheckFlowNLUPoint_BC extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		String elementDataName = Constants.EmptyString;
		try {
			elementDataName = (String) data.getSessionData("ElementKey");
			String strExitPoint = (String) data.getElementData(elementDataName,"RETURN_VALUE");
			data.addToLog(currElementName, "Flow Exit Point : "+strExitPoint);
			if(strExitPoint != null && !strExitPoint.equals("") && !strExitPoint.equals("NA")) {
				if("NLU".equalsIgnoreCase(strExitPoint)) {
					strExitState = "NLU";
					data.addToLog(currElementName, "Invoke NLU Module");
				}else {
					strExitState = "NA";
					data.addToLog(currElementName, "Default Return");
				}
			}else {
				strExitState = "NA";
				data.addToLog(currElementName, "Default Return");
			}
		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in CheckFlowNLUPoint_BC  :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"CheckFlowNLUPoint_BC   :: "+strExitState);
		return strExitState;
	}

}


