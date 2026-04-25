package com.farmers.shared.host;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

public class WATMDMNANILOOKUP extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);
		String strReqBody = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;
		try {

			String dnisbrand = (String) data.getSessionData(Constants.S_BU);
//			String strBristolCode = (String) data.getApplicationAPI().getApplicationData("A_KYCBA_BRISTOLWEST_LOB");
//			String strFarmersCode = (String) data.getApplicationAPI().getApplicationData("A_FARMERS_LOB");
//			String strForemostCode = (String) data.getApplicationAPI().getApplicationData("A_KYCBA_FOREMOST_LOB");
//			String strFWSCode = (String) data.getApplicationAPI().getApplicationData("A_KYCBA_FWS_LOB");
//			String str21stCode = (String) data.getApplicationAPI().getApplicationData("A_KYCBA_21ST_LOB");

			ArrayList<String> strBristolCode = (ArrayList<String>) data.getSessionData("BRISTOL_LOB_LIST");
			ArrayList<String> strFarmersCode = (ArrayList<String>) data.getSessionData("FARMER_LOB_LIST");
			ArrayList<String> strForemostCode = (ArrayList<String>) data.getSessionData("FOREMOST_LOB_LIST");
			ArrayList<String> strFWSCode = (ArrayList<String>) data.getSessionData("FWS_LOB_LIST");
			ArrayList<String> str21stCode = (ArrayList<String>) data.getSessionData("21st_LOB_LIST");

			data.addToLog(currElementName, " PolicyLookup : strBU :: " + dnisbrand);
			data.addToLog(currElementName, " A_BRISTOL_LOB : " + strBristolCode);
			data.addToLog(currElementName, " A_FARMERS_LOB : " + strFarmersCode);
			data.addToLog(currElementName, " A_FOREMOST_LOB : " + strForemostCode);
			data.addToLog(currElementName, " A_FWS_LOB : " + strFWSCode);
			data.addToLog(currElementName, " A_21ST_LOB : " + str21stCode);

			if (dnisbrand != null && strFarmersCode != null && (strFarmersCode.contains(dnisbrand))) {
				dnisbrand = "FDS";
				data.setSessionData("S_FLAG_FDS_BU", Constants.STRING_YES);
				data.addToLog("S_FLAG_FDS_BU: ", (String) data.getSessionData("S_FLAG_FDS_BU"));
			} else if (dnisbrand != null && strBristolCode != null && strBristolCode.contains(dnisbrand)) {
				dnisbrand = "BW";
				data.setSessionData("S_FLAG_BW_BU", Constants.STRING_YES);
				data.addToLog("S_FLAG_BW_BU: ", (String) data.getSessionData("S_FLAG_BW_BU"));
			} else if (dnisbrand != null && strForemostCode != null && strForemostCode.contains(dnisbrand)) {
				dnisbrand = "FM";
				data.setSessionData("S_FLAG_FOREMOST_BU", Constants.STRING_YES);
				data.addToLog("S_FLAG_FOREMOST_BU: ", (String) data.getSessionData("S_FLAG_FOREMOST_BU"));
			} else if (dnisbrand != null && strFWSCode != null && strFWSCode.contains(dnisbrand)) {
				dnisbrand = "FWS";
				data.setSessionData("S_FLAG_FWS_BU", Constants.STRING_YES);
				data.addToLog("S_FLAG_FWS_BU: ", (String) data.getSessionData("S_FLAG_FWS_BU"));
			} else if (dnisbrand != null && str21stCode != null && str21stCode.contains(dnisbrand)) {
				dnisbrand = "AUTO";
				data.setSessionData("S_FLAG_21ST_BU", Constants.STRING_YES);
				data.addToLog("S_FLAG_21ST_BU: ", (String) data.getSessionData("S_FLAG_21ST_BU"));
			}

			strExitState = MdmPhoneNoPolicyLookup(strRespBody, strRespBody, data, caa, currElementName);

			data.addToLog(currElementName, "dnisbrand :: " + dnisbrand);
		} catch (Exception e) {
			data.addToLog(currElementName,
					"Exception while forming host details for NEWVIAMdmPhoneNoPolicyLookup  :: " + e);
			caa.printStackTrace(e);
		}

		return strExitState;
	}

	private String MdmPhoneNoPolicyLookup(String strRespBody, String strRespBody2, DecisionElementData data,
			CommonAPIAccess caa, String currElementName) {
		String strExitState = Constants.ER, strReqBody = Constants.EMPTY;
		;
		SetHostDetails objHostDetails = new SetHostDetails(caa);
		objHostDetails.setinitalValue();
		boolean startsWithAlphabets = false;
		String policyContractNumber = Constants.EmptyString;
		// START- UAT ENV SETUP CODE (PRIYA, SHAIK)
		HashMap<String, String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
		// END- UAT ENV SETUP CODE (PRIYA, SHAIK)
		String wsurl = null;
		String region = null;
		// Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;

		try {

			// Checking MDM phone number lookup URL is available in session
			if (data.getSessionData(Constants.S_MDM_PHONENO_LOOKUP_URL) != null) {
				wsurl = (String) data.getSessionData(Constants.S_MDM_PHONENO_LOOKUP_URL);
				data.addToLog(currElementName, "S_MDM_PHONENO_LOOKUP_URL : " + wsurl);

				// retrieve ani/telephone number and excluded source
				String telephoneNumber = (String) data.getSessionData(Constants.S_ANI);
				String excludedSource = (String) data.getSessionData(Constants.S_EXCLUDED_SOURCE) != null
						? (String) data.getSessionData(Constants.S_EXCLUDED_SOURCE)
						: "Life";// default excluded source value

				// check if telephone/ani number is available in session
				String newPhoneNumberCollectionFlag = (String) data.getSessionData("S_PHONE_NUMBER_RESULT");
				if (newPhoneNumberCollectionFlag.equalsIgnoreCase("Y")) {
					if (data.getSessionData(Constants.S_TELEPHONENUMBER) != null
							&& !data.getSessionData(Constants.S_TELEPHONENUMBER).equals("")) {
						telephoneNumber = (String) data.getSessionData(Constants.S_TELEPHONENUMBER);
					}
				}

				// retrieve connection timeout value from session or use default
				int connTimeout = data.getSessionData(Constants.S_MDM_PHONENO_LOOKUP_CONN_TIMEOUT) != null
						? Integer.valueOf((String) data.getSessionData(Constants.S_MDM_PHONENO_LOOKUP_READ_TIMEOUT))
						: 12000; // default connection timeout

				// retrieve read time out value from session or use default
				int readTimeout = data.getSessionData(Constants.S_MDM_PHONENO_LOOKUP_CONN_TIMEOUT) != null
						? Integer.valueOf((String) data.getSessionData(Constants.S_MDM_PHONENO_LOOKUP_READ_TIMEOUT))
						: 12000; // default read timeout

				// retrieve call ID from session and logger context from application data
				String tid = (String) data.getSessionData(Constants.S_CALLID);
				LoggerContext context = (LoggerContext) data.getApplicationAPI()
						.getApplicationData(Constants.A_CONTEXT);

				Lookupcall lookups = new Lookupcall();

				// check UAT enviroment flag and set region respectively
				data.addToLog("API URL: ", wsurl);
				String prefix = "https://api-np-ss.farmersinsurance.com";
				String UAT_FLAG = "";
				if (wsurl.startsWith(prefix)) {
					UAT_FLAG = "YES";
				}
				if ("YES".equalsIgnoreCase(UAT_FLAG)) {
					region = regionDetails.get(Constants.S_MDM_PHONENO_LOOKUP_URL);
				} else {
					region = "PROD";
				}
				// Initialize JSON object for response
				org.json.simple.JSONObject responses = null;

				// Perform lookup based on telephone number(ani)

				responses = lookups.GetMdmFWSPhoneNoLookup(wsurl, tid, telephoneNumber, excludedSource, connTimeout,
						readTimeout, context, region, UAT_FLAG);

				data.addToLog("responses", responses.toString());
				// Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) responses.get(Constants.RESPONSE_CODE));

				// Process the response if not null

				if (responses != null) {
					if (responses.containsKey(Constants.REQUEST_BODY)
							&& responses.get(Constants.REQUEST_BODY) != null) {
						strReqBody = responses.get(Constants.REQUEST_BODY).toString();
					}
					if (responses.containsKey(Constants.RESPONSE_CODE) && responses.get(Constants.RESPONSE_CODE) != null
							&& (int) responses.get(Constants.RESPONSE_CODE) == 200
							&& responses.containsKey(Constants.RESPONSE_BODY)
							&& responses.get(Constants.RESPONSE_BODY) != null) {
						// Log and set session data
						Object responseBody = responses.get(Constants.RESPONSE_BODY);
						if (responseBody != null) {
							strRespBody = responseBody.toString();
							data.setSessionData(currElementName + Constants._RESP, responseBody);
							data.setSessionData("MDM_PHONE_NO_LOOKUP_VALUE_RESP", responseBody);
							JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
							strExitState = apiResponseManupulation_MdmPhoneNoPolicyLookup(data, caa, currElementName,
									strRespBody);
						}

					} else {
						// Handle non-200 response
						if (responses.containsKey(Constants.RESPONSE_MSG)
								&& responses.get(Constants.RESPONSE_MSG) != null) {
							strRespBody = responses.get(Constants.RESPONSE_MSG).toString();
						}
					}
				}
			}
		} catch (Exception e) {
			data.addToLog(currElementName,
					"Exception in " + currElementName + "  NEWVIAMdmPhoneNoPolicyLookup API call  :: " + e);
			caa.printStackTrace(e);
		}

		try {
			// MDM total no of policies count
			String totalNoOfPoliciesCount = "Total policies found= "
					+ (String) data.getSessionData("S_MDM_POLICIES_COUNT");
			// Check if the session data for policy lookup exit state matches any of the
			// specified constants

			if (null != data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE)
					&& (Constants.S_SINGLE_POLICY_FOUND.equalsIgnoreCase(
							(String) data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE))
							|| Constants.S_MULTIPLE_POLICIES_SINGLE_PRODUCTTYPE_FOUND.equalsIgnoreCase(
									(String) data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE))
							|| Constants.S_MULTIPLE_POLICIES_MULTIPLE_PRODUCTTYPE_FOUND.equalsIgnoreCase(
									(String) data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE)))) {

				// Start host reporting for the NewVIAPolicyLookup API call
				objHostDetails.startHostReport(currElementName, "NewVIAMdmPhoneNoLookup", strReqBody, region,
						(String) data.getSessionData(Constants.S_MDM_PHONENO_LOOKUP_URL));
				// End host reporting with success or error based on the exit state
				objHostDetails.endHostReport(data, strRespBody,
						strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,
						totalNoOfPoliciesCount);
			} else {

				// If policy lookup exit state does not match, start and end host reporting
				// indicating DB mismatch
				objHostDetails.startHostReport(currElementName, "NewVIAMdmPhoneNoLookup", strReqBody, region,
						(String) data.getSessionData(Constants.S_MDM_PHONENO_LOOKUP_URL));
				objHostDetails.endHostReport(data, "DB-mismatch : " + strRespBody,
						strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,
						totalNoOfPoliciesCount);
			}

		} catch (Exception e) {
			data.addToLog(currElementName,
					"Exception while forming host reporting for NewVIAMdmPhoneNoLookup API call  :: " + e);
			caa.printStackTrace(e);
		}

		return strExitState;
	}

	private String apiResponseManupulation_MdmPhoneNoPolicyLookup(DecisionElementData data, CommonAPIAccess caa,
			String currElementName, String strRespBody) {
		String strExitState = Constants.ER;
		JSONArray policiesArr = new JSONArray();
		// JSONArray insuredDetails = null;
		JSONArray insuredDetailsArray = new JSONArray();
		JSONArray postalAdddressArray = new JSONArray();
		PolicyBean beanObj = new PolicyBean();
		JSONObject customerNamesObj = null;
		JSONObject policyData = null;
		JSONArray customerNamesArr = null;
		String lob = null;
		String prompt = Constants.EmptyString;
		String strPolicyNum = Constants.EmptyString;
		String policySource = Constants.EmptyString;
		String strPolicyStatus = Constants.EmptyString;
		int autoprodtypecount = 0;
		int homeprodtypecount = 0;
		int umbrellaprodtypecount = 0;
		int mrprodtypecount = 0;
		int rvprodtypecount = 0;
		int spprodtypecount = 0;
		int ttprodtypecount = 0;
		int mhprodtypecount = 0;
		int mcprodtypecount = 0;
		int offprodtypecount = 0;
		int mobprodtypecount = 0;
		int renprodtypecount = 0;
		int sphprodtypecount = 0;

		boolean singleproducttype = false;
		String strDOB_2 = "";
		String temp = "";
		Map<String, Integer> productTypeCounts = new HashMap<>();
		int totalPoliciesCount = 0;
		String policyType = "";

		try {

			// caputuring JSON response into JSON objects & array
			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			JSONObject results = (JSONObject) resp.get("results");
			JSONArray enterpriseHousehold = (results.containsKey("enterpriseHousehold")
					&& results.get("enterpriseHousehold") != null) ? (JSONArray) results.get("enterpriseHousehold")
							: new JSONArray();
			JSONArray customers = (results.containsKey("customers") && results.get("customers") != null)
					? (JSONArray) results.get("customers")
					: new JSONArray();
			data.addToLog(currElementName, "MDM _API Response :" + resp.toJSONString());
			// taking enterpriseHousehold into policiesArr and insuredDetails
			int totalPolicyCount = 0;
			if (enterpriseHousehold.size() > 0) {
				for (int singleEnterprise = 0; singleEnterprise < enterpriseHousehold.size(); singleEnterprise++) {

					JSONObject currentEnterprise = (JSONObject) enterpriseHousehold.get(singleEnterprise);

					// collect all the policies from enterprise household in policies array
					if (currentEnterprise.containsKey("policies")) {
						JSONArray policiesArray = (JSONArray) currentEnterprise.get("policies");
						data.addToLog(currElementName, "currentPolices from Enterprise array ::" + policiesArray);
						totalPolicyCount += policiesArray.size(); // Increment the total count
						// policiesArr.add(policiesArray); // You are still adding the entire array here
						// Add each individual policy to the new array
						for (Object policyObj : policiesArray) {
							policiesArr.add(policyObj);
						}
					} else {
						data.addToLog(currElementName, "policies does not exist in enterpriseHousehold");
					}

					// collect all the insured details from enterprise household in insured array
					if (currentEnterprise.containsKey("insuredDetails")) {
						JSONArray insuredDetails = (JSONArray) currentEnterprise.get("insuredDetails");
						for (Object insuredObj : insuredDetails) {
							insuredDetailsArray.add(insuredObj);
						}
					} else {
						data.addToLog(currElementName, "insuredDetails does not exist in enterpriseHousehold");
					}
				}
				data.addToLog(currElementName,
						"Total Policies (individual objects) from Enterprise House Hold from NEW VIA MDM Lookup ::"
								+ totalPolicyCount);
			} else {
				data.addToLog(currElementName,
						"Enterprise house hold array is empty from the response:" + enterpriseHousehold.size());
			}

			if (customers.size() > 0) {
				for (int singleCustomer = 0; singleCustomer < customers.size(); singleCustomer++) {
					JSONObject currentCustomer = (JSONObject) customers.get(singleCustomer);
					// collect all the customer details from enterprise household in insured array
					if (currentCustomer.containsKey("policies")) {
						JSONArray policiesArray = (JSONArray) currentCustomer.get("policies");
						totalPolicyCount += policiesArray.size();
						// policiesArr.add(policiesArray);
						for (Object policyObj : policiesArray) {
							policiesArr.add(policyObj);
						}
					} else {
						data.addToLog(currElementName, "policies does not exist in customers:" + policiesArr.size());
					}

					// collect insured details from customer ininsured array
					if (currentCustomer.containsKey("insuredDetails")) {
						JSONArray insuredDetails = (JSONArray) currentCustomer.get("insuredDetails");
						for (Object insuredObj : insuredDetails) {
							insuredDetailsArray.add(insuredObj);
						}
					} else {
						data.addToLog(currElementName, "insuredDetails does not exist in customers");
					}
				}
				data.addToLog(currElementName,
						"Total Policies from Enterprise House Hold from NEW VIA MDM Lookup ::" + policiesArr.size());
			} else {
				data.addToLog(currElementName, "customers array is empty from the response:" + customers.size());
			}

			// checking insuredDetails and date of birth
			data.addToLog(currElementName, "InsuredDetails Array MDM :" + insuredDetailsArray.toJSONString());
			data.addToLog(currElementName, "InsuredDetails Array Size MDM::" + insuredDetailsArray.size());
			data.addToLog(currElementName, "Total No of policies count ::" + totalPolicyCount);
			String strTotalNoOfMdmPoliciesCount = String.valueOf(totalPolicyCount);
			data.setSessionData("S_MDM_POLICIES_COUNT", totalPolicyCount);
			data.addToLog(currElementName, "S_MDM_POLICIES_COUNT ::" + data.getSessionData("S_MDM_POLICIES_COUNT"));

			if (insuredDetailsArray != null && !insuredDetailsArray.isEmpty() && insuredDetailsArray.size() > 0) {
				Set<String> dobSet = new HashSet<>();
				for (int singleCus = 0; singleCus < insuredDetailsArray.size(); singleCus++) {
					JSONObject insuredDetailsObj = (JSONObject) insuredDetailsArray.get(singleCus);
					String strDOB = (String) insuredDetailsObj.get("dob");
					data.addToLog(currElementName, "DOB after sub string MDM :" + strDOB);
					if (insuredDetailsObj.containsKey("postalAddress")) {
						data.addToLog(currElementName, "postal address adding...");
						postalAdddressArray.add((JSONArray) insuredDetailsObj.get("postalAddress"));
					} else {
						data.addToLog(currElementName, "Postal Address not available in Insured Details");
					}
//						if (strDOB != null && !strDOB.isEmpty()) {
//							// Extract the first 10 characters of DOB
//							strDOB = strDOB.substring(0, Math.min(strDOB.length(), 10));
//							data.addToLog(currElementName, "Truncated DOB: " + strDOB);
//
//							strDOB_2 = (strDOB_2.equals("")) ? "" + strDOB : strDOB_2 + "," + strDOB;
//
//						}
					// Get All DOBs
					if (strDOB != null && !strDOB.isEmpty()) {
						String truncatedDOB = strDOB.substring(0, Math.min(strDOB.length(), 10));
						data.addToLog(currElementName, "Truncated DOB: " + truncatedDOB);

						if (dobSet.add(truncatedDOB)) {
							// Successfully added, now rebuild strDOB_2
							strDOB_2 = String.join(",", dobSet);
						}
					}

				}
				data.setSessionData("S_MDM_DOB", strDOB_2);
				beanObj.setStrDOB(strDOB_2);

			} else {
				data.addToLog(currElementName, "Insured Details array : " + insuredDetailsArray);
				// Get the first insuredDetails object
				JSONArray insuredDetailsArr = (JSONArray) insuredDetailsArray.get(0);
				data.addToLog(currElementName,
						"Insured Details array get o'th Index:" + insuredDetailsArray.get(0).toString());
				for (int singleInsured = 0; singleInsured < insuredDetailsArr.size(); singleInsured++) {
					JSONObject insuredDetailsObj = (JSONObject) insuredDetailsArr.get(singleInsured);
					if (insuredDetailsObj.containsKey("customerNames")) {
						customerNamesArr = (JSONArray) insuredDetailsObj.get("customerNames");
						String strDOB = (String) insuredDetailsObj.get("dob");
						if (strDOB != null && !strDOB.isEmpty()) {
							// Extract the first 10 characters of DOB
							strDOB = strDOB.substring(0, Math.min(strDOB.length(), 10));
							strDOB_2 = (strDOB_2.equals("")) ? "" + strDOB : strDOB_2 + "," + strDOB;
							data.setSessionData("S_MDM_DOB", strDOB_2);
							beanObj.setStrDOB(strDOB_2);

						} else {
							data.addToLog(currElementName,
									"DOB is not present in 0th index Insured Details Array:" + strDOB);
						}
						if (!customerNamesArr.isEmpty()) {
							// Access the first customerNames object
							customerNamesObj = (JSONObject) customerNamesArr.get(0);

							// Extract firstName and lastName
							if (customerNamesObj.containsKey("firstName")
									&& customerNamesObj.get("firstName") != null) {

								beanObj.setStrFirstName((String) customerNamesObj.get("firstName"));
								data.setSessionData("S_MDM_FIRSTNAME", beanObj.getStrFirstName());
							}

							if (customerNamesObj.containsKey("lastName") && customerNamesObj.get("lastName") != null) {

								beanObj.setStrLastName((String) customerNamesObj.get("lastName"));
								data.setSessionData("S_MDM_LASTNAME", beanObj.getStrLastName());
							}
						} else {
							data.addToLog(currElementName,
									"customerNames array is empty in insuredDetails at index 0.");
						}
					} else {
						data.addToLog(currElementName,
								"customerNames array does not exist in insuredDetails at index 0.");
					}
				}

			}
			data.addToLog(currElementName, "Final Concatenated DOBs: " + beanObj.getStrDOB());

			// Remove duplicate policies with same product Type
			policiesArr = filterDuplicatePoliciesByNumber(policiesArr);
			data.addToLog(currElementName, "policies array after removing duplicates:" + policiesArr);
			data.addToLog(currElementName, "Length of policy array is: " + policiesArr.size());
			strTotalNoOfMdmPoliciesCount = String.valueOf(policiesArr.size());
			data.setSessionData("S_MDM_POLICIES_COUNT", strTotalNoOfMdmPoliciesCount);

			data.addToLog(currElementName, "policies array ::" + policiesArr.toJSONString());

			if (policiesArr != null && policiesArr.size() == 1) {
				// policiesArr = (JSONArray) policiesArr.get(0);
				policyData = (JSONObject) policiesArr.get(0);

				if (policyData.containsKey("lineOfBusiness") && null != policyData.get("lineOfBusiness")) {
					lob = (String) policyData.get("lineOfBusiness");
					data.setSessionData("S_LOB", lob);
				}

			}
			if (policiesArr != null && policiesArr.size() > 0) {
				data.setSessionData(Constants.S_NOOF_POLICIES_LOOKUP, policiesArr.size());
				data.setSessionData(Constants.IS_MORETHAN_ZERO_POLICIES, Constants.STRING_YES);
				HashMap<String, HashMap<String, PolicyBean>> productPolicyMap = new HashMap<String, HashMap<String, PolicyBean>>();
				for (int i = 0; i < policiesArr.size(); i++) {
					PolicyBean beanObj2 = new PolicyBean();
					policyData = (JSONObject) policiesArr.get(i);
					String zip = "";
					Set<String> postalCodeSet = new HashSet<>();
					if (policyData.containsKey("billingAddress") && null != policyData.get("billingAddress")) {
						JSONArray billingAddressesArr = (JSONArray) policyData.get("billingAddress");
						for (int singleBillingAddress = 0; singleBillingAddress < billingAddressesArr
								.size(); singleBillingAddress++) {
							JSONObject billingAddressesObj = (JSONObject) billingAddressesArr.get(singleBillingAddress);

							// Extract ZIP code

							if (billingAddressesObj != null && billingAddressesObj.containsKey("postalcode")) {
								String postalCode = (String) billingAddressesObj.get("postalcode");
								if (postalCode != null && !postalCode.isEmpty() && postalCode.length() >= 5) {
									String code = postalCode.substring(0, 5);
									postalCodeSet.add(code);
								} else {
									data.addToLog(currElementName,
											"postalCode or zip code is null or not in proper format in billing address: "
													+ postalCode);
								}
							}

							if (policyData.containsKey("policyType") && null != policyData.get("policyType")) {
								policyType = (String) policyData.get("policyType");
								data.addToLog(currElementName, "Policy Type::" + policyType);
								beanObj2.setPolicyType(policyType);
							}
							// Extract state
							if (billingAddressesObj.containsKey("state"))
								beanObj2.setStrPolicyState((String) billingAddressesObj.get("state"));
							String strPolicyState = (String) beanObj2.getStrPolicyState();
							data.setSessionData(Constants.S_POLICY_STATE_CODE, strPolicyState);
							data.addToLog(currElementName, "Policy State Code :" + strPolicyState);
							String strPolicyStateName = setStateName(data, caa, currElementName, strPolicyState);
							data.addToLog(currElementName, "Policy State Name:" + strPolicyStateName);
						}

					}

					if (policyData.containsKey("lineOfBusiness") && null != policyData.get("lineOfBusiness")) {
						lob = (String) policyData.get("lineOfBusiness");
						data.setSessionData(Constants.S_FWS_POLICY_LOB, lob);
						beanObj2.setStrPolicyLOB(lob);
						data.addToLog(currElementName, "Entered into policy count by LOB:" + lob);
						data.addToLog(currElementName, "Policy Type is:" + policyType);
						if (productTypeCounts.containsKey(lob)) {
							productTypeCounts.put(lob, productTypeCounts.get(lob) + 1);
							if (lob.equalsIgnoreCase("RV")) {
								data.addToLog(currElementName, "Entered into RV lob" + "");
								if (policyType.equalsIgnoreCase("Travel Trailer"))
									productTypeCounts.put("TT", productTypeCounts.getOrDefault("TT", 0) + 1);
								else if (policyType.equalsIgnoreCase("Motor Home"))
									productTypeCounts.put("MH", productTypeCounts.getOrDefault("MH", 0) + 1);
								else if (policyType.equalsIgnoreCase("Motorcycle"))
									productTypeCounts.put("MC", productTypeCounts.getOrDefault("MC", 0) + 1);
								else if (policyType.equalsIgnoreCase("Motorcycle and Off Road Vehicle"))
									productTypeCounts.put("OFF", productTypeCounts.getOrDefault("OFF", 0) + 1);
							} else if (lob.equalsIgnoreCase("SP")) {
								data.addToLog(currElementName, "Entered into SP lob" + "");
								if (policyType.equalsIgnoreCase("Mobile Home"))
									productTypeCounts.put("MOB", productTypeCounts.getOrDefault("MOB", 0) + 1);
								else if (policyType.equalsIgnoreCase("Commercial Rental Mobile Home"))
									productTypeCounts.put("REN", productTypeCounts.getOrDefault("REN", 0) + 1);
								else if (policyType.equalsIgnoreCase("Specialty Dwelling"))
									productTypeCounts.put("SPH", productTypeCounts.getOrDefault("SPH", 0) + 1);
							}
						} else {
							productTypeCounts.put(lob, 1);
							if (lob.equalsIgnoreCase("RV")) {
								data.addToLog(currElementName, "Entered into RV lob" + "");
								if ("Travel Trailer".equalsIgnoreCase(policyType))
									productTypeCounts.put("TT", 1);
								else if (policyType.equalsIgnoreCase("Motor Home"))
									productTypeCounts.put("MH", 1);
								else if (policyType.equalsIgnoreCase("Motorcycle"))
									productTypeCounts.put("MC", 1);
								else if (policyType.equalsIgnoreCase("Motorcycle and Off Road Vehicle"))
									productTypeCounts.put("OFF", 1);
							} else if (lob.equalsIgnoreCase("SP")) {
								data.addToLog(currElementName, "Entered into  lob" + "");
								if (policyType.equalsIgnoreCase("Mobile Home"))
									productTypeCounts.put("MOB", 1);
								else if (policyType.equalsIgnoreCase("Commercial Rental Mobile Home"))
									productTypeCounts.put("REN", 1);
								else if (policyType.equalsIgnoreCase("Motorcycle"))
									productTypeCounts.put("MC", 1);
								else if (policyType.equalsIgnoreCase("Specialty Dwelling"))
									productTypeCounts.put("SPH", 1);
							}
						}
						data.addToLog(currElementName, "product count map :" + productTypeCounts);
					}

					data.addToLog(currElementName, "Postal Address Array:" + postalAdddressArray.toJSONString());
					if (!postalAdddressArray.isEmpty() && postalAdddressArray.size() > 0) {

						for (int singlePostalAddress = 0; singlePostalAddress < postalAdddressArray
								.size(); singlePostalAddress++) {
							JSONArray singlePostalAddressesArray = (JSONArray) postalAdddressArray
									.get(singlePostalAddress);
							for (int singlePostalArrofArr = 0; singlePostalArrofArr < singlePostalAddressesArray
									.size(); singlePostalArrofArr++) {
								JSONObject postalAddressesObj = (JSONObject) singlePostalAddressesArray
										.get(singlePostalArrofArr);
								if (postalAddressesObj != null && postalAddressesObj.containsKey("PostalCode")) {
									String postalCode = (String) postalAddressesObj.get("PostalCode");
									String nonHyphenPostalCode = postalCode.replace("-", "");
									if (nonHyphenPostalCode != null && !nonHyphenPostalCode.isEmpty()
											&& nonHyphenPostalCode.length() >= 5) {
										String code = nonHyphenPostalCode.substring(0, 5);
										postalCodeSet.add(code);
									} else {
										data.addToLog(currElementName,
												"Non Hiphen postalCode or zip code is null or not in proper format in postal address:"
														+ nonHyphenPostalCode);
									}

								}
								if (postalAddressesObj.containsKey("State"))
									beanObj2.setStrPolicyState((String) postalAddressesObj.get("State"));
								String strPolicyState = (String) beanObj2.getStrPolicyState();
								data.setSessionData(Constants.S_POLICY_STATE_CODE, strPolicyState);
								data.addToLog(currElementName, "Policy State Code in Postal Address:" + strPolicyState);
								String strPolicyStateName = setStateName(data, caa, currElementName, strPolicyState);
								data.addToLog(currElementName,
										"Policy State Name Using Postal State:" + strPolicyStateName);

							}
						}
					} else {
						data.addToLog(currElementName,
								"Postal Address array is empty with size:" + postalAdddressArray.size());
					}

					zip = String.join(",", postalCodeSet);
					beanObj2.setStrZipCode(zip);

					if (policyData.containsKey("policyNumber") && null != policyData.get("policyNumber")) {
						strPolicyNum = (String) policyData.get("policyNumber");
						beanObj2.setStrPolicyNum(strPolicyNum);
					}
					if (policyData.containsKey("policyStatus") && null != policyData.get("policyStatus")) {
						strPolicyStatus = (String) policyData.get("policyStatus");
						beanObj2.setPolicyStatus(strPolicyStatus);
					}

					// CS1151307 : Update Policy State Name(Policy State : HI, PolicyStateName :
					// Hawaii)
					if (policyData.containsKey("policyStateCode") && policyData.get("policyStateCode") != null
							&& !policyData.get("policyStateCode").toString().isEmpty()) {
						beanObj2.setStrPolicyState((String) policyData.get("policyStateCode"));
						String strPolicyState = beanObj2.getStrPolicyState();
						data.setSessionData(Constants.S_POLICY_STATE_CODE, strPolicyState);
						data.addToLog(currElementName, "Policy State Code: " + strPolicyState);
						String strPolicyStateName = setStateName(data, caa, currElementName, strPolicyState);
						data.addToLog(currElementName, "Policy State Name: " + strPolicyStateName);
					}

					if (policyData.containsKey("policySource") && null != policyData.get("policySource"))
						beanObj2.setStrPolicySource((String) policyData.get("policySource"));
					if (policyData.containsKey("policyState") && null != policyData.get("policyState"))
						beanObj2.setStrPolicyState((String) policyData.get("policyState"));
					if (policyData.containsKey("suffix") && null != policyData.get("suffix"))
						beanObj2.setStrPolicySuffix((String) policyData.get("suffix"));
					if (policyData.containsKey("termStartDate") && policyData.get("termStartDate") != null)
						;
					{
						String EffDate = formatDate(data, (String) policyData.get("termStartDate"),
								"yyyy-MM-dd HH:mm:ss.S", "MM/dd/yyyy");
						beanObj2.setStrEffectiveDate(EffDate);
						data.addToLog(currElementName, "StrEffectiveDate :" + beanObj2.getStrEffectiveDate());
						data.setSessionData("S_FWS_POLICY_EFF_DATE", EffDate);
					}
					if (policyData.containsKey("billingAccountNumber")
							&& null != policyData.get("billingAccountNumber")) {
						String tmpBillingAccNum = (String) policyData.get("billingAccountNumber");
						if ("ARS".equalsIgnoreCase(beanObj2.getStrPolicySource()) && tmpBillingAccNum.length() > 9) {
							beanObj2.setStrCompanyCode(tmpBillingAccNum.substring(0, 2));
							beanObj2.setStrBillingAccountNumber(
									tmpBillingAccNum.substring(2, tmpBillingAccNum.length()));
						} else {
							beanObj2.setStrCompanyCode(tmpBillingAccNum.substring(0, 2));
							beanObj2.setStrBillingAccountNumber(
									tmpBillingAccNum.substring(0, tmpBillingAccNum.length()));
						}
					}

					String tmpKey = Constants.EmptyString;
					switch (lob) {
					case Constants.PRODUCTTYPE_Y:
						tmpKey = Constants.MR_PRODUCTTYPECOUNT_KYC;
						break;
					case Constants.PRODUCTTYPE_HOME:
						tmpKey = Constants.HOME_PRODUCTTYPECOUNT_KYC;
						break;
					case Constants.PRODUCTTYPE_F:
						tmpKey = Constants.HOME_PRODUCTTYPECOUNT_KYC;
						break;
					case Constants.PRODUCTTYPE_AUTO:
						tmpKey = Constants.AUTO_PRODUCTTYPECOUNT_KYC;
						break;
					case Constants.PRODUCTTYPE_UMBRELLA:
						tmpKey = Constants.UMBRELLA_PRODUCTTYPECOUNT_KYC;
						break;
					case Constants.PRODUCTTYPE_SP:
						data.addToLog("Policy Type: ", policyType);
						if (policyType.equalsIgnoreCase("Mobile Home")) {
							tmpKey = Constants.MOBILEHOME_PRODUCTTYPECOUNT_KYC;
						} else if (policyType.equalsIgnoreCase("Commercial Rental Mobile Home")) {
							tmpKey = Constants.RENTALHOME_PRODUCTTYPECOUNT_KYC;
						} else if (policyType.equalsIgnoreCase("Specialty Dwelling")) {
							tmpKey = Constants.SPHOME_PRODUCTTYPECOUNT_KYC;
						}
						break;
					case Constants.PRODUCTTYPE_RV:
						data.addToLog("Policy Type: ", policyType);
						if (policyType.equalsIgnoreCase("Travel Trailer")) {
							tmpKey = Constants.TRAVEL_PRODUCTTYPECOUNT_KYC;
						} else if (policyType.equalsIgnoreCase("Motor Home")) {
							tmpKey = Constants.MOTORHOME_PRODUCTTYPECOUNT_KYC;
						} else if (policyType.equalsIgnoreCase("Motorcycle and Off Road Vehicle")) {
							tmpKey = Constants.OFFROAD_PRODUCTTYPECOUNT_KYC;
						} else if (policyType.equalsIgnoreCase("Motorcycle")) {
							tmpKey = Constants.MOTORCYCLE_PRODUCTTYPECOUNT_KYC;
						}
						break;
					case Constants.PRODUCTTYPE_MR:
						tmpKey = Constants.MR_PRODUCTTYPECOUNT_KYC;
						break;
					default:
						break;
					}
					data.addToLog("product policy map ::", productPolicyMap.toString());
					data.addToLog("tmpKey ::", tmpKey);

					if (productPolicyMap.containsKey(tmpKey)) {
						HashMap<String, PolicyBean> tmpPolicyDetails = productPolicyMap.get(tmpKey);
						tmpPolicyDetails.put(strPolicyNum, beanObj2);
						productPolicyMap.put(tmpKey, tmpPolicyDetails);
					} else {
						HashMap<String, PolicyBean> policyDetails = new HashMap<String, PolicyBean>();
						policyDetails.put(strPolicyNum, beanObj2);
						productPolicyMap.put(tmpKey, policyDetails);
					}

					String DOB = (String) data.getSessionData("S_MDM_DOB");
					beanObj2.setStrDOB(DOB);
				}
				data.setSessionData(Constants.S_POLICYDETAILS_MAP, productPolicyMap);
				data.addToLog(currElementName, "Product Type Count Hashmap : " + productTypeCounts);
				data.addToLog(currElementName, "policyDetails Hashmap : " + productPolicyMap);

				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_Y)) {
					mrprodtypecount = productTypeCounts.get(Constants.PRODUCTTYPE_Y);
				}
				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_MR)) {
					mrprodtypecount = productTypeCounts.get(Constants.PRODUCTTYPE_MR);
				}
				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_HOME)) {
					homeprodtypecount = homeprodtypecount + productTypeCounts.get(Constants.PRODUCTTYPE_HOME);
				}
				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_F)) {
					homeprodtypecount = homeprodtypecount + productTypeCounts.get(Constants.PRODUCTTYPE_F);
				}
				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_AUTO)) {
					autoprodtypecount = productTypeCounts.get(Constants.PRODUCTTYPE_AUTO);
				}
				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_UMBRELLA)) {
					umbrellaprodtypecount = productTypeCounts.get(Constants.PRODUCTTYPE_UMBRELLA);
				}
				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_RV)) {
					rvprodtypecount = productTypeCounts.get(Constants.PRODUCTTYPE_RV);
				}
				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_SP)) {
					spprodtypecount = productTypeCounts.get(Constants.PRODUCTTYPE_SP);
				}
				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_TT)) {
					ttprodtypecount = productTypeCounts.get(Constants.PRODUCTTYPE_TT);
				}
				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_MH)) {
					mhprodtypecount = productTypeCounts.get(Constants.PRODUCTTYPE_MH);
				}
				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_MC)) {
					mcprodtypecount = productTypeCounts.get(Constants.PRODUCTTYPE_MC);
				}
				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_OFF)) {
					offprodtypecount = productTypeCounts.get(Constants.PRODUCTTYPE_OFF);
				}
				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_MOB)) {
					mobprodtypecount = productTypeCounts.get(Constants.PRODUCTTYPE_MOB);
				}
				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_REN)) {
					renprodtypecount = productTypeCounts.get(Constants.PRODUCTTYPE_REN);
				}
				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_SPH)) {
					sphprodtypecount = productTypeCounts.get(Constants.PRODUCTTYPE_SPH);
				}

				data.setSessionData(Constants.AUTO_PRODUCTTYPECOUNT_KYC, autoprodtypecount);
				data.setSessionData(Constants.HOME_PRODUCTTYPECOUNT_KYC, homeprodtypecount);
				data.setSessionData(Constants.UMBRELLA_PRODUCTTYPECOUNT_KYC, umbrellaprodtypecount);
				data.setSessionData(Constants.MR_PRODUCTTYPECOUNT_KYC, mrprodtypecount);
				data.setSessionData(Constants.RV_PRODUCTTYPECOUNT_KYC, rvprodtypecount);
				data.setSessionData(Constants.SP_PRODUCTTYPECOUNT_KYC, spprodtypecount);

				data.setSessionData(Constants.TRAVEL_PRODUCTTYPECOUNT_KYC, ttprodtypecount);
				data.setSessionData(Constants.MOTORHOME_PRODUCTTYPECOUNT_KYC, mhprodtypecount);
				data.setSessionData(Constants.MOTORCYCLE_PRODUCTTYPECOUNT_KYC, mcprodtypecount);
				data.setSessionData(Constants.OFFROAD_PRODUCTTYPECOUNT_KYC, offprodtypecount);
				data.setSessionData(Constants.MOBILEHOME_PRODUCTTYPECOUNT_KYC, mobprodtypecount);
				data.setSessionData(Constants.RENTALHOME_PRODUCTTYPECOUNT_KYC, renprodtypecount);
				data.setSessionData(Constants.SPHOME_PRODUCTTYPECOUNT_KYC, sphprodtypecount);

				data.addToLog(currElementName,
						"Auto Product Type Count  = " + data.getSessionData(Constants.AUTO_PRODUCTTYPECOUNT_KYC));
				data.addToLog(currElementName,
						"Home Product Type Count  = " + data.getSessionData(Constants.HOME_PRODUCTTYPECOUNT_KYC));
				data.addToLog(currElementName, "Umbrella Product Type Count = "
						+ data.getSessionData(Constants.UMBRELLA_PRODUCTTYPECOUNT_KYC));
				data.addToLog(currElementName,
						"Marine Product Type Count  = " + data.getSessionData(Constants.MR_PRODUCTTYPECOUNT_KYC));
				data.addToLog(currElementName,
						"RV Type Count  = " + data.getSessionData(Constants.RV_PRODUCTTYPECOUNT_KYC));
				data.addToLog(currElementName,
						"Specialty Product Type Count  = " + data.getSessionData(Constants.SP_PRODUCTTYPECOUNT_KYC));
				data.addToLog(currElementName, "Travel trailer Product Type Count  = "
						+ data.getSessionData(Constants.TRAVEL_PRODUCTTYPECOUNT_KYC));
				data.addToLog(currElementName, "Motor Home Product Type Count  = "
						+ data.getSessionData(Constants.MOTORHOME_PRODUCTTYPECOUNT_KYC));
				data.addToLog(currElementName, "Motorcycle Product Type Count  = "
						+ data.getSessionData(Constants.MOTORCYCLE_PRODUCTTYPECOUNT_KYC));
				data.addToLog(currElementName, "Off road vehicle Product Type Count  = "
						+ data.getSessionData(Constants.OFFROAD_PRODUCTTYPECOUNT_KYC));
				data.addToLog(currElementName, "Mobile Home Product Type Count  = "
						+ data.getSessionData(Constants.MOBILEHOME_PRODUCTTYPECOUNT_KYC));
				data.addToLog(currElementName, "Rental Home Product Type Count  = "
						+ data.getSessionData(Constants.RENTALHOME_PRODUCTTYPECOUNT_KYC));
				data.addToLog(currElementName, "Specialty Home Product Type Count  = "
						+ data.getSessionData(Constants.SPHOME_PRODUCTTYPECOUNT_KYC));

				singleproducttype = checkifsingleproducttype(rvprodtypecount, autoprodtypecount, homeprodtypecount,
						umbrellaprodtypecount, mrprodtypecount, spprodtypecount, ttprodtypecount, mhprodtypecount,mcprodtypecount,offprodtypecount,
						mobprodtypecount,renprodtypecount,sphprodtypecount);
				data.addToLog(currElementName, "Check if single Product : " + singleproducttype);

				if (policiesArr.size() == 1) {
					data.setSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE, Constants.S_SINGLE_POLICY_FOUND);
					data.addToLog(currElementName,
							"S_NOOF_POLICIES_ANILOOKUP_EXITSTATE : " + Constants.S_SINGLE_POLICY_FOUND);
					for (String key : productPolicyMap.keySet()) {
						HashMap<String, PolicyBean> policyDetailsMap = productPolicyMap.get(key);
						data.addToLog(currElementName, "policyDetailsMap" + policyDetailsMap);
						for (String key1 : policyDetailsMap.keySet()) {
							PolicyBean obj = policyDetailsMap.get(key1);
							data.setSessionData(Constants.S_API_DOB, obj.getStrDOB());
							data.setSessionData(Constants.S_API_ZIP, obj.getStrZipCode());
							data.addToLog("DOB: ", obj.getStrDOB());
							data.addToLog("ZIP: ", obj.getStrZipCode());
							data.setSessionData(Constants.S_POLICY_NUM, key1);
							data.setSessionData(Constants.S_POLICY_STATE_CODE, obj.getStrPolicyState());
							data.setSessionData(Constants.S_POLICY_SOURCE, obj.getStrPolicySource());
							if("FWS-ARS".equalsIgnoreCase((String)data.getSessionData(Constants.S_POLICY_SOURCE))) {
								data.setSessionData(Constants.S_POLICY_SOURCE, "ARS");
							}
						
							//	// CS1336023 - Cancel policy - Arshath - start
							data.setSessionData(Constants.S_MDM_POLICY_STATUS, obj.getPolicyStatus());
							data.addToLog("Policy Status", obj.getPolicyStatus());
							// CS1336023 - Cancel policy - Arshath - start
							// New VIA Caller Auth and TI SCORE
							data.setSessionData("S_CALLER_AUTH", "Identified");
							data.setSessionData("S_TI_SCORE", "MEDIUM");
							data.setSessionData("ID_AUTH_SELECTED_PRODUCTTYPE", lob);
							data.setSessionData("S_EPC_PAYMENT_POLICYNUM", obj.getStrBillingAccountNumber());
						}
					}
					// DOB BYPASS CHANGE
					if (null == data.getSessionData(Constants.S_API_DOB)
							|| data.getSessionData(Constants.S_API_DOB).toString().isEmpty()) {
						data.setSessionData("DOB_CHECK", "NO");
						data.addToLog("DOB_CHECK", data.getSessionData("DOB_CHECK").toString());
					}
					if (null == data.getSessionData(Constants.S_API_ZIP)
							|| data.getSessionData(Constants.S_API_ZIP).toString().isEmpty()) {
						data.setSessionData("ZIP_CHECK", "NO");
						data.addToLog("ZIP_CHECK", data.getSessionData("ZIP_CHECK").toString());
					}
					// DOB BYPASS CHANGE

					if (policiesArr.size() == 1) {
						data.addToLog(currElementName, "New MDM Single Policy::");
//						Map<String, Map<String, PolicyBean>> tmpPolicyDetails = (Map<String, Map<String, PolicyBean>>) data
//								.getSessionData(Constants.S_POLICYDETAILS_MAP);
//
//						data.addToLog(currElementName, "tmpPolicyDetails ::" + tmpPolicyDetails);
//
//						// SP Product
//						Map<String, PolicyBean> spPolicyMap = tmpPolicyDetails.get(Constants.SP_PRODUCTTYPECOUNT_KYC);
//						PolicyBean beanObjSP = (spPolicyMap != null && !spPolicyMap.isEmpty())
//								? spPolicyMap.values().iterator().next()
//								: null;
//						data.addToLog(currElementName, "beanObjSP ::" + beanObjSP);
//
//						// MR Product
//						Map<String, PolicyBean> mrPolicyMap = tmpPolicyDetails.get(Constants.MR_PRODUCTTYPECOUNT_KYC);
//						PolicyBean beanObjMR = (mrPolicyMap != null && !mrPolicyMap.isEmpty())
//								? mrPolicyMap.values().iterator().next()
//								: null;
//						data.addToLog(currElementName, "beanObjMR ::" + beanObjMR);
//
//						// RV Product
//						Map<String, PolicyBean> rvPolicyMap = tmpPolicyDetails.get(Constants.RV_PRODUCTTYPECOUNT_KYC);
//						PolicyBean beanObjRV = (rvPolicyMap != null && !rvPolicyMap.isEmpty())
//								? rvPolicyMap.values().iterator().next()
//								: null;
//						data.addToLog(currElementName, "beanObjRV ::" + beanObjRV);

						if (lob.equalsIgnoreCase("AUTO")) {
							prompt = "Auto policy";
							data.setSessionData(Constants.FINAL_PRODUCTTYPE, lob);
						} else if (lob.equalsIgnoreCase("HOME")) {
							prompt = "Home policy";
							data.setSessionData(Constants.FINAL_PRODUCTTYPE, lob);
						} else if (lob.equalsIgnoreCase("MR")) {
							prompt = "Marine Watercraft policy";
							data.setSessionData(Constants.FINAL_PRODUCTTYPE, lob);
						} else if (lob.equalsIgnoreCase("RV")) {
							data.setSessionData(Constants.FINAL_PRODUCTTYPE, lob);
//							if (null != beanObjRV && beanObjRV.getPolicyType().equalsIgnoreCase("Travel Trailer")) {
//								prompt = "Travel Trailer policy ";
//							} else if (null != beanObjRV && beanObjRV.getPolicyType().equalsIgnoreCase("Motor Home")) {
//								prompt = "Motor Home policy";
//							} else if (null != beanObjRV && beanObjRV.getPolicyType().equalsIgnoreCase("Motorcycle")) {
//								prompt = "Motorcycle policy";
//							} else if (null != beanObjRV
//									&& beanObjRV.getPolicyType().equalsIgnoreCase("Motorcycle and Off Road Vehicle")) {
//								prompt = "Off Road Vehicle policy";
//							}
							if (ttprodtypecount == 1)
								prompt += "Travel Trailer Policy";
							if (mhprodtypecount == 1)
								prompt += "Motor Home Policy";
							if (mcprodtypecount == 1)
								prompt += "Motorcycle Policy";
							if (offprodtypecount == 1)
								prompt += "Off Road Vehicle Policy";
						} else if (lob.equalsIgnoreCase("SP")) {
//							data.setSessionData(Constants.FINAL_PRODUCTTYPE, lob);
//							if (null != beanObjSP && beanObjSP.getPolicyType().equalsIgnoreCase("Mobile Home")) {
//								prompt = "Manufactured Home policy";
//							} else if (null != beanObjSP
//									&& beanObjSP.getPolicyType().equalsIgnoreCase("Commercial Rental Mobile Home")) {
//								prompt = "Rental Manufactured Home policy";
//							} else if (null != beanObjSP
//									&& beanObjSP.getPolicyType().equalsIgnoreCase("Specialty Dwelling")) {
//								prompt = "Specialty Home Owners policy";
//							}
							if (mobprodtypecount == 1)
								prompt += "Manufactured Home Policy";
							if (renprodtypecount == 1)
								prompt += "Rental Manufactured Home Policy";
							if (sphprodtypecount == 1)
								prompt += "Specialty Home Owners Policy";
						} else if (lob.equalsIgnoreCase("UMBRELLA")) {
							prompt = "Umbrella Policy";
							data.setSessionData(Constants.FINAL_PRODUCTTYPE, lob);
						}

						String lang = (String) data.getSessionData("S_PREF_LANG");

						if (lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("auto policy")) {
							prompt = "tu póliza de auto";
						}
						if (lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("home policy")) {
							prompt = "tu póliza de hogar";
						}
						if (lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("Marine Watercraft policy")) {
							prompt = "su embarcación marina";
						}
						if (lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("Travel Trailer policy")) {
							prompt = "tu remolque de viaje";
						}
						if (lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("Motor Home policy")) {
							prompt = "tu casa rodante";
						}
						if (lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("Motorcycle policy")) {
							prompt = "tu motocicleta";
						}
						if (lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("Off Road Vehicle policy")) {
							prompt = "su vehículo todoterreno";
						}
						if (lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("Manufactured Home policy")) {
							prompt = "Su casa prefabricada";
						}
						if (lang.equalsIgnoreCase("SP")
								&& prompt.toLowerCase().contains("Rental Manufactured Home policy")) {
							prompt = "Su casa prefabricada de alquiler";
						}
						if (lang.equalsIgnoreCase("SP")
								&& prompt.toLowerCase().contains("Specialty Home Owners policy")) {
							prompt = "sus propietarios de viviendas especiales";
						}
						if (lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("Umbrella policy")) {
							prompt = "su política general";
						}
						if (prompt.contains(" ")) {
							prompt = prompt.replaceAll(" ", ".");
						}

						// JSONObject policyobject = (JSONObject) policiesArr.get(0);
						// String policysource = (String) policyobject.get("policySource");
						// data.setSessionData("SinglePolicySource", policysource);

						data.setSessionData(Constants.VXMLParam1, prompt);
						data.setSessionData("MDM_VXMLParam1", prompt);
						data.setSessionData(Constants.VXMLParam2, "NA");
						data.setSessionData(Constants.VXMLParam3, "NA");
						data.setSessionData(Constants.VXMLParam4, "NA");
						data.addToLog(currElementName, "Single Policy Scenario");
						data.addToLog(currElementName,
								"Number of Policies Dynamic Product Type Prompts = " + prompt.toString());
						data.addToLog(currElementName, "VXMLPARAM1 = " + data.getSessionData(Constants.VXMLParam1));

						data.setSessionData("S_NOOF_POLICIES_ACCLINKANI_EXITSTATE", Constants.S_SINGLE_POLICY_FOUND);
						data.addToLog(currElementName, "S_NOOF_POLICIES_ACCLINKANI_EXITSTATE : "
								+ data.getSessionData("S_NOOF_POLICIES_ACCLINKANI_EXITSTATE"));

						strExitState = Constants.SU;
					}
				} else if (!singleproducttype) {
					data.addToLog(currElementName, "New MDM Multiple Product Type::");

					Map<String, Map<String, PolicyBean>> tmpPolicyDetails = (Map<String, Map<String, PolicyBean>>) data
							.getSessionData(Constants.S_POLICYDETAILS_MAP);

					data.addToLog(currElementName, "tmpPolicyDetails ::" + tmpPolicyDetails);
					if (autoprodtypecount >= 1) {
						prompt += " your Auto Policy";
					}

					if (homeprodtypecount >= 1) {
						prompt += " your Home Policy";
					}

					if (mrprodtypecount >= 1) {
						prompt += " your Marine Watercraft Policy";
					}

					if (umbrellaprodtypecount >= 1) {
						prompt += " your Umbrella Policy";
					}

					if (rvprodtypecount >= 1) {
						if (ttprodtypecount >= 1)
							prompt += " your Travel Trailer Policy";
						if (mhprodtypecount >= 1)
							prompt += " your Motor Home Policy";
						if (mcprodtypecount >= 1)
							prompt += " your Motorcycle Policy";
						if (offprodtypecount >= 1)
							prompt += " your Off Road Vehicle Policy";
					}

					if (spprodtypecount >= 1) {
						if (mobprodtypecount >= 1)
							prompt += " your Manufactured Home Policy";
						if (renprodtypecount >= 1)
							prompt += " your Rental Manufactured Home Policy";
						if (sphprodtypecount >= 1)
							prompt += " your Specialty Home Owners Policy";
					}

					String lang = (String) data.getSessionData("S_PREF_LANG");

					if (lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains(" your Auto policy")) {
						data.addToLog(currElementName, "Enterd auto policy replace condition");
						prompt = prompt.replace("  your Auto policy", " tu póliza de auto");
					}
					if (lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains(" your Home Policy")) {
						prompt = prompt.replace("  your Home Policy", " tu póliza de hogar");
					}
					if (lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains(" your Umbrella Policy")) {
						prompt = prompt.replace("  your Umbrella Policy", " su política general");
					}
					if (lang.equalsIgnoreCase("SP")
							&& prompt.toLowerCase().contains(" your Marine Watercraft policy")) {
						prompt = prompt.replace(" your Marine Watercraft policy", " su embarcación marina");
					}
					if (lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains(" your Travel Trailer policy")) {
						prompt = prompt.replace(" your Travel Trailer policy", " tu remolque de viaje");
					}
					if (lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains(" your Motor Home policy")) {
						prompt = prompt.replace(" your Motor Home policy", " tu casa rodante");
					}
					if (lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains(" your Off Road Vehicle policy")) {
						prompt = prompt.replace(" your Off Road Vehicle policy", "su vehículo todoterreno");
					}
					if (lang.equalsIgnoreCase("SP")
							&& prompt.toLowerCase().contains(" your Manufactured Home policy")) {
						prompt = prompt.replace(" your Manufactured Home policy", "Su casa prefabricada");
					}
					if (lang.equalsIgnoreCase("SP")
							&& prompt.toLowerCase().contains(" your Rental Manufactured Home policy")) {
						prompt = prompt.replace(" your Rental Manufactured Home policy",
								"Su casa prefabricada de alquiler");
					}
					if (lang.equalsIgnoreCase("SP")
							&& prompt.toLowerCase().contains(" your Specialty Home Owners policy")) {
						prompt = prompt.replace(" your Specialty Home Owners policy",
								"sus propietarios de viviendas especiales");
					}

					if (prompt.contains(" ")) {
						prompt = prompt.replaceFirst(" ", "");
						prompt = prompt.replaceAll(" ", ".");
					}

					data.setSessionData(Constants.VXMLParam1, prompt);
					data.setSessionData("MDM_VXMLParam1", prompt);
					data.setSessionData(Constants.VXMLParam2, "NA");
					data.setSessionData(Constants.VXMLParam3, "NA");
					data.setSessionData(Constants.VXMLParam4, "NA");
					data.addToLog(currElementName, "Multiple Product Types Scenario");
					data.addToLog(currElementName, "Policies Dynamic Product Type Prompts = " + prompt.toString());
					data.addToLog(currElementName, "VXMLPARAM1 = " + data.getSessionData(Constants.VXMLParam1));

					data.setSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE,
							Constants.S_MULTIPLE_POLICIES_MULTIPLE_PRODUCTTYPE_FOUND);
					data.addToLog(currElementName, "S_NOOF_POLICIES_ANILOOKUP_EXITSTATE : "
							+ data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE));
					strExitState = Constants.SU;

				} else if (policiesArr.size() > 1 && singleproducttype) {
					data.addToLog(currElementName, "New MDM single Product Type and More than one policy::");
					String produtType = "";

					if (autoprodtypecount >= 1) {
						prompt = " Auto ";
						produtType = Constants.AUTO_PRODUCTTYPECOUNT_KYC;
					} else if (homeprodtypecount >= 1) {
						prompt = " Home ";
						produtType = Constants.HOME_PRODUCTTYPECOUNT_KYC;
					} else if (mrprodtypecount >= 1) {
						prompt = " Marine Watercraft ";
						produtType = Constants.MR_PRODUCTTYPECOUNT_KYC;
					} else if (rvprodtypecount >= 1) {
						if (ttprodtypecount >= 1) {
							prompt= " Travel Trailer ";
						    produtType = Constants.TRAVEL_PRODUCTTYPECOUNT_KYC;
						   }
						if (mhprodtypecount >= 1) {
							prompt = " Motor Home";
						    produtType = Constants.MOTORHOME_PRODUCTTYPECOUNT_KYC;
						}
						if (mcprodtypecount >= 1) {
							prompt = " Motorcycle ";
						    produtType = Constants.MOTORCYCLE_PRODUCTTYPECOUNT_KYC;
						}
						if (offprodtypecount >= 1) {
							prompt = " Off Road Vehicle ";
						    produtType = Constants.OFFROAD_PRODUCTTYPECOUNT_KYC;
						}
					} else if (spprodtypecount >= 1) {
						if (mobprodtypecount >= 1) {
							prompt = " Manufactured Home ";
							produtType = Constants.MOBILEHOME_PRODUCTTYPECOUNT_KYC;
						}
						if (renprodtypecount >= 1) {
							prompt = "Rental Manufactured Home ";
							produtType = Constants.RENTALHOME_PRODUCTTYPECOUNT_KYC;
						}
						if (sphprodtypecount >= 1) {
							prompt = " Specialty Home Owners ";
							produtType = Constants.SPHOME_PRODUCTTYPECOUNT_KYC;
						}
					} else if (umbrellaprodtypecount >= 1) {
						prompt = " an Umbrella ";
						produtType = Constants.UMBRELLA_PRODUCTTYPECOUNT_KYC;
					}
					String lang = (String) data.getSessionData("S_PREF_LANG");

					if ("SP".equalsIgnoreCase(lang)) {
						if (prompt.contains("Auto policy")) {
							prompt = prompt.replace("Auto Policy", "póliza de auto");
						}
						if (prompt.contains("Home Policy")) {
							prompt = prompt.replace("Home Policy", "póliza de hogar");
						}
						if (prompt.contains("Marine Watercraft policy")) {
							prompt = prompt.replace("Marine Watercraft policy", "embarcación marina");
						}
						if (prompt.contains("Travel Trailer Policy")) {
							prompt = prompt.replace("Travel Trailer Policy", "remolque de viaje");
						}
						if (prompt.contains("Motor Home Policy")) {
							prompt = prompt.replace("Motor Home Policy", "casa rodante");
						}
						if (prompt.contains("Motorcycle Policy")) {
							prompt = prompt.replace("Motorcycle Policy", "motocicleta");
						}
						if (prompt.contains("Off Road Vehicle Policy")) {
							prompt = prompt.replace("Off Road Vehicle Policy", "vehículo todoterreno");
						}
						if (prompt.contains("Umbrella Policy")) {
							prompt = prompt.replace("Umbrella Policy", "póliza paraguas");
						}
						if (prompt.contains("Manufactured Home")) {
							prompt = prompt.replace("Manufactured Home", "Su casa prefabricada");
						}
						if (prompt.contains("Rental Manufactured Home")) {
							prompt = prompt.replace("Rental Manufactured Home", "casa prefabricada de alquiler");
						}
						if (prompt.contains("Specialty Home Owners")) {
							prompt = prompt.replace("Specialty Home Owners", "póliza de vivienda especial");
						}
					}
                    
					HashMap<String, PolicyBean> policyDetailsMap = new HashMap<String, PolicyBean>();
					for (String key : productPolicyMap.keySet()) {
						policyDetailsMap = productPolicyMap.get(key);
					}
					data.setSessionData(Constants.POLICY_DETAILS_FOR_MN3, policyDetailsMap);

					
					if (prompt.contains(" ")) {
						prompt = prompt.replaceAll(" ", ".");
					}

					data.setSessionData(Constants.VXMLParam1, prompt);
					data.setSessionData("MDM_VXMLParam1", prompt);
					data.setSessionData(Constants.VXMLParam2, "NA");
					data.setSessionData(Constants.VXMLParam3, "NA");
					data.setSessionData(Constants.VXMLParam4, "NA");
					data.addToLog(currElementName, "Policies Dynamic Product Type Prompts = " + prompt);
					data.addToLog(currElementName, "VXMLPARAM1 = " + data.getSessionData(Constants.VXMLParam1));

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
			data.addToLog(currElementName, "Exception in MdmFWSPhoneNoPolicyLookup method  :: " + e);
			caa.printStackTrace(e);
		}
		return strExitState;

	}

	private JSONArray filterDuplicatePoliciesByNumber(JSONArray policiesArr) {
		HashSet<String> uniquePolicyLOBKeys = new HashSet<>();
		JSONArray filteredPolicies = new JSONArray();

		for (int i = 0; i < policiesArr.size(); i++) {
			JSONObject policyDataObj = (JSONObject) policiesArr.get(i);
			String policyNumber = (String) policyDataObj.get("policyNumber");
//			String lineOfBusiness = (String) policyDataObj.get("lineOfBusiness");
			// Check that both are not null
			if (policyNumber != null) {
				String key = policyNumber;
				if (uniquePolicyLOBKeys.add(key)) {
					filteredPolicies.add(policyDataObj);
				}
			}
		}

		return filteredPolicies;
	}

	public boolean checkifsingleproducttype(int autoprodtypecount, int homeprodtypecount) {
		int nonZeroCount = 0;

		if (autoprodtypecount != 0) {
			nonZeroCount++;
		}
		if (homeprodtypecount != 0) {
			nonZeroCount++;
		}
		// Return true only if there's exactly one non-zero element
		return nonZeroCount == 1;
	}

	public boolean checkifsingleproducttype(int rvprodtypecount, int autoprodtypecount, int homeprodtypecount,
			int umbrellaprodtypecount, int mrprodtypecount, int spprodtypecount, int ttprodtypecount, int mhprodtypecount, int mcprodtypecount, int offprodtypecount, int mobprodtypecount, int renprodtypecount, int sphprodtypecount) {
		int nonZeroCount = 0;
//		if (rvprodtypecount != 0) {
//			nonZeroCount++;
//		}
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
//		if (spprodtypecount != 0) {
//			nonZeroCount++;
//		}
		
		//new specialty products
		if(ttprodtypecount!=0) {
			nonZeroCount++;
		}
		if(mhprodtypecount!=0) {
			nonZeroCount++;
		}
		if(mcprodtypecount!=0) {
			nonZeroCount++;
		}
		if(offprodtypecount!=0) {
			nonZeroCount++;
		}
		if(mobprodtypecount!=0) {
			nonZeroCount++;
		}
		if(renprodtypecount!=0) {
			nonZeroCount++;
		}
		if(sphprodtypecount!=0) {
			nonZeroCount++;
		}
		
		// Return true only if there's exactly one non-zero element
		return nonZeroCount == 1;
	}

	// CS1151307 : Update Policy State Name
	public String setStateName(DecisionElementData data, CommonAPIAccess caa, String currElementName,
			String strPolicyState) {

		String strExitState = Constants.EMPTY;

		try {
			// Get state name from session
			strExitState = (String) data.getSessionData(Constants.S_STATENAME);
			data.addToLog("Caller State Name :::::", strExitState);

			// Get state code and policy state code from session
			String strStateCode = (String) data.getSessionData(Constants.S_STATECODE);
			String strStateValue = (String) data.getSessionData(Constants.S_POLICY_STATE_CODE);

			// Check if state code and policy state code are not null
			if (strStateCode != null && strStateValue != null) {
				data.addToLog(currElementName, "strStateCode : " + strStateCode);
				data.addToLog(currElementName, "strPolicyStateCode :: " + strStateValue);

				// Check if state code matches policy state code
				if (strStateCode.equalsIgnoreCase(strStateValue)) {
					strExitState = (String) data.getSessionData(Constants.S_STATENAME);
					data.addToLog(currElementName, "State Name : " + strExitState);

				} else {
					// Retrieve configuration state code from session data
					String strConfigState = (String) data.getSessionData("S_CONFIG_STATE_CODE");
					data.addToLog(currElementName, "Config State Code :::: " + strConfigState);

					// Initialize a map to store state codes and their corresponding values
					HashMap<String, String> stateMap = new HashMap<>();

					// Split the configuration state code string and populate the map
					if (strConfigState != null) {
						for (String strObject : strConfigState.split("\\|")) {
							data.addToLog(currElementName, "State Code Object ::::: " + strObject);
							String[] keyValue = strObject.split(":");
							if (keyValue.length == 2) {
								stateMap.put(keyValue[0], keyValue[1]);
							} else {
								data.addToLog(currElementName, "Invalid State Code Object :: " + strObject);
							}
						}

						// Check if the state map contains the state value
						if (stateMap.containsValue(strStateValue)) {
							for (Map.Entry<String, String> entry : stateMap.entrySet()) {
								String key = entry.getKey();
								String value = entry.getValue();

								// If the value matches the state value, update the session data
								if (value.equalsIgnoreCase(strStateValue)) {
									data.setSessionData(Constants.S_POLICY_STATE_CODE, strStateValue);
									data.setSessionData(Constants.S_STATENAME, key);
									strExitState = (String) data.getSessionData(Constants.S_STATENAME);
									data.addToLog(currElementName,
											"Caller State Name after verification :" + strExitState);
									break; // Exit loop after first match
								}
							}
						} else {
							data.addToLog(currElementName, "State Value not found in the config state map");
						}
					} else {
						data.addToLog(currElementName, "Config State Code is null");
					}
				}
			} else {
				data.addToLog(currElementName, "State Code or Policy State Code is null");
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in processConfigState :: " + e.getMessage());
			e.printStackTrace();
		}
		return strExitState;
	}

	// CS1200021 | Farmers Insurance | US | Farmers Service (FDS) - Add Routing for
	// 'True Direct' HouseAccountSubCategor

	public String setSubcategory(DecisionElementData data, CommonAPIAccess caa, String currElementName,
			String strapiSubcategory) {

		// Retrieve the configuration categories from the ivrconfig properties
		String configCategories = (String) data.getSessionData("S_CONFIG_CATEGORIES");
		data.addToLog(currElementName, "configCategories : " + configCategories);
		String strsubCategory = (String) data.getSessionData("S_API_SUBCATEGORY");
		data.addToLog(currElementName, "API subCategory : " + strsubCategory);

		try {

			// Check if the configCategories array is valid before proceeding for matching
			// with ivrconfig properties

			if (configCategories != null) {

				for (String subcategory : configCategories.split("\\|")) {

					if (null != strsubCategory && strsubCategory.equalsIgnoreCase(subcategory)) {
						data.addToLog(currElementName, "Inside method subcategory : " + subcategory);

						return strsubCategory;
					}
				}
			}

			// If no matching subcategory is found in ivrconfig properties, return this
			// message
			return "NA";

		} catch (Exception e) {
			// Log the exception if necessary and return error message
			return "ER";
		}
	}

	public static String formatDate(DecisionElementData data, String dateStr, String inputFormat, String outputFormat) {

		data.addToLog(data.getCurrentElement(), "Inside method formatDate : " + dateStr);
		data.addToLog(data.getCurrentElement(), "Inside method formatDate : " + inputFormat);
		data.addToLog(data.getCurrentElement(), "Inside method formatDate : " + outputFormat);

		if (dateStr == null || inputFormat == null || outputFormat == null) {
			return "EmptyDate"; // Exception will occurred based on requirement

		}

		SimpleDateFormat inputSDF = new SimpleDateFormat(inputFormat);
		SimpleDateFormat outputSDF = new SimpleDateFormat(outputFormat);

		data.addToLog(data.getCurrentElement(), "SimpleDateFormat inputSDF : " + inputSDF);
		data.addToLog(data.getCurrentElement(), "SimpleDateFormat outputSDF : " + outputSDF);

		try {
			// Input date
			Date date = inputSDF.parse(dateStr);
			data.addToLog(data.getCurrentElement(), "Date : " + date);

			return outputSDF.format(date);

		} catch (Exception e) {

			return e.getMessage(); // or throw an exception, depending on your requirements
		}
	}

//END : CS1245054:Mdm FWS PhoneNoLookup changes

	public static void main(String[] args) {

		String lob = "Foremost";
		String list = "Foremost AutoBristol West";
		String policynumber = "103700043593901";
		if (policynumber.length() == 12) {
			policynumber = policynumber.substring(0, 10);
		} else if (policynumber.length() == 13) {
			policynumber = policynumber.substring(3, 13);
		} else if (policynumber.length() == 15) {
			policynumber = policynumber.substring(3, 13);
		}
		System.out.println(policynumber);
	}

}