package com.farmers.util;

import com.audium.server.session.DecisionElementData;

public class PolicyNumManupulation {
	
	public String policyNumManupulation(String inputPolicyNum,  DecisionElementData data) {
		String resultPolicyNum = Constants.EmptyString;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			String strBU = (String) data.getSessionData(Constants.S_BU);
			String strBristolCode = (String)data.getApplicationAPI().getApplicationData("A_BRISTOL_LOB");
			String strFarmersCode = (String)data.getApplicationAPI().getApplicationData("A_FARMERS_LOB");
			String strForemostCode = (String)data.getApplicationAPI().getApplicationData("A_FOREMOST_LOB");
			String strFWSCode = (String)data.getApplicationAPI().getApplicationData("A_FWS_LOB");
			String str21stCode = (String)data.getApplicationAPI().getApplicationData("A_21ST_LOB");
					
			if(strBristolCode!=null && strBU!=null && strBristolCode.contains(strBU)) {
				resultPolicyNum = policyNumForBW(inputPolicyNum, strBU,  data);
			} else if(strFarmersCode!=null && strBU!=null && strFarmersCode.contains(strBU)) {
				resultPolicyNum = policyNumForFarmers(inputPolicyNum, strBU,  data);
			} else if(strForemostCode!=null && strBU!=null && strForemostCode.contains(strBU)) {
				resultPolicyNum = policyNumForForemost(inputPolicyNum, strBU,  data);
			} else if(strFWSCode!=null && strBU!=null && strFWSCode.contains(strBU)) {
				resultPolicyNum = policyNumForFWS(inputPolicyNum, strBU,  data);
			} else if(str21stCode!=null && strBU!=null && str21stCode.contains(strBU)) {
				resultPolicyNum = policyNumFor21st(inputPolicyNum, strBU,  data);
			} else {
				resultPolicyNum = inputPolicyNum;
			}
			data.addToLog("Global", resultPolicyNum);
		} catch (Exception e) {
			data.addToLog(data.getCurrentElement(),"Exception while forming policy num in policyNumManupulation()  :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(data.getCurrentElement(), "resultPolicyNum from policyNumManupulation method : "+resultPolicyNum);
		return resultPolicyNum;
	}
	
	private String policyNumForBW(String inputPolicyNum, String strBU,  DecisionElementData data) {
		String resultPolicyNum = inputPolicyNum;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			if(inputPolicyNum.startsWith(Constants.P_4))
				resultPolicyNum = inputPolicyNum.replaceFirst(Constants.P_4, Constants.P_G);
			else if(inputPolicyNum.startsWith(Constants.P_6)) 
				resultPolicyNum = inputPolicyNum.replaceFirst(Constants.P_6, Constants.P_M);
			else if(inputPolicyNum.startsWith(Constants.P_9)) 
				resultPolicyNum = inputPolicyNum.replaceFirst(Constants.P_6, Constants.P_W);
		} catch (Exception e) {
			data.addToLog(data.getCurrentElement(),"Exception while forming policy num in policyNumForBW()  :: "+e);
			caa.printStackTrace(e);
		}
		return resultPolicyNum;
	}
	
	private String policyNumForFarmers(String inputPolicyNum, String strBU,  DecisionElementData data) {
		data.addToLog("policyNumForFarmers", inputPolicyNum);
		String resultPolicyNum = inputPolicyNum;
		if(inputPolicyNum.length()==10) {
			resultPolicyNum = inputPolicyNum;
		}else if(inputPolicyNum.length()==13) {
			resultPolicyNum = inputPolicyNum.substring(inputPolicyNum.length()-10,inputPolicyNum.length());
		}else if(inputPolicyNum.length()==12) {
			resultPolicyNum = inputPolicyNum.substring(inputPolicyNum.length(),inputPolicyNum.length()-2);
		}else if(inputPolicyNum.length()==15) {
			resultPolicyNum = inputPolicyNum.substring(inputPolicyNum.length()-10,inputPolicyNum.length()-2);
		}
		data.addToLog("policyNumForFarmers", resultPolicyNum);
		return resultPolicyNum;
		
	}
	
	private String policyNumForForemost(String inputPolicyNum, String strBU,  DecisionElementData data) {
		String resultPolicyNum = inputPolicyNum;
		
		return resultPolicyNum;
	}
	
	private String policyNumForFWS(String inputPolicyNum, String strBU,  DecisionElementData data) {
		data.addToLog("policyNumForFWS", inputPolicyNum);
		String resultPolicyNum = inputPolicyNum;
		if(inputPolicyNum.length() >= 9 && inputPolicyNum.length() <= 11) {
			if(inputPolicyNum.contains(Constants.P_A)) 
				resultPolicyNum=inputPolicyNum.replaceFirst(Constants.P_A, Constants.EmptyString);
			else if(inputPolicyNum.contains(Constants.P_H)) 
				resultPolicyNum=inputPolicyNum.replaceFirst(Constants.P_H, Constants.EmptyString);
			else if(inputPolicyNum.contains("h"))
				resultPolicyNum=inputPolicyNum.replaceFirst("h", Constants.EmptyString);
			else if(inputPolicyNum.contains("a"))
				resultPolicyNum=inputPolicyNum.replaceFirst("a", Constants.EmptyString);
		}
		data.addToLog("policyNumForFWS", resultPolicyNum);
		return resultPolicyNum;
	}
	
	private String policyNumFor21st(String inputPolicyNum, String strBU,  DecisionElementData data) {
		data.addToLog("policyNumFor21st", inputPolicyNum);
		
		String resultPolicyNum = inputPolicyNum;
		if(inputPolicyNum.length() >= 8 && inputPolicyNum.length() <= 12) {
			if(inputPolicyNum.contains("T0")) 
				resultPolicyNum=inputPolicyNum.replaceFirst("T0", Constants.EmptyString);
			if(inputPolicyNum.contains("t0")) {
				resultPolicyNum=inputPolicyNum.replaceFirst("t0", Constants.EmptyString);
			}
			
			if(resultPolicyNum.length()<=8) {
				resultPolicyNum=resultPolicyNum.substring(0, 8);
			}
				
		}
		
		return resultPolicyNum;
	}

	public static void main(String[] args) {
		
		String inputPolicyNum = "T0998949100",resultPolicyNum=null;
		resultPolicyNum = inputPolicyNum;
		if(inputPolicyNum.length() >= 8 && inputPolicyNum.length() <= 12) {
			if(inputPolicyNum.contains("T0")) 
				resultPolicyNum=inputPolicyNum.replaceFirst("T0", Constants.EmptyString);
			
			if(resultPolicyNum.length()>=8) {
				resultPolicyNum=resultPolicyNum.substring(0, 8);
			}
				
		}
		System.err.println(resultPolicyNum);
	}
}
