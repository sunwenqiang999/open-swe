package com.example.concurrency;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 三个线程交替循环打印 1、2、3。
 *
 * <p>提供三种常见实现：synchronized + wait/notify、ReentrantLock + Condition、Semaphore。
 */
public class Print123 {

    private static final int ROUNDS = 5;

    /**
     * 方式一：synchronized + wait/notify
     */
    public static class SynchronizedVersion {
        private int state = 0; // 0 打印 1，1 打印 2，2 打印 3

        public void run() {
            Thread t1 = new Thread(new Printer(0, 1, "1"));
            Thread t2 = new Thread(new Printer(1, 2, "2"));
            Thread t3 = new Thread(new Printer(2, 0, "3"));
            t1.start();
            t2.start();
            t3.start();
            try {
                t1.join();
                t2.join();
                t3.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        private class Printer implements Runnable {
            private final int current;
            private final int next;
            private final String text;

            Printer(int current, int next, String text) {
                this.current = current;
                this.next = next;
                this.text = text;
            }

            @Override
            public void run() {
                for (int i = 0; i < ROUNDS; i++) {
                    synchronized (SynchronizedVersion.this) {
                        while (state != current) {
                            try {
                                SynchronizedVersion.this.wait();
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                return;
                            }
                        }
                        System.out.print(text);
                        state = next;
                        SynchronizedVersion.this.notifyAll();
                    }
                }
            }
        }
    }

    /**
     * 方式二：ReentrantLock + Condition
     */
    public static class LockVersion {
        private final Lock lock = new ReentrantLock();
        private final Condition c1 = lock.newCondition();
        private final Condition c2 = lock.newCondition();
        private final Condition c3 = lock.newCondition();
        private int state = 1;

        public void run() {
            Thread t1 = new Thread(() -> print(1, 2, c1, c2, "1"));
            Thread t2 = new Thread(() -> print(2, 3, c2, c3, "2"));
            Thread t3 = new Thread(() -> print(3, 1, c3, c1, "3"));
            t1.start();
            t2.start();
            t3.start();
            try {
                t1.join();
                t2.join();
                t3.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        private void print(int current, int next, Condition cur, Condition nxt, String text) {
            for (int i = 0; i < ROUNDS; i++) {
                lock.lock();
                try {
                    while (state != current) {
                        cur.await();
                    }
                    System.out.print(text);
                    state = next;
                    nxt.signal();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                } finally {
                    lock.unlock();
                }
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("--- synchronized version ---");
        new SynchronizedVersion().run();
        System.out.println();

        System.out.println("--- lock version ---");
        new LockVersion().run();
        System.out.println();
    }
}
