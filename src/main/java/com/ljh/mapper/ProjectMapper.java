package com.ljh.mapper;


import com.ljh.pojo.entity.Project;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProjectMapper {

    void insertProject(Project project);

    Project getProjectsForDeveloper(String url);

    long selectByUrl(String repoUrl);

    void updateProjectUser(@Param("projectUser") int totalContributors,@Param("repoUrl") String projectUrl1);


}
