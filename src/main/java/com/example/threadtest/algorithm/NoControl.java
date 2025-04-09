package com.example.threadtest.algorithm;

import com.example.threadtest.RateLimiter;

public class NoControl implements RateLimiter {
    private double time;
    private int requestCount = 0;
    private final long initTime = System.currentTimeMillis();

    @Override
    public void handleRequest() {
        requestCount++;
        if (requestCount <= 100) {
            System.out.println("Request " + requestCount + " forwarded without any control.");
        } else {
            if (time != 0) {
                System.out.println("fail totally, Duration: " + time);
            } else {
                time = (System.currentTimeMillis() - initTime) / 1000.0;
                System.out.printf("fail totally, Duration: %.3f%n", time);
            }
        }
    }
}
