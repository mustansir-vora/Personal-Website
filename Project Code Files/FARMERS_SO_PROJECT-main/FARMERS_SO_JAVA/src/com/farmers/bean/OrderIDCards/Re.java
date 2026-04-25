package com.farmers.bean.OrderIDCards;

import java.util.ArrayList;
import java.util.List;

public class Re {
	private Integer tenantid;
    private String key;
    private List<State> state = new ArrayList<State>();
    private String lob;
    private String category;
    private String brandlabel;
    private String newquotenotallowed;
    private Object newquotenotallowedaudio;
    private String mail;
    private String fax;
    private String email;
    private String achpaymentallowed;
    public Integer getTenantid() {
        return tenantid;
    }
    public void setTenantid(Integer tenantid) {
        this.tenantid = tenantid;
    }
    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public List<State> getState() {
        return state;
    }
    public void setState(List<State> state) {
        this.state = state;
    }
    public String getLob() {
        return lob;
    }
    public void setLob(String lob) {
        this.lob = lob;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public String getBrandlabel() {
        return brandlabel;
    }
    public void setBrandlabel(String brandlabel) {
        this.brandlabel = brandlabel;
    }
    public String getNewquotenotallowed() {
        return newquotenotallowed;
    }
    public void setNewquotenotallowed(String newquotenotallowed) {
        this.newquotenotallowed = newquotenotallowed;
    }
    public Object getNewquotenotallowedaudio() {
        return newquotenotallowedaudio;
    }
    public void setNewquotenotallowedaudio(Object newquotenotallowedaudio) {
        this.newquotenotallowedaudio = newquotenotallowedaudio;
    }
    public String getMail() {
        return mail;
    }
    public void setMail(String mail) {
        this.mail = mail;
    }
    public String getFax() {
        return fax;
    }
    public void setFax(String fax) {
        this.fax = fax;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getAchpaymentallowed() {
        return achpaymentallowed;
    }
    public void setAchpaymentallowed(String achpaymentallowed) {
        this.achpaymentallowed = achpaymentallowed;
    }
	@Override
	public String toString() {
		return "Re [tenantid=" + tenantid + ", key=" + key + ", lob=" + lob + ", category=" + category + ", brandlabel="
				+ brandlabel + ", newquotenotallowed=" + newquotenotallowed + ", newquotenotallowedaudio="
				+ newquotenotallowedaudio + ", mail=" + mail + ", fax=" + fax + ", email=" + email
				+ ", achpaymentallowed=" + achpaymentallowed + ", getTenantid()=" + getTenantid() + ", getKey()="
				+ getKey() + ", getLob()=" + getLob() + ", getCategory()=" + getCategory() + ", getBrandlabel()="
				+ getBrandlabel() + ", getNewquotenotallowed()=" + getNewquotenotallowed()
				+ ", getNewquotenotallowedaudio()=" + getNewquotenotallowedaudio() + ", getMail()=" + getMail()
				+ ", getFax()=" + getFax() + ", getEmail()=" + getEmail() + ", getAchpaymentallowed()="
				+ getAchpaymentallowed() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}
    
    
    
    
}
