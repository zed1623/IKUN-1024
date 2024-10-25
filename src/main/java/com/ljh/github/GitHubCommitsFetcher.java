package com.ljh.github;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 获取github某个用户对某个开源项目的提交记录
 */
public class GitHubCommitsFetcher {

    private static final String GITHUB_API_URL = "https://api.github.com/repos/";

    public static void main(String[] args) {
        String owner = "xtekky";  // 仓库拥有者
        String repo = "gpt4free";  // 仓库名称
        String username = "xtekky";  // GitHub 用户名

        try {
            // 构造 API 请求 URL
            String urlString = GITHUB_API_URL + owner + "/" + repo + "/commits?author=" + username;
            URL url = new URL(urlString);

            // 打开与 GitHub API 的连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/vnd.github.v3+json");

            // 检查响应代码
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

                // 解析响应的 JSON 数据
                JSONArray commits = new JSONArray(content.toString());
                for (int i = 0; i < commits.length(); i++) {
                    JSONObject commitObject = commits.getJSONObject(i).getJSONObject("commit");
                    JSONObject author = commitObject.getJSONObject("author");
                    String commitMessage = commitObject.getString("message");
                    String commitDate = author.getString("date");
                    String commitUrl = commits.getJSONObject(i).getString("html_url");

                    System.out.println("提交者: " + author.getString("name"));
                    System.out.println("提交日期: " + commitDate);
                    System.out.println("提交信息: " + commitMessage);
                    System.out.println("提交链接: " + commitUrl);
                    System.out.println("-------------------------------");
                }
            } else {
                System.out.println("无法获取提交记录，HTTP 响应代码: " + responseCode);
            }

            // 关闭连接
            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
