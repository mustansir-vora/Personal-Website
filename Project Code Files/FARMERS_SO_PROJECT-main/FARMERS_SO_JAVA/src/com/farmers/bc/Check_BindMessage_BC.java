package com.farmers.bc;

import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;

public class Check_BindMessage_BC  extends DecisionElementBase {
	
	
	@Override
	public String doDecision(String currElementName, DecisionElementData data) throws AudiumException {
		
		String exitState = "continue";
		CommonAPIAccess caa = CommonAPIAccess.getInstance(data);
		try {
		if(data.getSessionData("COMPLETE_COUNT")!=null) {
			
			int completeCount = (int)data.getSessionData("COMPLETE_COUNT");
			
			
			String wavefile = (String) data.getSessionData("COMPLETE_WAV");
			String ttsfile = (String) data.getSessionData("COMPLETE_TTS");
			
			String[] arrWaveFile = null;
			String[] arrTTSFile = null;
			if(wavefile!=null)
				arrWaveFile = wavefile.split(Constants.PIPE);
			
			if(ttsfile!=null)
			    arrTTSFile = ttsfile.split(Constants.PIPE);
			
			if(arrTTSFile==null &arrWaveFile == null) {
				arrWaveFile = new String[1];
				arrWaveFile[0]="2sec_ Silence_2.wav";
			}
			
			if((arrTTSFile!=null && completeCount<arrTTSFile.length) || (arrWaveFile!=null && completeCount <arrWaveFile.length)) {
				String audio ="";
				String tts ="";
				try{audio =arrWaveFile[completeCount];}catch(Exception e) {};
				try{tts =arrTTSFile[completeCount];}catch(Exception e) {};
				if(tts==null ||"".equals(tts)) {
					tts = "<speak><break time=\"3s\"/></speak>";
				}
				data.setSessionData("STR_PA_003_WAV", audio);
				data.setSessionData("STR_PA_003_TTS", tts);
				exitState = "loop";
			}
			completeCount++;
			data.setSessionData("COMPLETE_COUNT",completeCount);
		}
		
		}catch(Exception e) {
			data.addToLog(data.getCurrentElement(),"Exception in DynamicAudio :loadInitialAudioGroup   :: "+e);
			caa.printStackTrace(e);
		}
		return exitState;
	}
}
