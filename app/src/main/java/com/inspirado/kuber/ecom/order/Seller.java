package com.inspirado.kuber.ecom.order;

import com.inspirado.kuber.User;

import java.io.Serializable;

/**
 *
 */

/**
 * @author navnit
 *
 */
public class Seller implements Serializable {

    String sellerName;
    String sellerMobileNumber;
    String sellerEmail;
    String sellerBusinessName;
    String sellerAddress;
    String sellerState;
    String sellerPinCode;
    double sellerLng;
    double sellerLat;
    int sellerStatus;
    Long sellerLedgerId;
    String sellerCity;
    Long sellerSourceId;
    Long ownerId;

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getSellerMobileNumber() {
        return sellerMobileNumber;
    }

    public void setSellerMobileNumber(String sellerMobileNumber) {
        this.sellerMobileNumber = sellerMobileNumber;
    }

    public String getSellerEmail() {
        return sellerEmail;
    }

    public void setSellerEmail(String sellerEmail) {
        this.sellerEmail = sellerEmail;
    }

    public String getSellerBusinessName() {
        return sellerBusinessName;
    }

    public void setSellerBusinessName(String sellerBusinessName) {
        this.sellerBusinessName = sellerBusinessName;
    }

    public String getSellerAddress() {
        return sellerAddress;
    }

    public void setSellerAddress(String sellerAddress) {
        this.sellerAddress = sellerAddress;
    }

    public Long getSellerLedgerId() {
        return sellerLedgerId;
    }

    public void setSellerLedgerId(Long sellerLedgerId) {
        this.sellerLedgerId = sellerLedgerId;
    }

    public String getSellerState() {
        return sellerState;
    }

    public void setSellerState(String sellerState) {
        this.sellerState = sellerState;
    }

    public String getSellerPinCode() {
        return sellerPinCode;
    }

    public void setSellerPinCode(String sellerPinCode) {
        this.sellerPinCode = sellerPinCode;
    }

    public double getSellerLng() {
        return sellerLng;
    }

    public void setSellerLng(double sellerLng) {
        this.sellerLng = sellerLng;
    }

    public double getSellerLat() {
        return sellerLat;
    }

    public void setSellerLat(double sellerLat) {
        this.sellerLat = sellerLat;
    }

    public int getSellerStatus() {
        return sellerStatus;
    }

    public void setSellerStatus(int sellerStatus) {
        this.sellerStatus = sellerStatus;
    }

    public String getSellerCity() {
        return sellerCity;
    }

    public void setSellerCity(String sellerCity) {
        this.sellerCity = sellerCity;
    }

    public Long getSellerSourceId() {
        return sellerSourceId;
    }

    public void setSellerSourceId(Long sellerSourceId) {
        this.sellerSourceId = sellerSourceId;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }
}
