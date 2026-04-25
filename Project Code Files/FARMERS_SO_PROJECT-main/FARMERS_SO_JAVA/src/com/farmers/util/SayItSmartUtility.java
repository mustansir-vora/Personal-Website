package com.farmers.util;
import com.audium.server.AudiumException;
import com.audium.server.session.ElementAPI;
import com.audium.server.xml.VoiceElementConfig;
import com.audium.server.xml.VoiceElementConfig.StaticAudio;

public class SayItSmartUtility {
	/**
	 * @param args
	 *  English -ENG
  		Hindi - HIN
  		Tamil -TAM
  		Malayalam -MAL
  		Kannada -KAN
  		Telugu -TEL
  		Marathi -MAR
  		Guajarati -GUJ
  		Bengali - BEN
  		Punjabi -PNJ
  		Assamese -ASM
  		Oriya -ORI
	 */

	private final static String AudiumSayItSmartDigit = "com.audium.sayitsmart.plugins.AudiumSayItSmartDigit";
	private final static String AudiumSayItSmartFile = "com.audium.sayitsmart.plugins.AudiumSayItSmartFile";
	private static final String	AudiumSayItSmartTime = "com.audium.sayitsmart.plugins.AudiumSayItSmartTime";

	// Number
	private final static String AudiumSayItSmartNumber = "com.audium.sayitsmart.plugins.AudiumSayItSmartNumber";
	private static final String	SERVION_PLUGIN_INDIAN_NUMBER = "ServionSayItSmartIndianNumber";
	private static final String	SERVION_PLUGIN_TAM_NUMBER = "ServionSISIndianNumberTL";
	private static final String	SERVION_PLUGIN_KAN_NUMBER = "ServionSISKannadaNumber";
	private static final String	SERVION_PLUGIN_MAL_NUMBER = "ServionSISIndianNumberML";
	private static final String	SERVION_PLUGIN_BEN_NUMBER = "ServionSISBengaliNumber";

	// Currency
	private static final String SERVION_PLUGIN_INDIAN_CURRENCY = "ServionRPCurrency";// ServionRPCurrencyZero
	private static final String SERVION_PLUGIN_MAL_CURRENCY = "ServionRPCurrencyML";
	private static final String SERVION_PLUGIN_TAM_CURRENCY = "ServionRPCurrencyTL";
	private static final String SERVION_PLUGIN_KAN_CURRENCY = "ServionSISKannadaCurrency";
	private static final String SERVION_PLUGIN_BEN_CURRENCY = "ServionSISBengaliCurrency";
	private static final String SERVION_PLUGIN_ORI_CURRENCY = "ServionSISOriyaCurrency";
	private static final String SERVION_PLUGIN_PUN_CURRENCY = "ServionSISPunjabiCurrency";

	// Date
	private static final String	SERVION_PLUGIN_INDIAN_DATE = "ServionSayItSmartIndianDate";
	private static final String SERVION_PLUGIN_HIN_DATE = "ServionSayItSmartIndianDateHI";
	private static final String	SERVION_PLUGIN_MAL_DATE = "ServionSayItSmartIndianDateML";
	private static final String	SERVION_PLUGIN_TAM_DATE = "ServionSayItSmartIndianDateTL";
	private static final String SERVION_PLUGIN_KAN_DATE = "ServionSayItSmartKannadaDate";
	private static final String SERVION_PLUGIN_BEN_DATE = "ServionSayItSmartBengaliDate";
	private static final String SERVION_PLUGIN_ORI_DATE= "ServionSayItSmartOriyaDate";
	private static final String SERVION_PLUGIN_PUN_DATE= "ServionSayItSmartPunjabiDate";

