package com.ljh.service.impl;

import com.ljh.mapper.ContributionMapper;
import com.ljh.pojo.entity.Contribution;
import com.ljh.service.ContributionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ContributionServiceImpl implements ContributionService {

    @Autowired
    private ContributionMapper contributionMapper;

    /**
     * 存储贡献信息
     *
     * @param contribution
     */
    @Override
    public void saveContribution(Contribution contribution) {
        contributionMapper.insertContribution(contribution);
    }
}
