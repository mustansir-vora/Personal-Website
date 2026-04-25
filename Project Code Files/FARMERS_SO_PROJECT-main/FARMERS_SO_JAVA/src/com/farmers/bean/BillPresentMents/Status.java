package com.farmers.bean.BillPresentMents;

public class Status {
	private String state;
    private String description;
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	@Override
	public String toString() {
		return "Status [state=" + state + ", description=" + description + "]";
	}
    
   
    
    
    
}
