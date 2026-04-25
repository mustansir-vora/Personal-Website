package com.farmers.cancelExpress;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.bean.PolicyBean;
import com.farmers.client.Lookupcall;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class MulesoftPolicyLookup extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);
		String strReqBody = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;
		String CACallers = "";
		try {
		
	            strExitState = mulesoftFarmerPolicyInquiry(strRespBody, strRespBody, data, caa, currElementName);
	        
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception while forming host details for SIDA_HOST_001  :: " + e);
			caa.printStackTrace(e);
		}

		/*
		 * try { objHostDetails.startHostReport(currElementName,"SIDA_HOST_001",
		 * strReqBody); objHostDetails.endHostReport(data,strRespBody ,
		 * strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU); }
		 * catch (Exception e) { data.addToLog(
		 * currElementName,"Exception while forming host reporting for SIDA_HOST_001  PolicyInquiryAPI call  :: "
		 * +e); caa.printStackTrace(e); }
		 */
		return strExitState;
	}
	private String mulesoftFarmerPolicyInquiry(String strRespBody, String strRespBody2, DecisionElementData data,
			CommonAPIAccess caa, String currElementName) {
		String strExitState = Constants.ER, strReqBody = Constants.EMPTY;
		SetHostDetails objHostDetails = new SetHostDetails(caa);
		objHostDetails.setinitalValue();
		boolean startsWithAlphabets = false;
		String apiRespCode = Constants.EmptyString;
		String region = null;
		HashMap<String, String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);

		try {
			if (data.getSessionData(Constants.S_MULESOFT_FARMER_POLICYINQUIRY_URL) != null) {
				String wsurl = (String) data.getSessionData(Constants.S_MULESOFT_FARMER_POLICYINQUIRY_URL);
				String policyContractNumbers = (String) data.getSessionData(Constants.S_POLICY_NUM);
				if (policyContractNumbers != null) {
					startsWithAlphabets = Character.isLetter(policyContractNumbers.charAt(0));
					if (startsWithAlphabets) {
						policyContractNumbers = policyContractNumbers.toUpperCase();
						data.addToLog(currElementName,
								"Policy Number from caller contains Alphabet and so changing the value to Upper Case :: "
										+ policyContractNumbers);
						data.setSessionData(Constants.S_POLICY_NUM, policyContractNumbers);
					} else {
						data.addToLog(currElementName,
								"Policy Number from caller does not contains Alphabet and so Setting the same value "
										+ policyContractNumbers);
					}
				}
				String billingAccountNumber = (String) data.getSessionData(Constants.S_BILLING_ACC_NUM);
				String telephoneNumber = (String) data.getSessionData(Constants.S_ANI);
				if (data.getSessionData(Constants.S_TELEPHONENUMBER) != null
						&& !data.getSessionData(Constants.S_TELEPHONENUMBER).equals("")) {
					telephoneNumber = (String) data.getSessionData(Constants.S_TELEPHONENUMBER);
				}
				String conntimeout = (String) data.getSessionData(Constants.S_CONN_TIMEOUT);
				String readTimeout = (String) data.getSessionData(Constants.S_READ_TIMEOUT);
				String tid = (String) data.getSessionData(Constants.S_CALLID);
				LoggerContext context = (LoggerContext) data.getApplicationAPI()
						.getApplicationData(Constants.A_CONTEXT);
				// https://api-ss.farmersinsurance.com/plcyms/v3/policies?operation=searchByCriteria
				Lookupcall lookups = new Lookupcall();

				// UT ENV CHANGE START(SHAIK,PIYA)
				data.addToLog("API URL: ", wsurl);
				String prefix = "https://api-np-ss.farmersinsurance.com";
				String UAT_FLAG = "";
				if (wsurl.startsWith(prefix)) {
					UAT_FLAG = "YES";
				}
				if ("YES".equalsIgnoreCase(UAT_FLAG)) {
					region = regionDetails.get(Constants.S_MULESOFT_FARMER_POLICYINQUIRY_URL);
				} else {
					region = "PROD";
				}
				data.addToLog(currElementName, "policyContractNumbers : " + policyContractNumbers);
				data.addToLog(currElementName, "billingAccountNumber : " + billingAccountNumber);
				data.addToLog(currElementName, "telephoneNumber : " + telephoneNumber);

				org.json.simple.JSONObject responses = null;
				if (policyContractNumbers != null && !policyContractNumbers.equals("")) {

					responses = lookups.GetMulesoftFarmerPolicyInquiry(wsurl, tid, policyContractNumbers, null, null,
							Integer.parseInt(conntimeout), Integer.parseInt(readTimeout), context, region, UAT_FLAG);
				} else if (billingAccountNumber != null && !billingAccountNumber.equals("")) {
					responses = lookups.GetMulesoftFarmerPolicyInquiry(wsurl, tid, null, billingAccountNumber, null,
							Integer.parseInt(conntimeout), Integer.parseInt(readTimeout), context, region, UAT_FLAG);
				} else {
					responses = lookups.GetMulesoftFarmerPolicyInquiry(wsurl, tid, null, null, telephoneNumber,
							Integer.parseInt(conntimeout), Integer.parseInt(readTimeout), context, region, UAT_FLAG);
				}

				data.addToLog("responses", responses.toString());

				data.addToLog("responses", responses.toString());

				// Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) responses.get(Constants.RESPONSE_CODE));
				if (responses != null) {
					if (responses.containsKey(Constants.REQUEST_BODY))
						strReqBody = responses.get(Constants.REQUEST_BODY).toString();
					if (responses.containsKey(Constants.RESPONSE_CODE)
							&& (int) responses.get(Constants.RESPONSE_CODE) == 200
							&& responses.containsKey(Constants.RESPONSE_BODY)) {
						data.addToLog(currElementName,
								"Set SIDA_HOST_001 : MulesoftFarmerPolicyInquiry_Post API Response into session with the key name of "
										+ currElementName + Constants._RESP);
						strRespBody = responses.get(Constants.RESPONSE_BODY).toString();
						data.setSessionData(currElementName + Constants._RESP, responses.get(Constants.RESPONSE_BODY));
						data.setSessionData("SIDA_MN_005_VALUE_RESP", responses.get(Constants.RESPONSE_BODY));
						// CALLER VERIFICATION
						data.setSessionData("SIDA_MN_005_VALUE_RESP_CUST_NEW", responses.get(Constants.RESPONSE_BODY));

						data.addToLog("After setting the value in SIDA_MN_005_VALUE_RESP_CUST_NEW",
								(String) data.getSessionData("SIDA_MN_005_VALUE_RESP_CUST_NEW").toString());

						strExitState = apiResponseManupulation_MulesoftFarmerPolicyInquiry(data, caa, currElementName,
								strRespBody);
					} else {
						if (responses.get(Constants.RESPONSE_MSG) != null)
							strRespBody = responses.get(Constants.RESPONSE_MSG).toString();
					}
				}
			}
		} catch (Exception e) {
			data.addToLog(currElementName,
					"Exception in SIDA_HOST_001  MulesoftFarmerPolicyInquiry_Post API call  :: " + e);
			caa.printStackTrace(e);
		}
		try {
			if (null != data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE)
					&& (Constants.S_SINGLE_POLICY_FOUND.equalsIgnoreCase(
							(String) data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE))
							|| Constants.S_MULTIPLE_POLICIES_SINGLE_PRODUCTTYPE_FOUND.equalsIgnoreCase(
									(String) data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE))
							|| Constants.S_MULTIPLE_POLICIES_MULTIPLE_PRODUCTTYPE_FOUND.equalsIgnoreCase(
									(String) data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE)))) {
				objHostDetails.startHostReport(currElementName, "MulesoftFarmerPolicyInquiry_Post API call", strReqBody,
						region, (String) data.getSessionData(Constants.S_MULESOFT_FARMER_POLICYINQUIRY_URL));
				objHostDetails.endHostReport(data, strRespBody,
						strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,
						"");
			} else {
				objHostDetails.startHostReport(currElementName, "MulesoftFarmerPolicyInquiry_Post API call", strReqBody,
						region, (String) data.getSessionData(Constants.S_MULESOFT_FARMER_POLICYINQUIRY_URL));
				objHostDetails.endHostReport(data, "DB-mismatch : " + strRespBody,
						strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,
						"");
			}

		} catch (Exception e) {
			data.addToLog(currElementName,
					"Exception while forming host reporting for MulesoftFarmerPolicyInquiry_Post API call  :: " + e);
			caa.printStackTrace(e);
		}

		return strExitState;

	}
	
	private String apiResponseManupulation_MulesoftFarmerPolicyInquiry(DecisionElementData data, CommonAPIAccess caa,
			String currElementName, String strRespBody) {
		String strExitState = Constants.ER, appTag = Constants.EmptyString;
		try {
			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			JSONArray policiesArr = (JSONArray) resp.get("policies");
			if (policiesArr != null && !policiesArr.isEmpty()) {
				Iterator<Object> iterator = policiesArr.iterator();
				while (iterator.hasNext()) {
					JSONObject policyData = (JSONObject) iterator.next();
					if (policyData.containsKey("policySource")) {
						String policySource = (String) policyData.get("policySource");
						if (policySource.contains("ARS") || policySource.contains("360")) {
							iterator.remove();
						}
					}
				}
				data.addToLog(currElementName, "No. of polices after removing FWS polices : " + policiesArr.size());
			}

			String lob = null;
			String prompt = Constants.EmptyString;
			int autoprodtypecount = 0;
			int umbrellaprodtypecount = 0;
			int homeprodtypecount = 0;
			boolean singleproducttype = false;
			String agentAORid = Constants.EmptyString;
			String strPolicySource = Constants.EmptyString;
			String strPolicyGPC = Constants.EmptyString;

			String apiSubcategory = Constants.EmptyString;
			String strsubcategory = Constants.EmptyString;
			String strpayPlan = Constants.EmptyString;
			String strPolicyStatus = Constants.EmptyString;

			if (policiesArr != null && policiesArr.size() > 0) {
				data.setSessionData(Constants.S_NOOF_POLICIES_LOOKUP, policiesArr.size());
				data.setSessionData(Constants.IS_MORETHAN_ZERO_POLICIES, Constants.STRING_YES);
				data.setSessionData(Constants.CCAI_Customer_Policy_Number, (String)data.getSessionData("FEAW_MN_001_VALUE"));
				data.setSessionData("IS_CALLED_SHARED_ID_AUTH", "TRUE");
				Map<String, Integer> productTypeCounts = new HashMap<>();
				HashMap<String, HashMap<String, PolicyBean>> productPolicyMap = new HashMap<String, HashMap<String, PolicyBean>>();

				for (int i = 0; i < policiesArr.size(); i++) {
					JSONObject policyData = (JSONObject) policiesArr.get(i);
					PolicyBean beanObj = new PolicyBean();
					String strPolicyNum = Constants.EmptyString;
					if (policyData.containsKey("lineOfBusiness")) {
						lob = (String) policyData.get("lineOfBusiness");
						if (Constants.PRODUCTTYPE_F.equalsIgnoreCase(lob)) {
							lob = Constants.PRODUCTTYPE_H;
						}
						if (productTypeCounts.containsKey(lob))
							productTypeCounts.put(lob, productTypeCounts.get(lob) + 1);
						else
							productTypeCounts.put(lob, 1);
					}

					if (policyData.containsKey("address")) {
						JSONArray addressesArr = (JSONArray) policyData.get("address");
						if (addressesArr != null && addressesArr.size() > 0) {
							JSONObject addressesObj = (JSONObject) addressesArr.get(0);
							if (addressesObj.containsKey("zip"))
								beanObj.setStrZipCode((String) addressesObj.get("zip"));
							if (addressesObj.containsKey("state"))
								beanObj.setStrPolicyState((String) addressesObj.get("state"));
						} else {
							data.addToLog(currElementName, "Address Array is null or empty..");
						}

						// CS1151307 : Update Policy State Name(Policy State : WY, PolicyStateName :
						// Wyoming)
						String strPolicyState = (String) beanObj.getStrPolicyState();
						data.setSessionData(Constants.S_POLICY_STATE_CODE, strPolicyState);
						data.addToLog(currElementName, "Policy State Code :" + strPolicyState);
					}

					String strDOB = "";
					if (policyData.containsKey("insuredDetails")) {
						JSONArray insuredDetailsArr = (JSONArray) policyData.get("insuredDetails");
						for (int k = 0; k < insuredDetailsArr.size(); k++) {
							JSONObject obj = (JSONObject) insuredDetailsArr.get(k);
							strDOB = (strDOB.equals("")) ? "" + obj.get("birthDate")
									: strDOB + "," + obj.get("birthDate");
						}
						beanObj.setStrDOB(strDOB);
					}

					if (policyData.containsKey("policyNumber")) {
						strPolicyNum = (String) policyData.get("policyNumber");
					}

					if (policyData.containsKey("billingAccountNumber")
							&& null != policyData.get("billingAccountNumber")) {
						beanObj.setStrBillingAccountNumber((String) policyData.get("billingAccountNumber"));
					}

					if (policyData.containsKey("policySource")) {
						strPolicySource = (String) policyData.get("policySource");
						data.addToLog(data.getCurrentElement(), "Set policy source into session  "
								+ Constants.S_POLICY_SOURCE + " : " + strPolicySource);
						data.setSessionData(Constants.S_POLICY_SOURCE, strPolicySource);
						beanObj.setStrPolicySource(strPolicySource);
					}

					if (policyData.containsKey("policyStatus") && null != policyData.get("policyStatus")) {
						strPolicyStatus = (String) policyData.get("policyStatus");
						beanObj.setPolicyStatus(strPolicyStatus);
					}


					String tmpKey = Constants.EmptyString;
					switch (lob) {
					case Constants.PRODUCTTYPE_H:
						tmpKey = Constants.HOME_PRODUCTTYPECOUNT_KYC;
						break;
					case Constants.PRODUCTTYPE_A:
						tmpKey = Constants.AUTO_PRODUCTTYPECOUNT_KYC;
						break;
					case Constants.PRODUCTTYPE_U:
						tmpKey = Constants.UMBRELLA_PRODUCTTYPECOUNT_KYC;
						break;
					case Constants.PRODUCTTYPE_F:
						tmpKey = Constants.HOME_PRODUCTTYPECOUNT_KYC;
						break;
					default:
						break;
					}
					if (productPolicyMap.containsKey(tmpKey)) {
						HashMap<String, PolicyBean> tmpPolicyDetails = productPolicyMap.get(tmpKey);
						tmpPolicyDetails.put(strPolicyNum, beanObj);
						productPolicyMap.put(tmpKey, tmpPolicyDetails);
					} else {
						HashMap<String, PolicyBean> policyDetails = new HashMap<String, PolicyBean>();
						policyDetails.put(strPolicyNum, beanObj);
						productPolicyMap.put(tmpKey, policyDetails);
					}
				}

				data.setSessionData(Constants.S_POLICYDETAILS_MAP, productPolicyMap);
				data.addToLog(currElementName, "Product Type Count Hashmap : " + productTypeCounts);
				data.addToLog(currElementName, "policyDetails Hashmap : " + productPolicyMap);

				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_H)
						|| productTypeCounts.containsKey(Constants.PRODUCTTYPE_F)) {
					homeprodtypecount = productTypeCounts.get(Constants.PRODUCTTYPE_H);
				}
				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_A)) {
					autoprodtypecount = productTypeCounts.get(Constants.PRODUCTTYPE_A);
				}
				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_U)) {
					umbrellaprodtypecount = productTypeCounts.get(Constants.PRODUCTTYPE_U);
				}

				data.setSessionData(Constants.AUTO_PRODUCTTYPECOUNT_KYC, autoprodtypecount);
				data.setSessionData(Constants.HOME_PRODUCTTYPECOUNT_KYC, homeprodtypecount);
				data.setSessionData(Constants.UMBRELLA_PRODUCTTYPECOUNT_KYC, umbrellaprodtypecount);

				data.addToLog(currElementName,
						"Auto Product Type Count  = " + data.getSessionData(Constants.AUTO_PRODUCTTYPECOUNT_KYC));
				data.addToLog(currElementName,
						"Home Product Type Count  = " + data.getSessionData(Constants.HOME_PRODUCTTYPECOUNT_KYC));
				data.addToLog(currElementName, "Umbrella Product Type Count = "
						+ data.getSessionData(Constants.UMBRELLA_PRODUCTTYPECOUNT_KYC));

				singleproducttype = checkifsingleproducttype(0, autoprodtypecount, homeprodtypecount,
						umbrellaprodtypecount, 0, 0);
				data.addToLog(currElementName, "Check if single Product : " + singleproducttype);

				if (policiesArr.size() == 1) {
					data.setSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE, Constants.S_SINGLE_POLICY_FOUND);
					data.addToLog(currElementName,
							"S_NOOF_POLICIES_ANILOOKUP_EXITSTATE : " + Constants.S_SINGLE_POLICY_FOUND);
					for (String key : productPolicyMap.keySet()) {
						HashMap<String, PolicyBean> policyDetailsMap = productPolicyMap.get(key);
						for (String key1 : policyDetailsMap.keySet()) {
							PolicyBean obj = (PolicyBean) policyDetailsMap.get(key1);
							if ((null == data.getSessionData(Constants.S_API_DOB)
									|| ((String) data.getSessionData(Constants.S_API_DOB)).isEmpty())
									&& (null == data.getSessionData(Constants.S_API_ZIP)
											|| ((String) data.getSessionData(Constants.S_API_ZIP)).isEmpty())) {
								data.setSessionData(Constants.S_API_DOB, obj.getStrDOB());
								data.setSessionData(Constants.S_API_ZIP, obj.getStrZipCode());
								data.addToLog(currElementName, "DOB: " + obj.getStrDOB());
								data.addToLog(currElementName, "ZIP: " + obj.getStrZipCode());
							} else {
								data.addToLog(currElementName, "DOB and ZIP is not empty or null " + ".");
							}
							data.setSessionData(Constants.S_POLICY_NUM, key1);
							data.setSessionData(Constants.S_POLICY_STATE_CODE, obj.getStrPolicyState());
							data.setSessionData(Constants.S_FINAL_POLICY_OBJ, obj);
							data.setSessionData(Constants.S_POLICY_SOURCE, obj.getStrPolicySource());
							if (null == data.getSessionData(Constants.S_MDM_POLICY_STATUS)) {
								data.setSessionData(Constants.S_MDM_POLICY_STATUS, obj.getPolicyStatus());
								data.addToLog("Policy Status", obj.getPolicyStatus());
							} else {
								data.addToLog("Policy status is not empty", "");
							}
						}
					}
					data.addToLog(currElementName,
							"Value of S_POLICY_STATE_CODE : " + data.getSessionData(Constants.S_POLICY_STATE_CODE));
					
					if (policiesArr.size() == 1) {

						data.setSessionData("S_NOOF_POLICIES_ACCLINKANI_EXITSTATE", Constants.S_SINGLE_POLICY_FOUND);
						data.addToLog(currElementName, "S_NOOF_POLICIES_ACCLINKANI_EXITSTATE : "
								+ data.getSessionData("S_NOOF_POLICIES_ACCLINKANI_EXITSTATE"));

						strExitState = Constants.SU;
					}
					
				} else if (!singleproducttype) {
					data.setSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE,
							Constants.S_MULTIPLE_POLICIES_MULTIPLE_PRODUCTTYPE_FOUND);
					data.addToLog(currElementName, "S_NOOF_POLICIES_ANILOOKUP_EXITSTATE : "
							+ data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE));

					strExitState = Constants.SU;
				} else if (policiesArr.size() > 1 && singleproducttype) {


					data.setSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE,
							Constants.S_MULTIPLE_POLICIES_SINGLE_PRODUCTTYPE_FOUND);
					data.addToLog(currElementName, "S_NOOF_POLICIES_ANILOOKUP_EXITSTATE : "
							+ data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE));

					strExitState = Constants.SU;
				}
			} else {
				strExitState = Constants.ER;
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in AccLinkAniLookup method  :: " + e);
			caa.printStackTrace(e);
		}
		return strExitState;

	}
	public boolean checkifsingleproducttype(int rvprodtypecount, int autoprodtypecount, int homeprodtypecount,
			int umbrellaprodtypecount, int mrprodtypecount, int spprodtypecount) {
		int nonZeroCount = 0;
		if (rvprodtypecount != 0) {
			nonZeroCount++;
		}
		if (autoprodtypecount != 0) {
			nonZeroCount++;
		}
		if (homeprodtypecount != 0) {
			nonZeroCount++;
		}
		if (umbrellaprodtypecount != 0) {
			nonZeroCount++;
		}
		if (mrprodtypecount != 0) {
			nonZeroCount++;
		}
		if (spprodtypecount != 0) {
			nonZeroCount++;
		}
		// Return true only if there's exactly one non-zero element
		return nonZeroCount == 1;
	}

}

