package com.wool.repository

import com.wool.entity.Customer
import org.springframework.data.jpa.repository.JpaRepository

interface ModuleCoreCustomerRepository:JpaRepository<Customer, Long> {

}
