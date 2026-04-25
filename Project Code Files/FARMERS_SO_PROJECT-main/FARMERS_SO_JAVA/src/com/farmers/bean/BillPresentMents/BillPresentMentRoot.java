package com.farmers.bean.BillPresentMents;

public class BillPresentMentRoot {
	  private RetrieveBillingSummaryResponse RetrieveBillingSummaryResponse;

	public RetrieveBillingSummaryResponse getRetrieveBillingSummaryResponse() {
		return RetrieveBillingSummaryResponse;
	}

	public void setRetrieveBillingSummaryResponse(RetrieveBillingSummaryResponse retrieveBillingSummaryResponse) {
		RetrieveBillingSummaryResponse = retrieveBillingSummaryResponse;
	}

	@Override
	public String toString() {
		return "BillPresentMentRoot [RetrieveBillingSummaryResponse=" + RetrieveBillingSummaryResponse + "]";
	}
	   
	    
}
