package com.ljh.controller;

import com.ljh.pojo.dto.DeveloperPageQueryDTO;
import com.ljh.pojo.entity.Developer;
import com.ljh.properties.JwtProperties;
import com.ljh.result.PageResult;
import com.ljh.result.Result;
import com.ljh.service.DeveloperService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


@RestController
@RequestMapping("/developer")
@Slf4j
@Api(tags = "开发者接口")
public class DeveloperController {


    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private DeveloperService developerService;


    /**
     * 通过github API 查询项目贡献者的信息
     * @param
     * @return
     */
    @GetMapping("/getDeveloper")
    public Result<String> getDeveloper() {
        developerService.getDeveloper();
        return Result.success();
    };

    /**
     * 根据用户查找用户信息
     * @param login
     * @return
     */
    @PostMapping("/getLoginDeveloper")
    public Result<Developer> getLoginDeveloper(String login) {
        Developer developer =  developerService.getLoginDeveloper(login);
        return Result.success(developer);
    };

    /**
     * 用户分页查询
     * @param developerPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> page(DeveloperPageQueryDTO developerPageQueryDTO){
        log.info("分页查询：{}", developerPageQueryDTO);
        PageResult pageResult = developerService.pageQuery(developerPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 导出用户信息为excel表格
     * @param response
     * @return
     */
    @GetMapping("/exportDevelopers")
    public Result<String> exportDevelopers(HttpServletResponse response) {
        developerService.exportDevelopersToExcel(response);
        return Result.success();
    }

    /**
     * 删除用户信息的接口
     * @return
     */
    @DeleteMapping("/deleteProjectUrl")
    public Result<String> deleteDeveloper(String projectUrl) {
        developerService.deleteDeveloper(projectUrl);
        return Result.success();
    }

    /**
     * 根据nation查询用户信息
     * @param nation
     * @return
     */
    @PostMapping("/developersNation")
    public Result<List<Developer>> getDevelopersByNation(String nation) {
       List<Developer> developerList = developerService.getDevelopersByNation(nation);
        return Result.success(developerList);
    }

    /**
     * 根据领域搜索开发者并按 TalentRank 排序，可选使用 Nation 作为筛选条件
     * @param field 搜索的领域
     * @param nation (可选)筛选的国家
     * @return 开发者列表
     */
    @GetMapping("/search")
    public Result<List<Developer>> searchDevelopersByField(
            @RequestParam String field,
            @RequestParam(required = false) String nation) {
        List<Developer> developers = developerService.searchDevelopersByFieldAndNation(field, nation);
        return Result.success(developers);
    }
}
