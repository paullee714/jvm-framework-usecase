package com.example.springbootsimplescheduler.scheduler;


import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class FixedRateScheduler {

    @Scheduled(fixedRate = 1000)
    //@Scheduled(fixedRateString = "1000")
    public void myMethod() {
        System.out.println("FixedRateScheduler - " + new Date());
    }
}
