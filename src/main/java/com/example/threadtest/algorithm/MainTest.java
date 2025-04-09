package com.example.threadtest.algorithm;

import com.example.threadtest.RateLimiter;


import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class MainTest {
    public static void simulateRequests1() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Map<String, RateLimiter> algorithms = new HashMap<>(); // 类型改为 RateLimiter
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
//                    entry.getValue().getClass().getMethod("handleRequest").invoke(entry.getValue());
                    entry.getValue().handleRequest(); // 直接调用接口方法
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
