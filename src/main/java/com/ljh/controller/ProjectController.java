package com.ljh.controller;

import com.ljh.properties.JwtProperties;
import com.ljh.result.Result;
import com.ljh.service.ProjectService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/project")
@Slf4j
@Api(tags = "仓库项目相关接口")
public class ProjectController {


    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private ProjectService projectService;


    /**
     * 接收前端传过来的url
     * @param url
     * @return
     */
    @PostMapping("/analyseUrl")
    public Result<String> analyseUrl(String url) {
        projectService.analyseUrl(url);
        return  Result.success();
    }
}
