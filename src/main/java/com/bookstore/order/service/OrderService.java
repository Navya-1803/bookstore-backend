package com.bookstore.order.service;

import com.bookstore.book.entity.Book;
import com.bookstore.book.repository.BookRepository;

import com.bookstore.cart.entity.Cart;
import com.bookstore.cart.entity.CartItem;
import com.bookstore.cart.repository.CartRepository;

import com.bookstore.customer.entity.Address;
import com.bookstore.customer.entity.CustomerProfile;
import com.bookstore.customer.repository.CustomerProfileRepository;

import com.bookstore.order.dto.OrderItemResponse;
import com.bookstore.order.dto.OrderResponse;
import com.bookstore.order.entity.Order;
import com.bookstore.order.entity.OrderItem;
import com.bookstore.order.entity.OrderStatus;
import com.bookstore.order.repository.OrderRepository;

import com.bookstore.user.entity.User;
import com.bookstore.user.repository.UserRepository;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CustomerProfileRepository customerProfileRepository;
    private final BookRepository bookRepository;

    public OrderService(
            OrderRepository orderRepository,
            UserRepository userRepository,
            CartRepository cartRepository,
            CustomerProfileRepository customerProfileRepository,
            BookRepository bookRepository
    ) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.customerProfileRepository = customerProfileRepository;
        this.bookRepository = bookRepository;
    }

    // =========================================================
    // PLACE ORDER
    // =========================================================

    @Transactional
    public OrderResponse placeOrder(String email) {

        User user = getUserByEmail(email);

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Cart not found"
                        )
                );

        if (cart.getItems() == null ||
                cart.getItems().isEmpty()) {

            throw new RuntimeException(
                    "Cannot place order because cart is empty"
            );
        }

        CustomerProfile profile =
                customerProfileRepository
                        .findByUserId(user.getId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Please complete customer details before placing an order"
                                )
                        );

        if (profile.getAddress() == null) {

            throw new RuntimeException(
                    "Please complete delivery address before placing an order"
            );
        }

        Address address = profile.getAddress();

        // -----------------------------------------------------
        // CREATE ORDER
        // -----------------------------------------------------

        Order order = new Order();

        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);

        order.setPhoneNumber(
                profile.getPhoneNumber()
        );

        order.setHouseNo(
                address.getHouseNo()
        );

        order.setStreet(
                address.getStreet()
        );

        order.setCity(
                address.getCity()
        );

        order.setState(
                address.getState()
        );

        order.setPincode(
                address.getPincode()
        );

        order.setCountry(
                address.getCountry()
        );

        order.setDeliveryPreference(
                profile.getDeliveryPreference()
        );

        // -----------------------------------------------------
        // PROCESS CART ITEMS
        // -----------------------------------------------------

        BigDecimal totalAmount =
                BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {

            Book book = cartItem.getBook();

            // Check current stock again before placing order

            if (cartItem.getQuantity() >
                    book.getQuantity()) {

                throw new RuntimeException(
                        "Insufficient stock for book: "
                                + book.getTitle()
                );
            }

            // Deduct stock

            book.setQuantity(
                    book.getQuantity()
                            - cartItem.getQuantity()
            );

            bookRepository.save(book);

            // Calculate subtotal

            BigDecimal subtotal =
                    cartItem.getUnitPrice()
                            .multiply(
                                    BigDecimal.valueOf(
                                            cartItem.getQuantity()
                                    )
                            );

            totalAmount =
                    totalAmount.add(subtotal);

            // Create order item

            OrderItem orderItem =
                    new OrderItem();

            orderItem.setOrder(order);

            orderItem.setBookId(
                    book.getId()
            );

            orderItem.setTitle(
                    book.getTitle()
            );

            orderItem.setAuthor(
                    book.getAuthor()
            );

            orderItem.setUnitPrice(
                    cartItem.getUnitPrice()
            );

            orderItem.setQuantity(
                    cartItem.getQuantity()
            );

            orderItem.setSubtotal(
                    subtotal
            );

            orderItem.setImageUrl(
                    book.getImageUrl()
            );

            order.getItems().add(orderItem);
        }

        order.setTotalAmount(totalAmount);

        // -----------------------------------------------------
        // SAVE ORDER
        // -----------------------------------------------------

        Order savedOrder =
                orderRepository.save(order);

        // -----------------------------------------------------
        // CLEAR CART
        // -----------------------------------------------------

        cart.getItems().clear();

        cart.setTotalAmount(
                BigDecimal.ZERO
        );

        cartRepository.save(cart);

        return convertToResponse(savedOrder);
    }

    // =========================================================
    // GET MY ORDERS
    // =========================================================

    public List<OrderResponse> getMyOrders(
            String email
    ) {

        User user = getUserByEmail(email);

        return orderRepository
                .findByUserOrderByOrderDateDesc(user)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // =========================================================
    // GET MY ORDER BY ID
    // =========================================================

    public OrderResponse getMyOrderById(
            String email,
            Long orderId
    ) {

        User user = getUserByEmail(email);

        Order order =
                orderRepository
                        .findByIdAndUser(
                                orderId,
                                user
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Order not found"
                                )
                        );

        return convertToResponse(order);
    }

    // =========================================================
