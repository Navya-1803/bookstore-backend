package com.bookstore.order.repository;

import com.bookstore.order.entity.Order;
import com.bookstore.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository
        extends JpaRepository<Order, Long> {

    List<Order> findByUserOrderByOrderDateDesc(User user);

    Optional<Order> findByIdAndUser(Long id, User user);

    List<Order> findAllByOrderByOrderDateDesc();
}