package com.farmers.shared.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class GET_SPCQ_MN_001 extends DecisionElementBase 
{

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException 
	{

		String strExitState = Constants.ER;
		String strPolicyQuestion = Constants.MSP_POLICY_QUESTIONS;
		String strPolicyChange = Constants.MSP_COVERAGE_CHANGE;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {

			String mspValue = "";
			String returnValue = (String) data.getElementData("SPCQ_MN_001_Call","Return_Value");
			data.addToLog(currElementName, "Menu ID : "+"SPCQ_MN_001"+" :: Menu Value : "+returnValue);
			data.setSessionData(Constants.SPCQ_MN_001_VALUE, returnValue);
			data.addToLog(currElementName, "Value of SPCQ_MN_001 Retrurn Value : "+returnValue);
			if(returnValue != null && !returnValue.equals("")) 
			{
				data.addToLog(currElementName, "Value of SPCQ_MN_001 Retrurn Value in First IF :"+returnValue);
				if(returnValue.equalsIgnoreCase(Constants.ADDVECHILE)){
					strExitState = Constants.ADDVECHILE;
					mspValue = Constants.MSP_ADD_VEHICLE;
					caa.createMSPKey(caa, data, "SPCQ_MN_001", mspValue);
					data.addToLog(currElementName, "strExitState " + strExitState + "mspValue" + mspValue);
				}else if(returnValue.equalsIgnoreCase(Constants.ADDREMOVEVECHILE)){
					strExitState = Constants.ADDREMOVEVECHILE;
					mspValue = Constants.MSP_ADD_VEHICLE;
					caa.createMSPKey(caa, data, "SPCQ_MN_001", mspValue);
					data.addToLog(currElementName, "strExitState " + strExitState + "mspValue" + mspValue);
				}else if(returnValue.equalsIgnoreCase(Constants.REMOVEVECHILE)){
					strExitState = Constants.REMOVEVECHILE;
					mspValue = Constants.MSP_REMOVE_VEHICLE;
					caa.createMSPKey(caa, data, "SPCQ_MN_001", mspValue);
					data.addToLog(currElementName, "strExitState " + strExitState + "mspValue" + mspValue);
				}else if(returnValue.equalsIgnoreCase(Constants.MAKECOVERAGE)){
					strExitState = Constants.MAKECOVERAGE;
					mspValue = Constants.MSP_COVERAGE_CHANGE;
					caa.createMSPKey(caa, data, "SPCQ_MN_001", mspValue);
					data.addToLog(currElementName, "strExitState " + strExitState + "mspValue" + mspValue);
				}else if(returnValue.equalsIgnoreCase(Constants.GENERALPOLICY)){
					strExitState = Constants.GENERALPOLICY;
					mspValue = Constants.MSP_POLICY_QUESTIONS;
					caa.createMSPKey(caa, data, "SPCQ_MN_001", mspValue);
					data.addToLog(currElementName, "strExitState " + strExitState + "mspValue" + mspValue);
				}else if(returnValue.equalsIgnoreCase("Representative")) {
					strExitState = "Representative";
					mspValue = "REPRESENTATIVE";
					caa.createMSPKey(caa, data, "SPCQ_MN_001", mspValue);
					data.addToLog(currElementName, "strExitState " + strExitState + "mspValue" + mspValue);
				}
				else if(returnValue.equalsIgnoreCase(Constants.NOINPUT)) {	
					strExitState = Constants.NOINPUT;
					data.addToLog(currElementName, "strExitState " + strExitState + "mspValue" + mspValue);
				}else if(returnValue.equalsIgnoreCase(Constants.NOMATCH)) {
					strExitState = Constants.NOMATCH;
					mspValue = Constants.NOMATCH;
					data.addToLog(currElementName, "strExitState " + strExitState + "mspValue" + mspValue);
				}
				
				//NEWLY ADDED
				else if(returnValue.equalsIgnoreCase("Policy Auto Change")) {
					strExitState = "Policy Auto Change";
					caa.createMSPKey(caa, data, "SPCQ_MN_001", strPolicyChange);
					data.addToLog(currElementName, "strExitState " + strExitState + "mspValue" + strPolicyChange);
				}else if(returnValue.equalsIgnoreCase("Policy Auto Change Vehicle")) {
					strExitState = "Policy Auto Change Vehicle";
					caa.createMSPKey(caa, data, "SPCQ_MN_001", strPolicyChange);
					data.addToLog(currElementName, "strExitState " + strExitState + "mspValue" + strPolicyChange);
				}else if(returnValue.equalsIgnoreCase("Policy Auto Renew")) {
					strExitState = "Policy Auto Renew";
					caa.createMSPKey(caa, data, "SPCQ_MN_001", strPolicyChange);
					data.addToLog(currElementName, "strExitState " + strExitState + "mspValue" + strPolicyChange);
				}else if(returnValue.equalsIgnoreCase("Policy Cancellation Query")) {
					strExitState = "Policy Cancellation Query";
					caa.createMSPKey(caa, data, "SPCQ_MN_001", strPolicyQuestion);
					data.addToLog(currElementName, "strExitState " + strExitState + "mspValue" + strPolicyQuestion);
				}else if(returnValue.equalsIgnoreCase("Policy Client Info Change")) {
					strExitState = "Policy Client Info Change";
					caa.createMSPKey(caa, data, "SPCQ_MN_001", strPolicyQuestion);
					data.addToLog(currElementName, "strExitState " + strExitState + "mspValue" + strPolicyQuestion);
				}else if(returnValue.equalsIgnoreCase("Policy Coverage Change")) {
					strExitState = "Policy Coverage Change";
					caa.createMSPKey(caa, data, "SPCQ_MN_001", strPolicyChange);
					data.addToLog(currElementName, "strExitState " + strExitState + "mspValue" + strPolicyChange);
				}else if(returnValue.equalsIgnoreCase("Policy Effective Date Change")) {
					strExitState = "Policy Effective Date Change";
					caa.createMSPKey(caa, data, "SPCQ_MN_001", strPolicyChange);
					data.addToLog(currElementName, "strExitState " + strExitState + "mspValue" + strPolicyChange);
				}else if(returnValue.equalsIgnoreCase("Policy Homeowners Renew")) {
					strExitState = "Policy Homeowners Renew";
					caa.createMSPKey(caa, data, "SPCQ_MN_001", strPolicyChange);
					data.addToLog(currElementName, "strExitState " + strExitState + "mspValue" + strPolicyChange);
				}else if(returnValue.equalsIgnoreCase("Policy Homeowners Change")) {
					strExitState = "Policy Homeowners Change";
					caa.createMSPKey(caa, data, "SPCQ_MN_001", strPolicyChange);
					data.addToLog(currElementName, "strExitState " + strExitState + "mspValue" + strPolicyChange);
				}else if(returnValue.equalsIgnoreCase("Policy Mortgagee Change")) {
					strExitState = "Policy Mortgagee Change";
					caa.createMSPKey(caa, data, "SPCQ_MN_001", strPolicyChange);
					data.addToLog(currElementName, "strExitState " + strExitState + "mspValue" + strPolicyChange);
				}else if(returnValue.equalsIgnoreCase("Policy Umbrella Vague")) {
					strExitState = "Policy Umbrella Vague";
					caa.createMSPKey(caa, data, "SPCQ_MN_001", strPolicyChange);
					data.addToLog(currElementName, "strExitState " + strExitState + "mspValue" + strPolicyChange);
				}else if(returnValue.equalsIgnoreCase("Policy Add")) {
					strExitState = "Policy Add";
					caa.createMSPKey(caa, data, "SPCQ_MN_001", strPolicyChange);
					data.addToLog(currElementName, "strExitState " + strExitState + "mspValue" + strPolicyChange);
				}else if(returnValue.equalsIgnoreCase("Policy Add Remove Person")) {
					strExitState = "Policy Add Remove Person";
					caa.createMSPKey(caa, data, "SPCQ_MN_001", strPolicyChange);
					data.addToLog(currElementName, "strExitState " + strExitState + "mspValue" + strPolicyChange);
				}else if(returnValue.equalsIgnoreCase("Policy Billing Query")) {
					strExitState = "Policy Billing Query";
					caa.createMSPKey(caa, data, "SPCQ_MN_001", strPolicyQuestion);
					data.addToLog(currElementName, "strExitState " + strExitState + "mspValue" + strPolicyQuestion);
				}else if(returnValue.equalsIgnoreCase("Policy Cancel")) {
					strExitState = "Policy Cancel";
					caa.createMSPKey(caa, data, "SPCQ_MN_001", strPolicyChange);
					data.addToLog(currElementName, "strExitState " + strExitState + "mspValue" + strPolicyChange);
				}else if(returnValue.equalsIgnoreCase("Policy Relate Household")) {
					strExitState = "Policy Relate Household";
					caa.createMSPKey(caa, data, "SPCQ_MN_001", strPolicyChange);
					data.addToLog(currElementName, "strExitState " + strExitState + "mspValue" + strPolicyChange);
				}else if(returnValue.equalsIgnoreCase("Policy Renew")) {
					strExitState = "Policy Renew";
					caa.createMSPKey(caa, data, "SPCQ_MN_001", strPolicyChange);
					data.addToLog(currElementName, "strExitState " + strExitState + "mspValue" + strPolicyChange);
				}else if(returnValue.equalsIgnoreCase("Policy Transfer")) {
					strExitState = "Policy Transfer";
					caa.createMSPKey(caa, data, "SPCQ_MN_001", strPolicyChange);
					data.addToLog(currElementName, "strExitState " + strExitState + "mspValue" + strPolicyChange);
				}else if(returnValue.equalsIgnoreCase("Policy Information Verify")) {
					strExitState = "Policy Information Verify";
					caa.createMSPKey(caa, data, "SPCQ_MN_001", strPolicyQuestion);
					data.addToLog(currElementName, "strExitState " + strExitState + "mspValue" + strPolicyQuestion);
				}else if(returnValue.equalsIgnoreCase("Representative Request")) {
					strExitState = "Representative";
					mspValue = "REPRESENTATIVE";
					caa.createMSPKey(caa, data, "SPCQ_MN_001", mspValue);
					data.addToLog(currElementName, "strExitState " + strExitState + "mspValue" + mspValue);
				}
			}
			else {	
				strExitState = Constants.ER;
				data.addToLog(currElementName, "Value of SPCQ_MN_001 Retrurn Value in Last ELSE :"+returnValue);
			}

			data.addToLog(currElementName, "Final Value of returnValue : " + returnValue + "Final Value of mspValue : " + mspValue + " strExitState : " + strExitState);

			

		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in SPCQ_MN_001 :: "+e);
			caa.printStackTrace(e);
		}
		return strExitState;

	}

}

