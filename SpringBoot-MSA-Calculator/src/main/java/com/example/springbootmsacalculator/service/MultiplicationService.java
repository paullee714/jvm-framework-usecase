package com.example.springbootmsacalculator.service;


import com.example.springbootmsacalculator.domain.Multiplication;
import com.example.springbootmsacalculator.domain.MultiplicationResultAttempt;

import java.util.List;

public interface MultiplicationService {

    Multiplication createRandomMultiplication();

    List<MultiplicationResultAttempt> getStatsForuser(String userAlias);

    boolean checkAttempt(final MultiplicationResultAttempt resultAttempt);

}
