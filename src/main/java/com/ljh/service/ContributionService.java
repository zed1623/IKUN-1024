package com.ljh.service;


import com.ljh.pojo.dto.DeveloperPageQueryDTO;
import com.ljh.pojo.entity.Contribution;
import com.ljh.pojo.entity.Developer;
import com.ljh.result.PageResult;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface ContributionService {


    /**
     * 存储贡献信息
     * @param contribution
     */
    void saveContribution(Contribution contribution);
}
