package com.inspirado.kuber;

import java.io.Serializable;
import java.util.Date;


public class User implements Serializable {
    Long id;
    String username;
    String password;
    String name;
    String mobileNumber;
    String email;
    int userType;
    String businessName;
    String address;
    String state;
    String city;
    String pinCode;
    Double  lng;
    Double lat;
    int status;
    String otp;
    String referralCode;
    String clientCode;

    float lndrRatingScore;
    float lndrRatingTotal;
    int lndrTransactions;

    float rcvrRatingScore;
    float rcvrRatingTotal;
    int rcvrTransactions;

    float overallScore;
    boolean pickupServiceEnabled;
    double pickupRate;
    double pickupRateEscalated;
    double deliveryRate;
    double deliveryRateEscalated;

    public double getPickupRate() {
        return pickupRate;
    }

    public void setPickupRate(double pickupRate) {
        this.pickupRate = pickupRate;
    }

    public double getPickupRateEscalated() {
        return pickupRateEscalated;
    }

    public void setPickupRateEscalated(double pickupRateEscalated) {
        this.pickupRateEscalated = pickupRateEscalated;
    }

    public double getDeliveryRate() {
        return deliveryRate;
    }

    public void setDeliveryRate(double deliveryRate) {
        this.deliveryRate = deliveryRate;
    }

    public double getDeliveryRateEscalated() {
        return deliveryRateEscalated;
    }

    public void setDeliveryRateEscalated(double deliveryRateEscalated) {
        this.deliveryRateEscalated = deliveryRateEscalated;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getMobileNumber() {
        return mobileNumber;
    }
    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public int getUserType() {
        return userType;
    }
    public void setUserType(int userType) {
        this.userType = userType;
    }
    public String getBusinessName() {
        return businessName;
    }
    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getPinCode() {
        return pinCode;
    }
    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }
    public Double getLng() {
        return lng;
    }
    public void setLng(Double lng) {
        this.lng = lng;
    }
    public Double getLat() {
        return lat;
    }
    public void setLat(Double lat) {
        this.lat = lat;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public String getReferralCode() {
        return referralCode;
    }

    public void setReferralCode(String referralCode) {
        this.referralCode = referralCode;
    }

    public float getLndrRatingScore() {
        return lndrRatingScore;
    }

    public void setLndrRatingScore(float lndrRatingScore) {
        this.lndrRatingScore = lndrRatingScore;
    }

    public float getLndrRatingTotal() {
        return lndrRatingTotal;
    }

    public void setLndrRatingTotal(float lndrRatingTotal) {
        this.lndrRatingTotal = lndrRatingTotal;
    }

    public int getLndrTransactions() {
        return lndrTransactions;
    }

    public void setLndrTransactions(int lndrTransactions) {
        this.lndrTransactions = lndrTransactions;
    }

    public float getRcvrRatingScore() {
        return rcvrRatingScore;
    }

    public void setRcvrRatingScore(float rcvrRatingScore) {
        this.rcvrRatingScore = rcvrRatingScore;
    }

    public float getRcvrRatingTotal() {
        return rcvrRatingTotal;
    }

    public void setRcvrRatingTotal(float rcvrRatingTotal) {
        this.rcvrRatingTotal = rcvrRatingTotal;
    }

    public int getRcvrTransactions() {
        return rcvrTransactions;
    }

    public void setRcvrTransactions(int rcvrTransactions) {
        this.rcvrTransactions = rcvrTransactions;
    }

    public float getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(float overallScore) {
        this.overallScore = overallScore;
    }

    public String getClientCode() {
        return clientCode;
    }

    public void setClientCode(String clientCode) {
        this.clientCode = clientCode;
    }

    public boolean isPickupServiceEnabled() {
        return pickupServiceEnabled;
    }

    public void setPickupServiceEnabled(boolean pickupServiceEnabled) {
        this.pickupServiceEnabled = pickupServiceEnabled;
    }
}
