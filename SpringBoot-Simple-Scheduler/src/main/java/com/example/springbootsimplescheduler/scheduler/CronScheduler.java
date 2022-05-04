package com.example.springbootsimplescheduler.scheduler;


import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class CronScheduler {

    @Scheduled(cron = "* * * * * *")
    public void myMethod() {
        System.out.println("Hello cron Scheduler Three :" + new Date());
    }
}
