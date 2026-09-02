package com.bookstore.order.dto;

import com.bookstore.order.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderResponse {

    private Long orderId;

    private Long userId;

    private String userName;

    private String userEmail;

    private LocalDateTime orderDate;

    private OrderStatus status;

    private BigDecimal totalAmount;

    private String phoneNumber;

    private String houseNo;

    private String street;

    private String city;

    private String state;

    private String pincode;

    private String country;

    private String deliveryPreference;

    private List<OrderItemResponse> items;

    public OrderResponse() {
    }

    public OrderResponse(
            Long orderId,
            Long userId,
            String userName,
            String userEmail,
            LocalDateTime orderDate,
            OrderStatus status,
            BigDecimal totalAmount,
            String phoneNumber,
            String houseNo,
            String street,
            String city,
            String state,
            String pincode,
            String country,
            String deliveryPreference,
            List<OrderItemResponse> items
    ) {
        this.orderId = orderId;
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.orderDate = orderDate;
        this.status = status;
        this.totalAmount = totalAmount;
        this.phoneNumber = phoneNumber;
        this.houseNo = houseNo;
        this.street = street;
        this.city = city;
        this.state = state;
        this.pincode = pincode;
        this.country = country;
        this.deliveryPreference = deliveryPreference;
        this.items = items;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getHouseNo() {
        return houseNo;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getPincode() {
        return pincode;
    }

    public String getCountry() {
        return country;
    }

    public String getDeliveryPreference() {
        return deliveryPreference;
    }

    public List<OrderItemResponse> getItems() {
        return items;
    }
}