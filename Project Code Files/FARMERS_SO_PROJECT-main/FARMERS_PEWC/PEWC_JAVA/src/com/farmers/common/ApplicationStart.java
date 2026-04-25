package com.farmers.common;





import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import com.audium.server.AudiumException;
import com.audium.server.global.ApplicationStartAPI;
import com.audium.server.logger.events.EventException;
import com.audium.server.proxy.StartApplicationInterface;
import com.farmers.util.Constants;
import com.farmers.util.GlobalsCommon;

public class ApplicationStart implements StartApplicationInterface{

	private static Logger logger = LogManager.getLogger(ApplicationStart.class.getName());
    private static final LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);

	
	@Override
	public void onStartApplication(ApplicationStartAPI appStartAPI)
			throws AudiumException {
		try {
			File file = new File(Constants.LOG4J_CONFIG_PATH);
            context.setConfigLocation(file.toURI());
            appStartAPI.setApplicationData("LOGGER", context);
            
			logger.info("Entered into onStartApplication method");
			
			Properties prop =  readConfigProperties();
			if(prop != null) {
				appStartAPI.setApplicationData(Constants.A_IVR_PROP, prop);
				setToAppData(appStartAPI, prop);
			}
		} catch (Exception e) {
			logger.error(GlobalsCommon.getExceptionTrace(e));
		}
	}
	
	
	public Properties readConfigProperties() throws EventException {
		Properties prop = new Properties();
		try {
			FileInputStream fin = new FileInputStream(Constants.IVR_CONFIG_PATH);
			try {				
				prop.load(fin);
				logger.debug("VRM MAPPING PROPERTIES LOADED SUCCESSFULLY FROM : "+Constants.IVR_CONFIG_PATH);
			} catch (Exception fie) {
				throw new EventException("onStartApplication: LOGGER Config file load error. Ex:" + fie); 
			} finally {
				if(fin!=null){
					fin.close();
					fin = null;
				}
			}
		} catch (FileNotFoundException fle) {
			throw new EventException("FileNotFoundException occured in readVRMProperties method while reading VRM MAPPING file. Ex:" + fle); 
		} catch (IOException e) {
			throw new EventException("IOException occured in readVRMProperties method while reading reading VRM MAPPING file. Ex:" + e); 
		}
		return prop;
	}
	
	/**
	 * Method to read from properties 
	 * Get the defaultValue in case of properties value is empty or null
	 * Set to Application Data
	 */
	public void setToAppData(ApplicationStartAPI appStartAPI, Properties properties) throws EventException 
	{
		logger.info("setToAppData");
		Enumeration propertyNames =properties.propertyNames(); 
		StringBuilder sb = new StringBuilder();
		String strKey ="";
		String strValue="";
		try{
			while (propertyNames.hasMoreElements()) {
				strKey = ((String) propertyNames.nextElement()).trim();
				strValue = properties.getProperty(strKey).trim();
				if(strKey.startsWith(Constants.AppVarNameStartwith))
				{
					appStartAPI.setApplicationData(strKey,strValue);
					logger.info(strKey+"-"+strValue);
				}
			}
		}

		catch(Exception e){
			logger.error(GlobalsCommon.getExceptionTrace(e));
		}
	}
	
}
