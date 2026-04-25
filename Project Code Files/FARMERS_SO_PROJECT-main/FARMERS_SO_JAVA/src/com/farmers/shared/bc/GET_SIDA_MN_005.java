package com.farmers.shared.bc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;

import javax.xml.crypto.Data;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class GET_SIDA_MN_005 extends DecisionElementBase  {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		try {
			
			data.setSessionData("SIDA_PA_025a", "Sorry.");
			data.setSessionData("SIDA_PA_026a", "Let's try this one more time.");
			data.setSessionData("SIDA_PA_027a", "Looks like I'm still having trouble. Let's try this another way.");
			data.setSessionData("SIDA_PA_025b", "Hmm that information did not match what we have on file. Let's try again.");
			data.setSessionData("SIDA_PA_026b", "Hmm that information did not match what we have on file. let's try this one more time.");
			data.setSessionData("SIDA_PA_027b", "Hmm that information did not match what we have on file. Looks like I'm still having trouble. Let's try this another way.");
			
			String returnValue = (String) data.getElementData("SIDA_MN_005_Call","Return_Value");
			data.addToLog(currElementName, "Menu ID : "+"SIDA_MN_005"+" :: Menu Value : "+returnValue);
			data.setSessionData(Constants.SIDA_MN_005_VALUE, returnValue);
			String strAPI_DOB = (String) data.getSessionData(Constants.S_API_DOB);
			String finalDOB = "";
			if(strAPI_DOB!=null) {
				String[] arrAPIDOB = strAPI_DOB.split(",");
				for (String temp : arrAPIDOB) {
					data.addToLog(currElementName, "strMenuValue : "+returnValue+" :: strAPI_DOB : "+arrAPIDOB);
					if(temp.contains("/")) {
						temp = temp.replaceAll("/", "-");
						SimpleDateFormat inputSDF = new SimpleDateFormat("MM-dd-yyyy");
						SimpleDateFormat resultSDF = new SimpleDateFormat("MM-dd-yyyy");
						temp = resultSDF.format(inputSDF.parse(temp));
					} else if(temp.contains("-")) {
						SimpleDateFormat inputSDF = new SimpleDateFormat("yyyy-MM-dd");
						SimpleDateFormat resultSDF = new SimpleDateFormat("MM-dd-yyyy");
						temp = resultSDF.format(inputSDF.parse(temp));
					}
					finalDOB = finalDOB +","+temp;
					data.addToLog(currElementName, "Final Appended Date String After Format conversion :: "+finalDOB);
				}
			}
			data.addToLog(currElementName, "strMenuValue : "+returnValue+" :: strAPI_DOB post conversion: "+finalDOB);
			if(returnValue.equalsIgnoreCase(Constants.NOINPUT)) {
				strExitState = Constants.NOINPUT;
			} else if(returnValue.equalsIgnoreCase(Constants.NOMATCH)) {
				strExitState = Constants.NOMATCH;
			} 
			else if (returnValue.equalsIgnoreCase(Constants.REPRESENTATIVE)) {
				strExitState = Constants.REPRESENTATIVE;
				data.setSessionData("LAST_MENU_NAME_REP", "SIDA_MN_005");
			}
			else if(returnValue != null && !returnValue.equals("")) {
				if(returnValue.equalsIgnoreCase("I Dont Know")) {
					strExitState = "I Dont Know";
				}else {
					String strMenuValue = returnValue.replaceAll("-", "");
					finalDOB = finalDOB.replaceAll("-", "");
					if(strMenuValue.equalsIgnoreCase(finalDOB)||finalDOB.contains(strMenuValue)) {
						strExitState = Constants.VALID;
						
						data.addToLog(currElementName, "DOB Validation Successful :: API DOB :: "+finalDOB+" :: Caller Entered DOB :: "+strMenuValue);
						data.addToLog(currElementName, "Fetching Required Data For EPCPaymentUS & setting into session");
						JSONObject resp = new JSONObject();
						JSONObject SharedIDAuthresp = (JSONObject) data.getSessionData("SIDA_MN_005_VALUE_RESP");
						data.addToLog(currElementName, "Shared ID Auth Policy Lookup Resp :: "+SharedIDAuthresp);
						
						String BW_BU_Flag = (String) data.getSessionData("S_FLAG_BW_BU");
						String FDS_BU_Flag = (String) data.getSessionData("S_FLAG_FDS_BU");
						String FM_BU_Flag = (String) data.getSessionData("S_FLAG_FOREMOST_BU");
						String FWS_BU_Flag = (String) data.getSessionData("S_FLAG_FWS_BU");
						String POINT_BU_Flag = (String) data.getSessionData("S_FLAG_21ST_BU");
						
						data.addToLog(currElementName, "Bristol Flag :: " + data.getSessionData("S_FLAG_BW_BU") + " :: Farmers Flag :: " + data.getSessionData("S_FLAG_FDS_BU") + " :: Foremost Flag :: " + data.getSessionData("S_FLAG_FOREMOST_BU") + " :: FWS Flag :: "  + data.getSessionData("S_FLAG_FWS_BU") + " :: 21st Flag :: " + data.getSessionData("S_FLAG_21ST_BU"));
						
						if (null != BW_BU_Flag && BW_BU_Flag.equalsIgnoreCase(Constants.STRING_YES)) {
							resp = apiResponseManipulation_BW(currElementName, data, SharedIDAuthresp, returnValue);
							setsessionData(currElementName, data, resp);
						}
						else if (null != FDS_BU_Flag && FDS_BU_Flag.equalsIgnoreCase(Constants.STRING_YES)) {
							resp = apiResponseManipulation_FDS(currElementName, data, SharedIDAuthresp, returnValue);
							setsessionData(currElementName, data, resp);
						}
						else if (null != FM_BU_Flag && FM_BU_Flag.equalsIgnoreCase(Constants.STRING_YES)) {
							resp = apiResponseManipulation_FM(currElementName, data, SharedIDAuthresp, returnValue);
							setsessionData(currElementName, data, resp);
						}
						else if (null != POINT_BU_Flag && POINT_BU_Flag.equalsIgnoreCase(Constants.STRING_YES)) {
							resp = apiResponseManipulation_POINT(currElementName, data, SharedIDAuthresp, returnValue);
							setsessionData(currElementName, data, resp);
						}
						else if (null != FWS_BU_Flag && FWS_BU_Flag.equalsIgnoreCase(Constants.STRING_YES)) {
							resp = apiResponseManipulation_FWS(currElementName, data, SharedIDAuthresp, returnValue);
							setsessionData(currElementName, data, resp);
						}
					}
					else {
						strExitState = Constants.INVALID;
					}
				}
			} else strExitState = Constants.INVALID;
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in SIDA_MN_005 :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName,"SIDA_MN_005 :: "+strExitState);
		data.setSessionData("SIDA_MN_005_ExitState", strExitState);
		return strExitState;
	}
	
	
	public JSONObject apiResponseManipulation_BW(String currElementName, DecisionElementData data, JSONObject resp, String strCallerEnteredDOB) {
		JSONObject finalObject = new JSONObject();
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		String policysymbol = Constants.EmptyString;
		String policymod = Constants.EmptyString;
		String policynumber = Constants.EmptyString;
		String API_DOB = Constants.EmptyString;
		try {
			if (resp != null) {
				
				if (resp.containsKey("household")) {
					JSONObject household = (JSONObject) resp.get("household");
					
					if (null != household && household.containsKey("autoPolicies")) {
						JSONArray autopolicies = (JSONArray) household.get("autoPolicies");
						
						if (null != autopolicies && autopolicies.size() > 0) {
							for(Object autopoliciesIterator : autopolicies) {
								JSONObject autopolicyObject = (JSONObject) autopoliciesIterator;
								
								if (null != autopolicyObject && autopolicyObject.containsKey("namedInsured")) {
									JSONObject namedInsuredObject = (JSONObject) autopolicyObject.get("namedInsured");
										
										//Get First Name, Last Name & Add in Final Object
										if (namedInsuredObject.containsKey("birthName")) {
											JSONObject birthnameObject = (JSONObject) namedInsuredObject.get("birthName");
											
											if (null != strCallerEnteredDOB && null != namedInsuredObject.get("birthDate")) {
												API_DOB = (String) namedInsuredObject.get("birthDate").toString().trim();
												SimpleDateFormat inputSDF = new SimpleDateFormat("yyyy-MM-dd");
												SimpleDateFormat resultSDF = new SimpleDateFormat("MM-dd-yyyy");
												API_DOB = resultSDF.format(inputSDF.parse(API_DOB));
												data.addToLog(currElementName, "API DOB Post Conversion :: "+API_DOB);
												
												if (null != API_DOB && null != strCallerEnteredDOB && (API_DOB.contains(strCallerEnteredDOB) || strCallerEnteredDOB.contains(API_DOB))) {
													data.addToLog(currElementName, "Found Selected Policy Object :: Retrieving values");
													if (null != birthnameObject) {
														if (birthnameObject.containsKey("firstName")) {
															String firstname = (String) birthnameObject.get("firstName").toString().trim();
															finalObject.put("firstname", firstname);
														}
														if (birthnameObject.containsKey("lastName")) {
															String lastname = (String) birthnameObject.get("lastName").toString().trim();
															finalObject.put("lastname", lastname);
														}
													}
													else {
														data.addToLog(currElementName, "BirthName Object is null :: "+namedInsuredObject);
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
														policynumber = (String) basicpolicydata.get("policyNumber").toString().trim();
														policymod = (String) basicpolicydata.get("policyModNumber").toString().trim();
														data.setSessionData("POLICY_MOD", policymod);
														data.setSessionData("S_BW_POLICYNUM", policynumber);
														data.addToLog(currElementName, "Setting policy mod into Session :: "+policymod);
													}
													data.setSessionData(Constants.S_FULL_POLICY_NUM, policysymbol+""+policynumber);
													data.addToLog(currElementName, "S_FULL_POLICY_NUM : "+data.getSessionData(Constants.S_FULL_POLICY_NUM));
													String epcpaymentid = policysymbol + policynumber + policymod;
													data.setSessionData("S_EPC_PAYMENT_POLICYNUM", epcpaymentid);
													data.addToLog(currElementName, "Setting EPC PaymentUS ID into session :: "+data.getSessionData("S_EPC_PAYMENT_POLICYNUM"));
													
													
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
																finalObject.put("zip", postalcode);
																
																//Caller Verification Project
																
																data.setSessionData(Constants.S_ZIP_CODE, postalcode);
															}
															else {
																data.addToLog(currElementName, "Garaging Address Does not contain postal code");
															}
														}
														else {
															data.addToLog(currElementName, "Insured Vehicle Object does not contain garaging address object");
														}
													}
													else {
														data.addToLog(currElementName, "Selected Policy object does not contain insuredVehicle Object :: "+autopolicyObject);
													}
													//Postal Code End
													
												}
												else {
													data.addToLog(currElementName, "namedInsured Object does not contain birthName Object :: Cannot get First name & Last name to set into session");
												}
												//First Name & Last Name End
											}
										}
								}
								else {
									data.addToLog(currElementName, "AutoPolicy Object does not contain namedInsured Object :: "+autopoliciesIterator);
								}
							}
						}
						else {
							data.addToLog(currElementName, "Autopolicies Array is either null or empty :: "+household);
						}
					}
					else {
						data.addToLog(currElementName, "HouseHold Object does not contain autopolicies object :: "+resp.toString());
					}
				}
				else {
					data.addToLog(currElementName, "BW Response fetched from Shared ID Auth does not contain household object :: "+resp.toString());
				}
			}
			else {
				data.addToLog(currElementName, "BW Response String is either null or empty :: "+resp.toString());
			}
		}
		catch(Exception e) {
			data.addToLog(currElementName,"Exception in SIDA_MN_005 :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName, "Final Object with Policy data :: "+finalObject);
		return finalObject;
	}
	
	public JSONObject apiResponseManipulation_FDS(String currElementName, DecisionElementData data, JSONObject resp, String strCallerEnteredDOB) {
		JSONObject finalObject = new JSONObject();
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		String email = null;
		String overrideEmail = null;
		
		try {
			if (resp != null) {
				
				if (resp.containsKey("policies")) {
					JSONArray policiesArr = (JSONArray) resp.get("policies");
					
					for (Object policyArrayIterator : policiesArr) {
						JSONObject policyObject = (JSONObject) policyArrayIterator;
						
						if (null != policyObject) {
							
							if (policyObject.containsKey("policySource")) {
								data.setSessionData(Constants.S_POLICY_SOURCE, policyObject.get("policySource"));
							}
							
							//Get First name & last name & email
							if (policyObject.containsKey("insuredDetails")) {
								JSONArray insuredDetailArray = (JSONArray) policyObject.get("insuredDetails");
								
								for (Object insuredDetailsArrayIterator : insuredDetailArray) {
									JSONObject insuredDetailsObject = (JSONObject) insuredDetailsArrayIterator;
									
									if (null != insuredDetailsObject && insuredDetailsObject.containsKey("basicElectronicAddress")) {
										email = (String) insuredDetailsObject.get("basicElectronicAddress");
									}
									
									if (null != email && !email.isEmpty() && null == overrideEmail) {
										data.setSessionData(Constants.S_EMAIL, email);
										data.setSessionData(Constants.S_IS_POLICY_EMAIL_AVIALABLE, Constants.STRING_YES);
										data.addToLog(currElementName, "EMAIL_AVAILABLE :: YES :: EMAIL SET INTO SESSION :: " +email);
									}
									String API_DOB = (String) insuredDetailsObject.get("birthDate").toString().trim();
									SimpleDateFormat inputSDF = new SimpleDateFormat("yyyy-MM-dd");
									SimpleDateFormat resultSDF = new SimpleDateFormat("MM-dd-yyyy");
									API_DOB = resultSDF.format(inputSDF.parse(API_DOB));
									if (null != API_DOB && null != strCallerEnteredDOB && (API_DOB.contains(strCallerEnteredDOB) || strCallerEnteredDOB.contains(API_DOB))) {
										data.addToLog(currElementName, "Selected Policy Object found :: Retrieving values");
										
										//Set EPC PAYMENTUS ID
										if (policyObject.containsKey("billingAccountNumber")) {
											data.setSessionData("S_EPC_PAYMENT_POLICYNUM", (String) policyObject.get("billingAccountNumber").toString().trim());
											data.addToLog(currElementName, "Setting EPC PaymentUS ID into session :: "+data.getSessionData("S_EPC_PAYMENT_POLICYNUM"));
										}
										//Get Name & Email & Add in final Object
										if (insuredDetailsObject.containsKey("firstName")) {
											String firstname = (String) insuredDetailsObject.get("firstName").toString().trim();
											finalObject.put("firstname", firstname);
										}
										if (insuredDetailsObject.containsKey("lastName")) {
											String lastname = (String) insuredDetailsObject.get("lastName").toString().trim();
											finalObject.put("lastname", lastname);
										}
										if (insuredDetailsObject.containsKey("basicElectronicAddress")) {
											overrideEmail = (String) insuredDetailsObject.get("basicElectronicAddress").toString().trim();
											
											if (null != overrideEmail && !overrideEmail.isEmpty()) {
												data.setSessionData(Constants.S_EMAIL, overrideEmail);
												data.addToLog(currElementName, "OVERRIDING EMAIL WHICH MATCHED WITH DOB INTO SESSION :: " +overrideEmail);
											}
										}
										//Name End
										
										//Get Zip Code, City, State, Country information & Add in Final Object
										if (null != policyObject && policyObject.containsKey("address")) {
											JSONArray addressArr = (JSONArray) policyObject.get("address");
											if (null != addressArr && addressArr.size() > 0) {
												JSONObject addressObj = (JSONObject) addressArr.get(0);
												
												if (null != addressObj) {
													if (addressObj.containsKey("zip")) {
														String zip = (String) addressObj.get("zip").toString().trim();
														if (null != zip) {
															zip = zip.length() > 5 ? zip.substring(0, 5) : zip;
														}
														finalObject.put("zip", zip);
														//Caller Verification Project
														
														data.setSessionData(Constants.S_ZIP_CODE, zip);
													}
													if (addressObj.containsKey("city")) {
														String city = (String) addressObj.get("city").toString().trim();
														finalObject.put("city", city);
													}
													if (addressObj.containsKey("state")) {
														String state = (String) addressObj.get("state").toString().trim();
														finalObject.put("state", state);
													}
													if (addressObj.containsKey("country")) {
														String country = (String) addressObj.get("country").toString().trim();
														finalObject.put("country", country);
													}
													if (addressObj.containsKey("streetAddress1")) {
														String address = (String) addressObj.get("streetAddress1").toString().trim();
														finalObject.put("address", address);
													}
												}
											}
										}
										//Address End
									}
									else {
										data.addToLog(currElementName, "Policy Object does not contain insuredDetails Array :: "+policyObject);
									}
								}
							}
						}
					}
				}
				else {
					data.addToLog(currElementName, "FDS Policy Lookup Response does not contain Policy Array ::"+resp.toString());
				}
			}
			else {
				data.addToLog(currElementName, "FDS Response String is null or empty :: "+resp.toString());
			}
		}
		catch(Exception e) {
			data.addToLog(currElementName,"Exception in SIDA_MN_005 :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName, "Final Object with Policy data :: "+finalObject);
		return finalObject;
	}
	
	public JSONObject apiResponseManipulation_FM(String currElementName, DecisionElementData data, JSONObject resp, String strCallerEnteredDOB) {
		JSONObject finalObject = new JSONObject();
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		String productcode = null;
		String policynumber = null;
		
		try {
			if (resp != null) {
				
				if (null != resp && resp.containsKey("policies")) {
					JSONArray policiesArr = (JSONArray) resp.get("policies");
					for (Object policiesArraIterator : policiesArr) {
						JSONObject policyObject = (JSONObject) policiesArraIterator;
						
						if (null != policyObject && policyObject.containsKey("policyNumber")) {
							policynumber = (String) policyObject.get("policyNumber").toString().trim();
						}
						if (null != policyObject && policyObject.containsKey("policyProductCode")) {
							productcode = (String) policyObject.get("policyProductCode").toString().trim();
							data.addToLog(currElementName, "Foremost Product Code Received from API :: " + productcode);
							if (null != productcode && productcode.length() == 2) {
								productcode = "0"+productcode;
								data.addToLog(currElementName, "Foremost Product Code is 2 digits :: Adding zero as Prefix :: Product code Post manipulation :: " + productcode);
							}
							data.setSessionData("POLICY_PRODUCT_CODE", productcode);
							data.addToLog(currElementName, "Setting Foremost Product Code Into Session Post Manipulation :: " + data.getSessionData("POLICY_PRODUCT_CODE"));
						}
						
						//Set EPC PAYMENTUS ID
						if (null != policynumber && null != productcode) {
								data.setSessionData("S_EPC_PAYMENT_POLICYNUM", productcode+policynumber);
								data.addToLog(currElementName, "Setting EPC PaymentUS ID into session :: "+data.getSessionData("S_EPC_PAYMENT_POLICYNUM"));
						}
						
						//Get First Name & Last Name & Add it in Final Object
						if (null != policyObject && policyObject.containsKey("insureds")) {
							JSONArray insuredArray = (JSONArray) policyObject.get("insureds");
							
							for (Object insuredArrayIterator : insuredArray) {
								JSONObject insuredObject = (JSONObject) insuredArrayIterator;
								
								if (null != insuredObject && insuredObject.containsKey("name")) {
									JSONObject name = (JSONObject) insuredObject.get("name");
									
									if (null != name) {
										
										if (name.containsKey("firstName")) {
											String firstname = (String) name.get("firstName").toString().trim();
											finalObject.put("firstname", firstname);
										}
										if (name.containsKey("lastName")) {
											String lastname = (String) name.get("lastName").toString().trim();
											finalObject.put("lastname", lastname);
										}
									}
								}
								else {
									data.addToLog(currElementName, "Name Object not available in insured Object :: "+insuredObject);
								}
							}
						}
						//Name Ends
						
						//Get Email & Add it in Final Object
						if (null != policyObject && policyObject.containsKey("emailAddresses")) {
							JSONArray emailAddressArr = (JSONArray) policyObject.get("emailAddresses");
							
							if (null != emailAddressArr && emailAddressArr.size() > 0) {
								JSONObject emailObject = (JSONObject) emailAddressArr.get(0);
								
								if (null != emailObject && emailObject.containsKey("emailAddress")) {
									String emailaddress = (String) emailObject.get("emailAddress").toString().trim();
									finalObject.put("email", emailaddress);
								}
								else {
									data.addToLog(currElementName, "Email Object is null inside Email Array :: "+emailAddressArr);
								}
							}
							
						}
						
						//Get Address, city, state, country & Add in Final Object
						if (null != policyObject && policyObject.containsKey("addresses")) {
							JSONArray addressArr = (JSONArray) policyObject.get("addresses");
							if (null != addressArr && addressArr.size() > 0) {
								JSONObject addressObj = (JSONObject) addressArr.get(0);
								
								if (null != addressObj) {
									if (addressObj.containsKey("zip")) {
										String zip = (String) addressObj.get("zip").toString().trim();
										if (null != zip) {
											zip = zip.length() > 5 ?  zip.substring(0, 5) : zip;
										}
										finalObject.put("zip", zip);
										
										//Caller Verification Project
										
										data.setSessionData(Constants.S_ZIP_CODE, zip);
									}
									if (addressObj.containsKey("city")) {
										String city = (String) addressObj.get("city").toString().trim();
										finalObject.put("city", city);
									}
									if (addressObj.containsKey("state")) {
										String state = (String) addressObj.get("state").toString().trim();
										finalObject.put("state", state);
										data.setSessionData(Constants.S_POLICY_STATE_CODE, state);
									}
									if (addressObj.containsKey("country")) {
										String country = (String) addressObj.get("country").toString().trim();
										finalObject.put("country", country);
									}
									if (addressObj.containsKey("streetAddress1")) {
										String address = (String) addressObj.get("streetAddress1").toString().trim();
										finalObject.put("address", address);
									}
								}
								else {
									data.addToLog(currElementName, "Address Object is null :: "+addressArr);
								}
							}
							else {
								data.addToLog(currElementName, "Address Array is null :: "+policyObject);
							}
						}
						else {
							data.addToLog(currElementName, "Policy Object does not contain Address Object :: "+resp.toString());
						}
					}
				}
				else {
					data.addToLog(currElementName, "Foremost Policy Lookup Response does not contain policies Array :: "+resp.toString());
				}
			}
			else {
				data.addToLog(currElementName, "Foremost Response String is null :: "+resp.toString());
			}
		}
		catch(Exception e) {
			data.addToLog(currElementName,"Exception in SIDA_MN_005 :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName, "Final Object with Policy data :: "+finalObject);
		return finalObject;
	}
	
	
	public JSONObject apiResponseManipulation_POINT(String currElementName, DecisionElementData data, JSONObject resp, String strCallerEnteredDOB) {
		JSONObject finalObject = new JSONObject();
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		String email = null;
		String overrideEmail = null;
		
		try {
			if (resp != null) {
				
				if (null != resp && resp.containsKey("policySummary")) {
					JSONObject policyObject = (JSONObject) resp.get("policySummary");
					
					if (null != policyObject) {
						
						if (policyObject.containsKey("autoPolicy")) {
							JSONObject autoPolicy = (JSONObject) policyObject.get("autoPolicy");
							if (null != autoPolicy && autoPolicy.containsKey("drivers")) {
								JSONArray drivers = (JSONArray) policyObject.get("drivers");
								
								if (null != drivers && drivers.size() > 0) {
									for (Object driversIterator : drivers) {
										JSONObject driverObject = (JSONObject) driversIterator;
										
										if (null != driverObject) {
											
											if (driverObject.containsKey("electronicMailAddress")) {
												email = (String) driverObject.get("electronicMailAddress");
												
												if (null != email && !email.isEmpty() && null == overrideEmail) {
													data.setSessionData(Constants.S_EMAIL, email);
													data.setSessionData(Constants.S_IS_POLICY_EMAIL_AVIALABLE, Constants.STRING_YES);
												}
											}
											
											if (driverObject.containsKey("dateofbirth")) {
												
												String API_DOB = (String) driverObject.get("dateofbirth").toString().trim();
												SimpleDateFormat inputSDF = new SimpleDateFormat("MM/dd/yyyy");
												SimpleDateFormat resultSDF = new SimpleDateFormat("MM-dd-yyyy");
												API_DOB = resultSDF.format(inputSDF.parse(API_DOB));
												if (null != API_DOB && null != strCallerEnteredDOB && (API_DOB.contains(strCallerEnteredDOB) || strCallerEnteredDOB.contains(API_DOB))) {
													if (driverObject.containsKey("person")) {
														JSONObject person = (JSONObject) driverObject.get("person");
														if (person.containsKey("firstName")) {
															String firstname = (String) person.get("firstName").toString().trim();
															finalObject.put("firstname", firstname);
														}
														if (person.containsKey("lastName")) {
															String lastname = (String) person.get("lastName").toString().trim();
															finalObject.put("lastname", lastname);
														}
														if (driverObject.containsKey("electronicMailAddress")) {
															overrideEmail = (String) driverObject.get("electronicMailAddress").toString().trim();
															
															if (null != overrideEmail && !overrideEmail.isEmpty()) {
																data.setSessionData(Constants.S_EMAIL, overrideEmail);
																data.addToLog(currElementName, "OVERRIDING EMAIL WHICH MATCHED WITH DOB INTO SESSION :: " +overrideEmail);
															}
														}
													}
												}
											}
										}
										else {
											data.addToLog(currElementName, "Drivers Object null inside Drivers Array :: "+drivers);
										}
									}
								}
								else {
									data.addToLog(currElementName, "Drivers Array is null :: "+autoPolicy);
								}
							}
							else {
								data.addToLog(currElementName, "Autopolicy is null or does not contain drivers Array");
							}
						}
						
						if (policyObject.containsKey("address")) {
							JSONArray addressArr = (JSONArray) policyObject.get("address");
							
							if (null != addressArr && addressArr.size() > 0) {
								JSONObject addressObject = (JSONObject) addressArr.get(0);
								
								if (null != addressObject) {
									
									if (addressObject.containsKey("zip")) {
										String zip = (String) addressObject.get("zip").toString().trim();
										if (null != zip) {
											zip = zip.length() > 5 ? zip.substring(0, 5) : zip;
										}
										finalObject.put("zip", zip);
										
										//Caller Verification Project
										
										data.setSessionData(Constants.S_ZIP_CODE, zip);
									}
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
								}
							}
						}
						else {
							data.addToLog(currElementName, "Policy Object does not contain Address Array :: "+policyObject);
						}
					}
					else {
						data.addToLog(currElementName, "Policy Summary Object is null :: "+resp.toString());
					}
				}
				else {
					data.addToLog(currElementName, "21C Response Object is null or does not contain PolicySummary Object :: "+resp.toString());
				}
			}
			else {
				data.addToLog(currElementName, "21C Response String is null :: "+resp.toString());
				}
		}
		catch(Exception e) {
			data.addToLog(currElementName,"Exception in SIDA_MN_005 :: "+e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName, "Final Object with Policy data :: "+finalObject);
		return finalObject;
	}
	
	
	public JSONObject apiResponseManipulation_FWS(String currElementName, DecisionElementData data, JSONObject resp, String strCallerEnteredDOB) {
		JSONObject finalObject = new JSONObject();
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		String Selected_LOB = (String) data.getSessionData("S_LOB");
		String API_LOB = null;
		String billingaccountnumber;
		String policysource = null;
		
		try {
			if (resp != null) {
				
				data.addToLog(currElementName, "FWS Policy Lookup resp :: "+resp);
				
				if (null != resp && resp.containsKey("policies")) {
					JSONArray policiesArr = (JSONArray) resp.get("policies");
					
					for(Object policyobj : policiesArr) {
						JSONObject policydata = (JSONObject) policyobj;
						
						if (null != policydata) {
							
							if (policydata.containsKey("lineOfBusiness")) {
								API_LOB = (String) policydata.get("lineOfBusiness");
							}
							
							if (policydata.containsKey("insuredDetails")) {
								JSONArray insureddetails = (JSONArray) policydata.get("insuredDetails");
								
								for (Object insureddetailsiterator : insureddetails) {
									JSONObject insureddetailsObj = (JSONObject) insureddetailsiterator;
									
									if (null != insureddetailsObj && insureddetailsObj.containsKey("birthDate")) {
										String API_DOB = (String) insureddetailsObj.get("birthDate").toString().trim();
										
										if (policydata.get("policySource").toString().equalsIgnoreCase("ARS")) {
											SimpleDateFormat inputSDF = new SimpleDateFormat("MM/dd/yyyy");
											SimpleDateFormat resultSDF = new SimpleDateFormat("MM-dd-yyyy");
											API_DOB = resultSDF.format(inputSDF.parse(API_DOB));
										}
										else {
											SimpleDateFormat inputSDF = new SimpleDateFormat("yyyy-MM-dd");
											SimpleDateFormat resultSDF = new SimpleDateFormat("MM-dd-yyyy");
											API_DOB = resultSDF.format(inputSDF.parse(API_DOB));
										}
										
										if (null != strCallerEnteredDOB && null != API_DOB && null != Selected_LOB && null != API_LOB && (strCallerEnteredDOB.contains(API_DOB) || API_DOB.contains(strCallerEnteredDOB)) && API_LOB.equalsIgnoreCase(Selected_LOB)) {
											data.setSessionData(Constants.S_FWS_POLICY_LOB, policydata.get("lineOfBusiness").toString().trim());
											data.setSessionData(Constants.S_POLICY_SOURCE, policydata.get("policySource").toString().trim());
											data.setSessionData(Constants.S_POLICY_NUM, policydata.get("policyNumber").toString().trim());
											
											String firstname = (String) insureddetailsObj.get("firstName").toString().trim();
											finalObject.put("firstname", firstname);
											String lastname = (String) insureddetailsObj.get("lastName").toString().trim();
											finalObject.put("lastname", lastname);
											
											if (policydata.containsKey("addresses")) {
												JSONArray addressesArr = (JSONArray) policydata.get("addresses");
												JSONObject addressesObj = (JSONObject) addressesArr.get(0);
												if (null != addressesObj && addressesObj.containsKey("zip")) {
													String zip = (String) addressesObj.get("zip").toString().trim();
													zip = zip.length() > 5 ? zip.substring(0, 5) : zip;
													finalObject.put("zip", zip);
												}
											}
											
											if (policydata.containsKey("policySource")) {
												policysource = (String) policydata.get("policySource").toString().trim();
											}
											
											if (policydata.containsKey("billingAccountNumber")) {
												billingaccountnumber = (String) policydata.get("billingAccountNumber").toString().trim();
												data.setSessionData("S_EPC_PAYMENT_POLICYNUM", billingaccountnumber);
												data.addToLog(currElementName, "Setting S_EPC_PAYMENT_POLICYNUM id into session :: "+data.getSessionData("S_EPC_PAYMENT_POLICYNUM"));
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
												data.setSessionData("S_FWS_POLICY_REN_EFF_DATE", (String) policydata.get("renewalEffectiveDate").toString().trim());
												data.addToLog(currElementName, "Setting Policy Renewal Effective Date into session :: "+policydata.get("renewalEffectiveDate"));
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
												data.setSessionData("S_FWS_CALL_ROUTING_INDICATOR", (String) policydata.get("callRoutingIndicator").toString().trim());
												data.addToLog(currElementName, "Setting Call Routing Indicator Code into session :: "+policydata.get("callRoutingIndicator"));
											}
											if (policydata.containsKey("serviceLevels")) {
												data.setSessionData("S_FWS_SERVICE_LEVEL", (String) policydata.get("serviceLevels").toString().trim());
												data.addToLog(currElementName, "Setting ServiceLevel into session :: "+policydata.get("serviceLevels"));
											}
										}
										else {
											data.addToLog(currElementName, "Match Condition Failed");
										}
									}
								}
							}
						}
					}
				}
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

	public static void main(String[] args) throws ParseException {
		String strAPI_DOB = "1970-08-161970-10-08",finalDOB="";
		if(strAPI_DOB!=null) {
			String[] arrAPIDOB = strAPI_DOB.split(",");
			for (String temp : arrAPIDOB) {
				System.out.println("strMenuValue : "+" :: strAPI_DOB : "+arrAPIDOB);
				if(temp.contains("/")) {
					temp = temp.replaceAll("/", "-");
					SimpleDateFormat inputSDF = new SimpleDateFormat("MM-dd-yyyy");
					SimpleDateFormat resultSDF = new SimpleDateFormat("MM-dd-yyyy");
					temp = resultSDF.format(inputSDF.parse(temp));
				} else if(temp.contains("-")) {
					SimpleDateFormat inputSDF = new SimpleDateFormat("yyyy-MM-dd");
					SimpleDateFormat resultSDF = new SimpleDateFormat("MM-dd-yyyy");
					temp = resultSDF.format(inputSDF.parse(temp));
				}
				finalDOB = finalDOB +","+temp;
				System.out.println("strMenuValue :"+"Final Appended Date String After Format conversion :: "+finalDOB);
			}
			
			String API_DOB = "01/20/2024";
			SimpleDateFormat inputSDF = new SimpleDateFormat("MM/dd/yyyy");
			SimpleDateFormat resultSDF = new SimpleDateFormat("dd-MM-yyyy");
			System.out.println("Final Date Post Conversion :: "+resultSDF.format(inputSDF.parse(API_DOB)));
		}
	}

}