	String 	systemPromptURL = null;
	public void playDigits(VoiceElementConfig defaults, VoiceElementConfig.AudioGroup audioGroup, ElementAPI elementAPI, Object data)throws AudiumException
	{
		String inputFormat = "number";
		String outputFormat = "digits";
		//		String language = (String) elementAPI.getSessionData(Constants.S_ActiveLanguage);
		String language = (String) elementAPI.getSessionData(Constants.S_DEFAULT_LANG);

		VoiceElementConfig.SayItSmart sayItSmartAudioItem = defaults.new SayItSmart(AudiumSayItSmartDigit, inputFormat, outputFormat, data); 
		sayItSmartAudioItem.setUseDefaultAudioPath(false);
		sayItSmartAudioItem.setUseRecordedAudio(true);
		sayItSmartAudioItem.setAudioPath((String)elementAPI.getSessionData(Constants.S_MediaSysPath));
		sayItSmartAudioItem.setAudioType("wav");
		audioGroup.addAudioItem(sayItSmartAudioItem);
		elementAPI.addToLog("playDigits", "Lang : " + language + " | Data : " + data);
	}

	public void playCurrency(VoiceElementConfig defaults, VoiceElementConfig.AudioGroup audioGroup, ElementAPI elementAPI, Object data)throws AudiumException
	{
		String AudiumSayItSmartCurrency = null;
		String inputFormat = "standard";
		String outputFormat = "rupees_paise";
		//		String language = (String)elementAPI.getSessionData(Constants.S_ActiveLanguage);
		String language = (String)elementAPI.getSessionData(Constants.S_DEFAULT_LANG);

		if(language.equalsIgnoreCase("TAM"))
		{
			AudiumSayItSmartCurrency = SERVION_PLUGIN_TAM_CURRENCY;
		}
		else if(language.equalsIgnoreCase("KAN"))
		{
			AudiumSayItSmartCurrency = SERVION_PLUGIN_KAN_CURRENCY;
		}
		else if(language.equalsIgnoreCase("BEN"))
		{
			AudiumSayItSmartCurrency = SERVION_PLUGIN_BEN_CURRENCY;
		}
		else if(language.equalsIgnoreCase("MAL"))
		{
			AudiumSayItSmartCurrency = SERVION_PLUGIN_MAL_CURRENCY;
		}
		else if(language.equalsIgnoreCase("ORI"))
		{
			AudiumSayItSmartCurrency = SERVION_PLUGIN_ORI_CURRENCY;
		}
		else if(language.equalsIgnoreCase("PUN"))
		{
			AudiumSayItSmartCurrency = SERVION_PLUGIN_PUN_CURRENCY;
		}
		else
		{
			AudiumSayItSmartCurrency = SERVION_PLUGIN_INDIAN_CURRENCY;
		}

		VoiceElementConfig.SayItSmart sayItSmartAudioItem = defaults.new SayItSmart(AudiumSayItSmartCurrency, inputFormat, outputFormat, data); 
		sayItSmartAudioItem.setUseDefaultAudioPath(false);
		sayItSmartAudioItem.setUseRecordedAudio(true);
		sayItSmartAudioItem.setAudioPath((String)elementAPI.getSessionData(Constants.S_MediaSysPath));
		sayItSmartAudioItem.setFileset("enhanced"); // Added during Multilingual CR UAT
		sayItSmartAudioItem.setAudioType("wav");
		audioGroup.addAudioItem(sayItSmartAudioItem);

		elementAPI.addToLog("playCurrency", "Lang : " + language + " | Data : " + data);
	}

