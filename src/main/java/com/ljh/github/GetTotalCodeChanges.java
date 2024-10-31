package com.ljh.github;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
/**
 * 获取项目的代码总变更行数
 */
public class GetTotalCodeChanges {

    private static int getTotalCodeChanges(String owner, String repo) {
        String token = "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";
        int totalChanges = 0;
        int page = 1;
        boolean hasMore = true;

        try {
            while (hasMore) {
                String urlString = String.format("https://api.github.com/repos/%s/%s/commits?page=%d&per_page=100", owner, repo, page);
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

                    JSONArray commits = new JSONArray(content.toString());
                    if (commits.length() == 0) {
                        hasMore = false;
                    } else {
                        for (int i = 0; i < commits.length(); i++) {
                            JSONObject commit = commits.getJSONObject(i).getJSONObject("commit");
                            if (commit.has("stats")) {
                                JSONObject stats = commit.getJSONObject("stats");
                                totalChanges += stats.getInt("additions") + stats.getInt("deletions");
                            }
                        }
                        page++;
                    }
                } else {
                    hasMore = false;
                }
                connection.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return totalChanges;
    }

    public static void main(String[] args) {
        String owner = "xtekky";
        String repo = "gpt4free";
        int totalCodeChanges = getTotalCodeChanges(owner, repo);
        System.out.println("totalCodeChanges: " + totalCodeChanges);
    }


}
