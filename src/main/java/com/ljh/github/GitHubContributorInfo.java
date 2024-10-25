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
    private static final String GITHUB_CONTRIBUTORS_API_URL = "https://api.github.com/repos/";

    public static void main(String[] args) {
        String owner = "xtekky";
        String repo = "gpt4free";

        try {
            String urlString = GITHUB_CONTRIBUTORS_API_URL + owner + "/" + repo + "/contributors";
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/vnd.github.v3+json");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();

                while ((inputLine = in.readLine())!= null) {
                    content.append(inputLine);
                }

                in.close();

                JSONArray contributorsArray = new JSONArray(content.toString());
                for (int i = 0; i < contributorsArray.length(); i++) {
                    JSONObject contributor = contributorsArray.getJSONObject(i);
                    System.out.println("用户名: " + contributor.getString("login"));
                    System.out.println("用户 ID: " + contributor.getInt("id"));
                    System.out.println("贡献次数: " + contributor.getInt("contributions"));
                    System.out.println("GitHub 主页: " + contributor.getString("html_url"));
                    System.out.println("头像链接: " + contributor.getString("avatar_url"));
                    System.out.println("-----------------------------");
                }
            } else {
                System.out.println("Failed to get contributors info. HTTP Response Code: " + responseCode);
            }

            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}