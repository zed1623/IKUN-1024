package com.ljh.github;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * 调用github主页的搜索接口
 */
public class GitHubAPISearch {
    public static void main(String[] args) {
        String token = "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";
        String query = "gpt";
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://api.github.com/search/repositories?q=" + query)
                    .addHeader("Authorization", "token " + token)
                    .build();
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            String responseBody = response.body().string();
            JsonObject jsonObject = new Gson().fromJson(responseBody, JsonObject.class);
            JsonArray items = jsonObject.getAsJsonArray("items");
            for (int i = 0; i < items.size(); i++) {
                JsonObject repo = items.get(i).getAsJsonObject();
                String description = repo.get("description")!= null? repo.get("description").getAsString() : "无描述";
                JsonObject owner = repo.get("owner").getAsJsonObject();
                System.out.println("仓库名称：" + repo.get("name").getAsString());
                System.out.println("仓库描述：" + description);
                System.out.println("创建者：" + owner.get("login").getAsString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}