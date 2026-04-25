package com.farmers.start;

import com.audium.server.AudiumException;
import com.audium.server.global.ApplicationStartAPI;
import com.audium.server.proxy.StartApplicationInterface;
import com.farmers.util.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

public class AppStart implements StartApplicationInterface{
	
	private static final LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);
	private static Logger logger = LogManager.getLogger(AppStart.class.getName());
	private FileInputStream fin;
	private Properties properties = null;
	
	public void onStartApplication(ApplicationStartAPI appStartAPI) throws AudiumException {

		try 
		{
			File file = new File(new StringBuffer(Constants.CONFIG_PATH).append(Constants.LOG4J_XML_FILE).toString());
			context.setConfigLocation(file.toURI());
			logger.info(new StringBuffer(" onStartApplication-logger4J Config file loaded successfully from ").append(Constants.CONFIG_PATH).toString());
			
			appStartAPI.setApplicationData(Constants.A_CONTEXT, context);
			loadAppProperties(Constants.CONFIG_PATH,Constants.IVR_CONFIG_PROP_FILE,Constants.APP_LEVEL_IVRCONFIG_PROP_OBJECT, appStartAPI);
		} 
		catch (Exception e) 
		{
			logger.info(new StringBuffer("onStartApplication - Exception :: ").append(e));
			e.printStackTrace();
		} 
	}
	
	//Set Data starting with _A in conifg into App Data
	public void loadAppProperties(String strConfigPath,String configFile,String appLevelSessionVarName, ApplicationStartAPI appStartAPI) {
		String strKey = Constants.EmptyString;
		String strValue = Constants.EmptyString;
		StringBuilder sb = new StringBuilder();
		
		try {
			fin = new FileInputStream(new StringBuffer(strConfigPath).append(configFile).toString());
			properties=new Properties();
			properties.load(fin);
			
			appStartAPI.setApplicationData(appLevelSessionVarName,properties);
			
			//Start - Set data starting with _A in config file into App data
			Enumeration propertyNames =properties.propertyNames();
			while (propertyNames.hasMoreElements()) {
				strKey = ((String) propertyNames.nextElement()).trim();
				strValue = properties.getProperty(strKey).trim();
				
				if(strKey.startsWith(Constants.AppVarNameStartwith)){
					appStartAPI.setApplicationData(strKey,strValue);
					sb.append("strkey --> ").append(strKey).append(" strValue --> ").append(strValue).append(" | ") ;
				}
			}
			logger.info(new StringBuffer("PROPERTIES SET TO APPLICATION = ") + sb.toString());
		} catch (Exception e) {
			logger.info(new StringBuffer("onStartApplication: ").append(configFile).append(" Config file load error.").append(" Error :: " + e).toString());
			e.printStackTrace();
		}
	}
}
