package com.example.singleton;

/**
 * 单例模式常见实现方式对比。
 *
 * <p>包含：饿汉式、懒汉式（线程不安全/安全）、双重检查锁、静态内部类、枚举。
 */
public class SingletonPatterns {

    private SingletonPatterns() {
        // 工具类，禁止实例化
    }

    /**
     * 1. 饿汉式 - 静态常量
     *
     * <p>优点：简单、线程安全、无锁开销。
     * <p>缺点：类加载时即初始化，可能浪费内存。
     */
    public static class HungryStaticConstant {
        private static final HungryStaticConstant INSTANCE = new HungryStaticConstant();

        private HungryStaticConstant() {
        }

        public static HungryStaticConstant getInstance() {
            return INSTANCE;
        }
    }

    /**
     * 2. 饿汉式 - 静态代码块
     */
    public static class HungryStaticBlock {
        private static final HungryStaticBlock INSTANCE;

        static {
            INSTANCE = new HungryStaticBlock();
        }

        private HungryStaticBlock() {
        }

        public static HungryStaticBlock getInstance() {
            return INSTANCE;
        }
    }

    /**
     * 3. 懒汉式 - 线程不安全（仅单线程环境可用）
     */
    public static class LazyUnsafe {
        private static LazyUnsafe instance;

        private LazyUnsafe() {
        }

        public static LazyUnsafe getInstance() {
            if (instance == null) {
                instance = new LazyUnsafe();
            }
            return instance;
        }
    }

    /**
     * 4. 懒汉式 - 方法级同步（线程安全但效率低）
     */
    public static class LazySynchronized {
        private static LazySynchronized instance;

        private LazySynchronized() {
        }

        public static synchronized LazySynchronized getInstance() {
            if (instance == null) {
                instance = new LazySynchronized();
            }
            return instance;
        }
    }

    /**
     * 5. 懒汉式 - 双重检查锁（DCL，推荐）
     */
    public static class LazyDoubleCheck {
        private static volatile LazyDoubleCheck instance;

        private LazyDoubleCheck() {
        }

        public static LazyDoubleCheck getInstance() {
            if (instance == null) {
                synchronized (LazyDoubleCheck.class) {
                    if (instance == null) {
                        instance = new LazyDoubleCheck();
                    }
                }
            }
            return instance;
        }
    }

    /**
     * 6. 静态内部类（延迟加载 + 线程安全，推荐）
     */
    public static class LazyStaticInnerClass {
        private LazyStaticInnerClass() {
        }

        private static class Holder {
            private static final LazyStaticInnerClass INSTANCE = new LazyStaticInnerClass();
        }

        public static LazyStaticInnerClass getInstance() {
            return Holder.INSTANCE;
        }
    }

    /**
     * 7. 枚举单例（最简洁、防反射和反序列化，Effective Java 推荐）
     */
    public enum EnumSingleton {
        INSTANCE;

        public void doSomething() {
            System.out.println("Enum singleton is doing something");
        }
    }

    public static void main(String[] args) {
        System.out.println("HungryStaticConstant: " + HungryStaticConstant.getInstance());
        System.out.println("HungryStaticBlock: " + HungryStaticBlock.getInstance());
        System.out.println("LazyDoubleCheck: " + LazyDoubleCheck.getInstance());
        System.out.println("LazyStaticInnerClass: " + LazyStaticInnerClass.getInstance());
        EnumSingleton.INSTANCE.doSomething();
    }
}
