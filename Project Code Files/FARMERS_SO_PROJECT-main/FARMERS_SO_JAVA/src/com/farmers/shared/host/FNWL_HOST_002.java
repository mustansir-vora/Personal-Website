package com.farmers.shared.host;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.client.Lookupcall;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;
import com.farmers.bean.FNWL.PolicyBean_via_ANI;
import com.farmers.bean.FNWL.PolicyBean_via_PCN;

public class FNWL_HOST_002 extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws Exception {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);
		String strReqBody = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;
		JSONObject RespBody = null;
		String apiRespCode = Constants.EmptyString;
		String region=null;
		
		try {
			
			HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
			
			if (null != data.getSessionData(Constants.S_FNWL_MDM_ANI_LOOKUP_URL) && data.getSessionData(Constants.S_READ_TIMEOUT) != null && data.getSessionData(Constants.S_CONN_TIMEOUT) != null &&	data.getSessionData(Constants.S_ANI) != null) {
				
				String url = (String) data.getSessionData(Constants.S_FNWL_MDM_ANI_LOOKUP_URL);
				String ani = (String) data.getSessionData(Constants.S_ANI);
				ani = null != ani && ani.length() > 10 ? ani.substring(ani.length(), ani.length() - 10) : ani;
				String conntimeout = (String) data.getSessionData(Constants.S_CONN_TIMEOUT);
				String readtimeout = (String) data.getSessionData(Constants.S_READ_TIMEOUT);
				String Callid = (String) data.getSessionData(Constants.S_CALLID);
				LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
				String lookupType = "ANI";
				if (null != ani) {
					String anipart1 = ani.substring(0, 3);
					String anipart2 = ani.substring(3, 6);
					String anipart3 = ani.substring(6);
					
					ani = anipart1 + "-" + anipart2 + "-" + anipart3;
					data.addToLog(currElementName, "ANI being passed to API post manipulation :: " + ani);
				}
				
				data.addToLog(currElementName, "Call ID :: " + Callid);
				data.addToLog(currElementName, "readTimeOut :: " + readtimeout);
				data.addToLog(currElementName, "connTimeOut :: " + conntimeout);
				data.addToLog(currElementName, "ANI :: " + ani);
				data.addToLog(currElementName, "URL :: " + url);
				data.addToLog(currElementName, "LookupType :: " + lookupType);
				
				String prefix = "https://api-np-ss.farmersinsurance.com"; 
				String UAT_FLAG="";
				
				if(url.startsWith(prefix)) {
		        	UAT_FLAG="YES";
		        }
				
                if("YES".equalsIgnoreCase(UAT_FLAG)) {
                	
                region = regionDetails.get(Constants.S_FNWL_MDM_ANI_LOOKUP_URL);
                }
                else {
                	region="PROD";
                }
				
				Lookupcall lookupCall = new Lookupcall();
				JSONObject resp = lookupCall.FNWLMDMLookup_Post(url, Callid, ani, "", "", "", "", lookupType, region, UAT_FLAG, Integer.parseInt(conntimeout), Integer.parseInt(readtimeout), context);
				
				if (null != resp && resp.containsKey(Constants.RESPONSE_CODE)) {
					apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
				}
				data.addToLog("FNWL MDM via ANI Resp :: ", resp.toString());
				
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
						data.setSessionData(Constants.FNWL_MDM_VIA_ANI_CALLED, "TRUE");
						if ((int) resp.get(Constants.RESPONSE_CODE) == 400) {
							RespBody = (JSONObject) new JSONParser().parse((String) resp.get(Constants.RESPONSE_MSG));
						}
						else {
							RespBody = (JSONObject) resp.get(Constants.RESPONSE_BODY);
						}
						//data.setSessionData(Constants.FNWL_HOST_001_RESP, RespBody);
						strExitState = apiResponseManipulation(data, caa, currElementName, RespBody);
					}
					else {
						data.addToLog(currElementName, "Did not enter if cond");
					}
				}
			}
			
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in FNWL_HOST_002 API Call :: " + e);
			caa.printStackTrace(e);
		}
		
		try {
			objHostDetails.startHostReport(currElementName,"FNWL_MDM Lookup by ANI", strReqBody, region, (String) data.getSessionData(Constants.S_FNWL_MDM_ANI_LOOKUP_URL));
			objHostDetails.endHostReport(data, strRespBody , strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode, null == data.getSessionData("NoofPolicies") || data.getSessionData("NoofPolicies").toString().equalsIgnoreCase("0") ? "0 Policies found" : ((Integer) data.getSessionData("NoofPolicies")).toString().equalsIgnoreCase("1") ? "1 Policy found|Product Types: " + (HashSet<String>) data.getSessionData("policyproductTypeList") : ((Integer) data.getSessionData("NoofPolicies")).toString() + " policies found|Product Types: " + (HashSet<String>) data.getSessionData("policyproductTypeList")) ;
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for FNWL MDM Lookup via ANI call  :: " + e);
			caa.printStackTrace(e);
		}
		
		return strExitState;
	}
	
	public String apiResponseManipulation(DecisionElementData data, CommonAPIAccess caa, String currElementName, JSONObject resp) {
		String strExitState = Constants.ER;
		JSONArray customersArr = null;
		HashMap<String, PolicyBean_via_ANI> policyMap = new HashMap<String, PolicyBean_via_ANI>();
		int x = 0;		
		
		try {
			
			if (null != resp) {
				
				if (resp.containsKey("customers")) {
					customersArr = (JSONArray) resp.get("customers");
					
					if (null != customersArr) {
						
						if (customersArr.size() > 0) {
							data.addToLog(currElementName, "customers Array size is greater than 0 :: customer/customers found :: " + customersArr.size());
							
							
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
										
										PolicyBean_via_ANI policyInfo = new PolicyBean_via_ANI();
										
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
							}
							
							Set<String> uniquePolicyNumbers = new HashSet<>();
							
							data.setSessionData(Constants.FNWL_POLICYMAP_VIA_ANI, policyMap);
							data.addToLog(currElementName, "Policy Storage via ANI Hashmap :: " + policyMap);
							
							if (null != policyMap && policyMap.size() > 0) {
								data.setSessionData(Constants.FNWL_POLICY_FOUND_VIA_ANI, Constants.TRUE);
								
								if (policyMap.size() == 1) {
									data.setSessionData(Constants.FNWL_SINGLE_MDM_POLICY, Constants.TRUE);
									data.addToLog(currElementName, "Single Policiy found in MDM ANI Lookup :: " + data.getSessionData(Constants.FNWL_SINGLE_MDM_POLICY));
									
									PolicyBean_via_ANI policyBeanObj = policyMap.values().iterator().next();
									data.setSessionData(Constants.FNWL_POLICY_NUM, policyBeanObj.getPolicyNumber());
								}
								
								for (HashMap.Entry<String, PolicyBean_via_ANI> entry : policyMap.entrySet()) {
									PolicyBean_via_ANI policyDet = entry.getValue();
									uniquePolicyNumbers.add(policyDet.getPolicyNumber());
									data.addToLog(currElementName, "Iterating through Policy Map via ANI");
									
									String policyType = policyDet.getPolicyType();
									checkPolicyType(data, currElementName, caa, policyType);
								}
								data.setSessionData("NoofPolicies", uniquePolicyNumbers.size());
							}
							data.addToLog(currElementName, "Policy Storage via ANI Hashmap size :: " + policyMap.size());
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
			}
			else {
				data.addToLog(currElementName, "Response Body is null");
			}
			strExitState = Constants.SU;
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in FNWL_HOST_002 apiResponseManipulation method  :: " + e);
			caa.printStackTrace(e);
		}
		
		return strExitState;
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

}
