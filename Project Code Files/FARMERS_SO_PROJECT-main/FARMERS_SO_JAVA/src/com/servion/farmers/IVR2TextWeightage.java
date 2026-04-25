package com.servion.farmers;

public class IVR2TextWeightage {

	private String IVR2TextIntent;
	private float weightage;
	private float callsOffered;
	private float totalCalls;
	
	public String getIVR2TextIntent() {
		return IVR2TextIntent;
	}
	public void setIVR2TextIntent(String iVR2TextIntent) {
		IVR2TextIntent = iVR2TextIntent;
	}
	public float getWeightage() {
		return weightage;
	}
	public void setWeightage(float weightage) {
		this.weightage = weightage;
	}
	public float getCallsOffered() {
		return callsOffered;
	}
	public void setCallsOffered(float callsOffered) {
		this.callsOffered = callsOffered;
	}
	public float getTotalCalls() {
		return totalCalls;
	}
	public void setTotalCalls(float totalCalls) {
		this.totalCalls = totalCalls;
	}
	@Override
	public String toString() {
		return "IVR2TextWeightage [IVR2TextIntent=" + IVR2TextIntent + ", weightage=" + weightage + ", callsOffered="
				+ callsOffered + ", totalCalls=" + totalCalls + "]";
	}
}
