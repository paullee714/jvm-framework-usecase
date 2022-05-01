package com.example.springboothandlerinterceptorsimple.controller;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class MyTestController {

    @GetMapping("/test-one")
    public Map<String, Object> firstAPI(@RequestParam Map<String, Object> request) {
        return request;
    }

    @PostMapping("/test-two")
    public Map<String, Object> secondAPI(@RequestBody Map<String, Object> request) {
        return request;
    }
}
