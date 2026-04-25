package com.farmers.adminHost;

import java.util.regex.Pattern;

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
public class PreQue_Msg_Rules_Pre_Bind  extends DecisionElementBase {
	
	
	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String StrExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);objHostDetails.setinitalValue();
		String strReqBody = Constants.EmptyString;
		String strStateCode = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		try {
		if(data.getSessionData(Constants.S_PREQUE_MSG_RULES_URL) != null && data.getSessionData(Constants.S_LOB) != null 
				&& data.getSessionData(Constants.S_CONN_TIMEOUT) != null && data.getSessionData(Constants.S_READ_TIMEOUT) != null) {
			String url = (String)data.getSessionData(Constants.S_PREQUE_MSG_RULES_URL);
			String callerId = (String)data.getSessionData(Constants.S_CALLID);
			String dnis = (String)data.getSessionData(Constants.S_DNIS);
			strStateCode = (String)data.getSessionData("S_STATE_CODE");
			String dnisgroup = (String)data.getSessionData(Constants.S_FINAL_DNIS_GROUP);
			String category = (String)data.getSessionData(Constants.S_FINAL_CATEGORY);
			String lob = (String)data.getSessionData(Constants.S_BU);
			int conTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_CONN_TIMEOUT));
			int readTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_READ_TIMEOUT));
			
			data.addToLog(currElementName, "URL :: " + url + " :: Caller ID :: " + callerId + " :: DNIS :: " + dnis + " :: strStateCode :: " + strStateCode
					+ " :: DNIS Group :: "+ dnisgroup + " :: category :: " + category + " :: lob :: " + lob + " :: Conn timeout :: " + conTimeout + " :: readtimeout :: " + readTimeout);
			
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
							StrExitState = apiResponseManupulation(data, caa, currElementName, strRespBody,strStateCode);
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
							StrExitState = apiResponseManupulation(data, caa, currElementName, strRespBody,strStateCode);
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
							StrExitState = apiResponseManupulation(data, caa, currElementName, strRespBody,strStateCode);
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
							StrExitState = apiResponseManupulation(data, caa, currElementName, strRespBody,strStateCode);
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
			objHostDetails.startHostReport(currElementName,"GetPrequeueMessageRulesByBusinessObjects API", strReqBody, "",(String)data.getSessionData(Constants.S_PREQUE_MSG_RULES_URL));
			objHostDetails.endHostReport(data,strRespBody , StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for GetPrequeueMessageRulesByBusinessObjects API call  :: "+e);
			caa.printStackTrace(e);
		}
		
	return StrExitState;
	}
	
	private String apiResponseManupulation(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody, String stateCode) {
		String StrExitState = Constants.ER;
		try {
			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
			JSONArray resArr = (JSONArray)resp.get("res");
			JSONObject mainObj = null;
			
			if(resArr.size() == 0 ) {
				return Constants.ER;
			} else {
				//stategroup
				for (int i = 0; i < resArr.size(); i++) {
					mainObj = (JSONObject) resArr.get(i);
					String tempState = ""+mainObj.get(Constants.StateGroup);
					data.addToLog(currElementName, "State Code :"+tempState);
					if(mainObj!=null && tempState!=null && tempState.equals(stateCode)) {
						data.addToLog(currElementName, ": State Matched :");
						break;
					}else {
						continue;
					}
				}
				
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
								data.setSessionData(Constants.S_PRE_QUEUE_MSG_ENABLED, Constants.STRING_YES);
								
									if(null != prerecordingArr && prerecordingArr.size() > 0 ) {
										
										String[] arrDetails = AudioFormation(prerecordingArr,data);
										data.addToLog(currElementName,"finalWave :"+arrDetails[0]);
										data.addToLog(currElementName,"finalTTS :"+arrDetails[1]);
										
										data.setSessionData("STR_PA_001_WAV", arrDetails[0]);
										data.setSessionData("STR_PA_001_TTS", arrDetails[1]);
										
									}
									data.addToLog(currElementName, "SP:: audiospanishtext : "+data.getSessionData(Constants.STR_PA_001_WAV)+" :: audiospanishtext : "+data.getSessionData(Constants.STR_PA_001_TTS));
								}
							else {
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
								data.addToLog(currElementName, "Prerecording start");
								data.setSessionData(Constants.S_POST_QUEUE_MSG_ENABLED, Constants.STRING_YES);
								
									if(null != postrecordingArr && postrecordingArr.size() > 0 ) {
										
										String[] arrDetails = AudioFormation(postrecordingArr,data);
										data.addToLog(currElementName,"finalWave :"+arrDetails[0]);
										data.addToLog(currElementName,"finalTTS :"+arrDetails[1]);
										
										//Splitting out side
										String[] wav = arrDetails[0].split(Constants.PIPE);
										String[] tts = arrDetails[1].split(Constants.PIPE);
										data.setSessionData("COMPLETE_COUNT",1);
										data.setSessionData("STR_PA_003_WAV", wav[0]);
										data.setSessionData("STR_PA_003_TTS", tts[0]);
										
										data.setSessionData("COMPLETE_WAV", arrDetails[0]);
										data.setSessionData("COMPLETE_TTS", arrDetails[1]);
									}
									data.addToLog(currElementName, "SP:: audiospanishtext : "+data.getSessionData(Constants.STR_PA_003_WAV)+" :: audiospanishtext : "+data.getSessionData(Constants.STR_PA_003_TTS));
								}
							else {
								data.addToLog(currElementName, "Prerecording is empty");
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
	
	private JSONObject getOrderedJSON(JSONArray welcomemessageArr, int filecount,String prefLang) {
		JSONObject objJSONObject = null;
		
		for(int i=0; i<welcomemessageArr.size();i++) {
			JSONObject tempObj = (JSONObject) welcomemessageArr.get(i); 
			String key= (String)tempObj.get("key");
			
			if(key!=null && key.contains("_"+filecount) && !(key.contains("_1_"+filecount)) && key.endsWith("_"+filecount)) {
				objJSONObject = tempObj;
				break;
			}else {
				objJSONObject = (JSONObject) welcomemessageArr.get(0);
			}
		}
		return objJSONObject;
	}
	
	private String[] AudioFormation(JSONArray welcomemessageArr, DecisionElementData data) {
		String prefLang = (String)data.getSessionData(Constants.S_PREF_LANG);
		String finalWave="",finalTTS="";
		String currElementName=data.getCurrentElement();
		String[] arr = new String[2];
		for(int i=0; i<welcomemessageArr.size();i++) {
			int fileCount = i+1;
			JSONObject welcomemessageObj =getOrderedJSON(welcomemessageArr, fileCount,prefLang);
			data.addToLog(data.getCurrentElement(),"Count :"+fileCount);
			data.addToLog(data.getCurrentElement(),"key :"+welcomemessageObj.get("key"));
			
			if(Constants.EN.equals(prefLang)) {
				String wavFile = (String)welcomemessageObj.get("audioenglishwavpath"); 
				if(wavFile.contains("\\")) {
					String[] wavFileArr = Pattern.compile("\\\\").split(wavFile);
					wavFile = wavFileArr[wavFileArr.length-1];
				}
				finalWave= (finalWave.equals("") && i==0)?finalWave+ wavFile:finalWave+"|"+wavFile;
				finalTTS= (finalTTS.equals("") && i==0)?finalTTS+ (String)welcomemessageObj.get("audioenglishtext"):finalTTS+"|"+(String)welcomemessageObj.get("audioenglishtext");
				data.addToLog(currElementName, "EN:: audioenglishwavpath : "+finalWave+" :: audioenglishtext : "+finalTTS);
			} else {
				String wavFile = (String)welcomemessageObj.get("audiospanishwavpath"); 
				if(wavFile.contains("\\")) {
					String[] wavFileArr = Pattern.compile("\\\\").split(wavFile);
					wavFile = wavFileArr[wavFileArr.length-1];
				}
				finalWave= (finalWave.equals("") && i==0)?finalWave+ wavFile:finalWave+"|"+wavFile;
				finalTTS= (finalTTS.equals("") && i==0)?finalTTS+ (String)welcomemessageObj.get("audiospanishtext"):finalTTS+"|"+(String)welcomemessageObj.get("audiospanishtext");
				
				data.addToLog(currElementName, "SP:: audiospanishwavpath : "+finalWave+" :: audiospanishtext : "+finalTTS);
			}
			
		}
		arr[0]=finalWave;
		arr[1]=finalTTS;
		return arr;
	}

}