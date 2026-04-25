package com.farmers.shared.host;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.bean.FNWL.PolicyBean_via_ANI;
import com.farmers.bean.FNWL.PolicyBean_via_DETERMINISTIC_SEARCH;
import com.farmers.bean.FNWL.PolicyBean_via_PCN;
import com.farmers.client.Lookupcall;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class FNWL_HOST_006 extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws Exception {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);
		String strReqBody = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;
		String apiRespCode = Constants.EmptyString;
		String region=null;
		
		try {
			
			HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
			
			if (null != data.getSessionData(Constants.S_FNWL_MDM_DETERMINISTIC_LOOKUP_URL) && null != data.getSessionData(Constants.S_READ_TIMEOUT) && null != data.getSessionData(Constants.S_CONN_TIMEOUT)) {
				String url = (String) data.getSessionData(Constants.S_FNWL_MDM_DETERMINISTIC_LOOKUP_URL);
				String dob = null != (String) data.getSessionData(Constants.FNWL_MN_014_VALUE)? (String) data.getSessionData(Constants.FNWL_MN_014_VALUE): Constants.DEFAULT;				
				SimpleDateFormat sdf1 = new SimpleDateFormat("MM-dd-yyyy");
				SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
				dob = sdf2.format(sdf1.parse(dob));
				String zip = null != (String) data.getSessionData(Constants.FNWL_MN_015_VALUE)? (String) data.getSessionData(Constants.FNWL_MN_015_VALUE): Constants.DEFAULT;		
				String ssn = null != (String) data.getSessionData(Constants.FNWL_MN_016_VALUE)? (String) data.getSessionData(Constants.FNWL_MN_016_VALUE): Constants.DEFAULT;
				String conntimeout = (String) data.getSessionData(Constants.S_CONN_TIMEOUT);
				String readtimeout = (String) data.getSessionData(Constants.S_READ_TIMEOUT);
				String Callid = (String) data.getSessionData(Constants.S_CALLID);
				LoggerContext context = (LoggerContext) data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
				String lookupType = "DOB";
						
				data.addToLog(currElementName, "Call ID :: " + Callid);
				data.addToLog(currElementName, "readTimeOut :: " + readtimeout);
				data.addToLog(currElementName, "connTimeOut :: " + conntimeout);
				data.addToLog(currElementName, "URL :: " + url);
				data.addToLog(currElementName, "LookupType :: " + lookupType);
				data.addToLog(currElementName, "Date Of Birth :: " + dob);
				data.addToLog(currElementName, "Zip Code :: " + zip);
				data.addToLog(currElementName, "ssn :: " + ssn);
				
				String prefix = "https://api-np-ss.farmersinsurance.com"; 
				String UAT_FLAG="";
				
				if(url.startsWith(prefix)) {
		        	UAT_FLAG="YES";
		        }
				
                if("YES".equalsIgnoreCase(UAT_FLAG)) {
                	
                region = regionDetails.get(Constants.S_FNWL_MDM_DETERMINISTIC_LOOKUP_URL);
                }
                else {
                	region="PROD";
                }
				
				Lookupcall lookupCall = new Lookupcall();
				JSONObject resp = lookupCall.FNWLMDMLookup_Post(url, Callid, "", "", dob, zip, ssn, lookupType, region, UAT_FLAG, Integer.parseInt(conntimeout), Integer.parseInt(readtimeout), context);
				
				if (null != resp && resp.containsKey(Constants.RESPONSE_CODE)) {
					apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
				}
				data.addToLog("FNWL MDM via DOB, ZIP, SSN Resp :: ", resp.toString());
				
				if (null != resp) {
					
					if (resp.containsKey(Constants.REQUEST_BODY)) {
						strReqBody = resp.get(Constants.REQUEST_BODY).toString();
					}
					
					if (resp.containsKey(Constants.RESPONSE_BODY)) {
						strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
					}
					else {
						strRespBody = resp.get(Constants.RESPONSE_MSG).toString();
						data.addToLog(currElementName, "resp does not contain responseBody");
					}
					
					if (resp.containsKey(Constants.RESPONSE_CODE) && ((int) resp.get(Constants.RESPONSE_CODE) == 200 || (int) resp.get(Constants.RESPONSE_CODE) == 400)) 
					{
						strExitState = apiResponseManipulation(data, caa, currElementName, resp);
					}else {
						data.addToLog(currElementName, "response Code != 200 | 400");
					}
				}
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in FNWL_HOST_006 API Call :: " + e);
			caa.printStackTrace(e);
		}
			
		try {
			objHostDetails.startHostReport(currElementName, "FNWL_MDM Lookup by Customer Attributes", strReqBody, region, (String) data.getSessionData(Constants.S_FNWL_MDM_DETERMINISTIC_LOOKUP_URL));
			objHostDetails.endHostReport(data, strRespBody,	strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode, null == data.getSessionData("NoofPolicies") || data.getSessionData("NoofPolicies").toString().equalsIgnoreCase("0") ? "0 Policies found" : ((Integer) data.getSessionData("NoofPolicies")).toString().equalsIgnoreCase("1") ? "1 Policy found|Product Types: " + (HashSet<String>) data.getSessionData("policyproductTypeList") : ((Integer) data.getSessionData("NoofPolicies")).toString() + " policies found|Product Types: " + (HashSet<String>) data.getSessionData("policyproductTypeList")) ;
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception while forming host reporting for FNWL MDM Lookup via DOB, ZIP, SSN call  :: " + e);
			caa.printStackTrace(e);
		}
		return strExitState;
	}	
	
	public String apiResponseManipulation(DecisionElementData data, CommonAPIAccess caa, String currElementName, JSONObject resp) {
		String strExitState = Constants.ER;
		JSONArray customersArr = null;
		JSONArray policiesArr = null;
		JSONObject responseBody = null;
		HashMap<String, PolicyBean_via_DETERMINISTIC_SEARCH> policyMap = new HashMap<String, PolicyBean_via_DETERMINISTIC_SEARCH>();
		
		try {
			
			if(null != resp) {
				
				if (resp.containsKey("responseBody")) {
					responseBody = (JSONObject) resp.get("responseBody");
					
					if (responseBody.containsKey("customers")) {
						customersArr = (JSONArray) responseBody.get("customers");
						
						if (null != customersArr && customersArr.size()>0) {
							data.addToLog(currElementName, "customers Array size is greater than 0 :: customer/customer found");
							
							for (int i = 0; i < customersArr.size(); i++) {
								JSONObject customer = (JSONObject)customersArr.get(i);
								
								if (customer.containsKey("policies")) {
									policiesArr = (JSONArray) customer.get("policies");
									if (null != policiesArr && policiesArr.size()>0) {
										data.addToLog(currElementName, "Policies Array size is greater than 0 :: policies/policies found");
										
										for (int j = 0; j < policiesArr.size(); j++) {
											
											JSONObject policy = (JSONObject) policiesArr.get(j);
											
											PolicyBean_via_DETERMINISTIC_SEARCH policyInfo = new PolicyBean_via_DETERMINISTIC_SEARCH();
											
											List<String> rolesList = new ArrayList<String>();
											if (policy.containsKey("roles")) {
												
												JSONArray rolesArr = (JSONArray) policy.get("roles");
												for (int k = 0; k < rolesArr.size(); k++) {
													JSONObject roleObj = (JSONObject) rolesArr.get(k);
													if (roleObj.containsKey("role")) {
														rolesList.add((String) roleObj.get("role"));
													}
												}
											}
											
											policyInfo.setPolicyType(null != (String) policy.get("policyType") ? (String) policy.get("policyType") : Constants.EmptyString);
											policyInfo.setPolicyCategory(null != (String) policy.get("policyCategory") ? (String) policy.get("policyCategory") : Constants.EmptyString);
											policyInfo.setPolicyStateCode(null != (String) policy.get("policyStateCode") ? (String) policy.get("policyStateCode") : Constants.EmptyString);
											policyInfo.setPolicySource(null != (String) policy.get("policySource") ? (String) policy.get("policySource") : Constants.EmptyString);
											policyInfo.setPolicyNumber(null != (String) policy.get("policyNumber") ? (String) policy.get("policyNumber") : Constants.EmptyString);
											policyInfo.setIssueDate(null != (String) policy.get("issueDate") ? (String) policy.get("issueDate") : Constants.EmptyString);
											policyInfo.setPolicyStatus(null != (String) policy.get("policyStatus") ? (String) policy.get("policyStatus") : Constants.EmptyString);
											policyInfo.setLineOfBusiness(null != (String) policy.get("lineOfBusiness") ? (String) policy.get("lineOfBusiness") : Constants.EmptyString);
											policyInfo.setBookOfBusiness(null != (String) policy.get("bookOfBusiness") ? (String) policy.get("bookOfBusiness") : Constants.EmptyString);
											policyInfo.setRolesList(rolesList);
											
											policyMap.put((String) policy.get("policyNumber"), policyInfo);
										}
										
										data.setSessionData(Constants.FNWL_POLICYMAP_VIA_DETERMINISTICSEARCH, policyMap);
										data.addToLog(currElementName, "PolicyStorage via DeterministicSearch Hashmap :: " + policyMap);
										
										if(null != policyMap) {
											data.setSessionData(Constants.FNWL_POLICY_FOUND_VIA_DETERMINISTIC_SEARCH, Constants.TRUE);
											data.setSessionData(Constants.FNWL_POLICY_FOUND, Constants.TRUE);
											data.setSessionData("NoofPolicies", policyMap.size());
											
										}else {
											data.setSessionData(Constants.FNWL_POLICY_FOUND_VIA_DETERMINISTIC_SEARCH, Constants.FALSE);
											data.setSessionData(Constants.FNWL_POLICY_FOUND, Constants.FALSE);
										}
										
										int policiesSize = policyMap.size();
										data.addToLog(currElementName, "Policy Storage via DeterministicSearch Hashmap size :: " + policiesSize);
										
										if(policiesSize == 1) {
											data.setSessionData(Constants.FNWL_SINGLE_MDM_POLICY_FOUND_VIA_DETERMINISTIC_SEARCH, Constants.TRUE);
											data.addToLog(currElementName, "Single Policiy found in MDM DETERMINISTIC SEARCH Lookup :: " + data.getSessionData(Constants.FNWL_SINGLE_MDM_POLICY));
											
											PolicyBean_via_DETERMINISTIC_SEARCH policyBeanObj = policyMap.values().iterator().next();
											data.setSessionData(Constants.FNWL_POLICY_NUM, policyBeanObj.getPolicyNumber());
											
											String policyType = policyBeanObj.getPolicyType();
											String lineOfBusiness = policyBeanObj.getLineOfBusiness();
											String bookOfBusiness = policyBeanObj.getBookOfBusiness();
											String issueDate = policyBeanObj.getIssueDate();
											
											checkifIssueDateisBeforeComparisonDate(data, currElementName, caa, issueDate);
											checkPolicyType(data, currElementName, caa, policyType);
											checkPolicyLOB(data, currElementName, caa, lineOfBusiness);
											checkPolicyBookType(data, currElementName, caa, bookOfBusiness);
										}else {
											data.setSessionData(Constants.FNWL_SINGLE_MDM_POLICY_FOUND_VIA_DETERMINISTIC_SEARCH, Constants.FALSE);
										}
									} else {
										data.addToLog(currElementName, "policies array is null");
									}
								}else {
									data.addToLog(currElementName, "Response Body does not contain policies array");
								}
							}
						} else {
							data.addToLog(currElementName, "customers array is null");
						}
					}else {
						data.addToLog(currElementName, "Response Body does not contain customers array");
					}
				} else {
					data.addToLog(currElementName, "response does not contain Response Body");
				}
			}else {
				data.addToLog(currElementName, "Response is null");
			}
			strExitState = Constants.SU;
			
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in FNWL_HOST_006 apiResponseManipulation method  :: " + e);
			caa.printStackTrace(e);
		}
		
		return strExitState;
	}
	
	public void checkifIssueDateisBeforeComparisonDate(DecisionElementData data, String currElementName, CommonAPIAccess caa, String IssueDate) {
		
		try {
			DateTimeFormatter issueDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			DateTimeFormatter comparisonDateFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
			
			if (null != IssueDate) {
				LocalDate issueLocalDate = LocalDate.parse(IssueDate, issueDateFormatter);
				LocalDate comparisonLocalDate = LocalDate.parse(Constants.FNWL_IUL_COMPARISON_DATE, comparisonDateFormatter);
				
				if (issueLocalDate.isBefore(comparisonLocalDate)) {
					data.addToLog(currElementName, "Issue Date is before 10-01-2019");
					data.setSessionData(Constants.FNWL_POLICY_ISSUE_DATE, Constants.FNWL_BEFORE);
				}
				else {
					data.addToLog(currElementName, "Issue Date is After 10-01-2019");
					data.setSessionData(Constants.FNWL_POLICY_ISSUE_DATE, Constants.FNWL_AFTER);
				}
			}
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in FNWL_HOST_006 checkifIssueDateisBeforeComparisonDate method  :: " + e);
			caa.printStackTrace(e);
		}
	}
	
	public void checkPolicyType(DecisionElementData data, String currElementName, CommonAPIAccess caa, String policyType) {
		HashSet<String> policyProductTypeList = new HashSet<>();
			try {
				if (null != policyType && (Constants.FNWL_FARMERS_VARIABLE_ANNUITY.equalsIgnoreCase(policyType) || Constants.FNWL_FARMERS_VARIABLE_UNIVERSAL_LIFE.equalsIgnoreCase(policyType) || Constants.FNWL_FARMERS_VARIABLE_ANNUITY_IRA.equalsIgnoreCase(policyType) || Constants.FNWL_FARMERS_VARIABLE_ANNUITY_NON_QUALIFIED.equalsIgnoreCase(policyType) || Constants.FNWL_FARMERS_VARIABLE_ANNUITY_ROTH.equalsIgnoreCase(policyType) || Constants.FNWL_Variable_Universal_Life.equalsIgnoreCase(policyType) || Constants.FNWL_VARIABLE_UNIVERSAL_LIFE.equalsIgnoreCase(policyType) || Constants.FNWL_FARMERS_VUL_LIFEACCUMULATOR.equalsIgnoreCase(policyType))) {
					data.addToLog(currElementName, "POLICY Type :: " + policyType + " :: Identified as VUL Policy Type");
					data.setSessionData(Constants.FNWL_POLICY_TYPE, Constants.FNWL_VUL);
				}
				else if (null != policyType && (Constants.FNWL_FARMERS_INDEX_UNIVERSAL_LIFE.equalsIgnoreCase(policyType) || Constants.FNWL_FARMERS_INDEXED_UNIVERSAL_LIFE.equalsIgnoreCase(policyType))) {
					data.addToLog(currElementName, "POLICY Type :: " + policyType + " :: Identified as IUL Policy Type");
					data.setSessionData(Constants.FNWL_POLICY_TYPE, Constants.FNWL_IUL);
					}
				else {
					data.addToLog(currElementName, "POLICY Type :: " + policyType + " :: Identified as Other Policy Type");
					data.setSessionData(Constants.FNWL_POLICY_TYPE, Constants.FNWL_Others);
				}
				policyProductTypeList.add(policyType);
				data.setSessionData("policyproductTypeList", policyProductTypeList);
			} catch (Exception e) {
				data.addToLog(currElementName,"Exception in FNWL_HOST_006 checkpolicyType method  :: " + e);
				caa.printStackTrace(e);
			}
		}

	public void checkPolicyLOB(DecisionElementData data, String currElementName, CommonAPIAccess caa, String lineOfBusiness) {

		try {
			if (null != lineOfBusiness && Constants.FNWL_LIFE.equalsIgnoreCase(lineOfBusiness)) {
				data.setSessionData(Constants.FNWL_POLICY_LOB, Constants.FNWL_LIFE);
				data.addToLog(currElementName, "Policy LOB :: " + lineOfBusiness);
			}
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in FNWL_HOST_006 checkpolicyLOB method  :: " + e);
			caa.printStackTrace(e);
		}
	}
	
	public void checkPolicyBookType(DecisionElementData data, String currElementName, CommonAPIAccess caa, String bookOfBusiness) {
		
		try {
			if (null != bookOfBusiness && Constants.FNWL_FRONTBOOK.equalsIgnoreCase(bookOfBusiness)) {
				data.addToLog(currElementName, "Book Type :: " + bookOfBusiness + " :: Identified as F");
				data.setSessionData(Constants.FNWL_POLICY_BOOKTYPE, Constants.FNWL_FRONTBOOK);
			} else if (null != bookOfBusiness && Constants.FNWL_BACKBOOK.equalsIgnoreCase(bookOfBusiness)) {
				data.addToLog(currElementName, "Book Type :: " + bookOfBusiness + " :: Identified as B");
				data.setSessionData(Constants.FNWL_POLICY_BOOKTYPE, Constants.FNWL_BACKBOOK);
			} else if (null != bookOfBusiness && Constants.FNWL_MIDBOOK.equalsIgnoreCase(bookOfBusiness)) {
				data.addToLog(currElementName, "Book Type :: " + bookOfBusiness + " :: Identified as M");
				data.setSessionData(Constants.FNWL_POLICY_BOOKTYPE, Constants.FNWL_MIDBOOK);
			} else if (null == bookOfBusiness || Constants.EmptyString.equalsIgnoreCase(bookOfBusiness)){
				data.addToLog(currElementName, "Book Type :: " + bookOfBusiness + " :: Identified as null/blank");
				data.setSessionData(Constants.FNWL_POLICY_BOOKTYPE, Constants.NULL);
			}
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in FNWL_HOST_006 checkPolicyBookType method  :: " + e);
			caa.printStackTrace(e);
		}
	}
}		
