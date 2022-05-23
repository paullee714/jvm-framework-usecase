package com.example.springbootmsacalculator.domain;

import lombok.*;

import java.util.Objects;


@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public final class User {

    private final String alias;

    private User() {
        alias = null;
    }
}
