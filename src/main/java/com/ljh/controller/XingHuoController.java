package com.ljh.controller;

import com.ljh.config.SparkManager;
import com.ljh.mapper.ProjectMapper;
import com.ljh.pojo.entity.Project;
import com.ljh.result.Result;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/ai")
@Slf4j
@Api(tags = "ai对话相关接口")
public class XingHuoController {

    @Autowired
    private SparkManager sparkManager;

    @Autowired
    private ProjectMapper projectMapper;



    /**
     * 讯飞星火ai对话
     *
     * @param
     * @param
     * @return
     */
    @PostMapping("/aiChat")
    public Result<String> aiChart() {
        Project project = projectMapper.getMaxIdProject();
        String projectAsString = project.toString();
        // 调用 AI 服务
        String sendMesToAIUseXingHuo = sparkManager.sendMesToAIUseXingHuo(projectAsString);
        return Result.success(sendMesToAIUseXingHuo);
    }
}
