package com.ljh.service.impl;


import com.ljh.mapper.DeveloperMapper;
import com.ljh.mapper.ProjectMapper;
import com.ljh.pojo.entity.Developer;
import com.ljh.pojo.entity.Project;
import com.ljh.service.DeveloperService;
import com.ljh.service.ProjectService;
import com.ljh.utils.SimpleProjectScorer;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DeveloperServiceImpl implements DeveloperService {

    private static final String GITHUB_USER_API_URL = "https://api.github.com/users/";

    private static final String token = "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";

    @Autowired
    private DeveloperMapper developerMapper;


    @Override
    public void getDeveloper() {
        // 获取数据库中开发者的 GitHub 用户名列表
        List<String> developers = developerMapper.getTop10Developers();

        // 遍历每个开发者用户名
        for (String username : developers) {
            try {
                // 构造 GitHub API 请求 URL
                String urlString = GITHUB_USER_API_URL + username;
                URL url = new URL(urlString);

                // 打开与 GitHub API 的连接
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", "token " + token);

                // 处理响应
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder content = new StringBuilder();
                    String inputLine;

                    while ((inputLine = in.readLine()) != null) {
                        content.append(inputLine);
                    }

                    // 关闭输入流
                    in.close();

                    // 将 JSON 响应解析为对象
                    JSONObject userInfo = new JSONObject(content.toString());

                    // 创建 Developer 实例并设置属性
                    Developer developer = new Developer();
                    developer.setLogin(userInfo.optString("login", "N/A"));  // 根据 login 查找并更新
                    developer.setName(userInfo.optString("name", "N/A"));
                    developer.setEmail(userInfo.optString("email", "N/A"));
                    developer.setAvatarUrl(userInfo.optString("avatar_url", "N/A"));
                    developer.setBlogUrl(userInfo.optString("blog", "N/A"));
                    developer.setProfileUrl(userInfo.optString("html_url", "N/A"));
                    developer.setBio(userInfo.optString("bio", "N/A"));
                    developer.setNation("N/A");  // 这里可以通过其他 API 猜测国家
                    developer.setNationConfidence(0.0f);
                    developer.setField("N/A");  // 根据业务逻辑设置
                    developer.setTalentRank(0.0f);  // 使用 TalentRank 算法计算

                    // 日期格式处理，使用 Instant 解析并转换为 LocalDateTime
                    Instant createdAtInstant = Instant.parse(userInfo.getString("created_at"));
                    developer.setCreatedAt(LocalDateTime.ofInstant(createdAtInstant, ZoneId.systemDefault()));

                    Instant updatedAtInstant = Instant.parse(userInfo.getString("updated_at"));
                    developer.setUpdatedAt(LocalDateTime.ofInstant(updatedAtInstant, ZoneId.systemDefault()));

                    // 更新数据库中的开发者信息
                    developerMapper.updateDeveloperByLogin(developer);

                } else {
                    System.out.println("Failed to get user info. HTTP Response Code: " + responseCode);
                }

                connection.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }




}
