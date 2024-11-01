package com.ljh.service;


import com.ljh.pojo.entity.Project;

public interface ProjectService {

    /**
     * 查询指定 GitHub 项目的信息
     * @param url
     */
    void analyseUrl(String url);


    /**
     * 通过 URL 查询指定项目的信息。
     * @param url
     * @return
     */
    Project selectUrl(String url);

    /**
     * 删除指定项目信息
     * @param repoUrl
     */
    void deleteRepoUrl(String repoUrl);
}
