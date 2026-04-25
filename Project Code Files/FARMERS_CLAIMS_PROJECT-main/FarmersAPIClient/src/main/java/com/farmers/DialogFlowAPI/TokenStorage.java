package com.farmers.DialogFlowAPI;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.farmers.APIUtil.TokenBean;

public class TokenStorage {
	
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
	
	public TokenBean getTokenBean(String tokenScope, Logger logger, String tid) {
		logger.info(tid+" : Tokens Hashmap : "+tokens.toString());	
		return tokens.get(tokenScope);
	}
	
	public synchronized void putToken(String tokenScope, TokenBean tokenBean, Logger logger, String tid){
		tokens.put(tokenScope, tokenBean);
		logger.info(tid+" : Token added/updated for scope : " + tokenScope + " *** Tokens Hashmap : " + tokens.toString());
	}
}
