package com.example.threadtest.algorithm;

import com.example.threadtest.RateLimiter;

import java.util.Deque;
import java.util.LinkedList;

public class SlidingWindow implements RateLimiter {
    private final double window;
    private final int maxRequests;
    private Deque<Long> requests = new LinkedList<>();
    private int requestCount = 0;
    private int rejectedCount = 0;

    SlidingWindow(double window, int maxRequests) {
        this.window = window;
        this.maxRequests = maxRequests;
    }

    @Override
    public void handleRequest() {
        long currentTime = System.currentTimeMillis();
        // 移除过期请求
        while (!requests.isEmpty() && requests.peekFirst() + window * 1000 < currentTime) {
            requests.pollFirst();
        }
        // 判断当前请求数
        if (requests.size() < maxRequests) {
            requests.add(currentTime);
            requestCount++;
            System.out.println("allowed");
        } else {
            rejectedCount++;
            System.out.println("rejected");
        }
    }
}
