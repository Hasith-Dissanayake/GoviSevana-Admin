package lk.javainstitute.govisevana_admin.model;

import java.util.List;

public class OrderModel {
    private String orderId;
    private String farmerId;
    private String userPhone;
    private String fullName;
    private String city;
    private String address;
    private double totalAmount;
    private String status;
    private long timestamp;
    private String trackingNumber;
    private String paymentStatus;  // "Paid" or "Unpaid"


    public OrderModel() {
        // Required empty constructor for Firebase
    }

    public OrderModel(String orderId, String farmerId, String userPhone, String fullName, String city, String address,
                      double totalAmount, String status, long timestamp, String trackingNumber, String paymentStatus
                     ) {
        this.orderId = orderId;
        this.farmerId = farmerId;
        this.userPhone = userPhone;
        this.fullName = fullName;
        this.city = city;
        this.address = address;
        this.totalAmount = totalAmount;
        this.status = status;
        this.timestamp = timestamp;
        this.trackingNumber = trackingNumber;
        this.paymentStatus = paymentStatus;

    }

    public String getOrderId() { return orderId; }
    public String getFarmerId() { return farmerId; }
    public String getUserPhone() { return userPhone; }
    public String getFullName() { return fullName; }
    public String getCity() { return city; }
    public String getAddress() { return address; }
    public double getTotalAmount() { return totalAmount; }
    public String getStatus() { return status; }
    public long getTimestamp() { return timestamp; }
    public String getTrackingNumber() { return trackingNumber; }
    public String getPaymentStatus() { return paymentStatus; }


    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
}
