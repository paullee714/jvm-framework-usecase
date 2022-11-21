package com.wool.controller

import com.wool.service.CustomerService
import com.wool.service.OrderService
import com.wool.service.dtos.customer.CustomerDto
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController


@RestController
class CustomerController(
    private val customerService: CustomerService
) {

    @PostMapping("/customers")
    fun saveCustomers( @RequestBody customerDto: CustomerDto) {
        return this.customerService.saveCustomers(customerDto)
    }

    @GetMapping("/customers")
    fun getCustomers() = this.customerService.getCustomers()
}
