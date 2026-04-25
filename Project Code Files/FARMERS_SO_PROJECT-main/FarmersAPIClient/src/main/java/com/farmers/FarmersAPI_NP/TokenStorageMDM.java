package com.farmers.FarmersAPI_NP;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.farmers.bean.TokenBean;

public class TokenStorageMDM {
	
	//private Logger logger = LogManager.getLogger(Constants.LOGGER_NAME);

	private static TokenStorageMDM tokenStorage = null;
	private static HashMap<String, TokenBean> tokens = null;
	private TokenStorageMDM () {
		
	}
	
	public static TokenStorageMDM getInstance() {
		if(tokenStorage == null) {
			tokenStorage = new TokenStorageMDM();
			tokens = new HashMap<String, TokenBean>();
		}
		return tokenStorage;
	}
	
	public TokenBean getToken(String tokenScope, Logger logger, String tid) {
		logger.info(tid+" : Tokens Hashmap : "+tokens.toString());	
		return tokens.get(tokenScope);
	}
	
	
	public synchronized void putToken(String tokenScope, TokenBean tokenBean, Logger logger, String tid){
		tokens.put(tokenScope, tokenBean);
		logger.info(tid+" : Token added/updated for scope : " + tokenScope + " *** Tokens Hashmap : " + tokens.toString());
	}
}
