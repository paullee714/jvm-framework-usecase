package com.example.springbootsimplejpa.repository;

import com.example.springbootsimplejpa.domain.OrderModel;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Scanner;

@SpringBootTest
public class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void crud() { // create / read/ update / delete
        for (int i = 0; i < 10; i++) {
            var order = new OrderModel();
            String orderCode = RandomStringUtils.randomAlphanumeric(15);
            String orderUserMail = RandomStringUtils.randomAlphanumeric(7) + "@mail.com";
            order.setOrderCode(orderCode);
            order.setUserMail(orderUserMail);
            orderRepository.save(order);
        }

        orderRepository.findAll().forEach(System.out::println);
        for (OrderModel item : orderRepository.findAll()) {
            System.out.println(item);
        }

        List<OrderModel> orders = orderRepository.findAll(Sort.by(Sort.Direction.DESC,"orderCode"));
        orders.forEach(System.out::println);

    }
}
