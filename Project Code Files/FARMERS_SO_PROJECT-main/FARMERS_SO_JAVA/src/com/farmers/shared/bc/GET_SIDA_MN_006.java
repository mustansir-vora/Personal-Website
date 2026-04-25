package com.farmers.shared.bc;

import java.text.SimpleDateFormat;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class GET_SIDA_MN_006 extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			
			data.setSessionData("SIDA_PA_028a", "Sorry.");
			data.setSessionData("SIDA_PA_029a", "Let's try this one more time.");
			data.setSessionData("SIDA_PA_030a", "Looks like I'm still having trouble. Let's try this another way.");
			data.setSessionData("SIDA_PA_028b", "Hmm that information did not match what we have on file. Let's try again.");
			data.setSessionData("SIDA_PA_029b", "Hmm that information did not match what we have on file. let's try this one more time.");
			data.setSessionData("SIDA_PA_030b", "Hmm that information did not match what we have on file. Looks like I'm still having trouble. Let's try this another way.");
			data.setSessionData("SIDA_PA_024", "I recognize you're asking for a representative but providing your information will allow me to better assist you.");
			
			String returnValue = (String) data.getElementData("SIDA_MN_006_Call","Return_Value");
			data.addToLog(currElementName,"GET_SIDA_MN_006 Zip code Value : "+returnValue);
			data.setSessionData(Constants.SIDA_MN_006_VALUE, returnValue);
			String strAPI_ZIP = (String) data.getSessionData(Constants.S_API_ZIP);
			data.addToLog(currElementName, "strMenuValue : "+returnValue+" :: strAPI_ZIP : "+strAPI_ZIP);
			if(returnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			} else if(returnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
			} 
			else if (returnValue.equalsIgnoreCase(Constants.REPRESENTATIVE)) {
				strExitState = Constants.REPRESENTATIVE;
				data.setSessionData("LAST_MENU_NAME_REP", "SIDA_MN_006");
			}
			else if(returnValue != null && !returnValue.equals("")) {
				if(returnValue.contains(strAPI_ZIP) || strAPI_ZIP.contains(returnValue)) {
					strExitState = Constants.VALID;
					
					JSONObject resp = new JSONObject();
					
					JSONObject SharedIDAuthresp = (JSONObject) data.getSessionData("SIDA_MN_005_VALUE_RESP");
					data.addToLog(currElementName, "Shared ID Auth Policy Lookup Resp :: "+SharedIDAuthresp);
					String policynumber = (String) data.getSessionData("S_POLICY_NUM");
					String BW_BU_Flag = (String) data.getSessionData("S_FLAG_BW_BU");
					String FDS_BU_Flag = (String) data.getSessionData("S_FLAG_FDS_BU");
					String FM_BU_Flag = (String) data.getSessionData("S_FLAG_FOREMOST_BU");
					String FWS_BU_Flag = (String) data.getSessionData("S_FLAG_FWS_BU");
					String POINT_BU_Flag = (String) data.getSessionData("S_FLAG_21ST_BU");
					
					data.addToLog(currElementName, "Bristol Flag :: " + data.getSessionData("S_FLAG_BW_BU") + " :: Farmers Flag :: " + data.getSessionData("S_FLAG_FDS_BU") + " :: Foremost Flag :: " + data.getSessionData("S_FLAG_FOREMOST_BU") + " :: FWS Flag :: "  + data.getSessionData("S_FLAG_FWS_BU") + " :: 21st Flag :: " + data.getSessionData("S_FLAG_21ST_BU"));
					
					if (null != BW_BU_Flag && BW_BU_Flag.equalsIgnoreCase(Constants.STRING_YES)) {
						resp = apiResponseManipulation_BW(currElementName, data, SharedIDAuthresp, returnValue, policynumber);
						setsessionData(currElementName, data, resp);
					}
					else if (null != FDS_BU_Flag && FDS_BU_Flag.equalsIgnoreCase(Constants.STRING_YES)) {
						resp = apiResponseManipulation_FDS(currElementName, data, SharedIDAuthresp, returnValue, policynumber);
						setsessionData(currElementName, data, resp);
					}
					else if (null != FM_BU_Flag && FM_BU_Flag.equalsIgnoreCase(Constants.STRING_YES)) {
						resp = apiResponseManipulation_FM(currElementName, data, SharedIDAuthresp, returnValue, policynumber);
						setsessionData(currElementName, data, resp);
					}
					else if (null != POINT_BU_Flag && POINT_BU_Flag.equalsIgnoreCase(Constants.STRING_YES)) {
						resp = apiResponseManipulation_POINT(currElementName, data, SharedIDAuthresp, returnValue, policynumber);
						setsessionData(currElementName, data, resp);
					}
					else if (null != FWS_BU_Flag && FWS_BU_Flag.equalsIgnoreCase(Constants.STRING_YES)) {
						resp = apiResponseManipulation_FWS(currElementName, data, SharedIDAuthresp, returnValue, policynumber);
						setsessionData(currElementName, data, resp);
					}
					
				}
				else strExitState = Constants.INVALID;
			} else strExitState = Constants.INVALID;
		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in SIDA_MN_006 :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"SIDA_MN_006 :: "+strExitState);
		data.setSessionData("SIDA_MN_006_ExitState", strExitState);
		return strExitState;
	}
	
	public JSONObject apiResponseManipulation_BW(String currElementName, DecisionElementData data, JSONObject resp, String CallerEnteredZip, String policynumber) {
		JSONObject finalObject = new JSONObject();
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		String policymod = Constants.EmptyString;
		String policysymbol = Constants.EmptyString;
		String APIpolicynumber = Constants.EmptyString;
		char firstChar = policynumber.charAt(0);
		if (Character.isAlphabetic(firstChar)) {
			policynumber = policynumber.substring(3);
		}
		if (policynumber.length() >= 9) {
			policynumber = policynumber.substring(0, 7);
		}
		try {
			
			if (resp != null) {
				
				if (resp.containsKey("household")) {
					JSONObject household = (JSONObject) resp.get("household");
					
					if (null != household && household.containsKey("autoPolicies")) {
						JSONArray autopolicies = (JSONArray) household.get("autoPolicies");
						
						if (null != autopolicies && autopolicies.size() > 0) {
							for(Object autopoliciesIterator : autopolicies) {
								JSONObject autopolicyObject = (JSONObject) autopoliciesIterator;
								
								//Get PolicyNumber
								if (autopolicyObject.containsKey("basicPolicy")) {
									JSONObject basicPolicyObject = (JSONObject) autopolicyObject.get("basicPolicy");
									
									if (null != basicPolicyObject && basicPolicyObject.containsKey("policyNumber")) {
										APIpolicynumber = (String) basicPolicyObject.get("policyNumber").toString().trim();
									}
								}
			
								//Get Postal Code & Add in Final Object
								if (null != autopolicyObject && autopolicyObject.containsKey("insuredVehicle")) {
									JSONObject insuredvehicle = (JSONObject) autopolicyObject.get("insuredVehicle");
			
									if (null != insuredvehicle && insuredvehicle.containsKey("garagingAddress")) {
										JSONObject garagingaddress = (JSONObject) insuredvehicle.get("garagingAddress");
					
										if (null != garagingaddress && garagingaddress.containsKey("postalCode")) {
											String postalcode = (String) garagingaddress.get("postalCode").toString().trim();
											if (null != postalcode) {
												postalcode= postalcode.length() > 5 ? postalcode.substring(0, 5) : postalcode;
											}
											if (postalcode.equals(CallerEnteredZip) && (APIpolicynumber.contains(policynumber) || policynumber.contains(APIpolicynumber))) {
												finalObject.put("zip", postalcode);
												
												if (autopolicyObject.containsKey("namedInsured")) {
													JSONObject namedinsuredObject = (JSONObject) autopolicyObject.get("namedInsured");
													
													if (null != namedinsuredObject && namedinsuredObject.containsKey("birthName")) {
														JSONObject birthnameObject = (JSONObject) namedinsuredObject.get("birthName");
														
														if (null != birthnameObject) {
															if (birthnameObject.containsKey("firstName")) {
																String firstname = (String) birthnameObject.get("firstName").toString().trim();
																finalObject.put(Constants.S_FIRST_NAME, firstname);
															}
															if (birthnameObject.containsKey("lastName")) {
																String lastname = (String) birthnameObject.get("lastName").toString().trim();
																finalObject.put(Constants.S_LAST_NAME, lastname);
															}
														}
													}
												}
												if (null != autopolicyObject && autopolicyObject.containsKey("basicPolicyDetail")) {
													JSONObject basicpolicydetail = (JSONObject) autopolicyObject.get("basicPolicyDetail");
													
													if (null != basicpolicydetail && basicpolicydetail.containsKey("policySymbol")) {
														policysymbol = (String) basicpolicydetail.get("policySymbol").toString().trim();
														data.setSessionData("POLICY_SYMBOL", policysymbol);
														data.addToLog(currElementName, "Setting policy Symbol into Session :: "+policysymbol);
													}
													
													if(null != basicpolicydetail && basicpolicydetail.containsKey("insurerCompanyCode")) {
														String strInsurerCompanyCode = (String) basicpolicydetail.get("insurerCompanyCode").toString().trim();
														data.setSessionData("BW_COMPANYCODE", strInsurerCompanyCode);
													}
													
												}
												if (null != autopolicyObject && autopolicyObject.containsKey("basicPolicy")) {
													JSONObject basicpolicydata = (JSONObject) autopolicyObject.get("basicPolicy");
													policymod = (String) basicpolicydata.get("policyModNumber").toString().trim();
													data.setSessionData("POLICY_MOD", policymod);
													data.setSessionData("S_BW_POLICYNUM", APIpolicynumber);
													data.addToLog(currElementName, "Setting policy mod into Session :: "+policymod);
												}
												//Set EPCPAYMENTUS ID Into session
												if (!policysymbol.isEmpty() && !APIpolicynumber.isEmpty() && !policymod.isEmpty()) {
													String epcpaymentid = policysymbol + APIpolicynumber + policymod;
													data.setSessionData(Constants.S_FULL_POLICY_NUM, policysymbol+""+APIpolicynumber);
													data.addToLog(currElementName, "S_FULL_POLICY_NUM : "+data.getSessionData(Constants.S_FULL_POLICY_NUM));
													data.setSessionData("S_EPC_PAYMENT_POLICYNUM", epcpaymentid);
													data.addToLog(currElementName, "Setting EPC PaymentUS ID into session :: "+data.getSessionData("S_EPC_PAYMENT_POLICYNUM"));
												}
											}
										}
										else {
											data.addToLog(currElementName, "Garaging Address Does not contain postal code :: "+insuredvehicle);
										}
									}
									else {
										data.addToLog(currElementName, "Insured Vehicle Object does not contain garaging address object :: "+autopolicyObject);
									}
								}
								else {
									data.addToLog(currElementName, "Selected Policy object does not contain insuredVehicle Object :: "+autopolicyObject);
								}
							}
						}
						data.addToLog(currElementName, "AutoPolicies array is null :: "+household);
					}
					else {
						data.addToLog(currElementName, "Household Object does not contain Auto policies Array :: "+resp.toString());
					}
				}
				else {
					data.addToLog(currElementName, "BW Policy lookup Response is either null or does not contain household Object :: "+resp.toString());
				}
			}
			else {
				data.addToLog(currElementName, "BW Policy lookup Response String is null :: "+resp.toString());
			}
		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in SIDA_MN_006 :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName, "Final Object with Policy data :: "+finalObject);
		return finalObject;
		
	}
	
	public JSONObject apiResponseManipulation_FDS(String currElementName, DecisionElementData data, JSONObject resp, String CallerEnteredZip, String policynumber) {
		JSONObject finalObject = new JSONObject();
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		
		try {
			
			if (resp != null) {
				
				if (resp.containsKey("policies")) {
					JSONArray policiesArr = (JSONArray) resp.get("policies");
					
					if (null != policiesArr && policiesArr.size() > 0) {
						for (Object policiesArrIterator : policiesArr) {
							JSONObject policydata = (JSONObject) policiesArrIterator;
							
							if (null != policydata) {
								
								if (policydata.containsKey("policySource")) {
									data.setSessionData(Constants.S_POLICY_SOURCE, policydata.get("policySource"));
								}
								
								if (policydata.containsKey("address")) {
									JSONArray addressArr = (JSONArray) policydata.get("address");
									
									if (null != addressArr & addressArr.size() > 0) {
										for (Object addressArrIterator : addressArr) {
											JSONObject addressObject = (JSONObject) addressArrIterator;
											
											if (null != addressObject && addressObject.containsKey("zip")) {
												String zip = (String) addressObject.get("zip").toString().trim();
												
												if (null != zip) {
													zip = zip.length() > 5 ? zip.substring(0, 5) : zip;
												}
												
												if (zip.equalsIgnoreCase(CallerEnteredZip)) {
													
													if (policydata.containsKey("billingAccountNumber") && null != policydata.get("billingAccountNumber")) {
														data.setSessionData("S_EPC_PAYMENT_POLICYNUM", policydata.get("billingAccountNumber").toString().trim());
														data.addToLog(currElementName, "Setting EPC PaymentUS ID into session :: "+data.getSessionData("S_EPC_PAYMENT_POLICYNUM"));
													}
													
													finalObject.put("zip", zip);
													if (addressObject.containsKey("city")) {
														String city = (String) addressObject.get("city").toString().trim();
														finalObject.put("city", city);
													}
													if (addressObject.containsKey("state")) {
														String state = (String) addressObject.get("state").toString().trim();
														finalObject.put("state", state);
													}
													if (addressObject.containsKey("streetAddress1")) {
														String address = (String) addressObject.get("streetAddress1").toString().trim();
														finalObject.put("address", address);
													}
													if (addressObject.containsKey("country")) {
														String country = (String) addressObject.get("country").toString().trim();
														finalObject.put("country", country);
													}
													
													if (policydata.containsKey("insuredDetails")) {
														JSONArray insuredDetailsArr = (JSONArray) policydata.get("insuredDetails");
														
														if (null != insuredDetailsArr && insuredDetailsArr.size() > 0) {
															for (Object insuredArrIterator : insuredDetailsArr) {
																JSONObject insuredDetailsObject = (JSONObject) insuredArrIterator;
																
																if (null != insuredDetailsObject) {
																	
																	if (insuredDetailsObject.containsKey("basicElectronicAddress")) {
																		String email = (String) insuredDetailsObject.get("basicElectronicAddress");
																		data.setSessionData(Constants.S_EMAIL, email);
																		data.setSessionData(Constants.S_IS_POLICY_EMAIL_AVIALABLE, Constants.STRING_YES);
																		data.addToLog(currElementName, "EMAIL AVAILABLE :: YES :: EMAIL SET INTO SESSION :: " +email);
																	}
																	
																	if (insuredDetailsObject.containsKey("firstName")) {
																		String firstname = (String) insuredDetailsObject.get("firstName").toString().trim();
																		finalObject.put("firstname", firstname);
																	}
																	if (insuredDetailsObject.containsKey("lastName")) {
																		String lastname = (String) insuredDetailsObject.get("lastName").toString().trim();
																		finalObject.put("lastname", lastname);
																	}
																}
																else {
																	data.addToLog(currElementName, "insured Details Object is null :: "+insuredDetailsArr);
																}
															}
														}
														else {
															data.addToLog(currElementName, "insured Details Array is null :: "+insuredDetailsArr);
														}
													}
												}
											}
										}
									}
									else {
										data.addToLog(currElementName, "Address Array is null :: "+policydata);
									}
								}
							}
						}
					}
					else {
						data.addToLog(currElementName, "Policies Array is null or empty :: "+resp.toString());
					}
				}
				else {
					data.addToLog(currElementName, "Farmers Policy Lookup Response JSON is either null or empty :: "+resp.toString());
				}
			}
			else{
				data.addToLog(currElementName, "Farmers Policy Lookup Response String is either null or empty :: "+resp.toString());
			}
		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in SIDA_MN_006 :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName, "Final Object with Policy data :: "+finalObject);
		return finalObject;
	}
	
	public JSONObject apiResponseManipulation_FM(String currElementName, DecisionElementData data, JSONObject resp, String CallerEnteredZip, String policynumber) {
		JSONObject finalObject = new JSONObject();
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		String productcode = null;
		String APIpolicynumber = null;
		
		try {
			
			if (resp != null) {
				
				if (resp.containsKey("policies")) {
					JSONArray policiesArr = (JSONArray) resp.get("policies");
					
					if (null != policiesArr && policiesArr.size() > 0) {
						for (Object policiesArrIterator : policiesArr) {
							JSONObject policydata = (JSONObject) policiesArrIterator;
							
							if (null != policydata) {
								
								if (policydata.containsKey("addresses")) {
									JSONArray addressArr = (JSONArray) policydata.get("addresses");
									
									if (null != addressArr & addressArr.size() > 0) {
										for (Object addressArrIterator : addressArr) {
											JSONObject addressObject = (JSONObject) addressArrIterator;
											
											if (null != addressObject && addressObject.containsKey("zip")) {
												String zip = (String) addressObject.get("zip").toString().trim();
												
												if (null != zip) {
													zip = zip.length() > 5 ? zip.substring(0, 5) : zip;
												}
												if (zip.equalsIgnoreCase(CallerEnteredZip)) {
													
													if (null != policydata && policydata.containsKey("policyNumber")) {
														APIpolicynumber = (String) policydata.get("policyNumber").toString().trim();
													}
													if (null != policydata && policydata.containsKey("policyProductCode")) {
														productcode = (String) policydata.get("policyProductCode").toString().trim();
														data.addToLog(currElementName, "Foremost Product Code Received from API :: " + productcode);
														if (null != productcode && productcode.length() == 2) {
															productcode = "0"+productcode;
															data.addToLog(currElementName, "Foremost Product Code is 2 digits :: Adding zero as Prefix :: Product code Post manipulation :: " + productcode);
														}
														data.setSessionData("POLICY_PRODUCT_CODE", productcode);
														data.addToLog(currElementName, "Setting Foremost Product Code Into Session Post Manipulation :: " + data.getSessionData("POLICY_PRODUCT_CODE"));
													}
													
													//Set EPC PAYMENTUS ID
													if (null != policynumber && null != productcode ) {
															data.setSessionData("S_EPC_PAYMENT_POLICYNUM", productcode+APIpolicynumber);
															data.addToLog(currElementName, "Setting EPC PaymentUS ID into session :: "+data.getSessionData("S_EPC_PAYMENT_POLICYNUM"));
													}
													
													finalObject.put("zip", zip);
													if (addressObject.containsKey("city")) {
														String city = (String) addressObject.get("city").toString().trim();
														finalObject.put("city", city);
													}
													if (addressObject.containsKey("state")) {
														String state = (String) addressObject.get("state").toString().trim();
														finalObject.put("state", state);
														data.setSessionData(Constants.S_POLICY_STATE_CODE, state);
													}
													if (addressObject.containsKey("streetAddress1")) {
														String address = (String) addressObject.get("streetAddress1").toString().trim();
														finalObject.put("address", address);
													}
													if (addressObject.containsKey("country")) {
														String country = (String) addressObject.get("country").toString().trim();
														finalObject.put("country", country);
													}
													
													if (policydata.containsKey("insureds")) {
														JSONArray insuredDetailsArr = (JSONArray) policydata.get("insureds");
														
														if (null != insuredDetailsArr && insuredDetailsArr.size() > 0) {
															for (Object insuredArrIterator : insuredDetailsArr) {
																JSONObject insuredDetailsObject = (JSONObject) insuredArrIterator;
																
																if (null != insuredDetailsObject && insuredDetailsObject.containsKey("name")) {
																	JSONObject name = (JSONObject) insuredDetailsObject.get("name");
																	
																	if (name.containsKey("firstName")) {
																		String firstname = (String) name.get("firstName").toString().trim();
																		finalObject.put("firstname", firstname);
																	}
																	if (name.containsKey("lastName")) {
																		String lastname = (String) name.get("lastName").toString().trim();
																		finalObject.put("lastname", lastname);
																	}
																}
																else {
																	data.addToLog(currElementName, "insured Details Object is null :: "+insuredDetailsArr);
																}
															}
														}
														else {
															data.addToLog(currElementName, "insured Details Array is null :: "+insuredDetailsArr);
														}
													}
													else {
														data.addToLog(currElementName, "Policy Object does not contain insureds Array :: "+policydata);
													}
												}
											}
										}
									}
								}
							}
							else {
								data.addToLog(currElementName, "Addresses Array not available inside Policy Object :: is null or empty :: "+resp.toString());
							}
						}
					}
					else{
						data.addToLog(currElementName, "Policies Array is null or empty :: "+resp.toString());
					}
				}
				else {
					data.addToLog(currElementName, "Foremost Policy Lookup Response JSON is either null or empty :: "+resp.toString());
				}
			}
			else {
				data.addToLog(currElementName, "Foremost Policy Lookup Response String is either null or empty :: "+resp.toString());
			}
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in SIDA_MN_006 :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName, "Final Object with Policy data :: "+finalObject);
		return finalObject;
	}
	
	public JSONObject apiResponseManipulation_POINT(String currElementName, DecisionElementData data, JSONObject resp, String CallerEnteredZip, String policynumber) {
		JSONObject finalObject = new JSONObject();
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		
		try {
			
			if (resp != null) {
				
				if (resp.containsKey("policySummary")) {
					JSONObject policydata = (JSONObject) resp.get("policySummary");
					
					if (null != policydata) {
						
						if (policydata.containsKey("address")) {
							JSONArray addressArr = (JSONArray) policydata.get("address");
							
							if (null != addressArr & addressArr.size() > 0) {
								for (Object addressArrIterator : addressArr) {
									JSONObject addressObject = (JSONObject) addressArrIterator;
									
									if (null != addressObject && addressObject.containsKey("zip")) {
										String zip = (String) addressObject.get("zip").toString().trim();
										
										if (null != zip) {
											zip = zip.length() > 5 ? zip.substring(0, 5) : zip;
										}
										
										if (zip.equalsIgnoreCase(CallerEnteredZip)) {
											finalObject.put("zip", zip);
											if (addressObject.containsKey("city")) {
												String city = (String) addressObject.get("city").toString().trim();
												finalObject.put("city", city);
											}
											if (addressObject.containsKey("state")) {
												String state = (String) addressObject.get("state").toString().trim();
												finalObject.put("state", state);
											}
											if (addressObject.containsKey("streetAddress1")) {
												String address = (String) addressObject.get("streetAddress1").toString().trim();
												finalObject.put("address", address);
											}
											if (addressObject.containsKey("country")) {
												String country = (String) addressObject.get("country").toString().trim();
												finalObject.put("country", country);
											}
											
											if (policydata.containsKey("autoPolicy")) {
												JSONObject autopoliciesObject = (JSONObject) policydata.get("autoPolicy");
												
												if (null != autopoliciesObject && autopoliciesObject.containsKey("drivers")) {
													JSONArray drivers = (JSONArray) autopoliciesObject.get("drivers");
													
													for (Object driversArrIterator : drivers) {
														JSONObject driverObject = (JSONObject) driversArrIterator;
														
														if (null != driverObject && driverObject.containsKey("electronicMailAddress")) {
															String email = (String) driverObject.get("electronicMailAddress").toString().trim();
															data.setSessionData(Constants.S_EMAIL, email);
															data.setSessionData(Constants.S_IS_POLICY_EMAIL_AVIALABLE, Constants.STRING_YES);
															data.addToLog(currElementName, "EMAIL AVAILABLE :: YES :: EMAIL SET INTO SESSION :: " +email);
														}
														
														if (null != driverObject && driverObject.containsKey("person")) {
															JSONObject person = (JSONObject) driverObject.get("person");
															
															if (null != person) {
																if (person.containsKey("firstName")) {
																	String firstname = (String) person.get("firstName").toString().trim();
																	finalObject.put("firstname", firstname);
																}
																if (person.containsKey("lastName")) {
																	String lastname = (String) person.get("lastName").toString().trim();
																	finalObject.put("lastname", lastname);
																}
															}
															else {
																data.addToLog(currElementName, "Person Object is null :: "+drivers);
															}
														}
													}
												}
											}
											else {
												data.addToLog(currElementName, "Policy Object does not contain Auto Policies Object :: "+policydata);
											}
										}
									}
								}
							}
							else {
								data.addToLog(currElementName, "Address Array is either null or empty");
							}
						}
						else{
							data.addToLog(currElementName, "Policy Object does not contain Address Array");
						}
					}
					else {
						data.addToLog(currElementName, "Policy Object is null :: "+resp.toString());
					}
				}
				else {
					data.addToLog(currElementName, "21C Policy Lookup Response JSON is either null or empty :: "+resp.toString());
				}
			}
			else{
				data.addToLog(currElementName, "21C Policy Lookup Response String is either null or empty :: "+resp.toString());
			}
			
		}
		catch(Exception e) {
			data.addToLog(currElementName,"Exception in SIDA_MN_006 :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName, "Final Object with Policy data :: "+finalObject);
		return finalObject;
	}
	
	public JSONObject apiResponseManipulation_FWS(String currElementName, DecisionElementData data, JSONObject resp, String CallerEnteredZip, String policynumber) {
		JSONObject finalObject = new JSONObject();
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		JSONArray addressarr = null;
		String API_ZIP = null;
		JSONArray insuredDetailsArr = null;
		JSONObject insureddetailsObj = null;
		String Selected_LOB = (String) data.getSessionData("S_LOB");
		String API_LOB = null;
		String billingaccountnumber;
		String policysource = null;
		
		try {
			if (resp != null) {
				data.addToLog(currElementName, "FWS Policy Lookup resp :: "+resp);
				if (resp.containsKey("policies")) {
					JSONArray policiesArr = (JSONArray) resp.get("policies");
					
					for(Object policyobj : policiesArr) {
						JSONObject policydata = (JSONObject) policyobj;
						
						if (null != policydata) {
							
							if (policydata.containsKey("lineOfBusiness")) {
								API_LOB = (String) policydata.get("lineOfBusiness").toString().trim();
							}
							
							if (policydata.containsKey("addresses")) {
								addressarr = (JSONArray) policydata.get("addresses");
								
								for (Object adressesiterator : addressarr) {
									JSONObject addressObj = (JSONObject) adressesiterator;
									
									if (null != addressObj && addressObj.containsKey("zip")) {
										API_ZIP = (String) addressObj.get("zip").toString().trim();
										API_ZIP = API_ZIP.length() > 5 ? API_ZIP.substring(0, 5) : API_ZIP;
										data.addToLog(currElementName, "Zip Received from API :: "+API_ZIP);
										data.addToLog(currElementName, "Caller Entered Zip :: "+CallerEnteredZip);
										
										if (null != CallerEnteredZip && null != API_ZIP && null != Selected_LOB && null != API_LOB && (CallerEnteredZip.contains(API_ZIP) || API_ZIP.contains(CallerEnteredZip)) && API_LOB.equalsIgnoreCase(Selected_LOB)) {
											
											if (policydata.containsKey("insuredDetails")) {
												insuredDetailsArr = (JSONArray) policydata.get("insuredDetails");
												insureddetailsObj = (JSONObject) insuredDetailsArr.get(0);
												String firstname = (String) insureddetailsObj.get("firstName").toString().trim();
												finalObject.put("firstname", firstname);
												String lastname = (String) insureddetailsObj.get("lastName").toString().trim();
												finalObject.put("lastname", lastname);
											}
												finalObject.put("zip", API_ZIP);
												
												if (policydata.containsKey("policySource")) {
													policysource = (String) policydata.get("policySource").toString().trim();
												}
												
												if (policydata.containsKey("billingAccountNumber")) {
													billingaccountnumber = (String) policydata.get("billingAccountNumber").toString().trim();
													data.setSessionData("S_EPC_PAYMENT_POLICYNUM", billingaccountnumber);
													
													data.setSessionData(Constants.S_FWS_POLICY_BILLING_ACCT_NO, billingaccountnumber);
													data.addToLog(currElementName, "S_FWS_POLICY_BILLING_ACCT_NO :: "+data.getSessionData(Constants.S_FWS_POLICY_BILLING_ACCT_NO));
												}
												
												if (policydata.containsKey("policyNumber") && policydata.containsKey("lineOfBusiness")) {
													data.setSessionData("S_FWS_EPC_POLICY_NUM", (String) policydata.get("lineOfBusiness").toString().trim() +(String) policydata.get("policyNumber").toString().trim());
													data.addToLog(currElementName, "Setting FWS Policy Number for EPC into session :: "+data.getSessionData("S_FWS_EPC_POLICY_NUM"));
												}	
												if (policydata.containsKey("policyNumber")) {
													data.setSessionData("S_POLICY_NUM", (String) policydata.get("policyNumber").toString().trim());
													data.addToLog(currElementName, "Setting Policy Number into session :: "+policydata.get("policyNumber"));
												}
												if (policydata.containsKey("lineOfBusiness")) {
													data.setSessionData("S_FWS_POLICY_LOB", (String) policydata.get("lineOfBusiness").toString().trim());
													data.addToLog(currElementName, "Setting Policy LOB into session :: "+policydata.get("lineOfBusiness"));
												}
												if (policydata.containsKey("effectiveDate")) {
													String effectiveDate = (String) policydata.get("effectiveDate");
													effectiveDate = null != effectiveDate && effectiveDate.trim().length() > 10 ? effectiveDate.trim().substring(0, 10) : effectiveDate.trim();
													data.setSessionData("S_FWS_POLICY_EFF_DATE", effectiveDate);
													data.addToLog(currElementName, "Setting Policy Effective into session :: "+effectiveDate);
												}
												if (policydata.containsKey("renewalEffectiveDate")) {
													String renEffectiveDate = (String) policydata.get("effectiveDate");
													renEffectiveDate = null != renEffectiveDate && renEffectiveDate.trim().length() > 10 ? renEffectiveDate.trim().substring(0, 10) : renEffectiveDate.trim();
													data.setSessionData("S_FWS_POLICY_REN_EFF_DATE", renEffectiveDate);
													data.addToLog(currElementName, "Setting Policy Renewal Effective Date into session :: "+renEffectiveDate);
												}
												if (policydata.containsKey("suffix")) {
													data.setSessionData("S_FWS_POLICY_SUFFIX", (String) policydata.get("suffix").toString().trim());
													data.addToLog(currElementName, "Setting Policy Suffix into session :: "+policydata.get("suffix"));
												}
												if (policydata.containsKey("policyState")) {
													data.setSessionData("S_POLICY_STATE", (String) policydata.get("policyState").toString().trim());
													finalObject.put("state", (String) policydata.get("policyState"));
													data.addToLog(currElementName, "Setting Policy State into session :: "+policydata.get("policyState"));
												}
												if (policydata.containsKey("policySource")) {
													data.setSessionData("S_POLICY_SOURCE", (String) policydata.get("policySource").toString().trim());
													data.addToLog(currElementName, "Setting Policy Source into session :: "+policydata.get("policySource"));
												}
												if (policydata.containsKey("internalPolicyNum")) {
													data.setSessionData("S_FWS_INT_POLICY_NO", (String) policydata.get("internalPolicyNum").toString().trim());
													data.addToLog(currElementName, "Setting Internal Policy Number into session :: "+policydata.get("internalPolicyNum"));
												}
												if (policydata.containsKey("internalPolicyVersion")) {
													data.setSessionData("S_FWS_INTERNAL_POLICY_VERSION", (String) policydata.get("internalPolicyVersion").toString().trim());
													data.addToLog(currElementName, "Setting Internal Policy Version into session :: "+policydata.get("internalPolicyVersion"));
												}
												if (policydata.containsKey("GPC")) {
													data.setSessionData("S_FWS_GPC", (String) policydata.get("GPC").toString().trim());
													data.addToLog(currElementName, "Setting Policy GPC into session :: "+policydata.get("GPC"));
												}
												if (policydata.containsKey("comboPackageIndicator")) {
													data.setSessionData("S_FWS_COMBO_PACKAGE_INDICATOR", (String) policydata.get("comboPackageIndicator").toString().trim());
													data.addToLog(currElementName, "Setting Combo Package Indicator into session :: "+policydata.get("comboPackageIndicator"));
												}
												if (policydata.containsKey("producerRoleCode")) {
													data.setSessionData("S_FWS_PRODUCER_ROLE_CODE", (String) policydata.get("producerRoleCode").toString().trim());
													data.addToLog(currElementName, "Setting Producer Role Code into session :: "+policydata.get("producerRoleCode"));
												}
												if (policydata.containsKey("companyProductCode")) {
													data.setSessionData("S_FWS_COMPANY_PRODUCT_CODE", (String) policydata.get("companyProductCode").toString().trim());
													data.addToLog(currElementName, "Setting Company Product Code into session :: "+policydata.get("companyProductCode"));
												}
												if (policydata.containsKey("callRoutingIndicator")) {
													data.setSessionData("S_FWS_CALL_ROUTING_INDICATOR", (String) policydata.get("callRoutingIndicator"));
													data.addToLog(currElementName, "Setting Call Routing Indicator Code into session :: "+policydata.get("callRoutingIndicator"));
												}
												if (policydata.containsKey("serviceLevels")) {
													data.setSessionData("S_FWS_SERVICE_LEVEL", (String) policydata.get("serviceLevels").toString().trim());
													data.addToLog(currElementName, "Setting ServiceLevel into session :: "+policydata.get("serviceLevels"));
												}
											}
										}
									}
								}
								else {
									data.addToLog(currElementName, "Zip code not received from API :: "+policydata);
								}
							}
						}
					}
				}
				else{
					data.addToLog(currElementName, "FWS Resp is null :: "+resp.toString());
				}
			}
			catch (Exception e) {
				data.addToLog(currElementName,"Exception in SIDA_MN_005 :: "+e);
				caa.printStackTrace(e);
			}
			data.addToLog(currElementName, "Final Object with Policy data :: "+finalObject);
			return finalObject;
		}
	
	public void setsessionData(String currElementName, DecisionElementData data, JSONObject finalPolicyObject) {
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			if (null != finalPolicyObject) {
				if (finalPolicyObject.containsKey("firstname")) {
					data.setSessionData(Constants.S_FIRST_NAME, finalPolicyObject.get("firstname"));
					data.addToLog(currElementName, "Setting FirstName into session from Lookup API :: "+finalPolicyObject.get("firstname"));
				}
				if (finalPolicyObject.containsKey("lastname")) {
					data.setSessionData(Constants.S_LAST_NAME, finalPolicyObject.get("lastname"));
					data.addToLog(currElementName, "Setting LaststName into session from Lookup API :: "+finalPolicyObject.get("lastname"));
				}
				if (finalPolicyObject.containsKey("email")) {
					data.setSessionData(Constants.S_EMAIL, finalPolicyObject.get("email"));
					data.addToLog(currElementName, "Setting Email into session from Lookup API :: "+finalPolicyObject.get("email"));
				}
				if (finalPolicyObject.containsKey("city")) {
					data.setSessionData(Constants.S_CITY, finalPolicyObject.get("city"));
					data.addToLog(currElementName, "Setting City into session from Lookup API :: "+finalPolicyObject.get("city"));
				}
				if (finalPolicyObject.containsKey("state")) {
					data.setSessionData(Constants.S_STATE, finalPolicyObject.get("state"));
					data.addToLog(currElementName, "Setting State into session from Lookup API :: "+finalPolicyObject.get("state"));
				}
				if (finalPolicyObject.containsKey("zip")) {
					data.setSessionData(Constants.S_PAYOR_ZIP_CODE, finalPolicyObject.get("zip"));
					data.addToLog(currElementName, "Setting Zip into session from Lookup API :: "+finalPolicyObject.get("zip"));
				}
				if (finalPolicyObject.containsKey("country")) {
					data.setSessionData(Constants.S_COUNTRY, finalPolicyObject.get("country"));
					data.addToLog(currElementName, "Setting Country into session from Lookup API :: "+finalPolicyObject.get("country"));
				}
				if (finalPolicyObject.containsKey("address")) {
					data.setSessionData(Constants.S_LINE1, finalPolicyObject.get("address"));
					data.addToLog(currElementName, "Setting Address Line 1 into session from Lookup API :: "+finalPolicyObject.get("address"));
				}
			}
		}
		catch (Exception e) {
			data.addToLog(currElementName,"Exception in SIDA_MN_005 :: "+e);
			caa.printStackTrace(e);
		}
	}
	
}