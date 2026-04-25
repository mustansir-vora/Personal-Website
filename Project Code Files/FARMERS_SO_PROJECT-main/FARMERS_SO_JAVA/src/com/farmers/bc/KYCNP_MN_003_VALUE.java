package com.farmers.bc;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class KYCNP_MN_003_VALUE extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);

		try {
			String strReturnValue = (String) data.getElementData("KYCNP_MN_003_Call", "Return_Value");
			data.setSessionData("KYCNP_MN_003_VALUE", strReturnValue);

			data.addToLog(currElementName, "KYCNP_MN_003_Call Before: Return_Value :: " + strReturnValue);

			int rvprodtypecount = (int) data.getSessionData(Constants.RV_PRODUCTTYPECOUNT_KYC);
			int autoprodtypecount = (int) data.getSessionData(Constants.AUTO_PRODUCTTYPECOUNT_KYC);
			int homeprodtypecount = (int) data.getSessionData(Constants.HOME_PRODUCTTYPECOUNT_KYC);
			int umbrellaprodtypecount = (int) data.getSessionData(Constants.UMBRELLA_PRODUCTTYPECOUNT_KYC);
			int boatprodtypecount = (int) data.getSessionData(Constants.MR_PRODUCTTYPECOUNT_KYC);
			int spprodtypecount = (int) data.getSessionData(Constants.SP_PRODUCTTYPECOUNT_KYC);

			String productType = null;

			if (rvprodtypecount >= 2) {
				productType = "RV";
			} else if (autoprodtypecount >= 2) {
				productType = "AUTO";
			} else if (homeprodtypecount >= 2) {
				productType = "HOME";
			} else if (umbrellaprodtypecount >= 2) {
				productType = "UMBRELLA";
			} else if (boatprodtypecount >= 2) {
				productType = "MR";
			} else if (spprodtypecount >= 2) {
				productType = "SP";
			}

			if (data.getSessionData(Constants.IS_MORETHAN_ONE_POLICY_PRODUCTTYPE) != null && 
					!data.getSessionData(Constants.IS_MORETHAN_ONE_POLICY_PRODUCTTYPE).equals("")) {

				productType = (String) data.getSessionData(Constants.IS_MORETHAN_ONE_POLICY_PRODUCTTYPE);
				data.addToLog(currElementName, "Product Type Come From KYCNP_MN_002_Value" + productType);

			} 

			else if (productType != null) {
				data.setSessionData(Constants.IS_MORETHAN_ONE_POLICIES, Constants.STRING_YES);
				data.setSessionData(Constants.IS_MORETHAN_ONE_POLICY_PRODUCTTYPE, productType);
			} else {
				data.setSessionData(Constants.IS_MORETHAN_ONE_POLICIES, Constants.STRING_NO);
				data.setSessionData(Constants.IS_MORETHAN_ONE_POLICY_PRODUCTTYPE, "");
			}

			data.addToLog(currElementName, "IS_MORETHAN_ONE_POLICY_PRODUCTTYPE: " + data.getSessionData(Constants.IS_MORETHAN_ONE_POLICY_PRODUCTTYPE));

			String resp = (String) data.getSessionData(Constants.ACCLINKANIJSONRESPSTRING);
			data.addToLog(currElementName, "ACCLINKANILOOKUP RESP STRING: " + resp);

			JSONArray filteredPolicies = filterPolicies(resp, productType, currElementName, data);
			data.addToLog(currElementName, "Setting Filtered Policies Object into session :: "+filteredPolicies);
			data.setSessionData(Constants.FILTERED_POLICIES, filteredPolicies.toString());

			int count = 0;
			for (Object policyobj : filteredPolicies) {
				JSONObject policy = (JSONObject) policyobj;
				String policyNumber = (String) policy.get("policyNumber");
				String finalcount = String.valueOf(count + 1);
				String vxmlParam = "VXMLParam" + finalcount;
				data.setSessionData(vxmlParam, policyNumber.substring(policyNumber.length() - 4));
				data.addToLog(currElementName, "Dynamic VXMLParam that is set into session :: "+data.getSessionData(vxmlParam));
				data.addToLog(currElementName, vxmlParam + " = " + data.getSessionData(vxmlParam));
				count++;
			}


			if(strReturnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			} else if(strReturnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
			} else if(strReturnValue.equalsIgnoreCase(Constants.REPRESENTATIVE)) {
				strExitState = Constants.REPRESENTATIVE;
			} else if (strReturnValue.equalsIgnoreCase("yes")) {
				strExitState = Constants.STRING_YES;
			}

		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in KYCNP_MN_003_VALUE: " + e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName, "KYCNP_MN_003_VALUE: " + strExitState);
		return strExitState;
	}

	public JSONArray filterPolicies(String respString, String productType, String currElementName, DecisionElementData data) {
		JSONArray matchingPolicies = new JSONArray();
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {
			JSONObject resp = (JSONObject) new JSONParser().parse(respString);
			JSONObject results = (JSONObject) resp.get("results");
			JSONArray policiesArray = (JSONArray) results.get("policies");

			for (Object policyObj : policiesArray) {
				JSONObject policy = (JSONObject) policyObj;
				String lineOfBusiness = (String) policy.get("lineOfBusiness");
				if (lineOfBusiness.equals(productType)) {
					matchingPolicies.add(policy);
				}
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in KYCNP_MN_003_VALUE API Response Manipulation: " + e);
			caa.printStackTrace(e);
		}
		return matchingPolicies;
	}
}
