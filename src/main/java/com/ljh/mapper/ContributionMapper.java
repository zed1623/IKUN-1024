package com.ljh.mapper;


import com.github.pagehelper.Page;
import com.ljh.pojo.dto.DeveloperPageQueryDTO;
import com.ljh.pojo.entity.Contribution;
import com.ljh.pojo.entity.Developer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ContributionMapper {


    /**
     * 存储贡献信息
     * @param contribution
     */
    void insertContribution(Contribution contribution);
}