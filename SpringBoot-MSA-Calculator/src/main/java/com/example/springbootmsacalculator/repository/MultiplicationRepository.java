package com.example.springbootmsacalculator.repository;

import com.example.springbootmsacalculator.domain.Multiplication;
import org.springframework.data.repository.CrudRepository;

public interface MultiplicationRepository extends CrudRepository<Multiplication, Long> {
}
