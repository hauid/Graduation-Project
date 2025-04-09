package com.example.threadtest.algorithm;

import com.example.threadtest.RateLimiter;

public class LeakyBucket implements RateLimiter {
    private final double rate;
    private final int cap;
    private double water;
    private long lastFillTime;
    private int requestCount;
    private int rejectedCount;

    LeakyBucket(double rate, int cap) {
        this.rate = rate;
        this.cap = cap;
        this.lastFillTime = System.currentTimeMillis();
    }

    @Override
    public void handleRequest() {
        long currentTime = System.currentTimeMillis();
        double timeElapsed = (currentTime - lastFillTime) / 1000.0;
        water = Math.max(0, water - timeElapsed * rate);
        lastFillTime = currentTime;

        if (water < cap) {
            water += 1;
            requestCount++;
            System.out.println("Request allowed in leaky bucket.");
        } else {
            rejectedCount++;
            System.out.println("Request rejected, bucket full.");
        }
    }
}
