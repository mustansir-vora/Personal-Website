package com.servion.farmers;

public class DestinationWeightage{
    
	private String destinationIdentifier;
    private int weightage;
    private int weightageUsedSoFar;

    public String getDestinationIdentifier() {
		return destinationIdentifier;
	}
	public void setDestinationIdentifier(String destinationIdentifier) {
		this.destinationIdentifier = destinationIdentifier;
	}
	public int getWeightage() {
		return weightage;
	}
	public void setWeightage(int weightage) {
		this.weightage = weightage;
	}
	public int getWeightageUsedSoFar() {
		return weightageUsedSoFar;
	}
	public void setWeightageUsedSoFar(int weightageUsedSoFar) {
		this.weightageUsedSoFar = weightageUsedSoFar;
	}
	@Override
	public String toString() {
		return "DestinationWeightage [destinationIdentifier=" + destinationIdentifier + ", weightage=" + weightage
				+ ", weightageUsedSoFar=" + weightageUsedSoFar + "]";
	}
	
}