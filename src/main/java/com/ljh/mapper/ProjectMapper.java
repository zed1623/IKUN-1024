package com.ljh.mapper;


import com.ljh.pojo.entity.Project;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProjectMapper {

    void insertProject(Project project);

}
