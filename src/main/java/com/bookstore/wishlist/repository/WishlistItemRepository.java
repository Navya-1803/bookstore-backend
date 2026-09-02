package com.bookstore.wishlist.repository;

import com.bookstore.wishlist.entity.Wishlist;
import com.bookstore.wishlist.entity.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {

    Optional<WishlistItem> findByWishlistAndBookId(
            Wishlist wishlist,
            Long bookId
    );

    void deleteByWishlistAndBookId(
            Wishlist wishlist,
            Long bookId
    );

}