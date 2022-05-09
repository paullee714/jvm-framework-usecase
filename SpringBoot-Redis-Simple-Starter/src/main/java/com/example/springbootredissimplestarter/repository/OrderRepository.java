package com.example.springbootredissimplestarter.repository;

import com.example.springbootredissimplestarter.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

}
