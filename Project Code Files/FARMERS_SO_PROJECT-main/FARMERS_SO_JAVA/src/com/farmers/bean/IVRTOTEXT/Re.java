package com.farmers.bean.IVRTOTEXT;

import java.util.ArrayList;
import java.util.List;

public class Re {
	  	private String key;
	    private String intent;
	    private String businessunit;
	    private String allocation;
	    private String isdisable;
	    private String overrideintent;
	    private String downtimeschedule;
	    private List<Downtimeschedulewindow> downtimeschedulewindow = new ArrayList<Downtimeschedulewindow>();
	    private Object downtimescheduleexceptiondayoverrides;
	    public String getKey() {
	        return key;
	    }
	    public void setKey(String key) {
	        this.key = key;
	    }
	    public String getIntent() {
	        return intent;
	    }
	    public void setIntent(String intent) {
	        this.intent = intent;
	    }
	    public String getBusinessunit() {
	        return businessunit;
	    }
	    public void setBusinessunit(String businessunit) {
	        this.businessunit = businessunit;
	    }
	    public String getAllocation() {
	        return allocation;
	    }
	    public void setAllocation(String allocation) {
	        this.allocation = allocation;
	    }
	    public String getIsdisable() {
	        return isdisable;
	    }
	    public void setIsdisable(String isdisable) {
	        this.isdisable = isdisable;
	    }
	    public String getOverrideintent() {
	        return overrideintent;
	    }
	    public void setOverrideintent(String overrideintent) {
	        this.overrideintent = overrideintent;
	    }
	    public String getDowntimeschedule() {
	        return downtimeschedule;
	    }
	    public void setDowntimeschedule(String downtimeschedule) {
	        this.downtimeschedule = downtimeschedule;
	    }
	    public List<Downtimeschedulewindow> getDowntimeschedulewindow() {
	        return downtimeschedulewindow;
	    }
	    public void setDowntimeschedulewindow(List<Downtimeschedulewindow> downtimeschedulewindow) {
	        this.downtimeschedulewindow = downtimeschedulewindow;
	    }
	    public Object getDowntimescheduleexceptiondayoverrides() {
	        return downtimescheduleexceptiondayoverrides;
	    }
	    public void setDowntimescheduleexceptiondayoverrides(Object downtimescheduleexceptiondayoverrides) {
	        this.downtimescheduleexceptiondayoverrides = downtimescheduleexceptiondayoverrides;
	    }
		@Override
		public String toString() {
			return "Re [key=" + key + ", intent=" + intent + ", businessunit=" + businessunit + ", allocation="
					+ allocation + ", isdisable=" + isdisable + ", overrideintent=" + overrideintent
					+ ", downtimeschedule=" + downtimeschedule + ", downtimescheduleexceptiondayoverrides="
					+ downtimescheduleexceptiondayoverrides + ", getKey()=" + getKey() + ", getIntent()=" + getIntent()
					+ ", getBusinessunit()=" + getBusinessunit() + ", getAllocation()=" + getAllocation()
					+ ", getIsdisable()=" + getIsdisable() + ", getOverrideintent()=" + getOverrideintent()
					+ ", getDowntimeschedule()=" + getDowntimeschedule()
					+ ", getDowntimescheduleexceptiondayoverrides()=" + getDowntimescheduleexceptiondayoverrides()
					+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString()
					+ "]";
		}
	    
	    
	    
	    
}
