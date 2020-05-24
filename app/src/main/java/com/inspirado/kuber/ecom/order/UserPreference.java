package com.inspirado.kuber.ecom.order;

public class UserPreference {
	Long id;
	Long userId;
	int type; // 1 ==> property, 2 -> settings
	Long typeId; // like propertyId
	String attributeName;
	String attributValue;
	String attributeType;


	public UserPreference(Long id, Long userId, int type, Long typeId, String attributeName, String attributValue, String attributeType) {
		this.id = id;
		this.userId = userId;
		this.type = type;
		this.typeId = typeId;
		this.attributeName = attributeName;
		this.attributValue = attributValue;
		this.attributeType = attributeType;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public String getAttributValue() {
		return attributValue;
	}

	public void setAttributValue(String attributValue) {
		this.attributValue = attributValue;
	}

	public String getAttributeType() {
		return attributeType;
	}

	public void setAttributeType(String attributeType) {
		this.attributeType = attributeType;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Long getTypeId() {
		return typeId;
	}

	public void setTypeId(Long typeId) {
		this.typeId = typeId;
	}
}
