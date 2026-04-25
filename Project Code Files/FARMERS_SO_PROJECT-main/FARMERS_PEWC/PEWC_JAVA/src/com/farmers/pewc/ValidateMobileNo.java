package com.farmers.pewc;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.Constants;
import com.farmers.util.GlobalsCommon;

public class ValidateMobileNo extends DecisionElementBase{

	@Override
	public String doDecision(String strElementName, DecisionElementData decisionData) throws Exception {
		String strExitState = Constants.ERROR;
		try {
			
			String strMenuName = decisionData.getElementHistory().get(decisionData.getElementHistory().size()-2);
			strExitState = decisionData.getExitStateHistory().get(decisionData.getExitStateHistory().size()-1);
			decisionData.addToLog(strElementName, "Menu Name = "+strMenuName+" , strExitState = "+strExitState);
			
			if(strExitState.equalsIgnoreCase(Constants.DONE)){
				String digitValue = decisionData.getElementData(strMenuName, Constants.DTMF_INPUT);
				if(GlobalsCommon.isRepetitive(digitValue)) {
					strExitState = getExitState("max_nomatch", decisionData);
				}else {
					decisionData.addToLog(strElementName, "Input is not Repetitive ");
					decisionData.setSessionData(Constants.S_MOBILE_NO, digitValue);
					decisionData.addToLog(strElementName, " Session key = "+Constants.S_MOBILE_NO+" , session value = "+digitValue);
					decisionData.addToLog(strElementName, "strExitState = "+strExitState);
				}
			}else{
				strExitState = getExitState(strExitState, decisionData);
			}

			decisionData.addToLog(strElementName, "Exit State = "+strExitState+" Previous Menu Name = "+strMenuName);

			
			
		} catch (Exception e) {
			decisionData.addToLog(decisionData.getCurrentElement(), GlobalsCommon.getExceptionTrace(e));
		}
		return strExitState;
	}
	
	private String getExitState(String exitState, DecisionElementData decisionData) {
		String strExitState = exitState;
		try {
			
			int digitTriesCount = getCounterValue(Constants.S_DIGIT_TRIES_COUNTER, decisionData);
			if(digitTriesCount == Constants.NUMERIC_2){
				strExitState = Constants.DIGIT_TRIES_EXCEEDED;
			}
			incrementTriesCounter(Constants.S_DIGIT_TRIES_COUNTER, decisionData);
			
			
		} catch (Exception e) {
			decisionData.addToLog(decisionData.getCurrentElement(), GlobalsCommon.getExceptionTrace(e));
		}
		return strExitState;
	}
	
	private int getCounterValue(String counterName, DecisionElementData decisionData){
		int digitTriesCount = 0;
		try {

			if( decisionData.getSessionData(counterName) == null) {
				digitTriesCount = 0;
			}else {
				Object objDigitCounter = decisionData.getSessionData(counterName);
				if(objDigitCounter instanceof String){
					String counterValue = (String) decisionData.getSessionData(counterName);
					digitTriesCount = Integer.parseInt(counterValue);
				}else{
					digitTriesCount = (int) decisionData.getSessionData(counterName);
				}
			}

			decisionData.addToLog(decisionData.getCurrentElement(), "Digit Counter Value is = "+digitTriesCount);

		} catch (Exception e) {
			decisionData.addToLog(decisionData.getCurrentElement(), GlobalsCommon.getExceptionTrace(e));
		}
		return digitTriesCount;
	}
	
	private int incrementTriesCounter(String counterName, DecisionElementData decisionData){
		int digitTriesCount = 0;
		try {
			digitTriesCount = getCounterValue(counterName, decisionData);
			digitTriesCount = digitTriesCount + 1;
			decisionData.setSessionData(counterName, digitTriesCount);

		} catch (Exception e) {
			decisionData.addToLog(decisionData.getCurrentElement(), GlobalsCommon.getExceptionTrace(e));
		}
		return digitTriesCount;
	}


}
