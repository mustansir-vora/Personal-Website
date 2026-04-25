package com.farmers.adminHost;

public class RoutingBean {
	
	private String ccName;
	private String ccAllocation;
	private String destinationNum;
	private Object priority;
	private String screenPopUp;
	
	
	public String getCcName() {
		return ccName;
	}
	public void setCcName(String ccName) {
		this.ccName = ccName;
	}
	public String getCcAllocation() {
		return ccAllocation;
	}
	public void setCcAllocation(String ccAllocation) {
		this.ccAllocation = ccAllocation;
	}
	public String getDestinationNum() {
		return destinationNum;
	}
	public void setDestinationNum(String destinationNum) {
		this.destinationNum = destinationNum;
	}
	
	public Object getPriority() {
		return priority;
	}
	public void setPriority(Object priority) {
		this.priority = priority;
	}
	
	public String getScreenPopUp() {
		return screenPopUp;
	}
	public void setScreenPopUp(String screenPopUp) {
		this.screenPopUp = screenPopUp;
	}
	@Override
	public String toString() {
		return "RoutingBean [ccName=" + ccName + ", ccAllocation=" + ccAllocation + ", destinationNum=" + destinationNum
				+ ", priority=" + priority + ", screenPopUp=" + screenPopUp + "]";
	}
	
	
}
