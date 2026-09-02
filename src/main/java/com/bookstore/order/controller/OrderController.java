package com.bookstore.order.controller;

import com.bookstore.order.dto.OrderResponse;
import com.bookstore.order.dto.OrderStatusUpdateRequest;
import com.bookstore.order.service.OrderService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(
            OrderService orderService
    ) {
        this.orderService = orderService;
    }

    // =========================================================
    // USER - PLACE ORDER
    // =========================================================

    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(
            Authentication authentication
    ) {

        String email =
                authentication.getName();

        return ResponseEntity.ok(
                orderService.placeOrder(email)
        );
    }

    // =========================================================
    // USER - VIEW OWN ORDERS
    // =========================================================

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getMyOrders(
            Authentication authentication
    ) {

        String email =
                authentication.getName();

        return ResponseEntity.ok(
                orderService.getMyOrders(email)
        );
    }

    // =========================================================
    // USER - VIEW ONE OWN ORDER
    // =========================================================

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getMyOrderById(
            Authentication authentication,
            @PathVariable Long id
    ) {

        String email =
                authentication.getName();

        return ResponseEntity.ok(
                orderService.getMyOrderById(
                        email,
                        id
                )
        );
    }

    // =========================================================
// ADMIN - VIEW ORDER BY ID
// =========================================================

    @GetMapping("/admin/{id}")
    public ResponseEntity<OrderResponse> getOrderById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                orderService.getOrderById(id)
        );
    }

    // =========================================================
    // USER - CANCEL ORDER
    // =========================================================

    @PutMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(
            Authentication authentication,
            @PathVariable Long id
    ) {

        String email =
                authentication.getName();

        return ResponseEntity.ok(
                orderService.cancelOrder(
                        email,
                        id
                )
        );
    }

    // =========================================================
    // ADMIN - VIEW ALL ORDERS
    // =========================================================

    @GetMapping("/all")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {

        return ResponseEntity.ok(
                orderService.getAllOrders()
        );
    }

    // =========================================================
    // ADMIN - UPDATE ORDER STATUS
    // =========================================================

    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody
            OrderStatusUpdateRequest request
    ) {

        return ResponseEntity.ok(
                orderService.updateOrderStatus(
                        id,
                        request.getStatus()
                )
        );
    }
}