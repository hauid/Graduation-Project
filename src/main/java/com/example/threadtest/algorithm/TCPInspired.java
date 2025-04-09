package com.example.threadtest.algorithm;

import com.example.threadtest.RateLimiter;

public class TCPInspired implements RateLimiter {
    private final int initWindow;
    private int windowSize;
    private final double threshold;
    private boolean slowStart = true;
    private int requests;

    TCPInspired(int initWindow, double threshold) {
        this.initWindow = initWindow;
        this.windowSize = initWindow;
        this.threshold = threshold;
    }

    @Override
    public void handleRequest() {
        if (slowStart) {
            windowSize++;
            if (windowSize >= threshold * initWindow) {
                slowStart = false;
                System.out.printf("Window size %d reaching threshold, switching to congestion avoidance.%n", windowSize);
            }
        } else {
            windowSize += 1;
            if (windowSize > initWindow) {
                windowSize--;
            }
            System.out.printf("Request allowed in TCP-inspired algorithm with window size %d.%n", windowSize);
        }
    }
}
