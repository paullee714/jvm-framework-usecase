package com.example.springbootjpatest.repository;

import com.example.springbootjpatest.domain.OrderModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderModel, Long> {
}
