package com.example.springkotlinloggingsample

import lombok.extern.slf4j.Slf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Slf4j
@Controller
class TestController {
    val logger: Logger = LoggerFactory.getLogger(TestController::class.java)

    @GetMapping("/")
    fun index(): String {
        logger.info("Hello, This is INFO Message")
        logger.debug("Hello, This is DEBUG Message")
        logger.trace("Hello, This is TRACE Message")
        logger.warn("Hello, This is WARN Message")
        logger.error("Hello, This is ERROR Message")
        return "index"
    }
}
