package com.farmers.shared.host;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.bean.FNWL.PolicyBean_via_ANI;
import com.farmers.bean.FNWL.PolicyBean_via_PCN;
import com.farmers.client.Lookupcall;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class FNWL_HOST_004 extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws Exception {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);
		String strReqBody = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;
		String apiRespCode = Constants.EmptyString;
		String region=null;
		
		try {
			
			HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
			
			if (null != data.getSessionData(Constants.S_FNWL_MDM_PCN_LOOKUP_URL) && data.getSessionData(Constants.S_READ_TIMEOUT) != null && data.getSessionData(Constants.S_CONN_TIMEOUT) != null &&	data.getSessionData(Constants.S_ANI) != null) {
				String url = (String) data.getSessionData(Constants.S_FNWL_MDM_PCN_LOOKUP_URL);
				String conntimeout = (String) data.getSessionData(Constants.S_CONN_TIMEOUT);
				String readtimeout = (String) data.getSessionData(Constants.S_READ_TIMEOUT);
				String Callid = (String) data.getSessionData(Constants.S_CALLID);
				LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
				String lookupType = "POLICY";
				String PolicyNum = null != (String) data.getSessionData(Constants.FNWL_MN_003_VALUE) ? (String) data.getSessionData(Constants.FNWL_MN_003_VALUE) : Constants.DEFAULT;
				PolicyNum = PolicyNum.toUpperCase();
				PolicyNum = PolicyNum.replaceAll("[^a-zA-Z0-9 ]", "");
				
				data.addToLog(currElementName, "Call ID :: " + Callid);
				data.addToLog(currElementName, "readTimeOut :: " + readtimeout);
				data.addToLog(currElementName, "connTimeOut :: " + conntimeout);
				data.addToLog(currElementName, "URL :: " + url);
				data.addToLog(currElementName, "LookupType :: " + lookupType);
				data.addToLog(currElementName, "Policy Number :: " + PolicyNum);
				
				String prefix = "https://api-np-ss.farmersinsurance.com"; 
				String UAT_FLAG="";
				
				if(url.startsWith(prefix)) {
		        	UAT_FLAG="YES";
		        }
				
                if("YES".equalsIgnoreCase(UAT_FLAG)) {
                	
                region = regionDetails.get(Constants.S_FNWL_MDM_PCN_LOOKUP_URL);
                }
                else {
                	region="PROD";
                }
				
				Lookupcall lookupCall = new Lookupcall();
				JSONObject resp = lookupCall.FNWLMDMLookup_Post(url, Callid, "", PolicyNum, "", "", "", lookupType, region, UAT_FLAG, Integer.parseInt(conntimeout), Integer.parseInt(readtimeout), context);
				
				if (null != resp && resp.containsKey(Constants.RESPONSE_CODE)) {
					apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
				}
				data.addToLog("FNWL MDM via Policy Number Resp :: ", resp.toString());
				
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
			data.addToLog(currElementName,"Exception in FNWL_HOST_004 API Call :: " + e);
			caa.printStackTrace(e);
		}
		
		try {
			objHostDetails.startHostReport(currElementName,"FNWL_MDM Lookup by PolicyNumber", strReqBody, region, (String) data.getSessionData(Constants.S_FNWL_MDM_PCN_LOOKUP_URL));
			objHostDetails.endHostReport(data, strRespBody , strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode, null == data.getSessionData("NoofPolicies") || data.getSessionData("NoofPolicies").toString().equalsIgnoreCase("0") ? "0 Policies found" : ((Integer) data.getSessionData("NoofPolicies")).toString().equalsIgnoreCase("1") ? "1 Policy found|Product Types: " + (HashSet<String>) data.getSessionData("policyproductTypeList") : ((Integer) data.getSessionData("NoofPolicies")).toString() + " policies found|Product Types: " + (HashSet<String>) data.getSessionData("policyproductTypeList")) ;
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for FNWL MDM Lookup via PCN call  :: " + e);
			caa.printStackTrace(e);
		}
		return strExitState;
	}
	
	public String apiResponseManipulation(DecisionElementData data, CommonAPIAccess caa, String currElementName, JSONObject resp) {
		String strExitState = Constants.ER;
		JSONArray customersArr = null;
		JSONObject responseBody = null;
		int x =0;
		HashMap<String, PolicyBean_via_PCN> policyMap = new HashMap<String, PolicyBean_via_PCN>();
		
		try {
			String PolicyNum = null != (String) data.getSessionData(Constants.FNWL_MN_003_VALUE) ? (String) data.getSessionData(Constants.FNWL_MN_003_VALUE) : Constants.DEFAULT;
			
			if (null != resp) {
				
				if (resp.containsKey("responseBody")) {
					responseBody = (JSONObject) resp.get("responseBody");
					
					if (responseBody.containsKey("customers")) {
						
						customersArr = (JSONArray) responseBody.get("customers");
						
						if (null != customersArr) {
							
							if (customersArr.size() > 0) {
								data.addToLog(currElementName, "customers Array size is greater than 0 :: customer/customer found");
								
								for (int i = 0; i < customersArr.size(); i++) {
									
									JSONObject customer = (JSONObject) customersArr.get(i);
									JSONArray addressArr = (boolean) customer.containsKey("addresses") ? (JSONArray) customer.get("addresses") : new JSONArray();
									JSONObject address = null != addressArr && addressArr.size() > 0 ? (JSONObject) addressArr.get(0) : new JSONObject();
									JSONArray policiesArr = (boolean) customer.containsKey("policies") ? (JSONArray) customer.get("policies") : null;
									JSONObject custDetails = (JSONObject) customer.get("legalName");
									
									if (null != policiesArr && policiesArr.size() > 0) {
										
										data.addToLog(currElementName, "Policy Array size is greater than 0 :: Policy/Policies found for customer :: " + custDetails.get("firstName") + " " + custDetails.get("lastName"));
										
										for (int j = 0; j < policiesArr.size(); j++) {
											
											x = x + 1;
											
											JSONObject policy = (JSONObject) policiesArr.get(j);

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
											
											PolicyBean_via_PCN policyInfo = new PolicyBean_via_PCN();
											
			                                policyInfo.setPolicyType(null != (String) policy.get("policyType") ? (String) policy.get("policyType") : Constants.EmptyString);
											policyInfo.setPolicyCategory(null != (String) policy.get("policyCategory") ? (String) policy.get("policyCategory") : Constants.EmptyString);
											policyInfo.setPolicyStateCode(null != (String) policy.get("policyStateCode") ? (String) policy.get("policyStateCode") : Constants.EmptyString);
											policyInfo.setPolicySource(null != (String) policy.get("policySource") ? (String) policy.get("policySource") : Constants.EmptyString);
											policyInfo.setPolicyNumber(null != (String) policy.get("policyNumber") ? (String) policy.get("policyNumber") : Constants.EmptyString);
											policyInfo.setIssueDate(null != (String) policy.get("issueDate") ? (String) policy.get("issueDate") : Constants.EmptyString);
											policyInfo.setPolicyStatus(null != (String) policy.get("policyStatus") ? (String) policy.get("policyStatus") : Constants.EmptyString);
											policyInfo.setLineOfBusiness(null != (String) policy.get("lineOfBusiness") ? (String) policy.get("lineOfBusiness") : Constants.EmptyString);
											policyInfo.setBookOfBusiness(null != (String) policy.get("bookOfBusiness") ? (String) policy.get("bookOfBusiness") : Constants.EmptyString);
											policyInfo.setRoles(rolesList);
											policyInfo.setDateOfBirth(null != (String) customer.get("dateOfBirth") ? (String) customer.get("dateOfBirth") : Constants.EmptyString);
											policyInfo.setZip(null != (String) address.get("zip") ? (String) address.get("zip") : Constants.EmptyString);
											policyInfo.setSsn( null != (String) customer.get("ssn") ? ((String) customer.get("ssn")) : Constants.EmptyString);
											policyInfo.setAddressType(null != (String) address.get("type") ? (String) address.get("type") : Constants.EmptyString);
											policyInfo.setAddressLine1(null != (String) address.get("addressLine1") ? (String) address.get("addressLine1") : Constants.EmptyString);
											policyInfo.setCity(null != (String) address.get("city") ? (String) address.get("city") : Constants.EmptyString);
											policyInfo.setState(null != (String) address.get("state") ? (String) address.get("state") : Constants.EmptyString);
											policyInfo.setCountry(null != (String) address.get("country") ? (String) address.get("country") : Constants.EmptyString);
											policyInfo.setFirstName(null != (String) custDetails.get("firstName") ? (String) custDetails.get("firstName") : Constants.EmptyString);
											policyInfo.setLastName(null != (String) custDetails.get("lastName") ? (String) custDetails.get("lastName") : Constants.EmptyString);
											policyInfo.setTaxID(null != (String) customer.get("taxID") ? (String) customer.get("taxID") : Constants.EmptyString);
											
											policyMap.put("POLICY" + String.valueOf(x), policyInfo);
										}
									}
									else {
										data.addToLog(currElementName, "Policy Array is null :: No policies found for current customer");
									}
								}
								
								data.setSessionData(Constants.FNWL_POLICYMAP_VIA_PCN, policyMap);
								data.addToLog(currElementName, "Policy via PCN HashMap :: " + policyMap);
								data.addToLog(currElementName, "Policy Storage via PCN Hashmap size :: " + policyMap.size());
								
								//Iterate through the PolicyMap formed for further processing - Start 
								Set<String> uniquePolicyNumbers = new HashSet<>();
								
								for (HashMap.Entry<String, PolicyBean_via_PCN> entry : policyMap.entrySet()) {
									PolicyBean_via_PCN policyDet = entry.getValue();
									data.addToLog(currElementName, "Iterating through Policy Map via PCN");
									
									uniquePolicyNumbers.add(policyDet.getPolicyNumber());
									
									if (null != policyDet && policyDet.getPolicyNumber().equalsIgnoreCase(PolicyNum)) {
										
										data.setSessionData(Constants.FNWL_POLICY_FOUND_VIA_POLICY, Constants.TRUE);
										data.setSessionData(Constants.FNWL_POLICY_NUM, policyDet.getPolicyNumber());
										
										String policyType = policyDet.getPolicyType();
										String lineOfBusiness = policyDet.getLineOfBusiness();
										String bookOfBusiness = policyDet.getBookOfBusiness();
										String issueDate = policyDet.getIssueDate();
										
										checkifIssueDateisBeforeComparisonDate(data, currElementName, caa, issueDate);
										checkPolicyType(data, currElementName, caa, policyType);
										checkPolicyLOB(data, currElementName, caa, lineOfBusiness);
										checkPolicyBookType(data, currElementName, caa, bookOfBusiness);
									}
									else {
										data.addToLog(currElementName, "PolicyBean either null or doesn't contain infmoration relating to user entered policy number");
									}
								}
								if (uniquePolicyNumbers.size() == 1) {
									data.setSessionData(Constants.FNWL_SINGLE_MDM_POLICY, Constants.TRUE);
									data.addToLog(currElementName, "Single Policy Found in search via PCN");
								}
								data.setSessionData("NoofPolicies", uniquePolicyNumbers.size());
								//Iterate through the PolicyMap formed for further processing - End
								
								checkifPolicyfoundviaANILookup(data, currElementName, caa, PolicyNum);
							}
							else {
								data.addToLog(currElementName, "customers array size is not greater than zero");
							}
						}
						else{
							data.addToLog(currElementName, "customers array is null");
						}
					}
					else {
						data.addToLog(currElementName, "Response Body does not contain customers array");
					}
				} else {
					data.addToLog(currElementName, "Response does not contain Response Body");
				}
			}
			else {
				data.addToLog(currElementName, "Response Body is null");
			}
			strExitState = Constants.SU;
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in FNWL_HOST_004 apiResponseManipulation method  :: " + e);
			caa.printStackTrace(e);
		}
		
		return strExitState;
	}
	
	public void checkifIssueDateisBeforeComparisonDate(DecisionElementData data, String currElementName, CommonAPIAccess caa, String IssueDate) {
		
		try {
			DateTimeFormatter issueDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			DateTimeFormatter comparisonDateFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
			
			if (null != IssueDate && !Constants.EmptyString.equalsIgnoreCase(IssueDate)) {
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
			data.addToLog(currElementName,"Exception in FNWL_HOST_004 checkifIssueDateisBeforeComparisonDate method  :: " + e);
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
			data.addToLog(currElementName,"Exception in FNWL_HOST_004 checkpolicyType method  :: " + e);
			caa.printStackTrace(e);
		}
	}

	public void checkPolicyLOB(DecisionElementData data, String currElementName, CommonAPIAccess caa, String lineOfBusiness) {

		try {
			if (null != lineOfBusiness && Constants.FNWL_LIFE.equalsIgnoreCase(lineOfBusiness)) {
				data.setSessionData(Constants.FNWL_POLICY_LOB, Constants.TRUE);
				data.addToLog(currElementName, "Policy LOB :: " + lineOfBusiness);
			}
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in FNWL_HOST_004 checkpolicyLOB method  :: " + e);
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
			data.addToLog(currElementName,"Exception in FNWL_HOST_004 checkPolicyBookType method  :: " + e);
			caa.printStackTrace(e);
		}
	}
	
	public void checkifPolicyfoundviaANILookup(DecisionElementData data, String currElementName, CommonAPIAccess caa, String PolicyNum) {
		
		try {
			//Check if same policy was found via ANI as well - Start
			HashMap<String, PolicyBean_via_ANI> policyMapviaANI = (HashMap<String, PolicyBean_via_ANI>) data.getSessionData(Constants.FNWL_POLICYMAP_VIA_ANI);
			data.addToLog(currElementName, "Checking ANI Map to see if the same policy was found via ANI");
			data.addToLog(currElementName, "PolicyMap via ANI ::" + policyMapviaANI);
			boolean policyfound = false;
			
			if (null != policyMapviaANI) {
				for (HashMap.Entry<String, PolicyBean_via_ANI> entry1 : policyMapviaANI.entrySet()) {
					data.addToLog(currElementName, "Iterating through ANI Map");
					PolicyBean_via_ANI policyBeanObj = entry1.getValue();
					
					if (PolicyNum.equalsIgnoreCase(policyBeanObj.getPolicyNumber())) {
						data.setSessionData(Constants.FNWL_POLICY_FOUND_VIA_ANI, Constants.TRUE);
						policyfound = true;
						data.addToLog(currElementName, "Policy is found in ANI Map :: FNWL_POLICY_FOUND_VIA_ANI Flag :: TRUE");
					}
					else{
						if (policyfound) {
							data.addToLog(currElementName, "Policy alread found in ANI Hashmap :: Keeping FNWL_POLICY_FOUND_VIA_ANI Flag TRUE");
						}
						else {
							data.setSessionData(Constants.FNWL_POLICY_FOUND_VIA_ANI, Constants.FALSE);
							data.addToLog(currElementName, "Policy not found in ANI Map :: FNWL_POLICY_FOUND_VIA_ANI Flag :: FALSE");
						}
					}
				}
			}
			//Check if same policy is found via ANI as well - End
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in FNWL_HOST_004 checkPolicyfoundviaANILookup method  :: " + e);
			caa.printStackTrace(e);
		}
	}
}
