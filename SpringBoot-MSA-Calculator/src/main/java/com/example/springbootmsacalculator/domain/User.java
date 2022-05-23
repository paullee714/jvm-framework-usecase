package com.example.springbootmsacalculator.domain;

import lombok.*;

import javax.persistence.*;
import java.util.Objects;


@RequiredArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
@Entity
@Table(name = "Users")
public final class User {

    @Id
    @GeneratedValue
    @Column(name = "USER_ID")
    private Long id;

    private final String alias;


    protected User() {
        id = null;
        alias = null;
    }
}
