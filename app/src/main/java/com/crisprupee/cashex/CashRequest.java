package com.crisprupee.cashex;

import java.io.Serializable;
import java.util.Date;

/**
 *
 */

/**
 * @author navnit
 *
 */
public class CashRequest  implements Serializable {

    double amount;
    long id;
    double lenderDistance;
    String lndrTransactionId;
    String rcvrTransactionId;

    String lndrPaymentMode;
    String rcvrPaymentMode;
    double payableAmout;
    double incentive;
    double lndrLat;
    double lndrLng;
    double rcvrLat;
    double rcvrLng;
    User requestor;
    User lender;

    public String getRcvrTransactionId() {
        return rcvrTransactionId;
    }

    public void setRcvrTransactionId(String rcvrTransactionId) {
        this.rcvrTransactionId = rcvrTransactionId;
    }

    public String getRcvrPaymentMode() {
        return rcvrPaymentMode;
    }

    public void setRcvrPaymentMode(String rcvrPaymentMode) {
        this.rcvrPaymentMode = rcvrPaymentMode;
    }

    String paymentSlot;
    String preferredPaymentMode;
    String rcvTransactionId;
    Long requesterId;
    Long lenderId;

    int status;
    Date requestDate;
    Date acceptanceDate;
    Date completionDate;

    public Date getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(Date completionDate) {
        this.completionDate = completionDate;
    }

    public CashRequest() {
        super();
        this.id = id;
    }

    public CashRequest(long id) {
        super();
        this.id = id;
    }

    public double getIncentive() {
        return incentive;
    }

    public void setIncentive(double incentive) {
        this.incentive = incentive;
    }

    public User getLender() {
        return lender;
    }

    public void setLender(User lender) {
        this.lender = lender;
    }

    public CashRequest(long id, double amount, Long requesterId, double payableAmout, int status,
                       String preferredPaymentMode, String paymentSlot, String paymentMode, String rcvTransactionId, double lenderDistance,
                       String lndrTransactionId) {
        super();
        this.id = id;
        this.amount = amount;
        this.requesterId = requesterId;
        this.payableAmout = payableAmout;
        this.status = status;
        this.preferredPaymentMode = preferredPaymentMode;
        this.paymentSlot = paymentSlot;
        this.lndrPaymentMode = paymentMode;
        this.rcvTransactionId = rcvTransactionId;
        this.lenderDistance = lenderDistance;
        this.lndrTransactionId = lndrTransactionId;
    }

    public Date getAcceptanceDate() {
        return acceptanceDate;
    }

    public void setAcceptanceDate(Date acceptanceDate) {
        this.acceptanceDate = acceptanceDate;
    }

    public double getAmount() {
        return amount;
    }

    public long getId() {
        return id;
    }

    public double getLenderDistance() {
        return lenderDistance;
    }

    public String getLndrTransactionId() {
        return lndrTransactionId;
    }

    public double getPayableAmout() {
        return payableAmout;
    }



    public String getLndrPaymentMode() {
        return lndrPaymentMode;
    }

    public String getPaymentSlot() {
        return paymentSlot;
    }

    public String getPreferredPaymentMode() {
        return preferredPaymentMode;
    }

    public String getRcvTransactionId() {
        return rcvTransactionId;
    }

    public Long getRequesterId() {
        return requesterId;
    }

    public int getStatus() {
        return status;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setLenderDistance(double lenderDistance) {
        this.lenderDistance = lenderDistance;
    }

    public void setLndrTransactionId(String lndrTransactionId) {
        this.lndrTransactionId = lndrTransactionId;
    }

    public void setPayableAmout(double payableAmout) {
        this.payableAmout = payableAmout;
    }

    public void setLndrPaymentMode(String lndrPaymentMode) {
        this.lndrPaymentMode = lndrPaymentMode;
    }

    public void setPaymentSlot(String paymentSlot) {
        this.paymentSlot = paymentSlot;
    }

    public void setPreferredPaymentMode(String preferredPaymentMode) {
        this.preferredPaymentMode = preferredPaymentMode;
    }

    public void setRcvTransactionId(String rcvTransactionId) {
        this.rcvTransactionId = rcvTransactionId;
    }

    public void setRequesterId(Long requesterId) {
        this.requesterId = requesterId;
    }

    public void setStatus(int status) {
        this.status = status;
    }


    public Date getRequestDate() {
        return requestDate;
    }
    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public Long getLenderId() {
        return lenderId;
    }
    public void setLenderId(Long lenderId) {
        this.lenderId = lenderId;
    }

    public User getRequestor() {
        return requestor;
    }

    public void setRequestor(User requestor) {
        this.requestor = requestor;
    }

    public double getLndrLat() {
        return lndrLat;
    }

    public void setLndrLat(double lndrLat) {
        this.lndrLat = lndrLat;
    }

    public double getLndrLng() {
        return lndrLng;
    }

    public void setLndrLng(double lndrLng) {
        this.lndrLng = lndrLng;
    }

    public double getRcvrLat() {
        return rcvrLat;
    }

    public void setRcvrLat(double rcvrLat) {
        this.rcvrLat = rcvrLat;
    }

    public double getRcvrLng() {
        return rcvrLng;
    }

    public void setRcvrLng(double rcvrLng) {
        this.rcvrLng = rcvrLng;
    }
}
