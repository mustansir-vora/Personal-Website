package com.farmers.NewSpeechflow;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.audium.server.AudiumException;
import com.audium.server.proxy.VoiceElementInterface;
import com.audium.server.session.ElementAPI;
import com.audium.server.xml.VoiceElementConfig;
import com.audium.server.xml.VoiceElementConfig.StaticAudio;
import com.farmers.util.SayItSmartUtility;

public class MenuLoader implements VoiceElementInterface {

    public VoiceElementConfig getConfig(String elementName, ElementAPI elementAPI, VoiceElementConfig defaults) throws AudiumException {
        String getMenuID = (String) elementAPI.getElementData("NewSpeechDynamicMenu", "MenuID");
        Map<String, Map<String, Object>> mapMenuDetails = null;
        elementAPI.addToLog(getMenuID, "Value of Menu ID : " + getMenuID);
        VoiceElementConfig.AudioGroup initialAudioGroup = defaults.new AudioGroup("initial_audio_group", true, 1);
        SayItSmartUtility sayItSmartUtility = new SayItSmartUtility();
        String prefLang = (String) elementAPI.getSessionData("S_PREF_LANG");
        elementAPI.addToLog(elementAPI.getCurrentElement(), "Value of S_PREF_LANG In New Updated Code : " + prefLang);
        String settingName = "dtmf_keypress";

        try {
            mapMenuDetails = (Map<String, Map<String, Object>>) elementAPI.getApplicationAPI().getApplicationData("A_MENU_DETAILS");
            elementAPI.addToLog(getMenuID, "Menu Hash Map Details : " + mapMenuDetails);
            String globalMaxCount = (String) elementAPI.getApplicationAPI().getApplicationData("A_GLOBAL_MAX_COUNT");
            elementAPI.addToLog(getMenuID, "Global Max Count: " + globalMaxCount);
            String globalNIExWav = (String) elementAPI.getApplicationAPI().getApplicationData("A_GLOBAL_NI_EX_WAV");
            String globalNMExWav = (String) elementAPI.getApplicationAPI().getApplicationData("A_GLOBAL_NM_EX_WAV");
            elementAPI.addToLog(getMenuID, "Global NI Max Exceed Wav: " + globalNIExWav);
            elementAPI.addToLog(getMenuID, "Global NM Max Exceed Wav: " + globalNMExWav);
            if (null != mapMenuDetails && mapMenuDetails.size() > 1) {
                Map<String, Object> menuDetails = mapMenuDetails.get(getMenuID);
                elementAPI.addToLog(getMenuID, "Value of menuDetails Is : " + menuDetails);
                List<String> initialPrompts = (List<String>) menuDetails.get("MenuID_InitialPrompt");
                elementAPI.addToLog(getMenuID, "Value of initialPrompts Is : " + initialPrompts);

                if (null != initialPrompts) {
                    elementAPI.addToLog(getMenuID, "Initial Prompts is Not Null");
                    for (String prompt : initialPrompts) {
                        if (prompt.startsWith("S_") || prompt.startsWith("VXML")) {
                            String[] promptParts;
                            if (prompt.contains("-")) {
                                promptParts = prompt.split("-");
                                if (promptParts.length > 1) {
                                    String sessionDataKey = promptParts[0];
                                    String sessionDataType = promptParts[1];
                                    String sessionDataValue = (String) elementAPI.getSessionData(sessionDataKey);
                                    if ("CURR".equals(sessionDataType)) {
                                        sayItSmartUtility.playRPCurrency(defaults, initialAudioGroup, elementAPI, sessionDataValue);
                                    } else if ("DATE".equals(sessionDataType)) {
                                        sayItSmartUtility.playIndianDate(defaults, initialAudioGroup, elementAPI, sessionDataValue, "ddmmyyyy");
                                    } else if ("DIGIT".equals(sessionDataType)) {
                                        sayItSmartUtility.playDigits(defaults, initialAudioGroup, elementAPI, sessionDataValue);
                                    } else if ("TIME".equals(sessionDataType)) {
                                        sayItSmartUtility.playTime(defaults, initialAudioGroup, elementAPI, sessionDataValue);
                                    } else if ("NUMBER".equals(sessionDataType)) {
                                        sayItSmartUtility.playIndianNumber(defaults, initialAudioGroup, elementAPI, sessionDataValue);
                                    } else if ("EWT".equals(sessionDataType)) {
                                        sayItSmartUtility.playTime11(defaults, initialAudioGroup, elementAPI, Long.parseLong(sessionDataValue));
                                    } else if ("ALPHA_NUM".equals(sessionDataType)) {
                                        sayItSmartUtility.playISINID(defaults, initialAudioGroup, elementAPI, sessionDataValue);
                                    }
                                }
                            } else {
                                elementAPI.addToLog("Wav File Format Configured Incorrectly in the File: ", elementName);
                            }
                        } else {
                            elementAPI.addToLog(getMenuID, "Initial Prompts Loading from Property File");
                            elementAPI.addToLog(getMenuID, "Value of Loaded Prompts : " + prompt);
                            VoiceElementConfig.StaticAudio audioFile = defaults.new StaticAudio("", prompt);
                            initialAudioGroup.addAudioItem(audioFile);
                        }
                    }
                } else {
                    elementAPI.addToLog(getMenuID, "Value of loaded Menu Properties is Null");
                }
                defaults.setAudioGroup(initialAudioGroup);
                elementAPI.addToLog(getMenuID, "Value of initialAudioGroup : " + initialAudioGroup);

                VoiceElementConfig.AudioGroup noinputAudioGroup1 = defaults.new AudioGroup("noinput_audio_group", true, 1);
                VoiceElementConfig.AudioGroup noinputAudioGroup2 = defaults.new AudioGroup("noinput_audio_group", true, 2);
                VoiceElementConfig.AudioGroup noinputAudioGroup3 = defaults.new AudioGroup("noinput_audio_group", true, 3);

                VoiceElementConfig.AudioGroup nomatchAudioGroup1 = defaults.new AudioGroup("nomatch_audio_group", true, 1);
                VoiceElementConfig.AudioGroup nomatchAudioGroup2 = defaults.new AudioGroup("nomatch_audio_group", true, 2);
                VoiceElementConfig.AudioGroup nomatchAudioGroup3 = defaults.new AudioGroup("nomatch_audio_group", true, 3);

                loadAudioGroup(elementAPI, elementName, getMenuID, (List<String>) menuDetails.get("MenuID_NoInputPrompt_1"), noinputAudioGroup1, "A_GLOBAL_NI1_WAV", "NoInput1", defaults, initialPrompts);
                loadAudioGroup(elementAPI, elementName, getMenuID, (List<String>) menuDetails.get("MenuID_NoInputPrompt_2"), noinputAudioGroup2, "A_GLOBAL_NI2_WAV", "NoInput2", defaults, initialPrompts);
                loadAudioGroup(elementAPI, elementName, getMenuID, (List<String>) menuDetails.get("MenuID_NoMatchPrompt_1"), nomatchAudioGroup1, "A_GLOBAL_NM1_WAV", "NoMatch1", defaults, initialPrompts);
                loadAudioGroup(elementAPI, elementName, getMenuID, (List<String>) menuDetails.get("MenuID_NoMatchPrompt_2"), nomatchAudioGroup2, "A_GLOBAL_NM2_WAV", "NoMatch2", defaults, initialPrompts);

                StaticAudio globalNIExWavGroup = defaults.new StaticAudio(globalNIExWav);
                StaticAudio globalNMExWavGroup = defaults.new StaticAudio(globalNMExWav);
                noinputAudioGroup3.addAudioItem(globalNIExWavGroup);
                nomatchAudioGroup3.addAudioItem(globalNMExWavGroup);

                defaults.setAudioGroup(noinputAudioGroup1);
                defaults.setAudioGroup(noinputAudioGroup2);
                defaults.setAudioGroup(noinputAudioGroup3);
                defaults.setAudioGroup(nomatchAudioGroup1);
                defaults.setAudioGroup(nomatchAudioGroup2);
                defaults.setAudioGroup(nomatchAudioGroup3);

                Map<String, Object> dtmfOptions = (Map<String, Object>) menuDetails.get("MenuID_DTMF_Info");
                elementAPI.addToLog(getMenuID, "dtmfOptions : " + dtmfOptions);
                if (dtmfOptions != null) {
                    for (Entry<String, Object> entry : dtmfOptions.entrySet()) {

                        String dtmfKey = entry.getKey();
                        String dtmfText = (String) entry.getValue();
                    	elementAPI.addToLog(getMenuID, "dtmfKey : " + dtmfKey +" dtmfText :"+dtmfText);
                        defaults.addSettingValue(settingName, dtmfKey);
                        elementAPI.addToLog(getMenuID, "Value of MenuID_DTMF_Info : " + dtmfText + ", DTMF Option = " + dtmfKey);
                    }
                }

                String menuIDMode = (String) elementAPI.getSessionData("S_MENU_ID_MODE");
                elementAPI.addToLog("Value of MENU_ID_MODE", menuIDMode);
                
                // START - CS1240948 :: Adding Menu mode = "PURE_DTMF" to be handled exacty the same as Fallback DTMF
 
                if (menuIDMode != null && ("FallBackDTMF".equalsIgnoreCase(menuIDMode) || "PURE_DTMF".equalsIgnoreCase(menuIDMode)) && prefLang != null && ("EN".equalsIgnoreCase(prefLang) || "SP".equalsIgnoreCase(prefLang))) {
                    elementAPI.addToLog(getMenuID, "Menu ID Mode has FallBackDTMF");
                    defaults.setSettingValue("combinedTriesCount", "1");
                    defaults.setSettingValue("combinedTriesFlag", "Y");
                    noinputAudioGroup1.removeAllAudioItems();
                    //noinputAudioGroup1.addAudioItem(globalNIExWavGroup);
                    //defaults.setAudioGroup(noinputAudioGroup1);
                    nomatchAudioGroup1.removeAllAudioItems();
                    //nomatchAudioGroup1.addAudioItem(globalNMExWavGroup);
                    //defaults.setAudioGroup(nomatchAudioGroup1);
                 // END - CS1240948 :: Adding Menu mode = "PURE_DTMF" to be handled exacty the same as Fallback DTMF
                } else {
                    defaults.setSettingValue("combinedTriesCount", globalMaxCount);
                    defaults.setSettingValue("combinedTriesFlag", "Y");
                    elementAPI.addToLog(getMenuID, "Menu ID Mode Does not have FallBackDTMF");
                }
            }
        } catch (Exception e) {
            elementAPI.addToLog("Exception Occurred :" + e.getMessage(), elementName);
        }
        return defaults;
    }

