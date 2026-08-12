package com.example.concurrency;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 经典生产者-消费者模型，基于 synchronized + wait/notify 实现。
 *
 * <p>核心思路：
 * <ul>
 *     <li>使用一个有限容量的缓冲区（队列）存放产品</li>
 *     <li>生产者缓冲区满时调用 wait() 等待，生产后调用 notifyAll() 唤醒消费者</li>
 *     <li>消费者缓冲区空时调用 wait() 等待，消费后调用 notifyAll() 唤醒生产者</li>
 *     <li>支持多个生产者与多个消费者并发协作</li>
 * </ul>
 */
public class ProducerConsumerClassic {

    private final int capacity;
    private final Queue<Integer> buffer = new LinkedList<>();

    public ProducerConsumerClassic(int capacity) {
        this.capacity = capacity;
    }

    /**
     * 生产者向缓冲区放入一个产品。
     */
    public void produce(int item) throws InterruptedException {
        synchronized (buffer) {
            // 缓冲区已满，生产者等待
            while (buffer.size() == capacity) {
                System.out.println(Thread.currentThread().getName() + "：缓冲区已满，等待消费...");
                buffer.wait();
            }

            buffer.offer(item);
            System.out.println(Thread.currentThread().getName() + " 生产: " + item + "（当前库存: " + buffer.size() + ")");

            // 生产成功后唤醒所有等待的线程（包括其他生产者和消费者）
            buffer.notifyAll();
        }
    }

    /**
     * 消费者从缓冲区取出一个产品。
     *
     * @return 消费的产品编号，返回 null 表示没有更多产品且缓冲为空
     */
    public Integer consume() throws InterruptedException {
        synchronized (buffer) {
            // 缓冲区为空，消费者等待
            while (buffer.isEmpty()) {
                System.out.println(Thread.currentThread().getName() + "：缓冲区为空，等待生产...");
                buffer.wait();
            }

            Integer item = buffer.poll();
            System.out.println(Thread.currentThread().getName() + " 消费: " + item + "（当前库存: " + buffer.size() + ")");

            // 消费成功后唤醒所有等待的线程
            buffer.notifyAll();
            return item;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        final int capacity = 5;       // 缓冲区容量
        final int totalItems = 20;    // 总共生产的产品数
        final int producerCount = 2;  // 生产者数量
        final int consumerCount = 2;  // 消费者数量

        ProducerConsumerClassic pc = new ProducerConsumerClassic(capacity);
        AtomicInteger produced = new AtomicInteger(0);
        AtomicInteger consumed = new AtomicInteger(0);

        // 启动生产者线程
        for (int i = 0; i < producerCount; i++) {
            new Thread(() -> {
                while (true) {
                    int item = produced.incrementAndGet();
                    if (item > totalItems) {
                        return;
                    }
                    try {
                        pc.produce(item);
                        TimeUnit.MILLISECONDS.sleep(50);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }, "Producer-" + i).start();
        }

        // 启动消费者线程
        for (int i = 0; i < consumerCount; i++) {
            new Thread(() -> {
                while (consumed.incrementAndGet() <= totalItems) {
                    try {
                        pc.consume();
                        TimeUnit.MILLISECONDS.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }, "Consumer-" + i).start();
        }

        // 等待所有线程完成（简单等待，实际项目建议使用 ExecutorService）
        TimeUnit.SECONDS.sleep(3);
        System.out.println("--------------------");
        System.out.println("生产完成: " + totalItems + "，消费完成: " + consumed.get());
    }
}
