package com.farmers.nlu;


import com.audium.server.AudiumException;
import com.audium.server.session.ActionElementData;
import com.audium.server.voiceElement.ActionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class SetNLUFlowSpecific extends ActionElementBase {

	public void doAction(String currElementName, ActionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		String invocationFlow = Constants.EmptyString, strNLUFlag = Constants.EmptyString;
		try {
			data.setSessionData("S_NLU_FLOW_ENABLED", "FALSE");
			invocationFlow = (String) data.getSessionData("INVOCATION_FLOW");
			data.addToLog(currElementName, "Invocation Flow : "+invocationFlow);
			if(invocationFlow != null && !invocationFlow.equals("") && !invocationFlow.equals("NA")) {
				strNLUFlag = (String) data.getSessionData("S_"+invocationFlow+"_NLU_FLAG");
				if("Y".equalsIgnoreCase(strNLUFlag)) {
					data.setSessionData("S_NLU_FLOW_ENABLED", "TRUE");
				}
				strExitState = Constants.DONE;
			}else {
				strExitState = "NA";
				data.addToLog(currElementName, "Invocation flow value not available");
			}
		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in SetNLUFlowSpecific  :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"SetNLUFlowSpecific   :: "+strExitState);
	}

}


