package com.farmers.shared.host;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
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

public class PolicyLookupVIA extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);
		String strReqBody = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;
		try {
			String strBU = (String) data.getSessionData(Constants.S_BU);

			ArrayList<String> strBristolCode = (ArrayList<String>) data.getSessionData("BRISTOL_LOB_LIST");
			ArrayList<String> strFarmersCode = (ArrayList<String>) data.getSessionData("FARMER_LOB_LIST");
			ArrayList<String> strForemostCode = (ArrayList<String>) data.getSessionData("FOREMOST_LOB_LIST");
			ArrayList<String> strFWSCode = (ArrayList<String>) data.getSessionData("FWS_LOB_LIST");
			ArrayList<String> str21stCode = (ArrayList<String>) data.getSessionData("21st_LOB_LIST");

			data.addToLog(currElementName, " PolicyLookup : strBU :: " + strBU);
			data.addToLog(currElementName, " A_BRISTOL_LOB : " + strBristolCode);
			data.addToLog(currElementName, " A_FARMERS_LOB : " + strFarmersCode);
			data.addToLog(currElementName, " A_FOREMOST_LOB : " + strForemostCode);
			data.addToLog(currElementName, " A_FWS_LOB : " + strFWSCode);
			data.addToLog(currElementName, " A_21ST_LOB : " + str21stCode);

			if (strBristolCode != null && null != strBU && strBristolCode.contains(strBU)) {
				data.setSessionData("S_FLAG_BW_BU", Constants.STRING_YES);
				strExitState = policyInquiry_RetriveInsurancePoliciesByParty(strRespBody, strRespBody, data, caa,
						currElementName);
			} else if (strFarmersCode != null && strBU != null && strFarmersCode.contains(strBU)) {
				// S_FLAG_FDS_BU
				data.setSessionData("S_FLAG_FDS_BU", Constants.STRING_YES);
				strExitState = mulesoftFarmerPolicyInquiry(strRespBody, strRespBody, data, caa, currElementName);
			} else if (strForemostCode != null && strBU != null && strForemostCode.contains(strBU)) {
				data.addToLog(currElementName, "Its foremost LOB");
				strExitState = foremostPolicyInquiry(strRespBody, strRespBody, data, caa, currElementName);
				data.setSessionData("S_FLAG_FOREMOST_BU", Constants.STRING_YES);
			} else if (strFWSCode != null && strBU != null && strFWSCode.contains(strBU)) {
				strExitState = fwsPolicyLookup(strRespBody, strRespBody, data, caa, currElementName);
				data.setSessionData("S_FLAG_FWS_BU", Constants.STRING_YES);
			} else if (str21stCode != null && strBU != null && str21stCode.contains(strBU)) {
				strExitState = PointPolicyInquiry(strRespBody, strRespBody, data, caa, currElementName);
				data.setSessionData("S_FLAG_21ST_BU", Constants.STRING_YES);
			} else {
				strExitState = fwsPolicyLookup(strRespBody, strRespBody, data, caa, currElementName);
			}
			data.addToLog(currElementName,
					"Bristol Flag :: " + data.getSessionData("S_FLAG_BW_BU") + " :: Farmers Flag :: "
							+ data.getSessionData("S_FLAG_FDS_BU") + " :: Foremost Flag :: "
							+ data.getSessionData("S_FLAG_FOREMOST_BU") + " :: FWS Flag :: "
							+ data.getSessionData("S_FLAG_FWS_BU") + " :: 21st Flag :: "
							+ data.getSessionData("S_FLAG_21ST_BU"));
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

	private String PointPolicyInquiry(String strRespBody, String strRespBody2, DecisionElementData data,
			CommonAPIAccess caa, String currElementName) {
		String strExitState = Constants.ER, strReqBody = Constants.EMPTY;
		boolean startsWithAlphabets = false;
		SetHostDetails objHostDetails = new SetHostDetails(caa);
		objHostDetails.setinitalValue();

		// Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;

		// START- UAT ENV SETUP CODE (PRIYA, SHAIK)
		String region = null;
		HashMap<String, String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
		// END- UAT ENV SETUP CODE (PRIYA, SHAIK)
		try {
			if (data.getSessionData(Constants.S_POINT_POLICYINQUIIRY_URL) != null) {
				String wsurl = (String) data.getSessionData(Constants.S_POINT_POLICYINQUIIRY_URL);
				String input = (String) data.getSessionData(Constants.S_POLICY_NUM);
				if (input != null) {
					startsWithAlphabets = Character.isLetter(input.charAt(0));
					if (startsWithAlphabets) {
						input = input.toUpperCase();
						data.addToLog(currElementName,
								"Policy Number from caller contains Alphabet and so changing the value to Upper Case :: "
										+ input);
						data.setSessionData(Constants.S_POLICY_NUM, input);
					} else {
						data.addToLog(currElementName,
								"Policy Number from caller does not contains Alphabet and so Setting the same value "
										+ input);
					}
				}
				String connTimeout = (String) data.getSessionData(Constants.S_CONN_TIMEOUT);
				String readTimeout = (String) data.getSessionData(Constants.S_READ_TIMEOUT);
				LoggerContext context = (LoggerContext) data.getApplicationAPI()
						.getApplicationData(Constants.A_CONTEXT);
				// https://api-ss.farmersinsurance.com/plcyms/v3/policies/21365978

				if (wsurl != null && input != null) {
					wsurl = wsurl.replace(Constants.S_POLICY_NUM, input);
				}
				data.addToLog(currElementName, "Set wsurl : " + wsurl);

				Lookupcall lookups = new Lookupcall();
				// UAT CHNGE START(SHAIK,PRIYA)
				data.addToLog("API URL: ", wsurl);
				String prefix = "https://api-np-ss.farmersinsurance.com";
				String UAT_FLAG = "";
				if (wsurl.startsWith(prefix)) {
					UAT_FLAG = "YES";
				}

				if ("YES".equalsIgnoreCase(UAT_FLAG)) {
					region = regionDetails.get(Constants.S_POINT_POLICYINQUIIRY_URL);
				} else {
					region = "PROD";
				}

				org.json.simple.JSONObject responses = lookups.Getpointpolicyinquiry(wsurl, input,
						Integer.parseInt(connTimeout), Integer.parseInt(readTimeout), context, region, UAT_FLAG);
				data.addToLog("responses", responses.toString());
				// UAT ENV CHANGE END(SHAIK,PRIYA)

				// Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) responses.get(Constants.RESPONSE_CODE));

				if (responses != null) {
					if (responses.containsKey(Constants.REQUEST_BODY))
						strReqBody = responses.get(Constants.REQUEST_BODY).toString();
					if (responses.containsKey(Constants.RESPONSE_CODE)
							&& (int) responses.get(Constants.RESPONSE_CODE) == 200
							&& responses.containsKey(Constants.RESPONSE_BODY)) {
						data.addToLog(currElementName,
								"Set SIDA_HOST_001 : PointPolicyInquiry �PI Response into session with the key name of "
										+ currElementName + Constants._RESP);
						strRespBody = responses.get(Constants.RESPONSE_BODY).toString();
						data.setSessionData(currElementName + Constants._RESP, responses.get(Constants.RESPONSE_BODY));
						data.setSessionData("SIDA_MN_005_VALUE_RESP", responses.get(Constants.RESPONSE_BODY));
						// CALLER VERIFICATION
						data.setSessionData("SIDA_MN_005_VALUE_RESP_CUST_NEW", responses.get(Constants.RESPONSE_BODY));

						data.addToLog("After setting the value in SIDA_MN_005_VALUE_RESP_CUST_NEW",
								(String) data.getSessionData("SIDA_MN_005_VALUE_RESP_CUST_NEW").toString());

						strExitState = apiResponseManupulation_PointPolicyInquiry(data, caa, currElementName,
								strRespBody);

					} else {
						strRespBody = responses.get(Constants.RESPONSE_MSG).toString();
					}

				}
			}

		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in SIDA_HOST_001  PointPolicyInquiry API call  :: " + e);
			caa.printStackTrace(e);
		}

		try {
			String onBoardingFlag = (String) data.getSessionData(Constants.S_ONBOARDING_ELIGIBLE);
			data.addToLog(currElementName, "The OnBoarding Eligible Flag is ::" + onBoardingFlag);
			String stronBoardingFlag = "ONBOARDING_ELIGIBLE: " + onBoardingFlag;
			if (null != data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE)
					&& (Constants.S_SINGLE_POLICY_FOUND.equalsIgnoreCase(
							(String) data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE))
							|| Constants.S_MULTIPLE_POLICIES_SINGLE_PRODUCTTYPE_FOUND.equalsIgnoreCase(
									(String) data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE))
							|| Constants.S_MULTIPLE_POLICIES_MULTIPLE_PRODUCTTYPE_FOUND.equalsIgnoreCase(
									(String) data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE)))) {
				objHostDetails.startHostReport(currElementName, "PointPolicyInquiry API call", strReqBody, region,
						(String) data.getSessionData(Constants.S_POINT_POLICYINQUIIRY_URL));
				objHostDetails.endHostReport(data, strRespBody,
						strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,
						stronBoardingFlag);
			} else {
				objHostDetails.startHostReport(currElementName, "PointPolicyInquiry API call", strReqBody, region,
						(String) data.getSessionData(Constants.S_POINT_POLICYINQUIIRY_URL));
				objHostDetails.endHostReport(data, "DB-mismatch : " + strRespBody,
						strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,
						stronBoardingFlag);
			}
		} catch (Exception e) {
			data.addToLog(currElementName,
					"Exception while forming host reporting for GetMainTableByBusinessObjects API call  :: " + e);
			caa.printStackTrace(e);
		}

		return strExitState;
	}

	private String fwsPolicyLookup(String strRespBody, String strRespBody2, DecisionElementData data,
			CommonAPIAccess caa, String currElementName) {
		String strExitState = Constants.ER, strReqBody = Constants.EMPTY;
		;
		SetHostDetails objHostDetails = new SetHostDetails(caa);
		objHostDetails.setinitalValue();
		boolean startsWithAlphabets = false;
		String policyContractNumber = Constants.EmptyString;

		// Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;

		// START- UAT ENV SETUP CODE (PRIYA, SHAIK)
		String region = null;
		HashMap<String, String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
		// END- UAT ENV SETUP CODE (PRIYA, SHAIK)
		try {
			if (data.getSessionData(Constants.S_FWS_POLICYLOOKUP_URL) != null) {
				String wsurl = (String) data.getSessionData(Constants.S_FWS_POLICYLOOKUP_URL);
				data.addToLog(currElementName, " S_FWS_POLICYLOOKUP_URL : " + wsurl);
				if (null != data.getSessionData(Constants.S_POLICY_NUM)
						&& !((String) data.getSessionData(Constants.S_POLICY_NUM)).isEmpty()) {
					policyContractNumber = (String) data.getSessionData(Constants.S_POLICY_NUM);
					startsWithAlphabets = Character.isLetter(policyContractNumber.charAt(0));

					if (startsWithAlphabets) {
						data.addToLog(currElementName, "The string starts with an alphabet.");
						policyContractNumber = policyContractNumber.substring(1, policyContractNumber.length());
						data.addToLog(currElementName, "Post Sub String :: " + policyContractNumber);
						data.setSessionData(Constants.S_POLICY_NUM, policyContractNumber);
					} else {
						data.addToLog(currElementName, "The string does not start with an alphabet.");
					}
				}
				String billingAccountNumber = (String) data.getSessionData(Constants.S_BILLING_ACC_NUM);
				String telephoneNumber = (String) data.getSessionData(Constants.S_ANI);
				if (data.getSessionData(Constants.S_TELEPHONENUMBER) != null
						&& !data.getSessionData(Constants.S_TELEPHONENUMBER).equals("")) {
					telephoneNumber = (String) data.getSessionData(Constants.S_TELEPHONENUMBER);
				}
				String connTimeout = (String) data.getSessionData(Constants.S_CONN_TIMEOUT);
				String readTimeout = (String) data.getSessionData(Constants.S_READ_TIMEOUT);
				String tid = (String) data.getSessionData(Constants.S_CALLID);
				LoggerContext context = (LoggerContext) data.getApplicationAPI()
						.getApplicationData(Constants.A_CONTEXT);
				data.addToLog(currElementName, "S_POLICY_NUM : " + policyContractNumber + " :: S_BILLING_ACC_NUM : "
						+ billingAccountNumber + " :: S_ANI : " + telephoneNumber);
				Lookupcall lookups = new Lookupcall();
				// UAT ENV CHANGE(SHAIK,PRIYA)

				data.addToLog("API URL: ", wsurl);
				String prefix = "https://api-np-ss.farmersinsurance.com";
				String UAT_FLAG = "";
				if (wsurl.startsWith(prefix)) {
					UAT_FLAG = "YES";
				}
				if ("YES".equalsIgnoreCase(UAT_FLAG)) {
					region = regionDetails.get(Constants.S_FWS_POLICYLOOKUP_URL);
				} else {
					region = "PROD";
				}

				org.json.simple.JSONObject responses = null;
				if (policyContractNumber != null && !policyContractNumber.isEmpty()) {
					responses = lookups.GetFWSPolicyLookup(wsurl, tid, policyContractNumber, null, null,
							Integer.parseInt(connTimeout), Integer.parseInt(readTimeout), context, region, UAT_FLAG);
				} else if (billingAccountNumber != null && !billingAccountNumber.equals("")) {
					responses = lookups.GetFWSPolicyLookup(wsurl, tid, null, billingAccountNumber, null,
							Integer.parseInt(connTimeout), Integer.parseInt(readTimeout), context, region, UAT_FLAG);
				} else {
					responses = lookups.GetFWSPolicyLookup(wsurl, tid, null, null, telephoneNumber,
							Integer.parseInt(connTimeout), Integer.parseInt(readTimeout), context, region, UAT_FLAG);
				}
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
								"Set SIDA_HOST_001 : FWSPolicyLookup API Response into session with the key name of "
										+ currElementName + Constants._RESP);
						strRespBody = responses.get(Constants.RESPONSE_BODY).toString();
						data.setSessionData(currElementName + Constants._RESP, responses.get(Constants.RESPONSE_BODY));
						data.setSessionData("SIDA_MN_005_VALUE_RESP", responses.get(Constants.RESPONSE_BODY));
						// CALLER VERIFICATION
						data.setSessionData("SIDA_MN_005_VALUE_RESP_CUST_NEW", responses.get(Constants.RESPONSE_BODY));

						data.addToLog("After setting the value in SIDA_MN_005_VALUE_RESP_CUST_NEW",
								(String) data.getSessionData("SIDA_MN_005_VALUE_RESP_CUST_NEW").toString());

						strExitState = apiResponseManupulation_FwsPolicyLookup(data, caa, currElementName, strRespBody);

					} else {
						strRespBody = responses.get(Constants.RESPONSE_MSG).toString();
					}

				}
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in SIDA_HOST_001  FWSPolicyLookup API call  :: " + e);
			caa.printStackTrace(e);
		}

		try {
			String onBoardingFlag = (String) data.getSessionData(Constants.S_ONBOARDING_ELIGIBLE);
			data.addToLog(currElementName, "The OnBoarding Eligible Flag is ::" + onBoardingFlag);
			String stronBoardingFlag = "ONBOARDING_ELIGIBLE: " + onBoardingFlag;
			if (null != data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE)
					&& (Constants.S_SINGLE_POLICY_FOUND.equalsIgnoreCase(
							(String) data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE))
							|| Constants.S_MULTIPLE_POLICIES_SINGLE_PRODUCTTYPE_FOUND.equalsIgnoreCase(
									(String) data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE))
							|| Constants.S_MULTIPLE_POLICIES_MULTIPLE_PRODUCTTYPE_FOUND.equalsIgnoreCase(
									(String) data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE)))) {
				objHostDetails.startHostReport(currElementName, "FWSPolicyLookup API call", strReqBody, region,
						(String) data.getSessionData(Constants.S_FWS_POLICYLOOKUP_URL));
				objHostDetails.endHostReport(data, strRespBody,
						strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,
						stronBoardingFlag);
			} else {
				objHostDetails.startHostReport(currElementName, "FWSPolicyLookup API call", strReqBody, region,
						(String) data.getSessionData(Constants.S_FWS_POLICYLOOKUP_URL));
				objHostDetails.endHostReport(data, "DB-mismatch : " + strRespBody,
						strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,
						stronBoardingFlag);
			}

		} catch (Exception e) {
			data.addToLog(currElementName,
					"Exception while forming host reporting for GetMainTableByBusinessObjects API call  :: " + e);
			caa.printStackTrace(e);
		}

		return strExitState;
	}

	private String foremostPolicyInquiry(String strRespBody, String strRespBody2, DecisionElementData data,
			CommonAPIAccess caa, String currElementName) {
		String strExitState = Constants.ER, strReqBody = Constants.EMPTY;
		boolean startsWithAlphabets = false;
		SetHostDetails objHostDetails = new SetHostDetails(caa);
		objHostDetails.setinitalValue();

		// Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;

		// START- UAT ENV SETUP CODE (PRIYA, SHAIK)
		String region = null;
		HashMap<String, String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
		// END- UAT ENV SETUP CODE (PRIYA, SHAIK)
		try {
			if (data.getSessionData(Constants.S_FOREMOST_POLICYINQUIIRY_URL) != null) {
				String url = (String) data.getSessionData(Constants.S_FOREMOST_POLICYINQUIIRY_URL);
				String policynumber = (String) data.getSessionData(Constants.S_POLICY_NUM);
				startsWithAlphabets = Character.isLetter(policynumber.charAt(0));
				if (policynumber != null) {
					if (startsWithAlphabets) {
						policynumber = policynumber.toUpperCase();
						data.addToLog(currElementName,
								"Policy Number from caller contains Alphabet and so changing the value to Upper Case :: "
										+ policynumber);
						data.setSessionData(Constants.S_POLICY_NUM, policynumber);
					} else {
						data.addToLog(currElementName,
								"Policy Number from caller does not contains Alphabet and so Setting the same value "
										+ policynumber);
					}
				}
				// Foremost policies are 15 digits but caller can enter the full 15 digits, 13
				// digits, 12 digits, OR 10 digits.
				if (policynumber.length() == 12) {
					policynumber = policynumber.substring(0, 10);
					data.addToLog(currElementName,
							"Considered 10 digit policynumber from 12 digits :: " + policynumber);
				} else if (policynumber.length() == 13) {
					data.setSessionData("S_EPC_PAYMENT_POLICYNUM", policynumber.substring(0, 13));
					policynumber = policynumber.substring(3, 13);
					data.addToLog(currElementName,
							"Considered 10 digit policynumber from 13 digits :: " + policynumber);
				} else if (policynumber.length() == 15) {
					data.setSessionData("S_EPC_PAYMENT_POLICYNUM", policynumber.substring(0, 13));
					policynumber = policynumber.substring(3, 13);
					data.addToLog(currElementName,
							"Considered 10 digit policynumber from 15 digits :: " + policynumber);
				}

				String systemDate = new SimpleDateFormat("MM/dd/yyyy").format(new Date());
				String policysource = "Foremost";
				String connTimeout = (String) data.getSessionData(Constants.S_CONN_TIMEOUT);
				String readTimeout = (String) data.getSessionData(Constants.S_READ_TIMEOUT);
				String tid = (String) data.getSessionData(Constants.S_CALLID);
				LoggerContext context = (LoggerContext) data.getApplicationAPI()
						.getApplicationData(Constants.A_CONTEXT);
				// https://api-ss.farmersinsurance.com/plcyms/v3/policies?operation=searchByPolicySource
				Lookupcall lookups = new Lookupcall();
				data.addToLog("API URL: ", url);
				String prefix = "https://api-np-ss.farmersinsurance.com";
				String UAT_FLAG = "";
				if (url.startsWith(prefix)) {
					UAT_FLAG = "YES";
				}
				// UAT ENV CHANGE START(SHAIK,PRIYA)
				if ("YES".equalsIgnoreCase(UAT_FLAG)) {
					region = regionDetails.get(Constants.S_FOREMOST_POLICYINQUIIRY_URL);
				} else {
					region = "PROD";
				}
				org.json.simple.JSONObject responses = lookups.GetforemostPolicyInquiry(url, tid, policynumber,
						systemDate, policysource, Integer.parseInt(connTimeout), Integer.parseInt(readTimeout), context,
						region, UAT_FLAG);
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
								"Set SIDA_HOST_001 : ForemostPolicyInquiry API Response into session with the key name of "
										+ currElementName + Constants._RESP);
						strRespBody = responses.get(Constants.RESPONSE_BODY).toString();
						data.setSessionData(currElementName + Constants._RESP, responses.get(Constants.RESPONSE_BODY));
						data.setSessionData("SIDA_MN_005_VALUE_RESP", responses.get(Constants.RESPONSE_BODY));
						// CALLER VERIFICATION
						data.setSessionData("SIDA_MN_005_VALUE_RESP_CUST_NEW", responses.get(Constants.RESPONSE_BODY));

						data.addToLog("After setting the value in SIDA_MN_005_VALUE_RESP_CUST_NEW",
								(String) data.getSessionData("SIDA_MN_005_VALUE_RESP_CUST_NEW").toString());

						strExitState = apiResponseManupulation_ForemostPolicyInquiry(data, caa, currElementName,
								strRespBody);

					} else {
						strRespBody = responses.get(Constants.RESPONSE_MSG).toString();
					}

				}
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in SIDA_HOST_001  ForemostPolicyInquiry API call  :: " + e);
			caa.printStackTrace(e);
		}

		try {
			String onBoardingFlag = (String) data.getSessionData(Constants.S_ONBOARDING_ELIGIBLE);
			data.addToLog(currElementName, "The OnBoarding Eligible Flag is ::" + onBoardingFlag);
			String stronBoardingFlag = "ONBOARDING_ELIGIBLE: " + onBoardingFlag;
			if (null != data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE)
					&& (Constants.S_SINGLE_POLICY_FOUND.equalsIgnoreCase(
							(String) data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE))
							|| Constants.S_MULTIPLE_POLICIES_SINGLE_PRODUCTTYPE_FOUND.equalsIgnoreCase(
									(String) data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE))
							|| Constants.S_MULTIPLE_POLICIES_MULTIPLE_PRODUCTTYPE_FOUND.equalsIgnoreCase(
									(String) data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE)))) {
				objHostDetails.startHostReport(currElementName, "ForemostPolicyInquiry API call", strReqBody, region,
						(String) data.getSessionData(Constants.S_FOREMOST_POLICYINQUIIRY_URL));
				objHostDetails.endHostReport(data, strRespBody,
						strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,
						stronBoardingFlag);
			} else {
				objHostDetails.startHostReport(currElementName, "ForemostPolicyInquiry API call", strReqBody, region,
						(String) data.getSessionData(Constants.S_FOREMOST_POLICYINQUIIRY_URL));
				objHostDetails.endHostReport(data, "DB-mismatch : " + strRespBody,
						strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,
						stronBoardingFlag);
			}

		} catch (Exception e) {
			data.addToLog(currElementName,
					"Exception while forming host reporting for GetMainTableByBusinessObjects API call  :: " + e);
			caa.printStackTrace(e);
		}
		return strExitState;
	}

	private String mulesoftFarmerPolicyInquiry(String strRespBody, String strRespBody2, DecisionElementData data,
			CommonAPIAccess caa, String currElementName) {
		String strExitState = Constants.ER, strReqBody = Constants.EMPTY;
		SetHostDetails objHostDetails = new SetHostDetails(caa);
		objHostDetails.setinitalValue();
		boolean startsWithAlphabets = false;
		// Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;

		// START- UAT ENV SETUP CODE (PRIYA, SHAIK)
		String region = null;
		HashMap<String, String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
		// END- UAT ENV SETUP CODE (PRIYA, SHAIK)
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
			String onBoardingFlag = (String) data.getSessionData(Constants.S_ONBOARDING_ELIGIBLE);
			data.addToLog(currElementName, "The OnBoarding Eligible Flag is ::" + onBoardingFlag);
			String stronBoardingFlag = "ONBOARDING_ELIGIBLE: " + onBoardingFlag;
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
						stronBoardingFlag);
			} else {
				objHostDetails.startHostReport(currElementName, "MulesoftFarmerPolicyInquiry_Post API call", strReqBody,
						region, (String) data.getSessionData(Constants.S_MULESOFT_FARMER_POLICYINQUIRY_URL));
				objHostDetails.endHostReport(data, "DB-mismatch : " + strRespBody,
						strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,
						stronBoardingFlag);
			}

		} catch (Exception e) {
			data.addToLog(currElementName,
					"Exception while forming host reporting for MulesoftFarmerPolicyInquiry_Post API call  :: " + e);
			caa.printStackTrace(e);
		}

		return strExitState;

	}

	private String policyInquiry_RetriveInsurancePoliciesByParty(String strRespBody, String strRespBody2,
			DecisionElementData data, CommonAPIAccess caa, String currElementName) {
		String strExitState = Constants.ER, strReqBody = Constants.EMPTY;
		SetHostDetails objHostDetails = new SetHostDetails(caa);
		objHostDetails.setinitalValue();

		// Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;

		// START- UAT ENV SETUP CODE (PRIYA, SHAIK)
		String region = null;
		HashMap<String, String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
		// END- UAT ENV SETUP CODE (PRIYA, SHAIK)
		try {
			if (data.getSessionData(Constants.S_POLICYINQUIRY_RETRIVEINSURANCEPOLICIESBYPARTY_URL) != null) {
				String wsurl = (String) data
						.getSessionData(Constants.S_POLICYINQUIRY_RETRIVEINSURANCEPOLICIESBYPARTY_URL);
				String phoneNo = (String) data.getSessionData(Constants.S_ANI);
				if (data.getSessionData(Constants.S_TELEPHONENUMBER) != null
						&& !data.getSessionData(Constants.S_TELEPHONENUMBER).equals("")) {
					phoneNo = (String) data.getSessionData(Constants.S_TELEPHONENUMBER);
				}
				String policyNum = (String) data.getSessionData(Constants.S_POLICY_NUM);
				String connTimeout = (String) data.getSessionData(Constants.S_CONN_TIMEOUT);
				String readTimeout = (String) data.getSessionData(Constants.S_READ_TIMEOUT);
				String tid = (String) data.getSessionData(Constants.S_CALLID);
				Lookupcall lookups = new Lookupcall();

				// UAT ENV CHANGE START(SHIAK,PRIYA)
				data.addToLog("API URL: ", wsurl);
				String prefix = "https://api-np-ss.farmersinsurance.com";
				String UAT_FLAG = "";
				if (wsurl.startsWith(prefix)) {
					UAT_FLAG = "YES";
				}
				if ("YES".equalsIgnoreCase(UAT_FLAG)) {
					region = regionDetails.get(Constants.S_POLICYINQUIRY_RETRIVEINSURANCEPOLICIESBYPARTY_URL);
				} else {
					region = "PROD";
				}

				LoggerContext context = (LoggerContext) data.getApplicationAPI()
						.getApplicationData(Constants.A_CONTEXT);
				org.json.simple.JSONObject responses = lookups.GetPolicyInquiry_RetrieveInsurance(wsurl, tid, phoneNo,
						Integer.parseInt(connTimeout), Integer.parseInt(readTimeout), context, region, UAT_FLAG);
				data.addToLog("responses", responses.toString());
				// UAT CHANGE END(SHAIK,PRIYA)

				// Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) responses.get(Constants.RESPONSE_CODE));
				if (responses != null) {
					if (responses.containsKey(Constants.REQUEST_BODY))
						strReqBody = responses.get(Constants.REQUEST_BODY).toString();
					if (responses.containsKey(Constants.RESPONSE_CODE)
							&& (int) responses.get(Constants.RESPONSE_CODE) == 200
							&& responses.containsKey(Constants.RESPONSE_BODY)) {
						data.addToLog(currElementName,
								"Set SIDA_HOST_001 : GetPolicyInquiry_RetrieveInsurance API Response into session with the key name of "
										+ currElementName + Constants._RESP);
						strRespBody = responses.get(Constants.RESPONSE_BODY).toString();
						data.setSessionData(currElementName + Constants._RESP, responses.get(Constants.RESPONSE_BODY));
						data.setSessionData("SIDA_MN_005_VALUE_RESP", responses.get(Constants.RESPONSE_BODY));
						// CALLER VERIFICATION
						data.setSessionData("SIDA_MN_005_VALUE_RESP_CUST_NEW", responses.get(Constants.RESPONSE_BODY));

						data.addToLog("After setting the value in SIDA_MN_005_VALUE_RESP_CUST_NEW",
								(String) data.getSessionData("SIDA_MN_005_VALUE_RESP_CUST_NEW").toString());

						strExitState = apiResponseManupulation_RetriveInsurancePoliciesByParty(data, caa,
								currElementName, strRespBody, policyNum);

					} else {
						strRespBody = responses.get(Constants.RESPONSE_MSG).toString();
					}

				}

			}
		} catch (Exception e) {
			data.addToLog(currElementName,
					"Exception in SIDA_HOST_001  RetrieveIntermediarySegmentationInfo API call  :: " + e);
			caa.printStackTrace(e);
		}

		try {
			String onBoardingFlag = (String) data.getSessionData(Constants.S_ONBOARDING_ELIGIBLE);
			data.addToLog(currElementName, "The OnBoarding Eligible Flag is ::" + onBoardingFlag);
			String stronBoardingFlag = "ONBOARDING_ELIGIBLE: " + onBoardingFlag;
			if (null != data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE)
					&& (Constants.S_SINGLE_POLICY_FOUND.equalsIgnoreCase(
							(String) data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE))
							|| Constants.S_MULTIPLE_POLICIES_SINGLE_PRODUCTTYPE_FOUND.equalsIgnoreCase(
									(String) data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE))
							|| Constants.S_MULTIPLE_POLICIES_MULTIPLE_PRODUCTTYPE_FOUND.equalsIgnoreCase(
									(String) data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE)))) {
				objHostDetails.startHostReport(currElementName,
						"BW policyinquiry-retrieveInsurancePoliciesByParty API call", strReqBody, region,
						data.getSessionData(Constants.S_POLICYINQUIRY_RETRIVEINSURANCEPOLICIESBYPARTY_URL).toString()
								.length() > 99
										? (String) data
												.getSessionData(
														Constants.S_POLICYINQUIRY_RETRIVEINSURANCEPOLICIESBYPARTY_URL)
												.toString().substring(0, 99)
										: (String) data.getSessionData(
												Constants.S_POLICYINQUIRY_RETRIVEINSURANCEPOLICIESBYPARTY_URL));
				objHostDetails.endHostReport(data, strRespBody,
						strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,
						stronBoardingFlag);
			} else {
				objHostDetails.startHostReport(currElementName,
						"BW policyinquiry-retrieveInsurancePoliciesByParty API call", strReqBody, region,
						data.getSessionData(Constants.S_POLICYINQUIRY_RETRIVEINSURANCEPOLICIESBYPARTY_URL).toString()
								.length() > 99
										? (String) data
												.getSessionData(
														Constants.S_POLICYINQUIRY_RETRIVEINSURANCEPOLICIESBYPARTY_URL)
												.toString().substring(0, 99)
										: (String) data.getSessionData(
												Constants.S_POLICYINQUIRY_RETRIVEINSURANCEPOLICIESBYPARTY_URL));
				objHostDetails.endHostReport(data, "DB-mismatch : " + strRespBody,
						strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,
						stronBoardingFlag);
			}

		} catch (Exception e) {
			data.addToLog(currElementName,
					"Exception while forming host reporting for GetMainTableByBusinessObjects API call  :: " + e);
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
			// START: CS1310685 , Short description: Farmers IVR - Farmers Policy Lookup API
			// (Host_Method: MulesoftFarmerPolicyInquiry_Post API call) is returning FWS
			// policies
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
			} // END: CS1310685 , Short description: Farmers IVR - Farmers Policy Lookup API
				// (Host_Method: MulesoftFarmerPolicyInquiry_Post API call) is returning FWS
				// policies

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
				Map<String, Integer> productTypeCounts = new HashMap<>();
				HashMap<String, HashMap<String, PolicyBean>> productPolicyMap = new HashMap<String, HashMap<String, PolicyBean>>();

				for (int i = 0; i < policiesArr.size(); i++) {
					JSONObject policyData = (JSONObject) policiesArr.get(i);
					PolicyBean beanObj = new PolicyBean();
					String strPolicyNum = Constants.EmptyString;
					// Start CS1175332-Farmers Direct Services (FDS/Personal Lines) - Billing &
					// Payments - CA Payroll Deduct
					String gpcCode = (String) policyData.get("GPC");
					String servicingPhoneNumber = (String) policyData.get("servicingPhoneNumber");
					strpayPlan = (String) policyData.get("payPlan");
					beanObj.setGpcCode(gpcCode);
					beanObj.setPayPlan(strpayPlan);
					beanObj.setServicingPhoneNumber(servicingPhoneNumber);
					// Start - CS1175332-Farmers Direct Services (FDS/Personal Lines) - Billing &
					// Payments - CA Payroll Deduct
					data.setSessionData("S_FDS_GPC", gpcCode);
					data.setSessionData("S_FDS_PAYPLAN", strpayPlan);
					data.setSessionData("S_FDS_SERVICING_PHONE_NUMBER", servicingPhoneNumber);
					data.addToLog(data.getCurrentElement(),
							"GPC code for FDS PayrollDeduct ::" + data.getSessionData("S_FDS_GPC"));
					data.addToLog(data.getCurrentElement(),
							"Pay Plan for FDS PayrollDeduct ::" + data.getSessionData("S_FDS_PAYPLAN"));
					data.addToLog(data.getCurrentElement(), "Servicing Phone Number for FDS PayrollDeduct ::"
							+ data.getSessionData("S_FDS_SERVICING_PHONE_NUMBER"));
					// End CS1175332-Farmers Direct Services (FDS/Personal Lines) - Billing &
					// Payments - CA Payroll Deduct
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
						String strPolicyStateName = setStateName(data, caa, currElementName, strPolicyState);
						data.addToLog(currElementName, "Policy State Name:" + strPolicyStateName);

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

					// CS1348016 - All BU's - Onboarding Line Routing
					if (policyData.containsKey("inceptionDate")) {
						String strInceptionDate = (String) policyData.get("inceptionDate");
						beanObj.setInceptionDate(strInceptionDate);
						data.addToLog(currElementName, "Setting Inception Date  into Bean :: " + strInceptionDate);
					}

					// START : CS1265959 : Farmers Insurance | US | FDS - Payroll Deduct indicator &
					// routing
					if (policyData.containsKey("payPlan") && policyData.get("payPlan") != null) {
						strpayPlan = (String) policyData.get("payPlan");
						if ("PB".equalsIgnoreCase(strpayPlan) || "PA".equalsIgnoreCase(strpayPlan)) {
							data.addToLog(currElementName, "S_API_PAYPLAN : " + (String) policyData.get("payPlan"));
							data.setSessionData(Constants.S_POLICY_ATTRIBUTES, "Payroll Deduct");
							// Start CS1175332-Farmers Direct Services (FDS/Personal Lines) - Billing &
							// Payments - CA Payroll Deduct
							data.setSessionData("S_FDS_PAYROLL_DEDUCT", "Y");
							// End CS1175332-Farmers Direct Services (FDS/Personal Lines) - Billing &
							// Payments - CA Payroll Deduct
							data.addToLog(currElementName,
									"POLICY ATTRIBUTES : " + data.getSessionData(Constants.S_POLICY_ATTRIBUTES));
						}
					}

					// END : CS1265959 : Farmers Insurance | US | FDS - Payroll Deduct indicator &
					// routing
					// START : CS1200021 : Farmers Insurance | US | Farmers Service (FDS) - Add
					// Routing for 'True Direct' HouseAccountSubCategor
					if (policyData.containsKey("subCategory") && policyData.get("subCategory") != null
							&& !(("PB".equalsIgnoreCase((String) policyData.get("payPlan")))
									|| ("PA".equalsIgnoreCase((String) policyData.get("payPlan"))))) {
						apiSubcategory = (String) policyData.get("subCategory");
						beanObj.setStrsubCategory(apiSubcategory);
						data.addToLog(currElementName, "apiSubcategory :: " + apiSubcategory);
						data.setSessionData("S_API_SUBCATEGORY", apiSubcategory);
						strsubcategory = setSubcategory(data, caa, currElementName, apiSubcategory);
						data.addToLog(currElementName, "Subcategory : " + strsubcategory);
						if (strsubcategory.equalsIgnoreCase(apiSubcategory)) {
							data.setSessionData(Constants.S_POLICY_ATTRIBUTES, apiSubcategory);
							data.addToLog(currElementName, "S_API_SUBCATEGORY : " + apiSubcategory);
						}
					}
					if (policyData.containsKey("agentReference") && !strsubcategory.equalsIgnoreCase(apiSubcategory)
							&& !(("PB".equalsIgnoreCase((String) policyData.get("payPlan")))
									|| ("PA".equalsIgnoreCase((String) policyData.get("payPlan"))))) {
						agentAORid = (String) policyData.get("agentReference");
						beanObj.setStrAgentAORID(agentAORid);
						data.addToLog(currElementName, "AOR ID :: " + agentAORid);
						data.setSessionData("S_AGENT_ID", agentAORid);
						String policyAttributeCode = agentAORid.substring(2, 4);
						String policyAtttribute = "";
						if ("99".equalsIgnoreCase(policyAttributeCode)) {
							policyAtttribute = "AOR99";
						}
						data.addToLog(data.getCurrentElement(), "Set Policy Attribute into session  "
								+ Constants.S_POLICY_ATTRIBUTES + " : " + policyAtttribute);
						data.setSessionData(Constants.S_POLICY_ATTRIBUTES, policyAtttribute);
					}
					// END : CS1200021 : Farmers Insurance | US | Farmers Service (FDS) - Add
					// Routing for 'True Direct' HouseAccountSubCategor

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
					if (policyData.containsKey("GPC")) {
						strPolicyGPC = (String) policyData.get("GPC");
						String fasPolicySeg = (String) data.getSessionData("S_FDS_GPC_CA_HOME");
						data.addToLog(data.getCurrentElement(), "GPC : " + strPolicyGPC);
						data.addToLog(data.getCurrentElement(), "POLICY SEGMENTATION : " + fasPolicySeg);
						if (strPolicyGPC != null && !strPolicyGPC.equals("") && fasPolicySeg != null
								&& !fasPolicySeg.equals("") && fasPolicySeg.contains(strPolicyGPC)) {
							data.addToLog(data.getCurrentElement(), "Set policy segmena into session  "
									+ Constants.S_POLICY_SEGMENTATION + " : FDS_GPC_CA_HOME");
							data.setSessionData(Constants.S_POLICY_SEGMENTATION, "FDS_GPC_CA_HOME");
						}
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
							data.setSessionData("ID_AUTH_SELECTED_PRODUCTTYPE", lob);
							data.setSessionData("SIDA_RETENTION_PRODUCTYPE", lob);
							data.setSessionData("S_EPC_PAYMENT_POLICYNUM", obj.getStrBillingAccountNumber());
//							// CS1336023 - Cancel policy - Arshath - start
							if (null == data.getSessionData(Constants.S_MDM_POLICY_STATUS)) {
								data.setSessionData(Constants.S_MDM_POLICY_STATUS, obj.getPolicyStatus());
								data.addToLog("Policy Status", obj.getPolicyStatus());
							} else {
								data.addToLog("Policy status is not empty", "");
							}
							// CS1336023 - Cancel policy - Arshath - start

							// onboarding check code
							// CS1348016 - All BU's - Onboarding Line Routing
							String apponBoardingFlag = (String) data
									.getSessionData(Constants.S_APP_ONBOARDING_ELIGIBLE_FLAG);
							String onBoardingFlag = (String) data.getSessionData(Constants.S_ONBOARDING_ELIGIBLE);
							String policyInceptionDate = obj.getInceptionDate();
							if (!onBoardingFlag.equalsIgnoreCase(Constants.Y_FLAG) && !"".equalsIgnoreCase(policyInceptionDate))  {
								// if( apponBoardingFlag.equalsIgnoreCase(Constants.Y_FLAG)) {
							
								data.addToLog("Inception date is: ", policyInceptionDate);
								LocalDate currentDate = LocalDate.now();
								DateTimeFormatter formatter = new DateTimeFormatterBuilder().parseCaseInsensitive()
										.appendPattern("M/d/yyyy").toFormatter();
								String formattedDate = currentDate.format(formatter);
								data.addToLog("The Policy Inception Date is " + policyInceptionDate,
										"The Current date is " + formattedDate);
								LocalDate date1 = LocalDate.parse(policyInceptionDate, formatter);
								LocalDate date2 = LocalDate.parse(formattedDate, formatter);
								long daysBetween = ChronoUnit.DAYS.between(date1, date2);
								String daysBetweenString = String.valueOf(daysBetween);
								data.addToLog("The Days Between from Policy started and the current date::  ",
										daysBetweenString);
								String stronBoardingEligibleDays = (String) data
										.getSessionData(Constants.S_ONBOARDING_ELIGIBLE_DAYS);
								data.addToLog("The  days in Config file ::", stronBoardingEligibleDays);
								Long onBoardingEligibleDays = Long.valueOf(stronBoardingEligibleDays);

								// <=60 days check for onboarding->NLU
								appTag = (String) data.getSessionData(Constants.APPTAG);

								if ((appTag != null && !Constants.EmptyString.equalsIgnoreCase(appTag))
										&& apponBoardingFlag.equalsIgnoreCase(Constants.Y_FLAG)) {
									data.addToLog(currElementName, "It's from NLU" + appTag);
									if (daysBetween <= onBoardingEligibleDays) {
										data.setSessionData(Constants.S_ONBOARDING_ELIGIBLE, Constants.Y_FLAG);
										data.addToLog("The Policy Inception date is lesser than 60 Days::", "Y");
									} else {
										data.setSessionData(Constants.S_ONBOARDING_ELIGIBLE, Constants.N_FLAG);
										data.addToLog("The Policy Inception date is Lesser than 60 Days::", "N");
									}
								}
								// <=60 days check for onbaording->Main menus
								else {
									data.addToLog(currElementName, "It's from Main Menu");
									if (daysBetween <= onBoardingEligibleDays) {
										data.setSessionData(Constants.S_ONBOARDING_ELIGIBLE, Constants.Y_FLAG);
										data.addToLog("The Policy Inception date is lesser than 60 Days::", "Y");
									} else {
										data.setSessionData(Constants.S_ONBOARDING_ELIGIBLE, Constants.N_FLAG);
										data.addToLog("The Policy Inception date is Lesser than 60 Days::", "N");
									}
								}

//							}
//						    else {
//						    	data.setSessionData(Constants.S_ONBOARDING_ELIGIBLE, Constants.N_FLAG);
//						    	data.addToLog(currElementName, "In AppTag File the OnBoarding Eligible Flag as N");
//						    }
							} else {
								data.addToLog(currElementName,
										"The OnBoarding Is Eligible and it is setted in MDM Lookup::" + onBoardingFlag);
							}

						}
					}
					data.addToLog(currElementName,
							"Value of S_POLICY_STATE_CODE : " + data.getSessionData(Constants.S_POLICY_STATE_CODE));
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

					if (policiesArr.size() == 1) {

						if (lob.equalsIgnoreCase("A")) {
							prompt = "Auto policy";
							data.setSessionData(Constants.FINAL_PRODUCTTYPE, lob);
						} else if (lob.equalsIgnoreCase("H") || lob.equalsIgnoreCase("F")) {
							prompt = "Home policy";
							data.setSessionData(Constants.FINAL_PRODUCTTYPE, lob);
						} else if (lob.equalsIgnoreCase("U")) {
							prompt = "Umbrella Policy";
							data.setSessionData(Constants.FINAL_PRODUCTTYPE, lob);
						}

						String lang = (String) data.getSessionData("S_PREF_LANG");

						if (lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("auto policy")) {
							prompt = "póliza de auto";
						}
						if (lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("home policy")) {
							prompt = "póliza de casa";
						}
						if (lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("boat policy")) {
							prompt = "póliza de barco";
						}
						if (lang.equalsIgnoreCase("SP")
								&& prompt.toLowerCase().contains("motor home or motorcycle policy")) {
							prompt = "casa rodante o póliza de motocicleta";
						}
						if (lang.equalsIgnoreCase("SP")
								&& prompt.toLowerCase().contains("specialty dwelling or mobile home policy")) {
							prompt = "vivienda especial o póliza de casa móvil";
						}
						if (lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("umbrella policy")) {
							prompt = "póliza de paraguas";
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
					if (autoprodtypecount >= 1)
						prompt = prompt + " an Auto policy";
					if (homeprodtypecount >= 1)
						prompt = prompt + " a Home Policy";
					if (umbrellaprodtypecount >= 1)
						prompt = prompt + " or an Umbrella Policy";

					String lang = (String) data.getSessionData("S_PREF_LANG");

					if (lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("an auto policy")) {
						data.addToLog(currElementName, "Enterd auto policy replace condition");
						prompt = prompt.replace(" an Auto policy", " una póliza de auto");
					}
					if (lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("a home policy")) {
						prompt = prompt.replace(" a Home Policy", " una póliza de casa");
					}
					if (lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("or an umbrella policy")) {
						prompt = prompt.replace(" or an Umbrella Policy", " o una póliza de paraguas");
					}

					if (prompt.contains(" ")) {
						prompt = prompt.replaceAll(" ", ".");
					}

					data.setSessionData(Constants.VXMLParam1, prompt);
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

					if (autoprodtypecount >= 1) {
						prompt = " Auto ";
						data.setSessionData("SIDA_RETENTION_PRODUCTYPE", "A");
					} else if (homeprodtypecount >= 1) {
						prompt = " Home ";
						data.setSessionData("SIDA_RETENTION_PRODUCTYPE", "H");
					} else if (umbrellaprodtypecount >= 1) {
						prompt = " Umbrella ";
						data.setSessionData("SIDA_RETENTION_PRODUCTYPE", "U");
					}

					String lang = (String) data.getSessionData("S_PREF_LANG");

					if (lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("auto")) {
						prompt = prompt.replace(" Auto ", " auto ");
						data.setSessionData("SIDA_RETENTION_PRODUCTYPE", "A");
					}
					if (lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("home")) {
						prompt = prompt.replace(" Home ", " casa ");
						data.setSessionData("SIDA_RETENTION_PRODUCTYPE", "H");
					}
					if (lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("an umbrella")) {
						prompt = prompt.replace(" an Umbrella ", " una paraguas ");
						data.setSessionData("SIDA_RETENTION_PRODUCTYPE", "U");
					}

					if (prompt.contains(" ")) {
						prompt = prompt.replaceAll(" ", ".");
					}

					HashMap<String, PolicyBean> policyDetailsMap = new HashMap<String, PolicyBean>();
					for (String key : productPolicyMap.keySet()) {
						policyDetailsMap = productPolicyMap.get(key);
					}
					data.setSessionData(Constants.POLICY_DETAILS_FOR_MN3, policyDetailsMap);
					data.setSessionData(Constants.VXMLParam1, prompt);
					data.setSessionData(Constants.VXMLParam2, "NA");
					data.setSessionData(Constants.VXMLParam3, "NA");
					data.setSessionData(Constants.VXMLParam4, "NA");
					data.addToLog(currElementName, "Policies Dynamic Product Type Prompts = " + prompt.toString());
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
			data.addToLog(currElementName, "Exception in AccLinkAniLookup method  :: " + e);
			caa.printStackTrace(e);
		}
		return strExitState;

	}

	private String apiResponseManupulation_ForemostPolicyInquiry(DecisionElementData data, CommonAPIAccess caa,
			String currElementName, String strRespBody) {
		String strExitState = Constants.ER, appTag = Constants.EmptyString;
		try {
			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			JSONArray policiesArr = (JSONArray) resp.get("policies");

			String policyProductCode = null;
			String prompt = Constants.EmptyString;
			int autoprodtypecount = 0;
			int homeprodtypecount = 0;
			int umbrellaprodtypecount = 0;
			int mrprodtypecount = 0;
			int rvprodtypecount = 0;
			int spprodtypecount = 0;
			boolean singleproducttype = false;
			String agentAORid = Constants.EmptyString;

			if (policiesArr != null && policiesArr.size() > 0) {
				data.setSessionData(Constants.S_NOOF_POLICIES_LOOKUP, policiesArr.size());
				data.setSessionData(Constants.IS_MORETHAN_ZERO_POLICIES, Constants.STRING_YES);
				Map<String, Integer> productTypeCounts = new HashMap<>();
				HashMap<String, HashMap<String, PolicyBean>> productPolicyMap = new HashMap<String, HashMap<String, PolicyBean>>();

				for (int i = 0; i < policiesArr.size(); i++) {
					JSONObject policyData = (JSONObject) policiesArr.get(i);
					PolicyBean beanObj = new PolicyBean();
					String strPolicyNum = Constants.EmptyString;

					if (policyData.containsKey("policyProductCode")) {
						policyProductCode = (String) policyData.get("policyProductCode");
						if (productTypeCounts.containsKey(policyProductCode))
							productTypeCounts.put(policyProductCode, productTypeCounts.get(policyProductCode) + 1);
						else
							productTypeCounts.put(policyProductCode, 1);
						beanObj.setStrPolicyProductCode(policyProductCode);
					}

					if (policyData.containsKey("addresses")) {
						JSONArray addressesArr = (JSONArray) policyData.get("addresses");
						JSONObject addressesObj = (JSONObject) addressesArr.get(0);
						if (addressesObj.containsKey("zip")) {
							String zip = (String) addressesObj.get("zip");
							if (zip.length() > 5) {
								zip = zip.substring(0, 5);
							}
							beanObj.setStrZipCode(zip);
						}
						if (addressesObj.containsKey("state"))
							beanObj.setStrPolicyState((String) addressesObj.get("state"));

						// CS1151307 : Update Policy State Name(Policy State : WY, PolicyStateName :
						// Wyoming)
						String strPolicyState = (String) beanObj.getStrPolicyState();
						data.setSessionData(Constants.S_POLICY_STATE_CODE, strPolicyState);
						data.addToLog(currElementName, "Policy State Code :" + strPolicyState);
						String strPolicyStateName = setStateName(data, caa, currElementName, strPolicyState);
						data.addToLog(currElementName, "Policy State Name:" + strPolicyStateName);
					}

					if (policyData.containsKey("insureds")) {
						String strDOB = "";
						JSONArray insuredDetailsArr = (JSONArray) policyData.get("insureds");
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

					// CS1348016 - All BU's - Onboarding Line Routing
					if (policyData.containsKey("inceptionDate")) {
						String strInceptionDate = (String) policyData.get("inceptionDate");
						beanObj.setInceptionDate(strInceptionDate);
						data.addToLog(currElementName, "Setting Inception Date  into Bean :: " + strInceptionDate);
					}

					// START : CS1200021 : Farmers Insurance | US | Farmers Service (FDS) - Add
					// Routing for 'True Direct' HouseAccountSubCategor
					if (policyData.containsKey("subCategory")) {

						String apiSubcategory = (String) policyData.get("subCategory");
						beanObj.setStrsubCategory(apiSubcategory);
						data.addToLog(currElementName, "apiSubcategory :: " + apiSubcategory);
						data.setSessionData("S_API_SUBCATEGORY", apiSubcategory);

						String strsubcategory = setSubcategory(data, caa, currElementName, apiSubcategory);
						data.addToLog(currElementName, "Subcategory : " + strsubcategory);

						if (strsubcategory.equalsIgnoreCase(apiSubcategory)) {
							data.setSessionData(Constants.S_POLICY_ATTRIBUTES, apiSubcategory);
							data.addToLog(currElementName, "S_API_SUBCATEGORY : " + apiSubcategory);

						} else {
							if (policyData.containsKey("agentOfRecordId")) {
								agentAORid = (String) policyData.get("agentOfRecordId");
								beanObj.setStrAgentAORID(agentAORid);
								data.addToLog(currElementName, "AOR ID :: " + agentAORid);
								data.setSessionData("S_AGENT_ID", agentAORid);
							}
						}
					}
					// END : CS1200021 : Farmers Insurance | US | Farmers Service (FDS) - Add
					// Routing for 'True Direct' HouseAccountSubCategor

					String tmpKey = Constants.EmptyString;
					switch (policyProductCode) {
					case Constants.PRODUCTTYPE_601:
						tmpKey = Constants.MR_PRODUCTTYPECOUNT_KYC;
						break;
					case Constants.PRODUCTTYPE_602:
						tmpKey = Constants.MR_PRODUCTTYPECOUNT_KYC;
						break;
					case Constants.PRODUCTTYPE_077:
						tmpKey = Constants.RV_PRODUCTTYPECOUNT_KYC;
						break;
					case Constants.PRODUCTTYPE_255:
						tmpKey = Constants.RV_PRODUCTTYPECOUNT_KYC;
						break;
					case Constants.PRODUCTTYPE_276:
						tmpKey = Constants.RV_PRODUCTTYPECOUNT_KYC;
						break;
					case Constants.PRODUCTTYPE_103:
						tmpKey = Constants.SP_PRODUCTTYPECOUNT_KYC;
						break;
					case Constants.PRODUCTTYPE_105:
						tmpKey = Constants.SP_PRODUCTTYPECOUNT_KYC;
						break;
					case Constants.PRODUCTTYPE_106:
						tmpKey = Constants.SP_PRODUCTTYPECOUNT_KYC;
						break;
					case Constants.PRODUCTTYPE_107:
						tmpKey = Constants.SP_PRODUCTTYPECOUNT_KYC;
						break;
					case Constants.PRODUCTTYPE_381:
						tmpKey = Constants.SP_PRODUCTTYPECOUNT_KYC;
						break;
					case Constants.PRODUCTTYPE_444:
						tmpKey = Constants.SP_PRODUCTTYPECOUNT_KYC;
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

				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_601)) {
					mrprodtypecount = mrprodtypecount + productTypeCounts.get(Constants.PRODUCTTYPE_601);
				}
				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_602)) {
					mrprodtypecount = mrprodtypecount + productTypeCounts.get(Constants.PRODUCTTYPE_602);
				}
				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_077)) {
					rvprodtypecount = rvprodtypecount + productTypeCounts.get(Constants.PRODUCTTYPE_077);
				}
				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_255)) {
					rvprodtypecount = rvprodtypecount + productTypeCounts.get(Constants.PRODUCTTYPE_255);
				}
				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_276)) {
					rvprodtypecount = rvprodtypecount + productTypeCounts.get(Constants.PRODUCTTYPE_276);
				}
				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_103)) {
					spprodtypecount = spprodtypecount + productTypeCounts.get(Constants.PRODUCTTYPE_103);
				}
				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_105)) {
					spprodtypecount = spprodtypecount + productTypeCounts.get(Constants.PRODUCTTYPE_105);
				}
				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_106)) {
					spprodtypecount = spprodtypecount + productTypeCounts.get(Constants.PRODUCTTYPE_106);
				}
				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_107)) {
					spprodtypecount = spprodtypecount + productTypeCounts.get(Constants.PRODUCTTYPE_107);
				}
				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_381)) {
					spprodtypecount = spprodtypecount + productTypeCounts.get(Constants.PRODUCTTYPE_381);
				}
				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_444)) {
					spprodtypecount = spprodtypecount + productTypeCounts.get(Constants.PRODUCTTYPE_444);
				}

				data.setSessionData(Constants.SP_PRODUCTTYPECOUNT_KYC, spprodtypecount);
				data.setSessionData(Constants.MR_PRODUCTTYPECOUNT_KYC, mrprodtypecount);
				data.setSessionData(Constants.RV_PRODUCTTYPECOUNT_KYC, rvprodtypecount);

				data.addToLog(currElementName, "specialty dwelling or mobile home = "
						+ data.getSessionData(Constants.SP_PRODUCTTYPECOUNT_KYC));
				data.addToLog(currElementName,
						"Boat Product Type Count  = " + data.getSessionData(Constants.MR_PRODUCTTYPECOUNT_KYC));
				data.addToLog(currElementName,
						"motor home or motorcycle  = " + data.getSessionData(Constants.RV_PRODUCTTYPECOUNT_KYC));

				singleproducttype = checkifsingleproducttype(rvprodtypecount, 0, 0, 0, mrprodtypecount,
						spprodtypecount);
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
							data.setSessionData("POLICY_PRODUCT_CODE", obj.getStrPolicyProductCode());
							data.setSessionData("S_EPC_PAYMENT_POLICYNUM", obj.getStrPolicyProductCode() + key1);
							data.setSessionData(Constants.S_POLICY_NUM, key1);
							data.setSessionData(Constants.S_POLICY_STATE_CODE, obj.getStrPolicyState());
							data.setSessionData(Constants.S_FINAL_POLICY_OBJ, obj);

							// onboarding check code
							// CS1348016 - All BU's - Onboarding Line Routing
							String apponBoardingFlag = (String) data
									.getSessionData(Constants.S_APP_ONBOARDING_ELIGIBLE_FLAG);
							String onBoardingFlag = (String) data.getSessionData(Constants.S_ONBOARDING_ELIGIBLE);
							String policyInceptionDate = obj.getInceptionDate();
							if (!onBoardingFlag.equalsIgnoreCase(Constants.Y_FLAG) && !"".equalsIgnoreCase(policyInceptionDate)) {
								// if( apponBoardingFlag.equalsIgnoreCase(Constants.Y_FLAG)) {
								
								data.addToLog("Inception date is: ", policyInceptionDate);
								LocalDate currentDate = LocalDate.now();
								DateTimeFormatter formatter = new DateTimeFormatterBuilder().parseCaseInsensitive()
										.appendPattern("M/d/yyyy").toFormatter();
								String formattedDate = currentDate.format(formatter);
								data.addToLog("The Policy Inception Date is " + policyInceptionDate,
										"The Current date is " + formattedDate);

								LocalDate date1 = LocalDate.parse(policyInceptionDate, formatter);
								LocalDate date2 = LocalDate.parse(formattedDate, formatter);
								long daysBetween = ChronoUnit.DAYS.between(date1, date2);
								String daysBetweenString = String.valueOf(daysBetween);
								data.addToLog("The Days Between from Policy started and the current date::  ",
										daysBetweenString);
								String stronBoardingEligibleDays = (String) data
										.getSessionData(Constants.S_ONBOARDING_ELIGIBLE_DAYS);
								data.addToLog("The  days in Config file ::", stronBoardingEligibleDays);
								Long onBoardingEligibleDays = Long.valueOf(stronBoardingEligibleDays);

								// <=60 days check for onboarding->NLU
								appTag = (String) data.getSessionData(Constants.APPTAG);

								if ((appTag != null && !Constants.EmptyString.equalsIgnoreCase(appTag))
										&& apponBoardingFlag.equalsIgnoreCase(Constants.Y_FLAG)) {
									data.addToLog(currElementName, "It's from NLU" + appTag);
									if (daysBetween <= onBoardingEligibleDays) {
										data.setSessionData(Constants.S_ONBOARDING_ELIGIBLE, Constants.Y_FLAG);
										data.addToLog("The Policy Inception date is lesser than 60 Days::", "Y");
									} else {
										data.setSessionData(Constants.S_ONBOARDING_ELIGIBLE, Constants.N_FLAG);
										data.addToLog("The Policy Inception date is Lesser than 60 Days::", "N");
									}
								}
								// <=60 days check for onbaording->Main menus
								else {
									data.addToLog(currElementName, "It's from Main Menu");
									if (daysBetween <= onBoardingEligibleDays) {
										data.setSessionData(Constants.S_ONBOARDING_ELIGIBLE, Constants.Y_FLAG);
										data.addToLog("The Policy Inception date is lesser than 60 Days::", "Y");
									} else {
										data.setSessionData(Constants.S_ONBOARDING_ELIGIBLE, Constants.N_FLAG);
										data.addToLog("The Policy Inception date is Lesser than 60 Days::", "N");
									}
								}

							} else {
								data.addToLog(currElementName,
										"The OnBoarding Is Eligible and it is setted in MDM Lookup::" + onBoardingFlag);
							}

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

						if (policyProductCode.equalsIgnoreCase("601") || policyProductCode.equalsIgnoreCase("602")) {
							prompt = "Boat policy";
							data.setSessionData(Constants.FINAL_PRODUCTTYPE, "Boat");
						} else if (policyProductCode.equalsIgnoreCase("077")
								|| policyProductCode.equalsIgnoreCase("255")
								|| policyProductCode.equalsIgnoreCase("276")) {
							prompt = "motor home or motorcycle policy";
							data.setSessionData(Constants.FINAL_PRODUCTTYPE, "motor home or motorcycle");
						} else if (policyProductCode.equalsIgnoreCase("103")
								|| policyProductCode.equalsIgnoreCase("105")
								|| policyProductCode.equalsIgnoreCase("106")
								|| policyProductCode.equalsIgnoreCase("107")
								|| policyProductCode.equalsIgnoreCase("381")
								|| policyProductCode.equalsIgnoreCase("444")) {
							prompt = "specialty dwelling or mobile home policy";
							data.setSessionData(Constants.FINAL_PRODUCTTYPE, "specialty dwelling or mobile home");
						}

						String lang = (String) data.getSessionData("S_PREF_LANG");

						if (lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("auto policy")) {
							prompt = "póliza de auto";
						}
						if (lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("home policy")) {
							prompt = "póliza de casa";
						}
						if (lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("boat policy")) {
							prompt = "póliza de barco";
						}
						if (lang.equalsIgnoreCase("SP")
								&& prompt.toLowerCase().contains("motor home or motorcycle policy")) {
							prompt = "casa rodante o póliza de motocicleta";
						}
						if (lang.equalsIgnoreCase("SP")
								&& prompt.toLowerCase().contains("specialty dwelling or mobile home policy")) {
							prompt = "vivienda especial o póliza de casa móvil";
						}
						if (lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("umbrella policy")) {
							prompt = "póliza de paraguas";
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
					if (autoprodtypecount >= 1)
						prompt = prompt + " an Auto policy";
					if (homeprodtypecount >= 1)
						prompt = prompt + " a Home Policy";
					if (mrprodtypecount >= 1)
						prompt = prompt + " a Boat Policy";
					if (umbrellaprodtypecount >= 1)
						prompt = prompt + " or an Umbrella Policy";

					String lang = (String) data.getSessionData("S_PREF_LANG");

					if (lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("an auto policy")) {
						data.addToLog(currElementName, "Enterd auto policy replace condition");
						prompt = prompt.replace(" an Auto policy", " una póliza de auto");
					}
					if (lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("a home policy")) {
						prompt = prompt.replace(" a Home Policy", " una póliza de casa");
					}
					if (lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("or an umbrella policy")) {
						prompt = prompt.replace(" or an Umbrella Policy", " o una póliza de paraguas");
					}
					if (lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("a boat policy")) {
						prompt = prompt.replace(" a Boat Policy", " una póliza de barco");
					}

					if (prompt.contains(" ")) {
						prompt = prompt.replaceAll(" ", ".");
					}

					data.setSessionData(Constants.VXMLParam1, prompt);
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
					if (autoprodtypecount >= 1)
						prompt = " Auto ";
					else if (homeprodtypecount >= 1)
						prompt = " Home ";
					else if (umbrellaprodtypecount >= 1)
						prompt = " Umbrella ";
					else if (mrprodtypecount >= 1)
						prompt = " Boat ";

					String lang = (String) data.getSessionData("S_PREF_LANG");

					if (lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("auto")) {
						prompt = prompt.replace(" Auto ", " auto ");
					}
					if (lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("home")) {
						prompt = prompt.replace(" Home ", " casa ");
					}
					if (lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("umbrella")) {
						prompt = prompt.replace(" Umbrella ", " paraguas ");
					}
					if (lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("boat")) {
						prompt = prompt.replace(" Boat ", " barco ");
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
					data.setSessionData(Constants.VXMLParam2, "NA");
					data.setSessionData(Constants.VXMLParam3, "NA");
					data.setSessionData(Constants.VXMLParam4, "NA");
					data.addToLog(currElementName, "Policies Dynamic Product Type Prompts = " + prompt.toString());
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
			data.addToLog(currElementName, "Exception in AccLinkAniLookup method  :: " + e);
			caa.printStackTrace(e);
		}
		return strExitState;

	}

	private String apiResponseManupulation_FwsPolicyLookup(DecisionElementData data, CommonAPIAccess caa,
			String currElementName, String strRespBody) {
		String strExitState = Constants.ER, appTag = Constants.EmptyString;
		try {

			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			JSONArray policiesArr = (JSONArray) resp.get("policies");

			String lob = null;
			String prompt = Constants.EmptyString;
			int autoprodtypecount = 0;
			int homeprodtypecount = 0;
			int umbrellaprodtypecount = 0;
			int mrprodtypecount = 0;
			boolean singleproducttype = false;

			if (policiesArr != null && policiesArr.size() == 1) {
				JSONObject policyData = (JSONObject) policiesArr.get(0);
				if (policyData.containsKey("lineOfBusiness") && null != policyData.get("lineOfBusiness")) {
					lob = (String) policyData.get("lineOfBusiness");
					data.setSessionData("S_LOB", lob);
				}
			}

			if (policiesArr != null && policiesArr.size() > 0) {
				data.setSessionData(Constants.S_NOOF_POLICIES_LOOKUP, policiesArr.size());
				data.setSessionData(Constants.IS_MORETHAN_ZERO_POLICIES, Constants.STRING_YES);
				Map<String, Integer> productTypeCounts = new HashMap<>();
				HashMap<String, HashMap<String, PolicyBean>> productPolicyMap = new HashMap<String, HashMap<String, PolicyBean>>();
				for (int i = 0; i < policiesArr.size(); i++) {
					PolicyBean beanObj = new PolicyBean();
					String strPolicyNum = Constants.EmptyString;
					JSONObject policyData = (JSONObject) policiesArr.get(i);

					if (policyData.containsKey("lineOfBusiness") && null != policyData.get("lineOfBusiness")) {
						lob = (String) policyData.get("lineOfBusiness");
						data.setSessionData(Constants.S_FWS_POLICY_LOB, lob);
						beanObj.setStrPolicyLOB(lob);
						if (productTypeCounts.containsKey(lob))
							productTypeCounts.put(lob, productTypeCounts.get(lob) + 1);
						else
							productTypeCounts.put(lob, 1);
					}
					String zip = "";
					if (policyData.containsKey("addresses") && null != policyData.get("addresses")) {
						JSONArray addressesArr = (JSONArray) policyData.get("addresses");
						JSONObject addressesObj = (JSONObject) addressesArr.get(0);
						if (addressesObj.containsKey("zip")) {
							zip = zip.equals("") ? "" + addressesObj.get("zip").toString().substring(0, 5)
									: zip + "," + addressesObj.get("zip").toString().substring(0, 5);
							// zip = (String) addressesObj.get("zip");
						}
						beanObj.setStrZipCode(zip);
						if (addressesObj.containsKey("state"))
							beanObj.setStrPolicyState((String) addressesObj.get("state"));

						// CS1151307 : Update Policy State Name(Policy State : WY, PolicyStateName :
						// Wyoming)
						String strPolicyState = (String) beanObj.getStrPolicyState();
						data.setSessionData(Constants.S_POLICY_STATE_CODE, strPolicyState);
						data.addToLog(currElementName, "Policy State Code :" + strPolicyState);
						String strPolicyStateName = setStateName(data, caa, currElementName, strPolicyState);
						data.addToLog(currElementName, "Policy State Name:" + strPolicyStateName);
					}
					if (policyData.containsKey("insuredDetails")) {

						String strDOB = "";
						JSONArray insuredDetailsArr = (JSONArray) policyData.get("insuredDetails");
						for (int k = 0; k < insuredDetailsArr.size(); k++) {
							JSONObject obj = (JSONObject) insuredDetailsArr.get(k);
							strDOB = (strDOB.equals("")) ? "" + obj.get("birthDate")
									: strDOB + "," + obj.get("birthDate");
						}
						beanObj.setStrDOB(strDOB);

						JSONObject insuredDetailsObj = (JSONObject) insuredDetailsArr.get(0);
						if (insuredDetailsObj.containsKey("firstName") && null != insuredDetailsObj.get("firstName"))
							beanObj.setStrFirstName((String) insuredDetailsObj.get("firstName"));
						if (insuredDetailsObj.containsKey("lastName") && null != insuredDetailsObj.get("lastName"))
							beanObj.setStrLastName((String) insuredDetailsObj.get("lastName"));
						
						//START : CS1175316 Farmers Insurance | US | Add Emailed ID Card Self-Service
						if(insuredDetailsObj.containsKey("email") && null != insuredDetailsObj.get("email"));
						String email=(String) insuredDetailsObj.get("email");
						
						if (null != email && !email.isEmpty()) {
							beanObj.setStrEmail(email);							
							data.setSessionData(Constants.S_EMAIL, email);
							data.setSessionData(Constants.S_IS_POLICY_EMAIL_AVIALABLE, Constants.STRING_YES);
							data.addToLog(currElementName, "Is Email available"+beanObj.getStrEmail());
						}
						else {
							data.setSessionData(Constants.S_IS_POLICY_EMAIL_AVIALABLE, Constants.STRING_NO);
							data.addToLog(currElementName, "Is Email not available");
						}
						//END : CS1175316 Farmers Insurance | US | Add Emailed ID Card Self-Service
					}
					if (policyData.containsKey("policyNumber") && null != policyData.get("policyNumber")) {
						strPolicyNum = (String) policyData.get("policyNumber");
						beanObj.setStrPolicyNum(strPolicyNum);
					}

					// CS1348016 - All BU's - Onboarding Line Routing
					if (policyData.containsKey("inceptionDate")) {
						String strInceptionDate = (String) policyData.get("inceptionDate");
						beanObj.setInceptionDate(strInceptionDate);
						data.addToLog(currElementName, "Setting Inception Date  into Bean :: " + strInceptionDate);
					}

					// CS1151307 : Update Policy State Name(Policy State : HI, PolicyStateName :
					// Hawaii)
					if (policyData.containsKey("policyState") && policyData.get("policyState") != null) {
						beanObj.setStrPolicyState((String) policyData.get("policyState"));
						String strPolicyState = beanObj.getStrPolicyState();
						data.setSessionData(Constants.S_POLICY_STATE_CODE, strPolicyState);
						data.addToLog(currElementName, "Policy State Code: " + strPolicyState);
						String strPolicyStateName = setStateName(data, caa, currElementName, strPolicyState);
						data.addToLog(currElementName, "Policy State Name: " + strPolicyStateName);
					}

					if (policyData.containsKey("policySource") && null != policyData.get("policySource"))
						beanObj.setStrPolicySource((String) policyData.get("policySource"));
					if (policyData.containsKey("policyState") && null != policyData.get("policyState"))
						beanObj.setStrPolicyState((String) policyData.get("policyState"));
					if (policyData.containsKey("suffix") && null != policyData.get("suffix"))
						beanObj.setStrPolicySuffix((String) policyData.get("suffix"));
					if (policyData.containsKey("effectiveDate") && null != policyData.get("effectiveDate"))
						beanObj.setStrEffectiveDate((String) policyData.get("effectiveDate"));
					if (policyData.containsKey("InternalPolicyVersion")
							&& null != policyData.get("InternalPolicyVersion"))
						beanObj.setStrInternalPolicyNumber((String) policyData.get("InternalPolicyVersion"));
					if (policyData.containsKey("InternalPolicyNumber")
							&& null != policyData.get("InternalPolicyNumber"))
						beanObj.setStrInternalPolicyVersion((String) policyData.get("InternalPolicyNumber"));
					if (policyData.containsKey("billingAccountNumber")
							&& null != policyData.get("billingAccountNumber")) {
						String tmpBillingAccNum = (String) policyData.get("billingAccountNumber");
						if ("ARS".equalsIgnoreCase(beanObj.getStrPolicySource()) && tmpBillingAccNum.length() > 9) {
							beanObj.setStrCompanyCode(tmpBillingAccNum.substring(0, 2));
							beanObj.setStrBillingAccountNumber(
									tmpBillingAccNum.substring(2, tmpBillingAccNum.length()));
						} else {
							beanObj.setStrCompanyCode(tmpBillingAccNum.substring(0, 2));
							beanObj.setStrBillingAccountNumber(
									tmpBillingAccNum.substring(0, tmpBillingAccNum.length()));
						}
					}

					String tmpKey = Constants.EmptyString;
					switch (lob) {
					case Constants.PRODUCTTYPE_Y:
						tmpKey = Constants.MR_PRODUCTTYPECOUNT_KYC;
						break;
					case Constants.PRODUCTTYPE_H:
						tmpKey = Constants.HOME_PRODUCTTYPECOUNT_KYC;
						break;
					case Constants.PRODUCTTYPE_F:
						tmpKey = Constants.HOME_PRODUCTTYPECOUNT_KYC;
						break;
					case Constants.PRODUCTTYPE_A:
						tmpKey = Constants.AUTO_PRODUCTTYPECOUNT_KYC;
						break;
					case Constants.PRODUCTTYPE_U:
						tmpKey = Constants.UMBRELLA_PRODUCTTYPECOUNT_KYC;
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

				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_Y)) {
					mrprodtypecount = productTypeCounts.get(Constants.PRODUCTTYPE_Y);
				}
				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_H)) {
					homeprodtypecount = homeprodtypecount + productTypeCounts.get(Constants.PRODUCTTYPE_H);
				}
				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_F)) {
					homeprodtypecount = homeprodtypecount + productTypeCounts.get(Constants.PRODUCTTYPE_F);
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
				data.setSessionData(Constants.MR_PRODUCTTYPECOUNT_KYC, mrprodtypecount);

				data.addToLog(currElementName,
						"Auto Product Type Count  = " + data.getSessionData(Constants.AUTO_PRODUCTTYPECOUNT_KYC));
				data.addToLog(currElementName,
						"Home Product Type Count  = " + data.getSessionData(Constants.HOME_PRODUCTTYPECOUNT_KYC));
				data.addToLog(currElementName, "Umbrella Product Type Count = "
						+ data.getSessionData(Constants.UMBRELLA_PRODUCTTYPECOUNT_KYC));
				data.addToLog(currElementName,
						"Boat Product Type Count  = " + data.getSessionData(Constants.MR_PRODUCTTYPECOUNT_KYC));

				singleproducttype = checkifsingleproducttype(0, autoprodtypecount, homeprodtypecount,
						umbrellaprodtypecount, mrprodtypecount, 0);
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
							data.setSessionData(Constants.S_POLICY_SOURCE, obj.getStrPolicySource());
							data.setSessionData(Constants.S_FINAL_POLICY_OBJ, obj);

							data.setSessionData("ID_AUTH_SELECTED_PRODUCTTYPE", lob);
							data.setSessionData("S_EPC_PAYMENT_POLICYNUM", obj.getStrBillingAccountNumber());

							// onboarding check code
							// CS1348016 - All BU's - Onboarding Line Routing
							String apponBoardingFlag = (String) data
									.getSessionData(Constants.S_APP_ONBOARDING_ELIGIBLE_FLAG);
							String onBoardingFlag = (String) data.getSessionData(Constants.S_ONBOARDING_ELIGIBLE);
							String policyInceptionDate = obj.getInceptionDate();
							if (!onBoardingFlag.equalsIgnoreCase(Constants.Y_FLAG) && !"".equalsIgnoreCase(policyInceptionDate)) {
								// if( apponBoardingFlag.equalsIgnoreCase(Constants.Y_FLAG)) {
								
								data.addToLog("Inception date is: ", policyInceptionDate);
								LocalDate currentDate = LocalDate.now();
								DateTimeFormatter formatter = new DateTimeFormatterBuilder().parseCaseInsensitive()
										.appendPattern("M/d/yyyy").toFormatter();
								String formattedDate = currentDate.format(formatter);
								data.addToLog("The Policy Inception Date is " + policyInceptionDate,
										"The Current date is " + formattedDate);
								LocalDate date1 = LocalDate.parse(policyInceptionDate, formatter);
								LocalDate date2 = LocalDate.parse(formattedDate, formatter);
								long daysBetween = ChronoUnit.DAYS.between(date1, date2);
								String daysBetweenString = String.valueOf(daysBetween);
								data.addToLog("The Days Between from Policy started and the current date::  ",
										daysBetweenString);
								String stronBoardingEligibleDays = (String) data
										.getSessionData(Constants.S_ONBOARDING_ELIGIBLE_DAYS);
								data.addToLog("The  days in Config file ::", stronBoardingEligibleDays);
								Long onBoardingEligibleDays = Long.valueOf(stronBoardingEligibleDays);

								// <=60 days check for onboarding->NLU
								appTag = (String) data.getSessionData(Constants.APPTAG);

								if ((appTag != null && !Constants.EmptyString.equalsIgnoreCase(appTag))
										&& apponBoardingFlag.equalsIgnoreCase(Constants.Y_FLAG)) {
									data.addToLog(currElementName, "It's from NLU" + appTag);
									if (daysBetween <= onBoardingEligibleDays) {
										data.setSessionData(Constants.S_ONBOARDING_ELIGIBLE, Constants.Y_FLAG);
										data.addToLog("The Policy Inception date is lesser than 60 Days::", "Y");
									} else {
										data.setSessionData(Constants.S_ONBOARDING_ELIGIBLE, Constants.N_FLAG);
										data.addToLog("The Policy Inception date is Lesser than 60 Days::", "N");
									}
								}
								// <=60 days check for onbaording->Main menus
								else {
									data.addToLog(currElementName, "It's from Main Menu");
									if (daysBetween <= onBoardingEligibleDays) {
										data.setSessionData(Constants.S_ONBOARDING_ELIGIBLE, Constants.Y_FLAG);
										data.addToLog("The Policy Inception date is lesser than 60 Days::", "Y");
									} else {
										data.setSessionData(Constants.S_ONBOARDING_ELIGIBLE, Constants.N_FLAG);
										data.addToLog("The Policy Inception date is Lesser than 60 Days::", "N");
									}
								}

//							}
//						    else {
//						    	data.setSessionData(Constants.S_ONBOARDING_ELIGIBLE, Constants.N_FLAG);
//						    	data.addToLog(currElementName, "In AppTag File the OnBoarding Eligible Flag as N");
//						    }
							} else {
								data.addToLog(currElementName,
										"The OnBoarding Is Eligible and it is setted in MDM Lookup::" + onBoardingFlag);
							}
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

						if (lob.equalsIgnoreCase("A") || lob.equalsIgnoreCase("F")) {
							prompt = "Auto policy";
							data.setSessionData(Constants.FINAL_PRODUCTTYPE, lob);
						} else if (lob.equalsIgnoreCase("H")) {
							prompt = "Home policy";
							data.setSessionData(Constants.FINAL_PRODUCTTYPE, lob);
						} else if (lob.equalsIgnoreCase("Y")) {
							prompt = "Boat policy";
							data.setSessionData(Constants.FINAL_PRODUCTTYPE, lob);
						} else if (lob.equalsIgnoreCase("RV")) {
							prompt = "Motor Home or Motorcycle Policy";
							data.setSessionData(Constants.FINAL_PRODUCTTYPE, lob);
						} else if (lob.equalsIgnoreCase("SP")) {
							prompt = "Specialty Dwelling or Mobile Home Policy";
							data.setSessionData(Constants.FINAL_PRODUCTTYPE, lob);
						} else if (lob.equalsIgnoreCase("U")) {
							prompt = "Umbrella Policy";
							data.setSessionData(Constants.FINAL_PRODUCTTYPE, lob);
						}

						String lang = (String) data.getSessionData("S_PREF_LANG");

						if (lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("auto policy")) {
							prompt = "póliza de auto";
						}
						if (lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("home policy")) {
							prompt = "póliza de casa";
						}
						if (lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("boat policy")) {
							prompt = "póliza de barco";
						}
						if (lang.equalsIgnoreCase("SP")
								&& prompt.toLowerCase().contains("motor home or motorcycle policy")) {
							prompt = "casa rodante o póliza de motocicleta";
						}
						if (lang.equalsIgnoreCase("SP")
								&& prompt.toLowerCase().contains("specialty dwelling or mobile home policy")) {
							prompt = "vivienda especial o póliza de casa móvil";
						}
						if (lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("umbrella policy")) {
							prompt = "póliza de paraguas";
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
					if (autoprodtypecount >= 1)
						prompt = prompt + " an Auto policy";
					if (homeprodtypecount >= 1)
						prompt = prompt + " a Home Policy";
					if (mrprodtypecount >= 1)
						prompt = prompt + " a Boat Policy";
					if (umbrellaprodtypecount >= 1)
						prompt = prompt + " or an Umbrella Policy";

					String lang = (String) data.getSessionData("S_PREF_LANG");

					if (lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("an auto policy")) {
						data.addToLog(currElementName, "Enterd auto policy replace condition");
						prompt = prompt.replace(" an Auto policy", " una póliza de auto");
					}
					if (lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("a home policy")) {
						prompt = prompt.replace(" a Home Policy", " una póliza de casa");
					}
					if (lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("or an umbrella policy")) {
						prompt = prompt.replace(" or an Umbrella Policy", " o una póliza de paraguas");
					}
					if (lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("a boat policy")) {
						prompt = prompt.replace(" a Boat Policy", " una póliza de barco");
					}

					if (prompt.contains(" ")) {
						prompt = prompt.replaceAll(" ", ".");
					}

					data.setSessionData(Constants.VXMLParam1, prompt);
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
					String produtType = "";
					if (autoprodtypecount >= 1) {
						prompt = " Auto ";
						produtType = Constants.AUTO_PRODUCTTYPECOUNT_KYC;
					} else if (homeprodtypecount >= 1) {
						prompt = " Home ";
						produtType = Constants.HOME_PRODUCTTYPECOUNT_KYC;
					} else if (umbrellaprodtypecount >= 1) {
						prompt = " an Umbrella ";
						produtType = Constants.UMBRELLA_PRODUCTTYPECOUNT_KYC;
					} else if (mrprodtypecount >= 1) {
						prompt = " Boat ";
						produtType = Constants.MR_PRODUCTTYPECOUNT_KYC;
					}

					String lang = (String) data.getSessionData("S_PREF_LANG");

					if (lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("auto")) {
						prompt = prompt.replace("  Auto ", " auto ");
					}
					if (lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("home")) {
						prompt = prompt.replace(" Home ", " casa ");
					}
					if (lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("an umbrella")) {
						prompt = prompt.replace(" an Umbrella ", " una paraguas ");
					}
					if (lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("boat")) {
						prompt = prompt.replace(" Boat ", " barco ");
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
					data.setSessionData(Constants.VXMLParam2, "NA");
					data.setSessionData(Constants.VXMLParam3, "NA");
					data.setSessionData(Constants.VXMLParam4, "NA");
					data.addToLog(currElementName, "Policies Dynamic Product Type Prompts = " + prompt.toString());
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
			data.addToLog(currElementName, "Exception in AccLinkAniLookup method  :: " + e);
			caa.printStackTrace(e);
		}
		return strExitState;

	}

	private String apiResponseManupulation_RetriveInsurancePoliciesByParty(DecisionElementData data,
			CommonAPIAccess caa, String currElementName, String strRespBody, String policyNum) {
		String strExitState = Constants.ER, appTag = Constants.EmptyString;
		try {
			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			JSONObject household = null;
			if (resp.containsKey("household")) {
				household = (JSONObject) resp.get("household");
			}

			String agentAORid = Constants.EmptyString;
			if (household != null) {
				JSONArray autoPoliciesarr = (JSONArray) household.get("autoPolicies");
				if (policyNum == null || "".equals(policyNum)) {

					if (autoPoliciesarr != null && autoPoliciesarr.size() > 0) {
						data.setSessionData(Constants.S_NOOF_POLICIES_LOOKUP, autoPoliciesarr.size());
						data.setSessionData(Constants.IS_MORETHAN_ZERO_POLICIES, Constants.STRING_YES);
						Map<String, Integer> productTypeCounts = new HashMap<>();
						HashMap<String, HashMap<String, PolicyBean>> productPolicyMap = new HashMap<String, HashMap<String, PolicyBean>>();

						data.setSessionData(Constants.AUTO_PRODUCTTYPECOUNT_SHAUTH, autoPoliciesarr.size());
						data.addToLog(currElementName, "Auto Product Type Count  = "
								+ data.getSessionData(Constants.AUTO_PRODUCTTYPECOUNT_SHAUTH));

						for (int i = 0; i < autoPoliciesarr.size(); i++) {
							JSONObject policyData = (JSONObject) autoPoliciesarr.get(i);
							JSONObject basicPolicyData = (JSONObject) policyData.get("basicPolicy");
							JSONObject basicPolicyDetail = (JSONObject) policyData.get("basicPolicyDetail");
							JSONObject insuredVehicleData = (JSONObject) policyData.get("insuredVehicle");
							JSONObject namedInsuredData = (JSONObject) policyData.get("namedInsured");
							String strPolicyNum = Constants.EmptyString, strPolicyMod = Constants.EmptyString,
									strPolicySymbol = Constants.EmptyString;

							if (basicPolicyData != null && basicPolicyData.containsKey("policyNumber")) {
								strPolicyNum = (String) basicPolicyData.get("policyNumber");
								data.setSessionData("S_POLICY_NUM", basicPolicyData.get("policyNumber"));
							}
							HashMap<String, PolicyBean> productbeanObj = productPolicyMap
									.get(Constants.PRODUCTTYPE_AUTO);
							PolicyBean beanObj = new PolicyBean();
							if (productbeanObj != null && productbeanObj.containsKey(strPolicyNum)
									&& productbeanObj.get(strPolicyNum) != null) {
								beanObj = (PolicyBean) productbeanObj.get(strPolicyNum);
							} else {
								beanObj = new PolicyBean();
							}

							String zip = beanObj.getStrZipCode();
							if (insuredVehicleData.containsKey("garagingAddress")) {
								JSONObject garagingAddress = (JSONObject) insuredVehicleData.get("garagingAddress");
								if (garagingAddress.containsKey("postalCode")) {
									zip = (zip.equals("")) ? "" + garagingAddress.get("postalCode")
											: zip + "," + garagingAddress.get("postalCode");
									beanObj.setStrZipCode(zip);
								}
							}

							String strDOB = beanObj.getStrDOB();
							if (namedInsuredData.containsKey("birthDate")) {
								strDOB = (strDOB.equals("")) ? "" + namedInsuredData.get("birthDate")
										: strDOB + "," + namedInsuredData.get("birthDate");
								beanObj.setStrDOB(strDOB);
							}

							/**
							 * Policy Mod and Policy Symbol retreived
							 */

							if (basicPolicyData.containsKey("policyModNumber")) {
								strPolicyMod = (String) basicPolicyData.get("policyModNumber");
								data.setSessionData("POLICY_MOD", strPolicyMod);
								data.addToLog(currElementName,
										"Setting POLICY_MOD into session :: " + data.getSessionData("POLICY_MOD"));
							}

							if (basicPolicyDetail.containsKey("policySymbol")) {
								strPolicySymbol = (String) basicPolicyDetail.get("policySymbol");
								data.setSessionData("POLICY_SYMBOL", strPolicySymbol);
								if (basicPolicyDetail.containsKey("insurerCompanyCode")) {
									String strInsurerCompanyCode = (String) basicPolicyDetail.get("insurerCompanyCode");
									setBWSegmentation(caa, data, strPolicySymbol + "-" + strInsurerCompanyCode);
								}
								data.addToLog(currElementName, "Setting POLICY_SYMBOL into session :: "
										+ data.getSessionData("POLICY_SYMBOL"));
							}

							/**
							 * Policy Mod and Policy Symbol retreived
							 */

							if (productPolicyMap.containsKey(Constants.PRODUCTTYPE_AUTO)) {
								HashMap<String, PolicyBean> tmpPolicyDetails = productPolicyMap
										.get(Constants.PRODUCTTYPE_AUTO);
								tmpPolicyDetails.put(strPolicyNum, beanObj);
								productPolicyMap.put(Constants.PRODUCTTYPE_AUTO, tmpPolicyDetails);
							} else {
								HashMap<String, PolicyBean> policyDetails = new HashMap<String, PolicyBean>();
								policyDetails.put(strPolicyNum, beanObj);
								productPolicyMap.put(Constants.PRODUCTTYPE_AUTO, policyDetails);
							}

							if (policyData.containsKey("agentDetails")) {
								JSONObject agentdetails = (JSONObject) policyData.get("agentDetails");
								agentAORid = (String) agentdetails.get("agentCode");
								beanObj.setStrAgentAORID(agentAORid);
								data.addToLog(currElementName, "AOR ID :: " + agentAORid);
								data.setSessionData("S_AGENT_ID", agentAORid);
							}

							// CS1348016 - All BU's - Onboarding Line Routing
							if (basicPolicyData.containsKey("inceptionDate")) {
								String strInceptionDate = (String) basicPolicyData.get("inceptionDate");
								beanObj.setInceptionDate(strInceptionDate);
								data.addToLog(currElementName,
										"Setting Inception Date  into Bean :: " + strInceptionDate);
							}
						}

						data.setSessionData(Constants.S_POLICYDETAILS_MAP, productPolicyMap);
						data.addToLog(currElementName, "Product Type Count Hashmap : " + productTypeCounts);
						data.addToLog(currElementName, "policyDetails Hashmap : " + productPolicyMap);

						String policyCollectionFlag = (String) data.getSessionData("Policy_Collection");
						if ("True".equalsIgnoreCase(policyCollectionFlag) && productPolicyMap != null
								&& productPolicyMap.get(Constants.PRODUCTTYPE_AUTO) != null
								&& productPolicyMap.get(Constants.PRODUCTTYPE_AUTO).size() == 1) {
							data.setSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE,
									Constants.S_SINGLE_POLICY_FOUND);
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

									// onboarding check code
									// CS1348016 - All BU's - Onboarding Line Routing
									String apponBoardingFlag = (String) data
											.getSessionData(Constants.S_APP_ONBOARDING_ELIGIBLE_FLAG);
									String onBoardingFlag = (String) data
											.getSessionData(Constants.S_ONBOARDING_ELIGIBLE);
									String policyInceptionDate = obj.getInceptionDate();
									if (!onBoardingFlag.equalsIgnoreCase(Constants.Y_FLAG) && !"".equalsIgnoreCase(policyInceptionDate))  {
										// if( apponBoardingFlag.equalsIgnoreCase(Constants.Y_FLAG)) {
										
										data.addToLog("Inception date is: ", policyInceptionDate);
										LocalDate currentDate = LocalDate.now();
										DateTimeFormatter formatter = new DateTimeFormatterBuilder()
												.parseCaseInsensitive().appendPattern("M/d/yyyy").toFormatter();
										String formattedDate = currentDate.format(formatter);
										data.addToLog("The Policy Inception Date is " + policyInceptionDate,
												"The Current date is " + formattedDate);
										LocalDate date1 = LocalDate.parse(policyInceptionDate, formatter);
										LocalDate date2 = LocalDate.parse(formattedDate, formatter);
										long daysBetween = ChronoUnit.DAYS.between(date1, date2);
										String daysBetweenString = String.valueOf(daysBetween);
										data.addToLog("The Days Between from Policy started and the current date::  ",
												daysBetweenString);
										String stronBoardingEligibleDays = (String) data
												.getSessionData(Constants.S_ONBOARDING_ELIGIBLE_DAYS);
										data.addToLog("The  days in Config file ::", stronBoardingEligibleDays);
										Long onBoardingEligibleDays = Long.valueOf(stronBoardingEligibleDays);

										// <=60 days check for onboarding->NLU
										appTag = (String) data.getSessionData(Constants.APPTAG);

										if ((appTag != null && !Constants.EmptyString.equalsIgnoreCase(appTag))
												&& apponBoardingFlag.equalsIgnoreCase(Constants.Y_FLAG)) {
											data.addToLog(currElementName, "It's from NLU" + appTag);
											if (daysBetween <= onBoardingEligibleDays) {
												data.setSessionData(Constants.S_ONBOARDING_ELIGIBLE, Constants.Y_FLAG);
												data.addToLog("The Policy Inception date is lesser than 60 Days::",
														"Y");
											} else {
												data.setSessionData(Constants.S_ONBOARDING_ELIGIBLE, Constants.N_FLAG);
												data.addToLog("The Policy Inception date is Lesser than 60 Days::",
														"N");
											}
										}
										// <=60 days check for onbaording->Main menus
										else {
											data.addToLog(currElementName, "It's from Main Menu");
											if (daysBetween <= onBoardingEligibleDays) {
												data.setSessionData(Constants.S_ONBOARDING_ELIGIBLE, Constants.Y_FLAG);
												data.addToLog("The Policy Inception date is lesser than 60 Days::",
														"Y");
											} else {
												data.setSessionData(Constants.S_ONBOARDING_ELIGIBLE, Constants.N_FLAG);
												data.addToLog("The Policy Inception date is Lesser than 60 Days::",
														"N");
											}
										}

										// }
//						    else {
//						    	data.setSessionData(Constants.S_ONBOARDING_ELIGIBLE, Constants.N_FLAG);
//						    	data.addToLog(currElementName, "In AppTag File the OnBoarding Eligible Flag as N");
//						    }
									} else {
										data.addToLog(currElementName,
												"The OnBoarding Is Eligible and it is setted in MDM Lookup::"
														+ onBoardingFlag);
									}
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
							data.setSessionData(Constants.VXMLParam1, "AUTO");
							data.setSessionData(Constants.VXMLParam2, "NA");
							data.setSessionData(Constants.VXMLParam3, "NA");
							data.setSessionData(Constants.VXMLParam4, "NA");
							data.addToLog(currElementName, "Single Auto Policy");
							data.addToLog(currElementName, "VXMLPARAM1 = " + data.getSessionData(Constants.VXMLParam1));
							// DOB BYPASS CHANGE
							strExitState = Constants.SU;
						} else if (productPolicyMap != null && productPolicyMap.get(Constants.PRODUCTTYPE_AUTO) != null
								&& productPolicyMap.get(Constants.PRODUCTTYPE_AUTO).size() > 1) {

							data.setSessionData(Constants.VXMLParam1, "AUTO");
							data.setSessionData(Constants.VXMLParam2, "NA");
							data.setSessionData(Constants.VXMLParam3, "NA");
							data.setSessionData(Constants.VXMLParam4, "NA");
							data.addToLog(currElementName, "Single Product Type Multiple Policy scenario");
							data.addToLog(currElementName, "VXMLPARAM1 = " + data.getSessionData(Constants.VXMLParam1));

							HashMap<String, PolicyBean> policyDetailsMap = productPolicyMap
									.get(Constants.PRODUCTTYPE_AUTO);
							data.setSessionData(Constants.POLICY_DETAILS_FOR_MN3, policyDetailsMap);

							data.setSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE,
									Constants.S_MULTIPLE_POLICIES_SINGLE_PRODUCTTYPE_FOUND);
							data.addToLog(currElementName, "S_NOOF_POLICIES_ANILOOKUP_EXITSTATE : "
									+ data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE));

							strExitState = Constants.SU;
						} else {
							strExitState = Constants.ER;
						}
					} else {
						strExitState = Constants.ER;
					}
				} else {

					data.addToLog(currElementName, "Check BW Policy ");
					data.setSessionData(Constants.S_NOOF_POLICIES_LOOKUP, autoPoliciesarr.size());
					data.setSessionData(Constants.IS_MORETHAN_ZERO_POLICIES, Constants.STRING_YES);
					Map<String, Integer> productTypeCounts = new HashMap<>();
					HashMap<String, HashMap<String, PolicyBean>> productPolicyMap = new HashMap<String, HashMap<String, PolicyBean>>();
					String strDOB = "", strPolicyMod = "", strPolicySymbol = "";
					for (int i = 0; i < autoPoliciesarr.size(); i++) {
						JSONObject policyData = (JSONObject) autoPoliciesarr.get(i);
						data.addToLog(currElementName, "Policy Returned" + autoPoliciesarr.size());

						if (null != policyData && policyData.containsKey("agentDetails")) {
							JSONObject agentdetails = (JSONObject) policyData.get("agentDetails");
							agentAORid = (String) agentdetails.get("agentCode");
							data.addToLog(currElementName, "AOR ID :: " + agentAORid);
							// beanObj.setStrAgentAORID(agentAORid);
							data.setSessionData("S_AGENT_ID", agentAORid);
						}

						if (policyData != null && policyData.get("basicPolicy") != null
								&& policyData.get("insuredVehicle") != null && policyData.get("namedInsured") != null) {
							data.addToLog(currElementName,
									"Policy Returned in correct format" + policyData.get("basicPolicy"));

							JSONObject basicPolicyData = (JSONObject) policyData.get("basicPolicy");
							JSONObject basicPolicyDetail = (JSONObject) policyData.get("basicPolicyDetail");
							JSONObject insuredVehicleData = (JSONObject) policyData.get("insuredVehicle");
							JSONObject namedInsuredData = (JSONObject) policyData.get("namedInsured");

							if (basicPolicyData.containsKey("policyNumber")
									&& policyNum.contains("" + basicPolicyData.get("policyNumber"))) {
								data.setSessionData("S_POLICY_NUM", basicPolicyData.get("policyNumber"));
								data.setSessionData(Constants.S_API_DOB, "" + namedInsuredData.get("birthDate"));
								JSONObject garagingAddress = (JSONObject) insuredVehicleData.get("garagingAddress");
								data.setSessionData(Constants.S_API_ZIP, (String) garagingAddress.get("postalCode"));
								data.setSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE,
										Constants.S_SINGLE_POLICY_FOUND);
								data.addToLog(currElementName, "S_NOOF_POLICIES_ANILOOKUP_EXITSTATE : "
										+ data.getSessionData(Constants.S_SINGLE_POLICY_FOUND));
								data.addToLog(currElementName, "S_API_DOB : " + namedInsuredData.get("birthDate"));

								if (policyData.containsKey("namedInsured")) {
									JSONObject obj = (JSONObject) policyData.get("namedInsured");
									strDOB = (strDOB.equals("")) ? "" + obj.get("birthDate")
											: strDOB + "," + obj.get("birthDate");
									data.setSessionData(Constants.S_API_DOB, "" + strDOB);
									data.addToLog(currElementName, "S_API_DOB : " + strDOB);
								}

								if (basicPolicyDetail.containsKey("policySymbol")) {
									strPolicySymbol = (String) basicPolicyDetail.get("policySymbol");
									if (basicPolicyDetail.containsKey("insurerCompanyCode")) {
										String strInsurerCompanyCode = (String) basicPolicyDetail
												.get("insurerCompanyCode");
										setBWSegmentation(caa, data, strPolicySymbol + "-" + strInsurerCompanyCode);
									}
								}

							}

							/**
							 * Policy Mod and Policy Symbol retreived
							 */

							if (basicPolicyData.containsKey("policyModNumber")) {
								strPolicyMod = (String) basicPolicyData.get("policyModNumber");
								data.setSessionData("POLICY_MOD", strPolicyMod);
							}

							if (basicPolicyDetail.containsKey("policySymbol")) {
								strPolicySymbol = (String) basicPolicyDetail.get("policySymbol");
								data.setSessionData("POLICY_SYMBOL", strPolicySymbol);
							}

							/**
							 * Policy Mod and Policy Symbol retreived
							 */
						}

						strExitState = Constants.SU;
					}
					data.addToLog(currElementName,
							"Product Type Count Hashmap : " + data.getSessionData(Constants.S_API_DOB));
					data.addToLog(currElementName,
							"policyDetails Hashmap : " + data.getSessionData(Constants.S_API_ZIP));
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
				}
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in AccLinkAniLookup method  :: " + e);
			caa.printStackTrace(e);
		}
		return strExitState;

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

	private String apiResponseManupulation_PointPolicyInquiry(DecisionElementData data, CommonAPIAccess caa,
			String currElementName, String strRespBody) {
		String strExitState = Constants.ER, appTag = Constants.EmptyString;
		try {
			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			JSONObject policySummaryObj = (JSONObject) resp.get("policySummary");

			if (policySummaryObj != null && policySummaryObj.containsKey("policyNumber")) {
				data.setSessionData(Constants.S_NOOF_POLICIES_LOOKUP, 1);
				data.setSessionData(Constants.IS_MORETHAN_ZERO_POLICIES, Constants.STRING_YES);
				Map<String, Integer> productTypeCounts = new HashMap<>();
				HashMap<String, HashMap<String, PolicyBean>> productPolicyMap = new HashMap<String, HashMap<String, PolicyBean>>();
				String strPolicyNum = Constants.EmptyString;
				String strPolicyCompanyCode = Constants.EmptyString;
				String strPolicySourceSystem = Constants.EmptyString;
				String concatenatedDOB = Constants.EmptyString;
				PolicyBean beanObj = new PolicyBean();

				if (policySummaryObj.containsKey("address")) {
					JSONArray autoPoliciesarr = (JSONArray) policySummaryObj.get("address");
					JSONObject addressObj = (JSONObject) autoPoliciesarr.get(0);
					if (addressObj.containsKey("zip"))
						beanObj.setStrZipCode((String) addressObj.get("zip"));

					// CS1151307 : Update Policy State Name(Policy State : HI, PolicyStateName :
					// Hawaii)
					if (addressObj.containsKey("state"))
						beanObj.setStrPolicyState((String) addressObj.get("state"));
					String strPolicyState = (String) beanObj.getStrPolicyState();
					data.setSessionData(Constants.S_POLICY_STATE_CODE, strPolicyState);
					data.addToLog(currElementName, "Policy State Code :" + strPolicyState);
					String strPolicyStateName = setStateName(data, caa, currElementName, strPolicyState);
					data.addToLog(currElementName, "Policy State Name:" + strPolicyStateName);
				}

				if (policySummaryObj.containsKey("autoPolicy")) {
					JSONObject autoPolicyObj = (JSONObject) policySummaryObj.get("autoPolicy");
					JSONArray driverssarr = (JSONArray) autoPolicyObj.get("drivers");
					for (Object driverArrIterator : driverssarr) {
						JSONObject driverObject = (JSONObject) driverArrIterator;
						if (driverObject.containsKey("dateofbirth")) {
							if ("".equalsIgnoreCase(concatenatedDOB)) {
								concatenatedDOB = (String) driverObject.get("dateofbirth");
							} else {
								concatenatedDOB = concatenatedDOB + "," + (String) driverObject.get("dateofbirth");
							}
						}
					}
					data.addToLog(currElementName, "Concatenated DOB :: " + concatenatedDOB);
					beanObj.setStrDOB(concatenatedDOB);
				}

				if (policySummaryObj.containsKey("policyNumber")) {
					strPolicyNum = (String) policySummaryObj.get("policyNumber");
				}

				if (policySummaryObj.containsKey("masterCompanyCode")) {
					strPolicyCompanyCode = (String) policySummaryObj.get("masterCompanyCode");
				}

				if (policySummaryObj.containsKey("policySourceSystem")) {
					strPolicySourceSystem = (String) policySummaryObj.get("policySourceSystem");
					beanObj.setStrPolicySource(strPolicySourceSystem);
				}

				// CS1348016 - All BU's - Onboarding Line Routing
				if (policySummaryObj.containsKey("inceptionDate")) {
					String strInceptionDate = (String) policySummaryObj.get("inceptionDate");
					beanObj.setInceptionDate(strInceptionDate);
					data.addToLog(currElementName, "Setting Inception Date  into Bean :: " + strInceptionDate);
				}

				HashMap<String, PolicyBean> policyDetails = new HashMap<String, PolicyBean>();
				policyDetails.put(strPolicyNum, beanObj);
				productPolicyMap.put("21C", policyDetails);
				data.setSessionData("S_POLICY_MCO", strPolicyCompanyCode);
				data.setSessionData(Constants.S_POLICYDETAILS_MAP, productPolicyMap);
				data.addToLog(currElementName, "Product Type Count Hashmap : " + productTypeCounts);
				data.addToLog(currElementName, "policyDetails Hashmap : " + productPolicyMap);
				if (policyDetails.size() == 1) {
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
							data.setSessionData(Constants.S_POLICY_SOURCE, strPolicySourceSystem);

							// onboarding check code
							// CS1348016 - All BU's - Onboarding Line Routing
							String apponBoardingFlag = (String) data
									.getSessionData(Constants.S_APP_ONBOARDING_ELIGIBLE_FLAG);
							String onBoardingFlag = (String) data.getSessionData(Constants.S_ONBOARDING_ELIGIBLE);
							String policyInceptionDate = obj.getInceptionDate();
							if (!onBoardingFlag.equalsIgnoreCase(Constants.Y_FLAG) && !"".equalsIgnoreCase(policyInceptionDate))  {
								// if( apponBoardingFlag.equalsIgnoreCase(Constants.Y_FLAG)) {
								
								data.addToLog("Inception date is: ", policyInceptionDate);
								LocalDate currentDate = LocalDate.now();
								DateTimeFormatter formatter = new DateTimeFormatterBuilder().parseCaseInsensitive()
										.appendPattern("M/d/yyyy").toFormatter();
								String formattedDate = currentDate.format(formatter);
								data.addToLog("The Policy Inception Date is " + policyInceptionDate,
										"The Current date is " + formattedDate);
								LocalDate date1 = LocalDate.parse(policyInceptionDate, formatter);
								LocalDate date2 = LocalDate.parse(formattedDate, formatter);
								long daysBetween = ChronoUnit.DAYS.between(date1, date2);
								String daysBetweenString = String.valueOf(daysBetween);
								data.addToLog("The Days Between from Policy started and the current date::  ",
										daysBetweenString);
								String stronBoardingEligibleDays = (String) data
										.getSessionData(Constants.S_ONBOARDING_ELIGIBLE_DAYS);
								data.addToLog("The  days in Config file ::", stronBoardingEligibleDays);
								Long onBoardingEligibleDays = Long.valueOf(stronBoardingEligibleDays);

								// <=60 days check for onboarding->NLU
								appTag = (String) data.getSessionData(Constants.APPTAG);

								if ((appTag != null && !Constants.EmptyString.equalsIgnoreCase(appTag))
										&& apponBoardingFlag.equalsIgnoreCase(Constants.Y_FLAG)) {
									data.addToLog(currElementName, "It's from NLU" + appTag);
									if (daysBetween <= onBoardingEligibleDays) {
										data.setSessionData(Constants.S_ONBOARDING_ELIGIBLE, Constants.Y_FLAG);
										data.addToLog("The Policy Inception date is lesser than 60 Days::", "Y");
									} else {
										data.setSessionData(Constants.S_ONBOARDING_ELIGIBLE, Constants.N_FLAG);
										data.addToLog("The Policy Inception date is Lesser than 60 Days::", "N");
									}
								}
								// <=60 days check for onbaording->Main menus
								else {
									data.addToLog(currElementName, "It's from Main Menu");
									if (daysBetween <= onBoardingEligibleDays) {
										data.setSessionData(Constants.S_ONBOARDING_ELIGIBLE, Constants.Y_FLAG);
										data.addToLog("The Policy Inception date is lesser than 60 Days::", "Y");
									} else {
										data.setSessionData(Constants.S_ONBOARDING_ELIGIBLE, Constants.N_FLAG);
										data.addToLog("The Policy Inception date is Lesser than 60 Days::", "N");
									}
								}

//							}
//						    else {
//						    	data.setSessionData(Constants.S_ONBOARDING_ELIGIBLE, Constants.N_FLAG);
//						    	data.addToLog(currElementName, "In AppTag File the OnBoarding Eligible Flag as N");
//						    }
							} else {
								data.addToLog(currElementName,
										"The OnBoarding Is Eligible and it is setted in MDM Lookup::" + onBoardingFlag);
							}

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
					data.setSessionData(Constants.VXMLParam1, "AUTO");
					data.setSessionData(Constants.VXMLParam2, "NA");
					data.setSessionData(Constants.VXMLParam3, "NA");
					data.setSessionData(Constants.VXMLParam4, "NA");
					data.addToLog(currElementName, "Single Auto Policy");
					data.addToLog(currElementName, "VXMLPARAM1 = " + data.getSessionData(Constants.VXMLParam1));
					strExitState = Constants.SU;
				} else {
					strExitState = Constants.ER;
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

	private void setBWSegmentation(CommonAPIAccess caa, DecisionElementData data, String segmentationValue) {
		String strKeyName = Constants.EmptyString;
		try {
			String bwOther = (String) data.getSessionData("S_BW_OTHER");
			String bw44 = (String) data.getSessionData("S_BW_44");
			String bwCMMLOther = (String) data.getSessionData("S_BW_CMML_OTHER");
			String bwCMML44 = (String) data.getSessionData("S_BW_CMML_44");
			if (bwOther.toUpperCase().contains(segmentationValue.toUpperCase())) {
				strKeyName = "BW_OTHER";
			} else if (bw44.toUpperCase().contains(segmentationValue.toUpperCase())) {
				strKeyName = "BW_44";
			} else if (bwCMMLOther.toUpperCase().contains(segmentationValue.toUpperCase())) {
				strKeyName = "BW_CMML_OTHER";
			} else if (bwCMML44.toUpperCase().contains(segmentationValue.toUpperCase())) {
				strKeyName = "BW_CMML_44";
			}

			data.addToLog(data.getCurrentElement(),
					"BW Segmenation Value : " + segmentationValue + " Segmenation key : " + strKeyName);
			if (strKeyName != null && !strKeyName.equals("")) {
				data.addToLog(data.getCurrentElement(), "Set policy segmentation into session  "
						+ Constants.S_POLICY_SEGMENTATION + " : " + strKeyName);
				data.setSessionData(Constants.S_POLICY_SEGMENTATION, strKeyName);
			}
		} catch (Exception e) {
			data.addToLog(data.getCurrentElement(), "Exception while fetching BW Segmentation " + e);
			caa.printStackTrace(e);
		}
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

		// Retrieve the configuration categories from the ivrconfig properties :
		// S_CONFIG_CATEGORIES
		String configCategories = (String) data.getSessionData(Constants.S_CONFIG_CATEGORIES);
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

	public static void main(String[] args) {

		String lob = "Foremost";
		String list = "Foremost AutoBristol West";

		/*
		 * String[] lobList = list.split(","); ArrayList<String> foremost_Lob = null;
		 * 
		 * foremost_Lob = new ArrayList<String>(); for (String lobVal : lobList) {
		 * foremost_Lob.add(lobVal); }
		 * 
		 * if(foremost_Lob.contains(lob)) { System.out.println("Contains"); }else {
		 * System.out.println("Doesn't Contain"); }
		 */
		/*
		 * String[] arr = null; if(list.contains(",")) { arr = list.split(","); }else {
		 * arr = new String[1]; arr[0] = list; }
		 * 
		 * for(String str : arr) { System.out.println("String :: "+ str); }
		 */
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
