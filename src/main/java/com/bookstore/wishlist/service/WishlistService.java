package com.bookstore.wishlist.service;

import com.bookstore.book.entity.Book;
import com.bookstore.book.repository.BookRepository;
import com.bookstore.user.entity.User;
import com.bookstore.wishlist.dto.WishlistResponse;
import com.bookstore.wishlist.entity.Wishlist;
import com.bookstore.wishlist.entity.WishlistItem;
import com.bookstore.wishlist.repository.WishlistItemRepository;
import com.bookstore.wishlist.repository.WishlistRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;
    private final BookRepository bookRepository;

    public WishlistService(
            WishlistRepository wishlistRepository,
            WishlistItemRepository wishlistItemRepository,
            BookRepository bookRepository
    ) {
        this.wishlistRepository = wishlistRepository;
        this.wishlistItemRepository = wishlistItemRepository;
        this.bookRepository = bookRepository;
    }

    // =========================================================
    // GET USER WISHLIST
    // =========================================================

    @Transactional
    public WishlistResponse getWishlist(User user) {

        Wishlist wishlist =
                wishlistRepository
                        .findByUser(user)
                        .orElseGet(() -> {

                            Wishlist newWishlist =
                                    new Wishlist(user);

                            return wishlistRepository.save(
                                    newWishlist
                            );
                        });

        return convertToResponse(wishlist);
    }

    // =========================================================
    // ADD BOOK TO WISHLIST
    // =========================================================

    @Transactional
    public WishlistResponse addToWishlist(
            User user,
            Long bookId
    ) {

        Book book =
                bookRepository.findById(bookId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Book not found"
                                )
                        );

        Wishlist wishlist =
                wishlistRepository
                        .findByUser(user)
                        .orElseGet(() -> {

                            Wishlist newWishlist =
                                    new Wishlist(user);

                            return wishlistRepository.save(
                                    newWishlist
                            );
                        });

        // Check whether book already exists
        boolean alreadyExists =
                wishlistItemRepository
                        .findByWishlistAndBookId(
                                wishlist,
                                bookId
                        )
                        .isPresent();

        if (!alreadyExists) {

            WishlistItem item =
                    new WishlistItem(
                            wishlist,
                            book
                    );

            wishlist.getItems().add(item);

            wishlistItemRepository.save(item);
        }

        return convertToResponse(wishlist);
    }

    // =========================================================
    // REMOVE BOOK FROM WISHLIST
    // =========================================================

    @Transactional
    public WishlistResponse removeFromWishlist(
            User user,
            Long bookId
    ) {

        Wishlist wishlist =
                wishlistRepository
                        .findByUser(user)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Wishlist not found"
                                )
                        );

        wishlistItemRepository.deleteByWishlistAndBookId(
                wishlist,
                bookId
        );

        return convertToResponse(wishlist);
    }

    // =========================================================
    // CONVERT ENTITY → DTO
    // =========================================================

    private WishlistResponse convertToResponse(
            Wishlist wishlist
    ) {

        List<WishlistResponse.WishlistBookResponse> items =
                wishlist.getItems()
                        .stream()
                        .map(item -> {

                            Book book = item.getBook();

                            return new WishlistResponse.WishlistBookResponse(
                                    book.getId(),
                                    book.getTitle(),
                                    book.getAuthor(),
                                    book.getDescription(),
                                    book.getPrice(),
                                    book.getQuantity(),
                                    book.getCategory(),
                                    book.getImageUrl()
                            );

                        })
                        .toList();

        return new WishlistResponse(
                wishlist.getId(),
                items
        );
    }
}