package com.ljh.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 关系实体类，存储开发者之间的协作关系，通过这些关系可以推测国家、领域等信息。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Relation {

    /**
     * 开发者 ID
     * */
    private long developerId;

    /**
     * 关联的开发者 ID
     * */
    private long relatedDeveloperId;

    /**
     * 共同协作的次数（如参与同一个项目的次数）
     * */
    private int collaborationCount;

}
