package com.ljh.github;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 获取这个项目贡献者的提交次数等一些信息
 */
public class GitHubContributorInfo {
    private static final String GITHUB_API_URL = "https://api.github.com/repos/";
    private static final String GITHUB_TOKEN = "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"; // 添加你的 GitHub Token

    public static void main(String[] args) {
        String owner = "xtekky";
        String repo = "gpt4free";
        String contributorUsername = "xtekky"; // 指定的贡献者用户名

        fetchContributorCommits(owner, repo, contributorUsername);
    }

    public static void fetchContributorCommits(String owner, String repo, String contributorUsername) {
        HttpURLConnection connection = null;
        try {
            String urlString = GITHUB_API_URL + owner + "/" + repo + "/commits?author=" + contributorUsername;
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/vnd.github.v3+json");
            connection.setRequestProperty("Authorization", "token " + GITHUB_TOKEN);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder content = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        content.append(inputLine);
                    }

                    // 解析并打印提交记录
                    parseAndPrintCommits(new JSONArray(content.toString()));
                }
            } else {
                System.out.println("无法获取贡献者提交记录，HTTP 响应代码: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static void parseAndPrintCommits(JSONArray commits) {
        for (int i = 0; i < commits.length(); i++) {
            JSONObject commitObject = commits.getJSONObject(i).optJSONObject("commit");
            if (commitObject != null) {
                JSONObject author = commitObject.optJSONObject("author");
                String commitMessage = commitObject.optString("message", "无信息");
                String commitDate = author != null ? author.optString("date", "未知日期") : "未知日期";
                String commitUrl = commits.getJSONObject(i).optString("html_url", "无链接");

                System.out.println("提交者: " + (author != null ? author.optString("name", "未知") : "未知"));
                System.out.println("提交日期: " + commitDate);
                System.out.println("提交信息: " + commitMessage);
                System.out.println("提交链接: " + commitUrl);
                System.out.println("-------------------------------");
            }
        }
    }
}