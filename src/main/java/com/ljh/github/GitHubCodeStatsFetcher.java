package com.ljh.github;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 获取指定用户对指定项目的代码总量
 */
public class GitHubCodeStatsFetcher {

    private static final String GITHUB_API_URL = "https://api.github.com/repos/";

    public static void main(String[] args) {
        String owner = "xtekky";  // 仓库拥有者
        String repo = "gpt4free";  // 仓库名称
        String username = "xtekky";  // GitHub 用户名

        int totalAdditions = 0;  // 总新增行数
        int totalDeletions = 0;  // 总删除行数

        try {
            // 获取提交列表
            String commitsUrl = GITHUB_API_URL + owner + "/" + repo + "/commits?author=" + username;
            URL url = new URL(commitsUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/vnd.github.v3+json");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();

                // 解析提交的JSON数据
                JSONArray commits = new JSONArray(content.toString());

                // 遍历每个提交，获取每个提交的详细信息
                for (int i = 0; i < commits.length(); i++) {
                    String sha = commits.getJSONObject(i).getString("sha");
                    String commitDetailsUrl = GITHUB_API_URL + owner + "/" + repo + "/commits/" + sha;

                    URL detailsUrl = new URL(commitDetailsUrl);
                    HttpURLConnection detailsConnection = (HttpURLConnection) detailsUrl.openConnection();
                    detailsConnection.setRequestMethod("GET");
                    detailsConnection.setRequestProperty("Accept", "application/vnd.github.v3+json");

                    if (detailsConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader detailsIn = new BufferedReader(new InputStreamReader(detailsConnection.getInputStream()));
                        StringBuilder detailsContent = new StringBuilder();
                        String detailsLine;

                        while ((detailsLine = detailsIn.readLine()) != null) {
                            detailsContent.append(detailsLine);
                        }
                        detailsIn.close();

                        JSONObject commitDetails = new JSONObject(detailsContent.toString());
                        if (commitDetails.has("stats")) {
                            JSONObject stats = commitDetails.getJSONObject("stats");
                            totalAdditions += stats.getInt("additions");
                            totalDeletions += stats.getInt("deletions");
                        }
                    }
                }

                System.out.println("总新增行数: " + totalAdditions);
                System.out.println("总删除行数: " + totalDeletions);

            } else {
                System.out.println("无法获取提交记录，HTTP 响应代码: " + responseCode);
            }

            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

