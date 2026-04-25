package com.farmers.bean.Halo.BillingDetails;

public class PaymentMethod {
	 private String paymentSource;
	    private String tranSourceSystemUIDesc;
	    private String tranSourceSystemDesc;
	    private String type;
	    private String cardNumber;
	    public String getPaymentSource() {
	        return paymentSource;
	    }
	    public void setPaymentSource(String paymentSource) {
	        this.paymentSource = paymentSource;
	    }
	    public String getTranSourceSystemUIDesc() {
	        return tranSourceSystemUIDesc;
	    }
	    public void setTranSourceSystemUIDesc(String tranSourceSystemUIDesc) {
	        this.tranSourceSystemUIDesc = tranSourceSystemUIDesc;
	    }
	    public String getTranSourceSystemDesc() {
	        return tranSourceSystemDesc;
	    }
	    public void setTranSourceSystemDesc(String tranSourceSystemDesc) {
	        this.tranSourceSystemDesc = tranSourceSystemDesc;
	    }
	    public String getType() {
	        return type;
	    }
	    public void setType(String type) {
	        this.type = type;
	    }
	    public String getCardNumber() {
	        return cardNumber;
	    }
	    public void setCardNumber(String cardNumber) {
	        this.cardNumber = cardNumber;
	    }
		@Override
		public String toString() {
			return "PaymentMethod [paymentSource=" + paymentSource + ", tranSourceSystemUIDesc="
					+ tranSourceSystemUIDesc + ", tranSourceSystemDesc=" + tranSourceSystemDesc + ", type=" + type
					+ ", cardNumber=" + cardNumber + ", getPaymentSource()=" + getPaymentSource()
					+ ", getTranSourceSystemUIDesc()=" + getTranSourceSystemUIDesc() + ", getTranSourceSystemDesc()="
					+ getTranSourceSystemDesc() + ", getType()=" + getType() + ", getCardNumber()=" + getCardNumber()
					+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString()
					+ "]";
		}
}
