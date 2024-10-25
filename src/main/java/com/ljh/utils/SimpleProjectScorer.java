package com.ljh.utils;

public class SimpleProjectScorer {
    // 星标和Fork的权重，可以根据实际需求调整
    private static final double STAR_WEIGHT = 0.7;
    private static final double FORK_WEIGHT = 0.3;

    /**
     * 根据星标数量和Fork数量计算项目评分
     *
     * @param stars 项目的星标数量
     * @param forks 项目的Fork数量
     * @return 计算出的项目评分
     */
    public static double calculateScore(int stars, int forks) {
        // 计算评分：星标数量 * 权重 + Fork 数量 * 权重
        double score = (stars * STAR_WEIGHT) + (forks * FORK_WEIGHT);
        return score;
    }
}
