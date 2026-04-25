package com.farmers.speechflow;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.Constants;


public class CheckEventData extends DecisionElementBase {

	public String doDecision(String elementName, DecisionElementData data) throws AudiumException {
		String exitState = "done";
		//int counter = 0;
		String returnValue = Constants.EmptyString,strApptagTraversal = Constants.EmptyString;
		try {
			String secureFlow = (String) data.getSessionData("SECURE_FLAG");
			String eventData = (String) data.getElementData("VirtualAgentVoice", "eventData");
			String iscustomExit = (String) data.getElementData("VirtualAgentVoice", "isCustomExit");
			String issessionend = (String) data.getElementData("VirtualAgentVoice", "end_session");
			String agentHandoff = (String) data.getElementData("VirtualAgentVoice", "agent_handoff");
			
			String NoMatchCount="0";
			String NoinputCount="0";
			String NoInputCountString="";
			
			//Menu Counter NI/NM
			String query_text=(String) data.getElementData("VirtualAgentVoice", "query_text");
			
//START:GDF Connectivity Issue retries changes
			
			if(null!= data.getSessionData("S_HOTEVENT")&& "Y".equalsIgnoreCase((String)data.getSessionData("S_HOTEVENT")) 
					&& "1st".equalsIgnoreCase((String)data.getSessionData("S_HOTEVENT_RETRY_COUNT"))){
				
				data.addToLog("HotEvent occured:",(String) data.getSessionData("S_HOTEVENT_RETRY_COUNT"));
				
				data.setSessionData("S_HOTEVENT_RETRY_COUNT", "2nd");
				
				data.addToLog("Setting HOTEVENT RETRY VALUE to :",(String) data.getSessionData("S_HOTEVENT_RETRY_COUNT"));
				
				String menuIDMode = (String)data.getSessionData("S_MENU_ID_MODE");
				if(menuIDMode!=null && "FallBackDTMF".equals(menuIDMode.trim())){
					data.addToLog("FallBack DTMF available :", "YES");
					data.setSessionData("HOTEVENT_DTMF_RETRY", "Y");				
					return "retry";
				}else {
					data.addToLog("FallBack DTMF available :", "NO");
					data.setSessionData("HOTEVENT_GDF_RETRY", "Y");
				    return "loop";
				}
			}else if(null!= data.getSessionData("S_HOTEVENT")&& "Y".equalsIgnoreCase((String)data.getSessionData("S_HOTEVENT")) 
					&& "2nd".equalsIgnoreCase((String)data.getSessionData("S_HOTEVENT_RETRY_COUNT"))){
				data.setSessionData("S_HOTEVENT_TRANSFER", "Y");
				data.addToLog("HotEvent occured:",(String) data.getSessionData("S_HOTEVENT_RETRY_COUNT"));
				data.addToLog("Setting HOTEVENT Transfer as :",(String) data.getSessionData("S_HOTEVENT_TRANSFER"));
				return "Error";
			}
			//END: GDF Connectivity Issue retries changes
			
			if("N".equalsIgnoreCase((String) data.getSessionData(Constants.S_CV_GDF_CONNECTOR_FLAG))) {
			if("true".equalsIgnoreCase(iscustomExit)||"true".equalsIgnoreCase(issessionend)||"true".equalsIgnoreCase(agentHandoff)) {
				System.out.println(  "Value of eventData : " + eventData);
				if("true".equalsIgnoreCase(secureFlow)) {data.addToLog(data.getCurrentElement(), "Value of eventData : " + "*******");}else{data.addToLog(data.getCurrentElement(), "Value of eventData : " + eventData);}
				if(eventData!=null && !"".equals(eventData)) {
					eventData= eventData.replace("\\", "");
					System.out.println("eventData Json: " + eventData);
					if("true".equalsIgnoreCase(secureFlow)) {data.addToLog(data.getCurrentElement(), "Value of eventData : " + "*******");}else{data.addToLog(data.getCurrentElement(), "eventData Json: " + eventData);}
					JSONParser objJSONParser = new JSONParser();
					JSONObject jsonObject = (JSONObject)objJSONParser.parse(eventData);
					JSONObject paramsObject = jsonObject.containsKey("Params")?(JSONObject)jsonObject.get("Params"):null;
					if(paramsObject!=null) {
						String noMatchFlag = paramsObject.containsKey("NoMatchFlag")?(String)paramsObject.get("NoMatchFlag"):"";
						data.addToLog(data.getCurrentElement(), "NoMatch Flag :"+noMatchFlag);
						String value =paramsObject.containsKey("value")?(String)paramsObject.get("value"):"";

						if("true".equalsIgnoreCase(secureFlow)) {data.addToLog(data.getCurrentElement(), "Value of eventData : " + "*******");}else{data.addToLog(data.getCurrentElement(), "Value :"+value);}

						/**
						 * NLU Change
						 */
						String apptagTraversal = paramsObject.containsKey("AppTagTraversal")?(String)paramsObject.get("AppTagTraversal"):"NA";
						data.addToLog(data.getCurrentElement(),"AppTag Traversal : "+apptagTraversal);
						if(!Constants.NA.equalsIgnoreCase(apptagTraversal)) {
							strApptagTraversal = (String) data.getSessionData(Constants.AppTagTraversal);
							if(!Constants.NA.equalsIgnoreCase(strApptagTraversal)) {
								strApptagTraversal = strApptagTraversal.concat(Constants.PIPE);
								strApptagTraversal = strApptagTraversal.concat(apptagTraversal);
								data.setSessionData(Constants.AppTagTraversal, apptagTraversal);
							}else {
								data.setSessionData(Constants.AppTagTraversal, apptagTraversal);
							}
							data.addToLog(data.getCurrentElement(),"AppTag Traversal is  available " + data.getSessionData(Constants.AppTagTraversal));
						}else {
							data.addToLog(data.getCurrentElement(),"AppTag Traversal is not available ");
						}

						String nluResult = paramsObject.containsKey("NLUResult")?(String)paramsObject.get("NLUResult"):"NA";
						data.addToLog(data.getCurrentElement(),"NLUResult : "+nluResult);
						data.setSessionData(Constants.NLUResult, nluResult);
						
						// START - CS1240948 :: Fetch NoMatch, NoInput counter from Event Data
						String noMatch_Count = "0";
						String noInput_Count = "0";
						
						if (paramsObject.containsKey("NM_Count")) {
							noMatch_Count = (String) paramsObject.get("NM_Count");
						}
						
						if (paramsObject.containsKey("NI_Count")) {
							noInput_Count = (String) paramsObject.get("NI_Count");
						}
						
						data.setSessionData("NoMatch", noMatch_Count);
						data.setSessionData("NoInput", noInput_Count);
						//END - CS1240948 :: Fetch NoMatch, NoInput counter from Event Data
						
						//CS1240947 | Farmers Insurance | US | All BUs - Refine Representative Request handling
						
						if (value.equals("Representative") || value.equals("representative-request")) {
							
							String RepCounterHandling=(String) data.getSessionData(Constants.S_REP_HANDLING_COUNTER);
							
							int Repcounter=Integer.parseInt(RepCounterHandling);
					            
							if (Repcounter == 0) {
					                
								Repcounter++;
					               
					                String repCountString=String.valueOf(Repcounter);
					                
					                data.setSessionData(Constants.S_REP_HANDLING_COUNTER, repCountString);
					                
					                data.addToLog("First time Representative utterance Occured so setting the counter Value to  First Loop 1", repCountString);
					                
					                data.addToLog("After setting the value to 1",(String) data.getSessionData(Constants.S_REP_HANDLING_COUNTER));
					                
					            } 
							
							else if (Repcounter == 1) {
								
								Repcounter++;
					              
					                
					                String repCountString=String.valueOf(Repcounter);
					                
					                data.setSessionData(Constants.S_REP_HANDLING_COUNTER, repCountString);
					                
					                data.addToLog("Second time Representative utterance Occured so setting the counter Value to First Loop 2", repCountString);
					           
					                data.addToLog("After setting the value to 2", (String) data.getSessionData(Constants.S_REP_HANDLING_COUNTER));
					                
					            
					            }
					        }
						
						/**
						 * NLU Change
						 */
						if (value==null || value.isEmpty() || value.equalsIgnoreCase("NA") || value.equalsIgnoreCase("NI") ) {
							String menuIDMode = (String)data.getSessionData("S_MENU_ID_MODE");
							/**
							 * NLU Change
							 */
							if(!Constants.NA.equalsIgnoreCase(nluResult)) {
								if(!Constants.Success.equalsIgnoreCase(nluResult)) {
									if("true".equalsIgnoreCase(noMatchFlag)) {
										returnValue= Constants.NOMATCH;
									}else {
									returnValue = Constants.NLU_FAILURE;
									data.setSessionData("NLU_FAILURE_FLAG", 'Y');
									}
								}
							}else {
								if("true".equalsIgnoreCase(noMatchFlag)) {
									if(value.equalsIgnoreCase("NI")) {
										returnValue = Constants.NOINPUT;
									}
									else {
									returnValue = Constants.NOMATCH;
									}
									data.setSessionData("S_SPEECH_MN_NM_TRUE_FLAG", "Y");
								}else {
									returnValue = Constants.NOINPUT;
								}
							}
							/**
							 * NLU Change
							 */
							if(menuIDMode!=null && "FallBackDTMF".equals(menuIDMode.trim())){
								exitState="retry";
							}
							
							else {
								exitState="done";
							}

						} else {
							returnValue = value;
						}
						
						data.setSessionData(Constants.value, returnValue);
						
					}else {
						returnValue="ER";	
					}
				}else {
					returnValue="ER";
				}
				if(returnValue!=null && returnValue.contains("#"))
					returnValue = returnValue.replaceAll("#", "");
				
				data.setSessionData(Constants.value, returnValue);
				
			}else {
				
				exitState = "loop";
			}
			}

			//Caller Verification
			else {
				
				if("true".equalsIgnoreCase(iscustomExit)||"true".equalsIgnoreCase(issessionend)||"true".equalsIgnoreCase(agentHandoff)) {
					System.out.println(  "Value of eventData : " + eventData);
					if("true".equalsIgnoreCase(secureFlow)) {data.addToLog(data.getCurrentElement(), "Value of eventData : " + "*******");}else{data.addToLog(data.getCurrentElement(), "Value of eventData : " + eventData);}
					
					if(eventData!=null && !"".equals(eventData)) {
						eventData= eventData.replace("\\", "");
						System.out.println("eventData Json: " + eventData);
						if("true".equalsIgnoreCase(secureFlow)) {data.addToLog(data.getCurrentElement(), "Value of eventData : " + "*******");}else{data.addToLog(data.getCurrentElement(), "eventData Json: " + eventData);}
						JSONParser objJSONParser = new JSONParser();
						JSONObject jsonObject = (JSONObject)objJSONParser.parse(eventData);
						JSONObject paramsObject = jsonObject.containsKey("Params")?(JSONObject)jsonObject.get("Params"):null;
						
						data.setSessionData(Constants.CCAI_Event_Data, eventData);
						
						if(paramsObject!=null) {
							String noMatchFlag = paramsObject.containsKey("NoMatchFlag")?(String)paramsObject.get("NoMatchFlag"):"";
							
							data.addToLog(data.getCurrentElement(), "NoMatch Flag :"+noMatchFlag);
							
							//JSONObject ccaiparamsobject = jsonObject.containsKey("value")?(JSONObject)jsonObject.get("value"):null;
							

							//JSONObject paramsObject = jsonObject.containsKey("value")?(JSONObject)jsonObject.get("Agent Response"):null;
							

							//JSONObject paramsObject = jsonObject.containsKey("value")?(JSONObject)jsonObject.get("Customer response"):null;
							
							String CCAI_Agent_FirstName =paramsObject.containsKey("CCAI_Agent_FirstName")?(String)paramsObject.get("CCAI_Agent_FirstName"):"NA";
							String CCAI_Agent_LastName =paramsObject.containsKey("CCAI_Agent_LastName")?(String)paramsObject.get("CCAI_Agent_LastName"):"NA";
							String CCAI_Agent_State =paramsObject.containsKey("CCAI_Agent_State")?(String)paramsObject.get("CCAI_Agent_State"):"NA";
							String CCAI_Agent_Code_Producer_Code =paramsObject.containsKey("CCAI_Agent_Producer_Code")?(String)paramsObject.get("CCAI_Agent_Producer_Code"):"NA";
							String CCAI_Agency_Name =paramsObject.containsKey("CCAI_Agency_Name")?(String)paramsObject.get("CCAI_Agency_Name"):"NA";
							String CCAI_Agent_Verified =paramsObject.containsKey("CCAI_Agent_Verified")?(String)paramsObject.get("CCAI_Agent_Verified"):"false";
							String CCAI_Agent_Policy_Number =paramsObject.containsKey("CCAI_Agent_Policy_Number")?(String)paramsObject.get("CCAI_Agent_Policy_Number"):"NA";
							
							if(CCAI_Agent_Verified.equalsIgnoreCase("YES")) {
								
								CCAI_Agent_Verified="true";
							}
							
							
							if(CCAI_Agent_Verified.equalsIgnoreCase("NO")) {
								
								CCAI_Agent_Verified="false";
							}
							
							data.setSessionData(Constants.CCAI_Agent_FirstName, CCAI_Agent_FirstName);
							data.setSessionData(Constants.CCAI_Agent_LastName, CCAI_Agent_LastName);
							data.setSessionData(Constants.CCAI_Agent_State, CCAI_Agent_State);
							data.setSessionData(Constants.CCAI_Agent_Code_Producer_Code, CCAI_Agent_Code_Producer_Code);
							data.setSessionData(Constants.CCAI_Agency_Name, CCAI_Agency_Name);
							data.setSessionData(Constants.CCAI_Agent_Verified, CCAI_Agent_Verified);
							data.setSessionData(Constants.CCAI_Agent_Policy_Number, CCAI_Agent_Policy_Number);
							
							
							String CCAI_Customer_FirstName =paramsObject.containsKey("CCAI_Customer_FirstName")?(String)paramsObject.get("CCAI_Customer_FirstName"):"NA";
							String CCAI_Customer_LastName =paramsObject.containsKey("CCAI_Customer_LastName")?(String)paramsObject.get("CCAI_Customer_LastName"):"NA";
							String CCAI_Customer_SSN_Last_4 =paramsObject.containsKey("CCAI_Customer_SSN_Last_4")?(String)paramsObject.get("CCAI_Customer_SSN_Last_4"):"NA";
							String CCAI_Customer_DOB =paramsObject.containsKey("CCAI_Customer_DOB")?(String)paramsObject.get("CCAI_Customer_DOB"):"NA";
							String CCAI_Customer_DLNumber =paramsObject.containsKey("CCAI_Customer_DLNumber")?(String)paramsObject.get("CCAI_Customer_DLNumber"):"NA";
							String CCAI_Customer_Address_Street_1 =paramsObject.containsKey("CCAI_Customer_Address_Street_1")?(String)paramsObject.get("CCAI_Customer_Address_Street_1"):"NA";
							String CCAI_Customer_Address_Street_2 =paramsObject.containsKey("CCAI_Customer_Address_Street_2")?(String)paramsObject.get("CCAI_Customer_Address_Street_2"):"NA";
							String CCAI_Customer_Address_City =paramsObject.containsKey("CCAI_Customer_Address_City")?(String)paramsObject.get("CCAI_Customer_Address_City"):"NA";
							String CCAI_Customer_Address_State =paramsObject.containsKey("CCAI_Customer_Address_State")?(String)paramsObject.get("CCAI_Customer_Address_State"):"NA";
							String CCAI_Customer_Address_ZipCode =paramsObject.containsKey("CCAI_Customer_Address_ZipCode")?(String)paramsObject.get("CCAI_Customer_Address_ZipCode"):"NA";
							String CCAI_Customer_Address_Country =paramsObject.containsKey("CCAI_Customer_Address_Country")?(String)paramsObject.get("CCAI_Customer_Address_Country"):"NA";
							String CCAI_Customer_Address_Type =paramsObject.containsKey("CCAI_Customer_Address_Type")?(String)paramsObject.get("CCAI_Customer_Address_Type"):"NA";
							String CCAI_Customer_Policy_Number =paramsObject.containsKey("CCAI_Customer_Policy_Number")?(String)paramsObject.get("CCAI_Customer_Policy_Number"):"NA";
							String CCAI_Customer_BillingAccount_Number =paramsObject.containsKey("CCAI_Customer_BillingAccount_Number")?(String)paramsObject.get("CCAI_Customer_BillingAccount_Number"):"NA";
							String CCAI_Customer_Verified =paramsObject.containsKey("CCAI_Customer_Verified")?(String)paramsObject.get("CCAI_Customer_Verified"):"false";
							String CCAI_Customer_ECN =paramsObject.containsKey("CCAI_Customer_ECN")?(String)paramsObject.get("CCAI_Customer_ECN"):"NA";
							
							if(CCAI_Customer_Verified.equalsIgnoreCase("YES")) {
								
								CCAI_Customer_Verified="true";
							}
							
							
							if(CCAI_Customer_Verified.equalsIgnoreCase("NO")) {
								
								CCAI_Customer_Verified="false";
							}
							
							
							data.setSessionData(Constants.CCAI_Customer_FirstName, CCAI_Customer_FirstName);
							data.setSessionData(Constants.CCAI_Customer_LastName, CCAI_Customer_LastName);
							data.setSessionData(Constants.CCAI_Customer_SSN_Last_4, CCAI_Customer_SSN_Last_4);
							data.setSessionData(Constants.CCAI_Customer_DOB, CCAI_Customer_DOB);
							data.setSessionData(Constants.CCAI_Customer_DLNumber, CCAI_Customer_DLNumber);
							data.setSessionData(Constants.CCAI_Customer_Address_Street_1, CCAI_Customer_Address_Street_1);
							data.setSessionData(Constants.CCAI_Customer_Address_Street_2, CCAI_Customer_Address_Street_2);
							data.setSessionData(Constants.CCAI_Customer_Address_City, CCAI_Customer_Address_City);
							data.setSessionData(Constants.CCAI_Customer_Address_State, CCAI_Customer_Address_State);
							data.setSessionData(Constants.CCAI_Customer_Address_ZipCode, CCAI_Customer_Address_ZipCode);
							data.setSessionData(Constants.CCAI_Customer_Address_Country, CCAI_Customer_Address_Country);
							data.setSessionData(Constants.CCAI_Customer_Address_Type, CCAI_Customer_Address_Type);
							data.setSessionData(Constants.CCAI_Customer_Policy_Number, CCAI_Customer_Policy_Number);
							data.setSessionData(Constants.CCAI_Customer_BillingAccount_Number, CCAI_Customer_BillingAccount_Number);
							data.setSessionData(Constants.CCAI_Customer_Verified, CCAI_Customer_Verified);
							data.setSessionData(Constants.CCAI_Customer_ECN, CCAI_Customer_ECN);
							
							data.addToLog(data.getCurrentElement(), "CCAI_Agent_FirstName : "+CCAI_Agent_FirstName+" :: CCAI_Agent_LastName : "+CCAI_Agent_LastName);
							data.addToLog(data.getCurrentElement(), "CCAI_Agent_State : "+CCAI_Agent_State+" :: CCAI_Agent_Code_Producer_Code : "+CCAI_Agent_Code_Producer_Code);
							
							data.addToLog(data.getCurrentElement(), "CCAI_Agency_Name : "+CCAI_Agency_Name+" :: CCAI_Agent_Policy_Number : "+CCAI_Agent_LastName);
							
							data.addToLog(data.getCurrentElement(), "CCAI_Agent_Verified : "+CCAI_Agent_Verified+" :: CCAI_Agent_Policy_Number : "+CCAI_Agent_Policy_Number);
							
							data.addToLog(data.getCurrentElement(), "CCAI_Customer_FirstName : "+CCAI_Agent_FirstName+" :: CCAI_Customer_LastName : "+CCAI_Customer_LastName);
							data.addToLog(data.getCurrentElement(), "CCAI_Customer_SSN_Last_4 : "+CCAI_Customer_SSN_Last_4+" :: CCAI_Customer_DOB : "+CCAI_Customer_DOB);
							
							data.addToLog(data.getCurrentElement(), "CCAI_Customer_DLNumber : "+CCAI_Customer_DLNumber+" :: CCAI_Customer_Address_Street_1 : "+CCAI_Customer_Address_Street_1);
							data.addToLog(data.getCurrentElement(), "CCAI_Customer_Address_Street_2 : "+CCAI_Customer_Address_Street_2+" :: CCAI_Customer_Address_City : "+CCAI_Customer_Address_City);
							data.addToLog(data.getCurrentElement(), "CCAI_Customer_Address_State : "+CCAI_Customer_Address_State+" :: CCAI_Customer_Address_ZipCode : "+CCAI_Customer_Address_ZipCode);
							
							data.addToLog(data.getCurrentElement(), "CCAI_Customer_Address_Country : "+CCAI_Customer_Address_Country+" :: CCAI_Customer_Address_Type : "+CCAI_Customer_Address_Type);
							data.addToLog(data.getCurrentElement(), "CCAI_Customer_Policy_Number : "+CCAI_Customer_Policy_Number+" :: CCAI_Customer_BillingAccount_Number : "+CCAI_Customer_BillingAccount_Number);
							
							data.addToLog(data.getCurrentElement(), "CCAI_Customer_Verified : "+CCAI_Customer_Verified+" :: CCAI_Customer_ECN : "+CCAI_Customer_ECN);
							
							
							exitState="done";
							
							returnValue="SU";
							
							
						}
					}
				}
				
				else {
					exitState = "loop";
				}
			}
		

		} catch (Exception e) {
			data.addToLog(elementName,"Exception in Speech fLOW  :: "+e);
			
		}
		return exitState;
	}
}