// ADMIN - GET ORDER BY ID
// =========================================================

    public OrderResponse getOrderById(Long orderId) {

        Order order =
                orderRepository
                        .findById(orderId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Order not found"
                                )
                        );

        return convertToResponse(order);
    }

    // =========================================================
    // CANCEL ORDER
    // =========================================================

    @Transactional
    public OrderResponse cancelOrder(
            String email,
            Long orderId
    ) {

        User user = getUserByEmail(email);

        Order order =
                orderRepository
                        .findByIdAndUser(
                                orderId,
                                user
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Order not found"
                                )
                        );

        // Only pending orders can be cancelled

        if (order.getStatus()
                != OrderStatus.PENDING) {

            throw new RuntimeException(
                    "Only pending orders can be cancelled"
            );
        }

        // -----------------------------------------------------
        // RESTORE STOCK
        // -----------------------------------------------------

        for (OrderItem item :
                order.getItems()) {

            Book book =
                    bookRepository
                            .findById(
                                    item.getBookId()
                            )
                            .orElse(null);

            if (book != null) {

                book.setQuantity(
                        book.getQuantity()
                                + item.getQuantity()
                );

                bookRepository.save(book);
            }
        }

        order.setStatus(
                OrderStatus.CANCELLED
        );

        Order savedOrder =
                orderRepository.save(order);

        return convertToResponse(savedOrder);
    }

    // =========================================================
    // ADMIN - GET ALL ORDERS
    // =========================================================

    public List<OrderResponse> getAllOrders() {

        return orderRepository
                .findAllByOrderByOrderDateDesc()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // =========================================================
    // ADMIN - UPDATE STATUS
    // =========================================================

    @Transactional
    public OrderResponse updateOrderStatus(
            Long orderId,
            OrderStatus newStatus
    ) {

        Order order =
                orderRepository
                        .findById(orderId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Order not found"
                                )
                        );

        OrderStatus currentStatus =
                order.getStatus();

        // -----------------------------------------------------
        // PREVENT CHANGING CANCELLED / DELIVERED ORDERS
        // -----------------------------------------------------

        if (currentStatus ==
                OrderStatus.CANCELLED) {

            throw new RuntimeException(
                    "Cancelled order cannot be updated"
            );
        }

        if (currentStatus ==
                OrderStatus.DELIVERED) {

            throw new RuntimeException(
                    "Delivered order cannot be updated"
            );
        }

        // -----------------------------------------------------
        // VALIDATE STATUS FLOW
        // -----------------------------------------------------

        if (!isValidStatusTransition(
                currentStatus,
                newStatus
        )) {

            throw new RuntimeException(
                    "Invalid order status transition from "
                            + currentStatus
                            + " to "
                            + newStatus
            );
        }

        order.setStatus(newStatus);

        Order savedOrder =
                orderRepository.save(order);

        return convertToResponse(savedOrder);
    }

    // =========================================================
    // VALIDATE STATUS TRANSITION
    // =========================================================

    private boolean isValidStatusTransition(
            OrderStatus current,
            OrderStatus next
    ) {

        if (current == OrderStatus.PENDING) {

            return next == OrderStatus.CONFIRMED ||
                    next == OrderStatus.CANCELLED;
        }

        if (current == OrderStatus.CONFIRMED) {

            return next == OrderStatus.SHIPPED;
        }

        if (current == OrderStatus.SHIPPED) {

            return next == OrderStatus.DELIVERED;
        }

        return false;
    }

    // =========================================================
    // FIND USER
    // =========================================================

    private User getUserByEmail(
            String email
    ) {

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        )
                );
    }

    // =========================================================
    // ENTITY -> DTO
    // =========================================================

    private OrderResponse convertToResponse(
            Order order
    ) {

        List<OrderItemResponse> items =
                order.getItems()
                        .stream()
                        .map(item ->
                                new OrderItemResponse(
                                        item.getBookId(),
                                        item.getTitle(),
                                        item.getAuthor(),
                                        item.getUnitPrice(),
                                        item.getQuantity(),
                                        item.getSubtotal(),
                                        item.getImageUrl()
                                )
                        )
                        .toList();

        User user =
                order.getUser();

        return new OrderResponse(
                order.getId(),
                user.getId(),
                user.getName(),
                user.getEmail(),
                order.getOrderDate(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getPhoneNumber(),
                order.getHouseNo(),
                order.getStreet(),
                order.getCity(),
                order.getState(),
                order.getPincode(),
                order.getCountry(),
                order.getDeliveryPreference(),
                items
        );
    }
}