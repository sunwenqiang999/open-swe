package com.example.concurrency;

import java.util.concurrent.Semaphore;

/**
 * 使用 Semaphore 实现三个线程循环打印 1 到 100。
 *
 * <p>线程 A 打印 1, 4, 7...；线程 B 打印 2, 5, 8...；线程 C 打印 3, 6, 9...。
 * <p>由于 Semaphore 的 acquire/release 已经保证同一时刻只有一个线程访问 number，
 * 因此 number 使用普通 int 即可，无需 AtomicInteger。
 */
public class Print1To100Semaphore {

    private static final int MAX = 100;
    private static int number = 1;

    public static void main(String[] args) throws InterruptedException {
        Semaphore semaphoreA = new Semaphore(1);
        Semaphore semaphoreB = new Semaphore(0);
        Semaphore semaphoreC = new Semaphore(0);

        new Thread(() -> {
            while (number <= MAX) {
                try {
                    semaphoreA.acquire();
                    if (number > MAX) {
                        break;
                    }
                    System.out.println(Thread.currentThread().getName() + " 打印: " + number);
                    number++;
                    semaphoreB.release();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }, "线程-A").start();

        new Thread(() -> {
            while (number <= MAX) {
                try {
                    semaphoreB.acquire();
                    if (number > MAX) {
                        break;
                    }
                    System.out.println(Thread.currentThread().getName() + " 打印: " + number);
                    number++;
                    semaphoreC.release();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }, "线程-B").start();

        new Thread(() -> {
            while (number <= MAX) {
                try {
                    semaphoreC.acquire();
                    if (number > MAX) {
                        break;
                    }
                    System.out.println(Thread.currentThread().getName() + " 打印: " + number);
                    number++;
                    semaphoreA.release();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }, "线程-C").start();
    }
}
