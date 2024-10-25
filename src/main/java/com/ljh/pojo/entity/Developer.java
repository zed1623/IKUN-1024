package com.ljh.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 开发者实体类，存储开发者的基本信息，包括个人资料、所属国家、领域等信息。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Developer {

    /**
     * 开发者唯一标识
     * */
    private long id;

    /**
     * GitHub 用户名
     * */
    private String login;

    /**
     * 开发者全名
     * */
    private String name;

    /**
     * 开发者邮箱
     * */
    private String email;

    /**
     * 开发者头像
     * */
    private String avatarUrl;

    /**
     * 开发者博客链接
     * */
    private String blogUrl;

    /**
     * GitHub 个人主页链接
     * */
    private String profileUrl;

    /**
     * 开发者个人介绍
     * */
    private String bio;

    /**
     * 开发者所属国家（可猜测）
     * */
    private String nation;

    /**
     * 所属国家的置信度（如果通过猜测得出）
     * */
    private float nationConfidence;

    /**
     * 开发者所属的技术领域
     * */
    private String field;

    /**
     * 技术能力评价分数（根据 TalentRank 算法计算）
     * */
    private float talentRank;

    /**
     * 开发者账号创建时间
     * */
    private LocalDateTime createdTime;

    /**
     * 最后更新时间
     * */
    private LocalDateTime updatedTime;
}
