package com.ljh.mapper;


import com.github.pagehelper.Page;
import com.ljh.pojo.dto.DeveloperPageQueryDTO;
import com.ljh.pojo.entity.Developer;
import com.ljh.pojo.entity.Project;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DeveloperMapper {


    void saveContributorToDatabase(@Param("login") String login, @Param("projectUrl") String projectUrl);

    String findContributorToDatabase(@Param("login") String username);
    /**
     * 返回项目前10名贡献者
     * @return
     */
    List<String> getTop10Developers();

    void updateDeveloperByLogin(Developer developer);

    void updateDeveloperInfo(String login, String name, String email, String blogUrl, String profileUrl, String bio,String createdAt, String updatedAt);

    /**
     * 根据用户名查找用户信息
     * @param login
     * @return
     */
    Developer findDeveloperByLogin(String login);

    /**
     * 用户分页查询
     * @param developerPageQueryDTO
     * @return
     */
    Page<Developer> pageQuery(DeveloperPageQueryDTO developerPageQueryDTO);

    /**
     * 查询全部用户信息
     * @return
     */
    List<Developer> getAllDevelopers();

    /**
     * 删除所有用户信息
     * @param projectUrl
     */
    void deleteDeveloper(String projectUrl);

    /**
     * 根据nation查询用户信息
     * @param nation
     * @return
     */
    List<Developer> findDevelopersByNation(String nation);
}