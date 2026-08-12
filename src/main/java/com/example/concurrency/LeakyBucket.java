package com.example.concurrency;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 漏桶限流算法实现。
 *
 * <p>核心思路：
 * <ul>
 *     <li>桶有一个固定容量 capacity，代表最多可积压的请求数</li>
 *     <li>请求到来时先入桶（water++），桶满则直接丢弃</li>
 *     <li>以固定速率 ratePerSecond（个/秒）从桶底漏出（处理）请求</li>
 *     <li>漏出速率恒定，可有效平滑突发流量，保护下游服务</li>
 * </ul>
 *
 * <p>与令牌桶的区别：漏桶输出速率严格恒定，不允许突发；令牌桶允许短时间内的突发流量。
 */
public class LeakyBucket {

    private final long capacity;       // 桶的最大容量（最多积压的请求数）
    private final long ratePerSecond;  // 漏出速率（每秒处理的请求数）

    private long water;                // 当前桶中的水量（积压请求数）
    private long lastLeakTimestampMillis; // 上次漏水的时间戳

    public LeakyBucket(long capacity, long ratePerSecond) {
        this.capacity = capacity;
        this.ratePerSecond = ratePerSecond;
        this.water = 0;
        this.lastLeakTimestampMillis = System.currentTimeMillis();
    }

    /**
     * 判断当前请求是否允许通过。
     *
     * @return true 表示请求进入桶中（允许处理），false 表示桶已满（被限流丢弃）
     */
    public synchronized boolean tryAcquire() {
        leak();

        if (water < capacity) {
            water++;
            return true;
        }
        // 桶已满，请求被丢弃
        return false;
    }

    /**
     * 根据时间流逝，从桶中漏出对应数量的水（模拟请求被处理）。
     */
    private void leak() {
        long now = System.currentTimeMillis();
        long elapsedMillis = now - lastLeakTimestampMillis;

        // 计算这段时间内应该漏出的水量
        long leaked = elapsedMillis * ratePerSecond / 1000;
        if (leaked > 0) {
            water = Math.max(0, water - leaked);
            lastLeakTimestampMillis = now;
        }
    }

    /**
     * 获取当前桶中的水量（用于监控/调试）。
     */
    public synchronized long getWater() {
        leak();
        return water;
    }

    public static void main(String[] args) throws InterruptedException {
        // 容量为 5，每秒处理 2 个请求
        LeakyBucket bucket = new LeakyBucket(5, 2);

        AtomicInteger pass = new AtomicInteger(0);
        AtomicInteger reject = new AtomicInteger(0);

        // 第一阶段：短时间内连续发送 8 个请求，模拟突发流量
        System.out.println("=== 第一阶段：突发 8 个请求 ===");
        for (int i = 0; i < 8; i++) {
            boolean allowed = bucket.tryAcquire();
            if (allowed) {
                System.out.println("请求 " + (i + 1) + "：通过（当前水量=" + bucket.getWater() + ")");
                pass.incrementAndGet();
            } else {
                System.out.println("请求 " + (i + 1) + "：被限流（桶已满）");
                reject.incrementAndGet();
            }
        }

        // 等待 1 秒，让桶漏出部分水
        System.out.println("\n--- 等待 1 秒，漏桶处理请求... ---\n");
        TimeUnit.SECONDS.sleep(1);

        // 第二阶段：再次发送请求，此时桶已腾出空间
        System.out.println("=== 第二阶段：再次发送 5 个请求 ===");
        for (int i = 0; i < 5; i++) {
            boolean allowed = bucket.tryAcquire();
            if (allowed) {
                System.out.println("请求 " + (i + 1) + "：通过（当前水量=" + bucket.getWater() + ")");
                pass.incrementAndGet();
            } else {
                System.out.println("请求 " + (i + 1) + "：被限流（桶已满）");
                reject.incrementAndGet();
            }
        }

        System.out.println("\n--------------------");
        System.out.println("总通过: " + pass.get() + "，总限流: " + reject.get());
    }
}
