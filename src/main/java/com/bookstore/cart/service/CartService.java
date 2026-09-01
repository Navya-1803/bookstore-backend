package com.bookstore.cart.service;

import com.bookstore.book.entity.Book;
import com.bookstore.book.repository.BookRepository;
import com.bookstore.cart.dto.AddToCartRequest;
import com.bookstore.cart.dto.CartItemResponse;
import com.bookstore.cart.dto.CartResponse;
import com.bookstore.cart.dto.UpdateCartRequest;
import com.bookstore.cart.entity.Cart;
import com.bookstore.cart.entity.CartItem;
import com.bookstore.cart.repository.CartItemRepository;
import com.bookstore.cart.repository.CartRepository;
import com.bookstore.user.entity.User;
import com.bookstore.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public CartService(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            BookRepository bookRepository,
            UserRepository userRepository
    ) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    // ---------------------------------------------------------
    // GET CART
    // ---------------------------------------------------------

    public CartResponse getCart(String email) {

        User user = getUserByEmail(email);

        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> createCart(user));

        return convertToResponse(cart);
    }

    // ---------------------------------------------------------
    // ADD TO CART
    // ---------------------------------------------------------

    @Transactional
    public CartResponse addToCart(
            String email,
            AddToCartRequest request
    ) {

        User user = getUserByEmail(email);

        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() ->
                        new RuntimeException("Book not found")
                );

        if (request.getQuantity() > book.getQuantity()) {
            throw new RuntimeException(
                    "Requested quantity exceeds available stock"
            );
        }

        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> createCart(user));

        CartItem cartItem = cartItemRepository
                .findByCartAndBook(cart, book)
                .orElse(null);

        if (cartItem != null) {

            int newQuantity =
                    cartItem.getQuantity() + request.getQuantity();

            if (newQuantity > book.getQuantity()) {
                throw new RuntimeException(
                        "Requested quantity exceeds available stock"
                );
            }

            cartItem.setQuantity(newQuantity);

        } else {

            cartItem = new CartItem(
                    cart,
                    book,
                    request.getQuantity(),
                    book.getPrice()
            );

            cart.getItems().add(cartItem);
        }

        cartItemRepository.save(cartItem);

        recalculateTotal(cart);

        cartRepository.save(cart);

        return convertToResponse(cart);
    }

    // ---------------------------------------------------------
    // UPDATE QUANTITY
    // ---------------------------------------------------------

    @Transactional
    public CartResponse updateCart(
            String email,
            UpdateCartRequest request
    ) {

        User user = getUserByEmail(email);

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() ->
                        new RuntimeException("Cart not found")
                );

        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() ->
                        new RuntimeException("Book not found")
                );

        if (request.getQuantity() > book.getQuantity()) {
            throw new RuntimeException(
                    "Requested quantity exceeds available stock"
            );
        }

        CartItem cartItem = cartItemRepository
                .findByCartAndBook(cart, book)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Book is not present in cart"
                        )
                );

        cartItem.setQuantity(request.getQuantity());

        cartItemRepository.save(cartItem);

        recalculateTotal(cart);

        cartRepository.save(cart);

        return convertToResponse(cart);
    }

    // ---------------------------------------------------------
    // REMOVE ITEM
    // ---------------------------------------------------------

    @Transactional
    public CartResponse removeFromCart(
            String email,
            Long bookId
    ) {

        User user = getUserByEmail(email);

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() ->
                        new RuntimeException("Cart not found")
                );

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() ->
                        new RuntimeException("Book not found")
                );

        CartItem cartItem = cartItemRepository
                .findByCartAndBook(cart, book)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Book is not present in cart"
                        )
                );

        cart.getItems().remove(cartItem);

        cartItemRepository.delete(cartItem);

        recalculateTotal(cart);

        cartRepository.save(cart);

        return convertToResponse(cart);
    }

    // ---------------------------------------------------------
    // CLEAR CART
    // ---------------------------------------------------------

    @Transactional
    public void clearCart(String email) {

        User user = getUserByEmail(email);

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() ->
                        new RuntimeException("Cart not found")
                );

        cart.getItems().clear();

        cart.setTotalAmount(BigDecimal.ZERO);

        cartRepository.save(cart);
    }

    // ---------------------------------------------------------
    // CREATE CART
    // ---------------------------------------------------------

    private Cart createCart(User user) {

        Cart cart = new Cart(user);

        cart.setTotalAmount(BigDecimal.ZERO);

        return cartRepository.save(cart);
    }

    // ---------------------------------------------------------
    // RECALCULATE TOTAL
    // ---------------------------------------------------------

    private void recalculateTotal(Cart cart) {

        BigDecimal total = cart.getItems()
                .stream()
                .map(item ->
                        item.getUnitPrice()
                                .multiply(
                                        BigDecimal.valueOf(
                                                item.getQuantity()
                                        )
                                )
                )
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );

        cart.setTotalAmount(total);
    }

    // ---------------------------------------------------------
    // CONVERT ENTITY → DTO
    // ---------------------------------------------------------

    private CartResponse convertToResponse(Cart cart) {

        List<CartItemResponse> items =
                cart.getItems()
                        .stream()
                        .map(item -> {

                            Book book = item.getBook();

                            BigDecimal subtotal =
                                    item.getUnitPrice()
                                            .multiply(
                                                    BigDecimal.valueOf(
                                                            item.getQuantity()
                                                    )
                                            );

                            return new CartItemResponse(
                                    book.getId(),
                                    book.getTitle(),
                                    book.getAuthor(),
                                    item.getUnitPrice(),
                                    item.getQuantity(),
                                    subtotal,
                                    book.getImageUrl()
                            );
                        })
                        .toList();

        return new CartResponse(
                cart.getId(),
                items,
                cart.getTotalAmount()
        );
    }

    // ---------------------------------------------------------
    // FIND USER
    // ---------------------------------------------------------

    private User getUserByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );
    }
}