package com.farmers.adminHost;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
import com.farmers.AdminAPI.GetPrequeueMessageRulesByBusinessObjects;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

/*KYCLTM_ADM_002*/
public class PreQue_Msg_Rules  extends DecisionElementBase {
	
	private static final String S_ROUTING_BRAND_LABEL = "S_ROUTING_BRAND_LABEL";
	
	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String StrExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);objHostDetails.setinitalValue();
		String strReqBody = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		try {
		if(data.getSessionData(Constants.S_PREQUE_MSG_RULES_URL) != null && data.getSessionData(Constants.S_LOB) != null 
				&& data.getSessionData(Constants.S_CONN_TIMEOUT) != null && data.getSessionData(Constants.S_READ_TIMEOUT) != null) {
			String url = (String)data.getSessionData(Constants.S_PREQUE_MSG_RULES_URL);
			String callerId = (String)data.getSessionData(Constants.S_CALLID);
			String dnis = (String)data.getSessionData(Constants.S_FINAL_DNIS);
			String dnisgroup = (String)data.getSessionData(Constants.S_FINAL_DNIS_GROUP);
			String category = (String)data.getSessionData(Constants.S_FINAL_CATEGORY);
			String lob = (String)data.getSessionData(Constants.S_FINAL_LOB);
			int conTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_CONN_TIMEOUT));
			int readTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_READ_TIMEOUT));
			LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
			GetPrequeueMessageRulesByBusinessObjects test = new GetPrequeueMessageRulesByBusinessObjects();
			
			for (int prequeueLoop = 1; prequeueLoop <= 4; prequeueLoop++) {
				data.addToLog(currElementName, "***************************prequeue rule iteration : " + prequeueLoop);
				if (prequeueLoop == 1) {
					JSONObject resp =  (JSONObject) test.start(url, callerId, Constants.tenantid, dnis, Constants.EmptyString, Constants.EmptyString, Constants.EmptyString, conTimeout, readTimeout, context);
					strRespBody = resp.toString();
					data.addToLog(currElementName, "GetPrequeueMessageRulesByBusinessObjects API response  :"+resp);
					//Mustan - Alerting Mechanism ** Response Code Capture
					apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
					if(resp != null) {
						if(resp.containsKey(Constants.REQUEST_BODY)) strReqBody = resp.get(Constants.REQUEST_BODY).toString();
						if(resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200) {
							data.addToLog(currElementName, "Set KYCLTM_ADM_002  GetPrequeueMessageRulesByBusinessObjects API Response into session with the key name of "+currElementName+Constants._RESP);
							strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
							data.setSessionData(currElementName+Constants._RESP, resp.get(Constants.RESPONSE_BODY));
							StrExitState = apiResponseManupulation(data, caa, currElementName, strRespBody);
						}
					}
				} else if (prequeueLoop == 2) {
					JSONObject resp =  (JSONObject) test.start(url, callerId, Constants.tenantid, Constants.EmptyString, dnisgroup, Constants.EmptyString, Constants.EmptyString, conTimeout, readTimeout, context);
					strRespBody = resp.toString();
					data.addToLog(currElementName, "GetPrequeueMessageRulesByBusinessObjects API response  :"+resp);
					//Mustan - Alerting Mechanism ** Response Code Capture
					apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
					if(resp != null) {
						if(resp.containsKey(Constants.REQUEST_BODY)) strReqBody = resp.get(Constants.REQUEST_BODY).toString();
						if(resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200) {
							data.addToLog(currElementName, "Set KYCLTM_ADM_002  GetPrequeueMessageRulesByBusinessObjects API Response into session with the key name of "+currElementName+Constants._RESP);
							strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
							data.setSessionData(currElementName+Constants._RESP, resp.get(Constants.RESPONSE_BODY));
							StrExitState = apiResponseManupulation(data, caa, currElementName, strRespBody);
						}
					}
				} else if (prequeueLoop == 3) {
					JSONObject resp =  (JSONObject) test.start(url, callerId, Constants.tenantid, Constants.EmptyString, Constants.EmptyString, Constants.EmptyString, category, conTimeout, readTimeout, context);
					strRespBody = resp.toString();
					data.addToLog(currElementName, "GetPrequeueMessageRulesByBusinessObjects API response  :"+resp);
					//Mustan - Alerting Mechanism ** Response Code Capture
					apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
					if(resp != null) {
						if(resp.containsKey(Constants.REQUEST_BODY)) strReqBody = resp.get(Constants.REQUEST_BODY).toString();
						if(resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200) {
							data.addToLog(currElementName, "Set KYCLTM_ADM_002  GetPrequeueMessageRulesByBusinessObjects API Response into session with the key name of "+currElementName+Constants._RESP);
							strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
							data.setSessionData(currElementName+Constants._RESP, resp.get(Constants.RESPONSE_BODY));
							StrExitState = apiResponseManupulation(data, caa, currElementName, strRespBody);
						}
					}
				} else {
					JSONObject resp =  (JSONObject) test.start(url, callerId, Constants.tenantid, Constants.EmptyString, Constants.EmptyString, lob, Constants.EmptyString, conTimeout, readTimeout, context);
					strRespBody = resp.toString();
					data.addToLog(currElementName, "GetPrequeueMessageRulesByBusinessObjects API response  :"+resp);
					//Mustan - Alerting Mechanism ** Response Code Capture
					apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
					if(resp != null) {
						if(resp.containsKey(Constants.REQUEST_BODY)) strReqBody = resp.get(Constants.REQUEST_BODY).toString();
						if(resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200) {
							data.addToLog(currElementName, "Set KYCLTM_ADM_002  GetPrequeueMessageRulesByBusinessObjects API Response into session with the key name of "+currElementName+Constants._RESP);
							strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
							data.setSessionData(currElementName+Constants._RESP, resp.get(Constants.RESPONSE_BODY));
							StrExitState = apiResponseManupulation(data, caa, currElementName, strRespBody);
						}
					}
				}
				if (StrExitState.equalsIgnoreCase(Constants.SU)) {
					data.addToLog(currElementName,"***************************breaking from prequeue loop at iteration : " + prequeueLoop);
					break;
				}
			}		
		}
	} catch (Exception e) {
		data.addToLog(currElementName,"Exception in GetPrequeueMessageRulesByBusinessObjects API call  :: "+e);
		caa.printStackTrace(e);
	}
	
		try {
			objHostDetails.startHostReport(currElementName,"GetPrequeueMessageRulesByBusinessObjects API", strReqBody,"", (String)data.getSessionData(Constants.S_PREQUE_MSG_RULES_URL));
			objHostDetails.endHostReport(data,strRespBody , StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for GetPrequeueMessageRulesByBusinessObjects API call  :: "+e);
			caa.printStackTrace(e);
		}
		
	return StrExitState;
	}
	
	private String apiResponseManupulation(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {
		String StrExitState = Constants.ER;
		try {
			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			JSONArray resArr = (JSONArray)resp.get("res");
			JSONObject mainObj = null;
			
			if(resArr.size() == 0 ) {
				return Constants.ER;
			} else {
				@SuppressWarnings("unchecked")
				ArrayList<String> stategroup = (ArrayList<String>)data.getSessionData(Constants.S_STATE_GROUP);
				data.addToLog(currElementName,"Area based on state group :"+stategroup);
				//eliminate state groups
				@SuppressWarnings("unchecked")
				List<JSONObject> list = (List<JSONObject>) StreamSupport.stream(resArr.spliterator(), false).map(e -> (JSONObject) e).collect(Collectors.toList());
				List<JSONObject> tempList1 = new ArrayList<JSONObject>(routingSortingStateGroup(list, Constants.S_STATE_GROUP, Constants.StateGroup, data));
				
				/*if ((null != data.getSessionData(Constants.S_PREF_LANG)) && ((Constants.EN).equalsIgnoreCase((String) data.getSessionData(Constants.S_PREF_LANG)))) {
					data.setSessionData(Constants.S_FINAL_LANG, Constants.English);
				} else {
					data.setSessionData(Constants.S_FINAL_LANG, Constants.Spanish);
				}*/
				List<JSONObject>tempList2 = new ArrayList<JSONObject>();
				
				tempList2 = routingSortingLang(tempList1, Constants.S_FINAL_LANG, Constants.Language, data);
				
				tempList1 = new ArrayList<JSONObject>();
				
				tempList1 = routingSorting(tempList2, S_ROUTING_BRAND_LABEL, Constants.BrandLabel, data);
				
				if (tempList1.size() > 0) {
					data.addToLog(currElementName, "Set to break loop at more than zero");
					data.addToLog(currElementName, "TempList : " + tempList1);
					mainObj = (JSONObject) tempList1.get(0);
				} else {
					mainObj = getDefaultRouteRule(tempList1, data);
				}
				data.addToLog(currElementName,"mainObj based on state group :"+mainObj);
			}
			
			if(mainObj != null) {
				try {
					
					if(mainObj.containsKey("prepercentage") &&  mainObj.get("prepercentage") != null) {
						String prePercentage = (String) mainObj.get("prepercentage");
						if(prePercentage.equalsIgnoreCase("100")) {
							data.addToLog(data.getCurrentElement(), "Pre Queue Message percentage is 100%. So PreQueue message will play");
							data.setSessionData(Constants.S_PRE_QUEUE_MSG_ENABLED, Constants.STRING_YES);
							JSONArray prerecordingArr = (JSONArray)mainObj.get("prerecording");
							if(prerecordingArr != null) {
								if(prerecordingArr.size() == 0 ) return Constants.ER;
								data.addToLog(currElementName, "Prerecording start");
								JSONObject prerecordingObj = (JSONObject) prerecordingArr.get(0);
								
								//data.setSessionData(Constants.S_PRE_QUEUE_MSG_ENABLED, Constants.STRING_YES);
								if(data.getSessionData(Constants.S_PREF_LANG).equals(Constants.EN)) {
									data.addToLog(currElementName, "Prerecording pref lang eng");
									if(!((String)prerecordingObj.get("audioenglishwavpath")).equals("") && ((String)prerecordingObj.get("audioenglishwavpath")).contains(".wav")) {
										String wavFile = (String)prerecordingObj.get("audioenglishwavpath"); 
										if(wavFile.contains("\\")) {
											String[] wavFileArr = Pattern.compile("\\\\").split(wavFile);
											wavFile = wavFileArr[wavFileArr.length-1];
										}
										data.addToLog(currElementName, "Prerecording en wav set : " + wavFile);
										data.setSessionData(Constants.STR_PA_001_WAV, wavFile);
									}
									if(!((String)prerecordingObj.get("audioenglishtext")).equals(""))	data.setSessionData(Constants.STR_PA_001_TTS, prerecordingObj.get("audioenglishtext"));
									data.addToLog(currElementName, "EN :: audioenglishwavpath : "+data.getSessionData(Constants.STR_PA_001_WAV)+" :: audioenglishtext : "+data.getSessionData(Constants.STR_PA_001_TTS));
								} else {
									data.addToLog(currElementName, "Prerecording pref lang spa");
									data.addToLog(currElementName, "PreRecordingObj :: " + prerecordingObj);
									if(!((String)prerecordingObj.get("audiospanishwavpath")).equals("") && ((String)prerecordingObj.get("audiospanishwavpath")).contains(".wav")) {
										
										String wavFile = (String)prerecordingObj.get("audiospanishwavpath"); 
										data.addToLog(currElementName, "WaveFile Name :: " + wavFile);
										if(wavFile.contains("\\")) {
											String[] wavFileArr = Pattern.compile("\\\\").split(wavFile);
											wavFile = wavFileArr[wavFileArr.length-1];
											data.addToLog(currElementName, "Prerecording sp wav set");
										}
										data.addToLog(currElementName, "Prerecording en wav set : " + wavFile);
										data.setSessionData(Constants.STR_PA_001_WAV, wavFile);
									}
									if(!((String)prerecordingObj.get("audiospanishtext")).equals("")){
										data.setSessionData(Constants.STR_PA_001_TTS, prerecordingObj.get("audiospanishtext"));
									}
									data.addToLog(currElementName, "SP:: audiospanishtext : "+data.getSessionData(Constants.STR_PA_001_WAV)+" :: audiospanishtext : "+data.getSessionData(Constants.STR_PA_001_TTS));
								}
							} else {
								data.addToLog(currElementName, "Prerecording is empty");
							}
						}else {
							data.addToLog(data.getCurrentElement(), "Pre Queue Message percentage is not 100%. So IVR will skip PreQueue message");
						}
					}else {
						data.addToLog(data.getCurrentElement(), "Pre Queue Message percentage is not 100%. So IVR will skip PreQueue message");
					}
					
					
					
					
				} catch (Exception e) {
					data.addToLog(currElementName,"Exception in apiResponseManupulation method : Pre queue recoring :: "+e);
					caa.printStackTrace(e);
				}
				
				try {
					if(mainObj.containsKey("postpercentage") &&  mainObj.get("postpercentage") != null) {
						String postPercentage = (String) mainObj.get("postpercentage");
						if(postPercentage.equalsIgnoreCase("100")) {
							data.addToLog(data.getCurrentElement(), "Post Queue Message percentage is 100%. So PostQueue message will play");
							JSONArray postrecordingArr = (JSONArray)mainObj.get("postrecording");
							if(postrecordingArr != null) {
								if(postrecordingArr.size() == 0 ) return Constants.ER;
								data.setSessionData(Constants.S_POST_QUEUE_MSG_ENABLED, Constants.STRING_YES);
								JSONObject postrecordingObj = (JSONObject) postrecordingArr.get(0);
								if(data.getSessionData(Constants.S_PREF_LANG).equals(Constants.EN)) {
									if(!((String)postrecordingObj.get("audioenglishwavpath")).equals("") && ((String)postrecordingObj.get("audioenglishwavpath")).contains(".wav")) {
										String wavFile = (String)postrecordingObj.get("audioenglishwavpath"); 
										if(wavFile.contains("\\")) {
											String[] wavFileArr = Pattern.compile("\\\\").split(wavFile);
											wavFile = wavFileArr[wavFileArr.length-1];
										}
										data.setSessionData(Constants.STR_PA_003_WAV, wavFile);
									}
									if(!((String)postrecordingObj.get("audioenglishtext")).equals(""))	data.setSessionData(Constants.STR_PA_003_TTS, postrecordingObj.get("audioenglishtext"));
									data.addToLog(currElementName, "EN :: audioenglishwavpath : "+data.getSessionData(Constants.STR_PA_003_WAV)+" :: audioenglishtext : "+data.getSessionData(Constants.STR_PA_003_TTS));
								} else {
									if(!((String)postrecordingObj.get("audiospanishwavpath")).equals("") && ((String)postrecordingObj.get("audiospanishwavpath")).contains(".wav")) {
										String wavFile = (String)postrecordingObj.get("audiospanishwavpath"); 
										if(wavFile.contains("\\")) {
											String[] wavFileArr = Pattern.compile("\\\\").split(wavFile);
											wavFile = wavFileArr[wavFileArr.length-1];
										}
										data.setSessionData(Constants.STR_PA_003_WAV, wavFile);
									}
									if(!((String)postrecordingObj.get("audiospanishtext")).equals("")){
										data.setSessionData(Constants.STR_PA_003_TTS, postrecordingObj.get("audiospanishtext"));
									}
									data.addToLog(currElementName, "SP:: audiospanishtext : "+data.getSessionData(Constants.STR_PA_003_WAV)+" :: audiospanishtext : "+data.getSessionData(Constants.STR_PA_003_TTS));
								}
							} else {
								data.addToLog(currElementName, "Postrecording is empty");
							}
						}else {
							data.addToLog(data.getCurrentElement(), "Post Queue Message percentage is not 100%. So IVR will skip PostQueue message");
						}
						
					}else {
						data.addToLog(data.getCurrentElement(), "Post Queue Message percentage is not 100%. So IVR will skip PostQueue message");
					}
								
				} catch (Exception e) {
					data.addToLog(currElementName,"Exception in apiResponseManupulation method : Pre queue recoring :: "+e);
					caa.printStackTrace(e);
				}
				StrExitState = Constants.SU;
			} else {
				StrExitState = Constants.ER;
			}
			
			
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception in apiResponseManupulation method  :: "+e);
			caa.printStackTrace(e);
		}
		return StrExitState;
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
		if(respList.isEmpty()) {
			data.addToLog(data.getCurrentElement(),"Sorting list not match, returning empty sets : " + respList.size() + " : " + respList);
			respList = respEmptyList;
		}
		data.addToLog(data.getCurrentElement(),"Prequeue resp list size post sorting with " + sessionKey + " : " + respList.size());
		data.addToLog(data.getCurrentElement(),"Prequeue resp list post sorting with " + sessionKey + " : " + respList);
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
			if (Constants.EmptyString.equalsIgnoreCase(tempObj.get(routingRespKey).toString()) && sessKey.equals(Constants.English)) {
				data.addToLog(data.getCurrentElement(), "Adding empty lang entry");
				respList.add(tempObj);
			} else {
				if (tempObj.get(routingRespKey).toString().equalsIgnoreCase(sessKey)) {
					respList.add(tempObj);
				}
			}
		}
		data.addToLog(data.getCurrentElement(),"Prequeue resp list size post sorting with size " + sessionKey + " : " + respList.size());
		data.addToLog(data.getCurrentElement(),"Prequeue resp list post sorting with " + sessionKey + " : " + respList);	
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
		data.addToLog(data.getCurrentElement(), "Prequeue resp list size post sorting with " + sessionKey + " : " + respSet.size());
		data.addToLog(data.getCurrentElement(), "Prequeue resp set size post sorting with " + sessionKey + " : " + respSet);	
		return respSet;
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
		
		if(tempList2.size() > 0) {
			data.addToLog(data.getCurrentElement(), "Default Prequeue Rule picker size : " + tempList2.size());
			data.addToLog(data.getCurrentElement(), "Default Prequeue Rule picker : " + tempList2);
			routeRule = (JSONObject)tempList2.get(0);
			data.addToLog(data.getCurrentElement(), "Default Prequeue Rule picked : " + routeRule.toJSONString());	
		} else {
			data.addToLog(data.getCurrentElement(), "Default Prequeue Rule returned size : " + tempList2.size());
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
		data.addToLog(data.getCurrentElement(),"Prequeue resp list size post sorting with default values " + sessionKey + " : " + respEmptyList.size());
		data.addToLog(data.getCurrentElement(),"Prequeue resp list post sorting with " + sessionKey + " : " + respEmptyList);
		return respEmptyList;
	}

	
	@SuppressWarnings("unused")
	private List<JSONObject> routingDefaultSortingLang(List<JSONObject> reqList, String sessionKey, String routingRespKey,
			DecisionElementData data) {
		data.addToLog(data.getCurrentElement(), "Prequeue reqList size before sorting with " + sessionKey + " is : " + reqList.size());
		List<JSONObject> respList = new ArrayList<JSONObject>();
		//if (reqList.size() == 1)
			//return reqList;
		String sessKey = Constants.EmptyString;
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
		data.addToLog(data.getCurrentElement(),"Prequeue resp list size post sorting with default Prequeue " + sessionKey + " : " + respList.size());
		data.addToLog(data.getCurrentElement(),"Prequeue resp list post sorting with default Prequeue " + sessionKey + " : " + respList);	
		return respList;
	}

	
	@SuppressWarnings("unused")
	private Set<JSONObject> routingDefaultSortingStateGroup(List<JSONObject> reqList, String sessionKey,String routingRespKey, DecisionElementData data) {
		data.addToLog(data.getCurrentElement(),"Prequeue reqList size before sorting with " + sessionKey + " is : " + reqList.size());
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
		data.addToLog(data.getCurrentElement(), "Prequeue resp list size post sorting with default " + sessionKey + " : " + emptySet.size());
		data.addToLog(data.getCurrentElement(), "Prequeue resp set size post sorting with default " + sessionKey + " : " + emptySet);	
		return emptySet;
	}


}