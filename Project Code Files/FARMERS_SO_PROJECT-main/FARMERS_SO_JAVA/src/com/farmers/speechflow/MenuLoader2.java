package com.farmers.speechflow;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.audium.server.AudiumException;
import com.audium.server.proxy.VoiceElementInterface;
import com.audium.server.session.ElementAPI;
import com.audium.server.xml.VoiceElementConfig;
import com.audium.server.xml.VoiceElementConfig.StaticAudio;
import com.farmers.util.SayItSmartUtility;

public class MenuLoader2 implements VoiceElementInterface {

    public VoiceElementConfig getConfig(String elementName, ElementAPI elementAPI, VoiceElementConfig defaults) throws AudiumException {
        String getMenuID = (String) elementAPI.getElementData("SpeechDynamicMenu", "MenuID");
        Map<String, Map<String, Object>> mapMenuDetails = null;
        elementAPI.addToLog(getMenuID, "Value of Menu ID : " + getMenuID);
        VoiceElementConfig.AudioGroup initialAudioGroup = defaults.new AudioGroup("initial_audio_group", true);
        SayItSmartUtility sayItSmartUtility = new SayItSmartUtility();
        String settingName = "dtmf_keypress";

        try {
            mapMenuDetails = (Map<String, Map<String, Object>>) elementAPI.getApplicationAPI().getApplicationData("A_MENU_DETAILS");
            elementAPI.addToLog(getMenuID, "Menu Hash Map Details : " + mapMenuDetails);
            String globalMaxCount = (String) elementAPI.getApplicationAPI().getApplicationData("A_GLOBAL_MAX_COUNT");
            elementAPI.addToLog(getMenuID, "Global Max Count: " + globalMaxCount);
            if (null != mapMenuDetails && mapMenuDetails.size() > 1) {
                Map<String, Object> menuDetails = mapMenuDetails.get(getMenuID);
                elementAPI.addToLog(getMenuID, "Value of menuDetails Is : " + menuDetails);
                List<String> initialPrompts = (List<String>) menuDetails.get("MenuID_InitialPrompt");
                elementAPI.addToLog(getMenuID, "Value of initialPrompts Is : " + initialPrompts);

                if (null != initialPrompts) {
                    elementAPI.addToLog(getMenuID, "Initial Prompts is Not Null");
                    for (String prompt : initialPrompts) {
                        if (prompt.startsWith("S_")) {
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
                                    }
                                }
                            } else {
                                elementAPI.addToLog("Wav File Format Configured Incorrectly in the File: ", elementName);
                            }
                        } else {
                            elementAPI.addToLog(getMenuID, "Initial Prompts Loading from Property File");
                            elementAPI.addToLog(getMenuID, "Value of Loaded Prompts : " + prompt);
                            VoiceElementConfig.StaticAudio audioFile = defaults.new StaticAudio(prompt);
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
                VoiceElementConfig.AudioGroup nomatchAudioGroup1 = defaults.new AudioGroup("nomatch_audio_group", true, 1);
                VoiceElementConfig.AudioGroup nomatchAudioGroup2 = defaults.new AudioGroup("nomatch_audio_group", true, 2);

                List<String> noInputPrompts1 = (List<String>) menuDetails.get("MenuID_NoInputPrompt_1");
                List<String> noInputPrompts2 = (List<String>) menuDetails.get("MenuID_NoInputPrompt_2");
                List<String> noMatchPrompts1 = (List<String>) menuDetails.get("MenuID_NoMatchPrompt_1");
                List<String> noMatchPrompts2 = (List<String>) menuDetails.get("MenuID_NoMatchPrompt_2");

                loadAudioGroup(elementAPI, elementName, getMenuID, noInputPrompts1, noinputAudioGroup1, "A_GLOBAL_NI1_WAV", "NoInput1", defaults);
                loadAudioGroup(elementAPI, elementName, getMenuID, noInputPrompts2, noinputAudioGroup2, "A_GLOBAL_NI2_WAV", "NoInput2", defaults);
                loadAudioGroup(elementAPI, elementName, getMenuID, noMatchPrompts1, nomatchAudioGroup1, "A_GLOBAL_NM1_WAV", "NoMatch1", defaults);
                loadAudioGroup(elementAPI, elementName, getMenuID, noMatchPrompts2, nomatchAudioGroup2, "A_GLOBAL_NM2_WAV", "NoMatch2", defaults);

                defaults.setAudioGroup(noinputAudioGroup1);
                defaults.setAudioGroup(noinputAudioGroup2);
                defaults.setAudioGroup(nomatchAudioGroup1);
                defaults.setAudioGroup(nomatchAudioGroup2);

                Map<String, Object> dtmfOptions = (Map<String, Object>) menuDetails.get("MenuID_DTMF_Info");
                if (dtmfOptions != null) {
                    for (Entry<String, Object> entry : dtmfOptions.entrySet()) {
                        String dtmfKey = entry.getKey();
                        String dtmfText = (String) entry.getValue();
                        defaults.addSettingValue(settingName, dtmfKey);
                        elementAPI.addToLog(getMenuID, "Value of MenuID_DTMF_Info : " + dtmfText + ", DTMF Option = " + dtmfKey);
                    }
                }

                String menuIDMode = (String) elementAPI.getSessionData("S_MENU_ID_MODE");
                elementAPI.addToLog("Value of MENU_ID_MODE", menuIDMode);
                if (menuIDMode != null && "FallBackDTMF".equalsIgnoreCase(menuIDMode)) {
                    defaults.addSettingValue("combinedTriesCount", "1");
                    defaults.addSettingValue("combinedTriesFlag", "Y");
                } else {
                    defaults.addSettingValue("combinedTriesCount", globalMaxCount);
                    defaults.addSettingValue("combinedTriesFlag", "Y");
                }
            }
        } catch (Exception e) {
            elementAPI.addToLog("Exception Occurred :" + e.getMessage(), elementName);
        }
        return defaults;
    }

    private void loadAudioGroup(ElementAPI elementAPI, String elementName, String getMenuID, List<String> prompts, VoiceElementConfig.AudioGroup audioGroup, String globalPromptKey, String logPrefix, VoiceElementConfig defaults) {
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
            } catch (Exception e) {
                elementAPI.addToLog("Exception Occurred in Else - " + logPrefix + ": " + e.getMessage(), elementName);
            }
        }
    }
}