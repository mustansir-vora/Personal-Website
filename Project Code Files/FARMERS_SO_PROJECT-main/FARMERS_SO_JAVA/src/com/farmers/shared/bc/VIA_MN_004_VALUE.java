package com.farmers.shared.bc;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.bean.PolicyBean;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class VIA_MN_004_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException 
	{
	
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {

			String returnValue = (String) data.getElementData("VIA_MN_004_Call","Return_Value");
			data.addToLog(currElementName, "Menu ID : "+"VIA_MN_004"+" :: Menu Value : "+returnValue);
			data.setSessionData("VIA_MN_004_VALUE", returnValue);
			
			if (Constants.NOINPUT.equalsIgnoreCase(returnValue)) {
			    strExitState = Constants.NOINPUT;
			} else if (Constants.NOMATCH.equalsIgnoreCase(returnValue)) {
			    strExitState = Constants.NOMATCH;
			} else if (Constants.SOMETHINGELSE.equalsIgnoreCase(returnValue)) {
			    strExitState = Constants.SOMETHINGELSE;
			}else if(returnValue.equalsIgnoreCase("None of these")) {
				strExitState = Constants.SOMETHINGELSE;
			}else if(returnValue.equalsIgnoreCase(Constants.REPRESENTATIVE)) {
				data.setSessionData("LAST_MENU_NAME_REP", "VIA_MN_004");
				strExitState = Constants.REPRESENTATIVE;
			}  else if (Constants.ONE.equalsIgnoreCase(returnValue)) {
			    strExitState = Constants.ONE;
			    setDataIntoSession(Constants.ONE, data);
			} else if (Constants.TWO.equalsIgnoreCase(returnValue)) {
			    strExitState = Constants.TWO;
			    setDataIntoSession(Constants.TWO, data);
			} else if (Constants.THREE.equalsIgnoreCase(returnValue)) {
			    strExitState = Constants.THREE;
			    setDataIntoSession(Constants.THREE, data);
			} else if (Constants.FOUR.equalsIgnoreCase(returnValue)) {
			    strExitState = Constants.FOUR;
			    setDataIntoSession(Constants.FOUR, data);
			} else if (Constants.FIVE.equalsIgnoreCase(returnValue)) {
			    strExitState = Constants.FIVE;
			    setDataIntoSession(Constants.FIVE, data);
			}
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in VIA_MN_004 :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"VIA_MN_004 :: "+strExitState);
		return strExitState;
	
	}
	
	@SuppressWarnings("unchecked")
	private void setDataIntoSession(String index, DecisionElementData data) {
		String appTag= Constants.EmptyString;
		try {
			HashMap<String, PolicyBean> remainingPoliciesMap = (HashMap<String, PolicyBean>) data.getSessionData("S_REMAININGPOLICIES");
			
			if (remainingPoliciesMap != null && !remainingPoliciesMap.isEmpty()) {
				
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
				
				String lessthanfivepolicies = (String)data.getSessionData("S_LessthanFivePolicy");
				
				if(null!=lessthanfivepolicies && "Y".equalsIgnoreCase(lessthanfivepolicies)) {
					
					data.addToLog("S_LessthanFivePolicy:", lessthanfivepolicies);	
					for (String key1 : remainingPoliciesMap.keySet()) {
	    				data.addToLog("Entering into Remaining Policy details map:", key1);				
	    				j=j+1;
	    				if(i != j) continue;
	    				PolicyBean obj = (PolicyBean) remainingPoliciesMap.get(key1);
	    				data.setSessionData(Constants.S_API_DOB, obj.getStrDOB());
	    				data.setSessionData(Constants.S_API_ZIP, obj.getStrZipCode());
	    				data.addToLog("DOB: ",(String) data.getSessionData(Constants.S_API_DOB));
	    				data.addToLog("ZIP: ",(String)data.getSessionData(Constants.S_API_ZIP));
	    				data.setSessionData("DOB_CHECK","YES");
	    				data.setSessionData("ZIP_CHECK","YES");
	    				data.setSessionData("S_CALLER_AUTH", "Identified");
	    				data.setSessionData("S_TI_SCORE", "MEDIUM");
	    				data.setSessionData(Constants.S_POLICY_NUM, key1);
	    				data.setSessionData(Constants.S_POLICY_STATE_CODE, obj.getStrPolicyState());
	    				data.setSessionData(Constants.S_POLICY_SOURCE, obj.getStrPolicySource());
	    				data.setSessionData(Constants.S_FINAL_POLICY_OBJ, obj);
	    				//CS1360621
						data.setSessionData(Constants.S_POLICY_CATEGORY, obj.getpolicyCategory());
	//    				// CS1336023 - Cancel policy - Arshath - start
	    				data.setSessionData(Constants.S_MDM_POLICY_STATUS, obj.getPolicyStatus());
	    				data.addToLog("Policy Status", obj.getPolicyStatus());
	    				// CS1336023 - Cancel policy - Arshath - start
	    				
	    				if(null != data.getSessionData("S_FLAG_FOREMOST_BU") && Constants.STRING_YES.equals(data.getSessionData("S_FLAG_FOREMOST_BU"))) {
	    					data.setSessionData("S_EPC_PAYMENT_POLICYNUM", obj.getStrPolicyProductCode()+key1);
	    				} else if(null != data.getSessionData("S_FLAG_FWS_BU") && Constants.STRING_YES.equals(data.getSessionData("S_FLAG_FWS_BU"))) {
	    					data.setSessionData("S_EPC_PAYMENT_POLICYNUM", obj.getStrBillingAccountNumber());
	    				}
	    				
	    				 data.setSessionData("S_FDS_GPC", obj.getGpcCode());
	    				 data.setSessionData("S_FDS_PAYPLAN", obj.getPayPlan());
	    				 data.setSessionData("S_FDS_SERVICING_PHONE_NUMBER", obj.getServicingPhoneNumber());
	    			     data.addToLog("GPC code for FDS PayrollDeduct ::", (String)data.getSessionData("S_FDS_GPC"));
	    			     data.addToLog("Pay Plan for FDS PayrollDeduct ::", (String)data.getSessionData("S_FDS_PAYPLAN"));
	    			     data.addToLog("Servicing Phone Number for FDS PayrollDeduct ::", (String)data.getSessionData("S_FDS_SERVICING_PHONE_NUMBER"));
	    			     
	    			   //START : CS1265959 : Farmers Insurance | US | FDS - Payroll Deduct indicator & routing                 
	    					if ((obj.getPayPlan()!= null )&& (obj.getPayPlan().contains("payPlan")))  {
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
	    					
	    					// onboarding check code
	    					//CS1348016 - All BU's - Onboarding Line Routing
						    String apponBoardingFlag=(String) data.getSessionData(Constants.S_APP_ONBOARDING_ELIGIBLE_FLAG);
						   // if( apponBoardingFlag.equalsIgnoreCase(Constants.Y_FLAG)) {
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
							
//							}
//						    else {
//						    	data.setSessionData(Constants.S_ONBOARDING_ELIGIBLE, Constants.N_FLAG);
//						    	data.addToLog(data.getCurrentElement(), "In AppTag File the OnBoarding Eligible Flag as N");
//						    }
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
	    			}
					
				
			}	
            else {
            	
            	HashMap<String, PolicyBean> policyDetailsMap = (HashMap<String, PolicyBean>) data.getSessionData(Constants.POLICY_DETAILS_FOR_MN3);
            	
            	if (policyDetailsMap != null && !policyDetailsMap.isEmpty()) {	
            		
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
						data.addToLog("Entering into Policy details map:", key1);				
						j=j+1;
						if(i != j) continue;
						PolicyBean obj = (PolicyBean) policyDetailsMap.get(key1);
						data.setSessionData(Constants.S_API_DOB, obj.getStrDOB());
						data.setSessionData(Constants.S_API_ZIP, obj.getStrZipCode());
						data.addToLog("DOB: ",(String) data.getSessionData(Constants.S_API_DOB));
						data.addToLog("ZIP: ",(String)data.getSessionData(Constants.S_API_ZIP));
						data.setSessionData("DOB_CHECK","YES");
						data.setSessionData("ZIP_CHECK","YES");
						data.setSessionData("S_CALLER_AUTH", "Identified");
						data.setSessionData("S_TI_SCORE", "MEDIUM");
						data.setSessionData(Constants.S_POLICY_NUM, key1);
						data.setSessionData(Constants.S_POLICY_STATE_CODE, obj.getStrPolicyState());
						data.setSessionData(Constants.S_POLICY_SOURCE, obj.getStrPolicySource());
						data.setSessionData(Constants.S_FINAL_POLICY_OBJ, obj);
						//CS1360621
						data.setSessionData(Constants.S_POLICY_CATEGORY, obj.getpolicyCategory());
		//				// CS1336023 - Cancel policy - Arshath - start
						data.setSessionData(Constants.S_MDM_POLICY_STATUS, obj.getPolicyStatus());
						data.addToLog("Policy Status", obj.getPolicyStatus());
						// CS1336023 - Cancel policy - Arshath - start
						
						if(null != data.getSessionData("S_FLAG_FOREMOST_BU") && Constants.STRING_YES.equals(data.getSessionData("S_FLAG_FOREMOST_BU"))) {
							data.setSessionData("S_EPC_PAYMENT_POLICYNUM", obj.getStrPolicyProductCode()+key1);
						} else if(null != data.getSessionData("S_FLAG_FWS_BU") && Constants.STRING_YES.equals(data.getSessionData("S_FLAG_FWS_BU"))) {
							data.setSessionData("S_EPC_PAYMENT_POLICYNUM", obj.getStrBillingAccountNumber());
						}
						
						 data.setSessionData("S_FDS_GPC", obj.getGpcCode());
						 data.setSessionData("S_FDS_PAYPLAN", obj.getPayPlan());
						 data.setSessionData("S_FDS_SERVICING_PHONE_NUMBER", obj.getServicingPhoneNumber());
					     data.addToLog("GPC code for FDS PayrollDeduct ::", (String)data.getSessionData("S_FDS_GPC"));
					     data.addToLog("Pay Plan for FDS PayrollDeduct ::", (String)data.getSessionData("S_FDS_PAYPLAN"));
					     data.addToLog("Servicing Phone Number for FDS PayrollDeduct ::", (String)data.getSessionData("S_FDS_SERVICING_PHONE_NUMBER"));
					     
					   //START : CS1265959 : Farmers Insurance | US | FDS - Payroll Deduct indicator & routing                 
							if (((obj.getPayPlan()!= null )&& (obj.getPayPlan().contains("payPlan")))) {
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
							// onboarding check code
							//CS1348016 - All BU's - Onboarding Line Routing
						    String apponBoardingFlag=(String) data.getSessionData(Constants.S_APP_ONBOARDING_ELIGIBLE_FLAG);
						    if( apponBoardingFlag.equalsIgnoreCase(Constants.Y_FLAG)) {
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
							
							if(appTag!=null && !Constants.EmptyString.equalsIgnoreCase(appTag)) {
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
							
							}
						    else {
						    	data.setSessionData(Constants.S_ONBOARDING_ELIGIBLE, Constants.N_FLAG);
						    	data.addToLog(data.getCurrentElement(), "In AppTag File the OnBoarding Eligible Flag as N");
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
				
	            }
            	
            	}
            
		} catch (Exception e) {
			data.addToLog(data.getCurrentElement(),"Exception in VIA_MN_004 :: "+e);
		}
		
	}
}
