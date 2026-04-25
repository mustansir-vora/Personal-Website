package com.farmers.report;

import java.io.Serializable;

public class Announcement implements Serializable{
	private static final long serialVersionUID = 923456788;

	private int sequenceNum;
	private String strAnnouncementId = "";
	//private String calAnnouncementDateTime = null;
	//CS1263594 - Add tenth, hundred, thousands - Arshath
	private String calAnnouncementStartTime = null;
	private String calAnnouncementEndTime = null;
	
	public Announcement getAnnouncement()
	{
		return this;
	}
	
	public int getSequenceNum() {
		return sequenceNum;
	}
	public void setSequenceNum(int sequenceNum) {
		this.sequenceNum = sequenceNum;
	}
	public String getStrAnnouncementId() {
		return strAnnouncementId;
	}
	public void setStrAnnouncementId(String strAnnouncementId) {
		this.strAnnouncementId = strAnnouncementId;
	}
//	public String getCalAnnouncementDateTime() {
//		return calAnnouncementDateTime;
//	}
//	public void setCalAnnouncementDateTime(String calAnnouncementDateTime) {
//		this.calAnnouncementDateTime = calAnnouncementDateTime;
//	}
	//CS1263594 - Add tenth, hundred, thousands - Arshath -Start
	public String getCalAnnouncementStartTime() {
		return calAnnouncementStartTime;
	}
	public void setCalAnnouncementStartTime(String calAnnouncementStartTime) {
		this.calAnnouncementStartTime = calAnnouncementStartTime;
	}
	public String getCalAnnouncementEndTime() {
		return calAnnouncementEndTime;
	}
	public void setCalAnnouncementEndTime(String calAnnouncementEndTime) {
		this.calAnnouncementEndTime = calAnnouncementEndTime;
	}
	//CS1263594 - Add tenth, hundred, thousands - Arshath -End
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
	
}
