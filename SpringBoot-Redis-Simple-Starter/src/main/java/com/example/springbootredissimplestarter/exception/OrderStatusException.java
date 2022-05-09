package com.example.springbootredissimplestarter.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class OrderStatusException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public OrderStatusException(String message) {
        super(message);
    }
}
