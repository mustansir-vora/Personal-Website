package com.farmers.DialogFlow;

import java.util.Iterator;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.cisco.cvp.ivr.microapp.customgrammar.EN_US_Localizer;
import com.farmers.util.Constants;

public class CheckEventData extends DecisionElementBase {

	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws Exception {
		
		String exitState = Constants.ER_ES;
		String langCode = Constants.EmptyString;
		String voiceName = Constants.EmptyString;
		String nextEvent = Constants.EmptyString;
		String dfSessionID = Constants.EmptyString;
		String transitionFlow = Constants.EmptyString;
		String transitionPage = Constants.EmptyString;
		JSONObject jsonObject = null;
		JSONParser JSONParser = new JSONParser();
		try {
			
			String customExit = data.getElementData(Constants.VIRTUALAGENTVOICE, Constants.VAV_ISCUSTOMEXIT) != null ? data.getElementData(Constants.VIRTUALAGENTVOICE, Constants.VAV_ISCUSTOMEXIT) : Constants.FALSE;
			data.addToLog(currElementName, "isCustomExit :: " + customExit);
			
			String eventData = data.getElementData(Constants.VIRTUALAGENTVOICE, Constants.VAV_EVENTDATA) != null ? data.getElementData(Constants.VIRTUALAGENTVOICE, Constants.VAV_EVENTDATA) : Constants.EmptyString;
			
			if(null != eventData && !eventData.equalsIgnoreCase(Constants.EmptyString)) {
				eventData=eventData.replaceAll("/", "");
				eventData=eventData.replace("\\", "");
				
				jsonObject = (JSONObject)JSONParser.parse(eventData);
				data.addToLog(currElementName, "Event Data JSON :: " + jsonObject);
			}
			
			String eventName = data.getElementData(Constants.VIRTUALAGENTVOICE, Constants.VAV_EVENTNAME) != null ? data.getElementData(Constants.VIRTUALAGENTVOICE, Constants.VAV_EVENTNAME) : Constants.EmptyString;
			data.addToLog(currElementName, "Event Name :: "+ eventName);
			
			String agentHandoff = data.getElementData(Constants.VIRTUALAGENTVOICE, Constants.VAV_AGENTHANDOFF) != null ? data.getElementData(Constants.VIRTUALAGENTVOICE, Constants.VAV_AGENTHANDOFF) : Constants.FALSE;
			data.addToLog(currElementName, "Agent HandOff Flag :: " + agentHandoff);
			
			String endSession = data.getElementData(Constants.VIRTUALAGENTVOICE, Constants.VAV_ENDSESSION) != null ? data.getElementData(Constants.VIRTUALAGENTVOICE, Constants.VAV_ENDSESSION) : Constants.FALSE;
			data.addToLog(currElementName, "session End Flag :: " + endSession);
			
			if (Constants.FALSE.equalsIgnoreCase(customExit) && Constants.FALSE.equalsIgnoreCase(agentHandoff) && Constants.FALSE.equalsIgnoreCase(endSession)) {
				exitState = Constants.LOOP_ES;
			}
			
			//START Language Switch/Session ID exchange - Fetch required data from event JSON & set it into session
			else if (Constants.TRUE.equalsIgnoreCase(customExit) && Constants.FALSE.equalsIgnoreCase(agentHandoff) && Constants.FALSE.equalsIgnoreCase(endSession)) {
				
				//START IVR Disconnect 
				if (eventName.equalsIgnoreCase(Constants.EVENT_IVRDISCONNECT)) {
					exitState = Constants.DISCONNECT_ES;
				}
				//END IVR Disconnect
				
				if (null != jsonObject && jsonObject.containsKey(Constants.ED_Params)) {
					
					JSONObject params = (JSONObject) jsonObject.get(Constants.ED_Params);
					
					if (null != params) {
						
						if (params.containsKey(Constants.ED_LangCode)) {
							langCode = (String) params.get(Constants.ED_LangCode);
							data.addToLog(currElementName, "Language Code :: " + langCode);
						}
						
						if (params.containsKey(Constants.ED_VoiceName)) {
							voiceName = (String) params.get(Constants.ED_VoiceName);
							data.addToLog(currElementName, "Voice name :: " + voiceName);
						}
						
						if (params.containsKey(Constants.ED_NextEvent)) {
							nextEvent = (String) params.get(Constants.ED_NextEvent);
							data.addToLog(currElementName, "Next event :: " + nextEvent);
						}
						
						if (params.containsKey(Constants.ED_SessionID)) {
							dfSessionID = (String) params.get(Constants.ED_SessionID);
							data.addToLog(currElementName, "DF session ID :: " + dfSessionID);
						}
						
						if (params.containsKey(Constants.ED_TransitionFlow)) {
							transitionFlow = (String) params.get(Constants.ED_TransitionFlow);
							data.addToLog(currElementName, "Flow :: " + transitionFlow);
						}
						
						if (params.containsKey(Constants.ED_TransitionPage)) {
							transitionPage = (String) params.get(Constants.ED_TransitionPage);
							data.addToLog(currElementName, "Page :: " + transitionPage);
						}
					}
					
					if (eventName.equalsIgnoreCase(Constants.EVENT_LANGUAGECHANGE)) {
						data.setSessionData(Constants.ActiveLang, langCode);
						data.setSessionData(Constants.EventName, nextEvent);
						data.setSessionData(Constants.TransitionFlow, transitionFlow);
						data.setSessionData(Constants.TransitionPage, transitionPage);
						
						if (langCode.equalsIgnoreCase(Constants.es_US) || langCode.equalsIgnoreCase(Constants.SP)) {
							data.setSessionData(Constants.VoiceName, Constants.ES_VoiceName);
						}
						else {
							data.setSessionData(Constants.VoiceName, Constants.EN_VoiceName);
						}
						exitState = Constants.LANGSWITCH_ES;
					}
					else if (eventName.equalsIgnoreCase(Constants.EVENT_SESSIONIDEXCHANGE)) {
						data.setSessionData(Constants.DF_SESSIONID, dfSessionID);
						data.setSessionData(Constants.EventName, nextEvent);
						exitState = Constants.LOOP_ES;
					}
				}
			}
			//END Language Switch/Session ID exchange
			
			else {
				
				//START Agent Handoff - Fetch required data from event JSON & set into session
				if (!Constants.FALSE.equalsIgnoreCase(agentHandoff)) {
					JSONObject agent_Handoff_metadata = (JSONObject) JSONParser.parse(agentHandoff);
					JSONObject exec_req = null;
					JSONObject Params = null;
					
					exec_req = getJSONfromObj(currElementName, data, agent_Handoff_metadata, "metadata:Execute_Request");
					Params = getJSONfromObj(currElementName, data, agent_Handoff_metadata, "metadata:Execute_Request:Data:Params");
					String event = null;
										
					if (null != exec_req && exec_req.containsKey(Constants.EventName)) {
						event = (String) exec_req.get(Constants.EventName);
						data.addToLog(currElementName, "Event Name :: " + event);
					}
					
					if (null != event && event.equalsIgnoreCase(Constants.EVENT_AGENTTRANSFER)) {
												
							if (null != Params) {
										
								for (Object key : Params.keySet()) {
									data.setSessionData(key.toString(), (String) Params.get(key.toString()));
									data.addToLog(currElementName, "key-->" +  key.toString() + " set into session with value-->" + (String) Params.get(key.toString()));
								}
							}
						exitState = Constants.TRANSFER_ES;
					}
				}
				//END Agent Handoff
				
				//START IVR Disconnect 
				else if (Constants.FALSE.equalsIgnoreCase(agentHandoff) && eventName.equalsIgnoreCase(Constants.EVENT_IVRDISCONNECT)) {
					exitState = Constants.DISCONNECT_ES;
				}
				//END IVR Disconnect
			}
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception occured in CheckEventData doDecision method :: Exception : " + e);
			e.printStackTrace();
		}
		return exitState;
	}
	
	public JSONObject getJSONfromObj(String currElementName, DecisionElementData data, JSONObject Obj, String path) {
		data.addToLog(currElementName, "JSON Path :: " + path);
		try {
			String[] pathArr = path.split(":");
			for (String key : pathArr) {
				if (Obj.containsKey(key)) {
					Obj = (JSONObject) Obj.get(key);
				}
			}
			data.addToLog(currElementName, "Final JSON retrieved :: " + Obj);
		} catch (Exception e) {
			data.addToLog(currElementName, "Exception occured in getJSONfromObj method :: Exception : " + e);
			e.printStackTrace();
		}
		return Obj;
	}
}
