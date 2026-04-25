package com.farmers.start;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import com.audium.server.AudiumException;
import com.audium.server.global.ApplicationStartAPI;
import com.audium.server.proxy.StartApplicationInterface;
import com.farmers.util.Constants;


public class AppStart implements StartApplicationInterface{
	FileInputStream fin;
	Properties propLog4j;
	String strAppName = Constants.APPLICATION_NAME;
	AppDataBuilder appDataBuilder;
	private static final LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);;
	private static Logger logger = LogManager.getLogger(AppStart.class.getName());
	private static final String MENU_PROPS_FILE = Constants.CONFIG_PATH+Constants.MENU_CONFIG_FILE;
	//private static final String REPORTING_PROPS_FILE = Constants.CONFIG_PATH+Constants.IVR_CONFIG_PROP_FILE;
	public void onStartApplication(ApplicationStartAPI appStartAPI) throws AudiumException
	{
		try 
		{
			File file = new File(new StringBuffer(Constants.CONFIG_PATH).append(Constants.LOG4J_XML_FILE).toString());
			context.setConfigLocation(file.toURI());
			logger.info(new StringBuffer(" onStartApplication-logger4J Config file loaded successfully from ").append(Constants.CONFIG_PATH).toString());
		} 
		catch (Exception fie) 
		{
			System.out.println(new StringBuffer("onStartApplication-logger4J Config file load error. Ex:").append(fie.getMessage()).toString());
		} 
		finally 
		{
			if(fin!=null)
			{
				try {
					fin.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				fin = null;
			}
		}
		logger.debug("********************* FARMERS_SO_CVP APP loggerGER ****************************");
		appDataBuilder=new AppDataBuilder(appStartAPI);
		// loading All IVRconfiguration properties files into Application data 
		appDataBuilder.loadAppPropertiesFile(Constants.CONFIG_PATH,Constants.IVR_CONFIG_PROP_FILE,Constants.APP_LEVEL_IVRCONFIG_PROP_OBJECT);
		appDataBuilder.setToAppData();
		// loading All IVRconfiguration properties files into Application data 
		//AppDataBuilder.loadAppPropertiesFile(Constants.CONFIG_PATH,Constants.IVR_CONFIG_PROP_FILE,Constants.APP_LEVEL_MENUCONFIG_PROP_OBJECT);
		appDataBuilder.loadAppPropertiesFile(Constants.CONFIG_PATH,Constants.MENU_SELECTION_CONFIG_PROP_FILE,Constants.APP_LEVEL_MENU_SELECTION_PROP_OBJECT);
		appStartAPI.setApplicationData(Constants.A_CONTEXT, context);
		System.setProperty("com.sun.net.ssl.checkRevocation","false");
		/** Load menus **/
		Properties menuProps = new Properties();
		Map<String, Map<String, Object>> menuDetailsMap = new HashMap<>();

		try {
			FileReader reader = new FileReader(MENU_PROPS_FILE);
			menuProps.load(reader);
			loadGlobalProperties(appStartAPI, menuProps);
			loadMenuProperties(appStartAPI, menuProps, menuDetailsMap);
			appStartAPI.setApplicationData("A_MENU_DETAILS", menuDetailsMap);
		} catch (Exception e) {
			logger.error("Error loading menu properties", e);
			throw new AudiumException("Error loading menu properties");
		}
		/**
		 * NLU Change
		 */
		
		loadNLUProperties(Constants.CONFIG_PATH+Constants.APPTAG_CONFIG_PROP_FILE, appStartAPI);
	
		/**
		 * NLU Change
		 */
		
		
		Properties reportProps = new Properties();
		HashMap<String, String> containableMap = new HashMap<String, String>();
		HashMap<String, String> containedMap = new HashMap<String, String>();
		HashMap<String, String> partiallyContainedeMap = new HashMap<String, String>();
		try {
			fin = new FileInputStream(new StringBuffer(Constants.CONFIG_PATH).append(Constants.IVR_CONFIG_PROP_FILE).toString());
			//FileReader reader = new FileReader(Constants.CONFIG_PATH + Constants.IVR_CONFIG_PROP_FILE);
			reportProps.load(fin);
			loadReportingProperties(reportProps, containableMap, containedMap, partiallyContainedeMap);
			appStartAPI.setApplicationData("CONTAINABLE_ELEMENT_MAP", containableMap);
			logger.info("Setting Containable Map into session :: " + containableMap);
			appStartAPI.setApplicationData("CONTAINED_ELEMENT_MAP", containedMap);
			logger.info("Setting Contained Map into session :: " + containedMap);
			appStartAPI.setApplicationData("PARTIALLY_CONTAINED_ELEMENT_MAP", partiallyContainedeMap);
			logger.info("Setting PartiallyContained Map into session :: " + partiallyContainedeMap);
		}
		catch (Exception e) {
			logger.error("Error loading Reporting properties", e);
			//throw new AudiumException("Error loading Reporting properties");
		}
	}

	private boolean isGlobalProperty(String key) {
		return key.contains("GLOBAL_");
	}

	private void loadGlobalProperties(ApplicationStartAPI appApi, Properties menuProps) throws AudiumException {
		try
		{
			String globalNi1Wav = menuProps.getProperty("GLOBAL_NI1_WAV");
			appApi.setApplicationData("A_GLOBAL_NI1_WAV", globalNi1Wav);
			logger.info("Loaded GLOBAL_NI1_WAV: " + globalNi1Wav);

			String globalNi2Wav = menuProps.getProperty("GLOBAL_NI2_WAV");
			appApi.setApplicationData("A_GLOBAL_NI2_WAV", globalNi2Wav);
			logger.info("Loaded GLOBAL_NI2_WAV: " + globalNi2Wav);

			String globalNm1Wav = menuProps.getProperty("GLOBAL_NM1_WAV");
			appApi.setApplicationData("A_GLOBAL_NM1_WAV", globalNm1Wav);
			logger.info("Loaded GLOBAL_NM1_WAV: " + globalNm1Wav);

			String globalNm2Wav = menuProps.getProperty("GLOBAL_NM2_WAV");
			appApi.setApplicationData("A_GLOBAL_NM2_WAV", globalNm2Wav);
			logger.info("Loaded GLOBAL_NM2_WAV: " + globalNm2Wav);

			String globalNIExWav = menuProps.getProperty("GLOBAL_NI_EX_WAV");
			appApi.setApplicationData("A_GLOBAL_NI_EX_WAV", globalNIExWav);
			logger.info("Loaded GLOBAL_NI_EX_WAV: " + globalNIExWav);
			
			String globalNMExWav = menuProps.getProperty("GLOBAL_NM_EX_WAV");
			appApi.setApplicationData("A_GLOBAL_NM_EX_WAV", globalNMExWav);
			logger.info("Loaded GLOBAL_NM_EX_WAV: " + globalNMExWav);

			String globalConfig = menuProps.getProperty("GLOBAL_CONFIG");
			appApi.setApplicationData("A_GLOBAL_CONFIG", globalConfig);
			logger.info("Loaded GLOBAL_CONFIG: " + globalConfig);

			String globalMaxCount = menuProps.getProperty("GLOBAL_MAX_COUNT");
			appApi.setApplicationData("A_GLOBAL_MAX_COUNT", globalMaxCount);
			logger.info("Loaded GLOBAL_MAX_COUNT: " + globalMaxCount);
		} catch (Exception e) {
			logger.error("Error loading menu properties", e);
			throw new AudiumException("Error loading menu properties");
		}
	}

	private void loadMenuProperties(ApplicationStartAPI appApi, Properties menuProps, Map<String, Map<String, Object>> menuDetailsMap) throws AudiumException {
		try {
			for (String menuId : menuProps.stringPropertyNames()) {
				Map<String, Object> menuDetails = new HashMap<>();
				if (!isGlobalProperty(menuId) && !menuId.contains("_DTMF") && !menuId.contains("_NI1_WAV") && !menuId.contains("_NI2_WAV") && !menuId.contains("_NM1_WAV") && !menuId.contains("_NM2_WAV") && !menuId.contains("_EX_WAV") && !menuId.contains("_MAX_COUNT") && !menuId.contains("_EVENT_NAME") && !menuId.contains("_Mode")) {
					logger.info("MenuID: " + menuId);
					List<String> initalPrompt = getMenuPrompt(menuProps, menuId, "");
					menuDetails.put("MenuID_InitialPrompt",initalPrompt);
					List<String> noInput1Prompt = getMenuPrompt(menuProps, menuId, "_NI1_WAV");
					menuDetails.put("MenuID_NoInputPrompt_1", noInput1Prompt);
					logger.info("MenuID_NoInputPrompt_1 : " + noInput1Prompt);
					List<String> noInput2Prompt = getMenuPrompt(menuProps, menuId, "_NI2_WAV");
					menuDetails.put("MenuID_NoInputPrompt_2", noInput2Prompt);
					logger.info("MenuID_NoInputPrompt_2 : " + noInput2Prompt);
					List<String> noMatch1Prompt = getMenuPrompt(menuProps, menuId, "_NM1_WAV");
					menuDetails.put("MenuID_NoMatchPrompt_1", noMatch1Prompt);
					logger.info("MenuID_NoMatchPrompt_1 : " + noMatch1Prompt);
					List<String> noMatch2Prompt = getMenuPrompt(menuProps, menuId, "_NM2_WAV");
					menuDetails.put("MenuID_NoMatchPrompt_2", noMatch2Prompt);
					logger.info("MenuID_NoMatchPrompt_2 : " + noMatch2Prompt);
					List<String> maxExceededPrompt = getMenuPrompt(menuProps, menuId, "_EX_WAV");
					menuDetails.put("MenuID_MaxTriesExceed", maxExceededPrompt);
					logger.info("MenuID_MaxTriesExceed : " + maxExceededPrompt);

					String eventName = menuProps.getProperty(menuId + "_EVENT_NAME");
					menuDetails.put("MenuID_EventName", eventName);
					logger.info("MenuID_EventName : " + eventName);
					String mode = menuProps.getProperty(menuId + "_Mode");
					menuDetails.put("MenuID_Mode", mode);
					logger.info("MenuID_mode : " + mode);
					String menuIdDTMFInfo = menuProps.getProperty(menuId + "_DTMF");
					menuDetails.put("MenuID_DTMF_Info", menuIdDTMFInfo);

					Map<String, Object> dtmfMap = loadDtmfOptions(menuProps, menuId);
					menuDetails.put("MenuID_DTMF_Info", dtmfMap);

					menuDetailsMap.put(menuId, menuDetails);
				}
			}
			logger.info("Loaded Menu Properties: " + menuDetailsMap);
		}catch (Exception e) {
			logger.error("Error loading menu properties", e);
			throw new AudiumException("Error loading menu properties");
		}
	}

	private Map<String, Object> loadDtmfOptions(Properties menuProps, String menuId) throws AudiumException {
		try {
			Map<String, Object> dtmfMap = new HashMap<>();
			String dtmfInfo = menuProps.getProperty(menuId + "_DTMF");
			if (dtmfInfo != null) {
				String[] dtmfOptions = dtmfInfo.split("\\|");
				for (String option : dtmfOptions) {
					String[] parts = option.split("-");
					if (parts.length == 2) {
						String dtmf = parts[0].trim();
						String optionText = parts[1].trim();
						dtmfMap.put(dtmf, optionText);
					}
				}
			}
			return dtmfMap;
		}catch (Exception e) {
			logger.error("Error loading menu properties", e);
			throw new AudiumException("Error loading menu properties");
		}
	}

	private List<String> getMenuPrompt(Properties menuProps, String menuId, String promptType) throws AudiumException {
		try {
			List<String> promptList = new ArrayList<>();
			String menuPromptKey = menuId + promptType;
			if (menuProps.containsKey(menuPromptKey)) {
				String menuPrompt = menuProps.getProperty(menuPromptKey);
				String[] prompts = menuPrompt.split("\\|");
				for (String prompt : prompts) {
					promptList.add(prompt);
				}
			} 
			return promptList;
		}catch (Exception e) {
			logger.error("Error loading menu properties", e);
			throw new AudiumException("Error loading menu properties");
		}
	}
	
	/**
	 * NLU Change
	 */
	private void loadNLUProperties(String fileName, ApplicationStartAPI appStartAPI) throws AudiumException{

		BufferedReader fileReader = null;
		String[] tokens = null;
		try {
			ArrayList<String> configMap = null;
			HashMap<String, ArrayList<String>> hashMap = null;
			HashMap<String, ArrayList<String>> hashMap2 = null;
			String line = Constants.EmptyString;
			fileReader = new BufferedReader(new FileReader(fileName));
			fileReader.readLine();

			hashMap = new HashMap<String, ArrayList<String>>();

			while ((line = fileReader.readLine()) != null) {
				tokens = line.split(Constants.COMMA);
				logger.info("App Tags - entries  : " + tokens.length +" for app tag " + tokens[1]);
				if (tokens.length > 0) {
					configMap = new ArrayList<String>();
					for (int i = 0; i < tokens.length; i++) {
						logger.info("Entries  : " + tokens[i]);
						configMap.add(tokens[i]);
					}
				}else{
					logger.info("No of columns are less than 0");
					appStartAPI.setApplicationData(Constants.A_MAP_APPTAG_CONFIG, Constants.NA);
					break;
				}
				hashMap.put(tokens[1]+Constants.COLON+tokens[2], configMap);
				logger.info("map key  : " + tokens[1]+Constants.COLON+tokens[2]);
			}
			logger.info("App Tag Config Map : " + hashMap);
			appStartAPI.setApplicationData(Constants.A_MAP_APPTAG_CONFIG, hashMap);

		}
		catch (Exception e) {
			logger.error("Error in CsvFileReader !!!"+e);
			appStartAPI.setApplicationData(Constants.A_MAP_APPTAG_CONFIG, Constants.NA);	
			throw new AudiumException("Error loading App Tag Configuration");
		} finally {
			try {
				fileReader.close();
			} catch (IOException e) {
				logger.info("Error while closing fileReader !!!");
				e.printStackTrace();
			}
		}
	}
	/**
	 * NLU Change
	 */
	
	private void loadReportingProperties(Properties reportProps, Map<String, String> containableMap, Map<String, String> containedMap, Map<String, String> partiallyContainedeMap) {
		
		String str1 = reportProps.getProperty("CONTAINABLE_ELEMENT_LIST");
		logger.info("Containable List from config file :: " + str1);
		String[] str1Split = Pattern.compile("\\|").split(str1);
		
		for (String str : str1Split) {
			logger.info("Iterating through each Pipe splitted element :: " + str);
			String[] strSplit = Pattern.compile("\\:").split(str);
			containableMap.put(strSplit[0], strSplit[1]);
			logger.info("Containable Hashmap value after inserting element :: " + containableMap);
		}
		logger.info("Loaded Containable map successfully :: " + containableMap);
		
		String str2 = reportProps.getProperty("CONTAINED_ELEMENT_LIST");
		logger.info("Contained List from config file :: " + str2);
		String[] str2Split = Pattern.compile("\\|").split(str2);
		
		for(String str : str2Split) {
			logger.info("Iterating through each Pipe splitted element :: " + str);
			String[] strSplit = Pattern.compile("\\:").split(str);
			containedMap.put(strSplit[0], strSplit[1]);
			logger.info("Contained Hashmap value after inserting element :: " + containedMap);
		}
		logger.info("Loaded Contained map successfully :: " + containedMap);
		
		String str3 = reportProps.getProperty("PARTIALLY_CONTAINED_ELEMENT_LIST");
		logger.info("PartiallyContained List from config file :: " + str3);
		String[] str3Split = Pattern.compile("\\|").split(str3);
		
		for(String str : str3Split) {
			logger.info("Iterating through each Pipe splitted element :: " + str);
			String[] strSplit = Pattern.compile("\\:").split(str);
			partiallyContainedeMap.put(strSplit[0], strSplit[1]);
			logger.info("PartiallyContained Hashmap value after inserting element :: " + partiallyContainedeMap);
		}
		logger.info("Loaded Partially Contained map successfully :: " + partiallyContainedeMap);
	}
}