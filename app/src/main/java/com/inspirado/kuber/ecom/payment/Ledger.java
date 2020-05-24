package com.inspirado.kuber.ecom.payment;

import com.inspirado.kuber.User;
import com.inspirado.kuber.ecom.store.BankAccount;
import com.inspirado.kuber.ecom.store.Customer;

import java.io.Serializable;
import java.util.Date;


public class Ledger implements Serializable {
	Long id;
	String name;
	int ledgerType; // 1--> Corporate, 2--> Retail, 3 --> Employee
	int status; // 1-> active, 2--> dormant, 11-> inactive
	double outstandingBalance;
	private String displayId;
	String orgChain;
	Date refreshDate;
	Customer customer;
	BankAccount bankAccount;
	Long bankAccountId;
	String pan;
	boolean acceptsCOD;
	boolean acceptsDirectPayment;
	boolean acceptsPaymentViaPlatform;
	String directPaymentNotes;
	int kycStatus; // 0-> NA, 1 --> Submitted, 2-> Waiting for response, 3 --> Approved, 11--> Cancelled, 12 --> Rejected

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLedgerType() {
		return ledgerType;
	}

	public String getPan() {
		return pan;
	}

	public void setPan(String pan) {
		this.pan = pan;
	}

	public void setLedgerType(int ledgerType) {
		this.ledgerType = ledgerType;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public double getOutstandingBalance() {
		return outstandingBalance;
	}

	public void setOutstandingBalance(double outstandingBalance) {
		this.outstandingBalance = outstandingBalance;
	}

	public String getDisplayId() {
		return displayId;
	}

	public void setDisplayId(String displayId) {
		this.displayId = displayId;
	}

	public String getOrgChain() {
		return orgChain;
	}

	public void setOrgChain(String orgChain) {
		this.orgChain = orgChain;
	}

	public Date getRefreshDate() {
		return refreshDate;
	}

	public void setRefreshDate(Date refreshDate) {
		this.refreshDate = refreshDate;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public BankAccount getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(BankAccount bankAccount) {
		this.bankAccount = bankAccount;
	}

	public Long getBankAccountId() {
		return bankAccountId;
	}

	public void setBankAccountId(Long bankAccountId) {
		this.bankAccountId = bankAccountId;
	}

	public boolean isAcceptsCOD() {
		return acceptsCOD;
	}

	public void setAcceptsCOD(boolean acceptsCOD) {
		this.acceptsCOD = acceptsCOD;
	}

	public boolean isAcceptsDirectPayment() {
		return acceptsDirectPayment;
	}

	public void setAcceptsDirectPayment(boolean acceptsDirectPayment) {
		this.acceptsDirectPayment = acceptsDirectPayment;
	}

	public boolean isAcceptsPaymentViaPlatform() {
		return acceptsPaymentViaPlatform;
	}

	public void setAcceptsPaymentViaPlatform(boolean acceptsPaymentViaPlatform) {
		this.acceptsPaymentViaPlatform = acceptsPaymentViaPlatform;
	}

	public String getDirectPaymentNotes() {
		return directPaymentNotes;
	}

	public void setDirectPaymentNotes(String directPaymentNotes) {
		this.directPaymentNotes = directPaymentNotes;
	}

	public int getKycStatus() {
		return kycStatus;
	}

	public void setKycStatus(int kycStatus) {
		this.kycStatus = kycStatus;
	}
}
