package com.inspirado.kuber.ecom.store;

import java.io.Serializable;
import java.util.Date;

public class PlanHistory implements Serializable {

    private Long id;
    private Long invoiceId;
    private String invoiceDisplayId;
    double invoiceNet;
    double invoicetax;
    double invoiceGross;
    Date invoiceDate;
    Long membershipId;
    Date psd;
    Date ped;
    int planType;
    String details;
    String tranId;
    int status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getInvoiceDisplayId() {
        return invoiceDisplayId;
    }

    public void setInvoiceDisplayId(String invoiceDisplayId) {
        this.invoiceDisplayId = invoiceDisplayId;
    }

    public double getInvoiceNet() {
        return invoiceNet;
    }

    public void setInvoiceNet(double invoiceNet) {
        this.invoiceNet = invoiceNet;
    }

    public double getInvoicetax() {
        return invoicetax;
    }

    public void setInvoicetax(double invoicetax) {
        this.invoicetax = invoicetax;
    }

    public double getInvoiceGross() {
        return invoiceGross;
    }

    public void setInvoiceGross(double invoiceGross) {
        this.invoiceGross = invoiceGross;
    }

    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public Long getMembershipId() {
        return membershipId;
    }

    public void setMembershipId(Long membershipId) {
        this.membershipId = membershipId;
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

    public int getPlanType() {
        return planType;
    }

    public void setPlanType(int planType) {
        this.planType = planType;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getTranId() {
        return tranId;
    }

    public void setTranId(String tranId) {
        this.tranId = tranId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
