package com.ljh.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 项目实体类，存储 GitHub 上的开源项目的基本信息，用于评估开发者的贡献度和项目的重要性。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Project implements Serializable {

    /**
     * 项目唯一标识
     * */
    private Long id;


    /**
     * 项目名称
     * */
    private String name;

    /**
     * 项目成员数量
     */
    private int projectUser;

    /**
     * 项目总提交数
     */
    private int totalCommits;

    /**
     * 项目总代码行数
     */
    private int projectCode;

    /**
     * 项目介绍
     */
    private String description;

    /**
     * 项目所有者的 GitHub 用户名
     * */
    private String ownerLogin;

    /**
     * 仓库的 GitHub 链接
     * */
    private String repoUrl;

    /**
     * 项目 GitHub 星标数量
     * */
    private int stars;

    /**
     * 项目 Fork 的数量
     * */
    private int forks;

    /**
     * 项目开放的 issue 数量
     * */
    private int issues;

    /**
     * 项目创建时间
     * */
    private LocalDateTime createdAt;

    /**
     * 项目最后更新时间
     * */
    private LocalDateTime updatedAt;

    /**
     * 项目的重要性评分（根据星标、fork 等权重计算）
     * */
    private double importanceScore;

    /**
     * 项目关联的其他项目，用于表示该项目与其他项目的关系（例如被 Fork 或引用）。
     */
    private String[] linkedProjects;



}

