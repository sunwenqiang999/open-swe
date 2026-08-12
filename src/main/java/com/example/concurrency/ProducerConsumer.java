package com.example.concurrency;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 生产者-消费者模型，基于 BlockingQueue 实现。
 */
public class ProducerConsumer {

    private static final int CAPACITY = 5;
    private static final int TOTAL = 20;

    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(CAPACITY);

        Thread producer = new Thread(() -> {
            for (int i = 1; i <= TOTAL; i++) {
                try {
                    queue.put(i);
                    System.out.println("Producer produced: " + i);
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }, "Producer");

        Thread consumer = new Thread(() -> {
            int consumed = 0;
            while (consumed < TOTAL) {
                try {
                    Integer item = queue.take();
                    consumed++;
                    System.out.println("Consumer consumed: " + item);
                    TimeUnit.MILLISECONDS.sleep(150);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }, "Consumer");

        producer.start();
        consumer.start();
        producer.join();
        consumer.join();
        System.out.println("Done.");
    }
}
