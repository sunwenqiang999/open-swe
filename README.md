# open-swe2

Java 设计模式与并发编程示例集合。

## 项目结构

```text
open-swe2/
├── pom.xml
├── src/
│   ├── main/java/com/example/
│   │   ├── singleton/
│   │   │   └── SingletonPatterns.java
│   │   └── concurrency/
│   │       ├── Print123.java
│   │       ├── ProducerConsumer.java
│   │       └── DeadlockDemo.java
│   └── test/java/com/example/
└── target/
```

## 构建

本项目使用 Maven 构建，JDK 要求 11+。

```bash
mvn compile
```

## 运行示例

```bash
java -cp target/classes com.example.singleton.SingletonPatterns
java -cp target/classes com.example.concurrency.Print123
java -cp target/classes com.example.concurrency.ProducerConsumer
java -cp target/classes com.example.concurrency.DeadlockDemo
```

> 注意：[`DeadlockDemo`](src/main/java/com/example/concurrency/DeadlockDemo.java) 运行后会因死锁而卡住，可用 `jstack <pid>` 观察。
