package com.farmers.util;

import java.io.Serializable;
import java.util.Calendar;

public class Menu implements Serializable{
	private static final long serialVersionUID = 923456788;

	private String strMenuId = "";
	private Calendar calMenuDateTime = null;
	private String strMenuOption = "";
	private Calendar calMenuStartTime = null;
	private Calendar calMenuEndTime = null;
	private String strMenuCode = null;
	public Menu getMenu()
	{
		return this;
	}
	
	public void setCalMenuStartTime(Calendar calMenuStartTime) {
		this.calMenuStartTime = calMenuStartTime;
	}
	
	public void setCalMenuEndTime(Calendar calMenuEndTime) {
		this.calMenuEndTime = calMenuEndTime;
	}
	
	
	public void setCalMenuDateTime(Calendar calMenuDateTime) {
		this.calMenuDateTime = calMenuDateTime;
	}
	
	
	public void setStrMenuOption(String strMenuOption) {
		this.strMenuOption = strMenuOption;
	}
	public Calendar getCalMenuDateTime() {
		return calMenuDateTime;
	}

	public String getStrMenuOption() {
		return strMenuOption;
	}

	public Calendar getCalMenuEndTime() {
		return calMenuEndTime;
	}
	
	public Calendar getCalMenuStartTime() {
		return calMenuStartTime;
	}

	public String getStrMenuId() {
		return strMenuId;
	}

	public void setStrMenuId(String strMenuId) {
		this.strMenuId = strMenuId;
	}

	public String getStrMenuCode() {
		return strMenuCode;
	}

	public void setStrMenuCode(String strMenuCode) {
		this.strMenuCode = strMenuCode;
	}
	
}
