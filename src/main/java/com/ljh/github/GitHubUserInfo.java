package com.ljh.github;


import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 查询 GitHub 上某个用户主页的信息
 */
public class GitHubUserInfo {

    // GitHub 用户 API URL 示例：https://api.github.com/users/{username}
    private static final String GITHUB_USER_API_URL = "https://api.github.com/users/";

    public static void main(String[] args) {
        String username = "xtekky";  // GitHub 用户名

        try {
            // 构造 GitHub API 请求 URL
            String urlString = GITHUB_USER_API_URL + username;
            URL url = new URL(urlString);

            // 打开与 GitHub API 的连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/vnd.github.v3+json");

            // 处理响应
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 200 OK
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }

                // 关闭输入流
                in.close();

                // 将 JSON 响应解析为对象
                JSONObject userInfo = new JSONObject(content.toString());

                // 打印 GitHub 用户的关键信息
                System.out.println("GitHub 用户信息：");
                System.out.println("用户名: " + userInfo.getString("login"));
                System.out.println("用户 ID: " + userInfo.getInt("id"));
                System.out.println("名称: " + (userInfo.has("name") ? userInfo.getString("name") : "N/A"));
                System.out.println("公司: " + (userInfo.has("company") ? userInfo.getString("company") : "N/A"));
                System.out.println("博客: " + (userInfo.has("blog") ? userInfo.getString("blog") : "N/A"));
                System.out.println("位置: " + (userInfo.has("location") ? userInfo.getString("location") : "N/A"));
                System.out.println("简介: " + (userInfo.has("bio") ? userInfo.getString("bio") : "N/A"));
                System.out.println("公开仓库数: " + userInfo.getInt("public_repos"));
                System.out.println("公开 Gist 数: " + userInfo.getInt("public_gists"));
                System.out.println("粉丝数: " + userInfo.getInt("followers"));
                System.out.println("关注数: " + userInfo.getInt("following"));
                System.out.println("创建时间: " + userInfo.getString("created_at"));
                System.out.println("更新时间: " + userInfo.getString("updated_at"));

            } else {
                System.out.println("Failed to get user info. HTTP Response Code: " + responseCode);
            }

            // 断开连接
            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
