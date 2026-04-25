package com.farmers.util;

public class SequenceNumber {
	int seqNumber=0;
	public int get() {
		seqNumber=seqNumber+1;
		return seqNumber;
	}
}