	public void playRPCurrency(VoiceElementConfig defaults, VoiceElementConfig.AudioGroup audioGroup, ElementAPI elementAPI, Object data)throws AudiumException
	{	
		String AudiumSayItSmartCurrency = null;
		String inputFormat = "standard";
		//String inputFormat = "enhanced";
		String outputFormat = "rupees_paise";
		//		String language = ((String)elementAPI.getSessionData(Constants.S_ActiveLanguage)).trim();
		String language = ((String)elementAPI.getSessionData(Constants.S_DEFAULT_LANG)).trim();

		if(language.equalsIgnoreCase("TAM"))
		{
			AudiumSayItSmartCurrency = SERVION_PLUGIN_TAM_CURRENCY;
		}
		else if(language.equalsIgnoreCase("KAN"))
		{
			AudiumSayItSmartCurrency = SERVION_PLUGIN_KAN_CURRENCY;
		}
		else if(language.equalsIgnoreCase("BEN"))
		{
			AudiumSayItSmartCurrency = SERVION_PLUGIN_BEN_CURRENCY;
		}
		else if(language.equalsIgnoreCase("MAL"))
		{
			AudiumSayItSmartCurrency = SERVION_PLUGIN_MAL_CURRENCY;
		}
		else if(language.equalsIgnoreCase("ORI"))
		{
			AudiumSayItSmartCurrency = SERVION_PLUGIN_ORI_CURRENCY;
		}
		else if(language.equalsIgnoreCase("PUN"))
		{
			AudiumSayItSmartCurrency = SERVION_PLUGIN_PUN_CURRENCY;
		}
		else
		{
			AudiumSayItSmartCurrency = SERVION_PLUGIN_INDIAN_CURRENCY;
		}

		VoiceElementConfig.SayItSmart sayItSmartAudioItem = defaults.new SayItSmart(AudiumSayItSmartCurrency, inputFormat, outputFormat, data); 
		sayItSmartAudioItem.setFileset("enhanced"); // Added during Multilingual CR UAT
		sayItSmartAudioItem.setUseDefaultAudioPath(false);
		sayItSmartAudioItem.setUseRecordedAudio(true);
		sayItSmartAudioItem.setAudioPath((String)elementAPI.getSessionData(Constants.S_MediaSysPath));
		sayItSmartAudioItem.setAudioType("wav");		
		audioGroup.addAudioItem(sayItSmartAudioItem);

		elementAPI.addToLog("playRPCurrency", "Lang : " + language + " | Data : " + data);
	}

	public void playDate(VoiceElementConfig defaults, VoiceElementConfig.AudioGroup audioGroup, ElementAPI elementAPI, Object data,String inputFormat)throws AudiumException
	{
		String outputFormat = "month_year";		//date
		String AudiumSayItSmartDate = null;
		VoiceElementConfig.SayItSmart sayItSmartAudioItem = null;
		//		String language = (String)elementAPI.getSessionData(Constants.S_ActiveLanguage);
		String language = (String)elementAPI.getSessionData(Constants.S_DEFAULT_LANG);

		if(language.equalsIgnoreCase("TAM"))
		{
			AudiumSayItSmartDate = SERVION_PLUGIN_TAM_DATE;			
		}
		else if(language.equalsIgnoreCase("HIN"))
		{
			AudiumSayItSmartDate = SERVION_PLUGIN_HIN_DATE;
		}
		else if(language.equalsIgnoreCase("MAL"))
		{
			AudiumSayItSmartDate = SERVION_PLUGIN_MAL_DATE;
		}
		else if(language.equalsIgnoreCase("KAN"))
		{
			AudiumSayItSmartDate = SERVION_PLUGIN_KAN_DATE;
		}
		else if(language.equalsIgnoreCase("BEN"))
		{
			AudiumSayItSmartDate = SERVION_PLUGIN_BEN_DATE;
		}
		else
		{
			AudiumSayItSmartDate = SERVION_PLUGIN_INDIAN_DATE;
		}

		sayItSmartAudioItem = defaults.new SayItSmart(AudiumSayItSmartDate, inputFormat, outputFormat, data);

		sayItSmartAudioItem.setFileset("month_enhanced_year");

		sayItSmartAudioItem.setUseDefaultAudioPath(false);
		sayItSmartAudioItem.setUseRecordedAudio(true);
		sayItSmartAudioItem.setAudioPath((String)elementAPI.getSessionData(Constants.S_MediaSysPath));
		sayItSmartAudioItem.setAudioType("wav");
		audioGroup.addAudioItem(sayItSmartAudioItem);

		elementAPI.addToLog("playDate", "Lang : " + language + " | Data : " + data);
	}

