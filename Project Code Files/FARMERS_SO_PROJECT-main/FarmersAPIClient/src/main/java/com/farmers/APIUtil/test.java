package com.farmers.APIUtil;

import java.io.FileReader;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class test {

	public static void main(String[] args) {
		
		try {
			JSONObject resp = (JSONObject) new JSONParser().parse("{\r\n"
					+ "    \"billingInfo\": {\r\n"
					+ "        \"caseType\": \"ACT\",\r\n"
					+ "        \"billingDetails\": {\r\n"
					+ "            \"balanceOnPriorMode\": {\r\n"
					+ "                \"theCurrencyAmount\": null\r\n"
					+ "            },\r\n"
					+ "            \"balanceThresholdAmountOnPriorMode\": {\r\n"
					+ "                \"theCurrencyAmount\": null\r\n"
					+ "            },\r\n"
					+ "            \"fullBalanceDue\": {\r\n"
					+ "                \"theCurrencyAmount\": \"0.00\"\r\n"
					+ "            },\r\n"
					+ "            \"lastPaymentAmount\": {\r\n"
					+ "                \"theCurrencyAmount\": \"3153.00\"\r\n"
					+ "            },\r\n"
					+ "            \"lastPaymentDate\": \"2024-01-29\",\r\n"
					+ "            \"nextPaymentAmount\": {\r\n"
					+ "                \"theCurrencyAmount\": null\r\n"
					+ "            },\r\n"
					+ "            \"nextPaymentDate\": \"\",\r\n"
					+ "            \"policyActivityDate\": \"\",\r\n"
					+ "            \"policyCollectionAmount\": {\r\n"
					+ "                \"theCurrencyAmount\": null\r\n"
					+ "            },\r\n"
					+ "            \"policyCollectionThresholdAmount\": {\r\n"
					+ "                \"theCurrencyAmount\": null\r\n"
					+ "            },\r\n"
					+ "            \"hasBalanceOnPriorModeAsString\": \"N\",\r\n"
					+ "            \"hasEFTIndicatorAsString\": \"N\",\r\n"
					+ "            \"isPolicyInCollectionMode\": null\r\n"
					+ "        },\r\n"
					+ "        \"insuredDetails\": {\r\n"
					+ "            \"email\": \"ate2104@yahoo.com\"\r\n"
					+ "        },\r\n"
					+ "        \"policyDetails\": {\r\n"
					+ "            \"agentReference\": \"0943897\",\r\n"
					+ "            \"chargeFeeAmount\": {\r\n"
					+ "                \"theCurrencyAmount\": \"0\"\r\n"
					+ "            },\r\n"
					+ "            \"insurerCompanyCode\": \"62\",\r\n"
					+ "            \"lapsePeriodForPolicyReinstate\": null,\r\n"
					+ "            \"lapsePeriodForPolicyRenewal\": null,\r\n"
					+ "            \"notSufficientFundAmount\": {\r\n"
					+ "                \"theCurrencyAmount\": null\r\n"
					+ "            },\r\n"
					+ "            \"notSufficientFundIndicatorAsString\": null,\r\n"
					+ "            \"plannedEndDate\": \"2025-01-27\",\r\n"
					+ "            \"policyCancellationDate\": \"\",\r\n"
					+ "            \"policyEffectiveDate\": \"2024-01-27\",\r\n"
					+ "            \"policyNumber\": \"M00002699603\",\r\n"
					+ "            \"policyRewriteOfferAmount\": {\r\n"
					+ "                \"theCurrencyAmount\": null\r\n"
					+ "            },\r\n"
					+ "            \"policyRewriteOfferExpiryDate\": \"\",\r\n"
					+ "            \"policyStatus\": \"ACT\",\r\n"
					+ "            \"policyWrittenDate\": \"2024-02-20\",\r\n"
					+ "            \"policyZipcode\": \"33325\",\r\n"
					+ "            \"isPolicyQualifiedForReinstateWithLapse\": null,\r\n"
					+ "            \"isPolicyQualifiedForRenewalWithLapse\": null,\r\n"
					+ "            \"isPolicyQualifiedForRewrittenWithLapse\": null,\r\n"
					+ "            \"isPolicyRewriteOfferAvailable\": null,\r\n"
					+ "            \"isRenewalPendingIndicatorAsString\": \"N\"\r\n"
					+ "        },\r\n"
					+ "        \"renewalPolicyBillingDetails\": {\r\n"
					+ "            \"fullBalanceDue\": {\r\n"
					+ "                \"theCurrencyAmount\": null\r\n"
					+ "            },\r\n"
					+ "            \"lastPaymentAmount\": {\r\n"
					+ "                \"theCurrencyAmount\": null\r\n"
					+ "            },\r\n"
					+ "            \"lastPaymentDate\": \"\",\r\n"
					+ "            \"nextPaymentAmount\": {\r\n"
					+ "                \"theCurrencyAmount\": null\r\n"
					+ "            },\r\n"
					+ "            \"nextPaymentDate\": \"\",\r\n"
					+ "            \"policyActivityDate\": \"\"\r\n"
					+ "        },\r\n"
					+ "        \"renewalPolicyDetails\": {\r\n"
					+ "            \"agentReference\": null,\r\n"
					+ "            \"chargeFeeAmount\": {\r\n"
					+ "                \"theCurrencyAmount\": \"0\"\r\n"
					+ "            },\r\n"
					+ "            \"insurerCompanyCode\": null,\r\n"
					+ "            \"lapsePeriodForPolicyRenewal\": null,\r\n"
					+ "            \"plannedEndDate\": \"\",\r\n"
					+ "            \"policyCancellationDate\": \"\",\r\n"
					+ "            \"policyEffectiveDate\": \"\",\r\n"
					+ "            \"policyNumber\": null,\r\n"
					+ "            \"policyStatus\": null,\r\n"
					+ "            \"policyWrittenDate\": \"\",\r\n"
					+ "            \"policyZipcode\": null\r\n"
					+ "        },\r\n"
					+ "        \"transactionNotification\": {\r\n"
					+ "            \"transactionStatus\": \"S\"\r\n"
					+ "        }\r\n"
					+ "    }\r\n"
					+ "}");
			System.out.println("Parsed JSONObject :: " + resp);
			String fullBalance = getValuesFromJSONResp(resp, "billingInfo:policyDetails:agentReference");
			System.out.println("policyNumber :: " + fullBalance);
			} 
		catch(Exception e) {
			System.out.println("Exception Occured in finalObject Method :: " + e);
			e.printStackTrace();
		}
	}


	public static String getValuesFromJSONResp(JSONObject response, String location) {
		String finalString = null;
		JSONObject retrieve_JSON = null;
		String retrieve_String = null;
		Integer retrieve_Int = null;
		Double retrieve_Double = null;
		Boolean retrieve_Boolean;
		JSONArray retrieve_JSONArray = null;
		
		String[] locationSplitter = location.split(":");

		try {

			for (int i = 0; i < locationSplitter.length; i++) {
				if (null != response) {
					if (response.containsKey(locationSplitter[i])) {
						if (response.get(locationSplitter[i]) instanceof JSONObject) {
							retrieve_JSON = (JSONObject) response.get(locationSplitter[i]);
							response = retrieve_JSON;
						} else if (response.get(locationSplitter[i]) instanceof String) {
							retrieve_String = (String) response.get(locationSplitter[i]);
							finalString = retrieve_String;
						} else if (response.get(locationSplitter[i]) instanceof Integer) {
							retrieve_Int = (Integer) response.get(locationSplitter[i]);
							finalString = retrieve_Int.toString();
						} else if (response.get(locationSplitter[i]) instanceof Double) {
							retrieve_Double = (Double) response.get(locationSplitter[i]);
							finalString = retrieve_Double.toString();
						} else if (response.get(locationSplitter[i]) instanceof Boolean) {
							retrieve_Boolean = (Boolean) response.get(locationSplitter[i]);
							finalString = retrieve_Boolean.toString();
						}
						else if (response.get(locationSplitter[i]) instanceof JSONArray) {
							retrieve_JSONArray = (JSONArray) response.get(locationSplitter[i]);
							finalString = retrieve_JSONArray.toString();
						}
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Exception Occured in finalObject Method :: " + e);
			e.printStackTrace();
		}
		return finalString;
	}

}
