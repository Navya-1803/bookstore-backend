package com.bookstore.wishlist.controller;

import com.bookstore.user.entity.User;
import com.bookstore.user.repository.UserRepository;
import com.bookstore.wishlist.dto.WishlistResponse;
import com.bookstore.wishlist.service.WishlistService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;
    private final UserRepository userRepository;

    public WishlistController(
            WishlistService wishlistService,
            UserRepository userRepository
    ) {
        this.wishlistService = wishlistService;
        this.userRepository = userRepository;
    }

    // =========================================================
    // GET WISHLIST
    // =========================================================

    @GetMapping
    public ResponseEntity<WishlistResponse> getWishlist(
            Authentication authentication
    ) {

        User user = getAuthenticatedUser(authentication);

        WishlistResponse response =
                wishlistService.getWishlist(user);

        return ResponseEntity.ok(response);
    }

    // =========================================================
    // ADD TO WISHLIST
    // =========================================================

    @PostMapping("/add/{bookId}")
    public ResponseEntity<WishlistResponse> addToWishlist(
            @PathVariable Long bookId,
            Authentication authentication
    ) {

        User user = getAuthenticatedUser(authentication);

        WishlistResponse response =
                wishlistService.addToWishlist(
                        user,
                        bookId
                );

        return ResponseEntity.ok(response);
    }

    // =========================================================
    // REMOVE FROM WISHLIST
    // =========================================================

    @DeleteMapping("/remove/{bookId}")
    public ResponseEntity<WishlistResponse> removeFromWishlist(
            @PathVariable Long bookId,
            Authentication authentication
    ) {

        User user = getAuthenticatedUser(authentication);

        WishlistResponse response =
                wishlistService.removeFromWishlist(
                        user,
                        bookId
                );

        return ResponseEntity.ok(response);
    }

    // =========================================================
    // GET USER FROM JWT EMAIL
    // =========================================================

    private User getAuthenticatedUser(
            Authentication authentication
    ) {

        String email =
                authentication.getName();

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Authenticated user not found"
                        )
                );
    }
}