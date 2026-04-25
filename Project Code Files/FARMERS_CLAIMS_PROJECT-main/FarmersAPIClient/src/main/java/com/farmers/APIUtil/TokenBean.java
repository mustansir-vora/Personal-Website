package com.farmers.APIUtil;

import java.util.Date;

public class TokenBean {
	
	private String token;
	private Date tokenCreated;

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