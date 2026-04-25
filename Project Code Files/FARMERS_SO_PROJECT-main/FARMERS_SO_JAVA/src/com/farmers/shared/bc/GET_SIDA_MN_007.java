package com.farmers.shared.bc;

import java.util.HashMap;
import java.util.Map;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.bean.PolicyBean;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class GET_SIDA_MN_007 extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			String returnValue = (String) data.getElementData("SIDA_MN_007_Call","Return_Value");
			data.addToLog(currElementName,"GET_SIDA_MN_007 Zip code Value : "+returnValue);
			data.setSessionData(Constants.SIDA_MN_007_VALUE, returnValue);
			
			if(returnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			} else if(returnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
			} else if (returnValue.equalsIgnoreCase(Constants.SOMETHINGELSE)) {
				strExitState = Constants.SOMETHINGELSE;
			} else if(returnValue.toLowerCase().contains("auto policy") || returnValue.toLowerCase().contains("auto")) {
				strExitState = productCountStatus(Constants.AUTO_PRODUCTTYPECOUNT_KYC, data, caa);
				data.setSessionData("ID_AUTH_SELECTED_PRODUCTTYPE", "A");
				data.setSessionData("SIDA_RETENTION_PRODUCTYPE", "A");
			} else if((returnValue.toLowerCase().contains("home policy") || returnValue.toLowerCase().contains("home")) && !returnValue.toLowerCase().contains("motor") && !returnValue.toLowerCase().contains("mobile")) {
				strExitState = productCountStatus(Constants.HOME_PRODUCTTYPECOUNT_KYC, data, caa);
				data.setSessionData("ID_AUTH_SELECTED_PRODUCTTYPE", "H");
				data.setSessionData("SIDA_RETENTION_PRODUCTYPE", "H");
			} else if(returnValue.toLowerCase().contains("boat policy") || returnValue.toLowerCase().contains("boat")) {
				strExitState = productCountStatus(Constants.MR_PRODUCTTYPECOUNT_KYC, data, caa);
				data.setSessionData("ID_AUTH_SELECTED_PRODUCTTYPE", "Y");
				data.setSessionData("SIDA_RETENTION_PRODUCTYPE", "Y");
			} else if(returnValue.toLowerCase().contains("motorcycle policy") || returnValue.toLowerCase().contains("motorcycle")) {
				strExitState = productCountStatus(Constants.RV_PRODUCTTYPECOUNT_KYC, data, caa);
				data.setSessionData("ID_AUTH_SELECTED_PRODUCTTYPE", "RV");
			} else if(returnValue.toLowerCase().contains("motorhome policy") || returnValue.toLowerCase().contains("motorhome")) {
				strExitState = productCountStatus(Constants.RV_PRODUCTTYPECOUNT_KYC, data, caa);
				data.setSessionData("ID_AUTH_SELECTED_PRODUCTTYPE", "RV");
			} else if(returnValue.toLowerCase().contains("specialtydwelling policy") || returnValue.toLowerCase().contains("specialtydwelling") || returnValue.toLowerCase().contains("specialty dwelling")) {
				strExitState = productCountStatus(Constants.SP_PRODUCTTYPECOUNT_KYC, data, caa);
				data.setSessionData("ID_AUTH_SELECTED_PRODUCTTYPE", "SP");
			} else if(returnValue.toLowerCase().contains("mobilehome policy") || returnValue.toLowerCase().contains("mobile home")) {
				strExitState = productCountStatus(Constants.SP_PRODUCTTYPECOUNT_KYC, data, caa);
				data.setSessionData("ID_AUTH_SELECTED_PRODUCTTYPE", "SP");
			} else if(returnValue.toLowerCase().contains("umbrella policy") || returnValue.toLowerCase().contains("umbrella")) {
				strExitState = productCountStatus(Constants.UMBRELLA_PRODUCTTYPECOUNT_KYC, data, caa);
				data.setSessionData("ID_AUTH_SELECTED_PRODUCTTYPE", "U");
				data.setSessionData("SIDA_RETENTION_PRODUCTYPE", "U");

			}
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in GET_SIDA_MN_007 :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"GET_SIDA_MN_007 :: "+strExitState);
		return strExitState;
	
	}
	
	@SuppressWarnings("unchecked")
	private String productCountStatus(String produtType, DecisionElementData data, CommonAPIAccess caa) {
		String strExitState = Constants.ER;
		String prompt = Constants.EmptyString;
		try {
			int productCount = (int)data.getSessionData(produtType);
			if(productCount == 1) strExitState = Constants.ValidSinglePolicy;
			else strExitState = Constants.ValidMultiplePolicy;
			
			if(strExitState.equalsIgnoreCase(Constants.ValidSinglePolicy)) {
				HashMap<String, HashMap<String, PolicyBean>> productPolicyMap = (HashMap<String, HashMap<String, PolicyBean>>) data.getSessionData(Constants.S_POLICYDETAILS_MAP);
				HashMap<String, PolicyBean> policyDetailsMap = productPolicyMap.get(produtType);
				for (String key1 : policyDetailsMap.keySet()) {
					PolicyBean obj = (PolicyBean) policyDetailsMap.get(key1);
					data.setSessionData(Constants.S_API_DOB, obj.getStrDOB());
					data.setSessionData(Constants.S_API_ZIP, obj.getStrZipCode()); 
					data.addToLog("DOB: ", obj.getStrDOB());
					data.addToLog("ZIP: ", obj.getStrZipCode());
					data.addToLog("DOB in session after setting:", (String) data.getSessionData(Constants.S_API_DOB)); // Log after setting 
					data.addToLog("ZIP in session after setting:", (String) data.getSessionData(Constants.S_API_ZIP)); // Log after setting 
					//DOB BYPASS CHANGE
					data.setSessionData("DOB_CHECK","YES");
					data.setSessionData("ZIP_CHECK","YES");
					
					data.setSessionData(Constants.S_POLICY_NUM, key1);
					data.setSessionData(Constants.S_POLICY_SOURCE, obj.getStrPolicySource());
					data.setSessionData(Constants.S_POLICY_STATE_CODE, obj.getStrPolicyState());
					data.setSessionData(Constants.S_FINAL_POLICY_OBJ, obj);
					data.setSessionData("S_LOB", (String) obj.getStrPolicyLOB());
					if(null != data.getSessionData("S_FLAG_FOREMOST_BU") && Constants.STRING_YES.equals(data.getSessionData("S_FLAG_FOREMOST_BU"))) {
						data.setSessionData("S_EPC_PAYMENT_POLICYNUM", obj.getStrPolicyProductCode()+key1);
					} else if(null != data.getSessionData("S_FLAG_FWS_BU") && Constants.STRING_YES.equals(data.getSessionData("S_FLAG_FWS_BU"))) {
						data.setSessionData("S_EPC_PAYMENT_POLICYNUM", obj.getStrBillingAccountNumber());
					}
					//Start - CS1175332-Farmers Direct Services (FDS/Personal Lines) - Billing & Payments - CA Payroll Deduct
					 data.setSessionData("S_FDS_GPC", obj.getGpcCode());
					 data.setSessionData("S_FDS_PAYPLAN", obj.getPayPlan());
					 data.setSessionData("S_FDS_SERVICING_PHONE_NUMBER", obj.getServicingPhoneNumber());
				     data.addToLog(data.getCurrentElement(), "GPC code for FDS PayrollDeduct ::"+data.getSessionData("S_FDS_GPC"));
				     data.addToLog(data.getCurrentElement(), "Pay Plan for FDS PayrollDeduct ::"+data.getSessionData("S_FDS_PAYPLAN"));
				     data.addToLog(data.getCurrentElement(), "Servicing Phone Number for FDS PayrollDeduct ::"+data.getSessionData("S_FDS_SERVICING_PHONE_NUMBER"));
				     //End CS1175332-Farmers Direct Services (FDS/Personal Lines) - Billing & Payments - CA Payroll Deduct
				     
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
			} else if(strExitState.equalsIgnoreCase(Constants.ValidMultiplePolicy)) {
				if(Constants.AUTO_PRODUCTTYPECOUNT_KYC.equalsIgnoreCase(produtType)) {
					prompt = "auto"; 
					data.setSessionData("S_LOB", "A");
				}
				else if (Constants.HOME_PRODUCTTYPECOUNT_KYC.equalsIgnoreCase(produtType)) {
					prompt = "home"; 
					data.setSessionData("S_LOB", "H");
				}
				else if (Constants.UMBRELLA_PRODUCTTYPECOUNT_KYC.equalsIgnoreCase(produtType)) {
					prompt = "umbrella";
					data.setSessionData("S_LOB", "U");
				}
				else if (Constants.MR_PRODUCTTYPECOUNT_KYC.equalsIgnoreCase(produtType)) {
					prompt = "boat";
					data.setSessionData("S_LOB", "B");
				}
				else if (Constants.RV_PRODUCTTYPECOUNT_KYC.equalsIgnoreCase(produtType)) {
					prompt = "motorcycle";
					data.setSessionData("S_LOB", "M");
				}
				else if (Constants.SP_PRODUCTTYPECOUNT_KYC.equalsIgnoreCase(produtType)) {
					prompt = "specialty dwelling";
					data.setSessionData("S_LOB", "SP");
				}
				
				/*
				if (prompt.contains(" ")) {
					prompt = prompt.replaceAll(" ", ".");
				} 
				*/
				data.setSessionData(Constants.VXMLParam1, prompt);
				data.setSessionData(Constants.VXMLParam2, "NA");
				data.setSessionData(Constants.VXMLParam3, "NA");
				data.setSessionData(Constants.VXMLParam4, "NA");
				data.addToLog(data.getCurrentElement(),"Policies Dynamic Product Type Prompts = "+prompt.toString());
				data.addToLog(data.getCurrentElement(),"VXMLPARAM1 = "+data.getSessionData(Constants.VXMLParam1));
				
				HashMap<String, HashMap<String, PolicyBean>> productPolicyMap = (HashMap<String, HashMap<String, PolicyBean>>) data.getSessionData(Constants.S_POLICYDETAILS_MAP);
				HashMap<String, PolicyBean> policyDetailsMap = productPolicyMap.get(produtType);
				data.setSessionData(Constants.POLICY_DETAILS_FOR_MN3, policyDetailsMap);
			}
		} catch (Exception e) {
			data.addToLog(data.getCurrentElement(),"Exception in GET_SIDA_MN_007 productCountStatus() :: "+e);
			caa.printStackTrace(e);
		}
		
		return strExitState;
	}
	
}
