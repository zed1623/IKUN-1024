package com.ljh.mapper;


import com.ljh.pojo.entity.Project;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProjectMapper {

    void insertProject(Project project);

    Project getProjectsForDeveloper(String url);

    Long selectByUrl(String repoUrl);

    void updateProjectUser(@Param("projectUser") int totalContributors,@Param("repoUrl") String projectUrl1);


    /**
     * 删除指定项目信息
     * @param repoUrl
     */
    void deleteRepoUrl(String repoUrl);

    /**
     * 获取表中 id 最大的项目
     * @return id 最大的项目数据
     */
    Project getMaxIdProject();

}
