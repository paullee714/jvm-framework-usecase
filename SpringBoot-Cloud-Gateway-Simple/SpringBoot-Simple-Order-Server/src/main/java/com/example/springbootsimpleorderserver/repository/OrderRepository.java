package com.example.springbootsimpleorderserver.repository;

import com.example.springbootsimpleorderserver.domain.OrderDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<OrderDomain, Integer> {

}
