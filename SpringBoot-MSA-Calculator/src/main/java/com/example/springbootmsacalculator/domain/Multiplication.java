package com.example.springbootmsacalculator.domain;

import lombok.RequiredArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import java.util.Objects;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class Multiplication {

    // 인수
    private final int factorA;
    private final int factorB;

    Multiplication() {
        this(0, 0);
    }

}
