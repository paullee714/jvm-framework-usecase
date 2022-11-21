package com.wool.service

import com.wool.repository.ModuleCoreCustomerRepository
import com.wool.service.dtos.customer.CustomerDto
import org.springframework.stereotype.Service

@Service
class CustomerService(
    private val customerRepository: ModuleCoreCustomerRepository
) {

    fun getCustomers() = customerRepository.findAll()

    fun saveCustomers(customerDto: CustomerDto) {
        this.customerRepository.save(customerDto.toEntity())
    }
}
