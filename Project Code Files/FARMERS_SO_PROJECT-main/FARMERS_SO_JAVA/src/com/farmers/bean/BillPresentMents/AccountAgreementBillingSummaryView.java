package com.farmers.bean.BillPresentMents;

import java.util.ArrayList;
import java.util.List;

public class AccountAgreementBillingSummaryView {
	 private AccountAgreementAnalyticsView accountAgreementAnalyticsView;
	    private BasicBillingAccountAgreementView basicBillingAccountAgreementView;
	    private BasicPartyContactPointDetails basicPartyContactPointDetails;
	    private BillingAllocatedPaymentView billingAllocatedPaymentView;
	    private List<InsurancePolicyBillingSummaryView> insurancePolicyBillingSummaryView = new ArrayList<InsurancePolicyBillingSummaryView>();
	    private MoneyScheduler moneyScheduler;
	    private NextFinancialTransactionCommunicationView nextFinancialTransactionCommunicationView;
	    private PreviousFinancialTransactionCommunicationView previousFinancialTransactionCommunicationView;
	    private RefundPaymentView refundPaymentView;
	    private CancelNoticeFinTransCommunicationView cancelNoticeFinTransCommunicationView;
	    public AccountAgreementAnalyticsView getAccountAgreementAnalyticsView() {
	        return accountAgreementAnalyticsView;
	    }
	    public void setAccountAgreementAnalyticsView(AccountAgreementAnalyticsView accountAgreementAnalyticsView) {
	        this.accountAgreementAnalyticsView = accountAgreementAnalyticsView;
	    }
	    public BasicBillingAccountAgreementView getBasicBillingAccountAgreementView() {
	        return basicBillingAccountAgreementView;
	    }
	    public void setBasicBillingAccountAgreementView(BasicBillingAccountAgreementView basicBillingAccountAgreementView) {
	        this.basicBillingAccountAgreementView = basicBillingAccountAgreementView;
	    }
	    public BasicPartyContactPointDetails getBasicPartyContactPointDetails() {
	        return basicPartyContactPointDetails;
	    }
	    public void setBasicPartyContactPointDetails(BasicPartyContactPointDetails basicPartyContactPointDetails) {
	        this.basicPartyContactPointDetails = basicPartyContactPointDetails;
	    }
	    public BillingAllocatedPaymentView getBillingAllocatedPaymentView() {
	        return billingAllocatedPaymentView;
	    }
	    public void setBillingAllocatedPaymentView(BillingAllocatedPaymentView billingAllocatedPaymentView) {
	        this.billingAllocatedPaymentView = billingAllocatedPaymentView;
	    }
	    public List<InsurancePolicyBillingSummaryView> getInsurancePolicyBillingSummaryView() {
	        return insurancePolicyBillingSummaryView;
	    }
	    public void setInsurancePolicyBillingSummaryView(List<InsurancePolicyBillingSummaryView> insurancePolicyBillingSummaryView) {
	        this.insurancePolicyBillingSummaryView = insurancePolicyBillingSummaryView;
	    }
	    public MoneyScheduler getMoneyScheduler() {
	        return moneyScheduler;
	    }
	    public void setMoneyScheduler(MoneyScheduler moneyScheduler) {
	        this.moneyScheduler = moneyScheduler;
	    }
	    public NextFinancialTransactionCommunicationView getNextFinancialTransactionCommunicationView() {
	        return nextFinancialTransactionCommunicationView;
	    }
	    public void setNextFinancialTransactionCommunicationView(NextFinancialTransactionCommunicationView nextFinancialTransactionCommunicationView) {
	        this.nextFinancialTransactionCommunicationView = nextFinancialTransactionCommunicationView;
	    }
	    public PreviousFinancialTransactionCommunicationView getPreviousFinancialTransactionCommunicationView() {
	        return previousFinancialTransactionCommunicationView;
	    }
	    public void setPreviousFinancialTransactionCommunicationView(PreviousFinancialTransactionCommunicationView previousFinancialTransactionCommunicationView) {
	        this.previousFinancialTransactionCommunicationView = previousFinancialTransactionCommunicationView;
	    }
	    public RefundPaymentView getRefundPaymentView() {
	        return refundPaymentView;
	    }
	    public void setRefundPaymentView(RefundPaymentView refundPaymentView) {
	        this.refundPaymentView = refundPaymentView;
	    }
	    public CancelNoticeFinTransCommunicationView getCancelNoticeFinTransCommunicationView() {
	        return cancelNoticeFinTransCommunicationView;
	    }
	    public void setCancelNoticeFinTransCommunicationView(CancelNoticeFinTransCommunicationView cancelNoticeFinTransCommunicationView) {
	        this.cancelNoticeFinTransCommunicationView = cancelNoticeFinTransCommunicationView;
	    }
		@Override
		public String toString() {
			return "AccountAgreementBillingSummaryView [basicBillingAccountAgreementView="
					+ basicBillingAccountAgreementView + ", basicPartyContactPointDetails="
					+ basicPartyContactPointDetails + ", billingAllocatedPaymentView=" + billingAllocatedPaymentView
					+ ", moneyScheduler=" + moneyScheduler + ", nextFinancialTransactionCommunicationView="
					+ nextFinancialTransactionCommunicationView + ", previousFinancialTransactionCommunicationView="
					+ previousFinancialTransactionCommunicationView + ", refundPaymentView=" + refundPaymentView
					+ ", cancelNoticeFinTransCommunicationView=" + cancelNoticeFinTransCommunicationView
					+ ", getBasicBillingAccountAgreementView()=" + getBasicBillingAccountAgreementView()
					+ ", getBasicPartyContactPointDetails()=" + getBasicPartyContactPointDetails()
					+ ", getBillingAllocatedPaymentView()=" + getBillingAllocatedPaymentView()
					+ ", getMoneyScheduler()=" + getMoneyScheduler()
					+ ", getNextFinancialTransactionCommunicationView()="
					+ getNextFinancialTransactionCommunicationView()
					+ ", getPreviousFinancialTransactionCommunicationView()="
					+ getPreviousFinancialTransactionCommunicationView() + ", getRefundPaymentView()="
					+ getRefundPaymentView() + ", getCancelNoticeFinTransCommunicationView()="
					+ getCancelNoticeFinTransCommunicationView() + ", getClass()=" + getClass() + ", hashCode()="
					+ hashCode() + ", toString()=" + super.toString() + "]";
		}
	    
	    
	    
}
