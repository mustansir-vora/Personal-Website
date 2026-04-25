package com.farmers.start;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.audium.server.global.ApplicationStartAPI;
import com.farmers.util.CommonAPIAccess;
import com.farmers.util.Constants;


/**
 * The SessionBuilder Class is called CallStart method,
 * Read all the Properties from ivrconfig.properties
 * and set into Session ;
 */

public class AppDataBuilder {

	CommonAPIAccess caa;
	private FileInputStream fin;
	private Properties properties = null;
	private HashMap<String, String> defaultValueMap = new HashMap<String, String>();
	private ArrayList<String> nonZeroList = new ArrayList<String>();
	ApplicationStartAPI  appStartAPI = null;
	private static final Logger log = LogManager.getLogger(AppDataBuilder.class);
	
	public AppDataBuilder(ApplicationStartAPI pAppStartAPI){
		caa=CommonAPIAccess.getInstance(pAppStartAPI);
		this.appStartAPI = pAppStartAPI;
	}
	
	public void loadAppPropertiesFile(String strConfigPath,String configFile,String appLevelSessionVarName)
	{

		
		try{
				fin = new FileInputStream(new StringBuffer(strConfigPath).append(configFile).toString());
				properties=new Properties();
			try{				
				properties.load(fin);
				//PropertyConfigurator.configure(properties);
				appStartAPI.setApplicationData(appLevelSessionVarName,properties);
				log.info(new StringBuffer("onStartApplication: ").append(configFile).append(" Config file loaded successfully.").toString());
			} catch (Exception e) {
				log.debug(new StringBuffer("onStartApplication: ").append(configFile).append(" Config file load error.").toString());
				caa.printStackTrace(e);
			} finally {
				if(fin!=null){
					fin.close();
					fin = null;
				}
			}
		} catch (FileNotFoundException e) {
			log.debug(new StringBuffer("onStartApplication: ").append(configFile).append(" Config file load error.").toString());
			caa.printStackTrace(e);
		} catch (IOException e) {
			log.debug(new StringBuffer("onStartApplication: ").append(configFile).append(" Config file load error.").toString());
			caa.printStackTrace(e);
		}		
	}
	
	/**
	 * Method to read from properties 
	 * Get the defaultValue in case of properties value is empty or null
	 * Set to Application Data
	 */
	public void setToAppData()
	{
		Enumeration propertyNames =properties.propertyNames();  
		buildDefaultValueMap();
		StringBuilder sb = new StringBuilder();
		String strKey ="";
		String strValue="";
		try{
			while (propertyNames.hasMoreElements()) {
				strKey = ((String) propertyNames.nextElement()).trim();
				strValue = properties.getProperty(strKey).trim();
				if(strKey.startsWith(Constants.AppVarNameStartwith))
				{
					if(strValue==null || strValue.equals("")||(nonZeroList.contains(strKey) && strValue.equals("0")))
					{
						strValue = getDefaultValue(strKey);
					}
					appStartAPI.setApplicationData(strKey,strValue);
					sb.append("strValue-->").append(strValue).append(" strKey-->").append(strKey).append('|') ;   
					
				}
			}
			log.info(new StringBuffer(" PROPERTIES SET TO APPLICATION=")+sb.toString());
		}

		catch(Exception e){
			caa.printStackTrace(e);
		}finally{
			defaultValueMap = null;
			nonZeroList = null;
			propertyNames = null;
			properties=null;
		}
	}
	
	// Build the DefaultValues for properties
	/* Add to Map if any other Properties have defaultValue*/
	public void buildDefaultValueMap()
	{
			
		defaultValueMap.put("A_MEDIA_URL","/CVP/audio/FARMERS_SO_CVP");
		defaultValueMap.put("A_MEDIASERVER_PORT","80");
	}


	// Get the default value from Map
	public String getDefaultValue(String strKey)
	{
		String strDefaultValue = "";		
		if(defaultValueMap.containsKey(strKey))
		{
			strDefaultValue = defaultValueMap.get(strKey);
		}
		return strDefaultValue;
	}

}



