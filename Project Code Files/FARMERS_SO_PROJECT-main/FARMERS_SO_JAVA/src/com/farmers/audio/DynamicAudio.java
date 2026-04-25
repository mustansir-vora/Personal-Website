package com.farmers.audio;

import com.audium.server.AudiumException;
import com.audium.server.proxy.VoiceElementInterface;
import com.audium.server.session.ElementAPI;
import com.audium.server.xml.VoiceElementConfig;
import com.audium.server.xml.VoiceElementConfig.AudioGroup;
import com.audium.server.xml.VoiceElementConfig.StaticAudio;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class DynamicAudio implements VoiceElementInterface{

	CommonAPIAccess caa=null;
	@Override
	public VoiceElementConfig getConfig(String elementName, ElementAPI elementAPI, VoiceElementConfig voiceElementConfig) throws AudiumException {
		try {
			loadInitialAudioGroup(elementAPI, voiceElementConfig);
			caa = CommonAPIAccess.getInstance(elementAPI);
		} catch (Exception e) {
			elementAPI.addToLog(elementName,"Exception in DynamicAudio :getConfig   :: "+e);
			caa.printStackTrace(e);
		}

		return voiceElementConfig;
	}

	private void loadInitialAudioGroup(ElementAPI elementAPI , VoiceElementConfig voiceElementConfig){
		AudioGroup initialAudioGroup = null;
		try {
			String currElementName = elementAPI.getCurrentElement();
			if(currElementName.contains(Constants.DOT)) {
				currElementName = currElementName.split(Constants.DOT_SEPERATOR)[1];
			}

			String wavFileName = (String) elementAPI.getSessionData(elementAPI.getCurrentElement());
			elementAPI.addToLog(elementAPI.getCurrentElement(), "Session key : "+elementAPI.getCurrentElement() +" Session value  : "+wavFileName);

			if(wavFileName != null && !wavFileName.isEmpty()) {
				if(voiceElementConfig.getAudioGroup(Constants.INITIAL,1) == null) {
					initialAudioGroup = voiceElementConfig.new AudioGroup(Constants.INITIAL, true);
				}else{
					initialAudioGroup = voiceElementConfig.getAudioGroup(Constants.INITIAL,1); 
				}
				
				for (String audio : wavFileName.split(Constants.PIPE_SEPERATOR)) {
					
					if(audio.endsWith(".wav")) {
						StaticAudio staticAudio = voiceElementConfig.new StaticAudio(wavFileName.trim());
						initialAudioGroup.addAudioItem(staticAudio);
					}
					else {
						Object ttsString = wavFileName;
						VoiceElementConfig.SayItSmart sis = null;
						try {
							sis = voiceElementConfig.new SayItSmart("com.audium.sayitsmart.plugins.AudiumSayItSmartString", "string", "tts", ttsString);
							initialAudioGroup.addAudioItem(sis);
						} catch (Exception e) {
							elementAPI.addToLog(elementAPI.getCurrentElement(),"Exception in DynamicAudio :loadInitialAudioGroup   :: "+e);
							caa.printStackTrace(e);
						}
					}
				}

				
				voiceElementConfig.setAudioGroup(initialAudioGroup);
			}	


		} catch (Exception e) {
			elementAPI.addToLog(elementAPI.getCurrentElement(),"Exception in DynamicAudio :loadInitialAudioGroup   :: "+e);
			caa.printStackTrace(e);
		}
	}
}
