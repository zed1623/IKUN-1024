package com.ljh.controller;

import com.ljh.pojo.entity.Project;
import com.ljh.properties.JwtProperties;
import com.ljh.result.Result;
import com.ljh.service.ProjectService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/project")
@Slf4j
@Api(tags = "仓库项目相关接口")
public class ProjectController {

    @Autowired
    private ProjectService projectService;


    /**
     * 查询指定 GitHub 项目的信息
     * @param url
     * @return
     */
    @PostMapping("/analyseUrl")
    public Result<String> analyseUrl(String url) {
        projectService.analyseUrl(url);
        return  Result.success();
    }

    /**
     * 通过 URL 查询指定项目的信息。
     * @param url
     * @return
     */
    @PostMapping("/selectUrl")
    public Result<Project> selectUrl(String url) {
         Project project =  projectService.selectUrl(url);
        return  Result.success(project);
    }

    /**
     * 删除指定项目信息
     * @return
     */
    @DeleteMapping("/deleteRepoUrl")
    public Result<String> deleteRepoUrl(String repoUrl) {
        projectService.deleteRepoUrl(repoUrl);
        return Result.success();
    }
}
