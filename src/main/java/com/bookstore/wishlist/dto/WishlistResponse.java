package com.bookstore.wishlist.dto;

import java.math.BigDecimal;
import java.util.List;

public class WishlistResponse {

    private Long wishlistId;
    private List<WishlistBookResponse> items;

    public WishlistResponse() {
    }

    public WishlistResponse(
            Long wishlistId,
            List<WishlistBookResponse> items
    ) {
        this.wishlistId = wishlistId;
        this.items = items;
    }

    public Long getWishlistId() {
        return wishlistId;
    }

    public void setWishlistId(Long wishlistId) {
        this.wishlistId = wishlistId;
    }

    public List<WishlistBookResponse> getItems() {
        return items;
    }

    public void setItems(List<WishlistBookResponse> items) {
        this.items = items;
    }

    public static class WishlistBookResponse {

        private Long bookId;
        private String title;
        private String author;
        private String description;
        private BigDecimal price;
        private Integer quantity;
        private String category;
        private String imageUrl;

        public WishlistBookResponse() {
        }

        public WishlistBookResponse(
                Long bookId,
                String title,
                String author,
                String description,
                BigDecimal price,
                Integer quantity,
                String category,
                String imageUrl
        ) {
            this.bookId = bookId;
            this.title = title;
            this.author = author;
            this.description = description;
            this.price = price;
            this.quantity = quantity;
            this.category = category;
            this.imageUrl = imageUrl;
        }

        public Long getBookId() {
            return bookId;
        }

        public void setBookId(Long bookId) {
            this.bookId = bookId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
    }
}