package com.farmers.nlu;

import java.util.ArrayList;
import java.util.HashMap;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class CheckAppTagDetails_BC extends DecisionElementBase{



	@SuppressWarnings("unchecked")
	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER, apptag = Constants.EmptyString, lob = Constants.EmptyString, invocationFlow = Constants.EmptyString, flowLOBKey = Constants.EmptyString;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		HashMap<String, ArrayList<String>> apptagMap = null;
		ArrayList<String> apptagDetails = null;
		String isAuthReq = Constants.EmptyString, isImmediateTxf = Constants.EmptyString, isClarificationQuestion = Constants.EmptyString, nextAction = Constants.EmptyString ,onBoardingEligible= Constants.EmptyString; 
		try {
			apptag = (String) data.getSessionData(Constants.APPTAG);
			lob = (String) data.getSessionData(Constants.S_BU);
			invocationFlow = (String) data.getSessionData("INVOCATION_FLOW");
			data.addToLog(currElementName, " App Tag :: " + apptag + " | " + "LOB :: " + lob + " | INVOKATION_FLOW  :: " + invocationFlow);
			
			invocationFlow = invocationFlow.replaceAll("\\s", "");
			lob = lob.replaceAll("\\s", "");
			
			flowLOBKey = (String) data.getSessionData("S_"+invocationFlow+"_"+lob);
			data.addToLog(currElementName, " FLOW LOB Key :: " + flowLOBKey);
			
		String KYC_Flag=(String) data.getSessionData(Constants.S_KYC_AUTHENTICATED);
			
	
			
			String apptagKey=apptag+Constants.COLON+flowLOBKey;
			
			
			data.addToLog("apptagKey", apptagKey);
			
			
if(apptag!=null && !Constants.EmptyString.equalsIgnoreCase(apptag) && lob!=null & !Constants.EmptyString.equalsIgnoreCase(lob)) {
				
	apptagMap = (HashMap<String, ArrayList<String>>) data.getApplicationAPI().getApplicationData(Constants.A_MAP_APPTAG_CONFIG);
				
			if (apptagMap!=null) {
				
					data.addToLog(currElementName, " Key :: " + apptag+Constants.COLON+flowLOBKey + " Contains key : " + apptagMap.containsKey(apptag+Constants.COLON+flowLOBKey));
					
					apptagDetails = apptagMap.get(apptag+Constants.COLON+flowLOBKey);
					
					if(apptagDetails!=null && apptagDetails.size()>0) {
					

			if(apptagKey.equalsIgnoreCase("application-get_help:SHARED_MM_BW") || apptagKey.equalsIgnoreCase("application-get_help:FARMER_VANITY_VANITY") || apptagKey.equalsIgnoreCase("application-get_help:SHARED_MM_FARMERSERVICE")
								
								|| apptagKey.equalsIgnoreCase("application-get_help:SHARED_MAIN_MENU_FWS") || apptagKey.equalsIgnoreCase("quote_renters-get:SHARED_MM_BW") || apptagKey.equalsIgnoreCase("quote_renters-get:SHARED_MM_FARMERSERVICE")
								
								
								||  apptagKey.equalsIgnoreCase("quote_renters-get:FARMER_VANITY_VANITY") || apptagKey.equalsIgnoreCase("quote_renters-get:21_VANITY_FF_AUTO_SERVICE") || apptagKey.equalsIgnoreCase("quote_renters-get:21SM") ||  apptagKey.equalsIgnoreCase("quote_renters-get:SHARED_MAIN_MENU_FWS") ) {
						
						
						if(KYC_Flag!=null && !Constants.EmptyString.equalsIgnoreCase(KYC_Flag) && KYC_Flag.equalsIgnoreCase("YES")) {
							
							isAuthReq = apptagDetails.get(3);
							isImmediateTxf = apptagDetails.get(4);
							isClarificationQuestion = apptagDetails.get(5);
							nextAction = apptagDetails.get(6);
							onBoardingEligible= apptagDetails.get(7);
						
							data.setSessionData(Constants.IS_AUTHREQ, isAuthReq.trim());
							data.setSessionData(Constants.IS_CLARIFIER, replaceSpecialChar(isClarificationQuestion.trim()));
							data.setSessionData(Constants.IS_IMMEDIATE_TXF, isImmediateTxf.trim());
							data.setSessionData(Constants.S_APP_ONBOARDING_ELIGIBLE_FLAG, onBoardingEligible.trim());
							
							data.setSessionData(Constants.NEXT_ACTION, "TRANSFER");
							
							skipAuthadnContinue(nextAction.trim(), data);
							data.addToLog(currElementName, " Auth Required :: " + isAuthReq + " | " + "Clarifier :: " + isClarificationQuestion + "Immediate Txf : " + isImmediateTxf
									+ "Next Action : " + nextAction +" | "+ onBoardingEligible);
							
							strExitState = "NEXTACTION";
						}
						
						
					else{
							isAuthReq = apptagDetails.get(3);
							isImmediateTxf = apptagDetails.get(4);
							isClarificationQuestion = apptagDetails.get(5);
							nextAction = apptagDetails.get(6);
							onBoardingEligible= apptagDetails.get(7);
						
							data.setSessionData(Constants.IS_AUTHREQ, isAuthReq.trim());
							data.setSessionData(Constants.IS_CLARIFIER, replaceSpecialChar(isClarificationQuestion.trim()));
							data.setSessionData(Constants.IS_IMMEDIATE_TXF, isImmediateTxf.trim());
							data.setSessionData(Constants.NEXT_ACTION, nextAction.trim());
							data.setSessionData(Constants.S_APP_ONBOARDING_ELIGIBLE_FLAG, onBoardingEligible.trim());
							skipAuthadnContinue(nextAction.trim(), data);
							data.addToLog(currElementName, " Auth Required :: " + isAuthReq + " | " + "Clarifier :: " + isClarificationQuestion + "Immediate Txf : " + isImmediateTxf
									+ "Next Action : " + nextAction +" | "+ onBoardingEligible);
							
							strExitState = Constants.DONE;
						}
						
						
						
						}
						
						else{
							
							isAuthReq = apptagDetails.get(3);
							isImmediateTxf = apptagDetails.get(4);
							isClarificationQuestion = apptagDetails.get(5);
							nextAction = apptagDetails.get(6);
							onBoardingEligible= apptagDetails.get(7);
						
							data.setSessionData(Constants.IS_AUTHREQ, isAuthReq.trim());
							data.setSessionData(Constants.IS_CLARIFIER, replaceSpecialChar(isClarificationQuestion.trim()));
							data.setSessionData(Constants.IS_IMMEDIATE_TXF, isImmediateTxf.trim());
							data.setSessionData(Constants.NEXT_ACTION, nextAction.trim());
							data.setSessionData(Constants.S_APP_ONBOARDING_ELIGIBLE_FLAG, onBoardingEligible.trim());
							skipAuthadnContinue(nextAction.trim(), data);
							data.addToLog(currElementName, " Auth Required :: " + isAuthReq + " | " + "Clarifier :: " + isClarificationQuestion + "Immediate Txf : " + isImmediateTxf
									+ "Next Action : " + nextAction +" | "+ onBoardingEligible);
							
							strExitState = Constants.DONE;
						}
					
					
					
					}else {
						data.addToLog(currElementName,"App tag configuration for app tag : " + apptag + " and lob : " + lob + " is missing" );
					}
				}

			} else {
				data.addToLog(currElementName,"Either apptag or lob is null or empty");
			}
		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in CheckAppTagDetails_BC :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"CheckAppTagDetails_BC :: "+strExitState);
		return strExitState;
	}
	
	private String replaceSpecialChar(String clarifierQuestion) {
		String replaced = clarifierQuestion.replaceAll("[^a-zA-Z0-9]+", ".");
		return replaced;
		
	}
	public void skipAuthadnContinue(String nextAction, DecisionElementData data) throws AudiumException {
		
		if("POLICY_QUESTION".equalsIgnoreCase(nextAction) || "POLICY_CHANGE".equalsIgnoreCase(nextAction)) {
			data.setSessionData(Constants.SHARED_AUTH_RETURN, Constants.STRING_YES);
			data.addToLog("Next Element :- ", nextAction);
		}
		
	}
	
	

}
