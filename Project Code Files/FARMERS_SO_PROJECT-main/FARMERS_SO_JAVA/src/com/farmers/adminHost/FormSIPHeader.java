package com.farmers.adminHost;

import com.audium.server.session.ActionElementData;
import com.audium.server.voiceElement.ActionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class FormSIPHeader extends ActionElementBase {

	@Override
	public void doAction(String currElementName, ActionElementData data) throws Exception {
		Object routingPriority = Constants.EmptyString;
		String callerType = Constants.EmptyString;
		String policyNumber = Constants.EmptyString;
		String idAuthFlag = Constants.EmptyString;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		
		try {
			
			routingPriority = null != (Object) data.getSessionData(Constants.S_ROUTING_PRIORITY) ? (Object) data.getSessionData(Constants.S_ROUTING_PRIORITY) : "08";
			data.addToLog(currElementName, "Routing Priority recieved from Admin portal :: " + routingPriority);
			if (routingPriority.toString().length() == 1) {
				routingPriority = "0" + routingPriority;
				data.addToLog(currElementName, "Routing Priority post manipulation :: " + routingPriority);
			}
			
			callerType = null != (String) data.getSessionData(Constants.FNWL_CALLER_TYPE) ? (String) data.getSessionData(Constants.FNWL_CALLER_TYPE) : "03";
			
			policyNumber = null != (String) data.getSessionData(Constants.FNWL_POLICY_NUM) ? (String) data.getSessionData(Constants.FNWL_POLICY_NUM) : Constants.EmptyString;
			
			idAuthFlag = (String) data.getSessionData("FNWL_AUTH");
			
			String VXML3 = new StringBuffer(callerType).append('|').append(idAuthFlag).append('|').append(routingPriority.toString()).append('|').append(policyNumber).toString();
			data.addToLog(currElementName, "VXML_3 return variable value :: " + VXML3);
			data.setSessionData(Constants.S_VXML3, VXML3);
			
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in doDecision method in FormSIPHeader :: "+e);
			caa.printStackTrace(e);
		}
		
	}

}
