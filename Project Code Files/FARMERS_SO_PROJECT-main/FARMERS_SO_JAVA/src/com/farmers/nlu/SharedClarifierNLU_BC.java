package com.farmers.nlu;

import java.util.LinkedList;
import java.util.Queue;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class SharedClarifierNLU_BC extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER, strNLCL_MN_001_Value = Constants.EmptyString,
				appTag = Constants.EmptyString, categoryCode = Constants.EmptyString,
				nluTransferKey = Constants.EmptyString, strIsClarifierInvoked = Constants.EmptyString,
				strNLCL_MN_002_Value = Constants.EmptyString;
		String[] appTagResult = null;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {

			String strReturnValue = (String) data.getElementData("NLCL_MN_002_Call", "Return_Value");
			data.addToLog(currElementName, " SharedNLU_Clarifier_Call : Return_Value :: " + strReturnValue);
			strNLCL_MN_001_Value = (String) data.getSessionData("NLCL_MN_001_VALUE");
			if (strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			} else if (strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
			} else if (strReturnValue.contains(Constants.NLU_FAILURE)) {
				strExitState = Constants.NLU_FAILURE;
			} else {
				if (strReturnValue != null && !Constants.NA.equalsIgnoreCase(strReturnValue)
						&& !Constants.EmptyString.equalsIgnoreCase(strReturnValue)) {
					if (strReturnValue.contains(Constants.PIPE2)) {
						appTagResult = strReturnValue.split(Constants.PIPE_SEPERATOR);
						data.addToLog(currElementName,
								" SharedNLU_Clarifier_Call : App Tag Result :: " + strReturnValue);

						data.setSessionData(Constants.APPTAG, appTagResult[0]);

						strIsClarifierInvoked = (String) data.getSessionData("IS_CLARIFIER_ALREADY_INVOKED");
						strNLCL_MN_002_Value = (String) data.getSessionData("NLCL_MN_002_VALUE");
						data.addToLog(currElementName, " Is clarifier invoked again " + strIsClarifierInvoked);

						if (strIsClarifierInvoked != null && !"".equalsIgnoreCase(strIsClarifierInvoked)
								&& "Y".equalsIgnoreCase(strIsClarifierInvoked) && strNLCL_MN_002_Value != null
								&& !"".equalsIgnoreCase(strNLCL_MN_002_Value)
								&& strNLCL_MN_002_Value.equalsIgnoreCase(appTagResult[0])) {
							data.addToLog(currElementName, " Previous Clarifier Intent : " + strNLCL_MN_002_Value);
							data.addToLog(currElementName,
									" Clarifier Invoked again : Same intent provided in Clarifier ");
							return "SAME_TAG";
						}

						data.setSessionData("NLCL_MN_002_VALUE", appTagResult[0]);
						data.setSessionData(Constants.APPTAG_SCORE, appTagResult[0]+"("+appTagResult[1]+")");
					
						data.addToLog(currElementName, " SharedNLU_Clarifier_Call : App Tag :: " + data.getSessionData(Constants.APPTAG));
						data.addToLog(currElementName, " SharedNLU_Clarifier_Call : App Tag with Score :: " + data.getSessionData(Constants.APPTAG_SCORE));
						
						if (strNLCL_MN_001_Value.equalsIgnoreCase(appTagResult[0])) {
							strExitState = "SAME_TAG";
						} else {
							strExitState = Constants.DONE;
						}

						/**
						 * Transfer Key Set
						 */
						appTag = (String) data.getSessionData(Constants.APPTAG);
						categoryCode = (String) data.getSessionData(Constants.S_CATEGORY);

						if (appTag != null && !Constants.EmptyString.equalsIgnoreCase(appTag) && categoryCode != null
								&& !Constants.EmptyString.equalsIgnoreCase(categoryCode)) {
 
							data.addToLog(currElementName,
									" App Tag :: " + appTag + " | " + "Category Code :: " + categoryCode);

							nluTransferKey = Constants.NLU + Constants.UNDERSCORE + appTag + Constants.COLON
									+ categoryCode;
							data.setSessionData(Constants.S_MENU_SELCTION_KEY, nluTransferKey);
							data.addToLog(currElementName,
									" NLU Transfer Key :: " + data.getSessionData(Constants.S_MENU_SELCTION_KEY));
							addMSPInQueue(data, nluTransferKey);
							
							if(appTag.equalsIgnoreCase("cancel-vague")||appTag.equalsIgnoreCase("policy-cancel")||
									appTag.equalsIgnoreCase("policy_auto-cancel")||appTag.equalsIgnoreCase("policy_homeowners-cancel")||appTag.equalsIgnoreCase("cancellation-confirm")) {
								data.setSessionData("S_CANCEL_POLICY_INTENT", "Y");
								//CS1360621-Add 'IA' Routing Qualifier
								data.setSessionData("S_IA_CUSTOMER_CHECK", "Y");
								//
								}
						} else {
							data.addToLog(currElementName, "Either App tag or category code is null or empty");
						}

					}
				} else {
					data.addToLog(currElementName,
							" SharedNLU_Clarifier_Call : Return Value is either null or empty or NA :: ");
					data.setSessionData("NLCL_MN_002_VALUE", strReturnValue);
				}
			}

		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in SharedNLU_Clarifier_Call :: " + e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName, "SharedNLU_Clarifier_Call :: " + strExitState);
		return strExitState;
	}

	private void addMSPInQueue(DecisionElementData data, String mspKey) {
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		Queue<String> lastFiveMSPValues = null;
		try {
			lastFiveMSPValues = (Queue<String>) data.getSessionData("S_LAST_5_MSP_VALUES");
			data.addToLog(data.getCurrentElement(), "Value of lastFiveMSPValues Queue :: " + lastFiveMSPValues);
			if (lastFiveMSPValues == null) {
				lastFiveMSPValues = new LinkedList<>();
			}
			lastFiveMSPValues.offer(mspKey);

			while (lastFiveMSPValues.size() > 5) {
				lastFiveMSPValues.poll();
			}
			data.setSessionData("S_LAST_5_MSP_VALUES", lastFiveMSPValues);
		} catch (AudiumException e) {
			data.addToLog(data.getCurrentElement(), "Exception in Add MSP in Queue :: " + e);
			caa.printStackTrace(e);
		}

		data.addToLog(data.getCurrentElement(),
				"Value of lastFiveMSPValues Queue After Operation :: " + lastFiveMSPValues);
	}
}