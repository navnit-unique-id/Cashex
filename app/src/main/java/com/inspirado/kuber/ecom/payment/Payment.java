package com.inspirado.kuber.ecom.payment;

import java.io.Serializable;
import java.util.Date;

public class Payment implements Serializable {
	Long id;
	double amount;
	String creationTriggerId;
	int type; // 1--> advance, 2--> settlement
	int mop;
	Date paymentDate;
	String refNo;
	int refType;
	String comments;
	int status; // 1 --> unsettled, 2 --> settled 11 --> cancelled
	Long paymentsId;
	Long ledgerId;
	String summary;
	String extAttr1Name;
	String extAttr1Value;
	String extAttr2Name;
	String extAttr2Value;
	String extAttr3Name;
	String extAttr3Value;
	String orgChain;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getCreationTriggerId() {
		return creationTriggerId;
	}

	public void setCreationTriggerId(String creationTriggerId) {
		this.creationTriggerId = creationTriggerId;
	}

	public String getOrgChain() {
		return orgChain;
	}

	public void setOrgChain(String orgChain) {
		this.orgChain = orgChain;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getMop() {
		return mop;
	}

	public String getExtAttr1Name() {
		return extAttr1Name;
	}

	public void setExtAttr1Name(String extAttr1Name) {
		this.extAttr1Name = extAttr1Name;
	}

	public String getExtAttr1Value() {
		return extAttr1Value;
	}

	public void setExtAttr1Value(String extAttr1Value) {
		this.extAttr1Value = extAttr1Value;
	}

	public void setMop(int mop) {
		this.mop = mop;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public String getRefNo() {
		return refNo;
	}

	public void setRefNo(String refNo) {
		this.refNo = refNo;
	}

	public int getRefType() {
		return refType;
	}

	public void setRefType(int refType) {
		this.refType = refType;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Long getPaymentsId() {
		return paymentsId;
	}

	public void setPaymentsId(Long paymentsId) {
		this.paymentsId = paymentsId;
	}

	public Long getLedgerId() {
		return ledgerId;
	}

	public void setLedgerId(Long ledgerId) {
		this.ledgerId = ledgerId;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getExtAttr2Name() {
		return extAttr2Name;
	}

	public void setExtAttr2Name(String extAttr2Name) {
		this.extAttr2Name = extAttr2Name;
	}

	public String getExtAttr2Value() {
		return extAttr2Value;
	}

	public void setExtAttr2Value(String extAttr2Value) {
		this.extAttr2Value = extAttr2Value;
	}

	public String getExtAttr3Name() {
		return extAttr3Name;
	}

	public void setExtAttr3Name(String extAttr3Name) {
		this.extAttr3Name = extAttr3Name;
	}

	public String getExtAttr3Value() {
		return extAttr3Value;
	}

	public void setExtAttr3Value(String extAttr3Value) {
		this.extAttr3Value = extAttr3Value;
	}
}