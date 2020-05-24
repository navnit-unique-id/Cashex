package com.inspirado.kuber.ecom.store;

import com.inspirado.kuber.User;

import java.io.Serializable;
import java.util.Date;


public class Customer implements Serializable {
	private Long id;
	private String name;
	String orgChain;
	String displayId;
	private Long sourceId;
	private Long ledgerId;
	private String ledgerDisplayId;
	private String custId;
	private int custIdType;
	private String phone;
	private String address;
	private Date dob;
	private Date doa;
	private String email;
	private int customerType;
	private int state;
	private int city;
	private String photoKey;
	private String idType;
	private String idNumber;
	private String nationality;
	private int status; // 1--> active, 11--> deactivated
	//private Membership membership;
	private String summary;


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

	public String getOrgChain() {
		return orgChain;
	}

	public void setOrgChain(String orgChain) {
		this.orgChain = orgChain;
	}

	public String getDisplayId() {
		return displayId;
	}

	public void setDisplayId(String displayId) {
		this.displayId = displayId;
	}

	public Long getSourceId() {
		return sourceId;
	}

	public void setSourceId(Long sourceId) {
		this.sourceId = sourceId;
	}

	public Long getLedgerId() {
		return ledgerId;
	}

	public void setLedgerId(Long ledgerId) {
		this.ledgerId = ledgerId;
	}

	public String getLedgerDisplayId() {
		return ledgerDisplayId;
	}

	public void setLedgerDisplayId(String ledgerDisplayId) {
		this.ledgerDisplayId = ledgerDisplayId;
	}

	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	public int getCustIdType() {
		return custIdType;
	}

	public void setCustIdType(int custIdType) {
		this.custIdType = custIdType;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}

	public Date getDoa() {
		return doa;
	}

	public void setDoa(Date doa) {
		this.doa = doa;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getCustomerType() {
		return customerType;
	}

	public void setCustomerType(int customerType) {
		this.customerType = customerType;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getCity() {
		return city;
	}

	public void setCity(int city) {
		this.city = city;
	}

	public String getPhotoKey() {
		return photoKey;
	}

	public void setPhotoKey(String photoKey) {
		this.photoKey = photoKey;
	}

	public String getIdType() {
		return idType;
	}

	public void setIdType(String idType) {
		this.idType = idType;
	}

	public String getIdNumber() {
		return idNumber;
	}

	public void setIdNumber(String idNumber) {
		this.idNumber = idNumber;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}
}
