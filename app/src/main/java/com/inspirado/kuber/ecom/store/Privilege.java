package com.inspirado.kuber.ecom.store;

import java.util.Date;

public class Privilege{
	Long id;
	String type; // 1-> PROPERTY
	String description;
	Long referenceId; // which property for which this priv is all about
	String attributeImpacted; //this privilege impacts which attribute of type with refernce type above ...eg visibility of property 34
	int dataType; // 1-> date, 2 --> GB, yes/no, visibile/invisible
	String dataTypeDescription;
	String measure ; // 12-12-2020, 5
	Long membershipId; //might not be required
	int status;
	String orgChain;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOrgChain() {
		return orgChain;
	}

	public int getDataType() {
		return dataType;
	}

	public void setDataType(int dataType) {
		this.dataType = dataType;
	}

	public void setOrgChain(String orgChain) {
		this.orgChain = orgChain;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(Long referenceId) {
		this.referenceId = referenceId;
	}

	public String getAttributeImpacted() {
		return attributeImpacted;
	}

	public void setAttributeImpacted(String attributeImpacted) {
		this.attributeImpacted = attributeImpacted;
	}

	public String getDataTypeDescription() {
		return dataTypeDescription;
	}

	public void setDataTypeDescription(String dataTypeDescription) {
		this.dataTypeDescription = dataTypeDescription;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMeasure() {
		return measure;
	}

	public void setMeasure(String measure) {
		this.measure = measure;
	}

	public Long getMembershipId() {
		return membershipId;
	}

	public void setMembershipId(Long membershipId) {
		this.membershipId = membershipId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}
