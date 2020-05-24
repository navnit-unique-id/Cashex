package com.inspirado.kuber.ecom.store;

import com.inspirado.kuber.ecom.order.UserPreference;

import java.io.Serializable;
import java.util.Date;

public class Store implements Serializable {

    private Long id;
    private String name;
    private String address;
    private String city;
    private String state;
    private String zip;
    private String email;
    private String phone;
    private boolean hasDeliveryService;
    private String openTime; //in minutes
    private String closeTime; // in minutes since midnight
    private double lat;
    private double lng;
    private Long sourceId;
    private Long ownerId;
    private Long ownerLedgerId;
    private Date marketplaceVisibilityEndDate;


    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    private double rating;
    int type; // 1--> Banquet, 2--> Hotel Room, 3--> Kirana, 4 --> Salon, 5 -> fruit veg shop
    private String typeTxt;
    private int status;
    private String statusTxt;
    private String orgChain;
    private boolean selected;
    private double refLat;
    private double refLng;
    private double refDistance;
    private int openClose;
    private UserPreference favouriteStorePreferenceEntry;
    private double propertyRatingScoreAvg;
    private int propertyRatingScoreSum;
    private int propertyScoreCount;
    private double deliveryCharge;

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public double getRefLat() {
        return refLat;
    }

    public void setRefLat(double refLat) {
        this.refLat = refLat;
    }

    public Date getMarketplaceVisibilityEndDate() {
        return marketplaceVisibilityEndDate;
    }

    public void setMarketplaceVisibilityEndDate(Date marketplaceVisibilityEndDate) {
        this.marketplaceVisibilityEndDate = marketplaceVisibilityEndDate;
    }

    public double getDeliveryCharge() {
        return deliveryCharge;
    }

    public void setDeliveryCharge(double deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }

    public double getRefLng() {
        return refLng;
    }

    public UserPreference getFavouriteStorePreferenceEntry() {
        return favouriteStorePreferenceEntry;
    }

    public void setFavouriteStorePreferenceEntry(UserPreference favouriteStorePreferenceEntry) {
        this.favouriteStorePreferenceEntry = favouriteStorePreferenceEntry;
    }

    public int getOpenClose() {
        return openClose;
    }

    public void setOpenClose(int openClose) {
        this.openClose = openClose;
    }

    public void setRefLng(double refLng) {
        this.refLng = refLng;
    }

    public double getPropertyRatingScoreAvg() {
        return propertyRatingScoreAvg;
    }

    public void setPropertyRatingScoreAvg(double propertyRatingScoreAvg) {
        this.propertyRatingScoreAvg = propertyRatingScoreAvg;
    }

    public int getPropertyRatingScoreSum() {
        return propertyRatingScoreSum;
    }

    public void setPropertyRatingScoreSum(int propertyRatingScoreSum) {
        this.propertyRatingScoreSum = propertyRatingScoreSum;
    }

    public int getPropertyScoreCount() {
        return propertyScoreCount;
    }

    public void setPropertyScoreCount(int propertyScoreCount) {
        this.propertyScoreCount = propertyScoreCount;
    }

    public double getRefDistance() {
        return refDistance;
    }

    public void setRefDistance(double refDistance) {
        this.refDistance = refDistance;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public Long getId() {
        return id;
    }

    public Long getOwnerLedgerId() {
        return ownerLedgerId;
    }

    public void setOwnerLedgerId(Long ownerLedgerId) {
        this.ownerLedgerId = ownerLedgerId;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isHasDeliveryService() {
        return hasDeliveryService;
    }

    public void setHasDeliveryService(boolean hasDeliveryService) {
        this.hasDeliveryService = hasDeliveryService;
    }

    public String getOpenTime() {
        return openTime;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public String getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(String closeTime) {
        this.closeTime = closeTime;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTypeTxt() {
        return typeTxt;
    }

    public void setTypeTxt(String typeTxt) {
        this.typeTxt = typeTxt;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStatusTxt() {
        return statusTxt;
    }

    public void setStatusTxt(String statusTxt) {
        this.statusTxt = statusTxt;
    }

    public String getOrgChain() {
        return orgChain;
    }

    public void setOrgChain(String orgChain) {
        this.orgChain = orgChain;
    }
}
