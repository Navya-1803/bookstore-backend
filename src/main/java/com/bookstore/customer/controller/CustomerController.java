package com.bookstore.customer.controller;

import com.bookstore.customer.dto.CustomerDetailsRequest;
import com.bookstore.customer.service.CustomerService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(
            CustomerService customerService
    ) {
        this.customerService = customerService;
    }

    @GetMapping("/details")
    public ResponseEntity<Map<String, Object>> getCustomerDetails(
            Authentication authentication
    ) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                customerService.getCustomerDetailsByEmail(email)
        );
    }

    @PutMapping("/details")
    public ResponseEntity<Map<String, Object>> updateCustomerDetails(
            Authentication authentication,
            @Valid @RequestBody CustomerDetailsRequest request
    ) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                customerService.updateCustomerDetailsByEmail(
                        email,
                        request
                )
        );
    }
}