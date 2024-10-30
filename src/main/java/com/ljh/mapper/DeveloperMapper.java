package com.ljh.mapper;


import com.ljh.pojo.entity.Developer;
import com.ljh.pojo.entity.Project;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DeveloperMapper {


    void saveContributorToDatabase(String username);

    /**
     * 返回项目前10名贡献者
     * @return
     */
    List<String> getTop10Developers();

    void updateDeveloperByLogin(Developer developer);

    void updateDeveloperInfo(String login, String name, String email, String blogUrl, String profileUrl, String bio,String createdAt, String updatedAt);
}