    private void loadAudioGroup(ElementAPI elementAPI, String elementName, String getMenuID, List<String> prompts, VoiceElementConfig.AudioGroup audioGroup, String globalPromptKey, String logPrefix, VoiceElementConfig defaults, List<String> initialPrompts) {
        elementAPI.addToLog(getMenuID, "Value of " + logPrefix + " Prompts: " + prompts);
        elementAPI.addToLog(getMenuID, "Value of !" + logPrefix + "Prompts.isEmpty(): " + !prompts.isEmpty());
        if (!prompts.isEmpty()) {
            try {
                for (String prompt : prompts) {
                    StaticAudio phrase = defaults.new StaticAudio("", prompt);
                    audioGroup.addAudioItem(phrase);
                    elementAPI.addToLog(getMenuID, logPrefix + " Prompts Loaded from Property File With Menu Specific");
                    elementAPI.addToLog(getMenuID, "Value of Loaded Prompts: " + prompt);
                }
            } catch (Exception e) {
                elementAPI.addToLog("Exception Occurred in If - " + logPrefix + ": " + e.getMessage(), elementName);
            }
        } else {
            try {
                String globalPrompt = (String) elementAPI.getApplicationAPI().getApplicationData(globalPromptKey);
                StaticAudio phrase = defaults.new StaticAudio(globalPrompt);
                audioGroup.addAudioItem(phrase);
                elementAPI.addToLog(getMenuID, "Global " + logPrefix + " Prompts Loaded from Property File");
                elementAPI.addToLog(getMenuID, "Value of Loaded Prompts: " + globalPrompt);

                if (initialPrompts != null) {
                    elementAPI.addToLog(getMenuID, "Initial Prompts is Not Null");
                    for (String prompt : initialPrompts) {
                        if (prompt.startsWith("S_")|| prompt.startsWith("VXML")) {
                            String[] promptParts;
                            if (prompt.contains("-")) {
                                promptParts = prompt.split("-");
                                if (promptParts.length > 1) {
                                    String sessionDataKey = promptParts[0];
                                    String sessionDataType = promptParts[1];
                                    String sessionDataValue = (String) elementAPI.getSessionData(sessionDataKey);
                                    if ("CURR".equals(sessionDataType)) {
                                        SayItSmartUtility sayItSmartUtility = new SayItSmartUtility();
                                        sayItSmartUtility.playRPCurrency(defaults, audioGroup, elementAPI, sessionDataValue);
                                    } else if ("DATE".equals(sessionDataType)) {
                                        SayItSmartUtility sayItSmartUtility = new SayItSmartUtility();
                                        sayItSmartUtility.playIndianDate(defaults, audioGroup, elementAPI, sessionDataValue, "ddmmyyyy");
                                    } else if ("DIGIT".equals(sessionDataType)) {
                                        SayItSmartUtility sayItSmartUtility = new SayItSmartUtility();
                                        sayItSmartUtility.playDigits(defaults, audioGroup, elementAPI, sessionDataValue);
                                    } else if ("TIME".equals(sessionDataType)) {
                                        SayItSmartUtility sayItSmartUtility = new SayItSmartUtility();
                                        sayItSmartUtility.playTime(defaults, audioGroup, elementAPI, sessionDataValue);
                                    } else if ("NUMBER".equals(sessionDataType)) {
                                        SayItSmartUtility sayItSmartUtility = new SayItSmartUtility();
                                        sayItSmartUtility.playIndianNumber(defaults, audioGroup, elementAPI, sessionDataValue);
                                    } else if ("EWT".equals(sessionDataType)) {
                                        SayItSmartUtility sayItSmartUtility = new SayItSmartUtility();
                                        sayItSmartUtility.playTime11(defaults, audioGroup, elementAPI, Long.parseLong(sessionDataValue));
                                    }
                                }
                            } else {
                                elementAPI.addToLog("Wav File Format Configured Incorrectly in the File: ", elementName);
                            }
                        } else {
                            elementAPI.addToLog(getMenuID, "Initial Prompts Loading from Property File");
                            elementAPI.addToLog(getMenuID, "Value of Loaded Prompts : " + prompt);
                            VoiceElementConfig.StaticAudio audioFile = defaults.new StaticAudio("", prompt);
                            audioGroup.addAudioItem(audioFile);
                        }
                    }
                } else {
                    elementAPI.addToLog(getMenuID, "Value of loaded Menu Properties is Null");
                }
                elementAPI.addToLog(getMenuID, "Value of " + logPrefix + "AudioGroup: " + audioGroup);
            } catch (Exception e) {
                elementAPI.addToLog("Exception Occurred in Else - " + logPrefix + ": " + e.getMessage(), elementName);
            }
        }
    }
}