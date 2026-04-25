package com.farmers.nlu;

import java.util.LinkedList;
import java.util.Queue;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class SetTransferKey_BC extends DecisionElementBase {

	@Override
	public String doDecision(String name, DecisionElementData data) throws Exception {
		// TODO Auto-generated method stub
		String appTag = Constants.EmptyString, categoryCode = Constants.EmptyString, nluTransferKey = Constants.EmptyString, strExitState = Constants.ER;
		String strElementName = data.getCurrentElement();
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			appTag = (String) data.getSessionData(Constants.APPTAG);
			categoryCode = (String) data.getSessionData(Constants.S_CATEGORY);

			if(appTag!=null && !Constants.EmptyString.equalsIgnoreCase(appTag) && categoryCode!=null && !Constants.EmptyString.equalsIgnoreCase(categoryCode)) {

				data.addToLog(strElementName, " App Tag :: " + appTag + " | " + "Category Code :: " + categoryCode);

				nluTransferKey = Constants.NLU+Constants.UNDERSCORE+appTag+Constants.COLON+categoryCode;
				data.setSessionData(Constants.S_MENU_SELCTION_KEY, nluTransferKey);
				data.addToLog(strElementName, " NLU Transfer Key :: " + data.getSessionData(Constants.S_MENU_SELCTION_KEY));
				addMSPInQueue(data,nluTransferKey);
				strExitState = Constants.DONE;
			}else {
				data.addToLog(strElementName, "Either App tag or category code is null or empty");
			}

		}catch (Exception e) {
			data.addToLog(strElementName,"Exception in CheckAppTagDetails_BC :: "+e);
			caa.printStackTrace(e);
		}
		return strExitState;
	}
	
	private void addMSPInQueue( DecisionElementData data, String mspKey) {
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		Queue<String> lastFiveMSPValues = null;
		try {
			lastFiveMSPValues = (Queue<String>) data.getSessionData("S_LAST_5_MSP_VALUES");
			data.addToLog(data.getCurrentElement(), "Value of lastFiveMSPValues NLU Queue :: " + lastFiveMSPValues);
			if (lastFiveMSPValues == null) {
				lastFiveMSPValues = new LinkedList<>();
			}
			lastFiveMSPValues.offer(mspKey);

			while (lastFiveMSPValues.size() > 5) {
				lastFiveMSPValues.poll();
			}
			data.setSessionData("S_LAST_5_MSP_VALUES", lastFiveMSPValues);
		} catch (AudiumException e) {
			data.addToLog(data.getCurrentElement(),"Exception in Add MSP in NLU Queue :: "+e);
			caa.printStackTrace(e);
		}

		data.addToLog(data.getCurrentElement(), "Value of lastFiveMSPValues NLU Queue After Operation :: " + lastFiveMSPValues);
	}

}
