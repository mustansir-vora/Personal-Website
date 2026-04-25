package com.farmers.util;

import java.io.Serializable;

public class HostBean implements Serializable {
	private static final long serialVersionUID = 923456788;

	private String strHostID = "";
	private String strHostMethod = "";
	private String strHostType = "";
	private String strHostInParams = "";
	private String strHostOutParams = "";
	private String strHostResponse = "";
	private String strHostAccStartTime = "";
	private String strHostAccEndTime = "";
	private String strHostErrCode = "";
	private String strHostErrDesc = "";

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

	public String getHostID() {
		return strHostID;
	}

	public String getHostType() {
		return strHostType;
	}

	public String getHostResponse() {
		return strHostResponse;
	}

	public String getHostMethod() {
		return strHostMethod;
	}

	public String getHostInParams() {
		return strHostInParams;
	}

	public String getHostOutParams() {
		return strHostOutParams;
	}

	public String getHostAccStartTime() {
		return strHostAccStartTime;
	}

	public String getHostAccEndTime() {
		return strHostAccEndTime;
	}

	public String getHostErrCode() {
		return strHostErrCode;
	}

	public String getHostErrDesc() {
		return strHostErrDesc;
	}

}