	public void playIndianDate(VoiceElementConfig defaults, VoiceElementConfig.AudioGroup audioGroup, ElementAPI elementAPI, Object data,String inputFormat)throws AudiumException
	{
		String outputFormat = "date"; //month_year
		String AudiumSayItSmartDate = null;

		if("".equals(inputFormat) || inputFormat.equals(null))
		{
			inputFormat = "ddmmyyyy";
			elementAPI.addToLog("SAYITSMART", "Setting default date format as ddmmyyyy");
		}

		VoiceElementConfig.SayItSmart sayItSmartAudioItem = null;
		//		String language = (String)elementAPI.getSessionData(Constants.S_ActiveLanguage);
		String language = (String)elementAPI.getSessionData(Constants.S_DEFAULT_LANG);
		if(language.equalsIgnoreCase("TAM"))
		{
			AudiumSayItSmartDate = SERVION_PLUGIN_TAM_DATE;			
		}
		else if(language.equalsIgnoreCase("HIN"))
		{
			AudiumSayItSmartDate = SERVION_PLUGIN_HIN_DATE;
		}
		else if(language.equalsIgnoreCase("MAL"))
		{
			AudiumSayItSmartDate = SERVION_PLUGIN_MAL_DATE;
		}
		else if(language.equalsIgnoreCase("KAN"))
		{
			AudiumSayItSmartDate = SERVION_PLUGIN_KAN_DATE;
		}
		else if(language.equalsIgnoreCase("BEN"))
		{
			AudiumSayItSmartDate = SERVION_PLUGIN_BEN_DATE;
		}
		else if(language.equalsIgnoreCase("ORI"))
		{
			AudiumSayItSmartDate = SERVION_PLUGIN_ORI_DATE;
		}
		else if(language.equalsIgnoreCase("PUN"))
		{
			AudiumSayItSmartDate = SERVION_PLUGIN_PUN_DATE;
		}
		else
		{
			AudiumSayItSmartDate = SERVION_PLUGIN_INDIAN_DATE;
		}

		sayItSmartAudioItem = defaults.new SayItSmart(AudiumSayItSmartDate, inputFormat, outputFormat, data);
		sayItSmartAudioItem.setFileset("enhanced_date");
		sayItSmartAudioItem.setUseDefaultAudioPath(false);
		sayItSmartAudioItem.setUseRecordedAudio(true);
		sayItSmartAudioItem.setAudioPath((String)elementAPI.getSessionData(Constants.S_MediaSysPath));
		sayItSmartAudioItem.setAudioType("wav");
		audioGroup.addAudioItem(sayItSmartAudioItem);

		elementAPI.addToLog("playIndianDate", "Lang : " + language + " | Data : " + data);
	}

	public void playDateAlone(VoiceElementConfig defaults, VoiceElementConfig.AudioGroup audioGroup, ElementAPI elementAPI, Object data,String inputFormat)throws AudiumException
	{
		String outputFormat = "audio";

		VoiceElementConfig.SayItSmart sayItSmartAudioItem = null;
		//		String language = (String)elementAPI.getSessionData(Constants.S_ActiveLanguage);
		String language = (String)elementAPI.getSessionData(Constants.S_DEFAULT_LANG);

		sayItSmartAudioItem = defaults.new SayItSmart(AudiumSayItSmartFile, inputFormat, outputFormat, data);

		//sayItSmartAudioItem.setFileset("standard_date");

		sayItSmartAudioItem.setUseDefaultAudioPath(false);
		sayItSmartAudioItem.setUseRecordedAudio(true);
		sayItSmartAudioItem.setAudioPath((String)elementAPI.getSessionData(Constants.S_MediaSysPath));
		sayItSmartAudioItem.setAudioType("wav");
		audioGroup.addAudioItem(sayItSmartAudioItem);

		elementAPI.addToLog("playDateAlone", "Lang : " + language + " | Data : " + data);
	}
	public void playCardNumber(VoiceElementConfig defaults, VoiceElementConfig.AudioGroup audioGroup, ElementAPI elementAPI, Object data)throws AudiumException
	{
		playDigits( defaults,   audioGroup,   elementAPI,   data);
	}

	public void playNumber(VoiceElementConfig defaults, VoiceElementConfig.AudioGroup audioGroup, ElementAPI elementAPI, Object data)throws AudiumException
	{	  
		String inputFormat = "standard";
		String outputFormat = "standard";
		VoiceElementConfig.SayItSmart sayItSmartAudioItem = defaults.new SayItSmart(AudiumSayItSmartNumber, inputFormat, outputFormat, data); 
		sayItSmartAudioItem.setUseDefaultAudioPath(false);
		sayItSmartAudioItem.setUseRecordedAudio(true);
		sayItSmartAudioItem.setAudioPath((String)elementAPI.getSessionData(Constants.S_MediaSysPath));
		sayItSmartAudioItem.setAudioType("wav");
		sayItSmartAudioItem.setFileset("standard");
		audioGroup.addAudioItem(sayItSmartAudioItem);
	}

