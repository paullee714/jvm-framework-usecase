package com.example.springbootmsacalculator.service;


import com.example.springbootmsacalculator.domain.Multiplication;
import com.example.springbootmsacalculator.domain.MultiplicationResultAttempt;

public interface MultiplicationService {

    Multiplication createRandomMultiplication();

    boolean checkAttempt(final MultiplicationResultAttempt resultAttempt);

}
