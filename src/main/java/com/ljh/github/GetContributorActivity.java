package com.ljh.github;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class GetContributorActivity {
    /**
     * 获取项目贡献者活跃度分析
     */
    private static Map<String, Integer> getContributorActivity(String owner, String repo) {
        Map<String, Integer> contributorActivity = new HashMap<>();
        String token = "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";
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

                JSONArray contributorsArray = new JSONArray(content.toString());
                for (int i = 0; i < contributorsArray.length(); i++) {
                    JSONObject contributor = contributorsArray.getJSONObject(i);
                    String username = contributor.getJSONObject("author").getString("login");
                    int totalCommits = contributor.getInt("total");
                    contributorActivity.put(username, totalCommits);
                }
            } else {
                System.out.println("获取贡献者信息失败，HTTP 响应码：" + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return contributorActivity;
    }

    public static void main(String[] args) {
        String owner = "xtekky";
        String repo = "gpt4free";
        Map<String, Integer> contributorActivity = getContributorActivity(owner, repo);
        System.out.println(contributorActivity);

    }

}
