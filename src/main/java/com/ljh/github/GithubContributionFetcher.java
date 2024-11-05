package com.ljh.github;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import com.ljh.pojo.entity.Contribution;
import org.json.JSONArray;
import org.json.JSONObject;

public class GithubContributionFetcher {

    private static final String GITHUB_API_URL = "https://api.github.com";
    private static final String TOKEN = "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";

    /**
     * 获取贡献者的贡献信息
     * @param owner 项目所有者用户名
     * @param repo  项目仓库名称
     * @param username 开发者用户名
     * @return Contribution 贡献实体类，包含贡献的详细信息
     */
    public static Contribution fetchContribution(String owner, String repo, String username) {
        try {
            Contribution contribution = new Contribution();
//            contribution.setDeveloperId(getDeveloperId(username));  // 假设有方法获取开发者ID
//            contribution.setProjectId(getProjectId(owner, repo));  // 假设有方法获取项目ID

            // 获取提交信息
            String commitsUrl = GITHUB_API_URL + "/repos/" + owner + "/" + repo + "/commits?author=" + username;
            HttpURLConnection connection = createConnection(commitsUrl);

            int commitsCount = 0;
            int additions = 0;
            int deletions = 0;
            Date lastCommitDate = null;

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder content = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();

                JSONArray commits = new JSONArray(content.toString());
                for (int i = 0; i < commits.length(); i++) {
                    JSONObject commit = commits.getJSONObject(i);
                    commitsCount++;

                    // 获取每个提交的详细信息
                    String commitSha = commit.getString("sha");
                    JSONObject stats = fetchCommitStats(owner, repo, commitSha);
                    additions += stats.getInt("additions");
                    deletions += stats.getInt("deletions");

                    // 记录最新的提交时间
                    String commitDateStr = commit.getJSONObject("commit").getJSONObject("author").getString("date");
                    Date commitDate = javax.xml.bind.DatatypeConverter.parseDateTime(commitDateStr).getTime();
                    if (lastCommitDate == null || commitDate.after(lastCommitDate)) {
                        lastCommitDate = commitDate;
                    }
                }
            }

            contribution.setCommitsCount(commitsCount);
            contribution.setAdditions(additions);
            contribution.setDeletions(deletions);
            contribution.setLastCommitDate(lastCommitDate);

            // 计算贡献分数
            float contributionScore = calculateContributionScore(commitsCount, additions, deletions);
            contribution.setContributionScore(contributionScore);

            return contribution;

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch contribution data: " + e.getMessage());
        }
    }

    // 获取单次提交的统计信息（新增和删除行数）
    private static JSONObject fetchCommitStats(String owner, String repo, String commitSha) throws Exception {
        String commitUrl = GITHUB_API_URL + "/repos/" + owner + "/" + repo + "/commits/" + commitSha;
        HttpURLConnection connection = createConnection(commitUrl);

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder content = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            JSONObject commitDetail = new JSONObject(content.toString());
            return commitDetail.getJSONObject("stats");
        }
        return null;
    }

    // 创建连接并设置请求头
    private static HttpURLConnection createConnection(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "token " + TOKEN);
        return connection;
    }

    // 假设的贡献分数计算方法
    private static float calculateContributionScore(int commits, int additions, int deletions) {
        return commits * 1.0f + additions * 0.1f - deletions * 0.1f;
    }

    public static void main(String[] args) {
        String owner = "gpt4free";
        String repo = "xtekky";
        String username = "hlohaus";
        Contribution contribution = fetchContribution(owner, repo, username);
        System.out.println("Contribution: " + contribution);
    }

}
