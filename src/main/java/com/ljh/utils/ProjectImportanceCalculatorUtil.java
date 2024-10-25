package com.ljh.utils;

import com.ljh.pojo.entity.Project;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ProjectImportanceCalculatorUtil {
    // 星标和Fork的初始权重
    private static final double STAR_WEIGHT = 0.7;
    private static final double FORK_WEIGHT = 0.3;

    // 阻尼因子 (类似于PageRank)
    private static final double DAMPING_FACTOR = 0.85;

    // 迭代的最大次数
    private static final int MAX_ITERATIONS = 100;

    // 评分收敛阈值
    private static final double CONVERGENCE_THRESHOLD = 0.0001;

    /**
     * 根据星标、Fork、以及项目引用关系计算项目的重要性评分
     * @param projects 各项目的星标、Fork数据和引用关系
     * @return 各项目最终的重要性评分
     */
    public static Map<String, Double> calculateImportanceScores(Map<String, Project> projects) {
        // 初始化每个项目的评分
        Map<String, Double> scores = new HashMap<>();
        for (String projectName : projects.keySet()) {
            scores.put(projectName, 1.0);  // 初始评分设为1.0
        }

        // 迭代调整评分，模拟PageRank的评分传递过程
        for (int i = 0; i < MAX_ITERATIONS; i++) {
            Map<String, Double> newScores = new HashMap<>();

            double maxChange = 0;  // 收敛条件

            // 遍历每个项目
            for (Map.Entry<String, Project> entry : projects.entrySet()) {
                String projectName = entry.getKey();
                Project project = entry.getValue();

                // 基于星标和Fork的初始评分
                double baseScore = project.getStars() * STAR_WEIGHT + project.getForks() * FORK_WEIGHT;

                // 计算从其他项目传递过来的评分
                double linkScore = 0;
                // 检查 project.getLinkedProjects() 是否为 null
                if (project.getLinkedProjects() != null) {
                    for (String linkedProject : project.getLinkedProjects()) {
                        // 检查 projects.get(linkedProject) 是否为 null
                        Project linkedProjectObj = projects.get(linkedProject);
                        if (linkedProjectObj != null && linkedProjectObj.getLinkedProjects() != null) {
                            double linkedProjectScore = scores.getOrDefault(linkedProject, 0.0);
                            int outLinks = linkedProjectObj.getLinkedProjects().length;
                            if (outLinks > 0) {
                                linkScore += linkedProjectScore / outLinks;
                            }
                        }
                    }
                }


                // 计算最终评分（阻尼因子影响）
                double newScore = (1 - DAMPING_FACTOR) * baseScore + DAMPING_FACTOR * linkScore;
                newScores.put(projectName, newScore);

                // 计算收敛条件（评分变化幅度）
                maxChange = Math.max(maxChange, Math.abs(newScore - scores.get(projectName)));
            }

            // 更新评分
            scores = newScores;

            // 如果评分变化幅度小于阈值，提前退出迭代
            if (maxChange < CONVERGENCE_THRESHOLD) {
                break;
            }
        }

        return scores;
    }
}
