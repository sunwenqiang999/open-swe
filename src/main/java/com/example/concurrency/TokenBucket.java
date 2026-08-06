package com.example.concurrency;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 简单的令牌桶限流算法实现。
 *
 * <p>核心思路：
 * <ul>
 *     <li>桶有一个固定容量 capacity</li>
 *     <li>以固定速率 rate（个/秒）向桶中放入令牌</li>
 *     <li>每次请求尝试消费指定数量的令牌</li>
 *     <li>令牌足够则放行并扣减；不足则拒绝</li>
 * </ul>
 */
public class TokenBucket {

    private final long capacity;
    private final long ratePerSecond;

    private long tokens;
    private long lastTimestampMillis;

    public TokenBucket(long capacity, long ratePerSecond) {
        this.capacity = capacity;
        this.ratePerSecond = ratePerSecond;
        this.tokens = capacity;
        this.lastTimestampMillis = System.currentTimeMillis();
    }

    /**
     * 尝试获取指定数量的令牌。
     *
     * @param amount 请求的令牌数
     * @return true 表示获取成功，false 表示被限流
     */
    public synchronized boolean tryAcquire(long amount) {
        long now = System.currentTimeMillis();
        long elapsedMillis = now - lastTimestampMillis;

        // 根据时间差计算这段时间内应该新增的令牌数
        long tokensToAdd = elapsedMillis * ratePerSecond / 1000;
        tokens = Math.min(capacity, tokens + tokensToAdd);
        lastTimestampMillis = now;

        if (tokens >= amount) {
            tokens -= amount;
            return true;
        }
        return false;
    }

    /**
     * 简化版：默认只请求 1 个令牌。
     */
    public boolean tryAcquire() {
        return tryAcquire(1);
    }

    public static void main(String[] args) throws InterruptedException {
        // 容量为 10，每秒产生 5 个令牌
        TokenBucket bucket = new TokenBucket(10, 5);

        AtomicInteger pass = new AtomicInteger(0);
        AtomicInteger reject = new AtomicInteger(0);

        // 模拟 20 个请求，每隔 100 毫秒发起一次
        for (int i = 0; i < 20; i++) {
            boolean acquired = bucket.tryAcquire();
            if (acquired) {
                System.out.println("请求 " + (i + 1) + "：通过");
                pass.incrementAndGet();
            } else {
                System.out.println("请求 " + (i + 1) + "：被限流");
                reject.incrementAndGet();
            }
            TimeUnit.MILLISECONDS.sleep(100);
        }

        System.out.println("--------------------");
        System.out.println("通过: " + pass.get() + "，限流: " + reject.get());
    }
}
