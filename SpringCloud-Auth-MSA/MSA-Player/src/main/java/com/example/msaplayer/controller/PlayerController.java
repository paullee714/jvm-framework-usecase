package com.example.msaplayer.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/player")
public class PlayerController {

    @Value("${server.port}")
    private String port;

    @Value("${spring.application.name}")
    private String name;

    @GetMapping("/server-info")
    public String getPlayer() {
        return "SERVER: [" + name + "] Player Server is running on port " + port;
    }
}
