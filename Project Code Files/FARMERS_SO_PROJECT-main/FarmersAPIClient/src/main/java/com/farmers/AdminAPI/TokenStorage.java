package com.farmers.AdminAPI;

import java.util.HashMap;

import org.apache.logging.log4j.Logger;

import com.farmers.bean.TokenBean;

public class TokenStorage {
	
	//private static final Logger logger = Log4jLoading.context.getLogger(Constants.LOGGER_NAME);

	private static TokenStorage tokenStorage = null;
	private static HashMap<String, TokenBean> tokens = null;
	private TokenStorage () {
		
	}
	
	public static TokenStorage getInstance() {
		if(tokenStorage == null) {
			tokenStorage = new TokenStorage();
			tokens = new HashMap<String, TokenBean>();
		}
		return tokenStorage;
	}
	
	public TokenBean getToken(String token, Logger logger, String callid) {
		logger.info(callid+" : Tokens Hashmap : "+tokens.toString());	
		return tokens.get(token);
	}
	
	
	public synchronized void putToken(String token, TokenBean tokenBean, Logger logger, String callid){
		tokens.put(token, tokenBean);
		logger.info(callid+" : Token added/updated for scope : " + token + " *** Tokens Hashmap : " + tokens.toString());
	}

}
