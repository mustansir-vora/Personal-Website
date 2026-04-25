package com.farmers.audio;

import com.audium.server.AudiumException;
import com.audium.server.proxy.VoiceElementInterface;
import com.audium.server.session.ElementAPI;
import com.audium.server.xml.VoiceElementConfig;
import com.audium.server.xml.VoiceElementConfig.AudioGroup;
import com.audium.server.xml.VoiceElementConfig.StaticAudio;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class DynamicAudioAdmin implements VoiceElementInterface{

	CommonAPIAccess caa=null;
	@Override
	public VoiceElementConfig getConfig(String elementName, ElementAPI elementAPI, VoiceElementConfig voiceElementConfig) throws AudiumException {
		try {
			caa = CommonAPIAccess.getInstance(elementAPI);
			AudioGroup initialAudioGroup = null;
			String currElementName = elementAPI.getCurrentElement();
			if(currElementName.contains(Constants.DOT)) {
				currElementName = currElementName.split(Constants.DOT_SEPERATOR)[1];
			}

			String wavFileName = (String) elementAPI.getSessionData(elementAPI.getCurrentElement()+"_WAV");
			String ttsFileName = (String) elementAPI.getSessionData(elementAPI.getCurrentElement()+"_TTS");
			
			elementAPI.addToLog(elementAPI.getCurrentElement(), "Session key : "+elementAPI.getCurrentElement() +" Session value  : "+wavFileName+" Session TTS  : "+ttsFileName);

			elementAPI.addToLog(elementAPI.getCurrentElement(), "initialAudioGroup ::"+initialAudioGroup); 
			initialAudioGroup = voiceElementConfig.new AudioGroup(Constants.INITIAL_AUDIO_GROUP, true,1);
			elementAPI.addToLog(elementAPI.getCurrentElement(), "initialAudioGroup ::"+initialAudioGroup); 
			String[] arrWaveFile = null;
			String[] arrTTSFile = null;
			if(wavFileName!=null)
				arrWaveFile = wavFileName.split(Constants.PIPE);
			
			if(ttsFileName!=null)
			    arrTTSFile = ttsFileName.split(Constants.PIPE);
			
			if((arrTTSFile==null || "".equals(arrTTSFile))&& (arrWaveFile == null || "".equals(arrTTSFile))) {
				arrWaveFile = new String[1];
				arrWaveFile[0]="SILENCE.wav";
			}
			
			for(int i=0 ;(arrTTSFile!=null && i<arrTTSFile.length) || (arrWaveFile!=null && i <arrWaveFile.length);i++) {
				String audio ="";
				String tts ="";
				try{audio =arrWaveFile[i];}catch(Exception e) {};
				try{tts =arrTTSFile[i];}catch(Exception e) {};
				if(tts==null ||"".equals(tts)) {
					tts = " ";
				}
				StaticAudio staticAudio = voiceElementConfig.new StaticAudio(tts,audio.trim());
				initialAudioGroup.addAudioItem(staticAudio);
				initialAudioGroup.setBargein(false);
				elementAPI.addToLog(elementAPI.getCurrentElement(), "Setting ::"+audio +"::"+tts); 
			}

			voiceElementConfig.setAudioGroup(initialAudioGroup);
		} catch (Exception e) {
			elementAPI.addToLog(elementAPI.getCurrentElement(),"Exception in DynamicAudio :loadInitialAudioGroup   :: "+e);
			caa.printStackTrace(e);
		}
		return voiceElementConfig;
	}
	
	public static void main(String[] args) {
		String[] arrWaveFile = null;
		String[] arrTTSFile = null;
		if(arrTTSFile==null &arrWaveFile == null) {
			arrWaveFile = new String[1];
			arrWaveFile[0]="2sec_ Silence_2.wav";
		}

		for(int i=0 ;(arrTTSFile!=null && i<arrTTSFile.length) || (arrWaveFile!=null && i <arrWaveFile.length);i++) {
			String audio ="";
			String tts ="";
			try{audio =arrWaveFile[i];}catch(Exception e) {};
			try{tts =arrTTSFile[i];}catch(Exception e) {};
			if(tts==null ||"".equals(tts)) {
				tts = "<speak><break time=\"3s\"/></speak>";
			}
			System.out.println("tts "+tts + "audio"+audio);
		}

	}
}
