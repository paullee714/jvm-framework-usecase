package com.example.springkotlinsimpleexample.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api")
class SampleController {

    @GetMapping("/greet")
    fun greet(): String {
        return "Hello World"
    }
}
