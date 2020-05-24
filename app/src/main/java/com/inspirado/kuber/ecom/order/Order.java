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
public class Order implements Serializable {

    Long id;
    Seller seller;
    Buyer buyer;
    List<OrderItem> orderItems;
    Date dateOfOrder;
    String shippingAddress;
    double shippingLat;
    double shippingLng;
    double totalAmount;
    double deliveryCharge;
    double tax;
    double grossAmount;
    double status;
    String orgChain;
    private int totalQuantity;
    int mop;
    String extAttr1Name;
    String extAttr1Value;
    String extAttr2Name;
    String extAttr2Value;
    String extAttr3Name;
    String extAttr3Value;
    double sellerScore;
    String sellerFeedback;
    double buyerScore;
    String buyerFeedback;
    int paymentStatus;



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Seller getStore() {
        return seller;
    }

    public void setStore(Seller store) {
        this.seller = store;
    }

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    public double getShippingLat() {
        return shippingLat;
    }

    public void setShippingLat(double shippingLat) {
        this.shippingLat = shippingLat;
    }

    public double getShippingLng() {
        return shippingLng;
    }

    public void setShippingLng(double shippingLng) {
        this.shippingLng = shippingLng;
    }

    public String getOrgChain() {
        return orgChain;
    }

    public double getSellerScore() {
        return sellerScore;
    }

