package com.farmers.adminHost;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.AdminAPI.GetBrandTableByBusinessObjects;
import com.farmers.AdminAPI.GetBusinessObjectsByDnisKey;
import com.farmers.AdminAPI.GetMenuSelectionPropertyTableByKey;
import com.farmers.AdminAPI.GetRoutingChildObjects;
import com.farmers.AdminAPI.GetRoutingParentObjects;
//CS1151307 : Update policy state based routing
import com.farmers.AdminAPI.GetStateGroupTableByStateName;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;
import com.farmers.util.TimezoneCheck;
import com.servion.farmers.CollectionOfDestinationCombos;
import com.servion.farmers.DestinationDecider;
import com.servion.farmers.DestinationWeightage;

public class Routing extends DecisionElementBase {

	//private String routeRuleComboName = new String();

	private final static String S_ROUTING_BRAND_LABEL = "S_ROUTING_BRAND_LABEL";
	private final static String S_MSPTABLE_URL = "S_MSPTABLE_URL";
	private final static String S_ROUTING_PARENT_URL = "S_ROUTING_PARENT_URL";
	private final static String S_ROUTING_CHILD_URL = "S_ROUTING_CHILD_URL";
	
	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String StrExitState = Constants.ER;
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);
		String strReqBody = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;
		String dnis = Constants.EmptyString;
		String brandLabel = Constants.EmptyString;
		
		//CS1151307 : Update policy state based routing
		String originalStateName = Constants.EmptyString;
		String finalStateName = Constants.EmptyString;
		
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;

		try {
			data.addToLog(currElementName, "Routing API start  ");
			
			///CS1151307 : Update policy state based routing Recall State Group by State Name API & set State groups into session before routing starts if State name by ANI does not align with State Group received from policy lookup(Post Authentication).
			originalStateName = (String) data.getSessionData("S_ORIGINAL_STATE_NAME");
			finalStateName = (String) data.getSessionData(Constants.S_STATENAME);
			
			if (null != originalStateName && null != finalStateName && !originalStateName.equalsIgnoreCase(finalStateName)) {
				data.addToLog(currElementName, "Calling State Group API :: State Name :: " +finalStateName);
				callStateGroupAPI(currElementName, data);
				data.addToLog(currElementName, "State Groups updated into session :: " + data.getSessionData(Constants.S_STATE_GROUP));
			}
			
			if (data.getSessionData(Constants.S_ROUTING_URL) != null && data.getSessionData(Constants.S_CONN_TIMEOUT) != null && data.getSessionData(Constants.S_READ_TIMEOUT) != null) {
				String callerId = (String) data.getSessionData(Constants.S_CALLID);
				String menuSelectionKey = (String) data.getSessionData(Constants.S_MENU_SELCTION_KEY);
				if (menuSelectionKey == null)
					menuSelectionKey = Constants.EmptyString;
				data.addToLog(currElementName, "menuSelectionKey : " + menuSelectionKey);
				if ((null != data.getSessionData(Constants.IS_PEELOFF_MENU_OPTED)) && (((String) data.getSessionData(Constants.IS_PEELOFF_MENU_OPTED)).equals(Constants.STRING_YES))) {
					dnis = (String) data.getSessionData(Constants.S_PEEL_OFF_DNIS);
					data.addToLog(currElementName, "Peeloff DNIS : " + dnis);
					data.setSessionData(Constants.S_FINAL_DNIS, dnis);
				} else {
					dnis = (String)data.getSessionData(Constants.S_DNIS);
				}
				data.addToLog(currElementName, "DNIS : " + dnis);
				String dnisgroup = (String) data.getSessionData(Constants.S_DNISGROUP);
				String category = (String) data.getSessionData(Constants.S_CATEGORY);
				String lob = (String) data.getSessionData(Constants.S_LOB);
				
				int conTimeout = Integer.valueOf((String) data.getSessionData(Constants.S_CONN_TIMEOUT));
				int readTimeout = Integer.valueOf((String) data.getSessionData(Constants.S_READ_TIMEOUT));
				LoggerContext context = (LoggerContext) data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
				
				data.addToLog(currElementName, "Element Name : " + data.getCurrentElement());
				
				if(data.getCurrentElement().equalsIgnoreCase("STR_ADM_003")) {
					//calling BrandLabel lookup before routing using the original business objects
					String brandsUrl = (String) data.getSessionData(Constants.S_BRANDSTABLE_URL);
					brandLabel = brandsLabeLookup(brandsUrl, callerId, Constants.tenantid, dnis, dnisgroup, lob, category, conTimeout, readTimeout, context, data);
					data.addToLog(currElementName, "Brand Label Returned : " + brandLabel);
					if(brandLabel.isEmpty()) {
						data.setSessionData(Constants.S_BRAND, Constants.EmptyString);
					} else {
						brandLabel = brandLabel.replaceAll("[^a-zA-Z0-9\\s()-]","");
						data.setSessionData(Constants.S_BRAND, brandLabel);
					}
					
					//Calling MSP, UpdateBO and Brands only on transfer scenarios
					//MSP and BusinessObjects Update
					String menuSelectionUrl = (String) data.getSessionData(S_MSPTABLE_URL);
					String finalDnis = Constants.EmptyString;
					String menuSelectionKeys = Constants.EmptyString;
					
					@SuppressWarnings("unchecked")
					Queue<String> mspValues =  (Queue<String>) data.getSessionData("S_LAST_5_MSP_VALUES");
					if(mspValues != null) {
						data.addToLog(currElementName, "Menu Selection Keys Queue : " + mspValues);
						data.addToLog(currElementName, "Menu Selection Keys Queue Size : " + mspValues.size());
						int mspSize = mspValues.size();
						for (int i = 0; i < mspSize; i++ ) {
							menuSelectionKeys = menuSelectionKeys + (String)mspValues.poll() + "|";	
							data.addToLog(currElementName, "Menu Selection Keys loop : " + menuSelectionKeys);
						}
						menuSelectionKeys = menuSelectionKeys.substring(0, menuSelectionKeys.length()-1);
					} else {
						data.addToLog(currElementName, "MSP Values : " + mspValues);
					}
					
					data.addToLog(currElementName, "Menu Selection Keys : " + menuSelectionKeys);
					
					
					if ((null != data.getSessionData(Constants.IS_PEELOFF_MENU_OPTED)) && (((String) data.getSessionData(Constants.IS_PEELOFF_MENU_OPTED)).equals(Constants.STRING_YES))) {
						finalDnis = dnis;
					} else {
						finalDnis = mspTableLookup(menuSelectionUrl, callerId, Constants.tenantid, menuSelectionKeys, conTimeout, readTimeout, context, data);
					}
					
					
					if(!finalDnis.equalsIgnoreCase(Constants.EmptyString)) {
						boolean isBOUpdated = updateBO(finalDnis, data);
						if (isBOUpdated) {
							dnis = (String) data.getSessionData(Constants.S_FINAL_DNIS);
							dnisgroup = (String) data.getSessionData(Constants.S_FINAL_DNIS_GROUP); 
							category = (String) data.getSessionData(Constants.S_FINAL_CATEGORY);
							lob = (String) data.getSessionData(Constants.S_FINAL_LOB);
							data.addToLog(currElementName, "Final BO objects updated, DNIS <" + dnis + ">, DNIS Group <" + dnisgroup + ">, Category <" + category + ">, LOB <" + lob + ">");	
						}
					} else {
						data.addToLog(currElementName,"MSP Entry Missing for : " + menuSelectionKey);
					}
				}
				
				GetRoutingParentObjects objRoutingParent = new GetRoutingParentObjects();
				String routingParentUrl = (String) data.getSessionData(S_ROUTING_PARENT_URL);
				
				//GetRoutingTableByBusinessObjects objGetRoutingTableByBusinessObjects = new GetRoutingTableByBusinessObjects();
				List<String> retValue = new ArrayList<String>();

				// Routing logic changed to call with MSP Key and DNIS as first try. DNIS Group
				// as second try, Category as 3rd try and LOB as 4th try.
				JSONObject resp = null;
				for (int routingloop = 1; routingloop <= 4; routingloop++) {
					data.addToLog(currElementName, "***************************Route rule iteration : " + routingloop);
					if (routingloop == 1) {
						
						resp = (JSONObject)objRoutingParent.start(routingParentUrl, callerId, Constants.tenantid, menuSelectionKey, dnis, Constants.EmptyString, Constants.EmptyString, Constants.EmptyString, conTimeout, readTimeout, context);
						
						data.addToLog(currElementName, "Routing Parent API response :" + resp);
						
						//Mustan - Alerting Mechanism ** Response Code Capture
						apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
						if (resp != null) {
							if (resp.containsKey(Constants.REQUEST_BODY))
								strReqBody = resp.get(Constants.REQUEST_BODY).toString();
							if (resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200) {
								data.addToLog(currElementName,"Set  Routing API Response into session with the key name of " + currElementName+ Constants._RESP);
								strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
								data.setSessionData(currElementName + Constants._RESP,resp.get(Constants.RESPONSE_BODY));
								retValue = apiResponseManupulation(data, caa, currElementName, strRespBody, context);
								StrExitState = retValue.get(0);
							}
						}
					} else if (routingloop == 2) {
						 resp = (JSONObject)objRoutingParent.start(routingParentUrl, callerId, Constants.tenantid, Constants.EmptyString, Constants.EmptyString, dnisgroup, Constants.EmptyString, Constants.EmptyString, conTimeout, readTimeout, context);
						data.addToLog(currElementName, "Routing Parent API response :" + resp);
						
						//Mustan - Alerting Mechanism ** Response Code Capture
						apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
						if (resp != null) {
							if (resp.containsKey(Constants.REQUEST_BODY))
								strReqBody = resp.get(Constants.REQUEST_BODY).toString();
							if (resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200) {
								data.addToLog(currElementName, "Set  Routing API Response into session with the key name of " + currElementName + Constants._RESP);
								strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
								data.setSessionData(currElementName + Constants._RESP,resp.get(Constants.RESPONSE_BODY));
								retValue = apiResponseManupulation(data, caa, currElementName, strRespBody, context);
								StrExitState = retValue.get(0);
							}
						}
					} else if (routingloop == 3) {
						resp = (JSONObject)objRoutingParent.start(routingParentUrl, callerId, Constants.tenantid, Constants.EmptyString, Constants.EmptyString, Constants.EmptyString, category, Constants.EmptyString, conTimeout, readTimeout, context);
						data.addToLog(currElementName, "Routing Parent API response :" + resp);
						
						//Mustan - Alerting Mechanism ** Response Code Capture
						apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
						if (resp != null) {
							if (resp.containsKey(Constants.REQUEST_BODY))
								strReqBody = resp.get(Constants.REQUEST_BODY).toString();
							if (resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200) {
								data.addToLog(currElementName,"Set  Routing API Response into session with the key name of " + currElementName + Constants._RESP);
								strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
								data.setSessionData(currElementName + Constants._RESP,resp.get(Constants.RESPONSE_BODY));
								retValue = apiResponseManupulation(data, caa, currElementName, strRespBody, context);
								StrExitState = retValue.get(0);
							}
						}
					} else {
						resp = (JSONObject) objRoutingParent.start(routingParentUrl, callerId, Constants.tenantid, Constants.EmptyString, Constants.EmptyString, Constants.EmptyString, Constants.EmptyString, lob, conTimeout, readTimeout, context);
						data.addToLog(currElementName, "Routing Parent API response :" + resp);
						
						//Mustan - Alerting Mechanism ** Response Code Capture
						apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
						if (resp != null) {
							if (resp.containsKey(Constants.REQUEST_BODY))
								strReqBody = resp.get(Constants.REQUEST_BODY).toString();
							if (resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200) {
								data.addToLog(currElementName,"Set  Routing API Response into session with the key name of " + currElementName + Constants._RESP);
								strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
								data.setSessionData(currElementName + Constants._RESP, resp.get(Constants.RESPONSE_BODY));
								retValue = apiResponseManupulation(data, caa, currElementName, strRespBody, context);
								StrExitState = retValue.get(0);
							}
						}
					}
					data.setSessionData(currElementName, "Ret Value List : " + retValue);
					// check if we got the desired route rule and exit the loop
					if (retValue.get(1).equals("true")) {
						data.addToLog(currElementName,"***************************breaking from route loop at iteration : " + routingloop);
						break;
					}
					data.addToLog(currElementName, "For loop ending");
				}
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in Routing API call  :: " + e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName, "Report start");
		try {
			objHostDetails.startHostReport(currElementName, "Routing API", strReqBody,"", data.getSessionData(S_ROUTING_PARENT_URL).toString().length() > 99 ?  (String)data.getSessionData(S_ROUTING_PARENT_URL).toString().substring(0, 99) : (String)data.getSessionData(S_ROUTING_PARENT_URL));
			objHostDetails.endHostReport(data, strRespBody,	StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode," ");
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception while forming host reporting for Routing API call  :: " + e);
			caa.printStackTrace(e);
		}
		data.addToLog(currElementName, "Report end");
		data.setSessionData("ROUTINGDONE", "TRUE");
		return StrExitState;
	}

	@SuppressWarnings("unchecked")
	private List<String> apiResponseManupulation(DecisionElementData data, CommonAPIAccess caa, String currElementName,	String strRespBody, LoggerContext context) {
		List<String> retList = new ArrayList<String>();
		List<JSONObject> tempList1 = new ArrayList<JSONObject>();
		List<JSONObject> tempList2 = new ArrayList<JSONObject>();

		String ret1 = Constants.ER;
		String ret2 = "false";

		try {
			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			JSONArray resArr = (JSONArray) resp.get("res");
			data.addToLog(currElementName, "Result Array Size : " + resArr.size());
			data.addToLog(currElementName, "Result Size : " + resArr.size());
			JSONObject mainObj = null;
			if (resArr == null || resArr.size() == 0) {
				data.addToLog(currElementName, "Result Array 0 exit...");
			} else if (resArr.size() == 1) {
				mainObj = (JSONObject) resArr.get(0);
				data.addToLog(currElementName, "loop at single record");
				//check qualifiers
				//state groups
				List<JSONObject> list = (List<JSONObject>) StreamSupport.stream(resArr.spliterator(), false).map(e -> (JSONObject) e).collect(Collectors.toList());
				tempList1 = new ArrayList<JSONObject>(routingSortingStateGroup(list, Constants.S_STATE_GROUP, Constants.StateGroup, data));
				
				tempList2 = routingSortingLang(tempList1, Constants.S_FINAL_LANG, Constants.Language, data);
				tempList1 = new ArrayList<JSONObject>();

				// S_BRAND_LABEL sub attribute sorting
				tempList1 = routingSorting(tempList2, S_ROUTING_BRAND_LABEL, Constants.BrandLabel, data);
				tempList2 = new ArrayList<JSONObject>();

				// S_ANI_GROUP sub attribute sorting
				tempList2 = routingSorting(tempList1, Constants.S_ANI_GROUP, Constants.AnigroupHandling, data);
				tempList1 = new ArrayList<JSONObject>();

				// S_POLICY_SEGMENTATION sub attribute sorting
				tempList1 = routingSorting(tempList2, Constants.S_POLICY_SEGMENTATION, Constants.PolicySegmentation, data);
				tempList2 = new ArrayList<JSONObject>();

				// S_AGENT_SEGMENTATION sub attribute sorting
				tempList2 = routingSorting(tempList1, Constants.S_AGENT_SEGMENTATION, Constants.AgentSegmentation, data);
				tempList1 = new ArrayList<JSONObject>();

				// S_POLICY_SOURCE sub attribute sorting
				tempList1 = routingSorting(tempList2, Constants.S_POLICY_SOURCE, Constants.PolicySource, data);
				tempList2 = new ArrayList<JSONObject>();

				// S_POLICY_ATTRIBUTES sub attribute sorting
				tempList2 = routingSorting(tempList1, Constants.S_POLICY_ATTRIBUTES, Constants.PolicyAttributes, data);
				tempList1 = new ArrayList<JSONObject>();

				if (tempList2.size() > 0) {
					data.addToLog(currElementName, "Set to break loop at more than zero");
					data.addToLog(currElementName, "TempList : " + tempList2);
					mainObj = (JSONObject) tempList2.get(0);
					ret2 = "true";
				} else {
					mainObj = getDefaultRouteRule(list, data);
					if(mainObj != null) {
						ret2 = "true";
						data.addToLog(currElementName, "Default Route invoked : " + mainObj.toJSONString());
					}
				}
			} else {
				List<JSONObject> list = (List<JSONObject>) StreamSupport.stream(resArr.spliterator(), false).map(e -> (JSONObject) e).collect(Collectors.toList());
				data.addToLog(currElementName, "List size post Converted JSONARRAY into LIST  : " + list.size());

				// S_STATE_GROUP sub attribute sorting
				tempList1 = new ArrayList<JSONObject>(routingSortingStateGroup(list, Constants.S_STATE_GROUP, Constants.StateGroup, data));
				// S_PREF_LANG sub attribute sorting

				tempList2 = routingSortingLang(tempList1, Constants.S_FINAL_LANG, Constants.Language, data);
				tempList1 = new ArrayList<JSONObject>();

				// S_BRAND_LABEL sub attribute sorting
				tempList1 = routingSorting(tempList2, S_ROUTING_BRAND_LABEL, Constants.BrandLabel, data);
				tempList2 = new ArrayList<JSONObject>();

				// S_ANI_GROUP sub attribute sorting
				tempList2 = routingSorting(tempList1, Constants.S_ANI_GROUP, Constants.AnigroupHandling, data);
				tempList1 = new ArrayList<JSONObject>();

				// S_POLICY_SEGMENTATION sub attribute sorting
				tempList1 = routingSorting(tempList2, Constants.S_POLICY_SEGMENTATION, Constants.PolicySegmentation, data);
				tempList2 = new ArrayList<JSONObject>();

				// S_AGENT_SEGMENTATION sub attribute sorting
				tempList2 = routingSorting(tempList1, Constants.S_AGENT_SEGMENTATION, Constants.AgentSegmentation,data);
				tempList1 = new ArrayList<JSONObject>();

				// S_POLICY_SOURCE sub attribute sorting
				tempList1 = routingSorting(tempList2, Constants.S_POLICY_SOURCE, Constants.PolicySource, data);
				tempList2 = new ArrayList<JSONObject>();

				// S_POLICY_ATTRIBUTES sub attribute sorting
				tempList2 = routingSorting(tempList1, Constants.S_POLICY_ATTRIBUTES, Constants.PolicyAttributes, data);
				tempList1 = new ArrayList<JSONObject>();

				if(data.getSessionData(Constants.S_FINAL_LANG).equals("Spanish")) {
					
					if (tempList2.size() > 0) {
						
					}
						data.addToLog(currElementName, "Res multiple Set to break loop at more than zero");
						data.addToLog(currElementName, "TempList : " + tempList2);

						List<JSONObject> tempList3 = new ArrayList<JSONObject>();
						
						for (JSONObject tempObj : tempList2) {
							
							if (!(Constants.EmptyString.equalsIgnoreCase(tempObj.get("language").toString()))) {
								
								data.addToLog(currElementName+"added for emptyString check", tempObj.get("language").toString());
							
							if (Constants.Spanish.equalsIgnoreCase(tempObj.get("language").toString())) {
								
								data.addToLog(data.getCurrentElement(), "Adding Spanish Lanuage Entry");
								
								tempList3.add(tempObj);
								mainObj = (JSONObject) tempList3.get(0);
								ret2 = "true";
							} 
							}
						else if (tempList2.size() > 0) {
							data.addToLog(currElementName, "Res multiple Set to break loop at more than zero");
							data.addToLog(currElementName, "TempList : " + tempList2);
							mainObj = (JSONObject) tempList2.get(0);
							ret2 = "true";
						}
						}
				}
				else if (tempList2.size() > 0) {
					data.addToLog(currElementName, "Res multiple Set to break loop at more than zero");
					data.addToLog(currElementName, "TempList : " + tempList2);
					mainObj = (JSONObject) tempList2.get(0);
					ret2 = "true";
				} else {
					data.addToLog(currElementName, "GetDefaultRouteRule logic start ***********************");
					mainObj = getDefaultRouteRule(list, data);
					if(mainObj != null) {
						ret2 = "true";
						data.addToLog(currElementName, "Default Route invoked : " + mainObj.toJSONString());
					}
					data.addToLog(currElementName, "GetDefaultRouteRule logic end ***********************");
				}
			}

			if (mainObj != null) {
				//Call RoutingChild here
				data.addToLog(currElementName, "Routing child call start");
				GetRoutingChildObjects objGetRoutingChildObjects = new GetRoutingChildObjects();
				
				String routeRuleName = (String) mainObj.get("key");
				String routingChildUrl = (String) data.getSessionData(S_ROUTING_CHILD_URL);
				
				int conTimeout = Integer.valueOf((String) data.getSessionData(Constants.S_CONN_TIMEOUT));
				int readTimeout = Integer.valueOf((String) data.getSessionData(Constants.S_READ_TIMEOUT));
				String callerId = (String) data.getSessionData(Constants.S_CALLID);
				data.addToLog(currElementName, "Routing Child URL : " + routingChildUrl);
				data.addToLog(currElementName, "Routing Rule Name : " + routeRuleName);
				
				mainObj = objGetRoutingChildObjects.start(routingChildUrl, callerId, Constants.tenantid, Constants.EmptyString, routeRuleName, conTimeout, readTimeout, context);
				
				JSONObject responseBody = (JSONObject) mainObj.get("responseBody");
				
				JSONArray resArray = (JSONArray) responseBody.get("res");
				mainObj = (JSONObject) resArray.get(0);
				
				data.addToLog(currElementName, "Routing picker : " + mainObj);
				String strTransferAction = (String) mainObj.get("routingaction");
				data.addToLog(currElementName, "Route Rule : " + mainObj.get("key"));
				data.setSessionData(Constants.S_ROUTING_KEY, mainObj.get("key"));
				data.addToLog(currElementName, "FINAL DNIS : " + mainObj.get("dnis"));
				if (mainObj.get("dnis") != null) {
					if (!(Constants.EmptyString.equalsIgnoreCase(mainObj.get("dnis").toString()))) {
						data.setSessionData(Constants.S_FINAL_DNIS, mainObj.get("dnis"));
					}
				}
				if (mainObj.get("dnisgroup") != null) {
					if (!(Constants.EmptyString.equalsIgnoreCase(mainObj.get("dnisgroup").toString()))) {
						data.setSessionData(Constants.S_FINAL_DNIS_GROUP, mainObj.get("dnisgroup"));
					}
				}
				if (mainObj.get(Constants.StateGroup) != null) {
					if (!(Constants.EmptyString.equalsIgnoreCase(mainObj.get(Constants.StateGroup).toString()))) {
						data.setSessionData(Constants.S_FINAL_STATE_GROUP, mainObj.get(Constants.StateGroup));
					} else {
						data.setSessionData(Constants.S_FINAL_STATE_GROUP, Constants.EmptyString);
					}
				}
				if (mainObj.get("category") != null) {
					if (!(Constants.EmptyString.equalsIgnoreCase(mainObj.get("category").toString()))) {
						data.setSessionData(Constants.S_FINAL_CATEGORY, mainObj.get("category"));
					}
				}	
				if (mainObj.get("brandlabel") != null) {
					String brandLabel = (String) data.getSessionData(Constants.S_BRAND);
					if(!mainObj.get("brandlabel").toString().equalsIgnoreCase(brandLabel)) {
						data.setSessionData(Constants.S_BRAND, Constants.EmptyString);
					}
				}
				data.setSessionData(Constants.S_ROUTING_TRANSFER_ACTION, strTransferAction.toUpperCase());
				data.addToLog(currElementName, "S_ROUTING_TRANSFER_ACTION : " + strTransferAction);

				try {
					JSONArray transferrecordingArr = (JSONArray) mainObj.get("transferrecording");
					if (null != transferrecordingArr && transferrecordingArr.size() > 0) {
						JSONObject transferrecordingObj = (JSONObject) transferrecordingArr.get(0);
						if (data.getSessionData(Constants.S_PREF_LANG).equals(Constants.EN)) {
							if (!((String) transferrecordingObj.get("audioenglishwavpath"))
									.equals(Constants.EmptyString)
									&& ((String) transferrecordingObj.get("audioenglishwavpath")).contains(".wav")) {
								String wavFile = (String) transferrecordingObj.get("audioenglishwavpath");
								if (wavFile.contains("\\")) {
									String[] wavFileArr = Pattern.compile("\\\\").split(wavFile);
									wavFile = wavFileArr[wavFileArr.length - 1];
								}
								data.setSessionData(Constants.STR_PA_004_WAV, wavFile);
							}
							if (!((String) transferrecordingObj.get("audioenglishtext")).equals(Constants.EmptyString))
								data.setSessionData(Constants.STR_PA_004_TTS,
										transferrecordingObj.get("audioenglishtext"));
							data.addToLog(currElementName,
									"EN :: audioenglishwavpath : " + data.getSessionData(Constants.STR_PA_004_WAV)
											+ " :: audioenglishtext : "
											+ data.getSessionData(Constants.STR_PA_004_TTS));
						} else {
							if (!((String) transferrecordingObj.get("audiospanishwavpath"))
									.equals(Constants.EmptyString)
									&& ((String) transferrecordingObj.get("audiospanishwavpath")).contains(".wav")) {
								String wavFile = (String) transferrecordingObj.get("audiospanishwavpath");
								if (wavFile.contains("\\")) {
									String[] wavFileArr = Pattern.compile("\\\\").split(wavFile);
									wavFile = wavFileArr[wavFileArr.length - 1];
								}
								data.setSessionData(Constants.STR_PA_004_WAV, wavFile);
							}
							if (!((String) transferrecordingObj.get("audiospanishtext"))
									.equals(Constants.EmptyString)) {
								data.setSessionData(Constants.STR_PA_004_TTS,
										transferrecordingObj.get("audiospanishtext"));
							}
							data.addToLog(currElementName,
									"SP:: audiospanishtext : " + data.getSessionData(Constants.STR_PA_004_WAV)
											+ " :: audiospanishtext : "
											+ data.getSessionData(Constants.STR_PA_004_TTS));
						}
						if (null != ((String) data.getSessionData(Constants.STR_PA_004_WAV))
								|| null != ((String) data.getSessionData(Constants.STR_PA_004_TTS)))
							data.setSessionData(Constants.S_TRANFER_RECORDING_ENABLED, Constants.STRING_YES);
					} else {
						data.addToLog(currElementName, "transferrecording details are null or empty from Routing API");
					}
				} catch (Exception e) {
					data.addToLog(data.getCurrentElement(), "Exception in fetching tranfer recoring  :: " + e);
					caa.printStackTrace(e);
				}

				try {
					// After hours rules
					JSONArray afterhourrulesArr = (JSONArray) mainObj.get("afterhourrules");
					data.addToLog(currElementName, "After hours obj : " + afterhourrulesArr);
					if (afterhourrulesArr != null) {
						JSONObject afterhourrulesObj = (JSONObject) afterhourrulesArr.get(0);
						
						JSONArray recordingArr = (JSONArray) afterhourrulesObj.get("recording");
						JSONObject recordingObj = (JSONObject) recordingArr.get(0);
						
						data.addToLog(currElementName, "After hours available");
						if (data.getSessionData(Constants.S_PREF_LANG).equals(Constants.EN)) {
							data.addToLog(currElementName, "After hours english");
							if (!recordingObj.get("audioenglishwavpath").equals("")
									&& ((String) recordingObj.get("audioenglishwavpath")).contains(".wav")) {
								String wavFile = (String) recordingObj.get("audioenglishwavpath");
								if (wavFile.contains("\\")) {
									String[] wavFileArr = Pattern.compile("\\\\").split(wavFile);
									wavFile = wavFileArr[wavFileArr.length - 1];
								}
								// .WAV
								data.setSessionData(Constants.HOOP_PA_001_WAV, recordingObj.get("audioenglishwavpath"));
								data.setSessionData(Constants.Entry_HOOP_PA_001_WAV, recordingObj.get("audioenglishwavpath"));
								// TTS
								data.setSessionData(Constants.HOOP_PA_001_TTS, recordingObj.get("audioenglishtext"));
								data.setSessionData(Constants.Entry_HOOP_PA_001_TTS, recordingObj.get("audioenglishtext"));
								data.addToLog(currElementName,
										"EXIT EN :: audioenglishwavpath : " + data.getSessionData(Constants.HOOP_PA_001_WAV)
												+ " :: audioenglishtext : " + data.getSessionData(Constants.HOOP_PA_001_TTS));
								data.addToLog(currElementName,
										"ENTRY EN :: audioenglishwavpath : " + data.getSessionData(Constants.Entry_HOOP_PA_001_WAV)
												+ " :: audioenglishtext : " + data.getSessionData(Constants.Entry_HOOP_PA_001_TTS));
							}
						} else {
							data.addToLog(currElementName, "After hours spanish");
							if (!recordingObj.get("audiospanishwavpath").equals("")
									&& ((String) recordingObj.get("audiospanishwavpath")).contains(".wav")) {
								data.addToLog(currElementName, "After hours spanish");
								String wavFile = (String) recordingObj.get("audiospanishwavpath");
								if (wavFile.contains("\\")) {
									String[] wavFileArr = Pattern.compile("\\\\").split(wavFile);
									wavFile = wavFileArr[wavFileArr.length - 1];
								}
								// .WAV
								data.setSessionData(Constants.HOOP_PA_001_WAV, recordingObj.get("audiospanishwavpath"));
								data.setSessionData(Constants.Entry_HOOP_PA_001_WAV, recordingObj.get("audiospanishwavpath"));
								// TTS
								data.setSessionData(Constants.HOOP_PA_001_TTS, recordingObj.get("audiospanishtext"));
								data.setSessionData(Constants.Entry_HOOP_PA_001_TTS, recordingObj.get("audiospanishtext"));
								data.addToLog(currElementName,
										"EXIT SP :: audiospanishwavpath : " + data.getSessionData(Constants.HOOP_PA_001_WAV)
												+ " :: audiospanishtext : " + data.getSessionData(Constants.HOOP_PA_001_TTS));
								data.addToLog(currElementName,
										"ENTRY SP :: audioenglishwavpath : " + data.getSessionData(Constants.Entry_HOOP_PA_001_WAV)
												+ " :: audioenglishtext : " + data.getSessionData(Constants.Entry_HOOP_PA_001_TTS));

							}
						}
						String actionObj = (String)afterhourrulesObj.get("action");
						data.addToLog(currElementName, "Afterhours Action : " + actionObj);
						
						if(actionObj.equalsIgnoreCase("Transfer to Voicemail")) {
							String voicemailObject = (String) afterhourrulesObj.get("voicemailtransferno");
							data.addToLog(currElementName, "Voicemail : " + voicemailObject);
							data.setSessionData(Constants.S_FINAL_DESTNUM, voicemailObject);
							data.setSessionData("S_VM_FLAG", Constants.TRUE);
						}	
					}
				} catch (Exception e) {
					data.addToLog(data.getCurrentElement(), "Exception in fetching tranfer recoring  :: " + e);
					caa.printStackTrace(e);
				}
				
				

				// String ccNameList = "timetable2|timetable3";
				String ccNameList = Constants.S_CC_NAME_LIST;
				String routeRuleCombinationName = Constants.EmptyString;
				List<RoutingBean> beanList = new ArrayList<RoutingBean>();
				boolean defaultTableOpen = false;
				int totalAllocation = 0;
				for (String ccName : ccNameList.split("\\|")) {
					//check for timetable override
					data.addToLog(data.getCurrentElement(), "CC Name processing : " + ccName);
					List<String> ccoverrideReturnList = isCCOpenOverride(mainObj, ccName + "exceptiondayoverrides", data, caa);
					if(ccoverrideReturnList.get(0).equalsIgnoreCase(Constants.TRUE)) {
						String timeWindow = ccoverrideReturnList.get(1);
						data.addToLog(data.getCurrentElement(), "override time window : " + timeWindow);
						if(checkBusinessHour(timeWindow, data, caa)) {
							data.addToLog(data.getCurrentElement(), "Exception time window open");
							RoutingBean bean = new RoutingBean();
							bean.setCcName(ccName + "window");
							if (ccName.endsWith("2") || ccName.endsWith("3") || ccName.endsWith("4")
									|| ccName.endsWith("5")) {
								List<String> allocationReturnList = getAllocation(mainObj, ccName + "allocation", data, caa);
								String strAllocation = allocationReturnList.get(1);
								routeRuleCombinationName = allocationReturnList.get(0);
								if (null == strAllocation || strAllocation.isEmpty() || strAllocation.equals(" ")
										|| strAllocation.equals("0%"))
									continue;
								bean.setCcAllocation(strAllocation);
								totalAllocation = totalAllocation
										+ Integer.parseInt(strAllocation.replaceAll("%", Constants.EmptyString));
							}
							bean.setDestinationNum(getDestinationNum(mainObj, ccName, data, caa));
							bean.setPriority(getPriorityNum(mainObj, ccName, data, caa));
							bean.setScreenPopUp(getScreenPopUPType(mainObj, ccName, data, caa));
							data.addToLog(data.getCurrentElement(), "bean : " + bean.toString());
							beanList.add(bean);
							data.setSessionData(Constants.S_CALLCENTER_OPEN_CLOSED, Constants.S_OPEN);
							data.addToLog(data.getCurrentElement(), "Call center opend for override timetable window");
							if(ccName.equalsIgnoreCase("timetable")) {
								defaultTableOpen = true;
							}
						} else {
							if(ccName.equalsIgnoreCase("timetable")) {
								defaultTableOpen = false;
							}
							data.addToLog(data.getCurrentElement(), "Exception time window closed, disregard adding record");
						}	
					} else {
						data.addToLog(data.getCurrentElement(), "override not applicable for : " + ccName);
						if (isCCOpen(mainObj, ccName + "window", data, caa)) {
							RoutingBean bean = new RoutingBean();
							bean.setCcName(ccName + "window");
							if (ccName.endsWith("2") || ccName.endsWith("3") || ccName.endsWith("4")
									|| ccName.endsWith("5")) {
								List<String> allocationReturnList = getAllocation(mainObj, ccName + "allocation", data, caa);
								String strAllocation = allocationReturnList.get(1);
								routeRuleCombinationName = allocationReturnList.get(0);
								if (null == strAllocation || strAllocation.isEmpty() || strAllocation.equals(" ")
										|| strAllocation.equals("0%"))
									continue;
								bean.setCcAllocation(strAllocation);
								totalAllocation = totalAllocation + Integer.parseInt(strAllocation.replaceAll("%", Constants.EmptyString));
							}
							bean.setDestinationNum(getDestinationNum(mainObj, ccName, data, caa));
							bean.setPriority(getPriorityNum(mainObj, ccName, data, caa));
							bean.setScreenPopUp(getScreenPopUPType(mainObj, ccName, data, caa));
							data.addToLog(data.getCurrentElement(), "bean : " + bean.toString());
							beanList.add(bean);
							if(ccName.equalsIgnoreCase("timetable")) {
								defaultTableOpen = true;
							}
						}	
					}
					
					if (totalAllocation == 100) {
						break;
					}
				}
				data.addToLog(currElementName, "total allocation after parsing non default timetables : " + totalAllocation);
				if(defaultTableOpen) {
					if(totalAllocation != 100) {
						//filled the rest of allocation to default time table
						RoutingBean bean = new RoutingBean();
						bean.setCcName("timetablewindow");
						String strAllocation = String.valueOf(100 - totalAllocation);
						data.addToLog(currElementName, "Pending allocation added to default : " + strAllocation);
						bean.setCcAllocation(strAllocation);
						bean.setDestinationNum(getDestinationNum(mainObj, "timetable", data, caa));
						bean.setPriority(getPriorityNum(mainObj, "timetable", data, caa));
						bean.setScreenPopUp(getScreenPopUPType(mainObj, "timetable", data, caa));
						data.addToLog(data.getCurrentElement(), "bean : " + bean.toString());
						beanList.add(bean);	
					}
				}

				data.addToLog(currElementName, "Call center OPEN list & details : " + beanList.toString());
				if (beanList.size() == 0) {
					data.addToLog(currElementName, "CC is closed, hence skipping pickDestNumForCallTransfer");
				} else {
					data.setSessionData(Constants.S_FINAL_DESTNUM, pickDestNumForCalltransfer(data, caa, beanList, routeRuleCombinationName));
				}
				ret1 = Constants.SU;
			} else {
				//Balaji K START CS1352386- Farmers Insurance | US | BW After Hours Call Transfer
				String callCenterFlag = Constants.EmptyString;
				callCenterFlag = (String)data.getSessionData(Constants.S_CALLCENTER_OPEN_CLOSED);
				//Balaji K END CS1352386- Farmers Insurance | US | BW After Hours Call Transfer
				data.setSessionData(Constants.S_FINAL_DESTNUM, Constants.EmptyString);
				data.setSessionData(Constants.S_TRANFER_RECORDING_ENABLED, Constants.STRING_YES);
				data.setSessionData(Constants.S_ROUTING_TRANSFER_ACTION, Constants.TRANSFER);
				data.setSessionData(Constants.STR_PA_004_WAV, "Transfer_ENG.wav");
				//Balaji K START CS1352386- Farmers Insurance | US | BW After Hours Call Transfer
				if(callCenterFlag !=null && !callCenterFlag.isEmpty() && callCenterFlag.equalsIgnoreCase(Constants.S_OPEN)) {
					data.setSessionData(Constants.S_CALLCENTER_OPEN_CLOSED, Constants.S_OPEN);
					data.addToLog(currElementName, "Result array 0 exit call center is OPEN");
					}else {
						data.setSessionData(Constants.S_CALLCENTER_OPEN_CLOSED, Constants.CLOSED);
						data.addToLog(currElementName, "Result array 0 exit call center is CLOSED");
					}
				//Balaji K END CS1352386- Farmers Insurance | US | BW After Hours Call Transfer
				data.setSessionData(Constants.S_ROUTING_KEY, "NO ROUTE");
				data.addToLog(currElementName, "Result array 0 exit, setting default route and Transfer");
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception in apiResponseManupulation  :: " + e);
			caa.printStackTrace(e);
		}
		retList.add(ret1);
		retList.add(ret2);
		return retList;
	}

	private List<String> getAllocation(JSONObject mainObj, String ccName, DecisionElementData data, CommonAPIAccess caa) {
		List<String> retList = new ArrayList<String>();
		String routeRuleComboName = Constants.EmptyString;
		String allocation = Constants.EmptyString;
		try {
			data.addToLog(data.getCurrentElement(), " Allocation checking for*** " + ccName + " ***timetable");
			JSONArray routingPlanArr = (JSONArray) mainObj.get("routingplan");
			data.addToLog(data.getCurrentElement(), "routingPlanArr size : " + routingPlanArr.size());
			if (routingPlanArr.size() > 0) {
				JSONObject timetableObj = (JSONObject) routingPlanArr.get(0);
				routeRuleComboName = timetableObj.get("key").toString();
				String lastModifiedTimeStamp = timetableObj.get("modifieddate").toString();
				routeRuleComboName = routeRuleComboName + "____" + lastModifiedTimeStamp;
				if (timetableObj.containsKey(ccName)) {
					String ccAllocation = (String) timetableObj.get(ccName);
					data.addToLog(data.getCurrentElement(), "CC ALLOCATION : " + ccAllocation);
					ZonedDateTime nowInCentral = ZonedDateTime.now(ZoneId.of("America/Chicago"));
					// Extract the day of the week
			        DayOfWeek dayOfWeek = nowInCentral.getDayOfWeek();
			        // Print the day of the week in full text format (e.g., "Monday", "Tuesday", ...)
			        // Locale.ENGLISH is used to ensure the output is in English. You can adjust the locale as needed.
			        String dayOfWeekText = dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH);
			        String currDay = dayOfWeekText.substring(0,3);
					data.addToLog(data.getCurrentElement(), "CURRENRT DAY : " + currDay);
					for (String strAllocation : ccAllocation.split("\\|")) {
						if (strAllocation == null || strAllocation.isEmpty())
							continue;
						String day = strAllocation.split("\\s+")[0];
						String time = strAllocation.split("\\s+")[1];
						String percentage = strAllocation.split("\\s+")[2];
						if (day.equalsIgnoreCase(currDay)) {
							if (checkBusinessHour(time, data, caa)) {
								allocation = percentage;
								data.addToLog(data.getCurrentElement(), "day : " + day + " :: time : " + time
										+ " :: percentage : " + percentage + " :: allocation : " + allocation);
								routeRuleComboName = routeRuleComboName + day + time;
								break;
							}
						}
					}
				} else {
					data.addToLog(data.getCurrentElement(),
							"Contact Centre Name noy exist in API Response under Routing plan " + ccName);
				}
			}
		} catch (Exception e) {
			data.addToLog(data.getCurrentElement(), "Exception in getAllocation  :: " + e);
			caa.printStackTrace(e);
		}
		retList.add(routeRuleComboName);
		retList.add(allocation);
		data.addToLog(data.getCurrentElement(), "getAllocation RetList : " + retList);
		return retList;
	}

	private String getDestinationNum(JSONObject mainObj, String ccName, DecisionElementData data, CommonAPIAccess caa) {
		String desinationNum = Constants.EmptyString;
		try {
			data.addToLog(data.getCurrentElement(), " Destination num retriving for*** " + ccName);
			if (ccName.contains("2")) {
				desinationNum = (String) mainObj.get("destinationkeywordtransfernumber" + 2);
			} else if (ccName.contains("3")) {
				desinationNum = (String) mainObj.get("destinationkeywordtransfernumber" + 3);
			} else if (ccName.contains("4")) {
				desinationNum = (String) mainObj.get("destinationkeywordtransfernumber" + 4);
			} else if (ccName.contains("5")) {
				desinationNum = (String) mainObj.get("destinationkeywordtransfernumber" + 5);
			} else {
				desinationNum = (String) mainObj.get("destinationkeywordtransfernumber" + 1);
			}
		} catch (Exception e) {
			data.addToLog(data.getCurrentElement(), "Exception in getDestinationNum  :: " + e);
			caa.printStackTrace(e);
		}
		return desinationNum;
	}

	private Object getPriorityNum(JSONObject mainObj, String ccName, DecisionElementData data, CommonAPIAccess caa)
			throws AudiumException {
		Object priorityNum = 0;
		try {
			data.addToLog(data.getCurrentElement(), " Destination num retriving for*** " + ccName);
			if (ccName.contains("2")) {
				priorityNum = (Object) mainObj.get("priority" + 2);
			} else if (ccName.contains("3")) {
				priorityNum = (Object) mainObj.get("priority" + 3);
			} else if (ccName.contains("4")) {
				priorityNum = (Object) mainObj.get("priority" + 4);
			} else if (ccName.contains("5")) {
				priorityNum = (Object) mainObj.get("priority" + 5);
			} else {
				priorityNum = (Object) mainObj.get("priority" + 1);
			}
		} catch (Exception e) {
			data.addToLog(data.getCurrentElement(), "Exception in getPriorityNum  :: " + e);
			caa.printStackTrace(e);
		}
		data.addToLog(data.getCurrentElement(), "priorityNum : " + priorityNum.toString());
		try {
			if((Constants.EmptyString).equalsIgnoreCase(priorityNum.toString())){
				priorityNum = 8;
				data.addToLog(data.getCurrentElement(), "Default priorityNum set : " + priorityNum.toString());
			}
		} catch (Exception e) {
			priorityNum = 8;
			data.addToLog(data.getCurrentElement(), "Default priorityNum set in exc : " + priorityNum.toString());
		}
		return priorityNum;
	}

	private String getScreenPopUPType(JSONObject mainObj, String ccName, DecisionElementData data, CommonAPIAccess caa)
			throws AudiumException {
		String screenpopType = Constants.EmptyString;
		try {
			data.addToLog(data.getCurrentElement(), " screenpoptype retriving for*** " + ccName);
			if (ccName.contains("2")) {
				screenpopType = (String) mainObj.get("screenpoptype" + 2);
			} else if (ccName.contains("3")) {
				screenpopType = (String) mainObj.get("screenpoptype" + 3);
			} else if (ccName.contains("4")) {
				screenpopType = (String) mainObj.get("screenpoptype" + 4);
			} else if (ccName.contains("5")) {
				screenpopType = (String) mainObj.get("screenpoptype" + 5);
			} else {
				screenpopType = (String) mainObj.get("screenpoptype" + 1);
			}
			if (!screenpopType.isEmpty())
				data.setSessionData(Constants.S_SCREENPOPUP_ENABLED, Constants.STRING_YES);
		} catch (Exception e) {
			data.addToLog(data.getCurrentElement(), "Exception in screenpopType  :: " + e);
			caa.printStackTrace(e);
		}
		data.addToLog(data.getCurrentElement(), "screenpopType : " + screenpopType.toString());
		return screenpopType;
	}

	private List<String> isCCOpenOverride(JSONObject mainObj, String ccName, DecisionElementData data, CommonAPIAccess caa) {
		List<String> retList = new ArrayList<String>(); 
		String ccOverrideFlag = Constants.FALSE;
		String timeWindow = Constants.EmptyString;
		String timeZone = Constants.EmptyString;
		data.addToLog(data.getCurrentElement(), "Time table name for check exception over rides : " + ccName);
		if (mainObj.containsKey(ccName)) {
			JSONArray exceptiondayOverrideArray = (JSONArray) mainObj.get(ccName);
			if(exceptiondayOverrideArray != null) {
				if(exceptiondayOverrideArray.size() > 0) {
					for (@SuppressWarnings("rawtypes")
					Iterator iterator = exceptiondayOverrideArray.iterator(); iterator.hasNext();) {
						JSONObject overrideObject = (JSONObject) iterator.next();
						String dateString = (String)overrideObject.get("date");
						// Parse the date string to a LocalDateTime
				        LocalDateTime dateTime = LocalDateTime.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
				        // Convert LocalDateTime to LocalDate
				        LocalDate date = dateTime.toLocalDate();
				        data.addToLog(data.getCurrentElement(), "Override date returned : " + date);
				        // Get the current date
				        ZonedDateTime nowInCentral = ZonedDateTime.now(ZoneId.of("America/Chicago"));
				        LocalDate today = nowInCentral.toLocalDate();
				        data.addToLog(data.getCurrentElement(), "current day : " + today);
				        // Compare the dates
				        if(date.equals(today)) {
				        	data.addToLog(data.getCurrentElement(), "Override day matching for : " + ccName);
				        	JSONObject overrideTimeWindowObj = (JSONObject)overrideObject.get("timewindow");
				        	timeWindow = (String)overrideTimeWindowObj.get("timewindow");
				        	timeZone = (String)overrideTimeWindowObj.get("timezone");
				        	data.addToLog(data.getCurrentElement(), "Override time window : " + timeWindow);
				        	data.addToLog(data.getCurrentElement(), "Override timezone : " + timeZone);
				        	ccOverrideFlag = Constants.TRUE;
				        	//breaking for loop
				        	break;
				        } else {
				        	data.addToLog(data.getCurrentElement(), "Override day not matching for : " + ccName);
				        }
					}
				} else {
					data.addToLog(data.getCurrentElement(), "No Exception overrides for : " + ccName);
				}
			} else {
				data.addToLog(data.getCurrentElement(), "No Exception overrides for : " + ccName);
			}
		}
		retList.add(ccOverrideFlag);
		retList.add(timeWindow);
		retList.add(timeZone);
		data.addToLog(data.getCurrentElement(), "Override return list : " + retList);
		return retList;
	}
	

	
	
	private boolean isCCOpen(JSONObject mainObj, String ccName, DecisionElementData data, CommonAPIAccess caa) {
		boolean ccOpenFlag = false;
		try {
			data.addToLog(data.getCurrentElement(), "Time table name for check open or cloase : " + ccName);
			// check for holiday
			String holidayMessageKey = ccName.replace("window", "holidaymessage");
			data.addToLog(data.getCurrentElement(), "HolidayKey : " + holidayMessageKey);
			
			Map<String, String> holidayMap =  holidayCheck(mainObj, holidayMessageKey, data, caa);
			String holidayFlag = (String)holidayMap.get("HolidayFlag");
			if(Constants.TRUE.equalsIgnoreCase(holidayFlag)) {
				data.addToLog(data.getCurrentElement(), "Holiday set for : " + holidayMessageKey);
				ccOpenFlag = false;
				String audioText = "";
				String audioWav = "";
				if(Constants.SP.equalsIgnoreCase((String)data.getSessionData(Constants.S_PREF_LANG))){
					audioText = holidayMap.get("audiospanishtext");
					audioWav  = holidayMap.get("audiospanishwavpath");
					data.addToLog(data.getCurrentElement(), "audiospanishtext :"+audioText);
					data.addToLog(data.getCurrentElement(), "audiospanishwavpath :"+audioWav);
				}else {
					audioText = holidayMap.get("audioenglishtext");
					audioWav =  holidayMap.get("audioenglishwavpath");
					data.addToLog(data.getCurrentElement(), "audioenglishtext :"+audioText);
					data.addToLog(data.getCurrentElement(), "audioenglishwavpath :"+audioWav);
				}
				data.setSessionData(Constants.HOOP_PA_001_TTS, audioText);
				data.setSessionData(Constants.HOOP_PA_001_WAV, audioWav);
				data.setSessionData("HolidayFlag", holidayFlag);
			} else {
				// timetable details
				if (mainObj.containsKey(ccName)) {
					JSONArray timetableArr = (JSONArray) mainObj.get(ccName);
					if (timetableArr != null && timetableArr.size() > 0) {
						JSONObject timetableObj = (JSONObject) timetableArr.get(0);
						JSONObject dayObj = null;
						// Get the current date and time in the Central Time Zone
						ZonedDateTime nowInCentral = ZonedDateTime.now(ZoneId.of("America/Chicago"));
						// Extract the day of the week
				        DayOfWeek dayOfWeek = nowInCentral.getDayOfWeek();
				        // Print the day of the week in full text format (e.g., "Monday", "Tuesday", ...)
				        // Locale.ENGLISH is used to ensure the output is in English. You can adjust the locale as needed.
				        String dayOfWeekText = dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH);
				        dayOfWeekText = dayOfWeekText.toLowerCase();
				        data.addToLog(data.getCurrentElement(), "Day of week in Central : " + dayOfWeekText);
				        dayObj = (JSONObject)timetableObj.get(dayOfWeekText);
				        
						data.addToLog(data.getCurrentElement(), "dayObj " + dayObj.toString());

						String timewindow = (String) dayObj.get("timewindow");

						ccOpenFlag = checkBusinessHour(timewindow, data, caa);
					}
				} else {
					data.addToLog(data.getCurrentElement(), "Contact Centre Name noy exist in API Response " + ccName);
				}
			}
			
			
			data.addToLog(data.getCurrentElement(), "Contact Centre Name : " + ccName + " Is Open : " + ccOpenFlag);
			if (ccOpenFlag) {
				data.setSessionData(Constants.S_CALLCENTER_OPEN_CLOSED, Constants.S_OPEN);
			} else {
				data.setSessionData(Constants.S_CALLCENTER_OPEN_CLOSED, Constants.CLOSED);
			}
		} catch (Exception e) {
			data.addToLog(data.getCurrentElement(), "Exception in isCCOpen  :: " + e);
			caa.printStackTrace(e);
		}
		return ccOpenFlag;
	}

	private boolean checkBusinessHour(String timewindow, DecisionElementData data, CommonAPIAccess caa) {
		boolean isOpen = false;

		try {
			data.addToLog(data.getCurrentElement(), "Time window check for : " + timewindow);
			if (timewindow.contains("."))
				timewindow = timewindow.replaceAll("\\.", ":");
			isOpen = TimezoneCheck.checkwithinTime(timewindow, "CT", data);
		} catch (Exception e) {
			data.addToLog(data.getCurrentElement(), "Exception in checkBusinessHour  :: " + e);
			caa.printStackTrace(e);
		}
		return isOpen;
	}

	// logic is pending to pick final DNIS from list of dnis with respective open
	// call centers
	private String pickDestNumForCalltransfer(DecisionElementData data, CommonAPIAccess caa, List<RoutingBean> beanList, String routeRuleComboName) {
		String finalDestNum = Constants.EmptyString;
		String destCCName = Constants.EmptyString;
		List<DestinationWeightage> destList = null;
		String servicingPhoneNumberEnabled = (String)data.getSessionData("S_SERVICING_PHONE_NUMBER_ENABLED");
		String servicingPhoneNumber = (String) data
				.getSessionData("S_FDS_SERVICING_PHONE_NUMBER");
		DestinationDecider objDestinationDecider = new DestinationDecider();

		try {
			data.addToLog(data.getCurrentElement(), "No of call centers opend  : " + beanList.size());
			data.addToLog(data.getCurrentElement(), "BeanList taken for Pick Dest : " + beanList);
			data.addToLog(data.getCurrentElement(), "Route Rule combo name : " + routeRuleComboName);
			if (beanList.size() > 1) {
				destList = CollectionOfDestinationCombos.getInstance().getCombos(routeRuleComboName);
				if (destList != null) {
					data.addToLog(data.getCurrentElement(), "Dest List not null");
					destCCName = objDestinationDecider.getDestinationBasedOnHistory(destList, data);
					data.addToLog(data.getCurrentElement(), "Picked Dest Name : " + destCCName);
					for (Iterator<RoutingBean> iterator = beanList.iterator(); iterator.hasNext();) {
						RoutingBean rBean = iterator.next();
						if (rBean.getCcName().equalsIgnoreCase(destCCName)) {
							 //Start CS1175332-Farmers Direct Services (FDS/Personal Lines) - Billing & Payments - CA Payroll Deduct
							 if(servicingPhoneNumberEnabled!=null && !servicingPhoneNumberEnabled.isEmpty() && servicingPhoneNumberEnabled.equalsIgnoreCase(Constants.Y_FLAG)) {
								 data.addToLog(data.getCurrentElement(),"SERVICING PHONE NUMBER ENABLED GPC MATCHED AND PAYPLAN MATCHED::");
								if(servicingPhoneNumber !=null && !servicingPhoneNumber.isEmpty()) {
									finalDestNum = servicingPhoneNumber;	
								}else {
									finalDestNum = rBean.getDestinationNum();
									data.addToLog(data.getCurrentElement(),"Servicing phone Number not available for the brand FDS ::"+servicingPhoneNumber);
								}
							}
							else {
								finalDestNum = rBean.getDestinationNum();
							}
                           //End CS1175332-Farmers Direct Services (FDS/Personal Lines) - Billing & Payments - CA Payroll Deduct
							data.setSessionData(Constants.S_ROUTING_PRIORITY, rBean.getPriority());
							data.setSessionData(Constants.S_SCREEN_POPUP_KEY, rBean.getScreenPopUp());
							break;
						}
					}
					CollectionOfDestinationCombos.getInstance().removeAndReplaceIfExistOrAddNewEntry(routeRuleComboName, destList, data);
				} else {
					data.addToLog(data.getCurrentElement(), "Dest List null, adding entry");
					destList = new ArrayList<DestinationWeightage>();
					for (Iterator<RoutingBean> iterator = beanList.iterator(); iterator.hasNext();) {
						data.addToLog(data.getCurrentElement(), "Entered inside for loop to add entries into destList");
						RoutingBean routingBean = (RoutingBean) iterator.next();
						DestinationWeightage objDestinationWeightage = new DestinationWeightage();
						try {
							objDestinationWeightage.setDestinationIdentifier(routingBean.getCcName());
							if (null != routingBean.getCcAllocation()) {
								int percentage = Integer.parseInt(routingBean.getCcAllocation().replaceAll("%", Constants.EmptyString));
								objDestinationWeightage.setWeightage(percentage);
								objDestinationWeightage.setWeightageUsedSoFar(percentage);
							}
							destList.add(objDestinationWeightage);
						} catch (Exception e) {
							data.addToLog(data.getCurrentElement(),
									"Exception while adding details to destList : " + e);
							caa.printStackTrace(e);
						}
					}
					data.addToLog(data.getCurrentElement(), "DestList : " + destList);
					destCCName = objDestinationDecider.getDestinationBasedOnHistory(destList, data);
					data.addToLog(data.getCurrentElement(), "Picked Dest Name : " + destCCName);
					for (Iterator<RoutingBean> iterator = beanList.iterator(); iterator.hasNext();) {
						RoutingBean rBean = iterator.next();
						if (rBean.getCcName().equalsIgnoreCase(destCCName)) {
							 //Start CS1175332-Farmers Direct Services (FDS/Personal Lines) - Billing & Payments - CA Payroll Deduct
							 if(servicingPhoneNumberEnabled!=null && !servicingPhoneNumberEnabled.isEmpty() && servicingPhoneNumberEnabled.equalsIgnoreCase(Constants.Y_FLAG)) {
								data.addToLog(data.getCurrentElement(),"SERVICING PHONE NUMBER ENABLED GPC MATCHED AND PAYPLAN MATCHED::");
								if(servicingPhoneNumber !=null && !servicingPhoneNumber.isEmpty()) {
									finalDestNum = servicingPhoneNumber;	
								}else {
									finalDestNum = rBean.getDestinationNum();
									data.addToLog(data.getCurrentElement(),"Servicing phone Number not available for the brand FDS ::"+servicingPhoneNumber);
								}
							}
							else {
								finalDestNum = rBean.getDestinationNum();
							}
                           //End CS1175332-Farmers Direct Services (FDS/Personal Lines) - Billing & Payments - CA Payroll Deduct
							data.setSessionData(Constants.S_ROUTING_PRIORITY, rBean.getPriority());
							data.setSessionData(Constants.S_SCREEN_POPUP_KEY, rBean.getScreenPopUp());
							break;
						}
					}
					CollectionOfDestinationCombos.getInstance().removeAndReplaceIfExistOrAddNewEntry(routeRuleComboName, destList, data);
				}
				data.addToLog(data.getCurrentElement(), "Dest CC Name picked : " + destCCName);
			} else {
				//Start CS1175332-Farmers Direct Services (FDS/Personal Lines) - Billing & Payments - CA Payroll Deduct
				 if(servicingPhoneNumberEnabled!=null && !servicingPhoneNumberEnabled.isEmpty() && servicingPhoneNumberEnabled.equalsIgnoreCase(Constants.Y_FLAG)) {
					data.addToLog(data.getCurrentElement(),"SERVICING PHONE NUMBER ENABLED GPC MATCHED AND PAYPLAN MATCHED::");
					if(servicingPhoneNumber !=null && !servicingPhoneNumber.isEmpty()) {
						finalDestNum = servicingPhoneNumber;	
					}else {
						data.addToLog(data.getCurrentElement(),"Servicing phone Number not available for the brand FDS ::"+servicingPhoneNumber);
					}
					
				}
				else {
					finalDestNum = beanList.get(0).getDestinationNum();
				}
				//End CS1175332-Farmers Direct Services (FDS/Personal Lines) - Billing & Payments - CA Payroll Deduct
				data.setSessionData(Constants.S_ROUTING_PRIORITY, beanList.get(0).getPriority());
				data.setSessionData(Constants.S_SCREEN_POPUP_KEY, beanList.get(0).getScreenPopUp());
			}
		} catch (Exception e) {
			data.addToLog(data.getCurrentElement(), "Exception in pickDestNumForCalltransfer  :: " + e);
			caa.printStackTrace(e);
		}

		data.addToLog(data.getCurrentElement(), "S_FINAL_DESTNUM  : " + finalDestNum);
		return finalDestNum;
	}

	@SuppressWarnings("unused")
	private List<JSONObject> routingSorting(List<JSONObject> reqList, String sessionKey, String routingRespKey, DecisionElementData data) {
		data.addToLog(data.getCurrentElement(), "Routing reqList size before sorting with " + sessionKey + " is : " + reqList.size());
		List<JSONObject> respList = new ArrayList<JSONObject>();
		List<JSONObject> respEmptyList = new ArrayList<JSONObject>();
		//if (reqList.size() == 1)
			//return reqList;
		String sessKey = (String) data.getSessionData(sessionKey);
		data.addToLog(data.getCurrentElement(), sessionKey + " value from session : " + sessKey);
		if (sessKey == null) {
			data.addToLog(data.getCurrentElement(), "Entering null loop");
			for (JSONObject tempObj : reqList) {
				if (tempObj.get(routingRespKey).toString().equalsIgnoreCase(Constants.EmptyString)) {
					respEmptyList.add(tempObj);
				}
			}
		} else if (sessKey.equalsIgnoreCase(Constants.EmptyString)) {
			data.addToLog(data.getCurrentElement(), "Entering empty loop");
			for (JSONObject tempObj : reqList) {
				if (tempObj.get(routingRespKey).toString().equalsIgnoreCase(Constants.EmptyString) ) {
					respEmptyList.add(tempObj);
				}
			}
		} else {
			data.addToLog(data.getCurrentElement(), "Entering not empty loop");
			for (JSONObject tempObj : reqList) {
				//if (tempObj.get(routingRespKey).toString().equalsIgnoreCase(Constants.sessKey)) {
				if (tempObj.get(routingRespKey).toString().equalsIgnoreCase(sessKey)) {
					respList.add(tempObj);
				} else {
					if(tempObj.get(routingRespKey).toString().equalsIgnoreCase(Constants.EmptyString)) {
						respEmptyList.add(tempObj);
					}
				}
			}
		}
		if(respList.isEmpty()) {
			data.addToLog(data.getCurrentElement(),"Sorting list not match, returning empty sets : " + respList.size() + " : " + respList);
			respList = respEmptyList;
		}
		data.addToLog(data.getCurrentElement(),"Routing resp list size post sorting with " + sessionKey + " : " + respList.size());
		data.addToLog(data.getCurrentElement(),"Routing resp list post sorting with " + sessionKey + " : " + respList);
		return respList;
	}

	
	@SuppressWarnings("unused")
	private List<JSONObject> routingSortingLang(List<JSONObject> reqList, String sessionKey, String routingRespKey,
			DecisionElementData data) {
		data.addToLog(data.getCurrentElement(), "Routing reqList size before sorting with " + sessionKey + " is : " + reqList.size());
		List<JSONObject> respList = new ArrayList<JSONObject>();
		//if (reqList.size() == 1)
			//return reqList;
		String sessKey = (String) data.getSessionData(sessionKey);
		data.addToLog(data.getCurrentElement(), sessionKey + " value from session : " + sessKey);
		for (JSONObject tempObj : reqList) {
			//if (Constants.EmptyString.equalsIgnoreCase(tempObj.get(routingRespKey).toString()) && sessKey.equals(Constants.English)) {
			if (Constants.EmptyString.equalsIgnoreCase(tempObj.get(routingRespKey).toString()) && "EN".equalsIgnoreCase((String) data.getSessionData("S_PREF_LANG"))) {
				data.addToLog(data.getCurrentElement(), "Adding empty lang entry");
				respList.add(tempObj);
			} else {
				if (tempObj.get(routingRespKey).toString().equalsIgnoreCase(sessKey)) {
					respList.add(tempObj);
				}
			}
		}
		data.addToLog(data.getCurrentElement(),"Routing resp list size post sorting with size " + sessionKey + " : " + respList.size());
		data.addToLog(data.getCurrentElement(),"Routing resp list post sorting with " + sessionKey + " : " + respList);	
		return respList;
	}

	
	@SuppressWarnings("unused")
	private Set<JSONObject> routingSortingStateGroup(List<JSONObject> reqList, String sessionKey,String routingRespKey, DecisionElementData data) {
		data.addToLog(data.getCurrentElement(),"Routing reqList size before sorting with " + sessionKey + " is : " + reqList.size());
		Set<JSONObject> respSet = new HashSet<JSONObject>();
		Set<JSONObject> emptySet = new HashSet<JSONObject>();
		//if (reqList.size() == 1)
		//return reqList;

		try {
			@SuppressWarnings("unchecked")
			List<String> stateGroups = (List<String>) data.getSessionData(sessionKey);
			if (stateGroups != null) {
				data.addToLog(data.getCurrentElement(), "State groups : " + stateGroups);		
				for (Iterator<String> iterator = stateGroups.iterator(); iterator.hasNext();) {
					String stateGroupName = iterator.next();
					data.addToLog(data.getCurrentElement(), "State group for check : " + stateGroupName);
					for (JSONObject tempObj : reqList) {
						if(tempObj.get(routingRespKey).toString().equalsIgnoreCase(Constants.EmptyString)) {
							emptySet.add(tempObj);
						} else {
							if (tempObj.get(routingRespKey).toString().equalsIgnoreCase(stateGroupName)) {
								data.addToLog(data.getCurrentElement(), "State group matched and added : " + stateGroupName);
								data.setSessionData(Constants.S_FINAL_STATE_GROUP, stateGroupName);
								
								//CS1159483 : Update Final State Group in Original State Group for reporting purpose
								data.setSessionData(Constants.S_ORIGINAL_STATE_GROUP, stateGroupName);
								data.addToLog(data.getCurrentElement(), "Original State group updated : " + stateGroupName);
								
								respSet.add(tempObj);
							}	
						}
					}
				}
			} else {
				data.addToLog(data.getCurrentElement(), "State group is null");
				for (JSONObject tempObj : reqList) {
					if(Constants.EmptyString.equalsIgnoreCase(tempObj.get(routingRespKey).toString())) {
						emptySet.add(tempObj);
					}
				}
			}
		} catch (Exception e) {
			data.addToLog(data.getCurrentElement(), "Exception in State group sorting ");
			if (respSet.size() == 0) {
				return new HashSet<JSONObject>(reqList);
			} else {
				return respSet;
			}
		}
		if(respSet.isEmpty()) {
			respSet = emptySet;
		}
		data.addToLog(data.getCurrentElement(), "Routing resp list size post sorting with " + sessionKey + " : " + respSet.size());
		data.addToLog(data.getCurrentElement(), "Routing resp set size post sorting with " + sessionKey + " : " + respSet);	
		return respSet;
	}
	
	private Map<String,String> holidayCheck(JSONObject mainObj, String ccName, DecisionElementData data, CommonAPIAccess caa) {		
		Map<String,String> retList = new HashMap<String,String>();
		String ccOverrideFlag = Constants.FALSE;
		data.addToLog(data.getCurrentElement(), "Time table name for check holiday : " + ccName);
		
		if (mainObj.containsKey(ccName)) {
			JSONArray exceptiondayOverrideArray = (JSONArray) mainObj.get(ccName);
			if(exceptiondayOverrideArray != null) {
				if(exceptiondayOverrideArray.size() > 0) {
					for (@SuppressWarnings("rawtypes")
					Iterator iterator = exceptiondayOverrideArray.iterator(); iterator.hasNext();) {
						JSONObject overrideObject = (JSONObject) iterator.next();
						String startDateString = (String)overrideObject.get("startdatetime");
						String stopDateString = (String)overrideObject.get("stopdatetime");
						String latestartdatetime = (String)overrideObject.get("latestartdatetime");
						
						if(latestartdatetime!=null && !Constants.EmptyString.equals(latestartdatetime))
							startDateString=latestartdatetime;
						
						// Parse the date string to a LocalDateTime
				        LocalDateTime startDateTime = LocalDateTime.parse(startDateString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
				        LocalDateTime stopDateTime = LocalDateTime.parse(stopDateString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
				        // Convert LocalDateTime to LocalDate
				        
				        data.addToLog(data.getCurrentElement(), "Override start date returned : " + startDateTime);
				        data.addToLog(data.getCurrentElement(), "Override end date returned : " + stopDateTime);
				        // Get the current date
				        ZonedDateTime nowInCentral = ZonedDateTime.now(ZoneId.of("America/Chicago"));
				        LocalDateTime today = nowInCentral.toLocalDateTime();
				        data.addToLog(data.getCurrentElement(), "current day : " + today);
				        // Compare the dates
				        if(today.isAfter(startDateTime) && today.isBefore(stopDateTime)) {
				        	data.addToLog(data.getCurrentElement(), "Today is Holiday ");
				        	JSONArray holidayrecordingsArr = (JSONArray)overrideObject.get("holidayrecordings");
				        	JSONObject holidayrecordingsObj = (JSONObject) holidayrecordingsArr.get(0);
				        	
				        	retList.put("audioenglishtext",""+holidayrecordingsObj.get("audioenglishtext"));
				    		retList.put("audioenglishwavpath",""+holidayrecordingsObj.get("audioenglishwavpath"));
				    		retList.put("audiospanishtext",""+holidayrecordingsObj.get("audiospanishtext"));
				    		retList.put("audiospanishwavpath",""+holidayrecordingsObj.get("audiospanishwavpath"));
				        					        	
				        	data.addToLog(data.getCurrentElement(), "audio english text : " + holidayrecordingsObj.get("audioenglishtext"));
				        	data.addToLog(data.getCurrentElement(), "audio english wav path : " + holidayrecordingsObj.get("audioenglishwavpath"));
				        	data.addToLog(data.getCurrentElement(), "audio spanish text : " + holidayrecordingsObj.get("audiospanishtext"));
				        	data.addToLog(data.getCurrentElement(), "audio spanish wav path : " + holidayrecordingsObj.get("audiospanishwavpath"));
				        	ccOverrideFlag = Constants.TRUE;
				        	//breaking for loop
				        	break;
				        } else {
				        	data.addToLog(data.getCurrentElement(), "Override day not matching for : " + ccName);
				        }
					}
				} else {
					data.addToLog(data.getCurrentElement(), "No Exception overrides for : " + ccName);
				}
			} else {
				data.addToLog(data.getCurrentElement(), "No Exception overrides for : " + ccName);
			}
		}
		retList.put("HolidayFlag",ccOverrideFlag);
		
		data.addToLog(data.getCurrentElement(), "Override return list : " + retList);
		return retList;
	}
	
	private String brandsLabeLookup(String url, String callId, int tenantId, String dnis, String dnisgroup, String lob, String category, int conntimeout, int readtimeout, LoggerContext context, DecisionElementData data) throws AudiumException {
		
		String brandLabel = Constants.EmptyString;
		
		GetBrandTableByBusinessObjects objBrandTableByBusinessObjects = new GetBrandTableByBusinessObjects();
		
		JSONObject respBody = null;
		JSONObject objResp = objBrandTableByBusinessObjects.start(url, callId, tenantId, dnis, dnisgroup, lob, category, conntimeout, readtimeout, context);
		
		String strReqBody = Constants.EmptyString;
		
		if (objResp != null) {
			if (objResp.containsKey(Constants.REQUEST_BODY))
				strReqBody = objResp.get(Constants.REQUEST_BODY).toString();
			data.addToLog(getElementName(), "Req Body : " + strReqBody);
			if (objResp.containsKey(Constants.RESPONSE_CODE) && (int) objResp.get(Constants.RESPONSE_CODE) == 200) {
				respBody = (JSONObject) objResp.get(Constants.RESPONSE_BODY);
				data.addToLog(getElementName(), "Resp Body : " + respBody);
			}
		}
		
		if(respBody != null) {
			JSONArray resArray = (JSONArray) respBody.get("res");
			data.addToLog(getElementName(), "Brands Result : " + objResp);
			if (resArray == null || resArray.size() == 0) {
				data.addToLog(getElementName(), "Brands Result Array 0");
			} else {
				data.addToLog(getElementName(), "Brands Result Array Size : " + resArray.size());
				JSONObject brandObject = (JSONObject) resArray.get(0);
				data.addToLog(getElementName(), "Brands objects : " + brandObject);
				brandLabel = (String)brandObject.get("brandlabel");
				data.addToLog(getElementName(), "Brands Label : " + brandLabel);
				data.setSessionData(S_ROUTING_BRAND_LABEL, brandLabel);
			}
		}
		return brandLabel;
	}
	
	private String mspTableLookup(String url, String callid, int tenantId, String mspKey, int conntimeout, int readtimeout, LoggerContext context, DecisionElementData data) throws AudiumException {
		
		String retDNIS = Constants.EmptyString;
		
		GetMenuSelectionPropertyTableByKey objGetMenuSelectionPropertyTableByKey = new GetMenuSelectionPropertyTableByKey();
		
		JSONObject objResp = objGetMenuSelectionPropertyTableByKey.start(url, callid, tenantId, mspKey, conntimeout, readtimeout, context);
		JSONObject respBody = null;
		
		String[] msps = null;
		if(mspKey.contains("|")) {
			msps = mspKey.split("\\|");
			
		}
		data.addToLog(getElementName(), "MSPs : " + Arrays.toString(msps));
		
		String strReqBody = Constants.EmptyString;
		
		if (objResp != null) {
			if (objResp.containsKey(Constants.REQUEST_BODY))
				strReqBody = objResp.get(Constants.REQUEST_BODY).toString();
			data.addToLog(getElementName(), "Req Body : " + strReqBody);
			if (objResp.containsKey(Constants.RESPONSE_CODE) && (int) objResp.get(Constants.RESPONSE_CODE) == 200) {
				respBody = (JSONObject) objResp.get(Constants.RESPONSE_BODY);
				data.addToLog(getElementName(), "Resp Body : " + respBody);
			}
		}
		
		if(respBody != null) {
			JSONArray resArray = (JSONArray) respBody.get("res");
			
			data.addToLog(getElementName(), "MSP Result : " + objResp);
			
			JSONObject mspObject = null;
			
			String dnis = Constants.EmptyString;
			String category = Constants.EmptyString;
			String nlintentrollup = Constants.EmptyString;
			String intent = Constants.EmptyString;
			int id = 0;
			int tempId = 0;
			
			if (resArray == null || resArray.size() == 0) {
				data.addToLog(getElementName(), "MSP Array 0");
			} else {
				data.addToLog(getElementName(), "MSP Result Array Size : " + resArray.size());
				for (int i = 0; i < resArray.size(); i++) {
					mspObject = (JSONObject)resArray.get(i);	
					Long temp = (Long) mspObject.get("id");
					tempId = temp.intValue();
					dnis = (String)mspObject.get("dnis");
					category = (String)mspObject.get("category");
					nlintentrollup = (String)mspObject.get("nlintentrollup");
					intent = (String)mspObject.get("intent");
					if(tempId > id) {
						id = tempId;
					}
				}
				
				data.addToLog(getElementName(), "MSP Result Last available ID : " + id);
				
				if(msps != null) {
					for (int i = msps.length - 1; i >= 0; i--) {
						String currMsp = msps[i];
						boolean flag = false;
						data.addToLog(getElementName(), "Current MSP returned : " + currMsp);
						for (int j = 0; j < resArray.size(); j++) {
							mspObject = (JSONObject)resArray.get(j);
							if(currMsp.equalsIgnoreCase((String)mspObject.get("key"))) {
								dnis = (String)mspObject.get("dnis");
								category = (String)mspObject.get("category");
								nlintentrollup = (String)mspObject.get("nlintentrollup");
								intent = (String)mspObject.get("intent");
								data.addToLog(getElementName(), "Current MSP Matched : " + currMsp);
								flag = true;
								break;
							}
						}
						if(flag) {
							data.addToLog(getElementName(), "breaking loop");
							break;	
						}
					}
				} else {
					mspObject = (JSONObject)resArray.get(0);
					dnis = (String)mspObject.get("dnis");
					category = (String)mspObject.get("category");
					nlintentrollup = (String)mspObject.get("nlintentrollup");
					intent = (String)mspObject.get("intent");
					data.addToLog(getElementName(), "Current MSP set in session : " + mspObject);
				}
				data.addToLog(getElementName(), "MSP objects : " + mspObject);
				
				data.addToLog(getElementName(), "MSP dnis : " + dnis);
				data.addToLog(getElementName(), "MSP category : " + category);
				data.addToLog(getElementName(), "MSP nlintentrollup : " + nlintentrollup);
				data.addToLog(getElementName(), "MSP intent : " + intent);
				
				data.setSessionData(Constants.S_FINAL_DNIS, dnis);
				
				if(intent != null) {
					if(!intent.equalsIgnoreCase(Constants.EmptyString)) {
						data.setSessionData(Constants.S_FINAL_INTENT, intent);
					}
				}
				retDNIS = dnis;
			}
		}
		return retDNIS;
	}
	
	private boolean updateBO(String mspDnis, DecisionElementData data) {
		
		boolean isBOUpdated = false;
		
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);objHostDetails.setinitalValue();
		data.addToLog(data.getCurrentElement(), "BusinessObjects started");
		
		try {
			if(data.getSessionData(Constants.S_BUSSINESS_OBJECTS_URL) != null && data.getSessionData(Constants.S_CONN_TIMEOUT) != null && data.getSessionData(Constants.S_READ_TIMEOUT) != null) {
				String url = (String)data.getSessionData(Constants.S_BUSSINESS_OBJECTS_URL);
				String callerId = (String) data.getSessionData(Constants.S_CALLID);
				int conTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_CONN_TIMEOUT));
				int readTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_READ_TIMEOUT));
				LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
				data.addToLog(data.getCurrentElement(), "strDnis  :"+mspDnis+ " :: API url : "+data.getSessionData(Constants.S_BUSSINESS_OBJECTS_URL)+ " :: conTimeout : "+conTimeout);
				GetBusinessObjectsByDnisKey objBusinessObjects = new GetBusinessObjectsByDnisKey();
				JSONObject resp =  (JSONObject) objBusinessObjects.start(url, callerId, Constants.tenantid, mspDnis , conTimeout, readTimeout,context);
				data.addToLog(data.getCurrentElement(), "GetBusinessObjectsByDnisKey API response  :"+resp);
				if(resp != null) {
					if(resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200) {
						data.addToLog(data.getCurrentElement(), "" );
						
						JSONObject respObj = (JSONObject)resp.get(Constants.RESPONSE_BODY);
						JSONArray resArr = (JSONArray)respObj.get("res");
						
						if(resArr.size() == 1) {
							
							isBOUpdated = true;
							
							JSONObject obj = (JSONObject) resArr.get(0);
							
							if(obj.get("dnisgroup") != null) {
								if(!obj.get("dnisgroup").toString().equalsIgnoreCase(Constants.EmptyString)) {
									data.setSessionData(Constants.S_FINAL_DNIS_GROUP, obj.get("dnisgroup"));
								}
							}
							if(obj.get("category") != null) {
								if(!obj.get("category").toString().equalsIgnoreCase(Constants.EmptyString)) {
									data.setSessionData(Constants.S_FINAL_CATEGORY, obj.get("category"));
								}
							}
							if(obj.get("lineofbusiness") != null) {
								if(!obj.get("lineofbusiness").toString().equalsIgnoreCase(Constants.EmptyString)) {
									data.setSessionData(Constants.S_FINAL_LOB, obj.get("lineofbusiness"));
								}
							}
						}		
					}
				}
			}
		} catch (Exception e) {
			data.addToLog(data.getCurrentElement(),"Exception in GetBusinessObjectsByDnisKey API call  :: "+e);
			caa.printStackTrace(e);
		}
		return isBOUpdated;
	}
	
	private JSONObject getDefaultRouteRule(List<JSONObject> list, DecisionElementData data) {
		JSONObject routeRule = null;

		List<JSONObject> tempList1 = new ArrayList<JSONObject>();
		List<JSONObject> tempList2 = new ArrayList<JSONObject>();

		tempList1 = new ArrayList<JSONObject>(routingDefaultSortingStateGroup(list, Constants.S_STATE_GROUP, Constants.StateGroup, data));
		
		tempList2 = routingDefaultSortingLang(tempList1, Constants.S_FINAL_LANG, Constants.Language, data);
		tempList1 = new ArrayList<JSONObject>();

		// S_BRAND_LABEL sub attribute sorting
		tempList1 = routingDefaultSorting(tempList2, S_ROUTING_BRAND_LABEL, Constants.BrandLabel, data);
		tempList2 = new ArrayList<JSONObject>();

		// S_ANI_GROUP sub attribute sorting
		tempList2 = routingDefaultSorting(tempList1, Constants.S_ANI_GROUP, Constants.AnigroupHandling, data);
		tempList1 = new ArrayList<JSONObject>();

		// S_POLICY_SEGMENTATION sub attribute sorting
		tempList1 = routingDefaultSorting(tempList2, Constants.S_POLICY_SEGMENTATION, Constants.PolicySegmentation, data);
		tempList2 = new ArrayList<JSONObject>();

		// S_AGENT_SEGMENTATION sub attribute sorting
		tempList2 = routingDefaultSorting(tempList1, Constants.S_AGENT_SEGMENTATION, Constants.AgentSegmentation, data);
		tempList1 = new ArrayList<JSONObject>();

		// S_POLICY_SOURCE sub attribute sorting
		tempList1 = routingDefaultSorting(tempList2, Constants.S_POLICY_SOURCE, Constants.PolicySource, data);
		tempList2 = new ArrayList<JSONObject>();

		// S_POLICY_ATTRIBUTES sub attribute sorting
		tempList2 = routingDefaultSorting(tempList1, Constants.S_POLICY_ATTRIBUTES, Constants.PolicyAttributes, data);
		
		if(tempList2.size() > 0) {
			data.addToLog(data.getCurrentElement(), "Default Route Rule picker size : " + tempList2.size());
			data.addToLog(data.getCurrentElement(), "Default Route Rule picker : " + tempList2);
			routeRule = (JSONObject)tempList2.get(0);
			data.addToLog(data.getCurrentElement(), "Default Route Rule picked : " + routeRule.toJSONString());	
		} else {
			data.addToLog(data.getCurrentElement(), "Default Route Rule returned size : " + tempList2.size());
		}
		return routeRule;
	}
	
	@SuppressWarnings("unused")
	private List<JSONObject> routingDefaultSorting(List<JSONObject> reqList, String sessionKey, String routingRespKey, DecisionElementData data) {
		data.addToLog(data.getCurrentElement(), "Routing reqList size before sorting with " + sessionKey + " is : " + reqList.size());
		List<JSONObject> respList = new ArrayList<JSONObject>();
		List<JSONObject> respEmptyList = new ArrayList<JSONObject>();
		//if (reqList.size() == 1)
		//return reqList;
		String sessKey = Constants.EmptyString;
		data.addToLog(data.getCurrentElement(), sessionKey + " value from session : " + sessKey);
		if (sessKey == null) {
			data.addToLog(data.getCurrentElement(), "Entering null loop");
			for (JSONObject tempObj : reqList) {
				if (tempObj.get(routingRespKey).toString().equalsIgnoreCase(Constants.EmptyString)) {
					respEmptyList.add(tempObj);
				}
			}
		} else if (sessKey.equalsIgnoreCase(Constants.EmptyString)) {
			data.addToLog(data.getCurrentElement(), "Entering empty loop");
			for (JSONObject tempObj : reqList) {
				if (tempObj.get(routingRespKey).toString().equalsIgnoreCase(Constants.EmptyString)) {
					respEmptyList.add(tempObj);
				}
			}
		} else {
			data.addToLog(data.getCurrentElement(), "Entering not empty loop");
			for (JSONObject tempObj : reqList) {
				//if (tempObj.get(routingRespKey).toString().equalsIgnoreCase(Constants.sessKey)) {
				if (tempObj.get(routingRespKey).toString().equalsIgnoreCase(sessKey)) {
					respList.add(tempObj);
				} else {
					if(tempObj.get(routingRespKey).toString().equalsIgnoreCase(Constants.EmptyString)) {
						respEmptyList.add(tempObj);
					}
				}
			}
		}
		data.addToLog(data.getCurrentElement(),"Routing resp list size post sorting with default values " + sessionKey + " : " + respEmptyList.size());
		data.addToLog(data.getCurrentElement(),"Routing resp list post sorting with " + sessionKey + " : " + respEmptyList);
		return respEmptyList;
	}

	
	@SuppressWarnings("unused")
	private List<JSONObject> routingDefaultSortingLang(List<JSONObject> reqList, String sessionKey, String routingRespKey,
			DecisionElementData data) {
		data.addToLog(data.getCurrentElement(), "Routing reqList size before sorting with " + sessionKey + " is : " + reqList.size());
		List<JSONObject> respList = new ArrayList<JSONObject>();
		//if (reqList.size() == 1)
			//return reqList;
		String sessKey = (String) data.getSessionData(sessionKey);
		data.addToLog(data.getCurrentElement(), sessionKey + " value from session : " + sessKey);
		for (JSONObject tempObj : reqList) {
			if (Constants.EmptyString.equalsIgnoreCase(tempObj.get(routingRespKey).toString()) && sessKey.equals(Constants.English)) {
				respList.add(tempObj);
			} else {
				if (tempObj.get(routingRespKey).toString().equalsIgnoreCase(sessKey)) {
					respList.add(tempObj);
				}
			}
		}
		data.addToLog(data.getCurrentElement(),"Routing resp list size post sorting with default routing " + sessionKey + " : " + respList.size());
		data.addToLog(data.getCurrentElement(),"Routing resp list post sorting with default routing " + sessionKey + " : " + respList);	
		return respList;
	}

	
	@SuppressWarnings("unused")
	private Set<JSONObject> routingDefaultSortingStateGroup(List<JSONObject> reqList, String sessionKey,String routingRespKey, DecisionElementData data) {
		data.addToLog(data.getCurrentElement(),"Routing reqList size before sorting with " + sessionKey + " is : " + reqList.size());
		Set<JSONObject> emptySet = new HashSet<JSONObject>();
		
		try {
			data.addToLog(data.getCurrentElement(), "State group default values");
			for (JSONObject tempObj : reqList) {
				if(Constants.EmptyString.equalsIgnoreCase(tempObj.get(routingRespKey).toString())) {
					emptySet.add(tempObj);
				}
			}
		} catch (Exception e) {
			data.addToLog(data.getCurrentElement(), "Exception in State group sorting ");
		}
		data.addToLog(data.getCurrentElement(), "Routing resp list size post sorting with default " + sessionKey + " : " + emptySet.size());
		data.addToLog(data.getCurrentElement(), "Routing resp set size post sorting with default " + sessionKey + " : " + emptySet);	
		return emptySet;
	}
	//CS1151307 : Update policy state based routing
	private void callStateGroupAPI(String currElementName, DecisionElementData data) {
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);objHostDetails.setinitalValue();
		String strReqBody = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;
		String strExitState = Constants.ER;
		
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		
		try {
			if(data.getSessionData(Constants.S_GET_STATE_KEY_BY_NAME_URL)  != null  && data.getSessionData(Constants.S_CONN_TIMEOUT) != null && data.getSessionData(Constants.S_READ_TIMEOUT) != null) {
				String url = (String)data.getSessionData(Constants.S_GET_STATE_KEY_BY_NAME_URL);
				String callerId = (String) data.getSessionData(Constants.S_CALLID);
				String strStateName = (String) data.getSessionData(Constants.S_STATENAME);
				int conTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_CONN_TIMEOUT));
				int readTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_READ_TIMEOUT));
				LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
				GetStateGroupTableByStateName test = new GetStateGroupTableByStateName(); 
				JSONObject resp =  (JSONObject) test.start(url, callerId, Constants.tenantid, strStateName , conTimeout, readTimeout, context);
				strRespBody = resp.toString();
				data.addToLog(currElementName, "State Group by State Name API response  :"+resp);
				
				//Mustan - Alerting Mechanism ** Response Code Capture
				apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
				if(resp != null) {
					if(resp.containsKey(Constants.REQUEST_BODY)) strReqBody = resp.get(Constants.REQUEST_BODY).toString();
					if(resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200) {
						data.addToLog(currElementName, "Set StateAreaCode API Response into session with the key name of "+currElementName+Constants._RESP);
						strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
						data.setSessionData(currElementName+Constants._RESP, resp.get(Constants.RESPONSE_BODY));
						strExitState = apiResponseManupulationStateGroup(data, caa, currElementName, strRespBody);
					}
				}
			}
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in State Group by State Name API call :: "+e);
			caa.printStackTrace(e);
		}
		
			try {
				objHostDetails.startHostReport(currElementName,"Get StateGroup by State Name API", strReqBody, "",(String)data.getSessionData(Constants.S_GET_STATE_KEY_BY_NAME_URL));
				objHostDetails.endHostReport(data,strRespBody , strExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode," ");
			} catch (Exception e) {
				data.addToLog(currElementName,"Exception while forming host reporting for StateAreaCode API call :: "+e);
				caa.printStackTrace(e);
			}
	}
	
	private String apiResponseManupulationStateGroup(DecisionElementData data, CommonAPIAccess caa, String currentElementName, String responseBody) {
		String strExitState = Constants.ER;
		ArrayList<String> stateGroup = new ArrayList<String>();
		try {	
			JSONObject resp = (JSONObject) new JSONParser().parse(responseBody);
			JSONArray resArr = (JSONArray)resp.get("res");
			if(resArr.size() == 0 ) return Constants.ER;
			for(int i = 0; i<resArr.size();i++) {
				JSONObject stategObj = (JSONObject) resArr.get(i);
				String key =(String)stategObj.get("key");
				stateGroup.add(key);
				data.addToLog(currentElementName, "key :"+key);
			
			}
			data.addToLog(currentElementName, "State Group :"+stateGroup.toString());
			data.setSessionData(Constants.S_STATE_GROUP, stateGroup);
			strExitState = Constants.SU;
		} catch (Exception e) {
			data.addToLog(currentElementName,"Exception in apiResponseManupulation method :: "+e);
			caa.printStackTrace(e);
		}
		return strExitState;
	}
	

}