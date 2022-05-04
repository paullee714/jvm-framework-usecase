package com.example.springbootsimplescheduler.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class FixedDelayScheduler {

    @Scheduled(initialDelay = 5000, fixedDelay = 9000)
//	@Scheduled(initialDelayString = "5000" ,fixedDelayString = "9000")
    // 1000 milli sec = 1sec
    public void myMethod() {
        System.out.println("FixedDelayScheduler - " + new Date());
    }
}
