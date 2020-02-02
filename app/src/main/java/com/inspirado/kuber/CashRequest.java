package com.inspirado.kuber;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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
    double payableAmount;
    double incentive;
    double lndrLat;
    double lndrLng;
    double rcvrLat;
    double rcvrLng;
    User requestor;
    User lender;

    float lndrRating;
    float rcvrRating;

    String rcvrFeedback;
    String lndrFeedback;
    String clientCode;
    int requestType;
    boolean pickupServiceEnabled;
    String paymentSlot;
    String preferredPaymentMode;
    String rcvTransactionId;
    Long requesterId;
    Long lenderId;

    int status;
    Date requestDate;
    Date acceptanceDate;
    Date completionDate;
    String escalationId;
    String escalatedToId;
    String escalatedToName;
    String escalatedToAddress;
    String escalatedToMobileNumber;

    Date escalationRedressalDate;
    String escalationRedressalMode;
    String escalationRedressalId;
    String escalationRedressalTransactionId;
    String escalationRedressalStatus;
    List lenderOptions;

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

    public CashRequest(long id, double amount, Long requesterId, double payableAmount, int status,
                       String preferredPaymentMode, String paymentSlot, String paymentMode, String rcvTransactionId, double lenderDistance,
                       String lndrTransactionId) {
        super();
        this.id = id;
        this.amount = amount;
        this.requesterId = requesterId;
        this.payableAmount = payableAmount;
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

    public double getPayableAmount() {
        return payableAmount;
    }

    public boolean isPickupServiceEnabled() {
        return pickupServiceEnabled;
    }

    public void setPickupServiceEnabled(boolean pickupServiceEnabled) {
        this.pickupServiceEnabled = pickupServiceEnabled;
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

    public void setPayableAmount(double payableAmout) {
        this.payableAmount = payableAmout;
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

    public float getLndrRating() {
        return lndrRating;
    }

    public void setLndrRating(float lndrRating) {
        this.lndrRating = lndrRating;
    }

    public float getRcvrRating() {
        return rcvrRating;
    }

    public void setRcvrRating(float rcvrRating) {
        this.rcvrRating = rcvrRating;
    }

    public String getRcvrFeedback() {
        return rcvrFeedback;
    }

    public void setRcvrFeedback(String rcvrFeedback) {
        this.rcvrFeedback = rcvrFeedback;
    }

    public String getLndrFeedback() {
        return lndrFeedback;
    }

    public void setLndrFeedback(String lndrFeedback) {
        this.lndrFeedback = lndrFeedback;
    }

    public String getClientCode() {
        return clientCode;
    }

    public void setClientCode(String clientCode) {
        this.clientCode = clientCode;
    }

    public int getRequestType() {
        return requestType;
    }

    public void setRequestType(int requestType) {
        this.requestType = requestType;
    }

    public String getEscalationId() {
        return escalationId;
    }

    public void setEscalationId(String escalationId) {
        this.escalationId = escalationId;
    }

    public String getEscalatedToId() {
        return escalatedToId;
    }

    public void setEscalatedToId(String escalatedToId) {
        this.escalatedToId = escalatedToId;
    }

    public String getEscalatedToName() {
        return escalatedToName;
    }

    public void setEscalatedToName(String escalatedToName) {
        this.escalatedToName = escalatedToName;
    }

    public String getEscalatedToAddress() {
        return escalatedToAddress;
    }

    public void setEscalatedToAddress(String escalatedToAddress) {
        this.escalatedToAddress = escalatedToAddress;
    }

    public String getEscalatedToMobileNumber() {
        return escalatedToMobileNumber;
    }

    public void setEscalatedToMobileNumber(String escalatedToMobileNumber) {
        this.escalatedToMobileNumber = escalatedToMobileNumber;
    }

    public Date getEscalationRedressalDate() {
        return escalationRedressalDate;
    }

    public void setEscalationRedressalDate(Date escalationRedressalDate) {
        this.escalationRedressalDate = escalationRedressalDate;
    }

    public String getEscalationRedressalMode() {
        return escalationRedressalMode;
    }

    public void setEscalationRedressalMode(String escalationRedressalMode) {
        this.escalationRedressalMode = escalationRedressalMode;
    }

    public String getEscalationRedressalId() {
        return escalationRedressalId;
    }

    public void setEscalationRedressalId(String escalationRedressalId) {
        this.escalationRedressalId = escalationRedressalId;
    }

    public String getEscalationRedressalTransactionId() {
        return escalationRedressalTransactionId;
    }

    public void setEscalationRedressalTransactionId(String escalationRedressalTransactionId) {
        this.escalationRedressalTransactionId = escalationRedressalTransactionId;
    }

    public String getEscalationRedressalStatus() {
        return escalationRedressalStatus;
    }

    public void setEscalationRedressalStatus(String escalationRedressalStatus) {
        this.escalationRedressalStatus = escalationRedressalStatus;
    }

    public List getLenderOptions() {
        return lenderOptions;
    }

    public void setLenderOptions(List lenderOptions) {
        this.lenderOptions = lenderOptions;
    }
}
