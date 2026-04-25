package com.farmers.bc;


import java.io.FileInputStream;
import java.util.Properties;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class KYCNP_BrandCheck extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		String dnisbrand = (String) data.getSessionData(Constants.S_BU);
		String strBristolCode = (String)data.getApplicationAPI().getApplicationData("A_KYCBA_BRISTOLWEST_LOB");
		String strFarmersCode = (String)data.getApplicationAPI().getApplicationData("A_FARMERS_LOB");
		String strForemostCode = (String)data.getApplicationAPI().getApplicationData("A_KYCBA_FOREMOST_LOB");
		String strFWSCode = (String)data.getApplicationAPI().getApplicationData("A_KYCBA_FWS_LOB");
		String str21stCode = (String)data.getApplicationAPI().getApplicationData("A_KYCBA_21ST_LOB");
		
		
		if (dnisbrand!=null && strFarmersCode!=null && (strFarmersCode.toLowerCase().contains(dnisbrand.toLowerCase()))) {
			dnisbrand = "PLA || GWPC";
		}else if (dnisbrand!=null && strBristolCode!=null && strBristolCode.toLowerCase().contains(dnisbrand.toLowerCase())) {
			dnisbrand = "BW";
		}
		else if (dnisbrand!=null && strForemostCode!=null && strForemostCode.toLowerCase().contains(dnisbrand.toLowerCase())) {
			dnisbrand = "FM";
		}
		else if (dnisbrand!=null && strFWSCode!=null && strFWSCode.toLowerCase().contains(dnisbrand.toLowerCase())) {
			dnisbrand = "FWS";
		}
		else if (dnisbrand!=null && str21stCode!=null && str21stCode.toLowerCase().contains(dnisbrand.toLowerCase())) {
			dnisbrand = "AUTO";
		}
		
		JSONObject finalpolicyobject = new JSONObject();
		String policynumber = null;
		String strExitState = Constants.ER;
		JSONObject policy = new JSONObject();
		String brand = null;
		String email = null;
		JSONObject insuredDetailsObj = new JSONObject();
		JSONObject postalAddressObj = new JSONObject();
		
		
		try {
			String ismorethanonepolicies = null != (String) data.getSessionData(Constants.IS_MORETHAN_ONE_POLICIES) ? (String) data.getSessionData(Constants.IS_MORETHAN_ONE_POLICIES) : Constants.EmptyString;
			data.addToLog(currElementName, "IS_MORETHAN_ONE_POLICIES : "+ismorethanonepolicies);
			String previousmenu = (String) data.getSessionData(Constants.PREVIOUS_MENU_BRANDCHECK);
			data.addToLog(currElementName, "Previous Menu Check - "+previousmenu);

				if (ismorethanonepolicies.equalsIgnoreCase(Constants.STRING_NO)) {
					String lob = null;
					String acclinkrespString = (String) data.getSessionData(Constants.ACCLINKANIJSONRESPSTRING);
					JSONObject acclinkrespJSON = (JSONObject) new JSONParser().parse(acclinkrespString);
					JSONObject results = (JSONObject) acclinkrespJSON.get("results");
					JSONArray policiesArray = (JSONArray) results.get("policies");
					String producttype = (String) data.getSessionData(Constants.IS_MORETHAN_ONE_POLICY_PRODUCTTYPE);
					for (Object policyObj : policiesArray) {
						policy = (JSONObject) policyObj;
						lob = (String) policy.get("lineOfBusiness");
						
						if (producttype.equalsIgnoreCase(lob)) {
							finalpolicyobject = policy;
							brand = (String) finalpolicyobject.get("policySource");
							policynumber = (String) finalpolicyobject.get("policyNumber");
							insuredDetailsObj = (JSONObject) finalpolicyobject.get("insuredDetails");
							postalAddressObj = (JSONObject) insuredDetailsObj.get("postalAddress");
							email = (String) postalAddressObj.get("email");
							
							if (null != email && !email.isEmpty()) {
								data.setSessionData(Constants.S_EMAIL, email);
								data.setSessionData(Constants.S_IS_POLICY_EMAIL_AVIALABLE, Constants.STRING_YES);
							}
							else {
								data.setSessionData(Constants.S_IS_POLICY_EMAIL_AVIALABLE, Constants.STRING_NO);
							}
							data.setSessionData(Constants.FINALPOLICYOBJECT_KYCNP, finalpolicyobject.toString());
							data.setSessionData(Constants.S_FINAL_BRAND, brand);
							data.setSessionData(Constants.S_POLICY_SOURCE, brand);
							data.addToLog(currElementName, "Selected Policy Brand : "+data.getSessionData(Constants.S_FINAL_BRAND));
							data.setSessionData(Constants.S_POLICY_NUM, policynumber);
							data.addToLog(currElementName, "Selected Policy Number : "+data.getSessionData(Constants.S_POLICY_NUM));
						}
					}
					strExitState = Constants.SU;
				}
				
				else if (ismorethanonepolicies.equalsIgnoreCase(Constants.STRING_YES)) {
					
					if (previousmenu.equalsIgnoreCase(Constants.KYCNP_MN_004_MENUNAME)) {
						
						String strReturnValue = (String) data.getElementData("KYCNP_MN_004_Call", "Return_Value");
						data.addToLog(currElementName, " KYCNP_MN_004_Call Before : Return_Value :: " + strReturnValue);
						
						String resp = (String) data.getSessionData(Constants.FILTERED_POLICIES);
						JSONArray filteredPolicies = (JSONArray) new JSONParser().parse(resp);
						
						if (null != strReturnValue && strReturnValue.toLowerCase().contains("one") || strReturnValue.toLowerCase().contains("two") || strReturnValue.toLowerCase().contains("three") || strReturnValue.toLowerCase().contains("four")	|| strReturnValue.toLowerCase().contains("five")) {
							data.addToLog(currElementName, "Single Option Selected - "+strReturnValue);
							if (strReturnValue.toLowerCase().contains("one") || strReturnValue.toLowerCase().contains("1")) {
								policy = (JSONObject) filteredPolicies.get(0);
							}

							if (strReturnValue.toLowerCase().contains("two") || strReturnValue.toLowerCase().contains("2")) {
								policy = (JSONObject) filteredPolicies.get(1);
							}

							if (strReturnValue.toLowerCase().contains("three") || strReturnValue.toLowerCase().contains("3")) {
								policy = (JSONObject) filteredPolicies.get(2);
							}

							if (strReturnValue.toLowerCase().contains("four") || strReturnValue.toLowerCase().contains("4")) {
								policy = (JSONObject) filteredPolicies.get(3);
							}

							if (strReturnValue.toLowerCase().contains("five") || strReturnValue.toLowerCase().contains("5")) {
								policy = (JSONObject) filteredPolicies.get(4);
							}
							brand = (String) policy.get("policySource");
							policynumber = (String) policy.get("policyNumber");
							insuredDetailsObj = (JSONObject) policy.get("insuredDetails");
							postalAddressObj = (JSONObject) insuredDetailsObj.get("postalAddress");
							email = (String) postalAddressObj.get("email");
							
							if (null != email && !email.isEmpty()) {
								data.setSessionData(Constants.S_EMAIL, email);
								data.setSessionData(Constants.S_IS_POLICY_EMAIL_AVIALABLE, Constants.STRING_YES);
							}
							data.setSessionData(Constants.FINALPOLICYOBJECT_KYCNP, policy.toString());
							data.setSessionData(Constants.S_FINAL_BRAND, brand);
							data.setSessionData(Constants.S_POLICY_SOURCE, brand);
							data.setSessionData(Constants.S_POLICY_NUM, policynumber);
							data.addToLog(currElementName, "Selected Policy Number - "+data.getSessionData(Constants.S_POLICY_NUM));
							data.addToLog(currElementName, "Selected Policy Brand : "+data.getSessionData(Constants.S_FINAL_BRAND));
							strExitState = Constants.SU;
						}
						else if (null != strReturnValue && strReturnValue.toLowerCase().contains("all of these")) {
							
								for (Object policyObj : filteredPolicies) {
									policy = (JSONObject) policyObj;
									brand = (String) policy.get("policySource");

									if (dnisbrand.toUpperCase().contains(brand.toUpperCase())) {
										finalpolicyobject = policy;
										brand = (String) finalpolicyobject.get("policySource");
										policynumber = (String) finalpolicyobject.get("policyNumber");
										insuredDetailsObj = (JSONObject) finalpolicyobject.get("insuredDetails");
										postalAddressObj = (JSONObject) insuredDetailsObj.get("postalAddress");
										email = (String) postalAddressObj.get("email");
										
										if (null != email && !email.isEmpty()) {
											data.setSessionData(Constants.S_EMAIL, email);
											data.setSessionData(Constants.S_IS_POLICY_EMAIL_AVIALABLE, Constants.STRING_YES);
										}
										else {
											data.setSessionData(Constants.S_IS_POLICY_EMAIL_AVIALABLE, Constants.STRING_NO);
										}
										data.setSessionData(Constants.FINALPOLICYOBJECT_KYCNP, finalpolicyobject.toString());
										data.setSessionData(Constants.S_FINAL_BRAND, brand);
										data.setSessionData(Constants.S_POLICY_SOURCE, brand);
										data.setSessionData(Constants.S_POLICY_NUM, policynumber);
									}
								}
									if (finalpolicyobject == null || finalpolicyobject.isEmpty()) {
									finalpolicyobject = (JSONObject) filteredPolicies.get(0);
									brand = (String) finalpolicyobject.get("policySource");
									policynumber = (String) finalpolicyobject.get("policyNumber");
									insuredDetailsObj = (JSONObject) finalpolicyobject.get("insuredDetails");
									postalAddressObj = (JSONObject) insuredDetailsObj.get("postalAddress");
									email = (String) postalAddressObj.get("email");
									
									if (null != email && !email.isEmpty()) {
										data.setSessionData(Constants.S_EMAIL, email);
										data.setSessionData(Constants.S_IS_POLICY_EMAIL_AVIALABLE, Constants.STRING_YES);
									}
									else {
										data.setSessionData(Constants.S_IS_POLICY_EMAIL_AVIALABLE, Constants.STRING_NO);
									}
									data.setSessionData(Constants.FINALPOLICYOBJECT_KYCNP, finalpolicyobject.toString());
									data.setSessionData(Constants.S_FINAL_BRAND, brand);
									data.setSessionData(Constants.S_POLICY_SOURCE, brand);
									data.setSessionData(Constants.S_POLICY_NUM, policynumber);
								}
									data.addToLog(currElementName, "Selected Policy Number - "+data.getSessionData(Constants.S_POLICY_NUM));
									data.addToLog(currElementName, "Selected Policy Brand : "+data.getSessionData(Constants.S_FINAL_BRAND));
									strExitState = Constants.SU;
						}
					}
			}
				
				if (previousmenu.equalsIgnoreCase(Constants.KYCNP_MN_002_MENUNAME)) {
					
					String strReturnValue = (String) data.getElementData("KYCNP_MN_002_Call", "Return_Value");
					
					if (strReturnValue != null && strReturnValue.toLowerCase().contains("all of these")) {
						
						data.addToLog(currElementName, "All of these selected - "+strReturnValue);	
						String acclinkrespString = (String) data.getSessionData(Constants.ACCLINKANIJSONRESPSTRING);
						JSONObject acclinkrespJSON = (JSONObject) new JSONParser().parse(acclinkrespString);
						JSONObject results = (JSONObject) acclinkrespJSON.get("results");
						JSONArray policiesArray = (JSONArray) results.get("policies");
							
						for (Object policyObj : policiesArray) {
							policy = (JSONObject) policyObj;
							brand = (String) policy.get("policySource");

							if (dnisbrand.toUpperCase().contains(brand.toUpperCase())) {
								finalpolicyobject = policy;
								brand = (String) finalpolicyobject.get("policySource");
								policynumber = (String) finalpolicyobject.get("policyNumber");
								insuredDetailsObj = (JSONObject) finalpolicyobject.get("insuredDetails");
								postalAddressObj = (JSONObject) insuredDetailsObj.get("postalAddress");
								email = (String) postalAddressObj.get("email");
								
								if (null != email && !email.isEmpty()) {
									data.setSessionData(Constants.S_EMAIL, email);
									data.setSessionData(Constants.S_IS_POLICY_EMAIL_AVIALABLE, Constants.STRING_YES);
								}
								else {
									data.setSessionData(Constants.S_IS_POLICY_EMAIL_AVIALABLE, Constants.STRING_NO);
								}
								data.setSessionData(Constants.FINALPOLICYOBJECT_KYCNP, finalpolicyobject.toString());
								data.setSessionData(Constants.S_FINAL_BRAND, brand);
								data.setSessionData(Constants.S_POLICY_SOURCE, brand);
								data.setSessionData(Constants.S_POLICY_NUM, policynumber);
							}
						 }
							if (finalpolicyobject == null || finalpolicyobject.isEmpty()) {
								finalpolicyobject = (JSONObject) policiesArray.get(0);
								brand = (String) finalpolicyobject.get("policySource");
								policynumber = (String) finalpolicyobject.get("policyNumber");
								data.setSessionData(Constants.FINALPOLICYOBJECT_KYCNP, finalpolicyobject.toString());
								insuredDetailsObj = (JSONObject) finalpolicyobject.get("insuredDetails");
								postalAddressObj = (JSONObject) insuredDetailsObj.get("postalAddress");
								email = (String) postalAddressObj.get("email");
								
								if (null != email && !email.isEmpty()) {
									data.setSessionData(Constants.S_EMAIL, email);
									data.setSessionData(Constants.S_IS_POLICY_EMAIL_AVIALABLE, Constants.STRING_YES);
									}
								else {
									data.setSessionData(Constants.S_IS_POLICY_EMAIL_AVIALABLE, Constants.STRING_NO);
								}
								data.setSessionData(Constants.S_FINAL_BRAND, brand);
								data.setSessionData(Constants.S_POLICY_SOURCE, brand);
								data.setSessionData(Constants.S_POLICY_NUM, policynumber);
							}
							data.addToLog(currElementName, "Selected Policy Number - "+data.getSessionData(Constants.S_POLICY_NUM));
							data.addToLog(currElementName, "Selected Policy Brand : "+data.getSessionData(Constants.S_FINAL_BRAND));
							strExitState = Constants.SU;
					}
				}
				
				switch (brand) {
				case Constants.S_API_BU_BW:
					data.addToLog(currElementName,":: BW :: ");
					data.setSessionData(Constants.S_BU, "Foremost Auto");
					data.setSessionData(Constants.S_CATEGORY,"FM_AUTO");
					data.setSessionData("S_FLAG_BW_BU", Constants.STRING_YES);
					data.addToLog(currElementName, "selected BW Policy in KYC :: Overriding BU :: " + data.getSessionData(Constants.S_BU) + " :: Overriding Category :: " + data.getSessionData(Constants.S_CATEGORY));
					break;
				case Constants.S_API_BU_FDS_PLA:
					data.addToLog(currElementName,":: FDS Execution  :: ");
					data.setSessionData(Constants.S_BU, "Farmers");
					data.setSessionData(Constants.S_CATEGORY,"FarmersSRV");
					data.setSessionData("S_FLAG_FDS_BU", Constants.STRING_YES);
					data.addToLog(currElementName, "selected Farmers Policy in KYC :: Overriding BU :: " + data.getSessionData(Constants.S_BU) + " :: Overriding Category :: " + data.getSessionData(Constants.S_CATEGORY));
					break;
				case Constants.S_API_BU_FDS_GWPC:
					data.addToLog(currElementName,":: GWPC Execution  :: ");
					data.setSessionData(Constants.S_BU, "Farmers");
					data.setSessionData(Constants.S_CATEGORY,"FarmersSRV");
					data.setSessionData("S_FLAG_FDS_BU", Constants.STRING_YES);
					data.addToLog(currElementName, "selected Farmers Policy in KYC :: Overriding BU :: " + data.getSessionData(Constants.S_BU) + " :: Overriding Category :: " + data.getSessionData(Constants.S_CATEGORY));
					break;
				case Constants.S_API_BU_FM:
					data.addToLog(currElementName,":: FM Execution  :: ");
					data.setSessionData(Constants.S_BU, "Foremost");
					data.setSessionData(Constants.S_CATEGORY,"FM-SLS-SRV");
					data.setSessionData("S_FLAG_FOREMOST_BU", Constants.STRING_YES);
					data.addToLog(currElementName, "selected Foremost Policy in KYC :: Overriding BU :: " + data.getSessionData(Constants.S_BU) + " :: Overriding Category :: " + data.getSessionData(Constants.S_CATEGORY));
					break;
				case Constants.S_API_BU_FWS_ARS:
					data.addToLog(currElementName,":: FWS Execution  :: ");
					data.setSessionData(Constants.S_BU, "FWS Service");
					data.setSessionData(Constants.S_CATEGORY,"FWS_SVC");
					data.setSessionData("S_FLAG_FWS_BU", Constants.STRING_YES);
					data.addToLog(currElementName, "selected FWS Policy in KYC :: Overriding BU :: " + data.getSessionData(Constants.S_BU) + " :: Overriding Category :: " + data.getSessionData(Constants.S_CATEGORY));
					break;
				case Constants.S_API_BU_FWS_A360:
					data.addToLog(currElementName,":: FWS Execution  :: ");
					data.setSessionData(Constants.S_BU, "FWS Service");
					data.setSessionData(Constants.S_CATEGORY,"FWS_SVC");
					data.setSessionData("S_FLAG_FWS_BU", Constants.STRING_YES);
					data.addToLog(currElementName, "selected FWS Policy in KYC :: Overriding BU :: " + data.getSessionData(Constants.S_BU) + " :: Overriding Category :: " + data.getSessionData(Constants.S_CATEGORY));
					break;
				case Constants.S_API_BU_21C:
					data.addToLog(currElementName,":: Auto Execution  :: ");
					data.setSessionData(Constants.S_BU, "Auto-Service");
					data.setSessionData("S_FLAG_21ST_BU", Constants.STRING_YES);
					break;
				default:
					break;
				}
				data.addToLog(currElementName, "Bristol Flag :: " + data.getSessionData("S_FLAG_BW_BU") + " :: Farmers Flag :: " + data.getSessionData("S_FLAG_FDS_BU") + " :: Foremost Flag :: " + data.getSessionData("S_FLAG_FOREMOST_BU") + " :: FWS Flag :: "  + data.getSessionData("S_FLAG_FWS_BU") + " :: 21st Flag :: " + data.getSessionData("S_FLAG_21ST_BU"));
				
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in KYCNP_MN_004_VALUE :: " + e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName, "KYCNP_MN_004_VALUE :: " + strExitState);
		return strExitState;
	}
}
