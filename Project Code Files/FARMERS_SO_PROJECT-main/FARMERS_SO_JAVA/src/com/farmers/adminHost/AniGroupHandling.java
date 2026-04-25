package com.farmers.adminHost;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
import com.farmers.AdminAPI.GetAniGroupTableByKey;
import com.farmers.report.SetHostDetails;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

/* SANI_ADM_002 */
public class AniGroupHandling extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		String StrExitState = Constants.ER;
		CommonAPIAccess caa=CommonAPIAccess.getInstance(data);
		SetHostDetails objHostDetails = new SetHostDetails(caa);objHostDetails.setinitalValue();
		String strReqBody = Constants.EmptyString;
		//Mustan - Alerting Mechanism ** Response Code Capture
		String apiRespCode = Constants.EmptyString;
		String strRespBody = Constants.EmptyString;
		try {
		if(data.getSessionData(Constants.S_ANI_GROUP_URL) != null &&  data.getSessionData(Constants.S_ANI) != null
				&& data.getSessionData(Constants.S_CONN_TIMEOUT) != null && data.getSessionData(Constants.S_READ_TIMEOUT) != null) {
			String url = (String)data.getSessionData(Constants.S_ANI_GROUP_URL);
			String callerId = (String) data.getSessionData(Constants.S_CALLID);
			String key = (String) data.getSessionData(Constants.S_ANI);
			int conTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_CONN_TIMEOUT));
			int readTimeout = Integer.valueOf((String)data.getSessionData(Constants.S_READ_TIMEOUT));
			LoggerContext context = (LoggerContext)data.getApplicationAPI().getApplicationData(Constants.A_CONTEXT);
			GetAniGroupTableByKey test = new GetAniGroupTableByKey();
			JSONObject resp =  (JSONObject) test.start(url, callerId, Constants.tenantid, key, conTimeout, readTimeout, context);
			strRespBody = resp.toString();
			data.addToLog(currElementName, "GetAniGroupTableByKey API response  :"+resp);
			//Mustan - Alerting Mechanism ** Response Code Capture
			apiRespCode = String.valueOf((int) resp.get(Constants.RESPONSE_CODE));
			if(resp != null) {
				if(resp.containsKey(Constants.REQUEST_BODY)) strReqBody = resp.get(Constants.REQUEST_BODY).toString();
				if(resp.containsKey(Constants.RESPONSE_CODE) && (int) resp.get(Constants.RESPONSE_CODE) == 200) {
					data.addToLog(currElementName, "Set SANI_ADM_001  GetAniGroupTableByKey API Response into session with the key name of "+currElementName+Constants._RESP);
					strRespBody = resp.get(Constants.RESPONSE_BODY).toString();
					data.setSessionData(currElementName+Constants._RESP, resp.get(Constants.RESPONSE_BODY));
					StrExitState = apiResponseManupulation(data, caa, currElementName, strRespBody);
				}
			}
		}
	} catch (Exception e) {
		data.addToLog(currElementName,"Exception in GetAniGroupTableByKey API call  :: "+e);
		caa.printStackTrace(e);
	}
	
		try {
			objHostDetails.startHostReport(currElementName,"GetAniGroupHandlingTableByKey API", strReqBody,"", (String)data.getSessionData(Constants.S_ANI_GROUP_URL));
			objHostDetails.endHostReport(data,strRespBody , StrExitState.equalsIgnoreCase(Constants.ER) ? Constants.ER : Constants.SU, apiRespCode,"");
		} catch (Exception e) {
			data.addToLog(currElementName,"Exception while forming host reporting for GetAniGroupTableByKey API call  :: "+e);
			caa.printStackTrace(e);
		}
		
	return StrExitState;
	}
	
	private String apiResponseManupulation(DecisionElementData data, CommonAPIAccess caa, String currElementName, String strRespBody) {
		String StrExitState = Constants.ER;;
		try {
			JSONObject resp = (JSONObject) new JSONParser().parse(strRespBody);
				JSONArray resArr = (JSONArray) resp.get("res");
				if(resArr!=null && resArr.size() > 0) {
					JSONObject objAnidetails =  (JSONObject) resArr.get(0);
					JSONArray anigrouphandlingArr = (JSONArray) objAnidetails.get("anigrouphandling");
					if(null == anigrouphandlingArr || anigrouphandlingArr.size() == 0 ) return Constants.ER;
					@SuppressWarnings("unchecked")
					List<JSONObject> respList1 = (List<JSONObject>) StreamSupport.stream(anigrouphandlingArr.spliterator(), false).map(e -> (JSONObject) e).collect(Collectors.toList());
					List<JSONObject> respList2 = new ArrayList<JSONObject>();
					List<JSONObject> emptyList = new ArrayList<JSONObject>();
					JSONObject resultObj = null;
					boolean isRecordMatched = false;
					
					for (int loop = 1; loop <= 4; loop++) {
						data.addToLog(currElementName, "***************************Ani group handling iteration : " + loop);
						if (loop == 1) {
							data.addToLog(currElementName, "***************************Ani group handling iteration with DNIS : "+data.getSessionData(Constants.S_DNIS));
							for (JSONObject object : respList1) {
								if(object.get("dnis").toString().equalsIgnoreCase(Constants.EmptyString)) {
									emptyList.add(object);
								} else  if (object.get("dnis").equals(data.getSessionData(Constants.S_DNIS))) {
									respList2.add(object);
									isRecordMatched = true;
								}
							}
							data.addToLog(currElementName, "isRecordMatched in DNIS iteration : "+isRecordMatched);
							data.addToLog(currElementName, "respList2 size : "+respList2.size()+" :: emptyList size : "+emptyList.size());
							if(respList2.isEmpty() && emptyList.size() > 0) {
								data.addToLog(currElementName, "Ani group handling iteration with DNIS : respList2 is empty");
								respList2 = emptyList;
								emptyList = new ArrayList<JSONObject>(); 
							}
							if(isRecordMatched && respList2.size() == 1) {
								resultObj = (JSONObject)respList2.get(0);
								break;
							}
							respList1 = new ArrayList<JSONObject>();
						} else if (loop == 2) {
							isRecordMatched = false;
							data.addToLog(currElementName, "***************************Ani group handling iteration with DNISGROUP : "+data.getSessionData(Constants.S_DNISGROUP));
							for (JSONObject object : respList2) {
								if(object.get("dnisgroup").toString().equalsIgnoreCase(Constants.EmptyString)) {
									emptyList.add(object);
								} else  if (object.get("dnisgroup").equals(data.getSessionData(Constants.S_DNISGROUP))) {
									respList1.add(object);
									isRecordMatched = true;
								}
							}
							data.addToLog(currElementName, "isRecordMatched in DNISGROUP iteration : "+isRecordMatched);
							data.addToLog(currElementName, "respList1 size : "+respList1.size()+" :: emptyList size : "+emptyList.size());
							if(respList1.isEmpty() && emptyList.size() > 0) {
								data.addToLog(currElementName, "Ani group handling iteration with DNISGROUP : respList2 is empty");
								respList1 = emptyList;
								emptyList = new ArrayList<JSONObject>(); 
							}
							if(isRecordMatched && respList1.size() == 1) {
								resultObj = (JSONObject)respList1.get(0);
								break;
							}
							respList2 = new ArrayList<JSONObject>();
						} else if (loop == 3) {
							isRecordMatched = false;
							data.addToLog(currElementName, "***************************Ani group handling iteration with CATEGORY : "+data.getSessionData(Constants.S_CATEGORY));
							for (JSONObject object : respList1) {
								if(object.get("category").toString().equalsIgnoreCase(Constants.EmptyString)) {
									emptyList.add(object);
								} else  if (object.get("category").equals(data.getSessionData(Constants.S_CATEGORY))) {
									respList2.add(object);
									isRecordMatched = true;
								}
							}
							data.addToLog(currElementName, "isRecordMatched in CATEGORY iteration : "+isRecordMatched);
							data.addToLog(currElementName, "respList1 size : "+respList1.size()+" :: emptyList size : "+emptyList.size());
							if(respList2.isEmpty() && emptyList.size() > 0) {
								data.addToLog(currElementName, "Ani group handling iteration with CATEGORY : respList2 is empty");
								respList2 = emptyList;
								emptyList = new ArrayList<JSONObject>(); 
							}
							if(isRecordMatched && respList2.size() == 1) {
								resultObj = (JSONObject)respList2.get(0);
								break;
							}
							respList1 = new ArrayList<JSONObject>();
						} else if (loop == 4) {
							isRecordMatched = false;
							data.addToLog(currElementName, "***************************Ani group handling iteration with LOB : "+data.getSessionData(Constants.S_LOB));
							for (JSONObject object : respList2) {
								if(object.get("lob").toString().equalsIgnoreCase(Constants.EmptyString)) {
									emptyList.add(object);
								} else  if (object.get("lob").equals(data.getSessionData(Constants.S_LOB))) {
									respList1.add(object);
									isRecordMatched = true;
								}
							}
							data.addToLog(currElementName, "isRecordMatched in LOB iteration : "+isRecordMatched);
							data.addToLog(currElementName, "respList1 size : "+respList1.size()+" :: emptyList size : "+emptyList.size());
							if(respList1.isEmpty()) {
								data.addToLog(currElementName, "Ani group handling iteration with LOB : respList2 is empty");
							} else  if(isRecordMatched && respList1.size() == 1) {
								resultObj = (JSONObject)respList1.get(0);
							}
						}
					}
					
					emptyList = new ArrayList<JSONObject>(); 
					respList1 = new ArrayList<JSONObject>();
					respList2 = new ArrayList<JSONObject>();
					
					if(null != resultObj) {
						data.setSessionData(Constants.S_ANI_GROUP_BYPASSED, Constants.STRING_YES);
						data.addToLog(currElementName, "S_ANI_GROUP_BYPASSED :"+Constants.STRING_YES);
						data.addToLog(currElementName, "checkAni Action :"+resultObj.get("action"));
						data.addToLog(currElementName, "checkAni key :"+resultObj.get("key"));
						
						data.setSessionData(Constants.S_ANI_GROUP_ACTION,""+resultObj.get("action") );
						data.setSessionData(Constants.S_ANI_GROUP, ""+resultObj.get("key"));
						
						JSONArray welcomemessageArr = (JSONArray)resultObj.get("welcomemessage");
						if(welcomemessageArr!=null ) {
							String[] arrDetails = AudioFormation(welcomemessageArr,data);
							data.addToLog(currElementName,"finalWave :"+arrDetails[0]);
							data.addToLog(currElementName,"finalTTS :"+arrDetails[1]);
							
							data.setSessionData("SANI_PA_001_WAV", arrDetails[0]);
							data.setSessionData("SANI_PA_001_TTS", arrDetails[1]);
							StrExitState = Constants.SU;
						}
					} else {
						data.addToLog(currElementName, "AniGroupHandling resultObj is null :");
					}
			} else {
				data.addToLog(currElementName, "resArr is null or 0 size :");
			}
		}catch (Exception e) {
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
			
			if(key!=null && key.contains("_"+filecount) && !(key.contains("_1_"+filecount))) {
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
				finalWave= (finalWave.equals(""))?finalWave+ wavFile:finalWave+"|"+wavFile;
				finalTTS= (finalTTS.equals(""))?finalTTS+ (String)welcomemessageObj.get("audioenglishtext"):finalTTS+"|"+(String)welcomemessageObj.get("audioenglishtext");
				data.addToLog(currElementName, "SP:: audiospanishtext : "+finalWave+" :: audiospanishtext : "+finalTTS);
			} else {
				String wavFile = (String)welcomemessageObj.get("audiospanishwavpath"); 
				if(wavFile.contains("\\")) {
					String[] wavFileArr = Pattern.compile("\\\\").split(wavFile);
					wavFile = wavFileArr[wavFileArr.length-1];
				}
				finalWave= (finalWave.equals(""))?finalWave+ wavFile:finalWave+"|"+wavFile;
				finalTTS= (finalTTS.equals(""))?finalTTS+ (String)welcomemessageObj.get("audiospanishtext"):finalTTS+"|"+(String)welcomemessageObj.get("audiospanishtext");
				
				data.addToLog(currElementName, "SP:: audiospanishtext : "+finalWave+" :: audiospanishtext : "+finalTTS);
			}
			
		}
		arr[0]=finalWave;
		arr[1]=finalTTS;
		return arr;
	}
}