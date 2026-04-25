package com.farmers.shared.bc;

import java.util.HashMap;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.bean.PolicyBean;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class GET_SIDA_MN_004 extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException 
	{
	
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {

			String returnValue = (String) data.getElementData("SIDA_MN_004_Call","Return_Value");
			data.addToLog(currElementName, "Menu ID : "+"SIDA_MN_004"+" :: Menu Value : "+returnValue);
			data.setSessionData(Constants.SIDA_MN_004_VALUE, returnValue);
			
			if(returnValue.equalsIgnoreCase(Constants.NOINPUT)) strExitState = Constants.NOINPUT;
			else if(returnValue.equalsIgnoreCase(Constants.NOMATCH)) strExitState = Constants.NOMATCH;
			else if (returnValue.equalsIgnoreCase(Constants.SOMETHINGELSE)) strExitState = Constants.SOMETHINGELSE;
			else if(returnValue.equalsIgnoreCase(Constants.ONE)) {
				strExitState = Constants.ONE;
				setDataIntoSession(Constants.ONE, data);
			}else if(returnValue.equalsIgnoreCase(Constants.TWO)) {
				strExitState = Constants.TWO;
				setDataIntoSession(Constants.TWO, data);
			}else if(returnValue.equalsIgnoreCase(Constants.THREE)) {
				strExitState = Constants.THREE;
				setDataIntoSession(Constants.THREE, data);
			}else if(returnValue.equalsIgnoreCase(Constants.FOUR)) {
				strExitState = Constants.FOUR;
				setDataIntoSession(Constants.FOUR, data);
			}else if(returnValue.equalsIgnoreCase(Constants.FIVE)) {
				strExitState = Constants.FIVE;
				setDataIntoSession(Constants.FIVE, data);
			}
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in SIDA_MN_004 :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"SIDA_MN_004 :: "+strExitState);
		return strExitState;
	
	}
	
	@SuppressWarnings("unchecked")
	private void setDataIntoSession(String index, DecisionElementData data) {
		try {
			HashMap<String, PolicyBean> policyDetailsMap = (HashMap<String, PolicyBean>) data.getSessionData(Constants.POLICY_DETAILS_FOR_MN3);
			int i = 0;
			switch (index) {
			case Constants.ONE:
				i=1;
				break;
			case Constants.TWO:
				i=2;
				break;
			case Constants.THREE:
				i=3;
				break;
			case Constants.FOUR:
				i=4;
				break;
			case Constants.FIVE:
				i=5;
				break;
			default:
				break;
			}
			int j = 0;

			for (String key1 : policyDetailsMap.keySet()) {
				j=j+1;
				if(i != j) continue;
				PolicyBean obj = (PolicyBean) policyDetailsMap.get(key1);
				data.setSessionData(Constants.S_API_DOB, obj.getStrDOB());
				data.setSessionData(Constants.S_API_ZIP, obj.getStrZipCode());
				data.setSessionData("DOB_CHECK","YES");
				data.setSessionData("ZIP_CHECK","YES");
				
				data.setSessionData(Constants.S_POLICY_NUM, key1);
				data.setSessionData(Constants.S_POLICY_STATE_CODE, obj.getStrPolicyState());
				data.setSessionData(Constants.S_POLICY_SOURCE, obj.getStrPolicySource());
				data.setSessionData(Constants.S_FINAL_POLICY_OBJ, obj);
				if(null != data.getSessionData("S_FLAG_FOREMOST_BU") && Constants.STRING_YES.equals(data.getSessionData("S_FLAG_FOREMOST_BU"))) {
					data.setSessionData("S_EPC_PAYMENT_POLICYNUM", obj.getStrPolicyProductCode()+key1);
				} else if(null != data.getSessionData("S_FLAG_FWS_BU") && Constants.STRING_YES.equals(data.getSessionData("S_FLAG_FWS_BU"))) {
					data.setSessionData("S_EPC_PAYMENT_POLICYNUM", obj.getStrBillingAccountNumber());
				}
				 data.setSessionData("S_FDS_GPC", obj.getGpcCode());
				 data.setSessionData("S_FDS_PAYPLAN", obj.getPayPlan());
				 data.setSessionData("S_FDS_SERVICING_PHONE_NUMBER", obj.getServicingPhoneNumber());
			     data.addToLog(data.getCurrentElement(), "GPC code for FDS PayrollDeduct ::"+data.getSessionData("S_FDS_GPC"));
			     data.addToLog(data.getCurrentElement(), "Pay Plan for FDS PayrollDeduct ::"+data.getSessionData("S_FDS_PAYPLAN"));
			     data.addToLog(data.getCurrentElement(), "Servicing Phone Number for FDS PayrollDeduct ::"+data.getSessionData("S_FDS_SERVICING_PHONE_NUMBER"));
			     
			   //START : CS1265959 : Farmers Insurance | US | FDS - Payroll Deduct indicator & routing                 
					if (obj.getPayPlan().contains("payPlan") &&(obj.getPayPlan()!= null )) {
						if("PB".equalsIgnoreCase(obj.getPayPlan())||"PA".equalsIgnoreCase(obj.getPayPlan())) {
						data.addToLog(data.getCurrentElement(), "S_API_PAYPLAN : " + obj.getPayPlan());
						//Start CS1175332-Farmers Direct Services (FDS/Personal Lines) - Billing & Payments - CA Payroll Deduct
						data.setSessionData("S_FDS_PAYROLL_DEDUCT", "Y");
						//End CS1175332-Farmers Direct Services (FDS/Personal Lines) - Billing & Payments - CA Payroll Deduct
					    data.setSessionData(Constants.S_POLICY_ATTRIBUTES, "Payroll Deduct");
					    data.addToLog(data.getCurrentElement(), "POLICY ATTRIBUTES : " + data.getSessionData(Constants.S_POLICY_ATTRIBUTES));
						}
					}
					// END : CS1265959 : Farmers Insurance | US | FDS - Payroll Deduct  indicator & routing
			}
			//DOB BYPASS CHANGE
			if(null==data.getSessionData(Constants.S_API_DOB) || data.getSessionData(Constants.S_API_DOB).toString().isEmpty()) {
				data.setSessionData("DOB_CHECK", "NO");
				data.addToLog("DOB_CHECK", data.getSessionData("DOB_CHECK").toString());
			}
			if(null==data.getSessionData(Constants.S_API_ZIP) || data.getSessionData(Constants.S_API_ZIP).toString().isEmpty()) {
				data.setSessionData("ZIP_CHECK", "NO");
				data.addToLog("ZIP_CHECK", data.getSessionData("ZIP_CHECK").toString());
			}			
		//DOB BYPASS CHANGE
		} catch (Exception e) {
			data.addToLog(data.getCurrentElement(),"Exception in SIDA_MN_004 :: "+e);
		}
		
	}
}
