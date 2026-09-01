package com.bookstore.cart.controller;

import com.bookstore.cart.dto.AddToCartRequest;
import com.bookstore.cart.dto.CartResponse;
import com.bookstore.cart.dto.UpdateCartRequest;
import com.bookstore.cart.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // ---------------------------------------------------------
    // GET CART
    // ---------------------------------------------------------

    @GetMapping
    public ResponseEntity<CartResponse> getCart(
            Authentication authentication
    ) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                cartService.getCart(email)
        );
    }

    // ---------------------------------------------------------
    // ADD TO CART
    // ---------------------------------------------------------

    @PostMapping("/add")
    public ResponseEntity<CartResponse> addToCart(
            Authentication authentication,
            @Valid @RequestBody AddToCartRequest request
    ) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                cartService.addToCart(email, request)
        );
    }

    // ---------------------------------------------------------
    // UPDATE CART
    // ---------------------------------------------------------

    @PutMapping("/update")
    public ResponseEntity<CartResponse> updateCart(
            Authentication authentication,
            @Valid @RequestBody UpdateCartRequest request
    ) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                cartService.updateCart(email, request)
        );
    }

    // ---------------------------------------------------------
    // REMOVE ITEM
    // ---------------------------------------------------------

    @DeleteMapping("/remove/{bookId}")
    public ResponseEntity<CartResponse> removeFromCart(
            Authentication authentication,
            @PathVariable Long bookId
    ) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                cartService.removeFromCart(email, bookId)
        );
    }

    // ---------------------------------------------------------
    // CLEAR CART
    // ---------------------------------------------------------

    @DeleteMapping("/clear")
    public ResponseEntity<String> clearCart(
            Authentication authentication
    ) {

        String email = authentication.getName();

        cartService.clearCart(email);

        return ResponseEntity.ok(
                "Cart cleared successfully"
        );
    }
}