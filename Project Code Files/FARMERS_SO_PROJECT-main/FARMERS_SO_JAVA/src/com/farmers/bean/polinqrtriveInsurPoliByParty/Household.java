package com.farmers.bean.polinqrtriveInsurPoliByParty;

import java.util.ArrayList;

public class Household {
	 public ArrayList<AutoPolicy> autoPolicies;
	    public ArrayList<HomePolicy> homePolicies;
		public ArrayList<AutoPolicy> getAutoPolicies() {
			return autoPolicies;
		}
		public void setAutoPolicies(ArrayList<AutoPolicy> autoPolicies) {
			this.autoPolicies = autoPolicies;
		}
		public ArrayList<HomePolicy> getHomePolicies() {
			return homePolicies;
		}
		public void setHomePolicies(ArrayList<HomePolicy> homePolicies) {
			this.homePolicies = homePolicies;
		}
		@Override
		public String toString() {
			return "Household [autoPolicies=" + autoPolicies + ", homePolicies=" + homePolicies + ", getAutoPolicies()="
					+ getAutoPolicies() + ", getHomePolicies()=" + getHomePolicies() + ", getClass()=" + getClass()
					+ ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
		}
	    
	    
	    
	    
}
