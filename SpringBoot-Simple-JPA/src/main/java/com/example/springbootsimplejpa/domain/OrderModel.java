package com.example.springbootsimplejpa.domain;


import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "orders")
public class OrderModel {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private String orderCode;

    @NotNull
    private String userMail;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}
