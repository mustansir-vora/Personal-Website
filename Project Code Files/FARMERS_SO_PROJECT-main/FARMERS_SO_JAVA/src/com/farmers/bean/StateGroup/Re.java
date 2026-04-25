package com.farmers.bean.StateGroup;

import java.util.ArrayList;

public class Re {
	public int tenantid;
    public String description;
    public int id;
    public ArrayList<State> state;
    public String key;
	public int getTenantid() {
		return tenantid;
	}
	public void setTenantid(int tenantid) {
		this.tenantid = tenantid;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public ArrayList<State> getState() {
		return state;
	}
	public void setState(ArrayList<State> state) {
		this.state = state;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	@Override
	public String toString() {
		return "Re [tenantid=" + tenantid + ", description=" + description + ", id=" + id + ", state=" + state
				+ ", key=" + key + ", getTenantid()=" + getTenantid() + ", getDescription()=" + getDescription()
				+ ", getId()=" + getId() + ", getState()=" + getState() + ", getKey()=" + getKey() + ", getClass()="
				+ getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}
    
    
    
    
}
