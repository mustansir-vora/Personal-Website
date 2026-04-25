package com.farmers.report;

import java.io.Serializable;

public class Announcement implements Serializable{
	private static final long serialVersionUID = 923456788;

	private int sequenceNum;
	private String strAnnouncementId = "";
	private String calAnnouncementDateTime = null;
	
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
	public String getCalAnnouncementDateTime() {
		return calAnnouncementDateTime;
	}
	public void setCalAnnouncementDateTime(String calAnnouncementDateTime) {
		this.calAnnouncementDateTime = calAnnouncementDateTime;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
	
}
