package com.farmers.report;

import java.io.Serializable;

public class HostBean implements Serializable{
	private static final long serialVersionUID = 923456788;

	private int sequenceNum;
	private String strHostID="";
	private String strHostMethod="";
	private String strHostType="";
	private String strHostInParams="";
	private String strHostOutParams="";
	private String strHostResponse="";
	private String strHostAccStartTime="";
	private String strHostAccEndTime="";
	private String strHostErrCode="";
	private String strHostErrDesc="";
	private String strHostRegion = "";
	private String strHostEndpoint = "";
	private String strHostString ="";

	public int getSequenceNum() {
		return sequenceNum;
	}

	public void setSequenceNum(int sequenceNum) {
		this.sequenceNum = sequenceNum;
	}

	public void setHostID(String strHostID) {
		this.strHostID = strHostID;
	}
	
	public void setHostMethod(String strHostMethod) {
		this.strHostMethod = strHostMethod;
	}
	
	public void setHostType(String strHostType) {
		this.strHostType = strHostType;
	}

	public void setHostResponse(String strHostResponse) {
		this.strHostResponse = strHostResponse;
	}
	
	
	public void setHostInParams(String strHostInParams) {
		this.strHostInParams = strHostInParams;
	}
	
	public void setHostOutParams(String strHostOutParams) {
		this.strHostOutParams = strHostOutParams;
	}
	
	public void setHostAccStartTime(String strHostAccStartTime) {
		this.strHostAccStartTime = strHostAccStartTime;
	}
	
	public void setHostAccEndTime(String strHostAccEndTime) {
		this.strHostAccEndTime = strHostAccEndTime;
	}
	
	public void setHostErrCode(String strHostErrCode) {
		this.strHostErrCode = strHostErrCode;
	}
	
	public void setHostErrDesc(String strHostErrDesc) {
		this.strHostErrDesc = strHostErrDesc;
	}
	
	public void setHostRegion(String region) {
		this.strHostRegion = region;
	}
	
	public void setHostEndpoint(String endpoint) {
		this.strHostEndpoint = endpoint;
	}
	
	public String getHostID() {
		return strHostID;
	}
			
	public String getHostType(){
		return strHostType;
	}
	
	public String getHostResponse()	{
		return strHostResponse;
	}
	
	public String getHostMethod()	{
		return strHostMethod;
	}
	
	public String getHostInParams()	{
		return strHostInParams;
	}
	
	public String getHostOutParams()	{
		return strHostOutParams;
	}
	
	public String getHostAccStartTime()	{
		return strHostAccStartTime;
	}
	
	public String getHostAccEndTime()	{
		return  strHostAccEndTime;
	}
	
	public String getHostErrCode()	{
		return strHostErrCode;
	}
	
	public String getHostErrDesc()	{
		return strHostErrDesc;
	}
	
	public String getHostRegion()	{
		return strHostRegion;
	}
	
	public String getHostEndpoint()	{
		return strHostEndpoint;
	}
	public String getStrHostString() {
		return strHostString;
	}

	public void setStrHostString(String strHostString) {
		this.strHostString = strHostString;
	}

	@Override
	public String toString() {
		return "HostBean [sequenceNum=" + sequenceNum + ", strHostID=" + strHostID + ", strHostMethod=" + strHostMethod
				+ ", strHostType=" + strHostType + ", strHostInParams=" + strHostInParams + ", strHostOutParams="
				+ strHostOutParams + ", strHostResponse=" + strHostResponse + ", strHostAccStartTime="
				+ strHostAccStartTime + ", strHostAccEndTime=" + strHostAccEndTime + ", strHostErrCode="
				+ strHostErrCode + ", strHostErrDesc=" + strHostErrDesc + ", strHostRegion=" + strHostRegion + ", strhostEndpoint=" + strHostEndpoint + ", strHostString=" + strHostString + "]";
	}

	
	
	
	
	
}
