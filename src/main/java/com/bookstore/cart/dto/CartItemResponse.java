package com.bookstore.cart.dto;

import java.math.BigDecimal;

public class CartItemResponse {

    private Long bookId;
    private String title;
    private String author;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal subtotal;
    private String imageUrl;

    public CartItemResponse() {
    }

    public CartItemResponse(
            Long bookId,
            String title,
            String author,
            BigDecimal unitPrice,
            Integer quantity,
            BigDecimal subtotal,
            String imageUrl
    ) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.subtotal = subtotal;
        this.imageUrl = imageUrl;
    }

    public Long getBookId() {
        return bookId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}