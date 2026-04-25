package com.farmers.shared.host;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.bean.PolicyBean;
import com.farmers.bean.SHAuthForemost.ForemostRoot;
import com.farmers.client.Lookupcall;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;
import com.google.gson.Gson;

public class FWSPolicyLookupVIA extends DecisionElementBase 


{

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException 
	{
		String StrExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);objHostDetails.setinitalValue();
		String strReqBody = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;
		try {
			
			return fwsPolicyLookupNew(strRespBody, strRespBody, data, caa, currElementName);
			/*
			StrExitState = fwsPolicyLookup(strRespBody, strRespBody, data, caa, currElementName, StrExitState);
			return StrExitState;
			*/
				
		} catch (Exception e)
		{
			data.addToLog(currElementName,"Exception while forming host details for SIDA_HOST_002  :: "+e);
			caa.printStackTrace(e);
		}
	/*
		try {
			objHostDetails.startHostReport(currElementName,"fwsPolicyLookup", strReqBody);
			objHostDetails.endHostReport(data,strRespBody , StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU);
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for SIDA_HOST_001  PolicyInquiryAPI call  :: "+e);
			caa.printStackTrace(e);
		}
		*/
		return StrExitState;
	}

	public boolean checkifsingleproducttype( int autoprodtypecount, int homeprodtypecount)
	{
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

	public boolean checkifsingleproducttype(int rvprodtypecount, int autoprodtypecount, int homeprodtypecount, int umbrellaprodtypecount, int mrprodtypecount, int spprodtypecount) {
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

	
	private String fwsPolicyLookupNew(String strRespBody, String strRespBody2, DecisionElementData data, CommonAPIAccess caa, String currElementName) {
		String strExitState = Constants.ER, strReqBody = Constants.EMPTY;;
		SetHostDetails objHostDetails = new SetHostDetails(caa);objHostDetails.setinitalValue();
		boolean startsWithAlphabets = false;
		String policyContractNumber = Constants.EmptyString;
		
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		
		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
		String region=null;
        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
        //END- UAT ENV SETUP CODE (PRIYA, SHAIK)
		try {
			if(data.getSessionData(Constants.S_FWS_POLICYLOOKUP_URL) != null) {
				String wsurl = (String) data.getSessionData(Constants.S_FWS_POLICYLOOKUP_URL);
				data.addToLog(currElementName, " S_FWS_POLICYLOOKUP_URL : "+wsurl);
				if(null != data.getSessionData(Constants.S_POLICY_NUM) && !((String)data.getSessionData(Constants.S_POLICY_NUM)).isEmpty()) {
					policyContractNumber = (String) data.getSessionData(Constants.S_POLICY_NUM);
					startsWithAlphabets = Character.isLetter(policyContractNumber.charAt(0)); 

					if (startsWithAlphabets) {
						data.addToLog(currElementName, "The string starts with an alphabet.");
						policyContractNumber= policyContractNumber.substring(1,policyContractNumber.length());
			        	data.addToLog(currElementName, "Post Sub String :: " + policyContractNumber);
			        	data.setSessionData(Constants.S_POLICY_NUM, policyContractNumber);
			        } else {
			        	data.addToLog(currElementName, "The string does not start with an alphabet.");
			        }
				}
				String billingAccountNumber = (String) data.getSessionData(Constants.S_BILLING_ACC_NUM);
				String telephoneNumber = (String) data.getSessionData(Constants.S_ANI);
				if(data.getSessionData(Constants.S_TELEPHONENUMBER) != null && !data.getSessionData(Constants.S_TELEPHONENUMBER).equals("")) {
					telephoneNumber = (String) data.getSessionData(Constants.S_TELEPHONENUMBER);
				}
				String connTimeout = (String) data.getSessionData(Constants.S_CONN_TIMEOUT);
				String readTimeout = (String) data.getSessionData(Constants.S_READ_TIMEOUT);
				String  tid = (String) data.getSessionData(Constants.S_CALLID);
				LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
				data.addToLog(currElementName, "S_POLICY_NUM : "+policyContractNumber+" :: S_BILLING_ACC_NUM : "+billingAccountNumber+" :: S_ANI : "+telephoneNumber);
				Lookupcall lookups = new Lookupcall();
				org.json.simple.JSONObject responses = null;
				//UAT ENV CHANGE START
				data.addToLog("API URL: ", wsurl);
				String prefix = "https://api-np-ss.farmersinsurance.com"; 
				String UAT_FLAG="";
		        if(wsurl.startsWith(prefix)) {
		        	UAT_FLAG="YES";
		        }
				if("YES".equalsIgnoreCase(UAT_FLAG)) {
				 region = regionDetails.get(Constants.S_FWS_POLICYLOOKUP_URL);
				}else {
					region="PROD";
				}
				
				if(policyContractNumber != null && !policyContractNumber.isEmpty()) {
					responses = lookups.GetFWSPolicyLookup(wsurl,tid, policyContractNumber,null,null,Integer.parseInt(connTimeout), Integer.parseInt(readTimeout),context,region,UAT_FLAG);
				} else if(billingAccountNumber != null && !billingAccountNumber.equals("")){
					 responses = lookups.GetFWSPolicyLookup(wsurl,tid, null,billingAccountNumber,null,Integer.parseInt(connTimeout), Integer.parseInt(readTimeout),context,region,UAT_FLAG);
				}else {
					 responses = lookups.GetFWSPolicyLookup(wsurl,tid, null,null,telephoneNumber,Integer.parseInt(connTimeout), Integer.parseInt(readTimeout),context,region,UAT_FLAG);
				}
				data.addToLog("responses", responses.toString());
				
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) responses.get(Constants.RESPONSE_CODE));

				if(responses != null) 
				{
					if(responses.containsKey(Constants.REQUEST_BODY)) strReqBody = responses.get(Constants.REQUEST_BODY).toString();
					if(responses.containsKey(Constants.RESPONSE_CODE) && (int) responses.get(Constants.RESPONSE_CODE) == 200 && responses.containsKey(Constants.RESPONSE_BODY)) {
						data.addToLog(currElementName, "Set SIDA_HOST_002 : FWSPolicyLookup API Response into session with the key name of "+currElementName+Constants._RESP);
						JSONObject respbody=(JSONObject) responses.get(Constants.RESPONSE_BODY);
						//Zip Code fix 
						if(!respbody.containsKey("transactionNotification")) {
							data.setSessionData("SIDA_MN_005_VALUE_RESP", responses.get(Constants.RESPONSE_BODY));
							data.addToLog(currElementName, "Set SIDA_MN_005_VALUE_RESP: "+"SIDA_MN_005_VALUE_RESP");
						}
						
						strRespBody = responses.get(Constants.RESPONSE_BODY).toString();
						data.setSessionData(currElementName+Constants._RESP, responses.get(Constants.RESPONSE_BODY));						
						strExitState = apiResponseManupulation_FwsPolicyLookupNew(data, caa, currElementName, strRespBody);
						if(Constants.SU.equalsIgnoreCase(strExitState)) {
							data.setSessionData(Constants.S_BU, "FWS_SVC");
							data.setSessionData("S_FLAG_BW_BU", Constants.STRING_NO);
							data.setSessionData("S_FLAG_FDS_BU", Constants.STRING_NO);
							data.setSessionData("S_FLAG_FOREMOST_BU", Constants.STRING_NO);
							data.setSessionData("S_FLAG_FWS_BU", Constants.STRING_YES);
							data.setSessionData("S_FLAG_21ST_BU", Constants.STRING_NO);
							
							String menuSelectionKey = (String) data.getSessionData(Constants.S_MENU_SELCTION_KEY);
							//This check condition has been added because LOB can change to FWS in Shared ID Auth if Original BU is FDS
							if (null != menuSelectionKey && !menuSelectionKey.isEmpty() && menuSelectionKey.contains("FARMERS") && null != data.getSessionData(Constants.S_BU) && Constants.FWS.equalsIgnoreCase((String) data.getSessionData(Constants.S_BU)) && null != data.getSessionData("S_FLAG_FWS_BU") && Constants.STRING_YES.equalsIgnoreCase((String) data.getSessionData("S_FLAG_FWS_BU"))) {
								
								if(menuSelectionKey.startsWith("NLU")) {
									data.setSessionData(Constants.S_CATEGORY, "FWS_SVC");
									int colonIndex = menuSelectionKey.indexOf(':');

							        String result = "";
							        if (colonIndex != -1) {
							            // Preserve the part before colon and append the new string after colon
							            result = menuSelectionKey.substring(0, colonIndex + 1) + "FWS_SVC";
							        }
							        data.setSessionData(Constants.S_MENU_SELCTION_KEY, result);
							        data.addToLog(currElementName, "Replacing MSP From FARMERS to FWS Since BU switched in Shared ID & Auth :: New MSP ::  " +result);
								}else {
									menuSelectionKey = menuSelectionKey.replaceFirst("FARMERS", "FWS");
								   data.setSessionData(Constants.S_MENU_SELCTION_KEY, menuSelectionKey);
								   data.addToLog(currElementName, "Replacing MSP From FARMERS to FWS Since BU switched in Shared ID & Auth :: New MSP :: " +menuSelectionKey);
								}
								menuSelectionKey = menuSelectionKey.replaceFirst("FARMERS", "FWS");
								data.setSessionData(Constants.S_MENU_SELCTION_KEY, menuSelectionKey);
								data.addToLog(currElementName, "Replacing MSP From FARMERS to FWS Since BU switched in Shared ID & Auth :: New MSP :: " +menuSelectionKey);
							}
							//Replacing all the previously formed MSPs (BU - FARMERS) with the new BU(FWS)
							Queue<String> MSPQueue = (Queue<String>) data.getSessionData("S_LAST_5_MSP_VALUES");
							data.addToLog(currElementName, "MSP Queue Before BU was switched :: " +MSPQueue);
							
							Queue<String> tempQueue = new LinkedList<>();
							if (null != MSPQueue) {
								Iterator<String> iterator = MSPQueue.iterator();
						        data.addToLog(currElementName, "Iterating through the current Queue while replacing old BU (FARMERS) to new BU (FWS)");
						        while (iterator.hasNext()) { 
						        	data.setSessionData(Constants.S_CATEGORY, "FWS_SVC");
						        	data.addToLog("New Category code: ", (String)data.getSessionData(Constants.S_CATEGORY));
						        	String oldMSPEntry= iterator.next();
						        	data.addToLog(currElementName, "Old MSP entry " +oldMSPEntry);
						            //String tempKey = oldMSPEntry.replaceFirst("FARMERS", "FWS");
						            
						          	 String tempKey="";
							        	if(oldMSPEntry.startsWith("NLU")) {
											int colonIndex = oldMSPEntry.indexOf(':');
									       
									        if (colonIndex != -1) {
									            // Preserve the part before colon and append the new string after colon
									        	tempKey = oldMSPEntry.substring(0, colonIndex + 1) + "FWS_SVC";
									        }
										}else {
											tempKey = oldMSPEntry.replaceFirst("FARMERS", "FWS");
										}
						            data.addToLog(currElementName, "New MSP entry :: " +tempKey);
						            tempQueue.add(tempKey);
						        }
						        MSPQueue = tempQueue;
						        data.setSessionData("S_LAST_5_MSP_VALUES", MSPQueue);
						        data.addToLog(currElementName, "New MSP Queue post replacing the Old BU with New one :: " + data.getSessionData("S_LAST_5_MSP_VALUES"));
							}
						}
					} else {
						strRespBody = responses.get(Constants.RESPONSE_MSG).toString();
					}
					
				}
			}
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in SIDA_HOST_002  FWSPolicyLookup API call  :: "+e);
			caa.printStackTrace(e);
		}
		
		try {
			if (null != data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE) && (Constants.S_SINGLE_POLICY_FOUND.equalsIgnoreCase((String) data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE)) || Constants.S_MULTIPLE_POLICIES_SINGLE_PRODUCTTYPE_FOUND.equalsIgnoreCase((String) data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE)) || Constants.S_MULTIPLE_POLICIES_MULTIPLE_PRODUCTTYPE_FOUND.equalsIgnoreCase((String) data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE)))) {
				objHostDetails.startHostReport(currElementName,"FWSPolicyLookup API call", strReqBody, region,(String) data.getSessionData(Constants.S_FWS_POLICYLOOKUP_URL));
				objHostDetails.endHostReport(data,strRespBody , strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
			}
			else {
				objHostDetails.startHostReport(currElementName,"FWSPolicyLookup API call", strReqBody, region,(String) data.getSessionData(Constants.S_FWS_POLICYLOOKUP_URL));
				objHostDetails.endHostReport(data, "DB-mismatch : " + strRespBody , strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
			}
			
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for FWSPolicyLookup API call  :: "+e);
			caa.printStackTrace(e);
		}
		
		
		return strExitState;
	}
	
	private String apiResponseManupulation_FwsPolicyLookupNew(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {			
		String strExitState = Constants.ER;
		try {
			
			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			JSONArray policiesArr = (JSONArray)resp.get("policies");
		
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
			
			if(policiesArr != null && policiesArr.size() > 0) {
				data.setSessionData(Constants.S_NOOF_POLICIES_LOOKUP, policiesArr.size());
				data.setSessionData(Constants.IS_MORETHAN_ZERO_POLICIES, Constants.STRING_YES);
				Map<String, Integer> productTypeCounts = new HashMap<>();
				HashMap<String, HashMap<String, PolicyBean>> productPolicyMap = new HashMap<String, HashMap<String,PolicyBean>>();
				for(int i = 0; i < policiesArr.size(); i++) {
					PolicyBean beanObj = new PolicyBean();
					String strPolicyNum = Constants.EmptyString;
					JSONObject policyData = (JSONObject) policiesArr.get(i);
					
					if (policyData.containsKey("lineOfBusiness") && null != policyData.get("lineOfBusiness")) {
						lob = (String) policyData.get("lineOfBusiness");
						data.setSessionData(Constants.S_FWS_POLICY_LOB, lob);
						beanObj.setStrPolicyLOB(lob);
						if (productTypeCounts.containsKey(lob)) productTypeCounts.put(lob, productTypeCounts.get(lob) + 1);
						else productTypeCounts.put(lob, 1);
					}
					String zip = "";
					if (policyData.containsKey("addresses") && null != policyData.get("addresses")) {
						JSONArray addressesArr = (JSONArray)policyData.get("addresses");
						JSONObject addressesObj = (JSONObject) addressesArr.get(0);
						if (addressesObj.containsKey("zip")) {
							zip = zip.equals("")?""+addressesObj.get("zip").toString().substring(0, 5) : zip+","+addressesObj.get("zip").toString().substring(0, 5);
							//zip = (String) addressesObj.get("zip");
						}
						beanObj.setStrZipCode(zip);
						if (addressesObj.containsKey("state")) beanObj.setStrPolicyState((String)addressesObj.get("state"));
					}
					if (policyData.containsKey("insuredDetails")) {
						
						String strDOB = "";
						JSONArray insuredDetailsArr = (JSONArray)policyData.get("insuredDetails");
						for(int k =0;k<insuredDetailsArr.size();k++) {
							JSONObject obj =(JSONObject)insuredDetailsArr.get(k);
							strDOB = (strDOB.equals(""))?""+obj.get("birthDate"):strDOB+","+obj.get("birthDate");
						}
						beanObj.setStrDOB(strDOB);
						
						JSONObject insuredDetailsObj =(JSONObject)insuredDetailsArr.get(0);
						if(insuredDetailsObj.containsKey("firstName") && null != insuredDetailsObj.get("firstName")) beanObj.setStrFirstName((String)insuredDetailsObj.get("firstName"));
						if(insuredDetailsObj.containsKey("lastName") && null != insuredDetailsObj.get("lastName")) beanObj.setStrLastName((String)insuredDetailsObj.get("lastName"));
					}
					if (policyData.containsKey("policyNumber") && null != policyData.get("policyNumber")) {
						strPolicyNum = (String) policyData.get("policyNumber");
						beanObj.setStrPolicyNum(strPolicyNum);
					}
					
					if(policyData.containsKey("policySource") && null != policyData.get("policySource")) beanObj.setStrPolicySource((String)policyData.get("policySource"));
					if(policyData.containsKey("policyState") && null != policyData.get("policyState")) beanObj.setStrPolicyState((String)policyData.get("policyState"));
					if(policyData.containsKey("suffix") && null != policyData.get("suffix")) beanObj.setStrPolicySuffix((String)policyData.get("suffix"));
					if(policyData.containsKey("effectiveDate") && null != policyData.get("effectiveDate")) beanObj.setStrEffectiveDate((String)policyData.get("effectiveDate"));
					if(policyData.containsKey("InternalPolicyVersion") && null != policyData.get("InternalPolicyVersion")) beanObj.setStrInternalPolicyNumber((String)policyData.get("InternalPolicyVersion"));
					if(policyData.containsKey("InternalPolicyNumber") && null != policyData.get("InternalPolicyNumber")) beanObj.setStrInternalPolicyVersion((String)policyData.get("InternalPolicyNumber"));
					if(policyData.containsKey("billingAccountNumber") && null != policyData.get("billingAccountNumber")) {
						String tmpBillingAccNum = (String)policyData.get("billingAccountNumber");
						if("ARS".equalsIgnoreCase(beanObj.getStrPolicySource()) && tmpBillingAccNum.length() > 9) {
							beanObj.setStrCompanyCode(tmpBillingAccNum.substring(0, 2));
							beanObj.setStrBillingAccountNumber(tmpBillingAccNum.substring(2, tmpBillingAccNum.length()));
						} else {
							beanObj.setStrCompanyCode(tmpBillingAccNum.substring(0, 2));
							beanObj.setStrBillingAccountNumber(tmpBillingAccNum.substring(0, tmpBillingAccNum.length()));
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
					if(productPolicyMap.containsKey(tmpKey)) {
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
				data.addToLog(currElementName, "Product Type Count Hashmap : "+productTypeCounts);
				data.addToLog(currElementName, "policyDetails Hashmap : "+productPolicyMap);

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
				

				data.addToLog(currElementName,"Auto Product Type Count  = "+data.getSessionData(Constants.AUTO_PRODUCTTYPECOUNT_KYC));
				data.addToLog(currElementName,"Home Product Type Count  = "+data.getSessionData(Constants.HOME_PRODUCTTYPECOUNT_KYC));
				data.addToLog(currElementName,"Umbrella Product Type Count = "+data.getSessionData(Constants.UMBRELLA_PRODUCTTYPECOUNT_KYC));
				data.addToLog(currElementName,"Boat Product Type Count  = "+data.getSessionData(Constants.MR_PRODUCTTYPECOUNT_KYC));

				singleproducttype = checkifsingleproducttype(0, autoprodtypecount, homeprodtypecount, umbrellaprodtypecount, mrprodtypecount, 0);
				data.addToLog(currElementName, "Check if single Product : "+singleproducttype);

				if (policiesArr.size() == 1) {
					data.setSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE, Constants.S_SINGLE_POLICY_FOUND);
					data.addToLog(currElementName,"S_NOOF_POLICIES_ANILOOKUP_EXITSTATE : "+Constants.S_SINGLE_POLICY_FOUND);
					for (String key : productPolicyMap.keySet()) {
						HashMap<String, PolicyBean> policyDetailsMap = productPolicyMap.get(key);
						for (String key1 : policyDetailsMap.keySet()) {
							PolicyBean obj = (PolicyBean) policyDetailsMap.get(key1);
							data.setSessionData(Constants.S_API_DOB, obj.getStrDOB());
							data.setSessionData(Constants.S_API_ZIP, obj.getStrZipCode());
							data.setSessionData(Constants.S_POLICY_NUM, key1);
							data.setSessionData(Constants.S_POLICY_STATE_CODE, obj.getStrPolicyState());
							data.setSessionData(Constants.S_POLICY_SOURCE, obj.getStrPolicySource());
							data.setSessionData(Constants.S_FINAL_POLICY_OBJ, obj);
							
							data.setSessionData("ID_AUTH_SELECTED_PRODUCTTYPE", lob);
							data.setSessionData("S_EPC_PAYMENT_POLICYNUM", obj.getStrBillingAccountNumber());
						}
					}
					if (policiesArr.size() == 1) {

						if (lob.equalsIgnoreCase("A")) {
							prompt = "Auto policy";
							data.setSessionData(Constants.FINAL_PRODUCTTYPE, lob);
						} else if (lob.equalsIgnoreCase("H")||lob.equalsIgnoreCase("F")) {
							prompt = "Home policy";
							data.setSessionData(Constants.FINAL_PRODUCTTYPE, lob);
						} else if (lob.equalsIgnoreCase("MR")||lob.equalsIgnoreCase("Y")) {
							prompt = "Boat policy";
							data.setSessionData(Constants.FINAL_PRODUCTTYPE, lob);
						}else if (lob.equalsIgnoreCase("U")) {
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
					strExitState = Constants.SU;
				} else if (!singleproducttype) {
					if(autoprodtypecount >= 1) prompt = prompt+" an Auto policy"; 
					if (homeprodtypecount >= 1) prompt = prompt+" a Home Policy"; 
					if (mrprodtypecount >= 1) prompt = prompt+" a Boat Policy"; 
					if (umbrellaprodtypecount >= 1) prompt = prompt+" or an Umbrella Policy"; 
					String lang = (String) data.getSessionData("S_PREF_LANG");
					
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("an auto policy")) {
						prompt = prompt.replace(" an Auto policy"," una póliza de auto");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("a home policy")) {
						prompt = prompt.replace(" a Home Policy"," una póliza de casa");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("or an umbrella policy")) {
						prompt = prompt.replace(" or an Umbrella Policy"," o una póliza de paraguas");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("a boat policy")) {
						prompt = prompt.replace(" a Boat Policy"," una póliza de barco");
					}
					if (prompt.contains(" ")) {
						prompt = prompt.replaceAll(" ", ".");
					} 
					

					data.setSessionData(Constants.VXMLParam1, prompt);
					data.setSessionData(Constants.VXMLParam2, "NA");
					data.setSessionData(Constants.VXMLParam3, "NA");
					data.setSessionData(Constants.VXMLParam4, "NA");
					data.addToLog(currElementName,"Multiple Product Types Scenario");
					data.addToLog(currElementName,"Policies Dynamic Product Type Prompts = "+prompt.toString());
					data.addToLog(currElementName,"VXMLPARAM1 = "+data.getSessionData(Constants.VXMLParam1));
					
					data.setSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE, Constants.S_MULTIPLE_POLICIES_MULTIPLE_PRODUCTTYPE_FOUND);
					data.addToLog(currElementName,"S_NOOF_POLICIES_ANILOOKUP_EXITSTATE : "+data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE));
					
					strExitState = Constants.SU;
				} else if (policiesArr.size() > 1 && singleproducttype) {
					String produtType = "";
					if(autoprodtypecount >= 1) {
						prompt = " Auto "; 
						produtType = Constants.AUTO_PRODUCTTYPECOUNT_KYC;
					}
					else if (homeprodtypecount >= 1) {
						prompt = " Home "; 
						produtType = Constants.HOME_PRODUCTTYPECOUNT_KYC;
					}
					else if (umbrellaprodtypecount >= 1) {
						prompt = " an Umbrella ";
						produtType = Constants.UMBRELLA_PRODUCTTYPECOUNT_KYC;
					}
					else if(mrprodtypecount >= 1) {
						prompt = " Boat ";
						produtType = Constants.MR_PRODUCTTYPECOUNT_KYC;
					}
					
					String lang = (String) data.getSessionData("S_PREF_LANG");
					
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("auto")) {
						prompt = prompt.replace(" Auto "," auto ");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("home")) {
						prompt = prompt.replace(" Home "," casa ");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("umbrella")) {
						prompt = prompt.replace(" an Umbrella "," paraguas ");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("boat")) {
						prompt = prompt.replace(" Boat "," barco ");
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
					data.addToLog(currElementName,"Policies Dynamic Product Type Prompts = "+prompt.toString());
					data.addToLog(currElementName,"VXMLPARAM1 = "+data.getSessionData(Constants.VXMLParam1));
					
					data.setSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE, Constants.S_MULTIPLE_POLICIES_SINGLE_PRODUCTTYPE_FOUND);
					data.addToLog(currElementName,"S_NOOF_POLICIES_ANILOOKUP_EXITSTATE : "+data.getSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP_EXITSTATE));
					
					strExitState = Constants.SU;
				}
			} else {
				strExitState = Constants.ER;
			}
	} catch (Exception e) {
		data.addToLog(currElementName,"Exception in AccLinkAniLookup method  :: "+e);
		caa.printStackTrace(e);
	}
	return strExitState;
}
	
	public static void main(String[] args) {
		 Queue<String> MSPQueue = new LinkedList<>();
		 MSPQueue.add("FARMERS:OSPM_MN_001:POLICY ASSISTANCE");
		 MSPQueue.add("FARMERS:OSPM_MN_005:POLICY YES");
		 MSPQueue.add("FARMERS:OSPM_MN_005:POLICY YES");

		 
		 Queue<String> tempReplacement = new LinkedList<>();
		 
		 
	        // Iterate using iterator
	        Iterator<String> iterator = MSPQueue.iterator();
	        while (iterator.hasNext()) { 
	            String tempKey = iterator.next().replaceFirst("FARMERS", "FWS");
	            tempReplacement.add(tempKey);
	        }
	        
	        MSPQueue = tempReplacement;

	        System.out.println(MSPQueue);
	}
	
}
