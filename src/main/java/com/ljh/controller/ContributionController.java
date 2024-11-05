package com.ljh.controller;

import com.ljh.pojo.entity.Contribution;
import com.ljh.result.Result;
import com.ljh.service.ContributionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/contribution")
public class ContributionController {

    @Autowired
    private ContributionService contributionService;

    /**
     * 存储贡献信息
     * @param contribution
     * @return
     */
    @PostMapping("/add")
    public Result<String> addContribution(@RequestBody Contribution contribution) {
        contributionService.saveContribution(contribution);
        return Result.success();
    }
}