	public void playIndianNumber(VoiceElementConfig defaults, VoiceElementConfig.AudioGroup audioGroup, ElementAPI elementAPI, Object data)throws AudiumException
	{	  
		String inputFormat = "standard";
		String outputFormat = "standard";
		VoiceElementConfig.SayItSmart sayItSmartAudioItem = null;

		//		String language = (String)elementAPI.getSessionData(Constants.S_ActiveLanguage);
		String language = (String)elementAPI.getSessionData(Constants.S_DEFAULT_LANG);

		if(language.equalsIgnoreCase("TAM"))
		{
			sayItSmartAudioItem = defaults.new SayItSmart(SERVION_PLUGIN_TAM_NUMBER, inputFormat, outputFormat, data);
		}
		else if(language.equalsIgnoreCase("KAN"))
		{
			sayItSmartAudioItem = defaults.new SayItSmart(SERVION_PLUGIN_KAN_NUMBER, inputFormat, outputFormat, data);
		}
		else if(language.equalsIgnoreCase("MAL"))
		{
			sayItSmartAudioItem = defaults.new SayItSmart(SERVION_PLUGIN_MAL_NUMBER, inputFormat, outputFormat, data);
		}
		else if(language.equalsIgnoreCase("BEN"))
		{
			sayItSmartAudioItem = defaults.new SayItSmart(SERVION_PLUGIN_BEN_NUMBER, inputFormat, outputFormat, data);
		}
		else
		{
			sayItSmartAudioItem = defaults.new SayItSmart(SERVION_PLUGIN_INDIAN_NUMBER, inputFormat, outputFormat, data);
		}

		sayItSmartAudioItem.setUseDefaultAudioPath(false);
		sayItSmartAudioItem.setUseRecordedAudio(true);
		sayItSmartAudioItem.setAudioPath((String)elementAPI.getSessionData(Constants.S_MediaSysPath));
		sayItSmartAudioItem.setAudioType("wav");
		sayItSmartAudioItem.setFileset("enhanced");
		audioGroup.addAudioItem(sayItSmartAudioItem);
		elementAPI.addToLog("playIndianNumber", "Lang : " + language + " | Data : " + data);
	}

	public void playTimeInNumber(VoiceElementConfig defaults, VoiceElementConfig.AudioGroup audioGroup, ElementAPI elementAPI, Object data)throws AudiumException
	{
		String inputFormat = "standard";
		String outputFormat = "standard";

		VoiceElementConfig.SayItSmart sayItSmartAudioItem = defaults.new SayItSmart(AudiumSayItSmartNumber, inputFormat, outputFormat, data);
		sayItSmartAudioItem.setUseDefaultAudioPath(false);
		sayItSmartAudioItem.setUseRecordedAudio(true);
		sayItSmartAudioItem.setAudioPath((String)elementAPI.getSessionData(Constants.S_MediaSysPath));
		sayItSmartAudioItem.setAudioType("wav");
		sayItSmartAudioItem.setFileset("enhanced");
		audioGroup.addAudioItem(sayItSmartAudioItem);
	}

	/*******
	 * AudiumSayItSmartTime 
	 */
	public void playTime(VoiceElementConfig defaults, VoiceElementConfig.AudioGroup audioGroup, ElementAPI elementAPI, Object data)throws AudiumException
	{
		int timeinSecs = Integer.parseInt((String) data);
		int hrs = timeinSecs/3600;
		int mins = (timeinSecs%3600) /60;
		int secs = timeinSecs%60;
		String timeInput = String.valueOf(data);

		/*if(hrs == 0)
			timeInput = "00";
		else
		{
			if(hrs < 10)
				timeInput = "0"+hrs;
			else
				timeInput = ""+hrs;
		}
		if(mins == 0)
			timeInput = timeInput + "00";
		else
		{
			if(mins < 10)
				timeInput = timeInput + "0"+mins;
			else
				timeInput = timeInput + ""+mins;
		}
		if(secs == 0)
			timeInput = timeInput + "00";
		else
		{
			if(secs < 10)
				timeInput = timeInput + "0"+secs;
			else
				timeInput = timeInput + ""+secs;
		}*/

		String inputFormat = "time_hhmm";
		String outputFormat = "time";
		String fileSet = "enhanced_time";
		Object t = timeInput;
		VoiceElementConfig.SayItSmart sayItSmartAudioItem = defaults.new SayItSmart(AudiumSayItSmartTime, inputFormat, outputFormat, t);

		sayItSmartAudioItem.setUseDefaultAudioPath(false);
		sayItSmartAudioItem.setUseRecordedAudio(true);
		sayItSmartAudioItem.setAudioPath((String)elementAPI.getSessionData(Constants.S_MediaSysPath));
		sayItSmartAudioItem.setAudioType("wav");
		sayItSmartAudioItem.setFileset(fileSet);
		audioGroup.addAudioItem(sayItSmartAudioItem);		
	}

