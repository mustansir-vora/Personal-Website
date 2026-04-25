package com.farmers.bean.BillPresentMents;

public class MinimumPaymentDueAmount {
private String theCurrencyAmount;
private String theCurrencyCode;
public String getTheCurrencyAmount() {
	return theCurrencyAmount;
}
public void setTheCurrencyAmount(String theCurrencyAmount) {
	this.theCurrencyAmount = theCurrencyAmount;
}
public String getTheCurrencyCode() {
	return theCurrencyCode;
}
public void setTheCurrencyCode(String theCurrencyCode) {
	this.theCurrencyCode = theCurrencyCode;
}
@Override
public String toString() {
	return "MinimumPaymentDueAmount [theCurrencyAmount=" + theCurrencyAmount + ", theCurrencyCode=" + theCurrencyCode
			+ ", getTheCurrencyAmount()=" + getTheCurrencyAmount() + ", getTheCurrencyCode()=" + getTheCurrencyCode()
			+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
}


}
