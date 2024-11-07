package com.ljh.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 领域实体类，存储技术领域的信息，开发者可以根据技术领域分类。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Field implements Serializable {

    /**
     * 技术领域唯一标识
     * */
    private long id;

    /**
     * 技术领域名称
     * */
    private String name;

    /**
     * 领域描述
     * */
    private String description;

}