    public int getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(int paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public void setSellerScore(double sellerScore) {
        this.sellerScore = sellerScore;
    }

    public String getSellerFeedback() {
        return sellerFeedback;
    }

    public void setSellerFeedback(String sellerFeedback) {
        this.sellerFeedback = sellerFeedback;
    }

    public double getBuyerFeebackScore() {
        return buyerScore;
    }

    public void setBuyerFeebackScore(double buyerFeebackScore) {
        this.buyerScore = buyerFeebackScore;
    }

    public String getBuyerFeedback() {
        return buyerFeedback;
    }

    public void setBuyerFeedback(String buyerFeedback) {
        this.buyerFeedback = buyerFeedback;
    }

    public void setOrgChain(String orgChain) {
        this.orgChain = orgChain;
    }

    public String getExtAttr1Name() {
        return extAttr1Name;
    }

    public String getExtAttr1Value() {
        return extAttr1Value;
    }

    public void setExtAttr1Value(String extAttr1Value) {
        this.extAttr1Value = extAttr1Value;
    }

    public void setExtAttr1Name(String extAttr1Name) {
        extAttr1Name = extAttr1Name;
    }

    public Buyer getBuyer() {
        return buyer;
    }

    public int getMop() {
        return mop;
    }

    public void setMop(int mop) {
        this.mop = mop;
    }

    public String getExtAttr2Name() {
        return extAttr2Name;
    }

    public void setExtAttr2Name(String extAttr2Name) {
        this.extAttr2Name = extAttr2Name;
    }

    public String getExtAttr2Value() {
        return extAttr2Value;
    }

    public void setExtAttr2Value(String extAttr2Value) {
        this.extAttr2Value = extAttr2Value;
    }

    public String getExtAttr3Name() {
        return extAttr3Name;
    }

    public void setExtAttr3Name(String extAttr3Name) {
        this.extAttr3Name = extAttr3Name;
    }

    public String getExtAttr3Value() {
        return extAttr3Value;
    }

    public void setExtAttr3Value(String extAttr3Value) {
        this.extAttr3Value = extAttr3Value;
    }

    public void setBuyer(Buyer buyer) {
        this.buyer = buyer;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public Date getDateOfOrder() {
        return dateOfOrder;
    }

    public void setDateOfOrder(Date dateOfOrder) {
        this.dateOfOrder = dateOfOrder;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getDeliveryCharge() {
        return deliveryCharge;
    }

    public void setDeliveryCharge(double deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public double getGrossAmount() {
        return grossAmount;
    }

    public void setGrossAmount(double grossAmount) {
        this.grossAmount = grossAmount;
    }

    public double getStatus() {
        return status;
    }

    public void setStatus(double status) {
        this.status = status;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public void addItemAsPerInventory(Inventory inventory, int quantity) {
        OrderItem orderItem = getMatchingOrderItemForInventory(inventory);
        if (orderItem == null) {
            orderItem = new OrderItem();
            if (this.orderItems == null) this.orderItems = new ArrayList<OrderItem>();
            this.orderItems.add(orderItem);
        }
        orderItem.setInventoryId(inventory.getId());
        orderItem.setBrand(inventory.getBrand());
        orderItem.setDescription(inventory.getDescription());
        orderItem.setLocationId(inventory.getLocationId());
        orderItem.setMrp(inventory.getMrp());
        orderItem.setName(inventory.getName());
        orderItem.setOrgChain(inventory.getOrgChain());
        orderItem.setPrice(inventory.getPrice());
        orderItem.setQuantity(quantity);
        orderItem.setUom(inventory.getUom());
        orderItem.setMaxQuantity(inventory.getQuantity());
        calculateSummary();
    }


    public void removeItemAsPerInventory(Inventory inventory, int quantity) {
        OrderItem orderItem = getMatchingOrderItemForInventory(inventory);
        if (orderItem.getQuantity() == 1) {
            this.orderItems.remove(orderItem);
        } else {
            orderItem.setInventoryId(inventory.getId());
            orderItem.setBrand(inventory.getBrand());
            orderItem.setDescription(inventory.getDescription());
            orderItem.setLocationId(inventory.getLocationId());
            orderItem.setMrp(inventory.getMrp());
            orderItem.setName(inventory.getName());
            orderItem.setOrgChain(inventory.getOrgChain());
            orderItem.setPrice(inventory.getPrice());
            orderItem.setQuantity(quantity);
            orderItem.setUom(inventory.getUom());
            orderItem.setMaxQuantity(inventory.getQuantity());
        }
        calculateSummary();
    }

    private OrderItem getMatchingOrderItemForInventory(Inventory inventory) {
        if (this.orderItems == null) return null;
        OrderItem orderItem = null;
        List<OrderItem> orderItems = this.orderItems.stream().filter(orderItum -> {
            if (orderItum.getInventoryId().equals(inventory.getId())) {
                return true;
            }
            return false;
        }).collect(Collectors.toList());

        if (orderItems.size() >= 1) {
            orderItem = orderItems.get(0);
        }
        return orderItem;
    }

    public void calculateSummary() {
        this.grossAmount = 0;
        this.totalQuantity = 0;
        this.totalAmount = 0;
        if(this.orderItems==null) return;
        Iterator itr = this.orderItems.iterator();

        while (itr.hasNext()) {
            OrderItem orderItem = (OrderItem) itr.next();
            this.grossAmount = this.grossAmount + orderItem.getPrice()*orderItem.getQuantity();
            this.totalAmount = this.totalAmount + orderItem.getPrice()*orderItem.getQuantity();
            this.totalQuantity = this.totalQuantity + orderItem.getQuantity();
        }
        this.grossAmount = this.grossAmount + this.deliveryCharge;

    }

    public void updateOrderItem(OrderItem orderItem) {
        if(this.orderItems==null) return;
        List<OrderItem> orderItemsTemp = this.orderItems.stream().map(orderItem1   -> {
            if(orderItem1.getInventoryId().equals(orderItem.getInventoryId())){
                return orderItem;
            }else{
                return orderItem1;
            }
        } ).collect(Collectors.toList());
        this.orderItems=orderItemsTemp;
        calculateSummary();
    }

    public void removeOrderItem(OrderItem orderItem) {
        List<OrderItem> orderItemsTemp = this.orderItems.stream().filter(orderItem1   -> {
            if(orderItem.getInventoryId().equals(orderItem1.getInventoryId())){
                return false;
            }else{
                return true;
            }
        } ).collect(Collectors.toList());
        this.orderItems=orderItemsTemp;
        calculateSummary();
    }
}
