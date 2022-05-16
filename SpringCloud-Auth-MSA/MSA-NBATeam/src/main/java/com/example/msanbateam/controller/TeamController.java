package com.example.msanbateam.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/team")
public class TeamController {

    @Value("${server.port}")
    private String port;

    @Value("${spring.application.name}")
    private String name;

    @GetMapping("/server-info")
    public String getPlayer() {
        return "SERVER: [" + name + "] Team Server is running on port " + port;
    }

    @PostMapping("/server-info")
    public String postPlayer() {

        return "SERVER: [" + name + "] Team Server is running on port " + port;
    }
}
