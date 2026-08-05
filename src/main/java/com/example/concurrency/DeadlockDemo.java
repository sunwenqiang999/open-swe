package com.example.concurrency;

/**
 * 死锁示例：两个线程互相等待对方释放锁。
 *
 * <p>运行后通常会卡住，可用 jstack 观察死锁。
 */
public class DeadlockDemo {

    private static final Object LOCK_A = new Object();
    private static final Object LOCK_B = new Object();

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            synchronized (LOCK_A) {
                System.out.println("Thread-1 acquired LOCK_A");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                System.out.println("Thread-1 waiting for LOCK_B");
                synchronized (LOCK_B) {
                    System.out.println("Thread-1 acquired LOCK_B");
                }
            }
        }, "Thread-1");

        Thread t2 = new Thread(() -> {
            synchronized (LOCK_B) {
                System.out.println("Thread-2 acquired LOCK_B");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                System.out.println("Thread-2 waiting for LOCK_A");
                synchronized (LOCK_A) {
                    System.out.println("Thread-2 acquired LOCK_A");
                }
            }
        }, "Thread-2");

        t1.start();
        t2.start();
    }
}
