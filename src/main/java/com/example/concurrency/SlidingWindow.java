package com.example.concurrency;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 滑动窗口限流算法实现。
 *
 * <p>核心思路：
 * <ul>
 *     <li>将一个大时间窗口划分为多个等长的小窗口（slot）</li>
 *     <li>每个 slot 记录该时间片内的请求数量</li>
 *     <li>每次请求时，统计最近一个完整窗口周期内所有 slot 的请求总数</li>
 *     <li>总数未超过阈值则放行，并更新当前 slot 计数；超过则拒绝</li>
 * </ul>
 *
 * <p>与固定窗口相比，滑动窗口能避免窗口边界处的突发流量问题；
 * 与令牌桶/漏桶相比，它更直接地限制了“单位时间内的请求总数”。
 */
public class SlidingWindow {

    private final long windowSizeMillis;  // 整个滑动窗口的时间跨度
    private final int splitCount;         // 窗口切分成多少个小窗口
    private final long slotSizeMillis;    // 每个小窗口的时间长度
    private final long maxRequests;       // 整个窗口内允许的最大请求数

    private final long[] slots;           // 每个小窗口的请求计数
    private long windowStartMillis;       // 当前窗口的起始时间戳

    public SlidingWindow(long windowSizeMillis, int splitCount, long maxRequests) {
        if (windowSizeMillis <= 0 || splitCount <= 0 || maxRequests <= 0) {
            throw new IllegalArgumentException("windowSizeMillis、splitCount、maxRequests 必须大于 0");
        }
        if (windowSizeMillis % splitCount != 0) {
            throw new IllegalArgumentException("windowSizeMillis 必须能被 splitCount 整除");
        }
        this.windowSizeMillis = windowSizeMillis;
        this.splitCount = splitCount;
        this.slotSizeMillis = windowSizeMillis / splitCount;
        this.maxRequests = maxRequests;
        this.slots = new long[splitCount];
        this.windowStartMillis = System.currentTimeMillis();
    }

    /**
     * 判断当前请求是否允许通过。
     *
     * @return true 表示通过，false 表示被限流
     */
    public synchronized boolean tryAcquire() {
        long now = System.currentTimeMillis();

        // 计算当前时间相对于窗口起点经过了多少个 slot
        long elapsedSlots = (now - windowStartMillis) / slotSizeMillis;

        // 如果 elapsedSlots 超过 splitCount，说明整个窗口已经滑过去至少一轮
        // 需要清空过期的 slot 数据，并移动窗口起点
        if (elapsedSlots >= splitCount) {
            // 窗口整体向前滑动，清空所有计数
            // 新的窗口起点对齐到当前 slot 所在窗口的起始位置
            windowStartMillis = now - (now - windowStartMillis) % slotSizeMillis;
            for (int i = 0; i < splitCount; i++) {
                slots[i] = 0;
            }
            elapsedSlots = 0;
        }

        // 计算当前请求落在哪个 slot
        int currentSlot = (int) (elapsedSlots % splitCount);

        // 统计当前窗口内（最近 splitCount 个 slot）的总请求数
        long total = 0;
        for (long count : slots) {
            total += count;
        }

        if (total < maxRequests) {
            slots[currentSlot]++;
            return true;
        }
        return false;
    }

    /**
     * 获取当前窗口内的总请求数（用于监控/调试）。
     */
    public synchronized long getCurrentCount() {
        long total = 0;
        for (long count : slots) {
            total += count;
        }
        return total;
    }

    public static void main(String[] args) throws InterruptedException {
        // 1 秒窗口，分成 10 个 slot，最多允许 5 个请求
        SlidingWindow window = new SlidingWindow(1000, 10, 5);

        AtomicInteger pass = new AtomicInteger(0);
        AtomicInteger reject = new AtomicInteger(0);

        System.out.println("=== 第一阶段：连续发送 8 个请求（模拟突发）===");
        for (int i = 0; i < 8; i++) {
            boolean allowed = window.tryAcquire();
            if (allowed) {
                System.out.println("请求 " + (i + 1) + "：通过（当前窗口计数=" + window.getCurrentCount() + ")");
                pass.incrementAndGet();
            } else {
                System.out.println("请求 " + (i + 1) + "：被限流（当前窗口计数=" + window.getCurrentCount() + ")");
                reject.incrementAndGet();
            }
        }

        System.out.println("\n--- 等待 600 毫秒，让窗口滑动一部分 ---\n");
        TimeUnit.MILLISECONDS.sleep(600);

        System.out.println("=== 第二阶段：再次发送 5 个请求 ===");
        for (int i = 0; i < 5; i++) {
            boolean allowed = window.tryAcquire();
            if (allowed) {
                System.out.println("请求 " + (i + 1) + "：通过（当前窗口计数=" + window.getCurrentCount() + ")");
                pass.incrementAndGet();
            } else {
                System.out.println("请求 " + (i + 1) + "：被限流（当前窗口计数=" + window.getCurrentCount() + ")");
                reject.incrementAndGet();
            }
        }

        System.out.println("\n--------------------");
        System.out.println("总通过: " + pass.get() + "，总限流: " + reject.get());
    }
}
