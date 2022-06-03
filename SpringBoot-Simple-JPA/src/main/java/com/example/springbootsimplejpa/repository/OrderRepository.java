package com.example.springbootsimplejpa.repository;

import com.example.springbootsimplejpa.domain.OrderModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<OrderModel, String> {
    OrderModel findByOrderCode(String orderCode);
}