	public void playTime11(VoiceElementConfig defaults, VoiceElementConfig.AudioGroup audioGroup, ElementAPI elementAPI, Object data)throws AudiumException
	{
		long timeinSecs = ((Long) data).longValue();
		long hrs = timeinSecs/3600;
		long mins = (timeinSecs%3600) /60;
		long secs = timeinSecs%60;
		String systemDefaultAudioURL = elementAPI.getSessionData(Constants.S_MediaSysPath).toString();

		if(hrs != 0)
		{
			playTimeInNumber(defaults,audioGroup,elementAPI,""+hrs);			

			VoiceElementConfig.StaticAudio hours = defaults.new StaticAudio(systemDefaultAudioURL+"hours.wav");
			hours.setUseDefaultAudioPath(false);
			audioGroup.addAudioItem(hours);		
		}
		if(mins != 0)
		{
			playTimeInNumber(defaults,audioGroup,elementAPI,""+mins);
			VoiceElementConfig.StaticAudio hours = null;
			if(mins == 1) {
				hours = defaults.new StaticAudio(systemDefaultAudioURL+"minute.wav");
			}
			else {
				hours = defaults.new StaticAudio(systemDefaultAudioURL+"minutes.wav");
			}
			hours.setUseDefaultAudioPath(false);
			audioGroup.addAudioItem(hours);					
		}
		if(secs != 0)
		{
			playTimeInNumber(defaults,audioGroup,elementAPI,""+secs);	
			VoiceElementConfig.StaticAudio hours = null;
			if(secs == 1) {
				hours = defaults.new StaticAudio(systemDefaultAudioURL+"second.wav");
			}
			else {
				hours = defaults.new StaticAudio(systemDefaultAudioURL+"seconds.wav");
			}
			hours.setUseDefaultAudioPath(false);
			audioGroup.addAudioItem(hours);								
		}		
	}

	/*
	 * Used to play alphaNumeric reference number
	 */
	public void playISINID(VoiceElementConfig defaults, VoiceElementConfig.AudioGroup audioGroup, ElementAPI elementAPI, String referenceNumber)throws AudiumException
	{
		String systemDefaultAudioURL = elementAPI.getSessionData(Constants.S_MediaSysPath).toString();
		elementAPI.addToLog("Alpha value Audio URL:", systemDefaultAudioURL);
		elementAPI.addToLog("Reference Number:", referenceNumber);
		for(int index = 0; index<referenceNumber.length();index++)
		{
			String eachChar = Character.toString(referenceNumber.charAt(index)).toUpperCase(); 
			VoiceElementConfig.StaticAudio eachCharAudio = defaults.new StaticAudio(eachChar,systemDefaultAudioURL+eachChar+".wav");
			eachCharAudio.setUseDefaultAudioPath(false);
			audioGroup.addAudioItem(eachCharAudio);
		}
	}
	
	public void playAlphaTEXT(VoiceElementConfig defaults, VoiceElementConfig.AudioGroup audioGroup, ElementAPI elementAPI, String tts)throws AudiumException
	{
		VoiceElementConfig.StaticAudio eachCharAudio = defaults.new StaticAudio(tts,"");
		audioGroup.addAudioItem(null);
		eachCharAudio.setUseDefaultAudioPath(false);
		audioGroup.addAudioItem(eachCharAudio);
	}
}
