package com.example.threadtest.algorithm;

import com.example.threadtest.RateLimiter;

public class FixedWindow implements RateLimiter {
    private final double window;
    private final int maxRequests;
    private int requests;
    private final long initTime;
    private double time;
    private long windowStart;

    FixedWindow(double window, int maxRequests) {
        this.window = window;
        this.maxRequests = maxRequests;
        this.requests = 0;
        this.windowStart = System.currentTimeMillis();
        this.initTime = System.currentTimeMillis();
    }

    @Override
    public void handleRequest() {
        long currentTime = System.currentTimeMillis();
        if ((currentTime - windowStart) > window * 1000) {
            windowStart = currentTime;
            requests = 0;
        }
        if (requests < maxRequests) {
            requests++;
            System.out.println("allowed");
        } else {
            if (time != 0) {
                System.out.println("fail, Duration: " + time);
            } else {
                time = (currentTime - initTime) / 1000.0;
                System.out.printf("fail, Duration: %.3f%n", time);
            }
        }
    }
}
