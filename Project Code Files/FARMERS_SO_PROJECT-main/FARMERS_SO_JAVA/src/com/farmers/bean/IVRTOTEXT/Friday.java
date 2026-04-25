package com.farmers.bean.IVRTOTEXT;

public class Friday {
	 private String name;
	    private String timewindow;
	    private String timezone;
	    public String getName() {
	        return name;
	    }
	    public void setName(String name) {
	        this.name = name;
	    }
	    public String getTimewindow() {
	        return timewindow;
	    }
	    public void setTimewindow(String timewindow) {
	        this.timewindow = timewindow;
	    }
	    public String getTimezone() {
	        return timezone;
	    }
	    public void setTimezone(String timezone) {
	        this.timezone = timezone;
	    }
		@Override
		public String toString() {
			return "Friday [name=" + name + ", timewindow=" + timewindow + ", timezone=" + timezone + ", getName()="
					+ getName() + ", getTimewindow()=" + getTimewindow() + ", getTimezone()=" + getTimezone()
					+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString()
					+ "]";
		}
}
