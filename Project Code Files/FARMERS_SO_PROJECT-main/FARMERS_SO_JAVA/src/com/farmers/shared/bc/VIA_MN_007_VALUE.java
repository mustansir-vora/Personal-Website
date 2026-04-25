package com.farmers.shared.bc;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.bean.PolicyBean;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class VIA_MN_007_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			String returnValue = (String) data.getElementData("VIA_MN_007_Call","Return_Value");
			String returnValue2="";
			if (returnValue != null && returnValue.toLowerCase().contains("your")) {
			    // Remove standalone "your" (case-insensitive)
			    returnValue = returnValue.replaceAll("(?i)\\byour\\b", "");
			    
			    // Replace multiple spaces with a single space
			    returnValue = returnValue.replaceAll("\\s{2,}", " ").trim();
			}
			
			data.addToLog(currElementName,"VIA_MN_007 Value : "+returnValue);
			data.setSessionData("VIA_MN_007_VALUE", returnValue);
		//check if policy collection flag in enabled
			if(null!=data.getSessionData("Policy_Collection") && !"".equalsIgnoreCase((String)data.getSessionData("Policy_Collection"))
					&& "TRUE".equalsIgnoreCase((String)data.getSessionData("Policy_Collection"))) {
				data.addToLog("Policy collection flag is ", (String)data.getSessionData("Policy_Collection"));
				data.addToLog("Hence entering into if condition", "");
				if(returnValue.equalsIgnoreCase(Constants.NOINPUT)) {
					strExitState = Constants.NOINPUT;
				} else if(returnValue.equalsIgnoreCase(Constants.NOMATCH)) {
					strExitState = Constants.NOMATCH;
				} else if (returnValue.equalsIgnoreCase(Constants.SOMETHINGELSE)) {
					strExitState = Constants.SOMETHINGELSE;
				}else if(returnValue.equalsIgnoreCase("None of these")) {
					strExitState = Constants.SOMETHINGELSE;
				}else if(returnValue.equalsIgnoreCase(Constants.REPRESENTATIVE)) {
					data.setSessionData("LAST_MENU_NAME_REP", "VIA_MN_007");
					strExitState = Constants.REPRESENTATIVE;
				}   else if(returnValue.toLowerCase().contains("auto policy") || returnValue.toLowerCase().contains("auto")) {
					strExitState = productCountStatusPolicy(Constants.AUTO_PRODUCTTYPECOUNT_KYC, data, caa);
					data.setSessionData("ID_AUTH_SELECTED_PRODUCTTYPE", "A");
					data.setSessionData("SIDA_RETENTION_PRODUCTYPE", "A");
				} else if((returnValue.toLowerCase().contains("home policy") || returnValue.toLowerCase().contains("home")) && !returnValue.toLowerCase().contains("motor") && !returnValue.toLowerCase().contains("mobile")) {
					strExitState = productCountStatusPolicy(Constants.HOME_PRODUCTTYPECOUNT_KYC, data, caa);
					data.setSessionData("ID_AUTH_SELECTED_PRODUCTTYPE", "H");
					data.setSessionData("SIDA_RETENTION_PRODUCTYPE", "H");
				} else if(returnValue.toLowerCase().contains("boat policy") || returnValue.toLowerCase().contains("boat")) {
					strExitState = productCountStatusPolicy(Constants.MR_PRODUCTTYPECOUNT_KYC, data, caa);
					data.setSessionData("ID_AUTH_SELECTED_PRODUCTTYPE", "Y");
					data.setSessionData("SIDA_RETENTION_PRODUCTYPE", "Y");
				} else if(returnValue.toLowerCase().contains("motorcycle policy") || returnValue.toLowerCase().contains("motorcycle")) {
					strExitState = productCountStatusPolicy(Constants.RV_PRODUCTTYPECOUNT_KYC, data, caa);
					data.setSessionData("ID_AUTH_SELECTED_PRODUCTTYPE", "RV");
				} else if(returnValue.toLowerCase().contains("motorhome policy") || returnValue.toLowerCase().contains("motorhome")||returnValue.toLowerCase().contains("motor home policy") || returnValue.toLowerCase().contains("motor home")) {
					strExitState = productCountStatusPolicy(Constants.RV_PRODUCTTYPECOUNT_KYC, data, caa);
					data.setSessionData("ID_AUTH_SELECTED_PRODUCTTYPE", "RV");
				}else if(returnValue.toLowerCase().contains("a  specialty  dwelling  or  mobile  home  policy") || returnValue.toLowerCase().contains("specialty  dwelling  or  mobile  home  policy")) {
					strExitState = productCountStatusPolicy(Constants.SP_PRODUCTTYPECOUNT_KYC, data, caa);
					data.setSessionData("ID_AUTH_SELECTED_PRODUCTTYPE", "SP");
				}else if(returnValue.toLowerCase().contains("specialtydwelling policy") || returnValue.toLowerCase().contains("specialtydwelling") || returnValue.toLowerCase().contains("specialty dwelling")) {
					strExitState = productCountStatusPolicy(Constants.SP_PRODUCTTYPECOUNT_KYC, data, caa);
					data.setSessionData("ID_AUTH_SELECTED_PRODUCTTYPE", "SP");
				} else if(returnValue.toLowerCase().contains("mobilehome policy") || returnValue.toLowerCase().contains("mobile home")) {
					strExitState = productCountStatusPolicy(Constants.SP_PRODUCTTYPECOUNT_KYC, data, caa);
					data.setSessionData("ID_AUTH_SELECTED_PRODUCTTYPE", "SP");
				} else if(returnValue.toLowerCase().contains("umbrella policy") || returnValue.toLowerCase().contains("umbrella")) {
					strExitState = productCountStatusPolicy(Constants.UMBRELLA_PRODUCTTYPECOUNT_KYC, data, caa);
					data.setSessionData("ID_AUTH_SELECTED_PRODUCTTYPE", "U");
					data.setSessionData("SIDA_RETENTION_PRODUCTYPE", "U");
				}
			}else{			
				data.addToLog("Hence entering into else condition", "");
				if(returnValue.equalsIgnoreCase(Constants.NOINPUT)) {
					strExitState = Constants.NOINPUT;
				} else if(returnValue.equalsIgnoreCase(Constants.NOMATCH)) {
					strExitState = Constants.NOMATCH;
				} else if (returnValue.equalsIgnoreCase(Constants.SOMETHINGELSE)) {
					strExitState = Constants.SOMETHINGELSE;
				}else if(returnValue.equalsIgnoreCase("None of these")) {
					strExitState = Constants.SOMETHINGELSE;
				}else if(returnValue.equalsIgnoreCase(Constants.REPRESENTATIVE)) {
					data.setSessionData("LAST_MENU_NAME_REP", "VIA_MN_007");
					strExitState = Constants.REPRESENTATIVE;
				}else if(returnValue.toLowerCase().contains("auto policy") || returnValue.toLowerCase().contains("auto")) {
					strExitState = productCountStatus(Constants.AUTO_PRODUCTTYPECOUNT_KYC, data, caa);
					data.setSessionData("ID_AUTH_SELECTED_PRODUCTTYPE", "A");
					data.setSessionData("SIDA_RETENTION_PRODUCTYPE", "A");
				}else if((returnValue.toLowerCase().contains("home policy") || returnValue.toLowerCase().contains("home")) && !returnValue.toLowerCase().contains("motor") && !returnValue.toLowerCase().contains("mobile") && !returnValue.toLowerCase().contains("specialty")
						&& !returnValue.toLowerCase().contains("owners") && !returnValue.toLowerCase().contains("manufactured")) {
					strExitState = productCountStatus(Constants.HOME_PRODUCTTYPECOUNT_KYC, data, caa);
					data.setSessionData("ID_AUTH_SELECTED_PRODUCTTYPE", "H");
					data.setSessionData("SIDA_RETENTION_PRODUCTYPE", "H");
				}else if(returnValue.toLowerCase().contains("marine Watercraft policy") || returnValue.toLowerCase().contains("marine Watercraft") || returnValue.toLowerCase().contains("marine") || returnValue.toLowerCase().contains("watercraft")) {
					strExitState = productCountStatus(Constants.MR_PRODUCTTYPECOUNT_KYC, data, caa);
					data.setSessionData("ID_AUTH_SELECTED_PRODUCTTYPE", "Y");
					data.setSessionData("SIDA_RETENTION_PRODUCTYPE", "Y");
				}else if(returnValue.toLowerCase().contains("motorcycle policy") || returnValue.toLowerCase().contains("motorcycle")) {
					//strExitState = productCountStatus(Constants.RV_PRODUCTTYPECOUNT_KYC, data, caa);
					strExitState = productCountStatus(Constants.MOTORCYCLE_PRODUCTTYPECOUNT_KYC, data, caa);
					data.setSessionData("ID_AUTH_SELECTED_PRODUCTTYPE", "RV");
				}else if(returnValue.toLowerCase().contains("motorhome policy") || returnValue.toLowerCase().contains("motorhome")||returnValue.toLowerCase().contains("motor home policy") || returnValue.toLowerCase().contains("motor home")) {
					strExitState = productCountStatus(Constants.MOTORHOME_PRODUCTTYPECOUNT_KYC, data, caa);
					data.setSessionData("ID_AUTH_SELECTED_PRODUCTTYPE", "RV");
				}else if(returnValue.toLowerCase().contains("travel trailer policy") || returnValue.toLowerCase().contains("travel")||returnValue.toLowerCase().contains("trailer policy") || returnValue.toLowerCase().contains("travel trailer")) {
					strExitState = productCountStatus(Constants.TRAVEL_PRODUCTTYPECOUNT_KYC, data, caa);
					data.setSessionData("ID_AUTH_SELECTED_PRODUCTTYPE", "RV");
				}else if(returnValue.toLowerCase().contains("off road vehicle policy") || returnValue.toLowerCase().contains("off road vehicle")||returnValue.toLowerCase().contains("off road")) {
					strExitState = productCountStatus(Constants.OFFROAD_PRODUCTTYPECOUNT_KYC, data, caa);
					data.setSessionData("ID_AUTH_SELECTED_PRODUCTTYPE", "RV");
				}else if((returnValue.toLowerCase().contains("home policy manufactured") ||returnValue.toLowerCase().contains("manufactured home policy") || returnValue.toLowerCase().contains("manufactured home")) && !returnValue.toLowerCase().contains("rental")) {
					strExitState = productCountStatus(Constants.MOBILEHOME_PRODUCTTYPECOUNT_KYC, data, caa);
					data.setSessionData("ID_AUTH_SELECTED_PRODUCTTYPE", "SP");
				} else if(returnValue.toLowerCase().contains("rental manufactured home policy") || returnValue.toLowerCase().contains("rental manufactured home") || returnValue.toLowerCase().contains("rental manufactured")) {
					strExitState = productCountStatus(Constants.RENTALHOME_PRODUCTTYPECOUNT_KYC, data, caa);
					data.setSessionData("ID_AUTH_SELECTED_PRODUCTTYPE", "SP");
				}else if(returnValue.toLowerCase().contains("specialty home owners policy") || returnValue.toLowerCase().contains("specialty home owners") || returnValue.toLowerCase().contains("specialty home")) {
					strExitState = productCountStatus(Constants.SPHOME_PRODUCTTYPECOUNT_KYC, data, caa);
					data.setSessionData("ID_AUTH_SELECTED_PRODUCTTYPE", "SP");
				}else if(returnValue.toLowerCase().contains("umbrella policy") || returnValue.toLowerCase().contains("umbrella")) {
					strExitState = productCountStatus(Constants.UMBRELLA_PRODUCTTYPECOUNT_KYC, data, caa);
					data.setSessionData("ID_AUTH_SELECTED_PRODUCTTYPE", "U");
					data.setSessionData("SIDA_RETENTION_PRODUCTYPE", "U");
				}
		}	
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in VIA_MN_007 :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"VIA_MN_007 :: "+strExitState);
		return strExitState;
	
	}
	
	@SuppressWarnings("unchecked")
	private String productCountStatus(String produtType, DecisionElementData data, CommonAPIAccess caa) {
		String strExitState = Constants.ER, appTag = Constants.EmptyString;
		String prompt = Constants.EmptyString;
		String lang = (String) data.getSessionData("S_PREF_LANG");
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
					data.addToLog(data.getCurrentElement(),"DOB in session after setting:"+data.getSessionData(Constants.S_API_DOB)); // Log after setting 
					data.addToLog(data.getCurrentElement(),"ZIP in session after setting:"+(String) data.getSessionData(Constants.S_API_ZIP)); // Log after setting 
					//DOB BYPASS CHANGE
					data.setSessionData("DOB_CHECK","YES");
					data.setSessionData("ZIP_CHECK","YES");
					
					data.setSessionData(Constants.S_POLICY_NUM, key1);
					data.addToLog(data.getCurrentElement(),"Policy number for selected product:"+(String) data.getSessionData(Constants.S_POLICY_NUM));
					data.setSessionData(Constants.S_POLICY_SOURCE, obj.getStrPolicySource());
					data.addToLog(data.getCurrentElement(),"Policy Source for selected product:"+(String) data.getSessionData(Constants.S_POLICY_SOURCE));
					data.setSessionData(Constants.S_POLICY_STATE_CODE, obj.getStrPolicyState());
					data.setSessionData(Constants.S_FINAL_POLICY_OBJ, obj);
					data.setSessionData("S_LOB", (String) obj.getStrPolicyLOB());
					//CS1360621
					data.setSessionData(Constants.S_POLICY_CATEGORY, obj.getpolicyCategory());
					data.setSessionData("S_CALLER_AUTH", "Identified");
					data.setSessionData("S_TI_SCORE", "MEDIUM");
					if(null != data.getSessionData("S_FLAG_FOREMOST_BU") && Constants.STRING_YES.equals(data.getSessionData("S_FLAG_FOREMOST_BU"))) {
						data.setSessionData("S_EPC_PAYMENT_POLICYNUM", obj.getStrPolicyProductCode()+key1);
					} else if(null != data.getSessionData("S_FLAG_FWS_BU") && Constants.STRING_YES.equals(data.getSessionData("S_FLAG_FWS_BU"))) {
						data.setSessionData("S_EPC_PAYMENT_POLICYNUM", obj.getStrBillingAccountNumber());
					}
//					// CS1336023 - Cancel policy - Arshath - start
					data.setSessionData(Constants.S_MDM_POLICY_STATUS, obj.getPolicyStatus());
					data.addToLog("Policy Status", obj.getPolicyStatus());
					// CS1336023 - Cancel policy - Arshath - start
					
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
						
						//CS1348016 - All BU's - Onboarding Line Routing
					    String apponBoardingFlag=(String) data.getSessionData(Constants.S_APP_ONBOARDING_ELIGIBLE_FLAG);
					  //  if( apponBoardingFlag.equalsIgnoreCase(Constants.Y_FLAG)) {
						String InceptionDate= obj.getInceptionDate();
						data.addToLog("Inception date is: ", InceptionDate);
						LocalDate currentDate= LocalDate.now();
						DateTimeFormatter formatter= new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("M/d/yyyy").toFormatter();
						String formattedDate =currentDate.format(formatter);
						data.addToLog("The  Inception Date is "+InceptionDate, "The Current date is "+formattedDate);
						LocalDate date1= LocalDate.parse(InceptionDate,formatter);
						LocalDate date2= LocalDate.parse(formattedDate,formatter);
						long daysBetween= ChronoUnit.DAYS.between(date1, date2);
						String daysBetweenString= String.valueOf(daysBetween);
						data.addToLog("The Days Between from Policy started and the current date::  ", daysBetweenString);
						String stronBoardingEligibleDays=(String)data.getSessionData(Constants.S_ONBOARDING_ELIGIBLE_DAYS);
						data.addToLog("The  days in Config file ::", stronBoardingEligibleDays);
						Long onBoardingEligibleDays=Long.valueOf(stronBoardingEligibleDays);
						
						//<=60 days check for onboarding->NLU 
						appTag = (String) data.getSessionData(Constants.APPTAG);
						
						if((appTag!=null && !Constants.EmptyString.equalsIgnoreCase(appTag))&& apponBoardingFlag.equalsIgnoreCase(Constants.Y_FLAG)) {
							data.addToLog(data.getCurrentElement(), "It's from NLU"+appTag);
							if(daysBetween<=onBoardingEligibleDays) {
								data.setSessionData(Constants.S_ONBOARDING_ELIGIBLE, Constants.Y_FLAG);
								data.addToLog("The Policy Inception date is lesser than equal to 60 Days::", "Y");
							}
							else {
								data.setSessionData(Constants.S_ONBOARDING_ELIGIBLE, Constants.N_FLAG);
								data.addToLog("The Policy Inception date is Lesser than 60 Days::", "N");
							}
						}
						//<=60 days check for onbaording->Main menus
						else {
							data.addToLog(data.getCurrentElement(), "It's from Main Menu");
							if(daysBetween<=onBoardingEligibleDays) {
								data.setSessionData(Constants.S_ONBOARDING_ELIGIBLE, Constants.Y_FLAG);
								data.addToLog("The Policy Inception date is lesser than 60 Days::", "Y");
							}
							else {
								data.setSessionData(Constants.S_ONBOARDING_ELIGIBLE, Constants.N_FLAG);
								data.addToLog("The Policy Inception date is Lesser than 60 Days::", "N");
							}
						}
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
					if(lang.equalsIgnoreCase("sp")) {
					prompt = "auto"; 
					}else {
						prompt="auto";
					}
					data.setSessionData("S_LOB", "A");
				}
				else if (Constants.HOME_PRODUCTTYPECOUNT_KYC.equalsIgnoreCase(produtType)) {
					prompt = "home"; 
					if(lang.equalsIgnoreCase("sp")) {
						prompt = "hogar"; 
					}
					data.setSessionData("S_LOB", "H");
				}
				else if (Constants.UMBRELLA_PRODUCTTYPECOUNT_KYC.equalsIgnoreCase(produtType)) {
					prompt = "umbrella";
					if(lang.equalsIgnoreCase("sp")) {
						prompt = "paraguas"; 
					}
					data.setSessionData("S_LOB", "U");
				}
				else if (Constants.MR_PRODUCTTYPECOUNT_KYC.equalsIgnoreCase(produtType)) {
					prompt = "Marine Watercraft";
					if(lang.equalsIgnoreCase("sp")) {
						prompt = "embarcación marina"; 
					}
					prompt = prompt.replaceAll(" " , ".");
					data.setSessionData("S_LOB", "B");
				}
				else if (Constants.TRAVEL_PRODUCTTYPECOUNT_KYC.equalsIgnoreCase(produtType)) {					
					prompt = "Travel Trailer";
					if(lang.equalsIgnoreCase("sp")) {
						prompt = "remolque de viaje"; 
					}
					prompt = prompt.replaceAll(" " , ".");
					data.setSessionData("S_LOB", "M");
				}		
				else if(Constants.MOTORHOME_PRODUCTTYPECOUNT_KYC.equalsIgnoreCase(produtType)) {
					prompt = "Motor Home";
					if(lang.equalsIgnoreCase("sp")) {
						prompt = "casa rodante"; 
					}
					prompt = prompt.replaceAll(" " , ".");
					data.setSessionData("S_LOB", "M");
				}
				else if(Constants.MOTORCYCLE_PRODUCTTYPECOUNT_KYC.equalsIgnoreCase(produtType)) {
					prompt = "Motorcycle";
					if(lang.equalsIgnoreCase("sp")) {
						prompt = "motocicleta"; 
					}
					prompt = prompt.replaceAll(" " , ".");
					data.setSessionData("S_LOB", "M");
				}
				else if(Constants.OFFROAD_PRODUCTTYPECOUNT_KYC.equalsIgnoreCase(produtType)) {
					prompt = "Off Road Vehicle";
					if(lang.equalsIgnoreCase("sp")) {
						prompt = "vehículo todoterreno"; 
					}
					prompt = prompt.replaceAll(" " , ".");
					data.setSessionData("S_LOB", "M");
				}
					
				else if (Constants.MOBILEHOME_PRODUCTTYPECOUNT_KYC.equalsIgnoreCase(produtType)) {
					prompt = "Mobile Home";
					if(lang.equalsIgnoreCase("sp")) {
						prompt = "casa prefabricada"; 
					}
					prompt = prompt.replaceAll(" " , ".");
					data.setSessionData("S_LOB", "SP");
				}
				else if(Constants.RENTALHOME_PRODUCTTYPECOUNT_KYC.equalsIgnoreCase(produtType)) {
					prompt = "Rental Manufactured Home";
					if(lang.equalsIgnoreCase("sp")) {
						prompt = "prefabricada de alquiler"; 
					}
					prompt = prompt.replaceAll(" " , ".");
					data.setSessionData("S_LOB", "SP");
				}
				else if(Constants.SPHOME_PRODUCTTYPECOUNT_KYC.equalsIgnoreCase(produtType)) {
					prompt = "Specialty Home Owners";
					if(lang.equalsIgnoreCase("sp")) {
						prompt = "vivienda especial"; 
					}
					prompt = prompt.replaceAll(" " , ".");
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
			data.addToLog(data.getCurrentElement(),"Exception in VIA_MN_007 productCountStatus() :: "+e);
			caa.printStackTrace(e);
		}
		
		return strExitState;
	}
	
	@SuppressWarnings("unchecked")
	private String productCountStatusPolicy(String produtType, DecisionElementData data, CommonAPIAccess caa) {
		String strExitState = Constants.ER,appTag= Constants.EmptyString;
		String prompt = Constants.EmptyString;
		String lang = (String) data.getSessionData("S_PREF_LANG");
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
					data.setSessionData("S_CALLER_AUTH", "Identified");
					data.setSessionData("S_TI_SCORE", "MEDIUM");
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
						
						//CS1348016 - All BU's - Onboarding Line Routing
					    String apponBoardingFlag=(String) data.getSessionData(Constants.S_APP_ONBOARDING_ELIGIBLE_FLAG);
					  //  if( apponBoardingFlag.equalsIgnoreCase(Constants.Y_FLAG)) {
						String InceptionDate= obj.getInceptionDate();
						data.addToLog("Inception date is: ", InceptionDate);
						LocalDate currentDate= LocalDate.now();
						DateTimeFormatter formatter= new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("M/d/yyyy").toFormatter();
						String formattedDate =currentDate.format(formatter);
						data.addToLog("The  Inception Date is "+InceptionDate, "The Current date is "+formattedDate);
						LocalDate date1= LocalDate.parse(InceptionDate,formatter);
						LocalDate date2= LocalDate.parse(formattedDate,formatter);
						long daysBetween= ChronoUnit.DAYS.between(date1, date2);
						String daysBetweenString= String.valueOf(daysBetween);
						data.addToLog("The Days Between from Policy started and the current date::  ", daysBetweenString);
						String stronBoardingEligibleDays=(String)data.getSessionData(Constants.S_ONBOARDING_ELIGIBLE_DAYS);
						data.addToLog("The  days in Config file ::", stronBoardingEligibleDays);
						Long onBoardingEligibleDays=Long.valueOf(stronBoardingEligibleDays);
						
						//<=60 days check for onboarding->NLU 
						appTag = (String) data.getSessionData(Constants.APPTAG);
						
						if((appTag!=null && !Constants.EmptyString.equalsIgnoreCase(appTag))&& apponBoardingFlag.equalsIgnoreCase(Constants.Y_FLAG)) {
							data.addToLog(data.getCurrentElement(), "It's from NLU"+appTag);
							if(daysBetween<=onBoardingEligibleDays) {
								data.setSessionData(Constants.S_ONBOARDING_ELIGIBLE, Constants.Y_FLAG);
								data.addToLog("The Policy Inception date is lesser than 60 Days::", "Y");
							}
							else {
								data.setSessionData(Constants.S_ONBOARDING_ELIGIBLE, Constants.N_FLAG);
								data.addToLog("The Policy Inception date is Lesser than 60 Days::", "N");
							}
						}
						//<=60 days check for onbaording->Main menus
						else {
							data.addToLog(data.getCurrentElement(), "It's from Main Menu");
							if(daysBetween<=onBoardingEligibleDays) {
								data.setSessionData(Constants.S_ONBOARDING_ELIGIBLE, Constants.Y_FLAG);
								data.addToLog("The Policy Inception date is lesser than 60 Days::", "Y");
							}
							else {
								data.setSessionData(Constants.S_ONBOARDING_ELIGIBLE, Constants.N_FLAG);
								data.addToLog("The Policy Inception date is Lesser than 60 Days::", "N");
							}
						}
						
//						}
//					    else {
//					    	data.setSessionData(Constants.S_ONBOARDING_ELIGIBLE, Constants.N_FLAG);
//					    	data.addToLog(data.getCurrentElement(), "In AppTag File the OnBoarding Eligible Flag as N");
//					    }
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
					if(lang.equalsIgnoreCase("sp")) {
						prompt = "casa"; 
					}
					data.setSessionData("S_LOB", "H");
				}
				else if (Constants.UMBRELLA_PRODUCTTYPECOUNT_KYC.equalsIgnoreCase(produtType)) {
					prompt = "umbrella";
					if(lang.equalsIgnoreCase("sp")) {
						prompt = "paraguas"; 
					}
					data.setSessionData("S_LOB", "U");
				}
				else if (Constants.MR_PRODUCTTYPECOUNT_KYC.equalsIgnoreCase(produtType)) {
					prompt = "boat";
					if(lang.equalsIgnoreCase("sp")) {
						prompt = "barco"; 
					}
					data.setSessionData("S_LOB", "B");
				}
				else if (Constants.RV_PRODUCTTYPECOUNT_KYC.equalsIgnoreCase(produtType)) {
					prompt = "Motor Home or Motorcycle";
					if(lang.equalsIgnoreCase("sp")) {
						prompt = "casa rodante o póliza de motocicleta"; 
					}
					prompt = prompt.replaceAll(" " , ".");
					data.setSessionData("S_LOB", "M");
				}
				else if (Constants.SP_PRODUCTTYPECOUNT_KYC.equalsIgnoreCase(produtType)) {
					prompt = "Specialty Dwelling or Mobile Home";
					if(lang.equalsIgnoreCase("sp")) {
						prompt = "vivienda especial o póliza de casa móvil"; 
					}
					prompt = prompt.replaceAll(" " , ".");
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
			data.addToLog(data.getCurrentElement(),"Exception in VIA_MN_007 productCountStatus() :: "+e);
			caa.printStackTrace(e);
		}
		
		return strExitState;
	}
	
}


