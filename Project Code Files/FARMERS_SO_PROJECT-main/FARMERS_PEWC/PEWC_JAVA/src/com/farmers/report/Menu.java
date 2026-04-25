package com.farmers.report;

import java.io.Serializable;
import java.util.Calendar;

public class Menu implements Serializable{
	private static final long serialVersionUID = 923456788;

	private int sequenceNum;
	private String strMenuId = "";
	private Calendar calMenuDateTime = null;
	private String strMenuOption = "";
	private String calMenuStartTime = null;
	private String calMenuEndTime = null;
	private String strMenuCode = null;
	
	public Menu getMenu()
	{
		return this;
	}
	
	public int getSequenceNum() {
		return sequenceNum;
	}

	public void setSequenceNum(int sequenceNum) {
		this.sequenceNum = sequenceNum;
	}
	
	public void setCalMenuStartTime(String calMenuStartTime) {
		this.calMenuStartTime = calMenuStartTime;
	}
	
	public void setCalMenuEndTime(String calMenuEndTime) {
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

	public String getCalMenuEndTime() {
		return calMenuEndTime;
	}
	
	public String getCalMenuStartTime() {
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
