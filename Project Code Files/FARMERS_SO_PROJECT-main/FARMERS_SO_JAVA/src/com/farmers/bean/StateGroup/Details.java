package com.farmers.bean.StateGroup;

import java.util.ArrayList;

public class Details {
    public int id;
    public int tenantid;
    public String key;
    public ArrayList<State> state;
    public String description;
    
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public int getTenantid() {
		return tenantid;
	}


	public void setTenantid(int tenantid) {
		this.tenantid = tenantid;
	}


	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}





	public ArrayList<State> getState() {
		return state;
	}





	public void setState(ArrayList<State> state) {
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
		return "Details [id=" + id + ", tenantid=" + tenantid + ", key=" + key + ", state=" + state + ", description="
				+ description + ", getId()=" + getId() + ", getTenantid()=" + getTenantid() + ", getKey()=" + getKey()
				+ ", getState()=" + getState() + ", getDescription()=" + getDescription() + ", getClass()=" + getClass()
				+ ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}
    
}
