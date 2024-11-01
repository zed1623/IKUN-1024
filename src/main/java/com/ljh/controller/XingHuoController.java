package com.ljh.controller;

import com.ljh.config.SparkManager;
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
    /**
     * 讯飞星火ai对话
     *
     * @param
     * @param question
     * @return
     */
    @PostMapping("/aiChat")
    public Result<String> aiChart(String question, HttpServletRequest request) {
        // 调用 AI 服务
        String sendMesToAIUseXingHuo = sparkManager.sendMesToAIUseXingHuo(question);
        return Result.success(sendMesToAIUseXingHuo);
    }
}
