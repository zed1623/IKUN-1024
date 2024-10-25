package com.ljh.controller;//package com.ljh.controller.admin;

import com.ljh.properties.JwtProperties;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


//    @PostMapping("/add")
//    public Result<> add( Developer developer) {
//
//    };

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
