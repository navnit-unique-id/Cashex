package com.inspirado.kuber.ecom.store;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Membership implements Serializable {

    private Long id;
    private Long applicablePlanHistoryId;
    private int validity;
    private int validityUnit;
    private Date psd;
    private Date ped;
    private Date msd;
    private Date med;
    private int cover;
    private String details;
    private int planType;
    private List<Privilege> privileges;
    private int status; // 1--> active, 11--> deactivated
    private int intent;// //1--> update membership, 2-> updated Plan
    private String summary;
    private double net;
    private double tax1Percent;
    private double tax2Percent;
    private double tax3Percent;
    private double tax1;
    private double tax2;
    private double tax3;
    private double tax;
    private double discount;
    private double discountPercent;
    private double postDiscount;
    private double gross;
    private String comments;
    private Long primaryCustomerId;
    private List<PlanHistory> planHistories;
    String orgChain;
    private Long planId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getApplicablePlanHistoryId() {
        return applicablePlanHistoryId;
    }

    public void setApplicablePlanHistoryId(Long applicablePlanHistoryId) {
        this.applicablePlanHistoryId = applicablePlanHistoryId;
    }

    public int getValidity() {
        return validity;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public void setValidity(int validity) {
        this.validity = validity;
    }

    public int getValidityUnit() {
        return validityUnit;
    }

    public void setValidityUnit(int validityUnit) {
        this.validityUnit = validityUnit;
    }

    public Date getPsd() {
        return psd;
    }

    public void setPsd(Date psd) {
        this.psd = psd;
    }

    public Date getPed() {
        return ped;
    }

    public void setPed(Date ped) {
        this.ped = ped;
    }

    public Date getMsd() {
        return msd;
    }

    public void setMsd(Date msd) {
        this.msd = msd;
    }

    public String getOrgChain() {
        return orgChain;
    }

    public void setOrgChain(String orgChain) {
        this.orgChain = orgChain;
    }

    public Date getMed() {
        return med;
    }

    public void setMed(Date med) {
        this.med = med;
    }

    public int getCover() {
        return cover;
    }

    public void setCover(int cover) {
        this.cover = cover;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public int getPlanType() {
        return planType;
    }

    public void setPlanType(int planType) {
        this.planType = planType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getIntent() {
        return intent;
    }

    public void setIntent(int intent) {
        this.intent = intent;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public double getNet() {
        return net;
    }

    public void setNet(double net) {
        this.net = net;
    }

    public double getTax1Percent() {
        return tax1Percent;
    }

    public void setTax1Percent(double tax1Percent) {
        this.tax1Percent = tax1Percent;
    }

    public double getTax2Percent() {
        return tax2Percent;
    }

    public void setTax2Percent(double tax2Percent) {
        this.tax2Percent = tax2Percent;
    }

    public double getTax3Percent() {
        return tax3Percent;
    }

    public void setTax3Percent(double tax3Percent) {
        this.tax3Percent = tax3Percent;
    }

    public double getTax1() {
        return tax1;
    }

    public void setTax1(double tax1) {
        this.tax1 = tax1;
    }

    public double getTax2() {
        return tax2;
    }

    public void setTax2(double tax2) {
        this.tax2 = tax2;
    }

    public double getTax3() {
        return tax3;
    }

    public void setTax3(double tax3) {
        this.tax3 = tax3;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(double discountPercent) {
        this.discountPercent = discountPercent;
    }

    public double getPostDiscount() {
        return postDiscount;
    }

    public void setPostDiscount(double postDiscount) {
        this.postDiscount = postDiscount;
    }

    public double getGross() {
        return gross;
    }

    public void setGross(double gross) {
        this.gross = gross;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Long getPrimaryCustomerId() {
        return primaryCustomerId;
    }

    public void setPrimaryCustomerId(Long primaryCustomerId) {
        this.primaryCustomerId = primaryCustomerId;
    }

    public List<PlanHistory> getPlanHistories() {
        return planHistories;
    }

    public void setPlanHistories(List<PlanHistory> planHistories) {
        this.planHistories = planHistories;
    }

    public List<Privilege> getPrivileges() {
        return privileges;
    }

    public void setPrivileges(List<Privilege> privileges) {
        this.privileges = privileges;
    }
}
