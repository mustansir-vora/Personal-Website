package com.farmers.bean.PointerIDCard;

public class BillingDetails {
	private BalanceOnPriorMode balanceOnPriorMode;
    private BalanceThresholdAmountOnPriorMode balanceThresholdAmountOnPriorMode;
    private FullBalanceDue fullBalanceDue;
    private LastPaymentAmount lastPaymentAmount;
    private String lastPaymentDate;
    private NextPaymentAmount nextPaymentAmount;
    private String nextPaymentDate;
    private String policyActivityDate;
    private PolicyCollectionAmount policyCollectionAmount;
    private PolicyCollectionThresholdAmount policyCollectionThresholdAmount;
    private String hasBalanceOnPriorModeAsString;
    private String hasEFTIndicatorAsString;
    private Object isPolicyInCollectionMode;
    public BalanceOnPriorMode getBalanceOnPriorMode() {
        return balanceOnPriorMode;
    }
    public void setBalanceOnPriorMode(BalanceOnPriorMode balanceOnPriorMode) {
        this.balanceOnPriorMode = balanceOnPriorMode;
    }
    public BalanceThresholdAmountOnPriorMode getBalanceThresholdAmountOnPriorMode() {
        return balanceThresholdAmountOnPriorMode;
    }
    public void setBalanceThresholdAmountOnPriorMode(BalanceThresholdAmountOnPriorMode balanceThresholdAmountOnPriorMode) {
        this.balanceThresholdAmountOnPriorMode = balanceThresholdAmountOnPriorMode;
    }
    public FullBalanceDue getFullBalanceDue() {
        return fullBalanceDue;
    }
    public void setFullBalanceDue(FullBalanceDue fullBalanceDue) {
        this.fullBalanceDue = fullBalanceDue;
    }
    public LastPaymentAmount getLastPaymentAmount() {
        return lastPaymentAmount;
    }
    public void setLastPaymentAmount(LastPaymentAmount lastPaymentAmount) {
        this.lastPaymentAmount = lastPaymentAmount;
    }
    public String getLastPaymentDate() {
        return lastPaymentDate;
    }
    public void setLastPaymentDate(String lastPaymentDate) {
        this.lastPaymentDate = lastPaymentDate;
    }
    public NextPaymentAmount getNextPaymentAmount() {
        return nextPaymentAmount;
    }
    public void setNextPaymentAmount(NextPaymentAmount nextPaymentAmount) {
        this.nextPaymentAmount = nextPaymentAmount;
    }
    public String getNextPaymentDate() {
        return nextPaymentDate;
    }
    public void setNextPaymentDate(String nextPaymentDate) {
        this.nextPaymentDate = nextPaymentDate;
    }
    public String getPolicyActivityDate() {
        return policyActivityDate;
    }
    public void setPolicyActivityDate(String policyActivityDate) {
        this.policyActivityDate = policyActivityDate;
    }
    public PolicyCollectionAmount getPolicyCollectionAmount() {
        return policyCollectionAmount;
    }
    public void setPolicyCollectionAmount(PolicyCollectionAmount policyCollectionAmount) {
        this.policyCollectionAmount = policyCollectionAmount;
    }
    public PolicyCollectionThresholdAmount getPolicyCollectionThresholdAmount() {
        return policyCollectionThresholdAmount;
    }
    public void setPolicyCollectionThresholdAmount(PolicyCollectionThresholdAmount policyCollectionThresholdAmount) {
        this.policyCollectionThresholdAmount = policyCollectionThresholdAmount;
    }
    public String getHasBalanceOnPriorModeAsString() {
        return hasBalanceOnPriorModeAsString;
    }
    public void setHasBalanceOnPriorModeAsString(String hasBalanceOnPriorModeAsString) {
        this.hasBalanceOnPriorModeAsString = hasBalanceOnPriorModeAsString;
    }
    public String getHasEFTIndicatorAsString() {
        return hasEFTIndicatorAsString;
    }
    public void setHasEFTIndicatorAsString(String hasEFTIndicatorAsString) {
        this.hasEFTIndicatorAsString = hasEFTIndicatorAsString;
    }
    public Object getIsPolicyInCollectionMode() {
        return isPolicyInCollectionMode;
    }
    public void setIsPolicyInCollectionMode(Object isPolicyInCollectionMode) {
        this.isPolicyInCollectionMode = isPolicyInCollectionMode;
    }
	@Override
	public String toString() {
		return "BillingDetails [balanceOnPriorMode=" + balanceOnPriorMode + ", balanceThresholdAmountOnPriorMode="
				+ balanceThresholdAmountOnPriorMode + ", lastPaymentDate=" + lastPaymentDate + ", nextPaymentDate="
				+ nextPaymentDate + ", policyActivityDate=" + policyActivityDate + ", hasBalanceOnPriorModeAsString="
				+ hasBalanceOnPriorModeAsString + ", hasEFTIndicatorAsString=" + hasEFTIndicatorAsString
				+ ", isPolicyInCollectionMode=" + isPolicyInCollectionMode + ", getBalanceOnPriorMode()="
				+ getBalanceOnPriorMode() + ", getBalanceThresholdAmountOnPriorMode()="
				+ getBalanceThresholdAmountOnPriorMode() + ", getLastPaymentDate()=" + getLastPaymentDate()
				+ ", getNextPaymentDate()=" + getNextPaymentDate() + ", getPolicyActivityDate()="
				+ getPolicyActivityDate() + ", getHasBalanceOnPriorModeAsString()=" + getHasBalanceOnPriorModeAsString()
				+ ", getHasEFTIndicatorAsString()=" + getHasEFTIndicatorAsString() + ", getIsPolicyInCollectionMode()="
				+ getIsPolicyInCollectionMode() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode()
				+ ", toString()=" + super.toString() + "]";
	}
    
    
    
    
}
