package com.farmers.host;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.FarmersAPI.AccountLinkAniLookup_Post;
import com.farmers.FarmersAPI_NP.AccountLinkAniLookup_NP_Post;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;


public class AccLinkAniLookup extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String strExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);
		String strReqBody = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		
		//START- UAT ENV SETUP CODE (PRIYA, SHAIK)
		String region=null;
        HashMap<String,String> regionDetails = (HashMap<String, String>) data.getSessionData(Constants.RegionHM);
        //END- UAT ENV SETUP CODE (PRIYA, SHAIK)

		try {
			
			if(null != data.getSessionData("S_ACCLINKANI_CALLED") && "TRUE".equalsIgnoreCase((String)data.getSessionData("S_ACCLINKANI_CALLED"))) {
				data.addToLog(currElementName, "AccountLinkANI is already called :: Response from session :: "+(String)data.getSessionData(Constants.ACCLINKANIJSONRESPSTRING));
				String resp = (String)data.getSessionData(Constants.ACCLINKANIJSONRESPSTRING);
				strExitState = apiResponseManupulation(data, caa, currElementName, resp);
				
				if (strExitState.equalsIgnoreCase(Constants.SU)) {
					data.setSessionData("S_ANI_MATCH", "TRUE");
				}
				return strExitState;
			}
			
			String dnisbrand = (String) data.getSessionData(Constants.S_BU);
			String strBristolCode = (String)data.getApplicationAPI().getApplicationData("A_KYCBA_BRISTOLWEST_LOB");
			String strFarmersCode = (String)data.getApplicationAPI().getApplicationData("A_FARMERS_LOB");
			String strForemostCode = (String)data.getApplicationAPI().getApplicationData("A_KYCBA_FOREMOST_LOB");
			String strFWSCode = (String)data.getApplicationAPI().getApplicationData("A_KYCBA_FWS_LOB");
			String str21stCode = (String)data.getApplicationAPI().getApplicationData("A_KYCBA_21ST_LOB");


			if (dnisbrand!=null && strFarmersCode!=null && (strFarmersCode.toLowerCase().contains(dnisbrand.toLowerCase()))) {
				dnisbrand = "PLA || GWPC";
				//data.setSessionData("S_FLAG_FDS_BU", Constants.STRING_YES);
			}else if (dnisbrand!=null && strBristolCode!=null && strBristolCode.toLowerCase().contains(dnisbrand.toLowerCase())) {
				dnisbrand = "BW";
				//data.setSessionData("S_FLAG_BW_BU", Constants.STRING_YES);
			}
			else if (dnisbrand!=null && strForemostCode!=null && strForemostCode.toLowerCase().contains(dnisbrand.toLowerCase())) {
				dnisbrand = "FM";
				//data.setSessionData("S_FLAG_FOREMOST_BU", Constants.STRING_YES);
			}
			else if (dnisbrand!=null && strFWSCode!=null && strFWSCode.toLowerCase().contains(dnisbrand.toLowerCase())) {
				dnisbrand = "FWS";
				//data.setSessionData("S_FLAG_FWS_BU", Constants.STRING_YES);
			}
			else if (dnisbrand!=null && str21stCode!=null && str21stCode.toLowerCase().contains(dnisbrand.toLowerCase())) {
				dnisbrand = "AUTO";
				//data.setSessionData("S_FLAG_21ST_BU", Constants.STRING_YES);
			}
		}  catch (Exception e) {
			data.addToLog(currElementName,"Exception in setting falgs for BU  :: "+e);
			caa.printStackTrace(e);
		}

		try {
			
			//START : CS1173483: Separate TimeOut Configurations For AccLinkAniLookup API
			
			if(data.getSessionData(Constants.S_ACCLINK_ANILOOKUP_URL) != null 
					&& data.getSessionData(Constants.S_ANILOOKUP_CONN_TIMEOUT) != null && data.getSessionData(Constants.S_ANILOOKUP_READ_TIMEOUT) != null) {
				
				String url = (String) data.getSessionData(Constants.S_ACCLINK_ANILOOKUP_URL);
				String tid = (String) data.getSessionData(Constants.S_CALLID);
				String telephonenumber = (String) data.getSessionData(Constants.S_ANI);
				//For testing
				//String telephonenumber = "3604386708";
				
				
		        int conTimeout = data.getSessionData(Constants.S_ANILOOKUP_CONN_TIMEOUT) != null 
			            ? Integer.valueOf((String) data.getSessionData(Constants.S_ANILOOKUP_CONN_TIMEOUT)) 
			            : 12000; // default connection timeout

			        int readTimeout = data.getSessionData(Constants.S_ANILOOKUP_READ_TIMEOUT) != null 
			            ? Integer.valueOf((String) data.getSessionData(Constants.S_ANILOOKUP_READ_TIMEOUT)) 
			            : 12000; // default read timeout
				
				data.addToLog(currElementName, "AccLinkAniLookup ConnTimeOut :"+conTimeout);
				data.addToLog(currElementName, "AccLinkAniLookup ReadTimeOut :"+readTimeout);
				
				//END : CS1173483: Separate TimeOut Configurations For AccLinkAniLookup API

				LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
				AccountLinkAniLookup_Post obj = new AccountLinkAniLookup_Post();
				//START- Non prod changes-Priya
				data.addToLog("API URL: ", url);
				String prefix = "https://api-np-ss.farmersinsurance.com"; 
				String UAT_FLAG="";
		        if(url.startsWith(prefix)) {
		        	UAT_FLAG="YES";
		        }
				AccountLinkAniLookup_NP_Post objNP = new AccountLinkAniLookup_NP_Post();
				JSONObject resp=null ;
				if("YES".equalsIgnoreCase(UAT_FLAG)) {
					data.addToLog("S_ACCLINK_ANILOOKUP_URL: ", url);
					String Key =Constants.S_ACCLINK_ANILOOKUP_URL;
					region = regionDetails.get(Key);
					data.addToLog("Region for UAT endpoint: ", region);
					resp = objNP.start(url, tid, telephonenumber, conTimeout, readTimeout, context, region);
				}else {
					region="PROD";
				    resp = obj.start(url, tid, telephonenumber, conTimeout, readTimeout, context);
				}
				//END-Non prod changes-Priya
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
				if(resp != null) {
					if(resp.containsKey(Constants.REQUEST_BODY)) strReqBody = resp.get(Constants.REQUEST_BODY).toString();
					if(resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200 && resp.containsKey(Constants.RESPONSE_BODY)) {
						data.addToLog(currElementName, "Set AccLinkAniLookup  Response into session with the key name of "+currElementName+Constants._RESP);
						strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
						data.setSessionData(currElementName+Constants._RESP, resp.get(Constants.RESPONSE_BODY));
						data.setSessionData("S_ACCLINKANI_CALLED","TRUE");
						data.setSessionData(Constants.ACCLINKANIJSONRESPSTRING, strRespBody);
						data.addToLog(currElementName, "ACCLINKANIRESP String : "+data.getSessionData(Constants.ACCLINKANIJSONRESPSTRING));
						strExitState = apiResponseManupulation(data, caa, currElementName, strRespBody);

					} else {
						if(resp.get(Constants.RESPONSE_MSG)!=null)
							strRespBody = resp.get(Constants.RESPONSE_MSG).toString();
					}

				}
			}
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in AccLinkAniLookup call  :: "+e);
			caa.printStackTrace(e);
		}

		try {
			objHostDetails.startHostReport(currElementName,"AccLinkAniLookupAPI Response", strReqBody, region,(String) data.getSessionData(Constants.S_ACCLINK_ANILOOKUP_URL));
			objHostDetails.endHostReport(data,strRespBody , strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for AccLinkAniLookup call  :: "+e);
			caa.printStackTrace(e);
		}
		/*
		String hostMSPEndKey = Constants.EmptyString;
		if(strExitState.equalsIgnoreCase(Constants.ER)) hostMSPEndKey = Constants.API_FAILURE;
		data.setSessionData(Constants.S_MENU_SELCTION_KEY, data.getSessionData(Constants.S_BU)+":KYCNP_HOST_001:"+hostMSPEndKey);
		data.addToLog(currElementName,"S_MENU_SELCTION_KEY for  KYCNP_HOST_001 :: "+data.getSessionData(Constants.S_MENU_SELCTION_KEY));
		 */
		return strExitState;
	}

	private String apiResponseManupulation(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {
		String strExitState = Constants.ER;
		try {
			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			JSONObject resultsObj = (JSONObject) resp.get("results");
			JSONArray policiesArr = (JSONArray)resultsObj.get("policies");
			JSONArray claimsArr = (JSONArray)resultsObj.get("claims");
			String lob = null;
			String policytype = null;
			String prompt = null;
			int rvprodtypecount = 0;
			int autoprodtypecount = 0;
			int homeprodtypecount = 0;
			int umbrellaprodtypecount = 0;
			int mrprodtypecount = 0;
			int spprodtypecount = 0;	
			boolean singleproducttype = false;
			if(claimsArr != null && claimsArr.size() > 0) data.setSessionData(Constants.S_ACTIVE_CLAIM_AVAILABLE, Constants.STRING_YES); 
			if(policiesArr != null && policiesArr.size() > 0) {
				data.setSessionData(Constants.S_NOOF_POLICIES_ANILOOKUP, policiesArr.size());
				data.setSessionData(Constants.IS_MORETHAN_ZERO_POLICIES, Constants.STRING_YES);
				if(policiesArr.size() == 1) {
					data.setSessionData(Constants.S_IS_SINGLE_POLICIY, Constants.STRING_YES);
					JSONObject policy = (JSONObject) policiesArr.get(0);
					lob = (String) policy.get("lineOfBusiness");
					String brand = (String) policy.get("policySource");
					String policynumber = (String) policy.get("policyNumber");
					policytype = (String) policy.get("PolicyType");
					data.addToLog(currElementName, "Value of PolicyType : " + policytype);
					data.setSessionData(Constants.FINALPOLICYOBJECT_KYCNP, policy.toString());
					data.setSessionData(Constants.S_FINAL_BRAND, brand);
					data.addToLog(currElementName, "Selected Policy Brand : "+data.getSessionData(Constants.S_FINAL_BRAND));
					data.setSessionData(Constants.S_POLICY_NUM, policynumber);
					data.setSessionData(Constants.S_FULL_POLICY_NUM, policynumber);
					data.setSessionData("POLICY_TYPE", policytype);
					data.addToLog(currElementName, "Selected Policy Number : "+data.getSessionData(Constants.S_POLICY_NUM));
				}
				Map<String, Integer> productTypeCounts = new HashMap<>();

				if(currElementName.equalsIgnoreCase("KYCMF_HOST_004") || currElementName.equalsIgnoreCase("KYCMF_HOST_003")) return Constants.SU;

				for(int i = 0; i < policiesArr.size(); i++) {
					JSONObject policydata = (JSONObject) policiesArr.get(i);
					if (policydata.containsKey("lineOfBusiness")) {
						lob = (String) policydata.get("lineOfBusiness");
						policytype = (String) policydata.get("PolicyType");
						if (productTypeCounts.containsKey(lob)) {
							productTypeCounts.put(lob, productTypeCounts.get(lob) + 1);
						}

						else {
							productTypeCounts.put(lob, 1);
						}
					}

					if (policydata.containsKey("insuredDetails")) {
						JSONObject insuredDetailsObj = (JSONObject) policydata.get("insuredDetails");
						if (insuredDetailsObj.containsKey("postalAddress")) {
							JSONObject postalAddressObj = (JSONObject) insuredDetailsObj.get("postalAddress");
							if(postalAddressObj.containsKey("PostalCode") && null != postalAddressObj.get("PostalCode")) {
								String strPostalCode = (String) postalAddressObj.get("PostalCode");
								data.addToLog(currElementName, "PostalCode : "+strPostalCode);
								if(!strPostalCode.isEmpty()) {
									data.setSessionData(Constants.S_API_ZIP, strPostalCode);
								}
							}
						}
						data.addToLog(currElementName, "PostalCode : "+data.getSessionData(Constants.S_API_ZIP));

					}
				}
				data.addToLog(currElementName, "PostalCode : "+data.getSessionData(Constants.S_API_ZIP));
				data.addToLog(currElementName, "Product Type Count Hashmap : "+productTypeCounts);

				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_RV)) {
					rvprodtypecount = productTypeCounts.get(Constants.PRODUCTTYPE_RV);
				}
				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_SP)) {
					spprodtypecount = productTypeCounts.get(Constants.PRODUCTTYPE_SP);
				}
				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_MR)) {
					mrprodtypecount = productTypeCounts.get(Constants.PRODUCTTYPE_MR);
				}
				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_HOME)) {
					homeprodtypecount = productTypeCounts.get(Constants.PRODUCTTYPE_HOME);
				}
				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_AUTO)) {
					autoprodtypecount = productTypeCounts.get(Constants.PRODUCTTYPE_AUTO);
				}
				if (productTypeCounts.containsKey(Constants.PRODUCTTYPE_UMBRELLA)) {
					umbrellaprodtypecount = productTypeCounts.get(Constants.PRODUCTTYPE_UMBRELLA);
				}

				data.setSessionData(Constants.RV_PRODUCTTYPECOUNT_KYC, rvprodtypecount);
				data.setSessionData(Constants.AUTO_PRODUCTTYPECOUNT_KYC, autoprodtypecount);
				data.setSessionData(Constants.HOME_PRODUCTTYPECOUNT_KYC, homeprodtypecount);
				data.setSessionData(Constants.UMBRELLA_PRODUCTTYPECOUNT_KYC, umbrellaprodtypecount);
				data.setSessionData(Constants.MR_PRODUCTTYPECOUNT_KYC, mrprodtypecount);
				data.setSessionData(Constants.SP_PRODUCTTYPECOUNT_KYC, spprodtypecount);

				data.addToLog(currElementName,"Motorcycle or Motor Home Product Type Count  = "+data.getSessionData(Constants.RV_PRODUCTTYPECOUNT_KYC));
				data.addToLog(currElementName,"Auto Product Type Count  = "+data.getSessionData(Constants.AUTO_PRODUCTTYPECOUNT_KYC));
				data.addToLog(currElementName,"Home Product Type Count  = "+data.getSessionData(Constants.HOME_PRODUCTTYPECOUNT_KYC));
				data.addToLog(currElementName,"Umbrella Product Type Count = "+data.getSessionData(Constants.UMBRELLA_PRODUCTTYPECOUNT_KYC));
				data.addToLog(currElementName,"Boat Product Type Count  = "+data.getSessionData(Constants.MR_PRODUCTTYPECOUNT_KYC));
				data.addToLog(currElementName,"Specialty Dwelling or Mobile Home Product Type Count  = "+data.getSessionData(Constants.SP_PRODUCTTYPECOUNT_KYC));

				singleproducttype = checkifsingleproducttype(rvprodtypecount, autoprodtypecount, homeprodtypecount, umbrellaprodtypecount, mrprodtypecount, spprodtypecount);
				data.addToLog(currElementName, "Check if single Product : "+singleproducttype);

				if (policiesArr.size() == 1) {

					if (lob.equalsIgnoreCase("AUTO")) {
						prompt = "Auto policy";
						data.setSessionData(Constants.FINAL_PRODUCTTYPE, lob);
					}
					else if (lob.equalsIgnoreCase("HOME")) {
						prompt = "Home policy";
						data.setSessionData(Constants.FINAL_PRODUCTTYPE, lob);
					}
					else if (lob.equalsIgnoreCase("MR")) {
						prompt = "Boat policy";
						data.setSessionData(Constants.FINAL_PRODUCTTYPE, lob);
					}
					else if (lob.equalsIgnoreCase("RV")) {
						prompt = "Motor Home or Motorcycle Policy";
						data.setSessionData(Constants.FINAL_PRODUCTTYPE, lob);
					}
					else if (lob.equalsIgnoreCase("SP")) {
						prompt = "Specialty Dwelling or Mobile Home Policy";
						data.setSessionData(Constants.FINAL_PRODUCTTYPE, lob);
					}
					else if (lob.equalsIgnoreCase("UMBRELLA")) {
						prompt = "Umbrella Policy";
						data.setSessionData(Constants.FINAL_PRODUCTTYPE, lob);
					}
 
					
					String lang = (String) data.getSessionData("S_PREF_LANG");
					
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("auto policy")) {
						prompt = "póliza de auto";
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("home policy")) {
						prompt = "póliza de casa";
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("boat policy")) {
						prompt = "póliza de barco";
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("motor home or motorcycle policy")) {
						prompt = "casa rodante o póliza de motocicleta";
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("specialty dwelling or mobile home policy")) {
						prompt = "vivienda especial o póliza de casa móvil";
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("umbrella policy")) {
						prompt = "póliza de paraguas";
					}

					if (prompt.contains(" ")) {
						prompt = prompt.replaceAll(" ", ".");
					} 


					//JSONObject policyobject = (JSONObject) policiesArr.get(0);
					//String policysource = (String) policyobject.get("policySource");
					//data.setSessionData("SinglePolicySource", policysource);

					data.setSessionData(Constants.VXMLParam1, prompt);
					data.setSessionData(Constants.VXMLParam2, "NA");
					data.setSessionData(Constants.VXMLParam3, "NA");
					data.setSessionData(Constants.VXMLParam4, "NA");
					data.addToLog(currElementName,"Single Policy Scenario");
					data.addToLog(currElementName,"KYC Number Of Policies Dynamic Product Type Prompts = "+prompt.toString());
					data.addToLog(currElementName,"VXMLPARAM1 = "+data.getSessionData(Constants.VXMLParam1));

					data.setSessionData("S_NOOF_POLICIES_ACCLINKANI_EXITSTATE", Constants.S_SINGLE_POLICY_FOUND);
					data.addToLog(currElementName,"S_NOOF_POLICIES_ACCLINKANI_EXITSTATE : "+data.getSessionData("S_NOOF_POLICIES_ACCLINKANI_EXITSTATE"));

					strExitState = Constants.SU;
				}

				else if (!singleproducttype) {

					if(autoprodtypecount >= 1) {
						prompt = "an Auto policy"; 
					}
					if (homeprodtypecount >= 1) {
						if (null != prompt) {
							prompt += " a Home Policy";
						}else {
							prompt = "a Home Policy";
						}
					}
					if (mrprodtypecount >= 1) {
						if (null != prompt) {
							prompt += " a Boat Policy";
						}else {
							prompt = "a Boat Policy";
						}
					}
					if (rvprodtypecount >= 1) {
						if (null != prompt) {
							prompt += " a Motor Home or Motorcycle Policy";
						}else {
							prompt = "a Motor Home or Motorcycle Policy";
						}
					}
					if (spprodtypecount >= 1) {
						if (null != prompt) {
							prompt += " a Specialty Dwelling or Mobile Home Policy";
						}else {
							prompt = "a Specialty Dwelling or Mobile Home Policy";
						}
					}
					if (umbrellaprodtypecount >= 1) {
						if (null != prompt) {
							prompt += " or an Umbrella Policy";
						}else {
							prompt = "an Umbrella Policy";
						}
					}

					
					
					String lang = (String) data.getSessionData("S_PREF_LANG");
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("an auto policy")) {
						prompt = prompt.replace("an Auto policy", " una póliza de auto");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("a home policy")) {
						prompt = prompt.replace(" a Home Policy", " una póliza de casa");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("a home policy")) {
						prompt = prompt.replace("a Home Policy", "una póliza de casa");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("a boat policy")) {
						prompt = prompt.replace(" a Boat Policy", " una póliza de barco");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("a boat policy")) {
						prompt = prompt.replace("a Boat Policy", "una póliza de barco");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("a motor home or motorcycle policy")) {
						prompt = prompt.replace(" a Motor Home or Motorcycle Policy", " una casa rodante o póliza de motocicleta");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("a motor home or motorcycle policy")) {
						prompt = prompt.replace("a Motor Home or Motorcycle Policy", "una casa rodante o póliza de motocicleta");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("a specialty dwelling or mobile home policy")) {
						prompt = prompt.replace(" a Specialty Dwelling or Mobile Home Policy", " una vivienda especial o póliza de casa móvil");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("a specialty dwelling or mobile home policy")) {
						prompt = prompt.replace("a Specialty Dwelling or Mobile Home Policy", "una vivienda especial o póliza de casa móvil");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("or an umbrella policy")) {
						prompt = prompt.replace(" or an Umbrella Policy", " o una póliza de paraguas");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("an umbrella policy")) {
						prompt = prompt.replace("an Umbrella Policy", " una póliza de paraguas");
					}
					
					if (prompt.contains(" ")) {
						prompt = prompt.replaceAll(" ", ".");
					} 

					data.setSessionData(Constants.VXMLParam1, prompt);
					data.setSessionData(Constants.VXMLParam2, "NA");
					data.setSessionData(Constants.VXMLParam3, "NA");
					data.setSessionData(Constants.VXMLParam4, "NA");
					data.addToLog(currElementName,"Multiple Product Types Scenario");
					data.addToLog(currElementName,"KYC Number Of Policies Dynamic Product Type Prompts = "+prompt.toString());
					data.addToLog(currElementName,"VXMLPARAM1 = "+data.getSessionData(Constants.VXMLParam1));

					data.setSessionData("S_NOOF_POLICIES_ACCLINKANI_EXITSTATE", Constants.S_MULTIPLE_POLICIES_MULTIPLE_PRODUCTTYPE_FOUND);
					data.addToLog(currElementName,"S_NOOF_POLICIES_ACCLINKANI_EXITSTATE : "+data.getSessionData("S_NOOF_POLICIES_ACCLINKANI_EXITSTATE"));

					strExitState = Constants.SU;
				}

				else if (policiesArr.size() > 1 && singleproducttype) {
					if (lob.equalsIgnoreCase("RV")) {
						prompt = "Motor Home or Motorcycle";
					}
					else if (lob.equalsIgnoreCase("AUTO")) {
						prompt = "Auto";
					}
					else if (lob.equalsIgnoreCase("HOME")) {
						prompt = "Home";
					}
					else if (lob.equalsIgnoreCase("UMBRELLA")) {
						prompt = "Umbrella";
					}
					else if (lob.equalsIgnoreCase("MR")) {
						prompt = "Boat";
					}
					else if (lob.equalsIgnoreCase("SP")) {
						prompt = "Specialty Dwelling or Mobile Home";
					}	
 
					
					String lang = (String) data.getSessionData("S_PREF_LANG");
					
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("auto")) {
						prompt = prompt.replace("Auto", " auto");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("home")) {
						prompt = prompt.replace("Home", " casa");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("boat")) {
						prompt = prompt.replace("Boat", " barco");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("motor home or motorcycle policy")) {
						prompt = prompt.replace("Motor Home or Motorcycle", " casa rodante o motocicleta");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("specialty dwelling or mobile home")) {
						prompt = prompt.replace("Specialty Dwelling or Mobile Home", " vivienda especial o casa móvil");
					}
					if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("umbrella")) {
						prompt = prompt.replace("Umbrella", " paraguas");
					}

					
					if (prompt.contains(" ")) {
						prompt = prompt.replaceAll(" ", ".");
					}
					
					data.setSessionData(Constants.FINAL_PRODUCTTYPE, lob);
					data.setSessionData("POLICY_TYPE", policytype);
					data.addToLog(currElementName, "Value of POLICY_TYPE : " + policytype);
					data.setSessionData(Constants.VXMLParam1, prompt);
					data.setSessionData(Constants.VXMLParam2, "NA");
					data.setSessionData(Constants.VXMLParam3, "NA");
					data.setSessionData(Constants.VXMLParam4, "NA");
					data.addToLog(currElementName,"Single Product Type Multiple Policy scenario");
					data.addToLog(currElementName,"KYC Number Of Policies Dynamic Product Type Prompts = "+prompt.toString());
					data.addToLog(currElementName,"VXMLPARAM1 = "+data.getSessionData(Constants.VXMLParam1));

					data.setSessionData("S_NOOF_POLICIES_ACCLINKANI_EXITSTATE", Constants.S_MULTIPLE_POLICIES_SINGLE_PRODUCTTYPE_FOUND);
					data.addToLog(currElementName,"S_NOOF_POLICIES_ACCLINKANI_EXITSTATE : "+data.getSessionData("S_NOOF_POLICIES_ACCLINKANI_EXITSTATE"));

					strExitState = Constants.SU;
				}
			}
			else {
				strExitState = Constants.ER;
			}
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in AccLinkAniLookup method  :: "+e);
			caa.printStackTrace(e);
		}
		return strExitState;
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

}