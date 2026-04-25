package com.farmers.speechflow;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.Constants;
import com.servion.platform.utilities.ReturnVal;

public class CheckData extends DecisionElementBase {

	public String doDecision(String elementName, DecisionElementData data) throws AudiumException {
		String exitState = "ER";
		String returnValue = Constants.EmptyString;
		String strElementExit  = Constants.EmptyString;
		String enteredValue = Constants.EmptyString;

		try 
		{
			strElementExit =  data.getExitStateHistory().lastElement();
			data.addToLog(data.getCurrentElement(), "Exit State of COMBINED_TRIES : "+ strElementExit);
		}catch (Exception e) {
			data.addToLog(data.getCurrentElement(), "Exception At Exit State : " + e);
		}

		String getMenuID = (String) data.getElementData("SpeechDynamicMenu", "MenuID");
		enteredValue = data.getElementData("COMBINED_TRIES", "value");
		data.addToLog(data.getCurrentElement(), "DTMF Menu Entered Value :  "+enteredValue +" Menu ID : "+getMenuID +" Exit State : "+strElementExit);
		
		//START - CS1240948 :: Fetch NoMatch, NoInput counter from Event Data
		String noMatch_Count = (String) data.getSessionData("NoMatch") != null ? (String) data.getSessionData("NoMatch") : "0";
		String noInput_Count = (String) data.getSessionData("NoInput") != null ? (String) data.getSessionData("NoInput") : "0";
		String combined_Count = Constants.EmptyString;
		//END - CS1240948 :: Fetch NoMatch, NoInput counter from Event Data

		try 
		{
			@SuppressWarnings("unchecked")
			Map<String, Map<String, Object>> mapMenuDetails = (Map<String, Map<String, Object>>) data.getApplicationAPI().getApplicationData("A_MENU_DETAILS");
			if (mapMenuDetails != null && !mapMenuDetails.isEmpty()) {
				Map<String, Object> menuDetails = mapMenuDetails.get(getMenuID);
				if (menuDetails != null) {
					Map<String, Object> dtmfOptions = (Map<String, Object>) menuDetails.get("MenuID_DTMF_Info");
					if (dtmfOptions != null && dtmfOptions.containsKey(enteredValue)) {
						String dtmfText = (String) dtmfOptions.get(enteredValue);
						data.setSessionData("S_DTMF_ENTERED_VALUE", dtmfText);
						data.setSessionData(Constants.value, dtmfText);
						
						
						if (enteredValue.equals("0")) {
							
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
					
						
						exitState = "done";
					}
				}
			}
			data.addToLog(getElementName(), "Value of S_SPEECH_MN_NM_TRUE_FLAG : "+data.getSessionData("S_SPEECH_MN_NM_TRUE_FLAG"));
			if(strElementExit!=null && strElementExit.equalsIgnoreCase("max_nomatch")){
				data.addToLog(getElementName(), "Value of Return Value : "+returnValue);
				
				// START - CS1240948 :: Set NoMatch Counter
					noMatch_Count = String.valueOf(Integer.valueOf(noMatch_Count) + 1) ;
					data.setSessionData("NoMatch", noMatch_Count);
				// END - CS1240948 :: Set NoMatch Counter
				
			} 
			else if(strElementExit!=null && strElementExit.equalsIgnoreCase("max_noinput")){
				if(null!=data.getSessionData("S_SPEECH_MN_NM_TRUE_FLAG") && data.getSessionData("S_SPEECH_MN_NM_TRUE_FLAG").equals("Y")) {
					data.addToLog(getElementName(), "Value of Return Value : "+returnValue);
					
					// START - CS1240948 :: Set NoInput Counter - Final Exit state will be NOMATCH because nomatch has occurred in Speech menu, but actual exit state is NoInput which is why counter for no input is incremented here.
					if (strElementExit.equalsIgnoreCase("max_noinput")) {
							noInput_Count = String.valueOf(Integer.valueOf(noInput_Count) + 1) ;
							data.setSessionData("NoInput", noInput_Count);
					}
					// END - CS1240948 :: Set NoInput Counter
					
				}else {
					data.addToLog(getElementName(), "Value of Return Value : "+returnValue);
					
					// START - CS1240948 :: Set NoInput Counter
						noInput_Count = String.valueOf(Integer.valueOf(noInput_Count) + 1) ;
						data.setSessionData("NoInput", noInput_Count);
					// END - CS1240948 :: Set NoInput Counter
				}
			}
			
			
			// START - CS1240948 :: Added Logic for Handling Tries Dynamically (NoMatch, Noinput & Combined tries - refer to CR doc for exact details)
			data.addToLog(getElementName(), "DTMF :: NoMatch Counter :: " + noMatch_Count);
			data.addToLog(getElementName(), "DTMF :: NoInput Counter :: " + noInput_Count);
			combined_Count = String.valueOf(Integer.parseInt(noMatch_Count) + Integer.parseInt(noInput_Count));
			data.addToLog(getElementName(), "Combined Count :: " + combined_Count);
			
			if (Integer.parseInt(combined_Count) <= 3 && (strElementExit.equalsIgnoreCase("max_noinput") || strElementExit.equalsIgnoreCase("max_nomatch"))) {
				
				switch (combined_Count) {
				
				case "1":
					if (strElementExit.equalsIgnoreCase("max_noinput")) {
						switch (noInput_Count) {
						case "1":
							data.setSessionData("Error_Prompt", "STDERROR_003.wav");
							data.addToLog("Error prompt set into session: ", (String) data.getSessionData("Error_Prompt"));
							exitState = "retry";
							break;
							
						}
					}
					else if (strElementExit.equalsIgnoreCase("max_nomatch")) {
						switch (noMatch_Count) {
						case "1":
							data.setSessionData("Error_Prompt", "STDERROR_003.wav");
							data.addToLog("Error prompt set into session: ", (String) data.getSessionData("Error_Prompt"));
							exitState = "retry";
							break;
							
						}
					}
					
					break;
				
				case "2" :
					if (strElementExit.equalsIgnoreCase("max_noinput")) {
						switch (noInput_Count) {
						case "1":
							data.setSessionData("Error_Prompt", "STDERROR_003.wav");
							data.addToLog("Error prompt set into session: ", (String) data.getSessionData("Error_Prompt"));
							exitState = "retry";
							break;
							
						case "2":
							data.setSessionData("Error_Prompt", "STDERROR_001.wav");
							data.addToLog("Error prompt set into session: ", (String) data.getSessionData("Error_Prompt"));
							exitState = "retry";
							break;
							
						}
					}
					else if (strElementExit.equalsIgnoreCase("max_nomatch")) {
						switch (noMatch_Count) {
						case "1":
							data.setSessionData("Error_Prompt", "STDERROR_003.wav");
							data.addToLog("Error prompt set into session: ", (String) data.getSessionData("Error_Prompt"));
							exitState = "retry";
							break;
							
						case "2":
							exitState = "max_nomatch";
							returnValue = Constants.NOMATCH;
							data.setSessionData(Constants.value, returnValue);
							break;
							
						}
					}
					
					break;
					
				case "3" :
					
					if (strElementExit.equalsIgnoreCase("max_noinput")) {
						
						if (Integer.parseInt(noMatch_Count) > 0) {
							exitState = "max_nomatch";
							returnValue = Constants.NOMATCH;
							data.setSessionData(Constants.value, returnValue);
						}
						else {
							data.setSessionData("Error_Prompt", "STDERROR_002.wav");
							data.addToLog("Error prompt set into session: ", (String) data.getSessionData("Error_Prompt"));
							exitState = "max_noinput";
							returnValue = Constants.NOINPUT;
							data.setSessionData(Constants.value, returnValue);
						}
						
					}
					else if (strElementExit.equalsIgnoreCase("max_nomatch")) {
						exitState = "max_nomatch";
						returnValue = Constants.NOMATCH;
						data.setSessionData(Constants.value, returnValue);
					}
					
					break;
					
				}
			}
			else {
				exitState = "done";
			}
			// END - CS1240948 :: Added Logic for Handling Tries Dynamically (NoMatch, Noinput & Combined tries - refer to CR doc for exact details)
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			data.addToLog(data.getCurrentElement(), "Exception : " + e + " :: Full Exception :: " + sw.toString());
		}
		return exitState;
	}
}