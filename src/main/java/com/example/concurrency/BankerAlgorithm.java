package com.example.concurrency;

import java.util.Arrays;

/**
 * 银行家算法（Banker's Algorithm）实现。
 *
 * <p>核心思路：
 * <ul>
 *     <li>维护 Available（可用资源）、Max（进程最大需求）、Allocation（已分配资源）</li>
 *     <li>Need = Max - Allocation，表示进程还需要的资源</li>
 *     <li>通过安全性算法检查系统是否处于安全状态，即能否找到一个安全序列</li>
 *     <li>进程请求资源时，先试探性分配，再检查安全性；安全则真正分配，不安全则回滚</li>
 * </ul>
 *
 * <p>银行家算法可以避免死锁，但要求进程事先声明最大资源需求。
 */
public class BankerAlgorithm {

    private final int processCount;  // 进程数量
    private final int resourceCount; // 资源种类数量

    private final int[] available;        // 可用资源向量
    private final int[][] max;            // 每个进程对每种资源的最大需求
    private final int[][] allocation;     // 每个进程当前已分配的资源
    private final int[][] need;           // 每个进程还需要的资源

    public BankerAlgorithm(int[] available, int[][] max, int[][] allocation) {
        this.processCount = max.length;
        this.resourceCount = available.length;

        if (allocation.length != processCount || max[0].length != resourceCount) {
            throw new IllegalArgumentException("资源矩阵维度不匹配");
        }

        this.available = Arrays.copyOf(available, resourceCount);
        this.max = deepCopy(max);
        this.allocation = deepCopy(allocation);
        this.need = new int[processCount][resourceCount];

        // 计算 Need 矩阵
        for (int i = 0; i < processCount; i++) {
            for (int j = 0; j < resourceCount; j++) {
                need[i][j] = max[i][j] - allocation[i][j];
                if (need[i][j] < 0) {
                    throw new IllegalArgumentException("进程 " + i + " 的已分配资源不能超过最大需求");
                }
            }
        }
    }

    /**
     * 检查当前系统是否处于安全状态，并返回一个安全序列。
     *
     * @return 安全序列（进程编号数组），如果不存在则返回 null
     */
    public int[] findSafeSequence() {
        int[] work = Arrays.copyOf(available, resourceCount);
        boolean[] finish = new boolean[processCount];
        int[] safeSequence = new int[processCount];
        int count = 0;

        while (count < processCount) {
            boolean found = false;

            for (int i = 0; i < processCount; i++) {
                if (!finish[i] && canSatisfy(need[i], work)) {
                    // 模拟进程 i 完成后释放资源
                    for (int j = 0; j < resourceCount; j++) {
                        work[j] += allocation[i][j];
                    }
                    finish[i] = true;
                    safeSequence[count++] = i;
                    found = true;
                }
            }

            // 一轮遍历下来没有找到可以完成的进程，说明不存在安全序列
            if (!found) {
                return null;
            }
        }

        return safeSequence;
    }

    /**
     * 判断系统当前是否处于安全状态。
     */
    public boolean isSafe() {
        return findSafeSequence() != null;
    }

    /**
     * 处理进程对资源的请求。
     *
     * @param processId 请求资源的进程编号
     * @param request   请求的资源向量
     * @return true 表示请求被允许，false 表示请求被拒绝
     */
    public boolean requestResources(int processId, int[] request) {
        if (processId < 0 || processId >= processCount) {
            throw new IllegalArgumentException("进程编号不合法");
        }
        if (request.length != resourceCount) {
            throw new IllegalArgumentException("请求资源向量维度不匹配");
        }

        // 1. 检查请求是否超过进程声明的最大需求
        for (int i = 0; i < resourceCount; i++) {
            if (request[i] > need[processId][i]) {
                System.out.println("请求超过进程 " + processId + " 的最大需求，拒绝分配");
                return false;
            }
        }

        // 2. 检查当前可用资源是否足够
        for (int i = 0; i < resourceCount; i++) {
            if (request[i] > available[i]) {
                System.out.println("可用资源不足，进程 " + processId + " 必须等待");
                return false;
            }
        }

        // 3. 试探性分配
        for (int i = 0; i < resourceCount; i++) {
            available[i] -= request[i];
            allocation[processId][i] += request[i];
            need[processId][i] -= request[i];
        }

        // 4. 检查安全性
        if (isSafe()) {
            System.out.println("允许进程 " + processId + " 的资源请求，系统仍处于安全状态");
            return true;
        } else {
            // 5. 不安全，回滚分配
            for (int i = 0; i < resourceCount; i++) {
                available[i] += request[i];
                allocation[processId][i] -= request[i];
                need[processId][i] += request[i];
            }
            System.out.println("若满足进程 " + processId + " 的请求会导致不安全状态，拒绝分配");
            return false;
        }
    }

    /**
     * 打印当前系统状态。
     */
    public void printState() {
        System.out.println("Available: " + Arrays.toString(available));
        System.out.println("Max:        " + Arrays.deepToString(max));
        System.out.println("Allocation: " + Arrays.deepToString(allocation));
        System.out.println("Need:       " + Arrays.deepToString(need));
    }

    private boolean canSatisfy(int[] need, int[] work) {
        for (int i = 0; i < resourceCount; i++) {
            if (need[i] > work[i]) {
                return false;
            }
        }
        return true;
    }

    private int[][] deepCopy(int[][] src) {
        int[][] dest = new int[src.length][];
        for (int i = 0; i < src.length; i++) {
            dest[i] = Arrays.copyOf(src[i], src[i].length);
        }
        return dest;
    }

    public static void main(String[] args) {
        // 3 种资源，初始可用资源分别为 3, 3, 2
        int[] available = {3, 3, 2};

        // 5 个进程对 3 种资源的最大需求
        int[][] max = {
                {7, 5, 3},
                {3, 2, 2},
                {9, 0, 2},
                {2, 2, 2},
                {4, 3, 3}
        };

        // 5 个进程当前已分配的资源
        int[][] allocation = {
                {0, 1, 0},
                {2, 0, 0},
                {3, 0, 2},
                {2, 1, 1},
                {0, 0, 2}
        };

        BankerAlgorithm banker = new BankerAlgorithm(available, max, allocation);
        System.out.println("=== 初始状态 ===");
        banker.printState();

        int[] safeSequence = banker.findSafeSequence();
        if (safeSequence != null) {
            System.out.println("\n系统处于安全状态，安全序列: " + Arrays.toString(safeSequence));
        } else {
            System.out.println("\n系统不处于安全状态");
        }

        // 进程 1 请求资源 (1, 0, 2)
        System.out.println("\n=== 进程 1 请求资源 [1, 0, 2] ===");
        boolean granted = banker.requestResources(1, new int[]{1, 0, 2});
        System.out.println("请求结果: " + (granted ? "允许" : "拒绝"));

        // 进程 4 请求资源 (3, 3, 0)，预期会导致不安全状态而被拒绝
        System.out.println("\n=== 进程 4 请求资源 [3, 3, 0] ===");
        granted = banker.requestResources(4, new int[]{3, 3, 0});
        System.out.println("请求结果: " + (granted ? "允许" : "拒绝"));

        System.out.println("\n=== 最终状态 ===");
        banker.printState();
    }
}
