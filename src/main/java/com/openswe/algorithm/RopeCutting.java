package com.openswe.algorithm;

import java.util.Arrays;

/**
 * 绳子切割最小开销问题（区间DP）
 *
 * 问题描述：
 * 有一根长度为 n 的绳子，给定若干个切割点（位置）。
 * 在某个点切割时，开销等于当前绳子的总长度。
 * 问切割所有指定点的最小总开销。
 *
 * 示例：
 * n = 6，切割点 = [2, 3]
 * 最优策略：先切3（开销6），绳子变成[0,3]和[3,6]两段
 *           再切2（开销3），总开销 = 6 + 3 = 9
 */
public class RopeCutting {

    /**
     * 计算最小切割开销
     *
     * @param n     绳子总长度
     * @param cuts  切割点的位置数组（不包含0和n）
     * @return 最小总开销
     */
    public static int minCost(int n, int[] cuts) {
        if (cuts == null || cuts.length == 0) {
            return 0;
        }

        // 排序切割点，方便区间DP
        Arrays.sort(cuts);

        // 构建包含端点的完整点数组
        // 长度为 cuts.length + 2（包含0和n）
        int m = cuts.length + 2;
        int[] points = new int[m];
        points[0] = 0;
        points[m - 1] = n;
        for (int i = 0; i < cuts.length; i++) {
            points[i + 1] = cuts[i];
        }

        // dp[i][j] 表示从点 i 到点 j 这段绳子的最小切割开销
        // i 和 j 是 points 数组的索引
        int[][] dp = new int[m][m];

        // len 表示区间内点的个数（不包含端点）
        // 区间长度从2开始（至少包含一个切割点）
        for (int len = 2; len < m; len++) {
            for (int i = 0; i + len < m; i++) {
                int j = i + len;
                // 当前区间长度 = points[j] - points[i]
                dp[i][j] = Integer.MAX_VALUE;
                // 尝试所有可能的第一个切割点 k（在 i 和 j 之间）
                for (int k = i + 1; k < j; k++) {
                    int cost = dp[i][k] + dp[k][j] + (points[j] - points[i]);
                    dp[i][j] = Math.min(dp[i][j], cost);
                }
            }
        }

        return dp[0][m - 1];
    }

    public static void main(String[] args) {
        // 示例：n = 6，切割点 [2, 3]
        int n = 6;
        int[] cuts = {2, 3};
        System.out.println("绳子长度: " + n);
        System.out.println("切割点: " + Arrays.toString(cuts));
        System.out.println("最小开销: " + minCost(n, cuts));  // 期望: 9

        // 更多测试用例
        System.out.println();

        // 测试1: n = 7, cuts = [1, 3, 4, 5]
        int n2 = 7;
        int[] cuts2 = {1, 3, 4, 5};
        System.out.println("绳子长度: " + n2);
        System.out.println("切割点: " + Arrays.toString(cuts2));
        System.out.println("最小开销: " + minCost(n2, cuts2));

        // 测试2: n = 10, cuts = [1, 2, 7]
        int n3 = 10;
        int[] cuts3 = {1, 2, 7};
        System.out.println();
        System.out.println("绳子长度: " + n3);
        System.out.println("切割点: " + Arrays.toString(cuts3));
        System.out.println("最小开销: " + minCost(n3, cuts3));

        // 测试3: 无切割点
        int n4 = 10;
        int[] cuts4 = {};
        System.out.println();
        System.out.println("绳子长度: " + n4);
        System.out.println("切割点: " + Arrays.toString(cuts4));
        System.out.println("最小开销: " + minCost(n4, cuts4));  // 期望: 0
    }
}
