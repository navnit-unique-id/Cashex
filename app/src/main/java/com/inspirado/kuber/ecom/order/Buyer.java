package com.inspirado.kuber.ecom.order;

import com.inspirado.kuber.User;
import com.inspirado.kuber.ecom.order.inventory.Inventory;
import com.inspirado.kuber.ecom.store.Store;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */

/**
 * @author navnit
 *
 */
public class Buyer implements Serializable{

    String buyerName;
    String buyerMobileNumber;
    String buyerEmail;
    String buyerBusinessName;
    String buyerAddress;
    String buyerState;
    String buyerPinCode;
    double buyerLng;
    double buyerLat;
    String buyerCity;
    Long buyerSourceId;

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getBuyerMobileNumber() {
        return buyerMobileNumber;
    }

    public void setBuyerMobileNumber(String buyerMobileNumber) {
        this.buyerMobileNumber = buyerMobileNumber;
    }

    public String getBuyerEmail() {
        return buyerEmail;
    }

    public void setBuyerEmail(String buyerEmail) {
        this.buyerEmail = buyerEmail;
    }

    public String getBuyerBusinessName() {
        return buyerBusinessName;
    }

    public void setBuyerBusinessName(String buyerBusinessName) {
        this.buyerBusinessName = buyerBusinessName;
    }

    public String getBuyerAddress() {
        return buyerAddress;
    }

    public void setBuyerAddress(String buyerAddress) {
        this.buyerAddress = buyerAddress;
    }

    public String getBuyerState() {
        return buyerState;
    }

    public void setBuyerState(String buyerState) {
        this.buyerState = buyerState;
    }

    public String getBuyerPinCode() {
        return buyerPinCode;
    }

    public void setBuyerPinCode(String buyerPinCode) {
        this.buyerPinCode = buyerPinCode;
    }

    public double getBuyerLng() {
        return buyerLng;
    }

    public void setBuyerLng(double buyerLng) {
        this.buyerLng = buyerLng;
    }

    public double getBuyerLat() {
        return buyerLat;
    }

    public void setBuyerLat(double buyerLat) {
        this.buyerLat = buyerLat;
    }

    public String getBuyerCity() {
        return buyerCity;
    }

    public void setBuyerCity(String buyerCity) {
        this.buyerCity = buyerCity;
    }

    public Long getBuyerSourceId() {
        return buyerSourceId;
    }

    public void setBuyerSourceId(Long buyerSourceId) {
        this.buyerSourceId = buyerSourceId;
    }
}
