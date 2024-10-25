package com.ljh.github;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 获取某个github上的开源项目的一些信息
 */
public class GitHubRepoInfo {
    private static final String GITHUB_API_URL = "https://api.github.com/repos/";

    public static void main(String[] args) {
        String owner = "xtekky";
        String repo = "gpt4free";

        try {
            String urlString = GITHUB_API_URL + owner + "/" + repo;
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

                JSONObject jsonObject = new JSONObject(content.toString());
                System.out.println("仓库名称：" + jsonObject.getString("name"));
                System.out.println("仓库描述：" + jsonObject.getString("description"));
                System.out.println("星标数量：" + jsonObject.getInt("stargazers_count"));

                // 获取贡献者
                String contributorsUrl = jsonObject.getString("contributors_url");
                URL contributorsURL = new URL(contributorsUrl);
                HttpURLConnection contributorsConnection = (HttpURLConnection) contributorsURL.openConnection();
                contributorsConnection.setRequestMethod("GET");
                contributorsConnection.setRequestProperty("Accept", "application/vnd.github.v3+json");

                int contributorsResponseCode = contributorsConnection.getResponseCode();
                if (contributorsResponseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader contributorsIn = new BufferedReader(new InputStreamReader(contributorsConnection.getInputStream()));
                    StringBuilder contributorsContent = new StringBuilder();
                    while ((inputLine = contributorsIn.readLine())!= null) {
                        contributorsContent.append(inputLine);
                    }

                    contributorsIn.close();

                    JSONArray contributorsArray = new JSONArray(contributorsContent.toString());
                    System.out.println("贡献者：");
                    for (int i = 0; i < contributorsArray.length(); i++) {
                        JSONObject contributor = contributorsArray.getJSONObject(i);
                        System.out.println(contributor.getString("login"));
                    }
                } else {
                    System.out.println("获取贡献者信息失败。HTTP 响应码：" + contributorsResponseCode);
                }

            } else {
                System.out.println("获取仓库信息失败。HTTP 响应码：" + responseCode);
            }

            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}