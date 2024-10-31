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
import org.springframework.web.bind.annotation.*;

/**
 * 员工管理
 */
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

//    /**
//     * 登录
//     *
//     * @param employeeLoginDTO
//     * @return
//     */
//    @PostMapping("/login")
//    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
//        log.info("登录：{}", employeeLoginDTO);
//
//        Employee employee = employeeService.login(employeeLoginDTO);
//
//        //登录成功后，生成jwt令牌
//        Map<String, Object> claims = new HashMap<>();
//        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
//        String token = JwtUtil.createJWT(
//                jwtProperties.getAdminSecretKey(),
//                jwtProperties.getAdminTtl(),
//                claims);
//
//        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
//                .id(employee.getId())
//                .userName(employee.getUsername())
//                .name(employee.getName())
//                .token(token)
//                .build();
//
//        return Result.success(employeeLoginVO);
//    }
//
//    /**
//     * 退出
//     *
//     * @return
//     */
//    @PostMapping("/logout")
//    //@ApiOperation(value = "员工退出")
//    public Result<String> logout() {
//        return Result.success();
//    }




}
