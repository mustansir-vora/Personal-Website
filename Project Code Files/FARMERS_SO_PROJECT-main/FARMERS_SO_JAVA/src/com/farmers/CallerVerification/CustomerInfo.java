package com.farmers.CallerVerification;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class CustomerInfo extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {

			data.addToLog(currElementName, "Fetching Required Data For Google CCAI  & setting into session");

			String resp = "ER";

			JSONObject SharedIDAuthresp = (JSONObject) data.getSessionData("SIDA_MN_005_VALUE_RESP_CUST_NEW");

			data.addToLog(currElementName, "Shared ID Auth Policy Lookup Resp :: " + SharedIDAuthresp);

			String BW_BU_Flag = (String) data.getSessionData("S_FLAG_BW_BU");
			String FDS_BU_Flag = (String) data.getSessionData("S_FLAG_FDS_BU");
			String FM_BU_Flag = (String) data.getSessionData("S_FLAG_FOREMOST_BU");
			String FWS_BU_Flag = (String) data.getSessionData("S_FLAG_FWS_BU");
			String POINT_BU_Flag = (String) data.getSessionData("S_FLAG_21ST_BU");

			if (!(SharedIDAuthresp == null)) {

				if (null != FWS_BU_Flag && FWS_BU_Flag.equalsIgnoreCase(Constants.STRING_YES)) {
					resp = apiResponseManipulation_FWS(currElementName, data, SharedIDAuthresp);
					data.addToLog(currElementName, "resp :: " + resp);
					strExitState= resp;
				} else if (null != BW_BU_Flag && BW_BU_Flag.equalsIgnoreCase(Constants.STRING_YES)) {
					resp = apiResponseManipulation_BW(currElementName, data, SharedIDAuthresp);
					strExitState= resp;
				} else if (null != FDS_BU_Flag && FDS_BU_Flag.equalsIgnoreCase(Constants.STRING_YES)) {
					resp = apiResponseManipulation_FDS(currElementName, data, SharedIDAuthresp);
					strExitState= resp;
				} else if (null != FM_BU_Flag && FM_BU_Flag.equalsIgnoreCase(Constants.STRING_YES)) {
					resp = apiResponseManipulation_FM(currElementName, data, SharedIDAuthresp);
					strExitState= resp;
				} else if (null != POINT_BU_Flag && POINT_BU_Flag.equalsIgnoreCase(Constants.STRING_YES)) {
					resp = apiResponseManipulation_POINT(currElementName, data, SharedIDAuthresp);
					strExitState= resp;
				} 
			}else {

				JSONObject SharedIDAuthrespKYC = (JSONObject) data.getSessionData("SIDA_MN_005_VALUE_RESP");

				data.addToLog(currElementName, "Shared ID Auth Policy Lookup Resp KYC RESP:: " + SharedIDAuthrespKYC);

				if (null != FWS_BU_Flag && FWS_BU_Flag.equalsIgnoreCase(Constants.STRING_YES)) {
					resp = apiResponseManipulation_FWS(currElementName, data, SharedIDAuthrespKYC);
					data.addToLog(currElementName, "resp :: " + resp);
					strExitState= resp;
				} else if (null != BW_BU_Flag && BW_BU_Flag.equalsIgnoreCase(Constants.STRING_YES)) {
					resp = apiResponseManipulation_BW(currElementName, data, SharedIDAuthrespKYC);
					data.addToLog(currElementName, "resp :: " + resp);
					strExitState= resp;
				} else if (null != FDS_BU_Flag && FDS_BU_Flag.equalsIgnoreCase(Constants.STRING_YES)) {
					resp = apiResponseManipulation_FDS(currElementName, data, SharedIDAuthrespKYC);
					data.addToLog(currElementName, "resp :: " + resp);
					strExitState= resp;

				} else if (null != FM_BU_Flag && FM_BU_Flag.equalsIgnoreCase(Constants.STRING_YES)) {
					resp = apiResponseManipulation_FM(currElementName, data, SharedIDAuthrespKYC);
					data.addToLog(currElementName, "resp :: " + resp);
					strExitState= resp;

				} else if (null != POINT_BU_Flag && POINT_BU_Flag.equalsIgnoreCase(Constants.STRING_YES)) {
					resp = apiResponseManipulation_POINT(currElementName, data, SharedIDAuthrespKYC);
					data.addToLog(currElementName, "resp :: " + resp);
					strExitState= resp;
				} 

			}

		}

		catch (Exception e) {
			data.addToLog(currElementName, "Exception in CustomerInfo:: " + e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName, "CustomerInfo :: " + strExitState);
		return strExitState;
	}

	public String apiResponseManipulation_FWS(String currElementName, DecisionElementData data, JSONObject resp) {

		String strExitState = Constants.SU;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);

		try {

			StringBuilder formattedNames = new StringBuilder();
			StringBuilder formattedDOB = new StringBuilder();

			if (resp != null) {

				data.addToLog(currElementName, "FWS Policy Lookup resp :: " + resp);

				if (null != resp && resp.containsKey("policies")) {

					JSONArray policiesArr = (JSONArray) resp.get("policies");

					for (Object policyobj : policiesArr) {
						JSONObject policydata = (JSONObject) policyobj;
						String firstname = null;
						String lastname = null;
						String fullName = null;
						String DOB = null;
						String convertedDate = null;
		
						if (policydata.containsKey("insuredDetails")) {
							JSONArray insureddetails = (JSONArray) policydata.get("insuredDetails");

							for (Object insureddetailsiterator : insureddetails) {
								JSONObject insureddetailsObj = (JSONObject) insureddetailsiterator;

								data.addToLog(currElementName,
										"Retreive Agent Details by AOR Parsed Response name :: " + insureddetailsObj);

								// CS1195473
								//CS1195470 Farmers Insurance | US | CCAI Verification - Send FN/LN/DOB as 1:1 Matching Values. Exclude True Duplicates
								
								if (!insureddetailsObj.containsKey("firstName")
										|| !insureddetailsObj.containsKey("lastName")||insureddetailsObj.get("firstName")==null||insureddetailsObj.get("lastName")==null) {

									fullName="NA";
								} else {
									firstname = (String) insureddetailsObj.get("firstName").toString().trim();
									lastname = (String) insureddetailsObj.get("lastName").toString().trim();
									fullName = firstname + " " + lastname;      
								}

								if (!insureddetailsObj.containsKey("birthDate")||insureddetailsObj.get("birthDate")==null) {

									convertedDate="NA";
								} else {

									DOB = (String) insureddetailsObj.get("birthDate");

									DateTimeFormatter[] possibleFormatters = {
											DateTimeFormatter.ofPattern("MM/dd/yyyy"),
											DateTimeFormatter.ofPattern("M/dd/yyyy"),
											DateTimeFormatter.ofPattern("MM-dd-yyyy"),
											DateTimeFormatter.ofPattern("M-dd-yyyy"),
											DateTimeFormatter.ofPattern("yyyy-MM-dd"), };

									DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");

									for (DateTimeFormatter formatter : possibleFormatters) {
										try {
											LocalDate parsedDate = LocalDate.parse(DOB, formatter);
											convertedDate = parsedDate.format(outputFormatter);
											break; // Exit the loop if parsing succeeds
										} catch (DateTimeParseException ignored) {
											// Ignore the exception and try the next formatter
										}
									}
                                  
								}
								
								data.addToLog("fullName: ", fullName);
								data.addToLog("DOB: ", convertedDate);
							   
								//unique names with DOB as NA
								if(!fullName.equalsIgnoreCase("NA")&& !formattedNames.toString().contains(fullName) && convertedDate.equalsIgnoreCase("NA")) {
									formattedNames.append(fullName).append("|");
									formattedDOB.append(convertedDate).append("|");
								}//unique DOB with name as NA
								else if(fullName.equalsIgnoreCase("NA") && !convertedDate.equalsIgnoreCase("NA") && !formattedDOB.toString().contains(convertedDate)) {
									formattedNames.append(fullName).append("|");
									formattedDOB.append(convertedDate).append("|");
								}//if unique name with unique DOB (no NA)
								else if(!fullName.equalsIgnoreCase("NA")&& !formattedNames.toString().contains(fullName) && !convertedDate.equalsIgnoreCase("NA") && !formattedDOB.toString().contains(convertedDate)) {
									formattedNames.append(fullName).append("|");
									formattedDOB.append(convertedDate).append("|");
								}//duplicate name with unique or NA as DOB
								else if(formattedNames.toString().contains(fullName)) {
									if(convertedDate.equalsIgnoreCase("NA") || !formattedDOB.toString().contains(convertedDate)) {
									formattedNames.append(fullName).append("|");
									formattedDOB.append(convertedDate).append("|");
									}
								}
								//duplicate DOB with unique or NA as name
								else if(formattedDOB.toString().contains(convertedDate)) {
									if(fullName.equalsIgnoreCase("NA") || !formattedNames.toString().contains(fullName))
									formattedNames.append(fullName).append("|");
									formattedDOB.append(convertedDate).append("|");
								}
								
								
							}
						}
					}
					if (formattedNames.length() > 0 && formattedNames.charAt(formattedNames.length() - 1) == '|') {
						formattedNames.deleteCharAt(formattedNames.length() - 1);
					}
					if (formattedDOB.length() > 0 && formattedDOB.charAt(formattedDOB.length() - 1) == '|') {
						formattedDOB.deleteCharAt(formattedDOB.length() - 1);
					}

					data.setSessionData(Constants.S_CCAI_NAME, formattedNames.toString());
					data.addToLog(currElementName,
							"Appended First and Last Name  :: " + data.getSessionData(Constants.S_CCAI_NAME));
					data.setSessionData(Constants.S_API_DOB, formattedDOB.toString());
					data.addToLog(currElementName,
							"Appended formattedDOB  :: " + data.getSessionData(Constants.S_API_DOB));
				}
			}

		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in CustomerInfo :: " + e);
			caa.printStackTrace(e);
			strExitState=Constants.ER;
		}
		data.addToLog(currElementName, "Final Object with Policy data :: " + strExitState);
		return strExitState;
	}

	public String apiResponseManipulation_POINT(String currElementName, DecisionElementData data, JSONObject resp) {

		String strExitState = Constants.SU;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);

		try {

			StringBuilder formattedNames = new StringBuilder();
			StringBuilder formattedDOB = new StringBuilder();

			if (resp != null) {

				data.addToLog(currElementName, "POINT Policy Lookup resp :: " + resp);

				if (null != resp && resp.containsKey("policies")) {

					JSONArray policiesArr = (JSONArray) resp.get("policies");

					for (Object policyobj : policiesArr) {
						JSONObject policydata = (JSONObject) policyobj;
						String firstname = null;
						String lastname = null;
						String fullName = null;
						String DOB = null;
						String convertedDate = null;
						if (policydata.containsKey("insuredDetails")) {
							JSONArray insureddetails = (JSONArray) policydata.get("insuredDetails");

							for (Object insureddetailsiterator : insureddetails) {
								JSONObject insureddetailsObj = (JSONObject) insureddetailsiterator;

								data.addToLog(currElementName,
										"Retreive Agent Details by AOR Parsed Response name :: " + insureddetailsObj);
                                //CS1195473
								if(!insureddetailsObj.containsKey("firstName")||!insureddetailsObj.containsKey("lastName")||
										insureddetailsObj.get("firstName")==null||insureddetailsObj.get("lastName")==null) {
									 fullName="NA";
								}else {
								firstname = (String) insureddetailsObj.get("firstName").toString().trim();
								lastname = (String) insureddetailsObj.get("lastName").toString().trim();
								fullName = firstname + " " + lastname;
//								if (firstname != null && !firstname.isEmpty() && lastname != null && !lastname.isEmpty()
//										&& !formattedNames.toString().contains(fullName)) {
//									formattedNames.append(fullName).append("|");
//								}
								}
                                
								if(!insureddetailsObj.containsKey("birthDate")||insureddetailsObj.get("birthDate")==null) {
									//formattedDOB.append("NA").append("|");
									convertedDate="NA";
								}else {
								DOB = (String) insureddetailsObj.get("birthDate");
								DateTimeFormatter[] possibleFormatters = { DateTimeFormatter.ofPattern("MM/dd/yyyy"),
										DateTimeFormatter.ofPattern("M/dd/yyyy"),
										DateTimeFormatter.ofPattern("MM-dd-yyyy"),
										DateTimeFormatter.ofPattern("M-dd-yyyy"),
										DateTimeFormatter.ofPattern("yyyy-MM-dd"), };

								DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");

								for (DateTimeFormatter formatter : possibleFormatters) {
									try {
										LocalDate parsedDate = LocalDate.parse(DOB, formatter);
										convertedDate = parsedDate.format(outputFormatter);
										break; // Exit the loop if parsing succeeds
									} catch (DateTimeParseException ignored) {
										// Ignore the exception and try the next formatter
									}
								}
								
								}
								
								data.addToLog("fullName: ", fullName);
								data.addToLog("DOB: ", convertedDate);
							   
								//unique names with DOB as NA
								if(!fullName.equalsIgnoreCase("NA")&& !formattedNames.toString().contains(fullName) && convertedDate.equalsIgnoreCase("NA")) {
									formattedNames.append(fullName).append("|");
									formattedDOB.append(convertedDate).append("|");
								}//unique DOB with name as NA
								else if(fullName.equalsIgnoreCase("NA") && !convertedDate.equalsIgnoreCase("NA") && !formattedDOB.toString().contains(convertedDate)) {
									formattedNames.append(fullName).append("|");
									formattedDOB.append(convertedDate).append("|");
								}//if unique name with unique DOB (no NA)
								else if(!fullName.equalsIgnoreCase("NA")&& !formattedNames.toString().contains(fullName) && !convertedDate.equalsIgnoreCase("NA") && !formattedDOB.toString().contains(convertedDate)) {
									formattedNames.append(fullName).append("|");
									formattedDOB.append(convertedDate).append("|");
								}//duplicate name with unique or NA as DOB
								else if(formattedNames.toString().contains(fullName)) {
									if(convertedDate.equalsIgnoreCase("NA") || !formattedDOB.toString().contains(convertedDate)) {
									formattedNames.append(fullName).append("|");
									formattedDOB.append(convertedDate).append("|");
									}
								}
								//duplicate DOB with unique or NA as name
								else if(formattedDOB.toString().contains(convertedDate)) {
									if(fullName.equalsIgnoreCase("NA") || !formattedNames.toString().contains(fullName))
									formattedNames.append(fullName).append("|");
									formattedDOB.append(convertedDate).append("|");
								}
							}

						}
					}
					if (formattedNames.length() > 0 && formattedNames.charAt(formattedNames.length() - 1) == '|') {
						formattedNames.deleteCharAt(formattedNames.length() - 1);
					}
					if (formattedDOB.length() > 0 && formattedDOB.charAt(formattedDOB.length() - 1) == '|') {
						formattedDOB.deleteCharAt(formattedDOB.length() - 1);
					}

					data.setSessionData(Constants.S_CCAI_NAME, formattedNames.toString());
					data.addToLog(currElementName,
							"Appended First and Last Name  :: " + data.getSessionData(Constants.S_CCAI_NAME));
					data.setSessionData(Constants.S_API_DOB, formattedDOB.toString());
					data.addToLog(currElementName,
							"Appended formattedDOB  :: " + data.getSessionData(Constants.S_API_DOB));

				}

			}

			if (resp != null) {

				data.addToLog(currElementName, "POINT Policy Lookup resp HAwai Else loop :: " + resp);

				if (null != resp && resp.containsKey("policySummary")) {
					JSONObject policySummary = (JSONObject) resp.get("policySummary");

					data.addToLog(currElementName,
							"POINT Policy Lookup resp HAwai Else loop Policy Summary :: " + policySummary);

					if (null != policySummary && policySummary.containsKey("autoPolicy")) {

						JSONObject autoPolicy = (JSONObject) policySummary.get("autoPolicy");

						data.addToLog(currElementName,
								"POINT Policy Lookup resp HAwai Else loop autoPolicy :: " + autoPolicy);

						if (null != autoPolicy && autoPolicy.containsKey("drivers")) {

							JSONArray DriverDetails = (JSONArray) autoPolicy.get("drivers");

							for (Object driverdetailsdetailsiterator : DriverDetails) {

								JSONObject insureddetailsObj = (JSONObject) driverdetailsdetailsiterator;

								JSONObject namedInsured = (JSONObject) insureddetailsObj.get("person");

								data.addToLog(currElementName, "namedInsured:: " + namedInsured);

								String firstname = null;
								String lastname = null;
								String fullName = null;
								String DOB = null;
								String convertedDate = null;
								String state = null;
                                
								if(!namedInsured.containsKey("firstName")||!namedInsured.containsKey("lastName")
										|| namedInsured.get("firstName")==null||namedInsured.get("lastName")==null) {
									//formattedNames.append("NA").append("|");
									fullName="NA";
								}else {
								firstname = (String) namedInsured.get("firstName").toString().trim();
								lastname = (String) namedInsured.get("lastName").toString().trim();
								fullName = firstname + " " + lastname;

//								if (firstname != null && !firstname.isEmpty() && lastname != null && !lastname.isEmpty()
//										&& !formattedNames.toString().contains(fullName)) {
//									formattedNames.append(fullName).append("|");
//
//								}
								}
                                
								if(insureddetailsObj.containsKey("dateofbirth")||insureddetailsObj.get("dateofbirth")==null) {
									fullName="NA";
								}else {
								DOB = (String) insureddetailsObj.get("dateofbirth");
								DateTimeFormatter[] possibleFormatters = { DateTimeFormatter.ofPattern("MM/dd/yyyy"),
										DateTimeFormatter.ofPattern("M/dd/yyyy"),
										DateTimeFormatter.ofPattern("MM-dd-yyyy"),
										DateTimeFormatter.ofPattern("M-dd-yyyy"),
										DateTimeFormatter.ofPattern("yyyy-MM-dd"), };

								DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");

								for (DateTimeFormatter formatter : possibleFormatters) {
									try {
										LocalDate parsedDate = LocalDate.parse(DOB, formatter);
										convertedDate = parsedDate.format(outputFormatter);
										break; // Exit the loop if parsing succeeds
									} catch (DateTimeParseException ignored) {
										// Ignore the exception and try the next formatter
									}
								}
//								if (convertedDate != null && !convertedDate.isEmpty()
//										&& !formattedDOB.toString().contains(convertedDate)) {
//									formattedDOB.append(convertedDate).append("|");
//								}
								}
								data.addToLog("fullName: ", fullName);
								data.addToLog("DOB: ", convertedDate);
							   
								//unique names with DOB as NA
								if(!fullName.equalsIgnoreCase("NA")&& !formattedNames.toString().contains(fullName) && convertedDate.equalsIgnoreCase("NA")) {
									formattedNames.append(fullName).append("|");
									formattedDOB.append(convertedDate).append("|");
								}//unique DOB with name as NA
								else if(fullName.equalsIgnoreCase("NA") && !convertedDate.equalsIgnoreCase("NA") && !formattedDOB.toString().contains(convertedDate)) {
									formattedNames.append(fullName).append("|");
									formattedDOB.append(convertedDate).append("|");
								}//if unique name with unique DOB (no NA)
								else if(!fullName.equalsIgnoreCase("NA")&& !formattedNames.toString().contains(fullName) && !convertedDate.equalsIgnoreCase("NA") && !formattedDOB.toString().contains(convertedDate)) {
									formattedNames.append(fullName).append("|");
									formattedDOB.append(convertedDate).append("|");
								}//duplicate name with unique or NA as DOB
								else if(formattedNames.toString().contains(fullName)) {
									if(convertedDate.equalsIgnoreCase("NA") || !formattedDOB.toString().contains(convertedDate)) {
									formattedNames.append(fullName).append("|");
									formattedDOB.append(convertedDate).append("|");
									}
								}
								//duplicate DOB with unique or NA as name
								else if(formattedDOB.toString().contains(convertedDate)) {
									if(fullName.equalsIgnoreCase("NA") || !formattedNames.toString().contains(fullName))
									formattedNames.append(fullName).append("|");
									formattedDOB.append(convertedDate).append("|");
								}
							}
							if (formattedNames.length() > 0
									&& formattedNames.charAt(formattedNames.length() - 1) == '|') {
								formattedNames.deleteCharAt(formattedNames.length() - 1);
							}
							if (formattedDOB.length() > 0 && formattedDOB.charAt(formattedDOB.length() - 1) == '|') {
								formattedDOB.deleteCharAt(formattedDOB.length() - 1);
							}

							data.setSessionData(Constants.S_CCAI_NAME, formattedNames.toString());
							data.addToLog(currElementName,
									"Appended First and Last Name  :: " + data.getSessionData(Constants.S_CCAI_NAME));
							data.setSessionData(Constants.S_API_DOB, formattedDOB.toString());
							data.addToLog(currElementName,
									"Appended formattedDOB  :: " + data.getSessionData(Constants.S_API_DOB));
						}

					}

				}
			}
		}

		catch (Exception e) {
			data.addToLog(currElementName, "Exception in CustomerInfo :: " + e);
			caa.printStackTrace(e);
			strExitState=Constants.ER;
		}
		data.addToLog(currElementName, "Final Object with Policy data :: " + strExitState);
		return strExitState;
	}

	public String apiResponseManipulation_FM(String currElementName, DecisionElementData data, JSONObject resp) {

		String strExitState = Constants.SU;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);

		try {

			StringBuilder formattedNames = new StringBuilder();
			StringBuilder formattedDOB = new StringBuilder();
			StringBuilder formattedstate = new StringBuilder();
			if (resp != null) {

				data.addToLog(currElementName, "FM Policy Lookup resp :: " + resp);

				if (null != resp && resp.containsKey("policies")) {

					JSONArray policiesArr = (JSONArray) resp.get("policies");

					for (Object policyobj : policiesArr) {
						JSONObject policydata = (JSONObject) policyobj;
						String firstname = null;
						String lastname = null;
						String fullName = null;
						String DOB = null;
						String convertedDate = null;
						String state = null;
						if (policydata.containsKey("drivers")) {

							JSONArray driversdetails = (JSONArray) policydata.get("drivers");

							if (driversdetails != null && !driversdetails.isEmpty()) {

								for (Object driversdetailsiterator : driversdetails) {

									JSONObject driversdetailsObj = (JSONObject) driversdetailsiterator;

									JSONObject drivername = (JSONObject) driversdetailsObj.get("name");

									data.addToLog(currElementName, "inside Driver Details name :: " + drivername);
                                    if(!drivername.containsKey("firstName")||!drivername.containsKey("lastName")
                                    		|| drivername.get("firstName")==null||drivername.get("lastName")==null) {
                                    	//formattedNames.append("NA").append("|");
                                    	fullName="NA";
                                    }else {
									firstname = (String) drivername.get("firstName").toString().trim();
									lastname = (String) drivername.get("lastName").toString().trim();
									fullName = firstname + " " + lastname;
//									if (firstname != null && !firstname.isEmpty() && lastname != null
//											&& !lastname.isEmpty() && !formattedNames.toString().contains(fullName)) {
//										formattedNames.append(fullName).append("|");
//									}
                                    }
                                    
                                    if(!driversdetailsObj.containsKey("birthDate")||driversdetailsObj.get("birthDate")==null) {
                                    	//formattedDOB.append("NA").append("|");
                                    	convertedDate="NA";
                                    }else {
									DOB = (String) driversdetailsObj.get("birthDate");

									DateTimeFormatter[] possibleFormatters = {
											DateTimeFormatter.ofPattern("MM/dd/yyyy"),
											DateTimeFormatter.ofPattern("M/dd/yyyy"),
											DateTimeFormatter.ofPattern("MM-dd-yyyy"),
											DateTimeFormatter.ofPattern("M-dd-yyyy"),
											DateTimeFormatter.ofPattern("yyyy-MM-dd"), };

									DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");

									for (DateTimeFormatter formatter : possibleFormatters) {
										try {
											LocalDate parsedDate = LocalDate.parse(DOB, formatter);
											convertedDate = parsedDate.format(outputFormatter);
											break; // Exit the loop if parsing succeeds
										} catch (DateTimeParseException ignored) {
											// Ignore the exception and try the next formatter
										}
									}

//									if (convertedDate != null && !convertedDate.isEmpty()
//											&& !formattedDOB.toString().contains(convertedDate)) {
//										formattedDOB.append(convertedDate).append("|");
//									}
                                    }
                                    
                                	data.addToLog("fullName: ", fullName);
    								data.addToLog("DOB: ", convertedDate);
    							   
    								//unique names with DOB as NA
    								if(!fullName.equalsIgnoreCase("NA")&& !formattedNames.toString().contains(fullName) && convertedDate.equalsIgnoreCase("NA")) {
    									formattedNames.append(fullName).append("|");
    									formattedDOB.append(convertedDate).append("|");
    								}//unique DOB with name as NA
    								else if(fullName.equalsIgnoreCase("NA") && !convertedDate.equalsIgnoreCase("NA") && !formattedDOB.toString().contains(convertedDate)) {
    									formattedNames.append(fullName).append("|");
    									formattedDOB.append(convertedDate).append("|");
    								}//if unique name with unique DOB (no NA)
    								else if(!fullName.equalsIgnoreCase("NA")&& !formattedNames.toString().contains(fullName) && !convertedDate.equalsIgnoreCase("NA") && !formattedDOB.toString().contains(convertedDate)) {
    									formattedNames.append(fullName).append("|");
    									formattedDOB.append(convertedDate).append("|");
    								}//duplicate name with unique or NA as DOB
    								else if(formattedNames.toString().contains(fullName)) {
    									if(convertedDate.equalsIgnoreCase("NA") || !formattedDOB.toString().contains(convertedDate)) {
    									formattedNames.append(fullName).append("|");
    									formattedDOB.append(convertedDate).append("|");
    									}
    								}
    								//duplicate DOB with unique or NA as name
    								else if(formattedDOB.toString().contains(convertedDate)) {
    									if(fullName.equalsIgnoreCase("NA") || !formattedNames.toString().contains(fullName))
    									formattedNames.append(fullName).append("|");
    									formattedDOB.append(convertedDate).append("|");
    								}
								}
								if (formattedNames.length() > 0
										&& formattedNames.charAt(formattedNames.length() - 1) == '|') {
									formattedNames.deleteCharAt(formattedNames.length() - 1);
								}
								if (formattedDOB.length() > 0
										&& formattedDOB.charAt(formattedDOB.length() - 1) == '|') {
									formattedDOB.deleteCharAt(formattedDOB.length() - 1);
								}

								data.setSessionData(Constants.S_CCAI_NAME, formattedNames.toString());
								data.addToLog(currElementName, "Appended First and Last Name  :: "
										+ data.getSessionData(Constants.S_CCAI_NAME));
								data.setSessionData(Constants.S_API_DOB, formattedDOB.toString());
								data.addToLog(currElementName,
										"Appended formattedDOB  :: " + data.getSessionData(Constants.S_API_DOB));
                               
							} else {

								if (policydata.containsKey("insureds")) {

									JSONArray insureddetails = (JSONArray) policydata.get("insureds");

									data.addToLog(currElementName, "inside insuredname Response :: " + insureddetails);

									for (Object insureddetailsiterator : insureddetails) {

										JSONObject insureddetailsObj = (JSONObject) insureddetailsiterator;

										JSONObject insuredname = (JSONObject) insureddetailsObj.get("name");

										data.addToLog(currElementName, "inside insuredname Response :: " + insuredname);
                                        
										if(!insuredname.containsKey("firstName")||!insuredname.containsKey("lastName")
												|| insuredname.get("firstName")==null|| insuredname.get("lastName")==null) {
											fullName="NA";
											
										}else {
										firstname = (String) insuredname.get("firstName").toString().trim();
										lastname = (String) insuredname.get("lastName").toString().trim();
										fullName = firstname + " " + lastname;
										if (firstname != null && !firstname.isEmpty() && lastname != null
												&& !lastname.isEmpty()
												&& !formattedNames.toString().contains(fullName)) {
											formattedNames.append(fullName).append("|");
										}
										}
										

										if (insureddetailsObj.containsKey("addresses")) {

											JSONArray statename = (JSONArray) policydata.get("addresses");

											for (Object Statedetailsiterator1 : statename) {

												JSONObject statedetailsObj1 = (JSONObject) Statedetailsiterator1;

												// JSONArray statename = (JSONArray) insureddetails.get("insureds");

												// JSONObject statename = (JSONObject) statedetailsObj.get("addresses");

												// JSONObject StateObj = (JSONObject) statename.get(0);

												data.addToLog(currElementName,
														"insuredname Response name :: " + statedetailsObj1);
                                                if(statedetailsObj1.containsKey("state") && statedetailsObj1.get("state").toString()!=null) {
												state = (String) statedetailsObj1.get("state").toString().trim();
												if (state != null && !state.isEmpty()
														&& !formattedstate.toString().contains(state)) {
													formattedstate.append(state).append("|");
												}
                                                }else {
                                                	state="NA";
                                                	formattedstate.append(state).append("|");
                                                	
                                                }											

											}
										}
										
									}

								}
								
								if (formattedNames.length() > 0
										&& formattedNames.charAt(formattedNames.length() - 1) == '|') {
									formattedNames.deleteCharAt(formattedNames.length() - 1);
								}
								if (formattedstate.length() > 0 && formattedstate.charAt(formattedstate.length() - 1) == '|') {
									formattedstate.deleteCharAt(formattedstate.length() - 1);
								}
								
								data.setSessionData(Constants.S_CCAI_NAME, formattedNames.toString());
								data.addToLog(currElementName, "Appended First and Last Name  :: "
										+ data.getSessionData(Constants.S_CCAI_NAME));
								
								data.setSessionData(Constants.S_CCAI_STATE, formattedstate.toString());
								data.addToLog(currElementName,
										"Appended S_CCAI_STATE  :: " + data.getSessionData(Constants.S_CCAI_STATE));
							}
						}
					}
				}

			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in CustomerInfo :: " + e);
			caa.printStackTrace(e);
			strExitState=Constants.ER;
		}
		data.addToLog(currElementName, "Final Object with Policy data :: " + strExitState);
		return strExitState;
	}

	public String apiResponseManipulation_BW(String currElementName, DecisionElementData data, JSONObject resp) {

		String strExitState = Constants.SU;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);

		try {

			StringBuilder formattedNames = new StringBuilder();
			StringBuilder formattedDOB = new StringBuilder();

			if (resp != null) {

				data.addToLog(currElementName, "BW Policy Lookup resp :: " + resp);

				if (null != resp && resp.containsKey("household")) {

					JSONObject household = (JSONObject) resp.get("household");

					data.addToLog(currElementName, "householdArr :: " + household);

					if (null != household && household.containsKey("autoPolicies")) {

						JSONArray autopolicies = (JSONArray) household.get("autoPolicies");

						for (Object insureddetailsiterator : autopolicies) {

							JSONObject insureddetailsObj = (JSONObject) insureddetailsiterator;

							JSONObject namedInsured = (JSONObject) insureddetailsObj.get("namedInsured");

							data.addToLog(currElementName, "namedInsured:: " + namedInsured);
							String firstname = null;
							String lastname = null;
							String fullName = null;
							String DOB = null;
							String convertedDate = null;
							String state = null;

							if (namedInsured.containsKey("birthName")) {

								JSONObject birthnameObject = (JSONObject) namedInsured.get("birthName");
                                if(!birthnameObject.containsKey("firstName")||!birthnameObject.containsKey("lastName")||
                                		 birthnameObject.get("firstName")==null|| birthnameObject.get("lastName")==null	) {
                                	//formattedNames.append("NA").append("|");
                                	fullName="NA";
                                }else {
								firstname = (String) birthnameObject.get("firstName").toString().trim();
								lastname = (String) birthnameObject.get("lastName").toString().trim();
								fullName = firstname + " " + lastname;
//								if (firstname != null && !firstname.isEmpty() && lastname != null && !lastname.isEmpty()
//										&& !formattedNames.toString().contains(fullName)) {
//									formattedNames.append(fullName).append("|");
//								}
                                }
							}
                            
							if(!namedInsured.containsKey("birthDate")||namedInsured.get("birthDate")==null) {
//								formattedDOB.append("NA").append("|");
								convertedDate= "NA";
							}else {
								 DOB = (String) namedInsured.get("birthDate");
								 DateTimeFormatter[] possibleFormatters = {
										    DateTimeFormatter.ofPattern("MM/dd/yyyy"),
										    DateTimeFormatter.ofPattern("M/dd/yyyy"),  
										    DateTimeFormatter.ofPattern("MM-dd-yyyy"),
										    DateTimeFormatter.ofPattern("M-dd-yyyy"),   
										    DateTimeFormatter.ofPattern("yyyy-MM-dd"),
										};

								DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
								
								for (DateTimeFormatter formatter : possibleFormatters) {
								    try {
								        LocalDate parsedDate = LocalDate.parse(DOB, formatter);
								        convertedDate = parsedDate.format(outputFormatter);
								        break; // Exit the loop if parsing succeeds
								    } catch (DateTimeParseException ignored) {
								        // Ignore the exception and try the next formatter
								    }
								}
								
//								if (convertedDate != null && !convertedDate.isEmpty() && !formattedDOB.toString().contains(convertedDate)) {
//									formattedDOB.append(convertedDate).append("|");
//								}
						}
							data.addToLog("fullName: ", fullName);
							data.addToLog("DOB: ", convertedDate);
						   
							//unique names with DOB as NA
							if(!fullName.equalsIgnoreCase("NA")&& !formattedNames.toString().contains(fullName) && convertedDate.equalsIgnoreCase("NA")) {
								formattedNames.append(fullName).append("|");
								formattedDOB.append(convertedDate).append("|");
							}//unique DOB with name as NA
							else if(fullName.equalsIgnoreCase("NA") && !convertedDate.equalsIgnoreCase("NA") && !formattedDOB.toString().contains(convertedDate)) {
								formattedNames.append(fullName).append("|");
								formattedDOB.append(convertedDate).append("|");
							}//if unique name with unique DOB (no NA)
							else if(!fullName.equalsIgnoreCase("NA")&& !formattedNames.toString().contains(fullName) && !convertedDate.equalsIgnoreCase("NA") && !formattedDOB.toString().contains(convertedDate)) {
								formattedNames.append(fullName).append("|");
								formattedDOB.append(convertedDate).append("|");
							}//duplicate name with unique or NA as DOB
							else if(formattedNames.toString().contains(fullName)) {
								if(convertedDate.equalsIgnoreCase("NA") || !formattedDOB.toString().contains(convertedDate)) {
								formattedNames.append(fullName).append("|");
								formattedDOB.append(convertedDate).append("|");
								}
							}
							//duplicate DOB with unique or NA as name
							else if(formattedDOB.toString().contains(convertedDate)) {
								if(fullName.equalsIgnoreCase("NA") || !formattedNames.toString().contains(fullName))
								formattedNames.append(fullName).append("|");
								formattedDOB.append(convertedDate).append("|");
							}
						}
						if (formattedNames.length() > 0 && formattedNames.charAt(formattedNames.length() - 1) == '|') {
							formattedNames.deleteCharAt(formattedNames.length() - 1);
						}
						if (formattedDOB.length() > 0 && formattedDOB.charAt(formattedDOB.length() - 1) == '|') {
							formattedDOB.deleteCharAt(formattedDOB.length() - 1);
						}

						data.setSessionData(Constants.S_CCAI_NAME, formattedNames.toString());
						data.addToLog(currElementName,
								"Appended First and Last Name  :: " + data.getSessionData(Constants.S_CCAI_NAME));
						data.setSessionData(Constants.S_API_DOB, formattedDOB.toString());
						data.addToLog(currElementName,
								"Appended formattedDOB  :: " + data.getSessionData(Constants.S_API_DOB));

					}
				}

			}

			else {

				return strExitState;

			}

		}

		catch (Exception e) {
			data.addToLog(currElementName, "Exception in CustomerInfo :: " + e);
			caa.printStackTrace(e);
			strExitState=Constants.ER;
		}
		data.addToLog(currElementName, "Final Object with Policy data :: " + strExitState);
		return strExitState;
	}

	public String apiResponseManipulation_FDS(String currElementName, DecisionElementData data, JSONObject resp) {

		String strExitState = Constants.SU;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);

		try {

			StringBuilder formattedNames = new StringBuilder();
			StringBuilder formattedDOB = new StringBuilder();

			if (resp != null) {

				data.addToLog(currElementName, "FDS Policy Lookup resp :: " + resp);

				if (null != resp && resp.containsKey("policies")) {

					JSONArray policiesArr = (JSONArray) resp.get("policies");

					for (Object policyobj : policiesArr) {
						JSONObject policydata = (JSONObject) policyobj;
						String firstname = null;
						String lastname = null;
						String fullName = null;
						String DOB = null;
						String convertedDate = null;
						if (policydata.containsKey("insuredDetails")) {
							JSONArray insureddetails = (JSONArray) policydata.get("insuredDetails");

							for (Object insureddetailsiterator : insureddetails) {
								JSONObject insureddetailsObj = (JSONObject) insureddetailsiterator;

								data.addToLog(currElementName,
										"Retreive Agent Details by AOR Parsed Response name :: " + insureddetailsObj);
                                
								if(!insureddetailsObj.containsKey("firstName")||!insureddetailsObj.containsKey("lastName")||
										insureddetailsObj.get("firstName")==null||insureddetailsObj.get("lastName")==null) {
//									formattedNames.append("NA").append("|");
									fullName="NA";
								}else {
								firstname = (String) insureddetailsObj.get("firstName").toString().trim();
								lastname = (String) insureddetailsObj.get("lastName").toString().trim();
								fullName = firstname + " " + lastname;
//								if (firstname != null && !firstname.isEmpty() && lastname != null && !lastname.isEmpty()
//										&& !formattedNames.toString().contains(fullName)) {
//									formattedNames.append(fullName).append("|");
//								}
								}
								
								if(!insureddetailsObj.containsKey("birthDate")|| insureddetailsObj.get("birthDate").toString()==null) {
//									formattedDOB.append("NA").append("|");
									convertedDate="NA";
								}else {
								DOB = (String) insureddetailsObj.get("birthDate");
								DateTimeFormatter[] possibleFormatters = { DateTimeFormatter.ofPattern("MM/dd/yyyy"),
										DateTimeFormatter.ofPattern("M/dd/yyyy"),
										DateTimeFormatter.ofPattern("MM-dd-yyyy"),
										DateTimeFormatter.ofPattern("M-dd-yyyy"),
										DateTimeFormatter.ofPattern("yyyy-MM-dd"), };

								DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");

								for (DateTimeFormatter formatter : possibleFormatters) {
									try {
										LocalDate parsedDate = LocalDate.parse(DOB, formatter);
										convertedDate = parsedDate.format(outputFormatter);
										break; // Exit the loop if parsing succeeds
									} catch (DateTimeParseException ignored) {
										// Ignore the exception and try the next formatter
									}
								}

//								if (convertedDate != null && !convertedDate.isEmpty()
//										&& !formattedDOB.toString().contains(convertedDate)) {
//									formattedDOB.append(convertedDate).append("|");
//								}
								}
								data.addToLog("fullName: ", fullName);
								data.addToLog("DOB: ", convertedDate);
							   
								//unique names with DOB as NA
								if(!fullName.equalsIgnoreCase("NA")&& !formattedNames.toString().contains(fullName) && convertedDate.equalsIgnoreCase("NA")) {
									formattedNames.append(fullName).append("|");
									formattedDOB.append(convertedDate).append("|");
								}//unique DOB with name as NA
								else if(fullName.equalsIgnoreCase("NA") && !convertedDate.equalsIgnoreCase("NA") && !formattedDOB.toString().contains(convertedDate)) {
									formattedNames.append(fullName).append("|");
									formattedDOB.append(convertedDate).append("|");
								}//if unique name with unique DOB (no NA)
								else if(!fullName.equalsIgnoreCase("NA")&& !formattedNames.toString().contains(fullName) && !convertedDate.equalsIgnoreCase("NA") && !formattedDOB.toString().contains(convertedDate)) {
									formattedNames.append(fullName).append("|");
									formattedDOB.append(convertedDate).append("|");
								}//duplicate name with unique or NA as DOB
								else if(formattedNames.toString().contains(fullName)) {
									if(convertedDate.equalsIgnoreCase("NA") || !formattedDOB.toString().contains(convertedDate)) {
									formattedNames.append(fullName).append("|");
									formattedDOB.append(convertedDate).append("|");
									}
								}
								//duplicate DOB with unique or NA as name
								else if(formattedDOB.toString().contains(convertedDate)) {
									if(fullName.equalsIgnoreCase("NA") || !formattedNames.toString().contains(fullName))
									formattedNames.append(fullName).append("|");
									formattedDOB.append(convertedDate).append("|");
								}
							}
//							if (formattedNames.length() > 0
//									&& formattedNames.charAt(formattedNames.length() - 1) == '|') {
//								formattedNames.deleteCharAt(formattedNames.length() - 1);
//							}
//							if (formattedDOB.length() > 0 && formattedDOB.charAt(formattedDOB.length() - 1) == '|') {
//								formattedDOB.deleteCharAt(formattedDOB.length() - 1);
//							}

						}
					}
					if (formattedNames.length() > 0
							&& formattedNames.charAt(formattedNames.length() - 1) == '|') {
						formattedNames.deleteCharAt(formattedNames.length() - 1);
					}
					if (formattedDOB.length() > 0 && formattedDOB.charAt(formattedDOB.length() - 1) == '|') {
						formattedDOB.deleteCharAt(formattedDOB.length() - 1);
					}
					data.setSessionData(Constants.S_CCAI_NAME, formattedNames.toString());
					data.addToLog(currElementName,
							"Appended First and Last Name  :: " + data.getSessionData(Constants.S_CCAI_NAME));
					data.setSessionData(Constants.S_API_DOB, formattedDOB.toString());
					data.addToLog(currElementName,
							"Appended formattedDOB  :: " + data.getSessionData(Constants.S_API_DOB));
				}
			}

		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in CustomerInfo :: " + e);
			caa.printStackTrace(e);
			strExitState=Constants.ER;
		}
		data.addToLog(currElementName, "Final Object with Policy data :: " + strExitState);
		return strExitState;
	}

}
