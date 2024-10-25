package com.ljh.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 贡献实体类，存储开发者对项目的贡献，包括提交次数、代码行数变动等。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Contribution {

    /**
     * 对应的开发者 ID
     * */
    private long developerId;

    /**
     * 对应的项目 ID
     * */
    private long projectId;

    /**
     * 提交的总次数
     * */
    private int commitsCount;

    /**
     * 新增的代码行数
     * */
    private int additions;

    /**
     * 删除的代码行数
     * */
    private int deletions;

    /**
     * 最近一次提交的日期
     * */
    private java.util.Date lastCommitDate;

    /**
     * 贡献分数（综合提交次数、代码修改量、项目重要性等计算）
     * */
    private float contributionScore;


}