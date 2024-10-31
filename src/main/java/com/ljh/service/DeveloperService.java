package com.ljh.service;


import com.ljh.pojo.dto.DeveloperPageQueryDTO;
import com.ljh.pojo.entity.Developer;
import com.ljh.result.PageResult;

public interface DeveloperService {


    /**
     * 通过github API 查询项目贡献者的信息
     */
    void getDeveloper();

    /**
     * 根据用户查找用户信息
     * @param login
     * @return
     */
    Developer getLoginDeveloper(String login);

    /**
     * 用户分页查询
     * @param developerPageQueryDTO
     * @return
     */
    PageResult pageQuery(DeveloperPageQueryDTO developerPageQueryDTO);
}
