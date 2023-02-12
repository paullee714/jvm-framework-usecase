package com.example.springcontrolleradvice.controller.exception;

public record CustomExceptionModel(
        String message,
        String errorCode,
        String hint
) {
}
