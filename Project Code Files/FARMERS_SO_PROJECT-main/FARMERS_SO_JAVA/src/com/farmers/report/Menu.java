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
	
	//START - CS1240948 :: Add getters & Setters for Nomatch, NoInput & combined tries counts
	private String strNo_Match = null;
	private String strNo_Input = null;
	private String strCombined_Tries = null;
	//END - CS1240948 :: Add getters & Setters for Nomatch, NoInput & combined tries counts
	private String strHoteventRetry=null;
	
	private String strNluMenuUtterance = null;
	
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
	
	//START - CS1240948 :: Add getters & Setters for Nomatch, NoInput & combined tries counts
		public void setStrNo_Match(String strNo_Match) {
			this.strNo_Match = strNo_Match;
		}
		
		public void setStrNo_Input(String strNo_Input) {
			this.strNo_Input = strNo_Input;
		}
		
		public void setStrCombined_Tries(String strCombined_Tries) {
			this.strCombined_Tries = strCombined_Tries;
		}
		//END - CS1240948 :: Add getters & Setters for Nomatch, NoInput & combined tries counts
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
	//START - CS1240948 :: Add getters & Setters for Nomatch, NoInput & combined tries counts
	public String getstrNo_Match() {
		return strNo_Match;
	}
	
	public String getstrNo_Input() {
		return strNo_Input;
	}
	
	public String getstrCombined_Tries() {
		return strCombined_Tries;
	}
	//END - CS1240948 :: Add getters & Setters for Nomatch, NoInput & combined tries counts
    
	////START:GDF Connectivity Issue retries changes
	public String getStrHoteventRetry() {
		return strHoteventRetry;
	}

	public void setStrHoteventRetry(String strHoteventRetry) {
		this.strHoteventRetry = strHoteventRetry;
	}
	//END:GDF Connectivity Issue retries changes

	//START : All Bu's NLU User Utterances
	public String getStrNluMenuUtterance() {
		return strNluMenuUtterance;
	}

	public void setStrNluMenuUtterance(String strNluMenuUtterance) {
		this.strNluMenuUtterance = strNluMenuUtterance;
	}
	//END : All Bu's NLU User Utterances
}
