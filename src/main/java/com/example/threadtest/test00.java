package com.example.threadtest;

import java.lang.reflect.InvocationTargetException;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class test00 {
    static class NoControl {
        private double time;
        private int requestCount = 0;
        private final long initTime = System.currentTimeMillis();

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

    // FixedWindow类
    static class FixedWindow {
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

    static class SlidingWindow {
        private final double window;
        private final int maxRequests;
        private Deque<Long> requests = new LinkedList<>();
        private int requestCount = 0;
        private int rejectedCount = 0;

        SlidingWindow(double window, int maxRequests) {
            this.window = window;
            this.maxRequests = maxRequests;
        }

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

    // TokenBucket类
    static class TokenBucket {
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

    // LeakyBucket类
    static class LeakyBucket {
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

    // TCPInspired类
    static class TCPInspired {
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



    public static void simulateRequests1() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Map<String, Object> algorithms = new HashMap<>();
        algorithms.put("al1", new NoControl());
        algorithms.put("al2", new FixedWindow(0.1, 3));
        algorithms.put("al3", new SlidingWindow(60, 5000));
        algorithms.put("al4", new TokenBucket(1, 2));
        algorithms.put("al5", new LeakyBucket(10, 200));
        algorithms.put("al6", new TCPInspired(100, 0.8));


        long startTime = System.currentTimeMillis();
        int rps = 1200;
        double sleepDuration = 1.0 / rps;
        double time_lower = sleepDuration * 1.01;
        double time_upper = sleepDuration * 0.99;

        int simulateReqNum = 500;
        for (int i = 0; i < simulateReqNum; i++) {
            System.out.printf("\n---模拟RPS:%d-模拟请求 %d ---%n", rps, i + 1);
            for (var entry : algorithms.entrySet()) {
                try {
                    System.out.print(entry.getKey() + ": ");
                    entry.getValue().getClass().getMethod("handleRequest").invoke(entry.getValue());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep((long)(ThreadLocalRandom.current().nextDouble(time_upper, time_lower) ));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.printf("测试总耗时:%.3fs%n", (endTime - startTime)/1000.0);
        System.out.printf("真实RPS:%.2f%n", simulateReqNum * 1000.0/(endTime - startTime));
    }

    // 主方法
    public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        simulateRequests1();
    }


}
