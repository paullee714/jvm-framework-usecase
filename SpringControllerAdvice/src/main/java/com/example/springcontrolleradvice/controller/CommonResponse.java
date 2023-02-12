package com.example.springcontrolleradvice.controller;


public record CommonResponse(
        String Status,
        String message,
        Object data
) {}
