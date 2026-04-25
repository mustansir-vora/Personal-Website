package com.farmers.bean;

import java.util.Date;

public class TokenBean {
	
	private String token;
	private Date tokenCreated;

	/*
	 * private TokenBean() {
	 * 
	 * }
	 */
	
	public String getToken() {
		return token;
	}
	public Date getTokenCreated() {
		return tokenCreated;
	}
	
	public TokenBean(String token, Date tokenCreated) {
		this.token = token;
		this.tokenCreated = tokenCreated;
	}
	@Override
	public String toString() {
		return "TokenBean [token=" + token + ", tokenCreated=" + tokenCreated + "]";
	}
	
	
}