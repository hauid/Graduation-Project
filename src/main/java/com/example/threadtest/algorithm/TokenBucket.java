package com.example.threadtest.algorithm;

import com.example.threadtest.RateLimiter;

public class TokenBucket implements RateLimiter {
    private final double rate;
    private final int cap;
    private double tokens;
    private long lastFillTime;
    private int requestCount;
    private int rejectedCount;

    TokenBucket(double rate, int cap) {
        this.rate = rate;
        this.cap = cap;
        this.tokens = cap;
        this.lastFillTime = System.currentTimeMillis();
    }

    @Override
    public void handleRequest() {
        long currentTime = System.currentTimeMillis();
        double timeElapsed = (currentTime - lastFillTime) / 1000.0; // 转换为秒
        tokens = Math.min(cap, tokens + timeElapsed * rate);
        lastFillTime = currentTime;

        if (tokens >= 1) {
            tokens -= 1;
            requestCount++;
            System.out.println("allowed");
        } else {
            rejectedCount++;
            System.out.println("rejected");
        }
    }
}
