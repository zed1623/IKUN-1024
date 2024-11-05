package com.ljh.service.impl;


import com.ljh.exception.BaseException;
import com.ljh.mapper.DeveloperMapper;
import com.ljh.mapper.ProjectMapper;
import com.ljh.mapper.ProjectMapper;
import com.ljh.pojo.entity.Project;
import com.ljh.pojo.entity.Project;
import com.ljh.service.ProjectService;
import com.ljh.utils.ProjectImportanceCalculatorUtil;
import com.ljh.utils.SimpleProjectScorer;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class ProjectServiceImpl implements ProjectService {

    private static final String GITHUB_API_URL = "https://api.github.com/repos/";
    private static final String token = "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";

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private DeveloperMapper developerMapper;

    /**
     * 查询指定 GitHub 项目的信息
     * @param url
     */
    public void analyseUrl(String url) {
        try {
            // 检查数据库中是否已有该项目记录，如果存在则删除
            long existingProjectId = projectMapper.selectByUrl(url);
            if (existingProjectId > 0) {
                projectMapper.deleteRepoUrl(url);
                developerMapper.deleteDeveloper(url);
                System.out.println("已删除数据库中现有的项目记录。");
            }

            // 分析 URL，提取出仓库拥有者和仓库名称
            String[] parts = url.split("/");
            if (parts.length < 5) {
                System.out.println("Invalid GitHub repository URL.");
                return;
            }

            // 仓库拥有者和仓库名称
            String owner = parts[3];
            String repo = parts[4];

            // 获取项目的基本信息
            JSONObject projectInfo = fetchProjectInfo(owner, repo);
            if (projectInfo == null) {
                System.out.println("获取仓库信息失败。");
                return;
            }

            // 解析项目信息
            String name = projectInfo.getString("name");
            String description = projectInfo.getString("description");
            int stars = projectInfo.getInt("stargazers_count");
            int forks = projectInfo.getInt("forks_count");
            int issues = projectInfo.getInt("open_issues_count");
            String repoUrl = projectInfo.getString("html_url");

            // 获取关联的项目（例如 Fork 的项目）
            String[] linkedProjects = fetchLinkedProjects(owner, repo);

            // 创建 Project 对象并设置字段
            Project project = new Project();
            project.setName(name);
            project.setDescription(description);
            project.setOwnerLogin(owner);
            project.setRepoUrl(repoUrl);
            project.setStars(stars);
            project.setForks(forks);
            project.setIssues(issues);
            project.setCreatedAt(LocalDateTime.now());
            project.setUpdatedAt(LocalDateTime.now());
            project.setLinkedProjects(linkedProjects);

            // 准备 Map 来传入给 calculateImportanceScores
            Map<String, Project> projects = new HashMap<>();
            projects.put(project.getName(), project);  // 将当前项目添加到 Map 中

            // 计算项目的重要性评分
            float scores = (float) SimpleProjectScorer.calculateScore(stars, forks);

            int totalCommits = getTotalCommits(owner, repo);
            int projectCode = getTotalLinesOfCode(owner, repo);
            System.out.println("total commits: " + totalCommits);
            System.out.println("projectCode: " + projectCode);

            project.setTotalCommits(totalCommits);
            project.setProjectCode(projectCode);

            // 获取当前项目的评分
            project.setImportanceScore(scores);
            System.out.println("项目重要性评分已计算：" + scores);

            // 插入数据库
            projectMapper.insertProject(project);
            System.out.println("项目信息已保存到数据库。");

            // 获取并输出贡献者信息
            fetchAndSaveContributorsWithDelay(projectInfo.getString("contributors_url"), url);

        } catch (Exception e) {
            throw new BaseException("查询指定 GitHub 项目的信息失败：" + e.getMessage());
        }
    }


    /**
     * 通过 URL 查询指定项目的信息。
     * @param url
     * @return
     */
    @Override
    public Project selectUrl(String url) {
        try {
            return projectMapper.getProjectsForDeveloper(url); // 调用MyBatis映射的方法
        } catch (Exception e) {
            throw new BaseException("通过 URL 查询指定项目的信息失败：" + e.getMessage());
        }
    }

    /**
     * 删除指定项目信息
     *
     * @param repoUrl
     */
    @Override
    public void deleteRepoUrl(String repoUrl) {
        projectMapper.deleteRepoUrl(repoUrl);
    }

    /**
     * 获取项目的基本信息
     * @param owner
     * @param repo
     * @return
     * @throws Exception
     */
    private JSONObject fetchProjectInfo(String owner, String repo) throws Exception {
        String urlString = GITHUB_API_URL + owner + "/" + repo;
        URL apiUrl = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/vnd.github.v3+json");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder content = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            connection.disconnect();
            return new JSONObject(content.toString());
        } else {
            System.out.println("获取项目信息失败，HTTP 响应码：" + responseCode);
            connection.disconnect();
            return null;
        }
    }

    /**
     * 获取关联项目信息（例如Fork的项目）
     * @param owner
     * @param repo
     * @return
     * @throws Exception
     */
    private String[] fetchLinkedProjects(String owner, String repo) throws Exception {
        String forksUrl = GITHUB_API_URL + owner + "/" + repo + "/forks";
        URL url = new URL(forksUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/vnd.github.v3+json");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder content = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            connection.disconnect();

            // 解析 Fork 的项目
            JSONArray forksArray = new JSONArray(content.toString());
            String[] linkedProjects = new String[forksArray.length()];
            for (int i = 0; i < forksArray.length(); i++) {
                linkedProjects[i] = forksArray.getJSONObject(i).getString("full_name");
            }
            return linkedProjects;
        } else {
            System.out.println("获取关联项目失败，HTTP 响应码：" + responseCode);
            connection.disconnect();
            return new String[0];
        }
    }


    /**
     * 获取并保存前10名贡献者信息到数据库
     * @param contributorsUrl
     * @param projectUrl1
     * @throws Exception
     */
    private void fetchAndSaveContributorsWithDelay(String contributorsUrl,String projectUrl1) throws Exception {

        URL url = new URL(contributorsUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/vnd.github.v3+json");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder content = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            connection.disconnect();

            // 解析并输出贡献者信息
            JSONArray contributorsArray = new JSONArray(content.toString());
            int totalContributors = contributorsArray.length(); // 获取总贡献者数量
            System.out.println("项目总贡献者数量: " + totalContributors);
            projectMapper.updateProjectUser(totalContributors,projectUrl1);
            System.out.println("前10名贡献者：");


            // 获取前10名贡献者并保存到数据库
            for (int i = 0; i < Math.min(contributorsArray.length(), 10); i++) {
                JSONObject contributor = contributorsArray.getJSONObject(i);
                String username = contributor.getString("login");

                System.out.println("用户名: " + username);

                // 将用户名和项目url保存到数据库
                developerMapper.saveContributorToDatabase(username,projectUrl1);
            }
        } else {
            System.out.println("获取贡献者信息失败，HTTP 响应码：" + responseCode);
            connection.disconnect();
        }
    }

    /**
     * 获取项目的总提交次数
     * @param owner
     * @param repo
     * @return
     */
    private int getTotalCommits(String owner, String repo) {
        int totalCommits = 0;
        try {
            String urlString = String.format("https://api.github.com/repos/%s/%s/commits?per_page=1", owner, repo);
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "token " + token);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                String linkHeader = connection.getHeaderField("Link");
                System.out.println("linkHeader: " + linkHeader);

                if (linkHeader != null && linkHeader.contains("rel=\"last\"")) {
                    // 使用正则表达式提取 rel="last" 的 URL
                    String[] links = linkHeader.split(",");
                    for (String link : links) {
                        if (link.contains("rel=\"last\"")) {
                            String lastPageUrl = link.split(";")[0].replaceAll("[<>]", "").trim();
                            // 提取页码
                            String pageNumber = lastPageUrl.substring(lastPageUrl.lastIndexOf("page=") + 5);
                            totalCommits = Integer.parseInt(pageNumber);
                            break;
                        }
                    }
                }
            } else {
                System.out.println("获取提交信息失败，HTTP 响应码：" + responseCode);
            }
            connection.disconnect();
        } catch (Exception e) {
           throw new BaseException("获取项目的总提交次数失败：" + e.getMessage());
        }

        return totalCommits;
    }


    /**
     * 获取项目的总代码行数
     * @param owner
     * @param repo
     * @return
     */
    private int getTotalLinesOfCode(String owner, String repo) {
        int totalLines = 0;
        try {
            String urlString = String.format("https://api.github.com/repos/%s/%s/stats/contributors", owner, repo);
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "token " + token);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder content = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                connection.disconnect();

                JSONArray contributorsStats = new JSONArray(content.toString());
                for (int i = 0; i < contributorsStats.length(); i++) {
                    JSONObject contributor = contributorsStats.getJSONObject(i);
                    JSONObject weeks = contributor.getJSONArray("weeks").getJSONObject(0);
                    totalLines += weeks.getInt("a") - weeks.getInt("d"); // `a` 是添加的行数，`d` 是删除的行数
                }
            }
        } catch (Exception e) {
            throw new BaseException("获取项目的总代码行数失败：" + e.getMessage());
        }
        return totalLines;
    }

}
